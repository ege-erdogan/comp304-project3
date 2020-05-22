/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Linked Allocation Method Implementation
*/

import java.io.FileNotFoundException;
import java.util.HashMap;

public class LinkedAllocation implements AllocationMethod {

  private static final int BLOCK_COUNT = 32768;
  private int blockSize;

  // fixed length array for the secondary storage device
  int[] storage;

  // contains the directory entries pointing to the start block of each file in the fat
  // <file id, start block>
  HashMap<Integer, Integer> directoryEntries;

  // fat keeps file ids and their start index
  HashMap<Integer, Integer> fat;

  public LinkedAllocation(int blockSize) {
    this.blockSize = blockSize;
    directoryEntries = new HashMap<>();
    fat = new HashMap<>();
    int fatBlocks = (int) Math.ceil((double) (4 * BLOCK_COUNT) / (double) blockSize);
    storage = new int[BLOCK_COUNT];
    for (int i = 0; i < fatBlocks; i++) {
      // blocks allocated to the FAT contain -1 for values
      storage[i] = -1;
    }
  }

  // allocates space for a file starting from the end
  // the last block allocated is the file's start block
  @Override
  public void createFile(int id, int bytes) throws NotEnoughSpaceException {
    int blocks = (int) Math.ceil((double) bytes / (double) blockSize);
    if (haveSpace(blocks)) {
      // allocate space for file
      int start = getNextFreeIndex();
      directoryEntries.put(id, start);
      int last = start;
      storage[start] = start;
      for (int i = 1; i < blocks; i++) {
        int nextIndex = getNextFreeIndex();
        fat.put(last, nextIndex);
        storage[nextIndex] = nextIndex;
        last = nextIndex;
      }
    } else {
      throw new NotEnoughSpaceException("Not enough space to allocte blocks: " + blocks);
    }
  }

  // allocates more blocks to the file with the given id
  // throws exception if file with id doesn't exist
  @Override
  public void extend(int id, int blocks) throws NotEnoughSpaceException, FileNotFoundException {
    Integer start = directoryEntries.get(id);
    if (start != null) {
      if (haveSpace(blocks)) {
        // perform extension
        int end = getFileEndIndex(id);
        for (int i = 0; i < blocks; i++) {
          int nextFree = getNextFreeIndex();
          fat.put(end, nextFree);
          storage[nextFree] = nextFree;
          end = nextFree;
        }
      } else {
        throw new NotEnoughSpaceException("No space to allocate blocks: " + blocks);
      }
    } else {
      throw new FileNotFoundException("File with id doesn't exist: " + id);
    }
  }

  // returns the index of the block containing the byte with the given offset
  // raises exception if file with given id doesn't exist
  @Override
  public int access(int id, int byteOffset) throws FileNotFoundException {
    Integer block = directoryEntries.get(id);
    if (block != null) {
      int blockOffset = (int) Math.floor((double) byteOffset / (double) blockSize);
      for (int i = 0; i < blockOffset; i++) {
        block = fat.get(block);
      }
      return block;
    } else {
      throw new FileNotFoundException("No file with id: " + id);
    }
  }

  // deallocates a file's end block given number of times
  // deallocate: 1. change storage value to 0
  //             2. remove fat entry pointing to the end index
  @Override
  public void shrink(int id, int blocks) throws FileNotFoundException {
    Integer start = fat.get(id);
    if (start != null) {
      int end = getFileEndIndex(id);
      for (int i = 0; i < blocks; i++) {
        storage[end] = 0;
        for (int key : fat.keySet()) {
          if (fat.get(key) == end) {
            fat.remove(key);
          }
        }
      }
    } else {
      throw new FileNotFoundException("File with id doesn't exist: " + id);
    }
  }

  // returns true if there is enough space to allocate given number of blocks, false otherwise
  private boolean haveSpace(int blocks) {
    int freeSpace = 0;
    for (int elt : storage) {
      if (elt > 0) {
        freeSpace++;
      }
    }
    return freeSpace >= blocks;
  }

  // returns the index of the first free block
  private int getNextFreeIndex() {
    for (int i = 0; i < BLOCK_COUNT; i++) {
      if (storage[i] == 0) {
        return i;
      }
    }
    return -1;
  }

  // returns the index of the block at the end of the file with given id
  private int getFileEndIndex(int id) {
    int block = directoryEntries.get(id);
    Integer next = fat.get(block);
    while (true) {
      if (next == null) {
        return block;
      } else {
        block = next;
        next = fat.get(block);
      }
    }
  }

  // allocates given number of blocks and returns the index of the last allocated block
  private int allocateBlocksGetLast(int blocks) {
    int last = -1;
    int freeIndex = -1;
    for (int i = 0; i < blocks; i++) {
      freeIndex = getNextFreeIndex();
      storage[freeIndex] = new LinkedBlock(freeIndex, last);
      last = freeIndex;
    }
    return last;
  }

  // deallocates the last block of a file
  // changes the last block's content to 0 and pointer to -1
  // changes the new last block's pointer to -1, keeps content the same
  private void deallocateFileEnd(int id) {
    int end = getFileEndIndex(id);
    storage[end] = 0;
  }

  // for debugging
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
