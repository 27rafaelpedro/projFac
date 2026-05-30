/* Grupo 42
 * Rafael Pedro - 63697
 * Gabriel Brilha - 63792
 * Josué Dias - 63699
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>

#include "main.h"
#include "memory.h"
#include "process.h"
#include "cstats.h"
#include "csettings.h"
#include "synchronization.h"
#include "csignal.h"
#include "ctime.h"
#include "clog.h"

// Ler os ficheiros, o dos arguemntos e das configurações, passando a ser argv[1] e agrv[2]
void main_args(int argc, char *argv[], struct info_container *info)
{
    if (argc < 3) // vereficar se foram passados os dois nomes dos ficheiros de leitura
    {
        printf("[Main] Correto: ./SOestimate <args_file> <settings_file>\n");
        exit(1);
    }

    read_args(info, argv[1]); // chamar a função que le or agrs.txt

    read_settings(info, argv[2]); // chamar a função que le o settings.txt
}

// Alocar memoria dinamica
void create_dynamic_memory_structs(struct info_container *info, struct buffers *buffs)
{
    // Arrays dos PIDs do processos filho
    info->sensors_pids = allocate_dynamic_memory(info->n_sensors * sizeof(int));
    info->controllers_pids = allocate_dynamic_memory(info->n_sensors * sizeof(int));
    info->servers_pids = allocate_dynamic_memory(info->n_servers * sizeof(int));

    // Estruturas dos buffers de comunicação
    buffs->buff_main_sensors = allocate_dynamic_memory(sizeof(struct circ_buffer));
    buffs->buff_sensors_controllers = allocate_dynamic_memory(sizeof(struct ra_buffer));
    buffs->buff_controllers_servers = allocate_dynamic_memory(sizeof(struct ra_buffer));
}

void create_shared_memory_structs(struct info_container *info, struct buffers *buffs)
{
    // Contadores e flag terminate partilhados entre todos os processos
    info->total_measurements = create_shared_memory(ID_SHM_TOTAL_MEASUREMENTS, sizeof(int));
    info->terminate = create_shared_memory(ID_SHM_TERMINATE, sizeof(int));
    info->last_logged_id = create_shared_memory(ID_SHM_LAST_LOGGED_ID, sizeof(int));

    // Contadores por sensor e por servidor
    info->num_generated_measurements = create_shared_memory(
        ID_SHM_SENSORS_GENERATED,
        info->n_sensors * sizeof(int));
    info->num_invalid_measurements = create_shared_memory(
        ID_SHM_SENSORS_INVALID,
        info->n_sensors * sizeof(int));
    info->num_estimates = create_shared_memory(
        ID_SHM_SERVERS_PROCESSED,
        info->n_servers * sizeof(int));

    // Buffer circular Main -> Sensors
    buffs->buff_main_sensors->ptrs = create_shared_memory(
        ID_SHM_MAIN_SENSORS_PTR,
        sizeof(struct pointers));
    buffs->buff_main_sensors->buffer = create_shared_memory(
        ID_SHM_MAIN_SENSORS_BUFFER,
        info->buffers_size * sizeof(MeasurementInfo));

    // Buffer de acesso aleatorio Sensors -> Controllers
    buffs->buff_sensors_controllers->ptrs = create_shared_memory(
        ID_SHM_SENSORS_CONTROLLERS_PTR,
        info->buffers_size * sizeof(int));
    buffs->buff_sensors_controllers->buffer = create_shared_memory(
        ID_SHM_SENSORS_CONTROLLERS_BUFFER,
        info->buffers_size * sizeof(MeasurementInfo));

    // Buffer de acesso aleatorio Controllers -> Servidores
    buffs->buff_controllers_servers->ptrs = create_shared_memory(
        ID_SHM_CONTROLLERS_SERVERS_VERSION,
        info->buffers_size * sizeof(int));
    buffs->buff_controllers_servers->buffer = create_shared_memory(
        ID_SHM_CONTROLLERS_SERVERS_LATEST,
        info->buffers_size * sizeof(MeasurementInfo));
}

void create_processes(struct info_container *info, struct buffers *buffs)
{
    int i;
    // Criar um par sensor/controller para cada indice
    for (i = 0; i < info->n_sensors; i++)
    {
        // criar processo sensor i e guarda o PID
        info->sensors_pids[i] = launch_process(SENSOR_PROCESS, i, info, buffs);
        // criar processo controller i associado ao sensor i e guarda o PID
        info->controllers_pids[i] = launch_process(CONTROLLER_PROCESS, i, info, buffs);
    }
    // criar um server para cada indice i
    for (i = 0; i < info->n_servers; i++)
    {
        // criar o processo server i e guarda o PID
        info->servers_pids[i] = launch_process(SERVER_PROCESS, i, info, buffs);
    }
}

void user_interaction(struct info_container *info, struct buffers *buffs)
{
    char command[100]; // user command
    int running = 1;   // controlo do loop

    while (running)
    {
        printf("[Main] Introduzir comando: ");
        scanf("%s", command); // ler comando escrito pelo utilizador
        printf("\n");

        if (strcmp(command, "measure") == 0)
        {
            (*info->total_measurements)++;        // incrementar o contador total de medições
            MeasurementInfo req;                  // criar uma nova estrutura para representar a ronda
            req.m_id = *info->total_measurements; // numero da ronda
            req.state = REQUEST;
            req.counter_sensors = info->n_sensors; // numero de sensores q precisam ler o pedido
            req.counter_servers = info->n_servers; // numero de servers q precisam ler o pedido

            // espera q haja espaço no buffer
            sem_wait(info->sems->main_sensors->free_space);
            // para escrever
            sem_wait(info->sems->main_sensors->mutex);

            int sucesso = 0;
            while (sucesso == 0)
                sucesso = write_main_sensors_buffer(buffs->buff_main_sensors, info->buffers_size, &req);

            // libertar mutex
            sem_post(info->sems->main_sensors->mutex);

            // notificar cada sensor que ha dados para ler
            for (int i = 0; i < info->n_sensors; i++)
                sem_post(info->sems->main_sensors->unread);

            printf("[Main] Ciclo de medições começou com m_id=%d.\n\n", req.m_id);
            log_write_operation(info, "measure"); // só adcionar o comando ao ficheiro log.
        }
        else if (strcmp(command, "stat") == 0)
        {
            print_stat(info);
            write_final_statistics(info);      // escrever as estatisticas no ficheiro de estatisticas
            log_write_operation(info, "stat"); // só adcionar o comando ao ficheiro log.
        }
        else if (strcmp(command, "help") == 0)
        {
            help();
            log_write_operation(info, "help"); // só adcionar o comando ao ficheiro log.
        }
        else if (strcmp(command, "read") == 0)
        {
            int m_id;
            scanf("%d", &m_id);        // para saber o ID do read
            read_estimate(info, m_id); // ler a estimativa

            // Para o log
            char log_msg[30];
            sprintf(log_msg, "read %d", m_id);  // escrever dentro do log_msg
            log_write_operation(info, log_msg); // só adcionar o comando ao ficheiro log. ( ficara read 2, por exemplo)
        }
        else if (strcmp(command, "end") == 0)
        {
            log_write_operation(info, "end"); // só adcionar o comando ao ficheiro log.
            running = 0;                      // sair do loop
        }
        else
            printf("[Main] comando inválido \n");
    }
}

void help(void)
{
    printf("[Main] Operacoes disponíveis: \n");
    printf("[Main] measure - solicita uma ronda de medições \n");
    printf("[Main] read <cycle_id> - obtém a estimativa de um ciclo \n");
    printf("[Main] stat - mostra o estado atual do sistema \n");
    printf("[Main] help - mostra os comandos disponíveis \n");
    printf("[Main] end - termina o sistema \n\n");
}

void print_stat(struct info_container *info)
{
    printf("[Main] Estado do sistema:\n");

    // configuracao do sistema
    printf("[Main] n_sensors=%d, n_servers=%d, n_buffers_size=%d\n",
           info->n_sensors, info->n_servers, info->buffers_size);
    printf("[Main] terminated=%d, total_measurements=%d\n",
           *info->terminate, *info->total_measurements);

    int i;

    printf("[Main] Sensores:\n");
    for (i = 0; i < info->n_sensors; i++) // PIDs de todos os sensores
        printf("[Main]  Sensor=%d: PID = %d, Gerou=%d\n", i, info->sensors_pids[i], info->num_generated_measurements[i]);

    printf("[Main] Controllers:\n");
    for (i = 0; i < info->n_sensors; i++) // PIDs de todos os controllers
        printf("[Main]  Controller %d: PID = %d\n", i, info->controllers_pids[i]);

    printf("[Main] Servers:\n");
    for (i = 0; i < info->n_servers; i++) // PIDs de todos os servidores
        printf("[Main]  Server=%d: PID = %d, Estimativas:%d\n", i, info->servers_pids[i], info->num_estimates[i]);

    printf("\n"); // espaço extra para ler melhor
}

void wait_processes(struct info_container *info)
{
    int i;
    for (i = 0; i < info->n_sensors; i++)
        wait_process(info->sensors_pids[i]);
    for (i = 0; i < info->n_sensors; i++)
        wait_process(info->controllers_pids[i]);
    for (i = 0; i < info->n_servers; i++)
        wait_process(info->servers_pids[i]);
}

void write_final_statistics(struct info_container *info)
{
    write_fstat(info);
}

void end_execution(struct info_container *info, struct buffers *buffs)
{
    *info->terminate = 1; // avisar processos filhos p/ pararem

    wakeup_processes(info); //  acorodar todos os processos presos nos semaforos
    wait_processes(info);   // esperar que terminem

    write_final_statistics(info); // mostrar stats finais

    destroy_all_semaphores(info->sems); // chamar o detry_all_semaphores para destruir os semaforos

    // libertar memoria antes de terminar
    destroy_shared_memory_structs(info, buffs);  // codigo que tava na main passou para aqui
    destroy_dynamic_memory_structs(info, buffs); // para apenas chamar o end_execution e apagar tudo
}

void read_estimate(struct info_container *info, int m_id)
{

    FILE *f = fopen(info->log_filename, "r"); // abrir o ficheiro log, que está no info
    if (f == NULL)
    {
        printf("Ainda não existem medições registadas. \n"); // vereficar se n é null
        return;
    }

    char linha[256];
    int encontrado = 0;

    while (fgets(linha, sizeof(linha), f))
    { // lemos o ficheiro linha a linha até encontrar o ID

        if (strstr(linha, "result") != NULL)
        { // vereficar linha a linha se é um registo de resultado
            int id_lido;
            int server_lido;
            double estimativa;

            // extrair os valores , usar o strstr para ir logo para o result
            if (sscanf(strstr(linha, "result"), "result %d server %d estimate %lf ", &id_lido, &server_lido, &estimativa) == 3)
            {

                if (id_lido == m_id)
                { // verificar se o id lido é igual ao m_id
                    printf("[Main] Medição %d: Estimativa = %.4f (Servidor %d)\n ", id_lido, estimativa, server_lido);
                    encontrado = 1; // metemos a varivale que contrla se foi encontrado o resultado a 1
                    break;          // saimos do ciclo
                }
            }
        }
    }
    if (!encontrado)
        printf("[Main] Medição %d não foi encontrada ou não existe. \n", m_id);
    fclose(f);
}

void destroy_dynamic_memory_structs(struct info_container *info, struct buffers *buffs)
{
    // Libertar arrays de PIDs dos processos
    deallocate_dynamic_memory(info->sensors_pids);
    deallocate_dynamic_memory(info->controllers_pids);
    deallocate_dynamic_memory(info->servers_pids);

    // Libertar estruturas dos buffers de comunicação
    deallocate_dynamic_memory(buffs->buff_main_sensors);
    deallocate_dynamic_memory(buffs->buff_sensors_controllers);
    deallocate_dynamic_memory(buffs->buff_controllers_servers);
}

void destroy_shared_memory_structs(struct info_container *info, struct buffers *buffs)
{
    // libertar contadores e flag de terminação
    destroy_shared_memory(ID_SHM_TERMINATE, info->terminate, sizeof(int));
    destroy_shared_memory(ID_SHM_TOTAL_MEASUREMENTS, info->total_measurements, sizeof(int));
    destroy_shared_memory(ID_SHM_SENSORS_GENERATED, info->num_generated_measurements, info->n_sensors * sizeof(int));
    destroy_shared_memory(ID_SHM_SENSORS_INVALID, info->num_invalid_measurements, info->n_sensors * sizeof(int));
    destroy_shared_memory(ID_SHM_SERVERS_PROCESSED, info->num_estimates, info->n_servers * sizeof(int));

    // libertar buffer circular Main -> Sensores
    destroy_shared_memory(ID_SHM_MAIN_SENSORS_PTR, buffs->buff_main_sensors->ptrs, sizeof(struct pointers));
    destroy_shared_memory(ID_SHM_MAIN_SENSORS_BUFFER, buffs->buff_main_sensors->buffer, info->buffers_size * sizeof(MeasurementInfo));

    // libertar buffer de acesso aleatório Sensores -> Controllers
    destroy_shared_memory(ID_SHM_SENSORS_CONTROLLERS_PTR, buffs->buff_sensors_controllers->ptrs, info->buffers_size * sizeof(int));
    destroy_shared_memory(ID_SHM_SENSORS_CONTROLLERS_BUFFER, buffs->buff_sensors_controllers->buffer, info->buffers_size * sizeof(MeasurementInfo));

    // libertar buffer de acesso aleatório Controllers -> Servidores
    destroy_shared_memory(ID_SHM_CONTROLLERS_SERVERS_VERSION, buffs->buff_controllers_servers->ptrs, info->buffers_size * sizeof(int));
    destroy_shared_memory(ID_SHM_CONTROLLERS_SERVERS_LATEST, buffs->buff_controllers_servers->buffer, info->buffers_size * sizeof(MeasurementInfo));

    // destruir o last_logged_id
    destroy_shared_memory(ID_SHM_LAST_LOGGED_ID, info->last_logged_id, sizeof(int));
}

int main(int argc, char *argv[])
{
    // inicializar estruturas de dados
    struct info_container *info = allocate_dynamic_memory(sizeof(struct info_container));
    struct buffers *buffs = allocate_dynamic_memory(sizeof(struct buffers));

    // executar codigo principal
    main_args(argc, argv, info);

    // inicializar o ficheiro log
    log_init(info->log_filename);

    help();
    create_dynamic_memory_structs(info, buffs);
    create_shared_memory_structs(info, buffs);

    // criar todos  os semaforos
    info->sems = create_all_semaphores(info->buffers_size); // passando o tamanho do buffer para a função
    print_all_semaphores(info->sems);

    setup_signals(info, buffs); // inicializar os sinais

    // iniciar os processos e interações
    create_processes(info, buffs);
    user_interaction(info, buffs);

    end_execution(info, buffs); // encerrar o programa, com as lispezas todas
    return 0;
}
