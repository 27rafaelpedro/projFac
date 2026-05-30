/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <stdio.h>
#include <stdlib.h>
#include "main.h"
#include "memory.h" // para ler e escrever as medições
#include "sensor.h"
#include "synchronization.h"
#include "random_measurement.h" // para aceder ao get_measurement()
#include "ctime.h"

int execute_sensor(int sensor_id, struct info_container *info, struct buffers *buffs)
{
    seed_sensor_rng(sensor_id); // inicializar o gerador de números aleatórios para este sensor
    int expected_m_id = 1;      // variavel so pra saber qual o proximo que esperamos

    // ciclo que continua enquanto a flag terminate for 0
    while (*(info->terminate) == 0)
    {
        MeasurementInfo req;
        updateMainStartTime(&req.change_time); // Assim que criamos o pedido, registamos o instante
        sensor_receive_request(&req, expected_m_id, info, buffs);

        if (*(info->terminate) == 1) // tem de terminar
            break;

        // vereficar se o id experado é igual ao da medição
        if (req.m_id == expected_m_id)
        {
            sensor_process_request(&req, sensor_id, info); // gera a medição
            updateSensorMeasures(&req.change_time);        // Assim que o sensor processa a medição, registamos o instante
            sensor_send_measurement(&req, info, buffs);    // enviar a leitura parao buffer
            expected_m_id++;                               // incrimentamos para passar para outra leitura
        }
    }
    return 0;
}

// ler um pedido
void sensor_receive_request(MeasurementInfo *req, int expected_m_id, struct info_container *info, struct buffers *buffs)
{
    sem_wait(info->sems->main_sensors->unread); // bloqueia ate haver dados
    sem_wait(info->sems->main_sensors->mutex);  // exclusao mutua

    read_main_sensors_buffer(buffs->buff_main_sensors, info->buffers_size, expected_m_id, req);

    if (req->m_id != -1) // leitura ocorreu
    {
        // Verificar se o sensor foi o ultimo a ler a posicao esperada
        if (req->counter_sensors == 0) // se chegou a 0, a posicao foi libertada
            sem_post(info->sems->main_sensors->free_space);
        sem_post(info->sems->main_sensors->mutex); // libertar acesso
    }
    else // se n encontrou a medicao manda unread
    {
        sem_post(info->sems->main_sensors->mutex);
        sem_post(info->sems->main_sensors->unread);
    }
}

// Função que cria uma medição a aprtir de um pedido.
void sensor_process_request(MeasurementInfo *m, int sensor_id, struct info_container *info)
{
    m->sensor_id = sensor_id;                      // guardar o sensor id dentro da estrutura m ( para saber o sensor desta medição)
    m->controller_id = sensor_id;                  // Sao sempre iguais
    m->value = get_measurement();                  //  obtem o valor smiulado a partir do get measurement
    m->state = MEASURED;                           // atualiza o estado usando o enum State (dizer o estado da medição)
    info->num_generated_measurements[sensor_id]++; // incrementamos o contador para as medições válidas
}

// nota: o ra_buffer é a estrutura definida na memory (random access buffer)
void sensor_send_measurement(MeasurementInfo *m, struct info_container *info, struct buffers *buffs)
{
    sem_wait(info->sems->sensors_controllers->free_space); // espera posicao livre
    sem_wait(info->sems->sensors_controllers->mutex);      // exclusao mutua

    write_sensor_controller_buffer(buffs->buff_sensors_controllers, info->buffers_size, m);

    sem_post(info->sems->sensors_controllers->mutex);  // liberta acesso
    sem_post(info->sems->sensors_controllers->unread); // avisa

    printf("[Sensor %d] Medição gerada no ciclo %d: %.4f\n", m->sensor_id, m->m_id, m->value);
}