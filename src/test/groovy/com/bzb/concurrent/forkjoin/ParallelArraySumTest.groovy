package com.bzb.concurrent.forkjoin

import spock.lang.Specification

import java.util.concurrent.ForkJoinPool

class ParallelArraySumTest extends Specification {

  def "the recursive fork-join task ParallelArraySum correctly partitions and sums a big array"() {
    given:
    long numElements = 1_000_000
    def forkJoinPool = new ForkJoinPool()
    long[] numbers = new long[numElements]
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = i + 1
    }
    def parallelSum = new ParallelArraySum(numbers)

    when:
    def result = forkJoinPool.invoke(parallelSum)
    forkJoinPool.shutdown()

    then:
    result == numElements * (numElements + 1) / 2
  }
}
