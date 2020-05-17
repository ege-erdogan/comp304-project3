/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Main class to run
*/

import java.util.Arrays;

public class Main {
  public static void main(String[] args) {
    ContiguousAllocation cont = new ContiguousAllocation(100);
    try {
      cont.createFile(0, 250);
      cont.displayStorage();

      cont.createFile(1, 500);
      cont.displayStorage();

      cont.shrink(0, 1);
      cont.displayStorage();

      cont.createFile(2, 600);
      cont.displayStorage();

      cont.extend(1, 1);
      cont.displayStorage();

      cont.createFile(3, 270);
      cont.displayStorage();

      cont.createFile(4, 123);



    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}