package com.bzb.concurrent.cyclicbarrier;

/**
 * Thrown when a waiting thread is interrupted, the cyclic barrier has entered a broken state or the cyclic barrier was reset, while a thread was
 * waiting on the cyclic barrier.
 *
 * @see SimpleCyclicBarrier
 */
public class BrokenBarrierException extends RuntimeException {

  /**
   * Constructs a <tt>BrokenBarrierException</tt> with no specified detail message.
   */
  public BrokenBarrierException() {
  }

  /**
   * Constructs a <tt>BrokenBarrierException</tt> with the specified detail message.
   *
   * @param message the detail message
   */
  public BrokenBarrierException(String message) {
    super(message);
  }
}

