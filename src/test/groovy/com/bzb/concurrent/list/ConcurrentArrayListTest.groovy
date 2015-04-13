package com.bzb.concurrent.list

import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.IntStream

class ConcurrentArrayListTest extends Specification {

  def "multiple concurrent element additions and removals leave the list empty"() {
    given:
    def numThreads = 10
    def sut = new ConcurrentArrayList<Integer>(1000)
    def addThreads = IntStream.range(1, numThreads + 1).mapToObj({i ->
      new Thread({->
        for (int j = 0; j < 100; j++) {
          sut.add(new Random().nextInt())
        }
      })
    }).collect(Collectors.toList())
    def removeThreads = IntStream.range(1, numThreads + 1).mapToObj({i ->
      new Thread({->
        for (int j = 0; j < 100; j++) {
          while (true) {
            try {
              sut.remove(sut.size() - 1)
              break
            } catch (IndexOutOfBoundsException e) {
              Thread.sleep(5)
            }
          }
        }
      })
    }).collect(Collectors.toList())

    when:
    removeThreads.each {
      thread -> thread.start()
    }
    addThreads.each {
      thread -> thread.start()
    }

    addThreads.each {
      thread -> thread.join()
    }
    removeThreads.each {
      thread -> thread.join()
    }

    then:
    sut.size() == 0
  }
}
