package dev.yavuztas.javafast.search

import spock.lang.Specification

class BranchlessQuadSearchSpec extends Specification {

    static int binarySearch(int[] array, int target) {
        int found = Arrays.binarySearch(array as int[], target)
        return found < 0 ? -1 : found
    }

    def "search should return the index when found, otherwise return -1"() {
        expect:
        BranchlessQuadSearch.search(array as int[], target) == binarySearch(array as int[], target)

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
