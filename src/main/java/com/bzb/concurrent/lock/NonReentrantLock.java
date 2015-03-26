package com.bzb.concurrent.lock;

/**
 * A simple implementation of a non-reentrant lock. A thread holding the lock can't acquire it again.
 */
public class NonReentrantLock {

  private boolean locked;
  private long ownerId = -1;

  /**
   * Acquires the lock if the lock is not held by this or another thread. If the lock is held by another thread, the current thread will busy-wait
   * until the lock is released. An {@link IllegalMonitorStateException} is thrown if the current thread already holds the locks.
   *
   * @throws InterruptedException if the thread, which waits to acquire the lock is interrupted
   * @throws IllegalMonitorStateException if the current thread already is the owner of the this lock
   */
  public synchronized void lock() throws InterruptedException {
    if (ownerId == Thread.currentThread().getId()) {
      throw new IllegalMonitorStateException("The lock was already acquired by the current thread.");
    }

    // Waiting until the lock is released
    while (locked) {
      this.wait();
    }
    locked = true;
    ownerId = Thread.currentThread().getId();
  }

  /**
   * Releases the lock if the current thread is the owner of this lock. An {@link IllegalMonitorStateException} exception will be thrown, if the
   * current thread is not the owner of the lock.
   *
   * @throws IllegalMonitorStateException if the current thread is not owner of this lock
   */
  public synchronized void unlock() {
    if (!locked || ownerId != Thread.currentThread().getId()) {
      throw new IllegalMonitorStateException("The lock can only be released by the owning thread. "
          + "Current thread id: " + Thread.currentThread().getId());
    } else {
      ownerId = -1;
      locked = false;
      this.notifyAll();
    }
  }
}