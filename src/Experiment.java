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
  private HashMap<String, Long> opTimes;

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
      opTimes.put(op, 0l);
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
    time = System.currentTimeMillis();
  }

  public void endOperation(String opName) {
    time = System.currentTimeMillis() - time;
    opCounts.put(opName, opCounts.get(opName) + 1);
    opTimes.put(opName, opTimes.get(opName) + time);
    time = 0;
  }

  public void displayResults() {
    System.out.println("\tTotal completed operation counts: ");
    System.out.println("\t\tCreate: " + opCounts.get("CREATE"));
    System.out.println("\t\tExtend: " + opCounts.get("EXTEND"));
    System.out.println("\t\tAccess: " + opCounts.get("ACCESS"));
    System.out.println("\t\tShrink: " + opCounts.get("SHRINK"));
    System.out.println("\tAverage operation times (ms): ");
    System.out.println("\t\tCreate: " + getAverageOperationTime("CREATE"));
    System.out.println("\t\tExtend: " + getAverageOperationTime("EXTEND"));
    System.out.println("\t\tAccess: " + getAverageOperationTime("ACCESS"));
    System.out.println("\t\tShrink: " + getAverageOperationTime("SHRINK"));
    System.out.println("\t\tTOTAL:  " + getTotalAverageOperationTime());
    System.out.println("\tCreations rejected:\t" + createsRejected);
    System.out.println("\tExtensions rejected: " + extendsRejected);
    System.out.println();
  }

  private double getAverageOperationTime(String operation) {
    return (double) opTimes.get(operation) / (double) opCounts.get(operation);
  }

  private double getTotalAverageOperationTime() {
    double sum = 0;
    double count = 0;
    for (long time : opTimes.values()) {
      sum += time;
    }
    for (int c : opCounts.values()) {
      count += c;
    }
    return sum / count;
  }


}
