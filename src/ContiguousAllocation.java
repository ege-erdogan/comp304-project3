/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Contiguous AllocationMethod Implementation
*/

import java.util.HashMap;

public class ContiguousAllocation implements AllocationMethod {

  // stores file ids, start blocks, and lengths
  HashMap<Integer, DirEnt> directoryTable;

  // fixed length array representing the secondary storage device
  int[] storage;

  // size of a block in bytes
  private int blockSize;

  public ContiguousAllocation(int blockSize) {
    directoryTable = new HashMap<>();
    storage = new int[32768];
    this.blockSize = blockSize;
  }

  // tries to create new file with given id and length in bytes
  // raises exception if not enough space
  @Override
  public void createFile(int id, int bytes) throws Exception {
    int start;
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if ((start = haveSpace(blocks)) == -1) {
      performCompaction();
      if ((start = haveSpace(blocks)) == -1) {
        throw new Exception("Not enough space to allocate blocks: " + blockSize);
      }
    }
    allocate(id, start, blocks);
  }

  // returns the block containing the given byte in the file with id
  // raises exception if file with id doesn't exist, or offset is outside file bounds.
  @Override
  public int access(int id, int byteOffset) throws Exception {
    DirEnt entry = directoryTable.get(id);
    if (entry == null) {
      throw new Exception("No file with id: " + id);
    } else if (byteOffset >= (blockSize * entry.length)) {
      throw new Exception("Offset is outside file limits: " + byteOffset);
    } else {
      int blockOffset = (int) Math.floor((double) byteOffset / (double) blockSize);
      return entry.start + blockOffset;
    }
  }

  @Override
  public void extend(int id, int blocks) throws Exception {
    DirEnt entry = directoryTable.get(id);
    if (entry == null) {
      throw new Exception("No file with id: " + id);
    } else {
      if (!haveExtensionSpace(entry.getEndIndex(), blocks)) {
        performCompaction();
        if (!haveExtensionSpace(entry.getEndIndex(), blocks)) {
          throw new Exception("Not enough space for extension of size: " + blocks);
        }
      }
      for (int i = entry.getEndIndex(); i < blocks; i++) {
        storage[i] = i;
      }
      entry.length += blocks;
    }
  }

  @Override
  public void shrink(int id, int blocks) throws Exception {
    DirEnt entry = directoryTable.get(id);
    if (entry == null) {
      throw new Exception("No file with id " + id);
    } else if (blocks >= entry.length) {
      throw new Exception("Shrink should leave at least one block in file.");
    } else {
      deallocate(entry.getEndIndex(), blocks);
      entry.length -= blocks;
    }
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

  // returns true if there is enough contiguous space for extension of size length
  // after the start block
  private boolean haveExtensionSpace(int start, int length) {
    for (int i = start; i < length; i++) {
      if (storage[i] != 0) {
        return false;
      }
    }
    return true;
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

  // deallocates given number of blocks by assigning 0 to them
  // starts from the end and goes backwards
  private void deallocate(int start, int length) {
    for (int i = 0; i < length; i++) {
      storage[start - i] = 0;
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



