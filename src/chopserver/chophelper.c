#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <signal.h>
#include <stdarg.h>

#include "socket.h"
#include "chophelper.h"

/*
 * Client Management functions
 */

int setup_new_client(const int listen_fd, Client *clients[]) {
  // precondition for invalid arguments
  if (listen_fd < MIN_FD || clients == NULL) {
    debug_print("setup_new_client: invalid arguments");
    return -1;
  }

  // blocking until client connects
  int fd = accept_connection(listen_fd);
  if (fd < MIN_FD) {
    debug_print("setup_new_client: failed to accept new client");
    return -1;
  }
  debug_print("setup_new_client: accepted new client on %d", fd);

  // finding an 'empty' space in the client array
  for (int i = 0; i < MAX_CONNECTIONS; i++) {

    // placing new client in empty space
    if (clients[i] == NULL) {
      clients[i] = (Client *) malloc(sizeof(Client));
      clients[i]->buffer = (Buffer *) malloc(sizeof(Buffer));
      reset_client_struct(clients[i]);
      clients[i]->socket_fd = fd;
      debug_print("setup_new_client: placed client at index %d", i);
      return fd;
    }

  }

  // close the connection since we can't store it
  close(fd);
  debug_print("setup_new_client: no empty space for client found, closing connection");
  return -1;
}

int remove_client(const int client_index, Client *clients[]) {
  // precondition for invalid arguments
  if (client_index < MIN_FD) {
    debug_print("remove_client: invalid arguments");
    return -1;
  }

  // no client at index
  if (clients[client_index] == NULL) {
    debug_print("remove_client: target index %d has no client", client_index);
    return 1;
  }

  // closing fd and freeing heap memory
  int sav = clients[client_index]->socket_fd;
  close(clients[client_index]->socket_fd);
  free(clients[client_index]);
  clients[client_index] = NULL;

  debug_print("remove_client: client %d at index %d removed", sav, client_index);
  return 0;
}

/*
 * Sending functions
 */

int write_buf_to_client(Client *cli, const char *msg, const int msg_len) {
  // precondition for invalid arguments
  if (cli == NULL || msg == NULL || msg_len < 0) {
    debug_print("write_buf_to_client: invalid arguments");
    return -1;
  }

  // check if message is nonexistent
  if (msg_len == 0) {
    debug_print("write_buf_to_client: message is empty");
    return 0;
  }

  // check if message is too long
  if (msg_len > TEXT_LEN) { // TODO: negotiate a 'long message' for text with more than 255 bytes
    debug_print("write_buf_to_client: message is too long");
    return 1;
  }

  // assemble header with text signals
  char head[PACKET_LEN] = {0};
  head[PACKET_STATUS] = START_TEXT;
  head[PACKET_CONTROL1] = 1;
  head[PACKET_CONTROL2] = msg_len;

  // write header packet to target
  int head_written = write(cli->socket_fd, head, PACKET_LEN);
  if (head_written != PACKET_LEN) {

    // in case write was not perfectly successful
    if (head_written < 0) {
      debug_print("write_buf_to_client: failed to write header to client");
      return 1;
    } else {
      debug_print("write_buf_to_client: wrote incomplete header to client");
      return 1;
    }
  }

  // assemble message into buffer
  char buf[TEXT_LEN] = {0};
  snprintf(buf, TEXT_LEN, "%.*s", msg_len, msg);

  // write message to target
  int bytes_written = write(cli->socket_fd, buf, msg_len);
  if (bytes_written != msg_len) {

    // in case write was not perfectly successful
    if (bytes_written < 0) {
      debug_print("write_buf_to_client: failed to write message to client");
      return 1;
    } else {
      debug_print("write_buf_to_client: wrote incomplete message to client");
      return 1;
    }
  }

  debug_print("write_buf_to_client: wrote text packet of %d bytes to client", bytes_written);
  return 0;
}

int write_packet_to_client(Client *cli, Packet *pack) {
  // precondition for invalid arguments
  if (cli == NULL || pack == NULL) {
    debug_print("write_packet_to_client: invalid arguments");
    return -1;
  }

  // mark client outgoing flag with status
  cli->out_flag = pack->head[PACKET_STATUS];

  // write header packet to target
  int head_written = write(cli->socket_fd, pack->head, PACKET_LEN);
  if (head_written != PACKET_LEN) {

    // in case write was not perfectly successful
    if (head_written < 0) {
      debug_print("write_packet_to_client: failed to write header to client");
      return 1;
    } else {
      debug_print("write_packet_to_client: wrote incomplete header to client");
      return 1;
    }
  }

  // write message to target (if any)
  int bytes_written = write(cli->socket_fd, pack->buf, pack->inbuf);
  if (bytes_written != pack->inbuf) {

    // in case write was not perfectly successful
    if (bytes_written < 0) {
      debug_print("write_packet_to_client: failed to write message to client");
      return 1;
    } else {
      debug_print("write_packet_to_client: wrote incomplete message to client");
      return 1;
    }
  }

  // demark client outgoing flag
  cli->out_flag = 0;

  debug_print("write_packet_to_client: wrote packet of %d bytes to client", bytes_written);
  return 0;
}

