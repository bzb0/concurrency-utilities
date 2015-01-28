package com.bzb.concurrent.semaphore;

import java.util.concurrent.Semaphore;

/**
 * A simple implementation of a counting semaphore. This class has the same semantics as {@link Semaphore}.
 */
public class SimpleSemaphore {

  // The semaphore counter.
  private int counter = 0;

  // The lock object
  private final Object lock = new Object();

  public SimpleSemaphore(int permits) {
    this.counter = permits;
  }

  /**
   * Tries to acquire a permit from the semaphore. If a permit is available the method returns <code>true</code> and decrements the number of
   * available permits. If no permit is available the method returns <code>false</code>.
   *
   * @return <code>true</code> if a permit was acquired, <code>false</code> otherwise
   */
  public boolean tryAcquire() {
    synchronized (lock) {
      if (counter > 0) {
        counter--;
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Acquires a permit from this semaphore. This method blocks until a permit is available.
   *
   * @throws InterruptedException if the current thread is interrupted
   */
  public void acquire() throws InterruptedException {
    synchronized (lock) {
      if (counter > 0) {
        counter--;
      } else {
        lock.wait();
      }
    }
  }

  /**
   * Releases an acquired permit and returns it to the semaphore.
   */
  public void release() {
    synchronized (lock) {
      counter++;
      lock.notifyAll();
    }
  }
}
