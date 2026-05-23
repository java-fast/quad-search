package dev.yavuztas.javafast.search

import spock.lang.Specification

class SimdQuadSearchSpec extends Specification {

    def "search should return same result as Arrays.binarySearch for various inputs"() {
        expect:
        SimdQuadSearch.search(array as int[], target) == Arrays.binarySearch(array as int[], target)

        where:
        array                               | target
        []                                  | 5
        [1]                                 | 1
        [1]                                 | 0
        [1]                                 | 2
        [1, 3, 5]                           | 3
        [1, 3, 5]                           | 0
        [1, 3, 5]                           | 2
        [1, 3, 5]                           | 4
        [1, 3, 5]                           | 6
        (0..15).toArray()                   | 7
        (0..15).toArray()                   | -1
        (0..15).toArray()                   | 16
        (0..100).step(2).toArray()          | 50
        (0..100).step(2).toArray()          | 51
        (0..100).step(2).toArray()          | -1
        (0..100).step(2).toArray()          | 101
    }

}
