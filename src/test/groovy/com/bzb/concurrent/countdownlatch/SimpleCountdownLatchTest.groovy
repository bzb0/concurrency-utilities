package com.bzb.concurrent.countdownlatch

import spock.lang.Specification

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collectors
import java.util.stream.IntStream

class SimpleCountdownLatchTest extends Specification {

  def "countDown to zero signals all waiting threads to continue their execution"() {
    given:
    def threadCount = 10
    def failCounter = new AtomicInteger(0)
    def flag = new AtomicBoolean(false)
    def sut = new SimpleCountDownLatch(1)

    def workerThreads = IntStream.range(1, threadCount + 1).mapToObj({i ->
      new Thread({->
        sut.await()
        if (!flag.compareAndSet(false, true)) {
          failCounter.incrementAndGet()
        }
      })
    }).collect(Collectors.toList())

    when:
    workerThreads.each {
      thread -> thread.start()
    }
    sut.countDown()
    workerThreads.each {
      thread -> thread.join()
    }

    then:
    flag.get()
    failCounter.get() == threadCount - 1
  }
}
