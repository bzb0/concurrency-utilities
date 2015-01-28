package com.bzb.concurrent.semaphore

import spock.lang.Specification

class SimpleSemaphoreTest extends Specification {

  def "a permit acquisition on a semaphore with one permit enables a mutual-exclusive execution"() {
    given:
    def counter = 0
    def sut = new SimpleSemaphore(1)
    def incrementThread = new Thread({->
      try {
        sut.acquire()
        --counter
      } finally {
        sut.release()
      }
    })
    def decrementThread = new Thread({->
      try {
        sut.acquire()
        ++counter
      } finally {
        sut.release()
      }
    })

    when:
    incrementThread.start()
    decrementThread.start()

    incrementThread.join()
    decrementThread.join()

    then:
    counter == 0
  }
}