int send_str_to_client(Client *cli, char *str) {
  // precondition for invalid arguments
  if (cli == NULL || str == NULL) {
    debug_print("send_str_to_client: invalid arguments");
    return -1;
  }

  return write_buf_to_client(cli, str, strlen(str));
}

int send_fstr_to_client(Client *cli, const char *format, ...) {
  // precondition for invalid argument
  if (cli == NULL) {
    debug_print("send_fstr_to_client: invalid arguments");
    return -1;
  }

  // buffer for assembling format string
  char msg[TEXT_LEN + 1];

  va_list args;
  va_start(args, format);
  vsnprintf(msg, TEXT_LEN, format, args);
  va_end(args);

  return write_buf_to_client(cli, msg, TEXT_LEN);
}

/*
 * Receiving functions
 */

int read_header(Client *cli) {
  // precondition for invalid argument
  if (cli == NULL) {
    debug_print("read_header: invalid argument");
    return -1;
  }

  // read packet from client
  char head[PACKET_LEN];
  int head_read = read(cli->socket_fd, head, PACKET_LEN);
  if (head_read != PACKET_LEN) {

    // in case read isn't perfect
    if (head_read < 0) {
      debug_print("read_header: failed to read header");
      return 1;
    } else {
      debug_print("read_header: received incomplete header");
      return 1;
    }
  }

  // parse the status
  switch(head[PACKET_STATUS]) {
    case 0: // NULL
      debug_print("read_header: received NULL header");
      break;

    case 1: // Header
      debug_print("read_header: received extended header");
      // TODO: implement
      break;

    case 2: // Text
      debug_print("read_header: received text header");
      return parse_text(cli, head[PACKET_CONTROL1], head[PACKET_CONTROL2]);

    case 5: // Enquiry
      debug_print("read_header: received enquiry header");
      return parse_enquiry(cli, head[PACKET_CONTROL1]);

    case 6: // Acknowledge
      debug_print("read_header: received acknowledge header");
      return parse_acknowledge(cli);

    case 21: // Neg Acknowledge
      debug_print("read_header: received negative acknowledge header");
      return parse_neg_acknowledge(cli);

    case 24: // Cancel
      debug_print("read_header: received cancel header");
      return parse_cancel(cli);

    default: // unsupported/invalid
      debug_print("read_header: received invalid header");
      return -1;

  }

  return 0;
}

int parse_text(Client *cli, int count, int width) {
  // precondition for invalid arguments
  if (cli == NULL || count < 0 || width < 0) {
    debug_print("parse_text: invalid argument");
    return -1;
  }

  // control 1 count of messages
  // control 2 size of each message

  int bytes_read;
  char buffer[TEXT_LEN];
  for (int i = 0; i < count; i++) {

    // read the expected amount of data
    bytes_read = read(cli->socket_fd, buffer, width);
    if (bytes_read != width) {

      // in case read isn't perfect
      if (bytes_read < 0) {
        debug_print("parse_text: failed to read text section");
        return 1;
      } else {
        debug_print("parse_text: read incomplete text section");
        return 1;
      }
    }

    // TODO: consume the message
    printf("Received \"%.*s\"", width, buffer);
  }

  return 0;
}

int parse_cancel(Client *cli) {
  // precondition for invalid argument
  if (cli == NULL) {
    debug_print("parse_cancel: invalid arguments");
    return -1;
  }

  // marking this client as closed
  cli->inc_flag = -1;
  cli->out_flag = -1;

  return 0;
}

/*
 * Utility Functions
 */

// errno is preserved through this function,
// it will not change between this function calling and returning.
void debug_print(const char *format, ...) {
  // precondition checking if debugging is turned off
  if (debug_fd < MIN_FD) return;

  // saving errno
  int errsav = errno;

  // capturing variable argument list
  va_list args;
  va_start(args, format);

  // printing argument
  dprintf(debug_fd, "%s", debug_header);
  vdprintf(debug_fd, format, args);
  dprintf(debug_fd, "\n");

  // in case errno is nonzero
  if (errsav > 0) {
    dprintf(debug_fd, "%s%s\n", debug_header, strerror(errsav));
  }

  // cleaining up
  va_end(args);
  errno = errsav;
  return;
}

int reset_client_struct(Client *client) {
  // precondition for invalid argument
  if (client == NULL) {
    debug_print("reset_client_struct: invalid arguments");
    return -1;
  }

  // struct fields
  client->socket_fd = -1;
  client->inc_flag = 0;
  client->out_flag = 0;

  // client buffer
  return reset_buffer_struct(client->buffer);
}

int reset_buffer_struct(Buffer *buffer) {
  // precondition for invalid argument
  if (buffer == NULL) {
    debug_print("reset_buffer_struct: invalid arguments");
    return -1;
  }

  memset(buffer->buf, 0, TEXT_LEN);
  buffer->consumed = 0;
  buffer->inbuf = 0;

  return 0;
}

int reset_packet_struct(Packet *pack) {
  // precondition for invalid argument
  if (pack == NULL) {
    debug_print("reset_packet_struct: invalid arguments");
    return -1;
  }

  // reset struct fields
  memset(pack->head, 0, PACKET_LEN);
  memset(pack->buf, 0, TEXT_LEN);
  pack->inbuf = 0;

  return 0;
}
