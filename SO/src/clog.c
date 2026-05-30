#include <stdio.h>
#include <stdlib.h>
#include <time.h>

#include "clog.h"
#include "synchronization.h"

void log_init(char *filename)
{

    FILE *f = fopen(filename, "w"); // cria o ficheiro se não esixtir e abrimos em w (write)

    if (f == NULL) // vereficar se o ficheiro não é null
    {
        perror("Erro ao inicializar o ficheiro de log");
        exit(1);
    }

    fclose(f);
}

void log_write_operation(struct info_container *info, char *operation)
{

    sem_wait(info->sems->log_mutex); // reservar o recurso partilhado (o log é partilhado)

    FILE *f = fopen(info->log_filename, "a"); // abrir o ficheiro em modo append, para adiconar dados no final caso ja exista o ficheiro

    if (f != NULL)
    { // se nao for null ent fazemos a escrita

        struct timespec ts;
        struct tm *tm_info;

        getTime(&ts); // passamos o ts para a funcção preenche como clock_gettime (utilizando o ctime)

        tm_info = localtime(&ts.tv_sec); // converter para segundos

        int ms = ts.tv_nsec / 1000000; // converter para milisegundos a partir dos nanosegundos

        //  Escrever no ficheiro com o formato do enunciado
        fprintf(f, "%04d%02d%02d %02d:%02d:%02d.%03d %s\n", tm_info->tm_year + 1900, tm_info->tm_mon + 1,
                tm_info->tm_mday, tm_info->tm_hour, tm_info->tm_min, tm_info->tm_sec, ms, operation);

        fclose(f); // fechar o ficheiro
    }
    sem_post(info->sems->log_mutex); // libertar o fhcieiro para outros processos
}

void log_write_result(struct info_container *info, int m_id, int server_id, double estimate)
{

    sem_wait(info->sems->log_mutex); // reservar o recurso partilhado (o log é partilhado)

    if (m_id > *info->last_logged_id)
    {

        FILE *f = fopen(info->log_filename, "a"); // abrir o ficheiro
        if (f != NULL)
        {
            struct timespec ts;
            struct tm *tm_info;

            getTime(&ts); // passamos o ts para a funcção preenche como clock_gettime (utilizando o ctime)

            tm_info = localtime(&ts.tv_sec); // converter para segundos

            int ms = ts.tv_nsec / 1000000; // converter para milisegundos a partir dos nanosegundos

            fprintf(f, "%04d%02d%02d %02d:%02d:%02d.%03d result %d server %d estimate %.4f\n",
                    tm_info->tm_year + 1900, tm_info->tm_mon + 1, tm_info->tm_mday,
                    tm_info->tm_hour, tm_info->tm_min, tm_info->tm_sec,
                    ms, m_id, server_id, estimate);
            fclose(f);

            *info->last_logged_id = m_id; // Atualizar o id
        }
    }

    sem_post(info->sems->log_mutex); // libertar o fhcieiro para outros processos
}