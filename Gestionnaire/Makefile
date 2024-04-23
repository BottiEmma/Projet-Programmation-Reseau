CC=gcc
CFLAGS = -pthread

SRC :=   $(wildcard *.c) 

OBJECTS = $(SRC:.c=.o) 

main : $(OBJECTS)
	$(CC) $(CFLAGS) -o $@ $^

start:
	./main

clean:
	rm -rf $(OBJECTS)
	rm main
