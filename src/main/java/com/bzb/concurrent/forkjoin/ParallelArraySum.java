package com.bzb.concurrent.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * A {@link RecursiveTask} that calculates the sum of an array. The task splits the array in halves until it reaches a "sequential" threshold, when
 * the array can't be split anymore and the sum will be calculated.
 */
public class ParallelArraySum extends RecursiveTask<Long> {

  private static final int SEQUENTIAL_SUM_THRESHOLD = 5000;

  // The elements
  private final long[] array;
  // Lower index
  private final int low;
  // High index
  private final int high;

  public ParallelArraySum(long[] array) {
    this.array = array;
    this.low = 0;
    this.high = array.length;
  }

  private ParallelArraySum(long[] array, int low, int high) {
    this.array = array;
    this.low = low;
    this.high = high;
  }

  @Override
  protected Long compute() {
    // Computing a sequential sum
    if (high - low <= SEQUENTIAL_SUM_THRESHOLD) {
      long sum = 0;
      for (int i = low; i < high; ++i) {
        sum += array[i];
      }
      return sum;
    }

    // Splitting the array in two subtasks
    int mid = low + (high - low) / 2;
    ParallelArraySum leftSubtask = new ParallelArraySum(array, low, mid);
    ParallelArraySum rightSubtask = new ParallelArraySum(array, mid, high);

    // Executing the subtasks and merging the results
    leftSubtask.fork();
    rightSubtask.fork();
    return leftSubtask.join() + rightSubtask.join();
  }
}