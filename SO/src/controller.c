/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <stdio.h>
#include <stdlib.h>
#include "controller.h"
#include "main.h"
#include "memory.h"
#include "sensor.h"
#include "synchronization.h"
#include "random_measurement.h"
#include "server.h"
#include "ctime.h"

void controller_receive_measurement(MeasurementInfo *m, int controller_id, struct info_container *info, struct buffers *buffs)
{
  sem_wait(info->sems->sensors_controllers->unread);
  sem_wait(info->sems->sensors_controllers->mutex);

  read_sensor_controller_buffer(buffs->buff_sensors_controllers, controller_id, info->buffers_size, m);

  // se a leitura for bem feita
  if (m->m_id != -1)
  {
    sem_post(info->sems->sensors_controllers->mutex);
    sem_post(info->sems->sensors_controllers->free_space); // posicao libertada
  }
  else // se n encontrou medicoes devolve unread para outro controller
  {
    sem_post(info->sems->sensors_controllers->mutex);
    sem_post(info->sems->sensors_controllers->unread);
  }
}

void controller_process_measurement(MeasurementInfo *m, int controller_id, struct info_container *info)
{
  // guardar o id do controlador dentro da estrutura que guarda as medições medidas pelos controladores
  m->controller_id = controller_id;

  // passar a medição e vereficamos se ela é válida
  if (is_valid_measurement(m->value))
  {
    m->state = VALID; // se a medição for válida ent assinalamos como válida, ( usamos o enum State)
  }
  else
  {
    m->state = INVALID; // se for invalida marcamos como invalida
    /*
    se for invalida temos que icnrimentar o contador de medições
    invalidas e metemos o sensor_id , pois foi este que fez a
    medição invalida
    */
    info->num_invalid_measurements[m->sensor_id]++;
  }

  // Comparação mais facil com o enum do que com o num que representa
  printf("[Controller %d] Medição validada no ciclo %d: %.4f (%s)\n",
         controller_id, m->m_id, m->value, (m->state == VALID ? "VALID" : "INVALID"));
}

void controller_send_measurement(MeasurementInfo *m, struct info_container *info, struct buffers *buffs)
{
  m->counter_servers = info->n_servers; // Quantos servidores vão receber medidas

  sem_wait(info->sems->controllers_servers->free_space);
  sem_wait(info->sems->controllers_servers->mutex);

  write_controller_servers_buffer(buffs->buff_controllers_servers, info->buffers_size, m);

  sem_post(info->sems->controllers_servers->mutex);

  // para cada server que vai ler a medicao
  for (int i = 0; i < info->n_servers; i++)
    sem_post(info->sems->controllers_servers->unread);
}

// Lógica toda de um controlador , usando os metodos de receber, enciar e processar
int execute_controller(int controller_id, struct info_container *info, struct buffers *buffs)
{
  MeasurementInfo m; // inicializamos para guardar as medições

  // ciclo que continua enquanto a flag terminate for 0
  while (*(info->terminate) == 0)
  {
    // receber medição, usamos &m para dar o endereço para escrevermos diretamente lá
    controller_receive_measurement(&m, controller_id, info, buffs);

    // Se o measurement id for -1, é uma medição inválida
    if (m.m_id != -1)
    {
      controller_process_measurement(&m, controller_id, info); // validamos a medição que está em m
      updateControllerProcesses(&m.change_time);               // Assim que o controlador processa a medição, registamos o instante
      controller_send_measurement(&m, info, buffs);            // enviamos a medição para o buffer
    }
  }
  return 0;
}