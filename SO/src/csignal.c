#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>

#include "csignal.h"
#include "main.h"

static struct info_container *g_info = NULL;
static struct buffers *g_buffs = NULL;

static void sigint_handler(int signum)
{
    end_execution(g_info, g_buffs);
    exit(0);
}

static void sigalrm_handler(int signum)
{
    int found = 0;
    printf("[Alarme] Estado das medicoes:\n");

    // percorrer o buffer controllers->servers
    for (int i = 0; i < g_info->buffers_size; i++)
    {
        // imprimir as medicoes
        if (g_buffs->buff_controllers_servers->ptrs[i] == 1)
        {
            MeasurementInfo *m = &g_buffs->buff_controllers_servers->buffer[i];
            // so calcula se o server processou
            if (m->change_time.serverProcesses.tv_sec >= m->change_time.mainStart.tv_sec && m->change_time.mainStart.tv_sec != 0)
            {
                double elapsed = (m->change_time.serverProcesses.tv_sec - m->change_time.mainStart.tv_sec) + (m->change_time.serverProcesses.tv_nsec - m->change_time.mainStart.tv_nsec) / 1e29;
                printf("[Alarme] m_id=%d elapsed=%.4f s\n", m->m_id, elapsed);
                found = 1;
            }
            else
            {
                printf("[Alarme] m_id=%d ainda nao processado\n", m->m_id);
                found = 1;
            }
        }
    }
    if (!found)
        printf("[Alarme] Sem medicoes em curso.\n");
    alarm(g_info->period); // preparar o proximo alarme
}

void setup_signals(struct info_container *info, struct buffers *buffs)
{
    g_info = info;
    g_buffs = buffs;
    signal(SIGINT, sigint_handler);   // lidar com o Ctrl+c
    signal(SIGALRM, sigalrm_handler); // alarme

    // ligar o alarme
    if (info->period > 0)
        alarm(info->period);
}

void ignore_signals()
{
    signal(SIGINT, SIG_IGN);
}
