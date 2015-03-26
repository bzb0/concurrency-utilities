package com.bzb.concurrent.lock;

import java.util.Optional;

/**
 * A simple implementation of a reentrant lock. This implementation has same semantics as {@link java.util.concurrent.locks.ReentrantLock}.
 */
public class SimpleReentrantLock {

  // Indicates if the lock is currently held by a thread
  private volatile boolean locked;

  // Number of holds the owner thread has on this lock
  private int lockCounter = 0;

  // The lock object
  private final Object lock = new Object();

  // The thread that has acquired this lock
  private Thread owner;

  /**
   * Acquires the lock if it's not held by another thread. The hold count will be set to one and method will return immediately with the value
   * <code>true</code>. If the lock is already held by another thread, that the method simply returns <code>false</code>.
   *
   * @return <code>true</code> if the lock was acquired, <code>false</code> otherwise.
   */
  public boolean tryLock() {
    synchronized (lock) {
      if (!locked) {
        locked = true;
        lockCounter++;
        owner = Thread.currentThread();
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * Acquires the lock and sets the hold count to one. If the lock is already held by another thread, the current thread will wait until the lock is
   * released by owning thread.
   */
  public void lock() {
    synchronized (lock) {
      if (!locked) {
        // The lock has not be acquired yet
        locked = true;
        lockCounter++;
        owner = Thread.currentThread();
        return;
      } else if (locked && owner.equals(Thread.currentThread())) {
        // The lock was already acquired the current thread, so we increase the hold count
        lockCounter++;
        return;
      } else {
        // The lock is currently held by another thread, so we busy-wait until it's released
        while (locked) {
          try {
            lock.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
        owner = Thread.currentThread();
        locked = true;
        lockCounter++;
        return;
      }
    }
  }

  /**
   * Attempts to release the lock. The current thread has to be the owner of this lock otherwise an {@link IllegalMonitorStateException} exception
   * will be thrown. The lock's hold count will be decreased and when it reaches zero, the lock will be released, so other threads can acquire it.
   *
   * @throws IllegalMonitorStateException if the current thread is not owner of this lock
   */
  public void unlock() {
    synchronized (lock) {
      if (Thread.currentThread().equals(owner)) {
        if (--lockCounter == 0) {
          locked = false;
          lock.notifyAll();
        }
      } else {
        throw new IllegalMonitorStateException("Reentrant lock not held by: " + Thread.currentThread().getName());
      }
    }
  }

  /**
   * Returns the number of holds on this lock by the owner thread.
   *
   * @return The number of holds on the lock by the owner thread.
   */
  public int getHoldCount() {
    synchronized (lock) {
      return lockCounter;
    }
  }

  /**
   * Returns <code>true</code> if the lock is held by the current executing thread.
   *
   * @return <code>true</code> if the lock is held by the current executing thread, <code>false</code> otherwise
   */
  public boolean isHeldByCurrentThread() {
    synchronized (lock) {
      return owner.equals(Thread.currentThread());
    }
  }

  /**
   * Returns <code>true</code> if the lock is already acquired by a thread, and <code>false</code> otherwise.
   *
   * @return <code>true</code> if the lock is already acquired by a thread, <code>false</code> otherwise.
   */
  public boolean isLocked() {
    synchronized (lock) {
      return locked;
    }
  }

  /**
   * Returns the owner thread of this lock wrapped in an {@link Optional}, or {@link Optional#empty()} if the lock doesn't have an owner.
   *
   * @return The owner thread of the lock.
   */
  public Optional<Thread> getOwner() {
    synchronized (lock) {
      return Optional.ofNullable(owner);
    }
  }
}
