/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <sys/mman.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "memory.h"
#include <stdlib.h>
#include <unistd.h>
#include <stdio.h>

// Criar uma região da memória com size elementos, 1 byte para cada, com elementos a 0
void *allocate_dynamic_memory(int size)
{ 
  void *dmemory = calloc(1,size);

  if(dmemory == NULL){
  return calloc(1, size);
  }
  deallocate_dynamic_memory(dmemory);
  return calloc(1, size);
}

void *create_shared_memory(char *name, int size)
{
  shm_unlink(name); // Assumindo que esta zona da memória já existe, efetuamos a limepza
  int fd = shm_open(name, O_CREAT | O_RDWR, S_IRUSR | S_IWUSR);// Criar uma zona de memória partilhada

  if(fd == -1){ // Erro na criação da zona da memória partilhada
    printf("-create error");
    exit(1);
  }
  int ret = ftruncate(fd, size); // Definir tamanho na memória partilhada
  
  if(ret == -1){ // Erro na definição de tamanho da memória
    printf("-ftruncate error");
    exit(2);
  }
  void *ptr = mmap(0, size, PROT_EXEC | PROT_WRITE | PROT_READ, MAP_SHARED, fd, 0); // Projeção da zona criada

  if((int*) ptr == MAP_FAILED){ // Falha em projetar a memória partilhada
    printf("-mmap error");
    exit(3);
  }
  return ptr; // Se não houver problemas, devolvemos a projeção da zona esperada.
}

// Libertar zona dinâmica alocada
void deallocate_dynamic_memory(void *ptr)
{
  free(ptr);
}

// Libertar zona partilhada alocada
void destroy_shared_memory(char *name, void *ptr, int size)
{
  munmap(ptr, size);
  shm_unlink(name);
}

int write_main_sensors_buffer(struct circ_buffer *buffer, int buffer_size, const MeasurementInfo *req)
{
  int write = buffer->ptrs->in; // Obter pointer para escrita
  int read = buffer->ptrs->out; // Obter pointer para leitura

  // Se o apontador write não estiver logo atrás do apontador read há espaço para escrita
  if ((write + 1) % buffer_size != read)
  {
    buffer->buffer[write] = *req;                 // Escrevemos req no measurementInfo da atual posição do buffer
    buffer->ptrs->in = (write + 1) % buffer_size; // Escrevemos na posição write, deslocamos o apontador para a próxima posição livre
    return 1;                                     // Escrita bem sucedida
  }
  return 0; // Buffer sem espaço livre
}

void read_main_sensors_buffer(struct circ_buffer *buffer, int buffer_size, int expected_m_id, MeasurementInfo *req)
{
  int read = buffer->ptrs->out; // Obter pointer para leitura
  int write = buffer->ptrs->in; // Obter pointer para escrita
  int wasRead = 0;              // Boolean que garante se a leitura foi feita ou não

  // Procurar entrada no buffer com expected_m_id
  for (int i = 0; i < buffer_size; i++)
  {
    int ind = (read + i) % buffer_size; // Posição atual com base no comportamento do array circular

    // Se os apontadores estão em posições diferentes, há posições para ler
    if (ind != write)
    {
      // Se a posição atual tem o id esperado
      if (buffer->buffer[ind].m_id == expected_m_id)
      {
        buffer->buffer[ind].counter_sensors -= 1; // Decrementar o num de sensores por ler
        *req = buffer->buffer[ind];               // Efetuar a leitura

        // Se já lemos todos os sensores desta posição
        if (buffer->buffer[ind].counter_sensors == 0)
        {
          buffer->ptrs->out = (read + 1) % buffer_size; // Atualizamos a posição de read
        }
        wasRead = 1; // Leitura feita
        break;       // Como já lemos na posição certa, sair do ciclo
      }
    }
    else if (ind == write && wasRead == 0)
    {                 // Percorremos o buffer e não fizemos leituras
      req->m_id = -1; // Não existem requests
      break;
    }
  }
}

int write_sensor_controller_buffer(struct ra_buffer *buffer, int buffer_size, const MeasurementInfo *m)
{
  int *pt = buffer->ptrs; // Obter pointer comum a ambos os arrays

  // Percorrer buffer
  for (int i = 0; i < buffer_size; i++)
  {
    // Se a posição está livre
    if (pt[i] == 0)
    {
      buffer->buffer[i] = *m; // Escrever
      pt[i] = 1;              // Esta posição está agora ocupada
      return 1;               // Escrita bem sucedida
    }
  }
  return 0; // Não encontramos posições livres para escrever
}

void read_sensor_controller_buffer(struct ra_buffer *buffer, int controller_id, int buffer_size, MeasurementInfo *m)
{
  int *pt = buffer->ptrs; // Obter pointer comum a ambos os arrays
  int wasRead = 0;

  // Percorrer buffer
  for (int i = 0; i < buffer_size; i++)
  {
    // Encontramos o controlador que queremos
    if (pt[i] == 1 && buffer->buffer[i].controller_id == controller_id)
    {
      *m = buffer->buffer[i]; // Leitura efetuada
      pt[i] = 0;              // Posição atual passa a estar livre
      wasRead = 1;            // Leitura bem sucedida
      break;
    }

    // Se não foram feitas leituras e chegamos ao fim do array
    else if (i == buffer_size - 1 && wasRead == 0)
    {
      m->m_id = -1; // Definimos measurementid para -1
    }
  }
}

int write_controller_servers_buffer(struct ra_buffer *buffer, int buffer_size, const MeasurementInfo *m)
{
  int *pt = buffer->ptrs; // Obter pointer comum a ambos os arrays

  for (int i = 0; i < buffer_size; i++)
  {
    if (pt[i] == 0)
    {                         // Se a posição está livre
      buffer->buffer[i] = *m; // Escrever
      pt[i] = 1;              // Esta posição está agora ocupada
      return 1;               // Escrita bem sucedida
    }
  }
  return 0; // Buffer cheio (Sem posições livres)
}

void read_controller_servers_buffer(struct ra_buffer *buffer, int buffer_size, int expected_m_id, int expected_controller_id, MeasurementInfo *m)
{
  int *pt = buffer->ptrs; // Obter pointer comum a ambos os arrays
  int wasRead = 0;

  for (int i = 0; i < buffer_size; i++)
  {

    // Se a posição atual está ocupada e tem os ids de mensagem e controller esperados
    if (pt[i] == 1 && buffer->buffer[i].m_id == expected_m_id && buffer->buffer[i].controller_id == expected_controller_id)
    {
      // decrementar primeiro e so depois guardar o valor
      buffer->buffer[i].counter_servers -= 1; // Decrementar o num de servidores por ler
      *m = buffer->buffer[i];                 // Efetuar leitura
      wasRead = 1;

      // Se já lemos todos os servidores nesta posição
      if (wasRead == 1 && buffer->buffer[i].counter_servers == 0)
      {
        pt[i] = 0; // A posição passa a estar vazia
      }

      break;
    }

    // Se percorremos o buffer todo e não fizemos leituras
    else if (wasRead == 0 && i == buffer_size - 1)
    {
      m->m_id = -1;
      break;
    }
  }
}
