/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Directory entry and table implementation using a dynamic array
*/

#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>

#define INITIAL_CAPACITY  4

// type definitions to make the code more readable.
// specifically to distinguish between byte and block index values
typedef int block; 
typedef int byte; 

typedef struct {
  int file_id;
  block start;
  block size;
} DirEnt;

typedef struct {
  int cap;
  int size;
  DirEnt *array[INITIAL_CAPACITY]
} DirTable;

void init(DirTable *dt) {
  dt->cap = INITIAL_CAPACITY;
  dt->size = 0;
}

bool should_enlarge(DirTable *dt) {
  return dt->size == dt->cap - 1;
}

void enlarge(DirTable *dt) {
  printf("Increasing capacity\n");
  DirEnt *new_array[dt->cap * 2] = malloc(sizeof(*new_array));
  for (int i = 0; i < dt->size; i++) {
    new_array[i] = dt->array[i];
  }
  dt->array = new_array;
  dt->cap *= 2;
}

void insert(DirTable *dt, DirEnt *entry) {
  if (should_enlarge(dt)) {
    enlarge(dt);
  }
  dt->array[dt->size + 1] = entry;
}

DirEnt *find_by_id(DirTable *dt, int target) {
  for (int i = 0; i < dt->size; i++) {
    if (dt->array[i]->file_id == target) {
      return dt->array[i];
    }
  }
  return NULL;
}

void print_array(DirTable *dt) {
  char *sep = "";
  for (int i = 0; i < dt->size; i++) {
    printf("%s%d", sep, dt->array[i]->file_id);
    sep = " ";
  }
  printf("\n");
}
