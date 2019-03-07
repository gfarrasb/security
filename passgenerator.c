    #include <stdlib.h>
    #include <stdio.h>
    #include <time.h>
    int main(void) {
        int i;
        char c;
        srand(time(NULL));
        printf("Your Random Password is: ");
        for (i = 0; i < 5; ++i) {
            c = 'a' + rand() % ('z' - 'a' + 1);
            printf("%c", c);
        }
        for (i = 0; i < 5; ++i) {
            c = 'A' + rand() % ('Z' - 'A' + 1);
            printf("%c", c);
        }
        for (i = 0; i < 5; ++i) {
            c = '1' + rand() % ('1' - '9' + 1);
            printf("%c", c);
        }
        printf("\n\n");
    }
