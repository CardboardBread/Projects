#ifndef __CHOP_HELPER_H__
#define __CHOP_HELPER_H__

/*
 * Macros and Constants
 */

#ifndef PORT
  #define PORT 50001
#endif

#define PACKET_LEN 4
#define TEXT_LEN 255

/// Header Packet bytes
#define PACKET_HEAD 0 // currently no purpose
#define PACKET_STATUS 1 // defines what this packet means
#define PACKET_CONTROL1 2 // parameter 1 for packet type
#define PACKET_CONTROL2 3 // parameter 2 for packet type

/// status bytes
#define NULL_BYTE 0
#define HEADER_BYTE 1
#define START_TEXT 2
#define END_TEXT 3
#define END_TRANSMISSION 4
#define ENQUIRY 5
#define ACKNOWLEDGE 6
#define NEG_ACKNOWLEDGE 21
#define END_TRANSMISSION_BLOCK 23
#define FILE_SEPARATOR 28
#define GROUP_SEPARATOR 29
#define RECORD_SEPARATOR 30
#define UNIT_SEPARATOR 31
#define CONTROL_KEY 17
#define CONTROL_END 20
#define CANCEL 24
#define END_OF_MEDIUM 25

#define CONNECTION_QUEUE 5
#define MAX_CONNECTIONS 20

#define PIPE_READ 0
#define PIPE_WRITE 1

#define MIN_FD 0

static const int debug_fd = STDERR_FILENO;
static const char debug_header[] = "[DEBUG] ";

/*
 * Structures and Types
 */

struct socket_buffer {
	char buf[TEXT_LEN];
	int consumed;
	int inbuf;
};
typedef struct socket_buffer Buffer;

struct client {
	int socket_fd;
  int inc_flag;
  int out_flag;
	struct socket_buffer *buffer;
};
typedef struct client Client;

struct packet {
  char head[PACKET_LEN];
	char buf[TEXT_LEN];
  int inbuf;
};
typedef struct packet Packet;

/*
 * Client Management functions
 */

/*
 * Blocks until a client connects to a server already running on listen_fd.
 * Length of clients array is assumed to be MAX_CONNECTIONS.
 * Returns 0 on success, -1 on error and 1 if the list of clients is full.
 */
int setup_new_client(const int listen_fd, Client *clients[]);

/*
 * Frees the client at the given index in the given array.
 * Length of client array is assumed to be MAX_CONNECTIONS.
 * Returns 0 on success, -1 on error and 1 if the target client doesn't exist.
 */
int remove_client(const int client_index, Client *clients[]);

/*
 * Sending functions
 */

/*
 * Writes data to the given socket, assuming msg is the beginning of an array
 * of msg_len length.
 * Automatically packages the given data into a text style packet.
 * Refuses to send messages that are too large to fit in a single text packet.
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int write_buf_to_client(Client *cli, const char *msg, const int msg_len);

/*
 * Writes the given packet to the given socket.
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int write_packet_to_client(Client *cli, Packet *pack);

/*
 * Writes the given string to the given client.
 * Assumes the string is null-terminated, unpredictable behaviour otherwise.
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int send_str_to_client(Client *cli, const char *str);

/*
 * Writes a given format string to the given socket.
 * Functions similarly to dprintf, with a different return behaviour
 * Returns 0 on success, -1 on error, and 1 on an imcomplete/failed write.
 * ERRNO will be preserved from the write call.
 */
int send_fstr_to_client(Client *cli, const char *format, ...);

/*
 * Receiving functions
 */

int read_header(Client *cli);

int parse_text(Client *cli, const int control1, const int control2);

int parse_enquiry(Client *cli, const int control1);

int parse_acknowledge(Client *cli);

int parse_neg_acknowledge(Client *cli);

int parse_cancel(Client *cli);


/*
 * Utility Functions
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
 * Zeroes out all the fields of a given segment structure.
 */
int reset_segment_struct(Packet *pack);

#endif
