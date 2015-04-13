package com.bzb.concurrent.list;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A concurrent implementation of an array list. The concurrent access is managed with a {@link ReentrantReadWriteLock}.
 *
 * @param <T> The type of the elements in the list.
 */
public class ConcurrentArrayList<T> {

  private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
  private final Lock readLock = readWriteLock.readLock();
  private final Lock writeLock = readWriteLock.writeLock();

  private int currPos = 0;
  private Object[] data;

  /**
   * Creates a concurrent array list with the specified size.
   *
   * @param size The size of the list.
   */
  public ConcurrentArrayList(int size) {
    if (size <= 0) {
      throw new IllegalArgumentException("Size must be a positive integer.");
    }
    data = new Object[size];
  }

  /**
   * Adds a new element at the end of the list.
   *
   * @param e The element that will be added in the list.
   * @return <code>true</code> if the element was added in the list, <code>false</code> otherwise.
   */
  public boolean add(T e) {
    writeLock.lock();
    try {
      if (currPos + 1 > data.length) {
        data = Arrays.copyOf(data, data.length * 2);
      }
      data[currPos++] = e;
      return true;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Removes the element from the list at the specified index.
   *
   * @param index The index of the element to be removed.
   * @return The element that was removed from the list.
   * @throws IndexOutOfBoundsException thrown if the specified index is smaller than the current size of the list
   */
  public T remove(int index) {
    writeLock.lock();
    try {
      checkRange(index);
      T oldValue = (T) data[index];
      int numElements = currPos - index - 1;
      if (numElements > 0) {
        System.arraycopy(data, index + 1, data, index, numElements);
      }
      --currPos;
      return oldValue;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * Returns the element in the list at the specified index.
   *
   * @param index The index of the element.
   * @return The element at the specified index.
   * @throws IndexOutOfBoundsException thrown if the specified index is smaller than the current size of the list
   */
  public T get(int index) {
    readLock.lock();
    try {
      checkRange(index);
      return (T) data[index];
    } finally {
      readLock.unlock();
    }
  }

  /**
   * Returns the size of the list.
   *
   * @return The size of the list.
   */
  public int size() {
    readLock.lock();
    try {
      return currPos;
    } finally {
      readLock.unlock();
    }
  }

  private void checkRange(int index) {
    if (index >= currPos) {
      throw new IndexOutOfBoundsException(String.format("Index: %d, size: %d.", index, currPos));
    }
  }
}
  