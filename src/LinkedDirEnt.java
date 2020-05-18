/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Directory table entry class for linked allocation
*/

/**
 * To store file start and end blocks in the FAT for the linked allocation case
 */
public class LinkedDirEnt {
  int start;

  public LinkedDirEnt(int start) {
    this.start = start;
  }

}
