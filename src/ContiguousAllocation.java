/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Contiguous AllocationMethod Implementation
*/

import java.io.FileNotFoundException;
import java.util.HashMap;

public class ContiguousAllocation implements AllocationMethod {

  private static final int BLOCK_COUNT = 32768;

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
  public void createFile(int id, int bytes) throws NotEnoughSpaceException {
    int start;
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if ((start = haveContiguousSpace(blocks)) == -1) { // no space
      performCompaction();
      if ((start = haveContiguousSpace(blocks)) == -1) { // still no space after compaction
        throw new NotEnoughSpaceException("Not enough space to allocate blocks: " + blockSize);
      }
    }
    allocate(id, start, blocks);
  }

  // returns the block containing the given byte in the file with id
  // raises exception if file with id doesn't exist, or offset is outside file bounds.
  @Override
  public int access(int id, int byteOffset) throws FileNotFoundException {
    ContDirEnt entry = directoryTable.get(id);
    if (entry != null) {
      int blockOffset = (int) Math.floor((double) byteOffset / (double) blockSize);
      return entry.start + blockOffset;
    } else {
      throw new FileNotFoundException("No file with id: " + id);
    }
  }

  // allocates 'blocks' more blocks for the file with given id
  // if not enough space for extension, performs compaction and moves file to the end
  //   of the directory, leaving the required number of blocks free in the end.
  @Override
  public void extend(int id, int blocks) throws NotEnoughSpaceException, FileNotFoundException {
    ContDirEnt entry = directoryTable.get(id);
    if (entry != null) {
      if (haveTotalSpace(blocks)) {
        if (canExtendFile(id, blocks)) {
          performExtension(entry, blocks);
        } else {
          for (int i = entry.getEndIndex() + 1; i < BLOCK_COUNT; i++) {
            shiftBack(i, entry.length);
            if (isStartOfFile(i)) {
              getEntryByStartIndex(i).start = i - entry.length;
            }
            entry.start++;
            if (canExtendFile(id, blocks)) {
              performExtension(entry, blocks);
              return;
            }
          }

          // still can't extend file. see if compaction works.
          performCompaction();
          if (canExtendFile(id, blocks)) {
            performExtension(entry, blocks);
          } else {
            throw new NotEnoughSpaceException("Not enough space to allocate blocks: " + blocks);
          }
        }
      } else {
        throw new NotEnoughSpaceException("Not enough space to allocate blocks: " + blocks);
      }
    } else {
      throw new FileNotFoundException("No file with id: " + id);
    }
  }

  // de-allocates last 'blocks' blocks of file with given id
  // raises exception if file with id doesn't exist, or shrinking deletes the file
  @Override
  public void shrink(int id, int blocks) throws FileNotFoundException, CannotShrinkMoreException {
    ContDirEnt entry = directoryTable.get(id);
    if (entry != null) {
      if (blocks < entry.length) {
        deallocateBlocks(entry.getEndIndex(), blocks);
        entry.length -= blocks;
      } else {
        throw new CannotShrinkMoreException("Cannot shrink file more than its length.");
      }
    } else {
      throw new FileNotFoundException("No file with id: " + id);
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
  private void deallocateBlocks(int start, int length) {
    for (int i = 0; i < length; i++) {
      if (start < i) {
        return;
      }
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
          j--;
          k--;
        }

        if (isStartOfFile(i)) {
          ContDirEnt entry = getEntryByStartIndex(i);
          entry.start = j + 1;
        }
      }
    }
  }

  // shifts the contents of a block back `count` times
  private void shiftBack(int index, int count) {
    for (int i = 0; i < count; i++) {
      swap(index, index - 1);
      index--;
    }
  }

  // returns true if given index is the start of a file
  private boolean isStartOfFile(int index) {
    return directoryTable.values().contains(index);
  }

  // swaps the values in two blocks
  private void swap(int i, int j) {
    int temp = storage[i];
    storage[i] = storage[j];
    storage[j] = temp;
  }

  // returns the entry corresponding to the file starting at the given index
  private ContDirEnt getEntryByStartIndex(int index) {
    for (ContDirEnt entry : directoryTable.values()) {
      if (entry.start == index) {
        return entry;
      }
    }
    return null;
  }

  private void performExtension(ContDirEnt entry, int blocks) {
    for (int i = 1; i <= blocks; i++) {
      storage[entry.getEndIndex() + i] = entry.getEndIndex() + i;
    }
    entry.length += blocks;
  }

}



