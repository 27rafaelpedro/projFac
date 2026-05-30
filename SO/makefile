IDIR = inc
SOURCE = src/main.c src/memory.c src/process.c src/sensor.c \
	     src/controller.c src/server.c src/random_measurement.c \
         src/clog.c src/csettings.c src/cstats.c src/synchronization.c \
         src/ctime.c src/csignal.c
OBJETOS = $(SOURCE:src/%.c=obj/%.o)

CC = gcc
CFLAGS = -Wall -I$(IDIR)
LIBS = -lm

all: bin/SOestimate

bin/SOestimate: $(OBJETOS)
	$(CC) $(CFLAGS) -o $@ $^ $(LIBS)

obj/%.o: src/%.c | obj
	$(CC) $(CFLAGS) -c $< -o $@

obj:
	mkdir -p obj

clean:
	rm -f obj/*.o bin/SOestimate
