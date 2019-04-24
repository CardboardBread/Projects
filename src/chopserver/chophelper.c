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
 * Socket-Layer functions
 */

int setup_new_client(int listen_fd, Client *clients[]) {
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

int remove_client(int client_index, Client *clients[]) {
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

int write_buf_to_client(int client_fd, char *msg, int msg_len) {
  // precondition for invalid arguments
  if (client_fd < MIN_FD || msg == NULL || msg_len < 0) {
    debug_print("write_buf_to_client: invalid arguments");
    return -1;
  }

  // check if message is nonexistent
  if (msg_len == 0) {
    debug_print("write_buf_to_client: message is empty");
    return 0;
  }

  // check if message is too long
  if (msg_len > DATA_LEN) {
    debug_print("write_buf_to_client: message is too long");
    return 1;
  }

  // remove newline on message
  if (remove_newline(msg, msg_len) < 0) {
    debug_print("write_buf_to_client: failed to remove newline from message");
  }

  // assemble message with network newline
  char buf[MESG_LEN] = {0};
  buf[0] = 0;
  buf[HEAD_LEN + DATA_LEN] = 0;
  int total = snprintf(buf + HEAD_LEN, DATA_LEN, "%.*s", msg_len, msg);

  // write message to target
  int bytes_written = write(client_fd, buf, total);
  if (bytes_written != total) {

    // in case write was not perfectly successful
    if (bytes_written < 0) {
      debug_print("write_buf_to_client: failed to write message to client");
      return 1;
    } else {
      debug_print("write_buf_to_client: wrote incomplete message to client");
      return 1;
    }
  }

  debug_print("write_buf_to_client: wrote %d bytes to client", total);
  return 0;
}

int write_packet_to_client(int client_fd, Packet *pack) {

}

int send_message_to_client(int client_fd, Message *msg) {
  // precondition for invalid arguments
  if (client_fd < MIN_FD || msg == NULL) {
    debug_print("send_message_to_client: invalid arguments");
    return -1;
  }

  // if message is empty
  if (msg->first == NULL) {
    debug_print("send_message_to_client: message is empty");
    return 0;
  }

  char *head;
  int status;
  int msg_len;
  Segment *cur;
  for (cur = msg->first; cur != NULL; cur = cur->next) {
    head = cur->buf;
    msg_len = cur->inbuf;
    status = write_buf_to_client(client_fd, head, msg_len);
    if (status < 0 || status > 0) {
      debug_print("send_message_to_client: failed writing entire message");
      return 1;
    }
  }

  debug_print("send_message_to_client: wrote a message with %d segments", msg->seg_count);
  return 0;
}

int send_fstr_to_client(int client_fd, const char *format, ...) {
  // precondition for invalid argument
  if (client_fd < MIN_FD) {
    debug_print("send_fstr_to_client: invalid arguments");
    return -1;
  }

  char msg[BUFSIZE + 1];

  va_list args;
  va_start(args, format);
  vsnprintf(msg, BUFSIZE, format, args);
  va_end(args);

  return write_buf_to_client(client_fd, msg, BUFSIZE);
}

/*
 * Data-Layer functions
 */

Message *partition_message(char *msg, int msg_len) {
  if (msg == NULL || msg_len < 0) {
    debug_print("partition_message: invalid arguments");
    return NULL;
  }

  Message *out = malloc(sizeof(Message));
  setup_message_struct(out);

  int index = 0;
  char buf[BUFSIZE];
  for (int i = 0; i < msg_len; i++) {
    if (index < BUFSIZE) {
      buf[index] = msg[i];
      index++;
    } else {
      if (append_to_message(out, buf, BUFSIZE) < 0) {
        debug_print("partition_message: failed to partition message");
        return NULL;
      }
      memset(buf, 0, BUFSIZE);
      index = 0;
    }
  }

  return out;
}

int append_to_message(Message *msg, char *buf, int buf_len) {
  if (msg == NULL || buf == NULL || buf_len < 0) {
    debug_print("append_to_message: invalid arguments");
    return -1;
  }

  if (msg->first == NULL) {
    msg->first = malloc(sizeof(Segment));
    reset_segment_struct(msg->first);
    memmove(msg->first->buf, buf, buf_len);
    return 0;
  }

  Segment *cur;
  for (cur = msg->first; cur != NULL; cur = cur->next) {
    if (cur->next == NULL) {
      cur->next = malloc(sizeof(Segment));
      reset_segment_struct(cur->next);
      memmove(cur->next, buf, buf_len);
    }
  }

  return 0;
}

int remove_newline(char *buf, int len) {
  // Precondition for invalid arguments
  if (buf == NULL || len < 2) {
    debug_print("remove_newline: invalid arguments");
    return -1;
  }

  int index;
  for (index = 1; index < len; index++) {
    // network newline
    if (buf[index - 1] == '\r' && buf[index] == '\n') {
      debug_print("remove_newline: network newline at %d", index);
      buf[index - 1] = '\0';
      buf[index] = '\0';
      return index;
    }

    // unix newline
    if (buf[index - 1] != '\r' && buf[index] == '\n') {
      debug_print("remove_newline: unix newline at %d", index);
      buf[index] = '\0';
      return index;
    }
  }

  // nothing found
  debug_print("remove_newline: no newline found");
  return -1;
}

int convert_to_crlf(char *buf, int len) {
  // Precondition for invalid arguments
  if (buf == NULL || len < 1) {
    debug_print("convert_to_crlf: invalid arguments");
    return -1;
  }

  int index;
  for (index = 0; index < len; index++) {
    if (buf[index] == '\n') {
      debug_print("convert_to_crlf: found newline at %d", index);

      // last index
      if (index == (len - 1)) {
        debug_print("convert_to_crlf: no space for network newline");
        return -2;
      } else {
        buf[index] = '\r';
        buf[index + 1] = '\n';
        return index + 1;
      }
    }
  }

  // nothing found
  debug_print("convert_to_crlf: no newline found");
  return -1;
}

int find_network_newline(const char *buf, int inbuf) {
  // Precondition for invalid arguments
  if (buf == NULL || inbuf < 2) {
    debug_print("find_network_newline: invalid arguments");
    return -1;
  }

  // stop loop when \r\n is found, fail when over length
  int index;
  for (index = 2; !(buf[index - 2] == '\r' && buf[index - 1] == '\n'); index++) {
    if (index >= inbuf) {
      debug_print("find_network_newline: no network newline found");
      return -1;
    }
  }

  debug_print("find_network_newline: found network newline at %d", index);
  return index;
}

int find_unix_newline(const char *buf, int inbuf) {
  // Precondition for invalid arguments
  if (buf == NULL || inbuf < 1) {
    debug_print("find_unix_newline: invalid arguments");
    return -1;
  }

  // only thing in the buffer is a newline
  if (buf[0] == '\n') {
    return 1;
  }

  // stop loop when *\n is found, fail when over length, * meaning any character but \r
  int index;
  for (index = 2; !(buf[index - 2] != '\r' && buf[index - 1] == '\n'); index++) {
    if (index >= inbuf) {
      debug_print("find_unix_newline: no unix newline found");
      return -1;
    }
  }

  debug_print("find_unix_newline: found unix newline at %d", index);
  return index;
}

int read_to_buf(int fd, Buffer *bufstr) {
  if (fd < MIN_FD || bufstr == NULL) {
    debug_print("read_to_buf: invalid arguments");
    return -1;
  }

  char *head = &(bufstr->buf[bufstr->inbuf]);
  int left = sizeof(bufstr->buf) - bufstr->inbuf;
  int total_read = 0;

  int nbytes = read(fd, head, left);
  if (nbytes > 0) {
    bufstr->inbuf += nbytes;
    total_read += nbytes;

    // intermediary step goes here
    left -= nbytes;
    head += nbytes;

    debug_print("read_to_buf: read %d bytes into buffer", total_read);
    return total_read;
  } else if (nbytes == 0) {
    debug_print("read_to_buf: fd closed");
    return 0;
  } else {
    debug_print("read_to_buf: encountered error");
    return -1;
  }

  debug_print("read_to_buf: instruction error");
  return -1;
}

char* get_next_msg(Buffer* buf, int *msg_len, NewlineType newline) {
  // precondition for invalid arguments
  if (buf == NULL || msg_len == NULL) {
    debug_print("get_next_msg: invalid arguments");
    return NULL;
  }

  int nl = 0;
  char *head = &(buf->buf[buf->consumed]);
  switch (newline) {
    case NEWLINE_CRLF:
      nl = find_network_newline(head, buf->inbuf);
      break;
    case NEWLINE_LF:
      nl = find_unix_newline(head, buf->inbuf);
      break;
    default:
      debug_print("get_next_msg: newline type is invalid");
      return NULL;
  }

  // error case for finding
  if (nl < 0) {
    debug_print("get_next_msg: failed to find any non-consumed newline");
    return NULL;
  }

  debug_print("get_next_msg: found non-consumed newline at %d", nl);
  *msg_len = nl;
  return head;
}

int mark_consumed(Buffer *buf, int index) {
  // precondition for invalid arguments
  if (buf == NULL || index < 0) {
    debug_print("mark_consumed: invalid arguments");
    return -1;
  }

  buf->consumed = index;
  debug_print("mark_consumed: consumed set to %d", index);
  return 0;
}

int advance_consumed(Buffer *buf, int increment) {
  // precondition for invalid arguments
  if (buf == NULL || increment < 0) {
    debug_print("advance_consumed: invalid arguments");
    return -1;
  }

  buf->consumed += increment;
  debug_print("mark_consumed: increased consumed by %d", buf->consumed);
  return 0;
}

void shift_buffer(Buffer *buf) {
  // precondition for invalid argument
  if (buf == NULL) {
    debug_print("shift_buffer: invalid arguments");
    return;
  }

  // complex variables
  char *head = &(buf->buf[buf->consumed]);
  int left = buf->inbuf - buf->consumed;

  // memory operations
  memset(buf->buf, 0, buf->consumed);
  memmove(buf->buf, head, left);

  // updating fields
  buf->inbuf -= buf->consumed;
  buf->consumed = 0;

  debug_print("shift_buffer: cleared consumed, shifted non-consumed");
  return;
}

int is_buffer_full(Buffer *buf) {
  // precondition for invalid argument
  if (buf == NULL) {
    debug_print("is_buffer_full: invalid arguments");
    return -1;
  }

  return (buf->inbuf == sizeof(buf->buf));
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

  // client socket
  client->socket_fd = -1;

  // client buffer
  return reset_buffer_struct(client->buffer);
}

int reset_buffer_struct(Buffer *buffer) {
  // precondition for invalid argument
  if (buffer == NULL) {
    debug_print("reset_buffer_struct: invalid arguments");
    return -1;
  }

  memset(buffer->buf, 0, MESG_LEN);
  buffer->consumed = 0;
  buffer->inbuf = 0;

  return 0;
}

int reset_message_struct(Message *message) {
  // precondition for invalid argument
  if (message == NULL) {
    debug_print("reset_message_struct: invalid arguments");
    return -1;
  }

  // free every node in the message
  Packet *cur;
  Packet *last = NULL;
  for (cur = message->first; cur != NULL; cur = cur->next) {
    free(last);
    last = cur;
  }
  free(last);

  // reset struct fields
  message->first = NULL;
  message->seg_count = 0;

  return 0;
}

int reset_packet_struct(Packet *pack) {
  // precondition for invalid argument
  if (pack == NULL) {
    debug_print("reset_packet_struct: invalid arguments");
    return -1;
  }

  // reset struct fields
  memset(pack->buf, 0, MESG_LEN);
  pack->inbuf = 0;

  return 0;
}

int set_packet_tail(char buf[MESG_LEN], char a, char b, char c, char d) {
  // precondition for invalid argument
  if (buf == NULL) {
    debug_print("set_packet_tail: invalid arguments");
    return -1;
  }

  // setting head
  int offset = 0;
  buf[offset + 0] = a;
  buf[offset + 1] = b;
  buf[offset + 2] = c;
  buf[offset + 3] = d;

  return 0;
}

int set_packet_head(char buf[MESG_LEN], char a, char b, char c, char d) {
  // precondition for invalid argument
  if (buf == NULL) {
    debug_print("set_packet_head: invalid arguments");
    return -1;
  }

  // setting tail
  int offset = DATA_LEN + HEAD_LEN;
  buf[offset + 0] = a;
  buf[offset + 1] = b;
  buf[offset + 2] = c;
  buf[offset + 3] = d;

  return 0;
}
