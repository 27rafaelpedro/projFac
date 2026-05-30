/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>
#include "process.h"
#include "sensor.h"
#include "controller.h"
#include "server.h"

int launch_process(enum process_type process_type, int process_id, struct info_container *info, struct buffers *buffs)
{
    int pid = fork();
    if (pid < 0)
    {
        perror("Erro ao criar processo");
        exit(1);
    }
    else if (pid == 0)
    {
        switch (process_type)
        {
        case SENSOR_PROCESS:
            execute_sensor(process_id, info, buffs);
            break;
        case CONTROLLER_PROCESS:
            execute_controller(process_id, info, buffs);
            break;
        case SERVER_PROCESS:
            execute_server(process_id, info, buffs);
            break;
        default:
            break;
        }
        exit(0);
    }
    else
    {
        return pid;
    }
}

int wait_process(int process_id)
{
    int status;
    waitpid(process_id, &status, 0);
    return WEXITSTATUS(status); // obter info de como terminou o filho
}
