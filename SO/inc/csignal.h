#ifndef CSIGNAL_H_GUARD
#define CSIGNAL_H_GUARD

#include "main.h"

/*
 * Configura os signal handler na Main
 * Regista o handler de Ctrl+C
 * e chama o alarme SIGALRM
 */
void setup_signals(struct info_container *info, struct buffers *buffs);

/*
 * Os processos filhos ignoram o Ctrl+C
 */
void ignore_signals();

#endif