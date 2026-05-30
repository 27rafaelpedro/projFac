#include <stdio.h>
#include <unistd.h>

#include "main.h"
#include "cstats.h"


// logica para escrever num ficheiro
void write_fstat(struct info_container *info){

    FILE *f = fopen(info -> statistics_filename, "w");    // abrir o ficheiro para ler 
    if (f== NULL){                                        // verificar se não é null
        return;
    }
    // logica para ficar igual ao enunciado
    fprintf(f, "===== FINAL STATISTICS =====\n");
    fprintf(f, "Main PID: %d\n", getpid());
    // bloco dos sensores
    fprintf(f, "Sensors:\n");
    for (int i = 0; i < info -> n_sensors; i++){   /// para mostrar todos os sensores com PIDs
       fprintf(f, "Sensor %d (PID %d): generated = %d\n", i, 
              info->sensors_pids[i], info->num_generated_measurements[i]);
    }
    //bloco dos controladores 
    fprintf(f, "Controllers:\n");
    for (int i = 0; i < info->n_sensors; i++) { // n_sensors porque o numero de controladores é igual
         fprintf(f, "controller=%d pid=%d\n", i, info->controllers_pids[i]);
    }
    // bloco dos servers 
    fprintf(f, "Servers:\n");
    for (int i = 0; i < info->n_servers; i++) {    // fazer a mesma coisa mas agora para os servers
        fprintf(f, "Server %d (PID %d): estimates = %d\n", 
                i, info->servers_pids[i], info->num_estimates[i]);
    }
    fprintf(f, "Last logged ID: %d\n", *info -> last_logged_id);
    fclose(f);
}