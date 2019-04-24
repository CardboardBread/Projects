#include <stdio.h>
#include <stdlib.h>

int main(void) {

  for (unsigned int i = 0; i < 512; i++) {
    printf("Number %d is %c as char.\n", i, i);
  }

  return 0;
}
