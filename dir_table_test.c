/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Directory entry and table implementation using a dynamic array
*/

#include <stdio.h>
#include <stdlib.h>
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
  DirEnt *array[INITIAL_CAPACITY];
} DirTable;

void init(DirTable *dt) {
  dt->cap = INITIAL_CAPACITY;
  dt->size = 0;
}

bool should_enlarge(DirTable *dt) {
  return dt->size == dt->cap - 2;
}

void enlarge(DirTable *dt) {
  printf("Increasing capacity\n");
  DirEnt *new_array[dt->cap * 2];
  for (int i = 0; i < dt->size; i++) {
    new_array[i] = dt->array[i];
  }
  DirTable temp = {dt->cap * 2, dt->size, new_array};
  dt = &temp;
}

void insert(DirTable *dt, DirEnt *entry) {
  if (should_enlarge(dt)) {
    enlarge(dt);
  }
  dt->array[dt->size + 1] = entry;
  dt->size++;
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
  for (int i = 0; i < dt->size; i++) {
    printf(" %d", dt->array[i]->file_id);
  }
  printf("\n");
}

int main() {
  printf("merhaba\n");
  DirTable *table = malloc(sizeof(*table));
  printf("merhaba\n");
  for (int i = 0; i < 100; i++) {
    printf("merhaba\n");
    DirEnt *entry = malloc(sizeof(*entry));
    entry->file_id = 1;
    entry->size = 10;
    entry->start = 10;
    insert(table, entry);
    print_array(table);
  }
  return 0;
}