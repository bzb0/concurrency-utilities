package com.bzb.concurrent.countdownlatch;

/**
 * A simple implementation of a count down latch. This implementation has similar semantics to {@link java.util.concurrent.CountDownLatch}.
 */
public class SimpleCountDownLatch {

  // The counter
  private int counter;

  // The lock object
  private final Object lock = new Object();

  /**
   * Creates a new count down latch, with the specified counter.
   *
   * @param counter The counter value.
   */
  public SimpleCountDownLatch(int counter) {
    if (counter <= 0) {
      throw new IllegalArgumentException("Counter must be a positive integer.");
    }
    this.counter = counter;
  }

  /**
   * Decreases the latch counter by one and notifies all waiting threads (threads that have called {@link SimpleCountDownLatch#await()}).
   */
  public void countDown() {
    synchronized (lock) {
      counter--;
      lock.notifyAll();
    }
  }

  /**
   * Causes the current thread to wait until the latch counter hits zero.
   *
   * @throws InterruptedException if the current thread is interrupted while waiting
   */
  public void await() throws InterruptedException {
    while (counter > 0) {
      synchronized (lock) {
        lock.wait();
      }
    }
  }
}
