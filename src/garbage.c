#include <stdio.h>
#include <stdlib.h>

#define ARRLEN 100

int main(void) {
  char arr[ARRLEN];

  printf("Contents: ");
  int nonzero = 0;
  for (int i = 0; i < ARRLEN; i++) {
    if (arr[i] != 0) {
      nonzero++;
    }
    printf("%d ", arr[i]);
  }
  printf("\n");

  int place = 0;
  char carr[nonzero];
  for (int i = 0; i < ARRLEN; i++) {
    if (arr[i] != 0) {
      carr[place] = arr[i];
      place++;
    }
  }
  printf("\n");

  printf("Filtered: ");
  for (int i = 0; i < place; i++) {
    printf("%d ", carr[i]);
  }
  printf("\n");

  return 0;
}
