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
char *gestionnaire_address = NULL;
char *gestionnaire_port = NULL;

char *formatNumber(int n)
{
    char *num = malloc(3);
    memset(num, '\0', 3);
    if (n < 10)
    {
        sprintf(num, "0%d", n);
    }
    else
    {
        sprintf(num, "%d", n);
    }

    return num;
}

char *formatNumberThree(int n)
{
    char *num = malloc(4);
    memset(num, '\0', 4);
    if (n < 10)
    {
        sprintf(num, "00%d", n);
    }
    else if (n < 100)
    {
        sprintf(num, "0%d", n);
    }
    else
    {
        sprintf(num, "%d", n);
    }
    return num;
}

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

    getaddrinfo(gestionnaire_address, gestionnaire_port, &hints, &servinfo);

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
    {
        sin_size = sizeof their_addr;
        new_fd = accept(sockfd, (struct sockaddr *)&their_addr, &sin_size);
        if (new_fd == -1)
        {
            perror("accept");
            continue;
        }

        char message[160];
        int read = recv(new_fd, message, 160 * sizeof(char), 0);
        message[read] = '\0';

        if (startsWith("REGI", message))
        {
            if (canAdd(head->next) && isInList(head->next, message) != 1)
            {
                pthread_mutex_lock(&lock);
                char *message_cpy = malloc(160);
                strcpy(message_cpy, message);
                push_to_list(head, message_cpy);
                printf("[ADDING] - %s\n", message + 5);

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
            sprintf(s, "LINB %s\r\n", formatNumber(getSize(head->next)));
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
        else if (startsWith("MGES", message))
        {

            struct DiffuseurList *current;
            if (head->next != NULL)
            {
                printf("%s \n", "[TRANSFERING] MESSAGE TO DIFFUSEUR");
                current = head->next;
            }
            while (current != NULL)
            {
                char **message_information = split_message(current->diffuseur_information);
                int port = atoi(message_information[5]);
                struct sockaddr_in adress_sock;
                adress_sock.sin_family = AF_INET;
                adress_sock.sin_port = htons(port);
                inet_aton(message_information[4], &adress_sock.sin_addr);
                int descr = socket(PF_INET, SOCK_STREAM, 0);
                connect(descr, (struct sockaddr *)&adress_sock,
                        sizeof(struct sockaddr_in));

                send(descr, message, strlen(message), 0);
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
    if (argc < 2)
    {
        printf("Gestionnaire - Config file missing \n");
        exit(0);
    }

    FILE *fp = fopen(argv[1], "r");
    if (fp == NULL)
    {
        perror("Error while opening the file.\n");
        exit(EXIT_FAILURE);
    }

    char *temp_address = NULL;
    size_t len = 0;
    getline(&temp_address, &len, fp);
    getline(&gestionnaire_port, &len, fp);
    char ip_address[16];
    memset(ip_address,'\0',16);
    memcpy(ip_address,temp_address,strlen(temp_address));
    char *ptr = strtok(ip_address, ".");
    char *split[4];

    int i = 0;
    while (ptr != NULL)
    {
        split[i++] = formatNumberThree(atoi(ptr));
        ptr = strtok(NULL, ".");
    }

    char gestionnaire_address_temp[15];
    memcpy(gestionnaire_address_temp, split[0], 3);
    memcpy(gestionnaire_address_temp + 3, ".", 1);
    memcpy(gestionnaire_address_temp + 4, split[1], 3);
    memcpy(gestionnaire_address_temp + 7, ".", 1);
    memcpy(gestionnaire_address_temp + 8, split[2], 3);
    memcpy(gestionnaire_address_temp + 11, ".", 3);
    memcpy(gestionnaire_address_temp + 12, split[3], 3);

    gestionnaire_address = malloc(16);
    memset(gestionnaire_address,'\0',16);
    memcpy(gestionnaire_address,gestionnaire_address_temp,15);
    
    /* *************** */
    printf("---------------------------------------\n");
    printf("[GEST ADDRESS] : %s \n", gestionnaire_address);
    printf("[GES PORT] : %s \n", gestionnaire_port);
    printf("---------------------------------------\n");

    /* **************** */
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
