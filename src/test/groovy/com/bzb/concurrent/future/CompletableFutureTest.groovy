package com.bzb.concurrent.future

import spock.lang.Specification

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class CompletableFutureTest extends Specification {

  def "consuming a value from an asynchronous supplier"() {
    given:
    def suppliedValue = 100
    def value = new AtomicInteger(-1)

    when:
    CompletableFuture.supplyAsync({-> suppliedValue
    }).thenAccept({val -> value.set(val)
    }).get()

    then:
    value.get() == suppliedValue
  }

  def "chaining sequential and asynchronous tasks"() {
    when:
    def result = CompletableFuture.supplyAsync({-> 5
    }).thenApply({i -> i * 3
    }).thenApplyAsync({result ->
      throw new IndexOutOfBoundsException(Integer.toString(result))
    }).exceptionally({throwable -> 150
    }).get()

    then:
    result == 150
  }

  def "combining results from chained completable futures"() {
    when:
    def result = CompletableFuture.supplyAsync({-> "Hello"
    }).thenCombine(CompletableFuture.supplyAsync({-> " World"
    }), {
      string1, string2 -> string1.concat(string2)
    }).get()

    then:
    result == "Hello World"
  }
}
