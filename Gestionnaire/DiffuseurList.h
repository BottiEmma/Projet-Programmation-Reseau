
#ifndef DIFF_LIST_H
#define DIFF_LIST_H

#define MAX_SIZE 5
struct DiffuseurList
{
    char *diffuseur_information;
    struct DiffuseurList *next;
};
char *getIdentifiant(char *message);
void remove_from_list(struct DiffuseurList **head, int index);
int canAdd(struct DiffuseurList *head);
void push_to_list(struct DiffuseurList *head, char *diffuseur_information);
void print_list(struct DiffuseurList *node);
int getSize(struct DiffuseurList *head);

int isInList(struct DiffuseurList *node, char *message);
#endif
