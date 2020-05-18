/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Block class for linked
*/

/**
 * This class represents the contents of a block for the linked allocation method.
 * Each block contains some content, and the index of the next block allocated to that file.
 */

public class LinkedBlock {
  int content;
  int next;

  // a block has a pointer to index -1 by default.
  // it remains so when a block is the final block of a file
  public LinkedBlock(int content, int next) {
    this.content = content;
    this.next = next;
  }
}
