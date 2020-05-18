/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Contiguous AllocationMethod Implementation
*/

import java.util.HashMap;

public class ContiguousAllocation implements AllocationMethod {

  private static final int BLOCK_COUNT = 16;

  // stores file ids, start blocks, and lengths
  HashMap<Integer, ContDirEnt> directoryTable;

  // fixed length array representing the secondary storage device
  int[] storage;

  // size of a block in bytes
  private int blockSize;

  public ContiguousAllocation(int blockSize) {
    directoryTable = new HashMap<>();
    storage = new int[BLOCK_COUNT];
    this.blockSize = blockSize;
  }

  // tries to create new file with given id and length in bytes
  // raises exception if not enough space
  @Override
  public void createFile(int id, int bytes) throws Exception {
    int start;
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if ((start = haveContiguousSpace(blocks)) == -1) { // no space
      performCompaction();
      if ((start = haveContiguousSpace(blocks)) == -1) { // still no space after compaction
        throw new Exception("Not enough space to allocate blocks: " + blockSize);
      }
    }
    allocate(id, start, blocks);
  }

  // returns the block containing the given byte in the file with id
  // raises exception if file with id doesn't exist, or offset is outside file bounds.
  @Override
  public int access(int id, int byteOffset) throws Exception {
    ContDirEnt entry = directoryTable.get(id);
    if (entry == null) {
      throw new Exception("No file with id: " + id);
    } else if (byteOffset >= (blockSize * entry.length)) {
      throw new Exception("Offset is outside file limits: " + byteOffset);
    } else {
      int blockOffset = (int) Math.floor((double) byteOffset / (double) blockSize);
      return entry.start + blockOffset;
    }
  }

  // allocates 'blocks' more blocks for the file with given id
  // if not enoguh space for extension, performs compaction and moves file to the end
  //   of the directory, leaving the required number of blocks free in the end.
  @Override
  public void extend(int id, int blocks) throws Exception {
    ContDirEnt entry = directoryTable.get(id);
    if (entry != null) {
      if (haveTotalSpace(blocks)) {
        System.out.println("Before shift:");
        displayStorage();
        for (int i = entry.getEndIndex() + 1; i < BLOCK_COUNT; i++) {
          shiftBack(i, entry.length);
          displayStorage();
          if (startOfFile(i)) {
            getEntryByStartIndex(i).start = i - entry.length;
          }
          entry.start++;
          if (canExtendFile(id, blocks)) {
            break;
          }
        }

        if (!canExtendFile(id, blocks)) {
          System.out.println("Performing compaction");
          performCompaction();
          displayStorage();
        }

        // extend
        for (int i = 1; i <= blocks; i++) {
          storage[entry.getEndIndex() + i] = entry.getEndIndex() + i;
        }
      } else {
        throw new Exception("Not enough space to allocate blocks: " + blocks);
      }
    } else {
      throw new Exception("No file with id: " + id);
    }
  }

  // de-allocates last 'blocks' blocks of file with given id
  // raises exception if file with id doesn't exist, or shrinking deletes the file
  @Override
  public void shrink(int id, int blocks) throws Exception {
    ContDirEnt entry = directoryTable.get(id);
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
  private int haveContiguousSpace(int blocks) {
    int freeSpace = 0;
    for (int i = 0; i < BLOCK_COUNT; i++) {
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

  // returns true if there is enough total space available to allocate given amount of blocks
  // space doesn't have to be contiguous
  private boolean haveTotalSpace(int blocks) {
    int freeSpace = 0;
    for (int i = 0; i < BLOCK_COUNT; i++) {
      if (storage[i] == 0) {
        freeSpace++;
        if (freeSpace == blocks) {
          return true;
        }
      }
    }
    return false;
  }

  // returns true if there is enough contiguous space for extension of size length
  // after the start block
  private boolean canExtendFile(int id, int length) {
    ContDirEnt entry = directoryTable.get(id);
    for (int i = 0; i < length; i++) {
      try {
        if (storage[entry.getEndIndex() + i + 1] != 0) {
          return false;
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        return false;
      }
    }
    return true;
  }

  // allocates space in storage for a given block size and adds it to the DT
  // each block contains (its index value + 1) if it is used
  // this can cause unexpected behavior if it is called before checking with haveSpace
  private void allocate(int id, int start, int length) {
    ContDirEnt entry = new ContDirEnt(start, length);
    directoryTable.put(id, entry);
    for (int i = 0; i < length; i++) {
      int index = start + i;
      storage[index] = index + 1;
    }
  }

  // deallocates given number of blocks by assigning 0 to them
  // starts from the end and goes backwards
  private void deallocate(int start, int length) {
    for (int i = 0; i < length; i++) {
      storage[start - i] = 0;
    }
  }

  // iterates over the storage array from start to end
  // moves each element back until no more space to go
  // this is not adaptive since each element either stays in place or moves to a smaller index
  private void performCompaction() {
    for (int i = 0; i < BLOCK_COUNT; i++) {
      if (storage[i] > 0) {
        int j = i - 1;
        int k = i;
        while (j >= 0 && storage[j] == 0) {
          swap(j, k);
          displayStorage();
          j--;
          k--;
        }

        if (startOfFile(i)) {
          ContDirEnt entry = getEntryByStartIndex(i);
          entry.start = j + 1;
        }
      }
    }
    System.out.println("Compaction ended");
  }

  // shifts the contents of a file back `count` times
  private void shiftBack(int index, int count) {
    for (int i = 0; i < count; i++) {
      swap(index, index - 1);
      index--;
    }
  }

  // returns true if given index is a start of a file
  private boolean startOfFile(int index) {
    for (ContDirEnt entry : directoryTable.values()) {
      if (entry.start == index) {
        return true;
      }
    }
    return false;
  }

  // moves a file to the end of the directory, leaving given number of blocks free in the end
  private void moveFileToEnd(int id, int blocks) {
    ContDirEnt entry = directoryTable.get(id);
    int oldStart = entry.start;
    int newStart = BLOCK_COUNT - 1 - blocks;
    for (int i = 0; i < entry.length; i++) {
      storage[newStart + i] = storage[oldStart + i];
      storage[oldStart + i] = 0;
    }
    entry.start = newStart;
  }

  // swaps the values in two indices
  private void swap(int i, int j) {
    int temp = storage[i];
    storage[i] = storage[j];
    storage[j] = temp;
  }

  // returns the entry corresponding to the file startign at the given index
  private ContDirEnt getEntryByStartIndex(int index) {
    for (ContDirEnt entry : directoryTable.values()) {
      if (entry.start == index) {
        return entry;
      }
    }
    return null;
  }

  // for debugging
  public void displayStorage() {
    for (int i = 0; i < BLOCK_COUNT; i++) {
      System.out.print(String.format("%d\t", storage[i]));
    }
    System.out.println();
  }

}



