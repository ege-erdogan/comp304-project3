/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Main class to run
*/

import java.util.Arrays;
import java.util.HashMap;

public class Main {
  public static void main(String[] args) {
    ContiguousAllocation cont = new ContiguousAllocation(100);

    try {
      cont.createFile(0, 400);
      cont.displayStorage();

      cont.createFile(1, 500);
      cont.displayStorage();

      cont.extend(0, 3);
      cont.displayStorage();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}