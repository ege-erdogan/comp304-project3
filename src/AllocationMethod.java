/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Interface for allocation methods
*/

public interface AllocationMethod {
  void createFile(int id, int bytes) throws Exception;
  int access(int id, int byteOffset) throws Exception;
  void extend(int id, int blocks) throws Exception;
  void shrink(int id, int blocks);
}