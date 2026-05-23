package dev.yavuztas.javafast.search;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@Fork(value = 3)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 10, time = 2)
@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class ArraySearchColdCacheBenchmark {

    static final int MAX_ARRAY_SIZE = 1 << 20; // aka 1m
    static final int ARRAY_SIZE = Integer.getInteger("benchmark.array.size", MAX_ARRAY_SIZE);
    // For Max Array Size (1m) => Array Count = 1024
    // For Min Array Size (4k) => Array Count = 256k
    static final int ARRAY_COUNT = 1024 * MAX_ARRAY_SIZE / ARRAY_SIZE;

    static final int[][] testArrays = new int[ARRAY_COUNT][ARRAY_SIZE];
    static final int[] queries = new int[ARRAY_SIZE];

    static int arrayIndex(int counter) {
        return counter & (ARRAY_COUNT - 1);
    }

    static int queryIndex(int counter) {
        return counter & (ARRAY_SIZE - 1);
    }

    int counter;

    @Setup(Level.Trial)
    public void setup() {
        System.out.printf("Running with: Array Count = %d, Array Size = %d%n", ARRAY_COUNT, ARRAY_SIZE);

        // generate random queries
        final Random rnd = ThreadLocalRandom.current();
        final int bound =  ARRAY_SIZE * 8; // possible max element due to rnd.nextInt(8)
        for (int i = 0; i < queries.length; i++) {
            queries[i] = rnd.nextInt(bound);
        }

        for (int i = 0; i < ARRAY_COUNT; i++) {
            final int[] array = new int[ARRAY_SIZE];
            int base = 0;
            for (int j = 0; j < ARRAY_SIZE; j++) {
                array[j] = (base += rnd.nextInt(8) + 1); // min: 1, max: 8
            }
            testArrays[i] = array;
        }
    }

    @Benchmark
    public int binarySearchDefault() {
        final int[] array = testArrays[arrayIndex(this.counter++)];
        return Arrays.binarySearch(array, queries[queryIndex(this.counter)]);
    }

    @Benchmark
    public int quadSearch() {
        final int[] array = testArrays[arrayIndex(this.counter++)];
        return QuadSearch.search(array, queries[queryIndex(this.counter)]);
    }

    @Benchmark
    public int simdQuadSearch() {
        final int[] array = testArrays[arrayIndex(this.counter++)];
        return SimdQuadSearch.search(array, queries[queryIndex(this.counter)]);
    }

}

