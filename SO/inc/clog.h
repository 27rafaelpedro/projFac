#ifndef CLOG_H_GUARD
#define CLOG_H_GUARD

#include "main.h"

/*
* Abre o ficheiro e limpa o conteúdo.
*/
void log_init(char* filename);

/*
*Escreve uma operação genérica.
*/
void log_write_operation(struct info_container* info, char* operation);


/*
*Escreve o resultado de uma medição.
*/
void log_write_result(struct info_container* info, int  m_id, int server_id, double estimate);


#endif