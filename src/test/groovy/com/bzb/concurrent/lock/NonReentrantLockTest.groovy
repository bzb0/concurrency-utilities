package com.bzb.concurrent.lock

import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.IntStream

class NonReentrantLockTest extends Specification {

  def "multiple lock from the same thread throws IllegalMonitorStateException"() {
    given:
    Throwable thrownException
    def sut = new NonReentrantLock()
    def thread = new Thread({->
      try {
        sut.lock()
        sut.lock()
      } catch (IllegalMonitorStateException e) {
        thrownException = e
      }
    })

    when:
    thread.start()
    thread.join()

    then:
    thrownException.message == "The lock was already acquired by the current thread."
  }

  def "parallel counter increment by ten threads, which is guarded by NonReentrantLock finishes with correct final count"() {
    given:
    def sut = new NonReentrantLock()
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
