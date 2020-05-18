/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Linked Allocation Method Implementation
*/

import java.util.HashMap;

public class LinkedAllocation implements AllocationMethod {

  private static final int BLOCK_COUNT = 32768;
  private int blockSize;

  // fixed length array for the secondary storage device
  LinkedBlock[] storage;

  // fat keeps file ids and their start index
  HashMap<Integer, Integer> fat;

  public LinkedAllocation(int blockSize) {
    this.blockSize = blockSize;
    storage = new LinkedBlock[BLOCK_COUNT];
    int fatBlocks = (int) Math.ceil((double) (4 * BLOCK_COUNT) / (double) blockSize);
    for (int i = 0; i < fatBlocks; i++) {
      // blocks allocated to the FAT contain -1 values for content and as pointers
      storage[i] = new LinkedBlock(-1, -1);
    }
    fat = new HashMap<>();
  }

  // allocates space for a file starting from the end
  // the last block allocated is the file's start block
  @Override
  public void createFile(int id, int bytes) throws Exception {
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if (haveSpace(blocks)) {
      int freeIndex = -1;
      int last = -1;
      for (int i = 0; i < blocks; i++) {
        freeIndex = getNextFreeIndex();
        storage[freeIndex] = new LinkedBlock(freeIndex, last);
        last = freeIndex;
      }
      fat.put(id, freeIndex);
    } else {
      throw new Exception("Not enough space to allocate blocks: " + blocks);
    }
  }

  // returns the index of the block containing the byte with the given offset
  // raises exception if file with given id doesn't exist
  @Override
  public int access(int id, int byteOffset) throws Exception {
    Integer block = fat.get(id);
    if (block != null) {
      int blockOffset = (int) Math.floor((double) byteOffset / (double) blockSize);
      for (int i = 0; i < blockOffset; i++) {
        block = storage[block].next;
      }
      return block;
    } else {
      throw new Exception("No file with id: " + id);
    }
  }

  // TODO: refactor to `allocate` method
  @Override
  public void extend(int id, int blocks) throws Exception {
    Integer start = fat.get(id);
    if (start != null) {
      if (haveSpace(blocks)) {
        int endIndex = getFileEndIndex(start);
        int last = -1;
        int freeIndex = -1;
        for (int i = 0; i < blocks; i++) {
          freeIndex = getNextFreeIndex();
          storage[freeIndex] = new LinkedBlock(freeIndex, last);
          last = freeIndex;
        }
        storage[endIndex].next = last;
      } else {
        throw new Exception("No space to allocate blocks: " + blocks);
      }
    } else {
      throw new Exception("File with id doesn't exist: " + id);
    }
  }

  @Override
  public void shrink(int id, int blocks) throws Exception {
    Integer start = fat.get(id);
    if (start != null) {
      int endIndex = getFileEndIndex(start);
      for (int i = 0; i < blocks; i++) {
        storage[endIndex - i].content = 0;
        storage[endIndex - i].next = -1;
      }
      storage[endIndex - blocks].next = -1; // new end block of file points to -1
    } else {
      throw new Exception("File with id doesn't exist: " + id);
    }
  }

  // returns true if there is enough space to allocate given number of blocks, false otherwise
  private boolean haveSpace(int blocks) {
    int freeSpace = 0;
    for (LinkedBlock block : storage) {
      if (block.content == 0) {
        freeSpace++;
      }
    }
    return freeSpace >= blocks;
  }

  // returns the index of the first free block
  private int getNextFreeIndex() {
    for (int i = 0; i < BLOCK_COUNT; i++) {
      if (storage[i].content == 0) {
        return i;
      }
    }
    return -1;
  }

  private int getFileEndIndex(int start) {
    int block = start;
    while (storage[block].next != -1) {
      block = storage[block].next;
    }
    return block;
  }


}
