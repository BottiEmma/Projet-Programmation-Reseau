#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <netinet/in.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>
#include <stdbool.h>
#include "DiffuseurList.h"
#include "DiffuseurChecker.h"
#include "Gestionnaire.h"
#include <pthread.h>

pthread_mutex_t lock;

bool startsWith(const char *pre, const char *str)
{
    size_t lenpre = strlen(pre),
           lenstr = strlen(str);
    return lenstr < lenpre ? false : memcmp(pre, str, lenpre) == 0;
}

void *start_gestionnaire(void *arg)
{
    struct DiffuseurList *head = arg;
    struct addrinfo hints, *servinfo, *p;
    struct sockaddr_storage their_addr;
    socklen_t sin_size;
    memset(&hints, 0, sizeof hints);
    hints.ai_family = AF_UNSPEC;
    hints.ai_socktype = SOCK_STREAM;
    hints.ai_flags = AI_PASSIVE;
    int sockfd, new_fd;

    getaddrinfo(NULL, "6060", &hints, &servinfo);

    for (p = servinfo; p != NULL; p = p->ai_next)
    {
        if ((sockfd = socket(p->ai_family, p->ai_socktype,
                             p->ai_protocol)) == -1)
        {
            perror("server: socket");
            continue;
        }

        if (bind(sockfd, p->ai_addr, p->ai_addrlen) == -1)
        {
            close(sockfd);
            perror("server: bind");
            continue;
        }
        break;
    }
    freeaddrinfo(servinfo);
    listen(sockfd, 10);

    while (1)
    { // main accept() loop
        sin_size = sizeof their_addr;
        new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
        if (new_fd == -1)
        {
            perror("accept");
            continue;
        }

        char message[160];
        int read = recv(new_fd, message, 99 * sizeof(char), 0);
        message[read] = '\0';

        if (startsWith("REGI", message))
        {
            if (canAdd(head->next) && isInList(head->next, message) != 1)
            {
                pthread_mutex_lock(&lock);
                char *message_cpy = malloc(160);
                strcpy(message_cpy, message);
                push_to_list(head, message_cpy);
                printf("[ADDING] - %s \n", message + 5);

                char *message = "REOK\r\n";
                send(new_fd, message, strlen(message), 0);
                pthread_mutex_unlock(&lock);
            }
            else
            {
                char *message = "RENO\r\n";
                send(new_fd, message, strlen(message), 0);
                close(new_fd);
            }
        }
        else if (startsWith("LIST", message))
        {
            char s[11];
            sprintf(s, "LINB %d\r\n", getSize(head->next));
            send(new_fd, s, 11, 0);
            struct DiffuseurList *current;
            if (head->next != NULL)
            {
                current = head->next;
            }
            while (current != NULL)
            {
                char formatted_message[39] = "ITEM ";
                strcat(formatted_message, (current->diffuseur_information) + 5);
                send(new_fd, formatted_message, strlen(current->diffuseur_information), 0);
                current = current->next;
            }
        }
        else
        {
            printf("Message recu : %s\n", message);
        }
    }
}

int main(int argc, char **argv)
{
    struct DiffuseurList *head = NULL;
    head = (struct DiffuseurList *)malloc(sizeof(struct DiffuseurList));
    pthread_t t_gestionnaire;
    pthread_t t_dc;
    pthread_create(&t_gestionnaire, NULL, start_gestionnaire, head);
    pthread_create(&t_dc, NULL, check_diffuseur, head);
    pthread_join(t_gestionnaire, NULL);
    pthread_join(t_dc, NULL);

    return 0;
}