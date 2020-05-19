/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Main class to run
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    final File folder = new File(("input/"));
    String method = "CONTIGUOUS";

    for (int i = 0; i < 10; i++) {
      if (i == 5) {
        method = "LINKED";
      }

      for (File file : folder.listFiles()) {
        int blockSize = Integer.parseInt(file.getName().split("_")[1]);
        int nextFileId = 0;
        System.out.println("Starting " + method + " allocation experiment with block size: " + blockSize);

        AllocationMethod allocation;
        if (method.equals("CONTIGUOUS")) {
          allocation = new ContiguousAllocation(blockSize);
        } else {
          allocation = new LinkedAllocation(blockSize);
        }

        try {
          Scanner scanner = new Scanner(file);
          while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split(":");

            switch (tokens[0]) {
              case "c":
                try {
                  int bytes = Integer.parseInt(tokens[1]);
                  allocation.createFile(nextFileId, bytes);
                  nextFileId++;
                } catch (Exception e) {
//                  e.printStackTrace();
                }
              case "a":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int offset = Integer.parseInt(tokens[2]);
                  allocation.access(id, offset);
                } catch (Exception e) {
//                  e.printStackTrace();
                }
              case "e":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int blocks = Integer.parseInt(tokens[2]);
                  allocation.extend(id, blocks);
                } catch (Exception e) {
//                  e.printStackTrace();
                }
              case "sh":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int blocks = Integer.parseInt(tokens[2]);
                  allocation.shrink(id, blocks);
                } catch (Exception e) {
//                  e.printStackTrace();
                }
            }
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
    }


  }


}