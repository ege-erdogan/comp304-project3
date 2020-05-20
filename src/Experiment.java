/*
  Ege Erdogan 64004 - COMP 304 Project 3
  Experiment class to encapsulate away the bookkeeping logic from the main program for readability
*/

import java.util.HashMap;

public class Experiment {
  private final String[] ops = {"CREATE", "EXTEND", "SHRINK", "ACCESS"};

  private int createsRejected;
  private int extendsRejected;

  // keeps track of how many times an operation was invoked
  private HashMap<String, Integer> opCounts;

  // keeps track of how much time has been elapsed for an operation in total
  private HashMap<String, Integer> opTimes;

  // keep track of the time elapsed for the ongoing operation
  // this assumes that there are no concurrent operations
  private long time;

  public Experiment() {
    createsRejected = 0;
    extendsRejected = 0;
    opCounts = new HashMap<>();
    opTimes = new HashMap<>();
    for (String op : ops) {
      opCounts.put(op, 0);
      opTimes.put(op, 0);
    }
  }

  public void createRejected() {
    createsRejected++;
    time = 0;
  }

  public void extendRejected() {
    extendsRejected++;
    time = 0;
  }

  public void startOperation(String opName) {
    opCounts.put(opName, opCounts.get(opName) + 1);
    time = System.currentTimeMillis();
  }

  public void endOperation(String opName) {
    time = System.currentTimeMillis() - time;
    time = 0;
  }

  public void displayInfo() {
    System.out.println("Total operation counts: ");
    System.out.println("\tCreate: " + opCounts.get("CREATE"));
    System.out.println("\tExtend: " + opCounts.get("EXTEND"));
    System.out.println("\tAccess: " + opCounts.get("ACCESS"));
    System.out.println("\tShrink: " + opCounts.get("SHRINK"));
    System.out.println("Average operation times (ms): ");
    System.out.println("\tCreate: " + getOperationAverageTime("CREATE"));
    System.out.println("\tExtend: " + getOperationAverageTime("EXTEND"));
    System.out.println("\tAccess: " + getOperationAverageTime("ACCESS"));
    System.out.println("\tShrink: " + getOperationAverageTime("SHRINK"));
    System.out.println("Creations rejected:\t" + createsRejected);
    System.out.println("Extensions rejected:\t" + extendsRejected);
  }

  private int getOperationAverageTime(String operation) {
    return opTimes.get(operation) / opCounts.get(operation);
  }


}
