/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Directory table entry class for contiguous allocation
*/

/**
 * A simple class to store file ids and length in blocks in the directory table
 */
public class ContDirEnt {
  int start;
  int length;

  public ContDirEnt(int start, int length) {
    this.start = start;
    this.length = length;
  }

  // returns the index of the final block allocated to the file
  public int getEndIndex() {
    return start + length - 1;
  }

}
