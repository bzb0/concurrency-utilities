package com.bzb.concurrent.cyclicbarrier

import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.IntStream

class SimpleCyclicBarrierTest extends Specification {

  def "SimpleCyclicBarrier constructor creates a cyclic barrier with given number of parties/threads"() {
    given:
    def numParties = 5

    when:
    def cyclicBarrier = new SimpleCyclicBarrier(5)

    then:
    cyclicBarrier.getNumberWaiting() == 0
    cyclicBarrier.getParties() == numParties
    !cyclicBarrier.isBroken()
  }

  def "a waiting interrupted thread brings the cyclic barrier in a broken state"() {
    given:
    def cyclicBarrier = new SimpleCyclicBarrier(2)
    def thread1 = new Thread({->
      try {
        cyclicBarrier.await()
      } catch (BrokenBarrierException e) {
        // nothing to do
      }
    })

    when:
    thread1.start()
    Thread.sleep(100)
    thread1.interrupt()
    thread1.join()

    then:
    cyclicBarrier.isBroken()
  }

  def "barrier action will not be executed until all parties have called await on the cyclic barrier"() {
    given:
    def numParties = 3
    def allPartiesArrived = false
    def cyclicBarrier = new SimpleCyclicBarrier(numParties, {->
      allPartiesArrived = true
    })
    def parties = IntStream.range(1, numParties + 1).mapToObj({i ->
      new Thread({->
        cyclicBarrier.await()
      })
    }).collect(Collectors.toList())

    when:
    parties.get(0).start()
    parties.get(1).start()

    then:
    !allPartiesArrived

    when:
    parties.get(2).start()
    parties.each {
      thread -> thread.join()
    }

    then:
    allPartiesArrived
  }
}
