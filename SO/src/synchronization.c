#include "synchronization.h"
#include <stdlib.h>
#include <stdio.h>
#include "memory.h"
#include <unistd.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "main.h"

sem_t *create_semaphore(char *name, int value)
{
sem_unlink(name); // Mesma lógica do memory
sem_t *sem = sem_open(name, O_CREAT, S_IRUSR | S_IWUSR | S_IRGRP | S_IROTH, value); // Criar novo sem como valor inicial fornecido

if(sem == SEM_FAILED){
  printf("-sem_open");
  exit(1);
}
return sem; 
}

void destroy_semaphore(char *name, sem_t *sem)
{
 sem_close(sem); // Fechar semáforo
 sem_unlink(name); // Remover semáforo
}

struct buffer_semaphores *create_buffer_semaphores(int value, char *unread_name, char *free_name, char *mutex_name)
{
  struct buffer_semaphores* b_sem; // Definir apontador de retorno
  b_sem = allocate_dynamic_memory(sizeof(struct buffer_semaphores)); // Alocar espaço na memória para adquirir apontador

  b_sem-> unread = create_semaphore(unread_name, 0); // Posições por ler (Consumidor)
  b_sem-> free_space = create_semaphore(free_name, value); // Quantas posições estão livres para escrita (Produtor)
  b_sem-> mutex = create_semaphore(mutex_name, 1); // Garante exclusão mútua, i.e um processo de cada vez a operar na SC (advinhem quem leu os
                                                    //eslidessssssss)
  
  return b_sem;
}

void destroy_buffer_semaphores(struct buffer_semaphores *sems, char *unread_name, char *free_name, char *mutex_name)
{
destroy_semaphore(unread_name, sems->unread); // Destruir semáforos
destroy_semaphore(free_name, sems->free_space);
destroy_semaphore(mutex_name, sems->mutex);
}

struct semaphores *create_all_semaphores(unsigned value) // Value is free space size
{
struct semaphores* p_sem; // Definir apontador de retorno
p_sem = allocate_dynamic_memory(sizeof(struct semaphores)); // Alocamos memória pelo que devolve void* que é transformado em semaphores *
                                                           // e porque o hassan assim o disse
p_sem-> main_sensors = create_buffer_semaphores(value, SEM_MAIN_SENSORS_UNREAD, SEM_MAIN_SENSORS_FREE, SEM_MAIN_SENSORS_MUTEX);
p_sem-> sensors_controllers = create_buffer_semaphores(value, SEM_SENSORS_CONTROLLERS_UNREAD, SEM_SENSORS_CONTROLLERS_FREE, SEM_SENSORS_CONTROLLERS_MUTEX); // É necessário definir nomes distintos para os semáforos?
p_sem-> controllers_servers =  create_buffer_semaphores(value, SEM_CONTROLLERS_SERVERS_UNREAD, SEM_CONTROLLERS_SERVERS_FREE , SEM_CONTROLLERS_SERVERS_MUTEX);
p_sem-> terminate_mutex = create_semaphore(SEM_TERMINATE_MUTEX, 1); // Mesma lógica abaixo
p_sem-> log_mutex = create_semaphore(SEM_LOG_MUTEX, 1); // Se o log mutex assegura que somente 1 processo escreve no log, significa que quando
                                                       // um processo fizer sem_wait() este vai agir como sem binário, pelo que faz a escrita e baza

return p_sem;
}

void destroy_all_semaphores(struct semaphores *sems)
{
  destroy_buffer_semaphores(sems-> main_sensors, SEM_MAIN_SENSORS_UNREAD, SEM_MAIN_SENSORS_FREE, SEM_MAIN_SENSORS_MUTEX);
  destroy_buffer_semaphores(sems-> sensors_controllers, SEM_SENSORS_CONTROLLERS_UNREAD, SEM_SENSORS_CONTROLLERS_FREE, SEM_SENSORS_CONTROLLERS_MUTEX);
  destroy_buffer_semaphores(sems-> controllers_servers, SEM_CONTROLLERS_SERVERS_UNREAD, SEM_CONTROLLERS_SERVERS_FREE, SEM_CONTROLLERS_SERVERS_MUTEX);
  destroy_semaphore(SEM_TERMINATE_MUTEX, sems-> terminate_mutex);
  destroy_semaphore(SEM_LOG_MUTEX, sems-> log_mutex);
}

void print_all_semaphores(struct semaphores *sems)
{
  if(sems != NULL){
  printf("[Synchronization] Semáforos sincronizados com sucesso.\n");
  }
  else{
  printf("[Synchronization] Erro na sincronização de semáforos. \n");
  exit(1);
  }
};

void wakeUpCall(struct buffer_semaphores *buff_sems, int numEntity)
{
  sem_t *unread = buff_sems-> unread; // Obter cada semáforo individual do conjunto fornecido
  sem_t *free = buff_sems-> free_space; // Não utilizamos o sem mutex, pelo que fazendo vários sem_posts(mutex) incrementamos o valor do semáforo > 1
                                        // deixando de atuar como sem binário
  for(int i = 0; i < numEntity; i++){ // Como temos várias entidades (sensors, controllers e servidores) a mexerem no buffer é necessário "acordar"
                                      // o num de instancias que estas entidades tem
  sem_post(unread);
  sem_post(free);
  }
}

/* Wake child processes that may be blocked in sem_wait(). */
void wakeup_processes(struct info_container *info)
{
struct semaphores *sems = info->sems; // Obter pointer para semáforos deste container

struct buffer_semaphores *main_sem = sems-> main_sensors; // Obter cada conjunto dos 3 semáforos
struct buffer_semaphores *sens_con_sem = sems-> sensors_controllers;
struct buffer_semaphores *con_serv_sem = sems-> controllers_servers;

wakeUpCall(main_sem, info-> n_sensors);
wakeUpCall(sens_con_sem, info-> n_sensors);
wakeUpCall(con_serv_sem, info-> n_servers);
}
