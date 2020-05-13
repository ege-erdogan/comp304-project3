/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Contiguous Space Allocation 
*/

// TODO: i can make it so that there is a single main file,
//  and imports either one of implementations depending on a flag
//  or global variable

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

// directory entry and table data structures are defined in this header file
#include "dir_table.h"

// -- MAIN METHOD SIGNATURES -- 
bool create_file(int, byte);
int access(int, byte);
bool extend(int, block);
void shrink(int, block);

// -- DATA STRUCTURES --

// array representing the storage device
// each element corresponds to a block, initially all 0 (empty)
int storage[32768];

// directory table
//   can also abstract away a file type
// buffer (one block size long)
//  can be a boolean full?empty

int main(int argc, char *argv[]) {
  
  return 0;
}