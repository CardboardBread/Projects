#include <stdio.h>
#include <stdlib.h>

#include "chophelper.h"

#define MESG_LEN 128
#define HEAD_LEN 4
#define TAIL_LEN 4
#define DATA_LEN 120

int sigint_received;

Client *global_clients[MAX_CONNECTIONS];

void sigint_handler(int code);

/*
 * SIGINT handler:
 * We are just raising the sigint_received flag here. Our program will
 * periodically check to see if this flag has been raised, and any necessary
 * work will be done in main() and/or the helper functions. Write your signal
 * handlers with care, to avoid issues with async-signal-safety.
 */
void sigint_handler(int code) {
  debug_print("sigint_handler: received SIGINT, setting flag");
  sigint_received = 1;
}

int main(void) {
  // Reset SIGINT received flag.
  sigint_received = 0;
}
