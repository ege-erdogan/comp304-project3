/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Main class to run
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    System.out.println("--- Starting CONTIGUOUS allocation experiments ---");
    System.out.println();
    runExperiment("CONTIGUOUS");

    System.out.println("--- Starting LINKED allocation experiments ---");
    System.out.println();
    runExperiment("LINKED");
  }

  // runs the experiment by executing each file's contents 5 times using the given method
  // prints details for each file after five iterations
  private static void runExperiment(String method) {
    final File folder = new File("input/");
    for (File file : folder.listFiles()) {
      Experiment exp = new Experiment();

      int blockSize = Integer.parseInt(file.getName().split("_")[1]);
      int nextFileId = 0;

      AllocationMethod allocation;
      if (method.equals("CONTIGUOUS")) {
        allocation = new ContiguousAllocation(blockSize);
      } else {
        allocation = new LinkedAllocation(blockSize);
      }

      for (int i = 0; i < 5; i++) {
        nextFileId = 0;
        Scanner scanner = null;
        try {
          scanner = new Scanner(file);

          while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] tokens = line.split(":");

            switch (tokens[0]) {
              case "c":
                try {
                  int bytes = Integer.parseInt(tokens[1]);
                  exp.startOperation("CREATE");
                  allocation.createFile(nextFileId, bytes);
                  exp.endOperation("CREATE");
                  nextFileId++;
                } catch (NotEnoughSpaceException e) {
                  exp.createRejected();
                  nextFileId++;
                }
              case "a":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int offset = Integer.parseInt(tokens[2]);
                  exp.startOperation("ACCESS");
                  allocation.access(id, offset);
                  exp.endOperation("ACCESS");
                } catch (Exception e) {

                }
              case "e":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int blocks = Integer.parseInt(tokens[2]);
                  exp.startOperation("EXTEND");
                  allocation.extend(id, blocks);
                  exp.endOperation("EXTEND");
                } catch (NotEnoughSpaceException e) {
                  exp.extendRejected();
                }
              case "sh":
                try {
                  int id = Integer.parseInt(tokens[1]);
                  int blocks = Integer.parseInt(tokens[2]);
                  exp.startOperation("SHRINK");
                  allocation.shrink(id, blocks);
                  exp.endOperation("SHRINK");
                } catch (Exception e) {

                }
            }
          }
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }

      System.out.println("Experiment results from file: " + file.getName());
      exp.displayInfo();
    }
  }


}