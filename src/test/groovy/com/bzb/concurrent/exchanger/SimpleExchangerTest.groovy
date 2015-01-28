package com.bzb.concurrent.exchanger

import spock.lang.Specification

class SimpleExchangerTest extends Specification {

  def "exchange with two different threads exchanges thread data"() {
    given:
    def exchanger = new SimpleExchanger<String>()
    def worker1 = new StringExchanger("value1", exchanger)
    def worker2 = new StringExchanger("value2", exchanger)
    def thread1 = new Thread(worker1)
    def thread2 = new Thread(worker2)

    when:
    thread1.start()
    thread2.start()

    thread1.join()
    thread2.join()

    then:
    worker1.getValue() == "value2"
    worker2.getValue() == "value1"
  }

  private static class StringExchanger implements Runnable {

    private String value;
    private SimpleExchanger<String> exchanger

    StringExchanger(String value, SimpleExchanger<String> exchanger) {
      this.value = value
      this.exchanger = exchanger
    }

    @Override
    void run() {
      try {
        value = exchanger.exchange(value)
      } catch (InterruptedException e) {
        e.printStackTrace()
      }
    }

    String getValue() {
      return value
    }
  }
}
