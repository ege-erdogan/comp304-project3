/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Contiguous AllocationMethod Implementation
*/

import java.util.HashMap;

public class ContiguousAllocation implements AllocationMethod {

  HashMap<Integer, DirEnt> directoryTable;
  int[] storage;

  private int blockSize;

  public ContiguousAllocation(int blockSize) {
    directoryTable = new HashMap<>();
    storage = new int[32768];
    this.blockSize = blockSize;
  }

  @Override
  public void createFile(int id, int bytes) throws Exception {
    int start;
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if ((start = haveSpace(blocks)) == -1) {
      performCompaction();
    }
    if ((start = haveSpace(blocks)) == -1) {
      throw new Exception("Not enough space to allocate blocks: " + blockSize);
    }
    allocate(id, start, blocks);
  }

  @Override
  public int access(int id, int byteOffset) {
    return 0;
  }

  @Override
  public void extend(int id, int blocks) throws Exception {

  }

  @Override
  public void shrink(int id, int blocks) {

  }

  // checks if there is a contiguous space of given size
  // returns the starting index if space exists, -1 otherwise
  private int haveSpace(int blocks) {
    int freeSpace = 0;
    for (int i = 0; i < storage.length; i++) {
      if (storage[i] == 0) {
        freeSpace++;
        if (freeSpace == blocks) {
          return i - blocks + 1;
        }
      } else {
        freeSpace = 0;
      }
    }
    return -1;
  }

  // allocates space in storage for a given block size and adds it to the DT
  // each block contains its index value if it is used
  // this can cause unexpected behavior if it is called before checking with haveSpace
  private void allocate(int id, int start, int length) {
    DirEnt entry = new DirEnt(start, length);
    directoryTable.put(id, entry);
    for (int i = start; i < length; i++) {
      storage[i] = i;
    }
  }

  // allocates each file in the directory table from start
  // in the end, there is a single group of free blocks
  private void performCompaction() {
    int[] newStorage = new int[storage.length];
    int firstFreeBlock = 0;
    for (DirEnt entry : directoryTable.values()) {
      int temp = entry.start;
      entry.start = firstFreeBlock;
      for (int i = temp; i < entry.length; i++) {
        newStorage[firstFreeBlock] = storage[i];
        firstFreeBlock++;
      }
    }
    storage = newStorage;
  }

}













\