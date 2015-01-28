package com.bzb.concurrent.exchanger;

/**
 * A simple implementation of a inter-thread data exchanger. The semantics of this class are similar to {@link java.util.concurrent.Exchanger}.
 *
 * @param <V> The type of the exchanged data.
 */
public class SimpleExchanger<V> {

  // The accumulator variable used for making the exchange
  private V accumulator;

  // The lock object
  private final Object lock = new Object();

  /**
   * Exchanges data between two threads. The first thread sets the value in an accumulator object and waits until the second thread provides the data
   * to be exchanged. The second threads signals the first thread, that the exchange has be done and the data can be fetched.. *
   *
   * @param data The data to be exchanged.
   * @return The exchanged value from the other thread.
   * @throws InterruptedException if the waiting thread is interrupted
   */
  public V exchange(V data) throws InterruptedException {
    synchronized (lock) {
      if (accumulator == null) {
        accumulator = data;
        lock.wait();
      } else {
        V exchangeValue = accumulator;
        accumulator = data;
        lock.notifyAll();
        return exchangeValue;
      }
      return accumulator;
    }
  }
}
