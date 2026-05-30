#ifndef CTIME_H_GUARD
#define CTIME_H_GUARD

#include <stdlib.h>
#include <time.h>
#include <unistd.h>

typedef struct timestamps
{
struct timespec mainStart; // tempo em que o pedido foi criado
struct timespec sensorMeasures; // tempo em que o sensor fez a medição
struct timespec controllerProcesses; // tempo em que o controlador processou a medição
struct timespec serverProcesses; // tempo em que o servidor processou a medição
} timestamps;

/** Preenche o struct com o instante em que o pedido das medições foi criado*/
void updateMainStartTime(struct timestamps *times);

/** Preenche o struct com o instante em que o sensor faz a medição */
void updateSensorMeasures(struct timestamps *times);

/** Preenche o struct com o instante em que o controlador processa a medição */
void updateControllerProcesses(struct timestamps *times);

/** Preenche o struct com o instante em que o servidor processa a medição */
void updateServerProcesses(struct timestamps *times);

/** Funcão auxiliar para evitar código repetido, porque nós somos bons programadores com boas práticas */
 void getTime(struct timespec *ts);

 #endif