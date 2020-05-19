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
      LinkedAllocation la = new LinkedAllocation(100);

      la.createFile(0, 350);
      la.displayStorage();
      la.displayFat();

      la.extend(0, 3);
      la.displayStorage();


    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}