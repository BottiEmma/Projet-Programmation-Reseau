
#include <pthread.h>
#ifndef GES_H
#define GES_H

extern pthread_mutex_t lock;
bool startsWith(const char *pre, const char *str);
void *start_gestionnaire(void *arg);

#endif
