/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Interface for allocation methods
*/

import java.io.FileNotFoundException;

public interface AllocationMethod {
  void createFile(int id, int bytes) throws NotEnoughSpaceException;
  int access(int id, int byteOffset) throws FileNotFoundException;
  void extend(int id, int blocks) throws NotEnoughSpaceException, FileNotFoundException;
  void shrink(int id, int blocks) throws Exception;
}