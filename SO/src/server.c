/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <stdio.h>
#include "memory.h"
#include "server.h"
#include "main.h"
#include "clog.h"
#include "ctime.h"

int execute_server(int server_id, struct info_container *info, struct buffers *buffs)
{
    MeasurementInfo m;     // onde vamos anotar as medições
    int expected_m_id = 1; // deve iniciar a 1
    int read_count = 0;    // contador de medições lidas  (NUNCA É INICIALIZADO)
    int valid_count = 0;   // contador de medições válidas (NUNCA É INICIALIZADO)
    double estimate = 0.0; // valor da estimativa
    int received_flags[info->n_sensors];

    for (int i = 0; i < info->n_sensors; i++) // iniciar received_flags a 0
        received_flags[i] = 0;

    // ciclo principal
    while (*info->terminate == 0)
    {
        // lemos uma medição que ainda temos neste ciclo
        read_expected_cycle_measurement(&m, expected_m_id, received_flags, info, buffs);

        // só entramos se encontarmos uma medição nao lida
        if (m.m_id != -1)
        {
            received_flags[m.sensor_id] = 1; // marca que este sensor ja tem trabalho
            read_count++;                    // Incrementa o número total leituras

            // chamamos  o process para retirar 1 ao contador dos servidores e libertar  se for o ultimo
            server_process_measurement(&m, server_id, info, &estimate, &valid_count);
            updateServerProcesses(&m.change_time); // Assim que o servidor processa a medição, registamos o instante

            // se ja tiver lido todos os sensores ent
            if (read_count == info->n_sensors)
            {
                // calcular a média final, usamos o ternário (?) para evitar a divisão por 0
                double final_avg = (valid_count > 0) ? (estimate / valid_count) : 0.0;

                if (valid_count > 0)
                    printf("[Server %d] Nova estimativa: ciclo %d = %.4f (valid_count=%d)\n",
                           server_id, expected_m_id, final_avg, valid_count);
                else
                    printf("[Server %d] Nova estimativa: ciclo %d = NULL\n", server_id, expected_m_id);

                printf("\n");
                log_write_result(info, expected_m_id, server_id, final_avg);
                info->num_estimates[server_id]++; // mostrava sempre 0 para os servers

                // damos reset para a proxima ronda
                expected_m_id++; // avança par ao ciclo seguinte
                read_count = 0;  // recomeçamos a contagem
                valid_count = 0; // recomeçamos a contagem da leituras validas
                estimate = 0.0;  // limpamos a soma para a proxima estimativa

                // começamos do 0, pomos tudo como nao lido
                for (int i = 0; i < info->n_sensors; i++)
                    received_flags[i] = 0;
            }
        }
    }
    return 0;
}

void read_expected_cycle_measurement(MeasurementInfo *m, int expected_m_id, const int *received_flags, struct info_container *info, struct buffers *buffs)
{
    sem_wait(info->sems->controllers_servers->unread); // esperar que haja algo no buffer
    sem_wait(info->sems->controllers_servers->mutex);  // exclusao mutua

    int wasRead = 0;

    for (int i = 0; i < info->n_sensors; i++)
    {
        // vereficamos se já lemos este sensor
        if (received_flags[i] == 0)
        {
            read_controller_servers_buffer(buffs->buff_controllers_servers, info->buffers_size, expected_m_id, i, m);

            if (m->m_id != -1) // encontramos a medicao
            {
                wasRead = 1;
                break;
            }
        }
    }
    if (wasRead)
    {
        // o read_controller_servers_buffer ja decrementou counter_servers
        // se chegou a 0, a posicao foi libertada -> devolvemos o espaco livre
        if (m->counter_servers == 0)
            sem_post(info->sems->controllers_servers->free_space);
    }
    else // n encontramos, devolvemos count ao semaforo
    {
        m->m_id = -1;
        sem_post(info->sems->controllers_servers->unread);
    }
    sem_post(info->sems->controllers_servers->mutex); // libertar acesso
}

int server_process_measurement(MeasurementInfo *m, int server_id, struct info_container *info, double *estimate, int *valid_count)
{
    if (m->state == VALID)
    {                          // adicionamos á estimativa apenas se o valor for valido, verificamos primeiro
        *estimate += m->value; // adcionamos ao estime o valor
        (*valid_count)++;      // incriemtnamos o número de medições válidas usadas
    }
    return 0;
}