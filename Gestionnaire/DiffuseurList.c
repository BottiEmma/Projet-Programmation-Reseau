
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#define MAX_SIZE 5

struct DiffuseurList
{
    char *diffuseur_information;
    struct DiffuseurList *next;
};
struct DiffuseurList diffuseurList;

char *getIdentifiant(char *message)
{
    int size = 8;
    char *message_copy = message + 5; /*  ----|######## */
    char *id = malloc(size + 1);
    char *id_copy = id;
    memset(id, '#', size);
    id[8] = '\0';
    int i = 0;
    while (1)
    {
        if (message_copy[i] == ' ')
        {
            break;
        }
        char *c = malloc(1);
        c[0] = message_copy[i];
        memmove(id_copy, c, 1);
        id_copy = id_copy + 1;
        i = i + 1;
    }
    return id;
}

int getSize(struct DiffuseurList *head)
{
    struct DiffuseurList *current = head;
    if (current == NULL)
    {
        return 0;
    }
    int counter = 0;
    while (current != NULL)
    {
        current = current->next;
        counter++;
    }
    return counter;
}

int canAdd(struct DiffuseurList *head)
{
    if (getSize(head) == MAX_SIZE)
    {
        return 0;
    }

    return 1;
}

void push_to_list(struct DiffuseurList *head, char *diffuseur_information)
{
    struct DiffuseurList *current = head;
    while (current->next != NULL)
    {
        current = current->next;
    }

    /* now we can add a new variable */
    current->next = (struct DiffuseurList *)malloc(sizeof(struct DiffuseurList *));
    current->next->diffuseur_information = diffuseur_information;
    current->next->next = NULL;
}

void print_list(struct DiffuseurList *node)
{
    struct DiffuseurList *current = node;

    while (current != NULL)
    {
        printf("%s\n", current->diffuseur_information);
        current = current->next;
    }
}

void remove_from_list(struct DiffuseurList **head, int index)
{
    int counter = 0;
    struct DiffuseurList *current = *head;
    struct DiffuseurList *prev;
    if (current != NULL && index == counter)
    {
        *head = current->next;
        free(current);
        return;
    }

    while (current != NULL && counter != index)
    {
        prev = current;
        current = current->next;
        counter++;
    }

    if (current == NULL)
    {
        return;
    }

    prev->next = current->next;
    free(current);
}

int isInList(struct DiffuseurList *node, char *message)
{
    char *message_to_add = getIdentifiant(message);
    while (node != NULL)
    {
        char *current_message = getIdentifiant(node->diffuseur_information);
        if (strcmp(message_to_add, current_message) == 0)
        {
            return 1;
        };
        node = node->next;
    }

    return 0;
}
