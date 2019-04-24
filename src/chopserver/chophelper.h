#ifndef __CHOP_HELPER_H__
#define __CHOP_HELPER_H__

/*
 * Macros and Constants
 */

#ifndef PORT
  #define PORT 50001
#endif

#define MESG_LEN 128
#define HEAD_LEN 4
#define TAIL_LEN 4
#define DATA_LEN 120

#define CONNECTION_QUEUE 5
#define MAX_CONNECTIONS 20

#define PIPE_READ 0
#define PIPE_WRITE 1

#define MIN_FD 0

static const int debug_fd = STDERR_FILENO;
static const char debug_header[] = "[DEBUG] ";

static const char *newline_str[] = {"\r\n", "\n"};
static const int newlen_len[] = {2, 1};

/*
 * Structures and Types
 */

typedef enum {NEWLINE_CRLF, NEWLINE_LF} NewlineType;

struct socket_buffer {
	char buf[MESG_LEN];
	int consumed;
	int inbuf;
};
typedef struct socket_buffer Buffer;

struct client {
	int socket_fd;
	struct socket_buffer *buffer;
};
typedef struct client Client;

struct packet {
	char buf[MESG_LEN];
	int inbuf;
  struct packet *next;
};
typedef struct packet Packet;

struct message {
  struct packet *first;
  int seg_count;
};
typedef struct message Message;

/*
 * Socket-Layer functions
 */

/*
 * Blocks until a client connects to a server already running on listen_fd.
 * Length of clients array is assumed to be MAX_CONNECTIONS.
 * Returns 0 on success, -1 on error and 1 if the list of clients is full.
 */
int setup_new_client(int listen_fd, Client *clients[]);

/*
 * Frees the client at the given index in the given array.
 * Length of client array is assumed to be MAX_CONNECTIONS.
 * Returns 0 on success, -1 on error and 1 if the target client doesn't exist.
 */
int remove_client(int client_index, Client *clients[]);

/*
 * Writes data to the given socket, assuming msg is the beginning of an array
 * of msg_len length.
 * Refuses to send messages that are too large to fit in a single packet
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int write_buf_to_client(int client_fd, char *msg, int msg_len);

/*
 * Writes the given packet to the given socket.
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int write_packet_to_client(int client_fd, Packet *pack);

/*
 * Writes all the segments in a given message to the given socket.
 * Assumes the chain of segments is NULL terminated.
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 */
int send_message_to_client(int client_fd, Message *msg);

/*
 * Writes a given format string to the given socket.
 * Functions similarly to dprintf, with a different return behaviour
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int send_fstr_to_client(int client_fd, const char *format, ...);

/*
 * Data-Layer functions
 */

Message *partition_message(char *msg, int msg_len);

int append_to_message(Message *msg, char *buf, int buf_len);

/*
 * Replaces the first '\n' or '\r\n' found in str with a null terminator.
 * Returns the index of the new first null terminator if found, or -1 if
 * not found.
 */
int remove_newline(char *str, int len);

/*
 * Replaces the first '\n' found in str with '\r', then
 * replaces the character after it with '\n'.
 * Returns the index of the new '\n' on success, -1 if there is no newline,
 * or -2 if there's no space for a new character.
 */
int convert_to_crlf(char *buf, int buflen);

/*
 * Search the first n characters of buf for a network newline (\r\n).
 * Return one plus the index of the '\n' of the first network newline,
 * or -1 if no network newline is found.
 */
int find_network_newline(const char *buf, int buflen);

/*
 * Search the first n characters of buf for an unix newline (\n).
 * Return the index of the first newline, or -1 if no newline is found.
 */
int find_unix_newline(const char *buf, int buflen);

/*
 * Reads as much as possible from file descriptor fd into the given buffer.
 * Returns number of bytes read, or 0 if fd closed, or -1 on error.
 */
int read_to_buf(int fd, Buffer *buf);

/*
 * Returns a pointer to the first non-consumed message in the buffer,
 * with the corresponding newline. Sets msg_len to the length of the message.
 * Returns NULL if no full message is found, or on error.
 */
char* get_next_msg(Buffer *buf, int *msg_len, NewlineType newline);

/*
 * Sets the consumed field on a given buffer to the given value.
 * Returns 0 on completion, -1 on error.
 */
int mark_consumed(Buffer *buf, int index);

/*
 * Increments the consumed field on a given buffer to the given value.
 * Returns 0 on completion, -1 on error.
 */
int advance_consumed(Buffer *buf, int increment);

/*
 * Removes consumed characters from the buffer and shifts the rest
 * to make space for new characters.
 * Returns incomplete on error.
 */
void shift_buffer(Buffer *buf);

/*
 * Returns 1 if buffer is full, 0 if not, -1 on error.
 */
int is_buffer_full(Buffer *buf);

/*
 * Utility Functions (non-essential)
 */

/*
 * Prints requested format string into stderr, prefixing properly
 */
void debug_print(const char *format, ...);

/*
 * Zeroes out all the fields of a given client
 */
int reset_client_struct(Client *client);

/*
 * Zeroes out all the fields of a given buffer.
 */
int reset_buffer_struct(Buffer *buffer);

/*
 * Zeroes out all of the fields and segments of a given message structure.
 */
int reset_message_struct(Message *message);

/*
 * Zeroes out all the fields of a given segment structure.
 */
int reset_segment_struct(Packet *pack);

int set_packet_head(char buf[MESG_LEN], char a, char b, char c, char d);

int set_packet_head(char buf[MESG_LEN], char a, char b, char c, char d);

#endif
