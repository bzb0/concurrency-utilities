package com.bzb.concurrent.lock

import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.IntStream

class SimpleReentrantLockTest extends Specification {

  def "lock sets lock owner, hold counter and locked flag"() {
    given:
    def sut = new SimpleReentrantLock()
    def thread = new Thread({-> sut.lock()
    }, "Lock-Thread")

    when:
    thread.start()
    thread.join()

    then:
    sut.getHoldCount() == 1
    sut.getOwner().get() == thread
    sut.isLocked() == true
  }

  def "unlock from a thread that doesn't hold the lock throws IllegalMonitorStateException"() {
    given:
    IllegalMonitorStateException monitorException
    def sut = new SimpleReentrantLock()
    def lockThread = new Thread({-> sut.lock()
    }, "Lock-Thread")
    def unlockThread = new Thread({->
      try {
        sut.unlock()
      } catch (IllegalMonitorStateException e) {
        monitorException = e
      }
    }, "Unlock-Thread")

    when:
    lockThread.start()
    unlockThread.start()
    lockThread.join()
    unlockThread.join()

    then:
    monitorException != null
    monitorException.getMessage() == "Reentrant lock not held by: Unlock-Thread"
  }

  def "parallel counter increment by several threads, which is guarded by SimpleReentrantLock finishes with correct final count"() {
    given:
    def sut = new SimpleReentrantLock()
    def counter = 0
    def workerThreads = IntStream.range(1, 11).mapToObj({i ->
      new Thread({->
        for (int j = 0; j < 100; j++) {
          sut.lock();
          try {
            counter++
          } finally {
            sut.unlock();
          }
        }
      })
    }).collect(Collectors.toList())

    when:
    workerThreads.each {
      thread ->
        thread.start()
        thread.join()
    }

    then:
    counter == 1000
  }
}
