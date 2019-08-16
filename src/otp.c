#include <stdio.h>
#include <stdlib.h>
#include <time.h>

int main() {
    srand(time(NULL));

    char code[15] = "hello password";
    char key[15] = {0};
    char shift[15] = {0};
    char res[15] = {0};

    printf("Code: %s\n", code);

    for (int i = 0 ; i < 15 - 1; i++) {
        key[i] = rand() % 127;
    }
    key[14] = '\0';

    printf("Key: %s\n", key);

    for (int i = 0; i < 15 - 1; i++) {
        shift[i] = code[i] + key[i];
    }
    shift[14] = '\0';

    printf("Shift: %s\n", shift);

    for (int i = 0; i < 15 - 1; i++) {
        res[i] = shift[i] - key[i];
        if (res[i] < 0) {
            res[i] += 127;
        }
    }
    res[14] = '\0';

    printf("Result: %s\n", res);
}

