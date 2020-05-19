/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Linked Allocation Method Implementation
*/

import java.util.Collections;
import java.util.HashMap;

// TODO: check for assumptions
// TODO: test
// TODO: see if fat is implemented the right way
// TODO: comment
public class LinkedAllocation implements AllocationMethod {

  private static final int BLOCK_COUNT = 16;
  private int blockSize;

  // fixed length array for the secondary storage device
  LinkedBlock[] storage;

  // fat keeps file ids and their start index
  HashMap<Integer, Integer> fat;

  public LinkedAllocation(int blockSize) {
    this.blockSize = blockSize;
    storage = new LinkedBlock[BLOCK_COUNT];
    for (int i = 0; i < BLOCK_COUNT; i++) {
      storage[i] = new LinkedBlock(0, -1);
    }
    int fatBlocks = (int) Math.ceil((double) (4 * BLOCK_COUNT) / (double) blockSize);
    for (int i = 0; i < fatBlocks; i++) {
      // blocks allocated to the FAT contain -1 values for content and as pointers
      storage[i].content = -1;
      storage[i].next = -1;
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
        int endIndex = getFileEndIndex(id);
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

  // deallocates a given number of blocks from the file's end
  // deallocate: change content to 0, pointer to -1
  @Override
  public void shrink(int id, int blocks) throws Exception {
    Integer start = fat.get(id);
    if (start != null) {
      for (int i = 0; i < blocks; i++) {
        deallocateFileEnd(id);
      }
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

  // returns the end index of a file by following its linked blocks until
  //    a block that points to -1
  private int getFileEndIndex(int id) {
    int block = fat.get(id);
    while (storage[block].next != -1) {
      block = storage[block].next;
    }
    return block;
  }

  private void deallocateFileEnd(int id) {
    int start = fat.get(id);
    int end = getFileEndIndex(id);
    int newEnd = start; // index of the block that points to the end of file
    while (storage[newEnd].next != end) {
      newEnd = storage[newEnd].next;
    }
    storage[end].next = -1;
    storage[end].content = 0;
    storage[newEnd].next = -1;
  }

  public void displayStorage() {
    for (LinkedBlock block : storage) {
      System.out.print(String.format("[%d:%d]\t", block.content, block.next));
    }
    System.out.println();
  }

  public void displayFat() {
    System.out.println(fat.toString());
  }

}
