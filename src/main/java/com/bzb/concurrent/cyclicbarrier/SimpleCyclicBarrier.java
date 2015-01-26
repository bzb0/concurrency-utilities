package com.bzb.concurrent.cyclicbarrier;

import java.util.concurrent.CyclicBarrier;

/**
 * A simple implementation of a cyclic barrier. The semantics of this class are similar to {@link CyclicBarrier}.
 */
public class SimpleCyclicBarrier {

  // The state of the barrier
  private boolean broken = false;

  // The number of parties that have tripped the barrier
  private int currParties;

  // The total number of parties/threads
  private final int totalParties;

  // The lock object
  private final Object lock = new Object();

  // The barrier action to be executed
  private Runnable barrierAction;

  /**
   * Creates a {@link SimpleCyclicBarrier} with the given number of parties.
   *
   * @param parties The number of parties/threads.
   */
  public SimpleCyclicBarrier(int parties) {
    this.totalParties = parties;
    this.currParties = parties;
  }

  /**
   * Creates a {@link SimpleCyclicBarrier} with the given number of parties and a barrier action. The barrier action, will be executed when all
   * parties have tripped the cyclic barrier.
   *
   * @param parties Number number of threads that must invoke {@link SimpleCyclicBarrier#await()} before the barrier is tripped.
   * @param barrierAction A {@link Runnable} that will be executed, when all threads tripped the barrier.
   */
  public SimpleCyclicBarrier(int parties, Runnable barrierAction) {
    this.totalParties = parties;
    this.currParties = parties;
    this.barrierAction = barrierAction;
  }

  /**
   * Waits until all parties have called await on the cyclic barrier.
   *
   * @throws InterruptedException if the current thread had its interrupted status set while entering this method
   * @throws BrokenBarrierException if the cyclic barrier was reset, in a broken state or one of the waiting threads was interrupted
   */
  public void await() throws InterruptedException, BrokenBarrierException {
    synchronized (lock) {
      // The barrier state is broken
      if (broken) {
        return;
      }

      // The current thread has been interrupted
      if (Thread.currentThread().isInterrupted()) {
        throw new InterruptedException("The current thread has been interrupted.");
      }

      if (--currParties == 0) {
        // Executing the cyclic barrier action, before allowing other waiting threads to continue
        if (barrierAction != null) {
          barrierAction.run();
        }
        lock.notifyAll();
      } else {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          // This thread was interrupted, so we set the cyclic barrier state to broken
          broken = true;
          // Notifying other threads that the cyclic barrier is broken
          lock.notifyAll();
          throw new BrokenBarrierException("The waiting party was interrupted.");
        }

        if (broken) {
          throw new BrokenBarrierException("The cyclic barrier is in a broken state (interrupted thread, failed action).");
        }
        // The cyclic barrier was reset
        if (currParties == totalParties) {
          throw new BrokenBarrierException("The cyclic barrier was reset.");
        }
      }
    }
  }

  /**
   * Returns <code>true</code> if this barrier is in a broken state.
   *
   * @return <code>true</code> if the barrier is in a broken state (a party was interrupted/timed out or a barrier action failed), <code>false</code>
   * otherwise
   */
  public boolean isBroken() {
    synchronized (lock) {
      return broken;
    }
  }

  /**
   * Resets the cyclic barrier.
   */
  public void reset() {
    synchronized (lock) {
      currParties = totalParties;
      lock.notifyAll();
    }
  }

  /**
   * Returns the number of waiting parties at the barrier.
   *
   * @return number of waiting parties
   */
  public int getNumberWaiting() {
    synchronized (lock) {
      return totalParties - currParties;
    }
  }

  /**
   * Returns the number of parties required to trip the barrier.
   *
   * @return number of parties required to trip the barrier
   */
  public int getParties() {
    return totalParties;
  }
}
