#define _POSIX_C_SOURCE 200809L
#include "ctime.h"
#include "stdio.h"
#include <stdlib.h>
#include <time.h>

// MeasurementInfo tem um novo struct timestamps que é fornecido como argumento nas funções
// para atualizar o estado do struct e os respetivos tempos


 void getTime(struct timespec *ts)
 {
    if(clock_gettime(CLOCK_REALTIME, ts) == -1){ // Preencher o struct ts com informação do realtime
        printf("Error in clock_gettime");
        exit(1);
    }
 }

void updateMainStartTime(struct timestamps *times)
{
   getTime(&times-> mainStart);
}

void updateSensorMeasures(struct timestamps *times)
{
   getTime(&times-> sensorMeasures); 
}

void updateControllerProcesses(struct timestamps *times)
{
   getTime(&times-> controllerProcesses);
}

void updateServerProcesses(struct timestamps *times){
   getTime(&times-> serverProcesses);
}
