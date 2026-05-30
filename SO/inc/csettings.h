#ifndef CSETTINGS_H_GUARD
#define CSETTINGS_H_GUARD

#include "main.h" // para termos o info_container

// Ler args.txt e mete o n_sensors, n_servers e buff_size no info
void read_args(struct info_container *info, char *filename);

// Ler settings.txt e mete o log_filename, statistics_filename e period no info
void read_settings(struct info_container *info, char *filename);

#endif