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

  @Override
  public int access(int id, int byteOffset) throws Exception {
    return 0;
  }

  @Override
  public void extend(int id, int blocks) throws Exception {

  }

  @Override
  public void shrink(int id, int blocks) throws Exception {

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


}
