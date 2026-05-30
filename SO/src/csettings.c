#include <stdio.h>
#include <stdlib.h>
#include "csettings.h"

void read_args(struct info_container *info, char *filename)
{
    FILE *f = fopen(filename, "r");
    if (f == NULL)
    {
        perror("args.txt: erro");
        exit(1);
    }
    fscanf(f, "%d", &info->n_sensors);
    fscanf(f, "%d", &info->n_servers);
    fscanf(f, "%d", &info->buffers_size);

    fclose(f);
}

void read_settings(struct info_container *info, char *filename)
{
    FILE *f = fopen(filename, "r");
    if (f == NULL)
    {
        perror("settings.txt: erro");
        exit(1);
    }
    fscanf(f, "%s", info->log_filename);
    fscanf(f, "%s", info->statistics_filename);
    fscanf(f, "%d", &info->period);

    fclose(f);
}