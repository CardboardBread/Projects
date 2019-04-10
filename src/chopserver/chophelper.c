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
    if (clients[i] == NULL) {
      clients[i] = malloc(sizeof(Client));
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

  // remove newline on message
  if (remove_newline(msg, msg_len) < 0) {
    debug_print("write_buf_to_client: failed to remove newline from message");
  }

  // check if message is too long
  char buf[BUFSIZE];
  if (msg_len + newlen_len[NEWLINE_CRLF] > BUFSIZE) {
    debug_print("write_buf_to_client: message is too long");
    return 1;
  }
}

int send_message_to_client(int client_fd, Message *msg) {

}

int send_fstr_to_client(int client_fd, const char *format, ...) {

}

/*
 * Data-Layer functions
 */

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
 * Utility Functions (non-essential)
 */

// errno is preserved through this function,
// it will not change between this function calling and returning.
void debug_print(const char *format, ...) {
  if (debug_fd < MIN_FD) return;
  int errsav = errno;

  va_list args;
  va_start(args, format);

  dprintf(debug_fd, "%s", debug_header);
  vdprintf(debug_fd, format, args);
  dprintf(debug_fd, "\n");

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

  memset(buffer->buf, 0, BUFSIZE);
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

  int count = 0;
  Segment *cur;
  for (cur = message->first; cur != NULL; cur = cur->next) {
    reset_segment_struct(cur);
    count++;
  }

  message->seg_count = count;

  return 0;
}

int reset_segment_struct(Segment *segment) {
  // precondition for invalid argument
  if (segment == NULL) {
    debug_print("reset_segment_struct: invalid arguments");
    return -1;
  }

  memset(segment->buf, 0, BUFSIZE);
  segment->inbuf = 0;

  return 0;
}
