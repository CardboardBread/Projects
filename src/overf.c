#include <stdio.h>
#include <stdlib.h>

const int start_health = 150;

int calc_health(int round) {
  int health = start_health;
  for (int i = 2; i < round; i++) {
    if (i >= 10) {
      health = (health * 1.1);
    } else {
      health = health + 100;
    }
  }

  return health;
}

int main(void) {

  int round_health;
  for (int i = 1; i < 200 ; i++) {
    round_health = calc_health(i);
    printf("Round number %3d: Health = %12d.\n", i, round_health);
  }

  return 0;
}
