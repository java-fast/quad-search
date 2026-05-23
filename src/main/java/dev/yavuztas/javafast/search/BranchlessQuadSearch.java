package dev.yavuztas.javafast.search;

/**
 * Experimental branchless Quaternary Interpolation narrowing.
 * Although the branchless search looks very sexy, it doesn't perform well enough in Apple M4,
 * actually even worse than default Arrays.binarySearch.
 */
public final class BranchlessQuadSearch {

    private static final int BLOCK_SIZE = 16;

    private BranchlessQuadSearch() {
    }

    /**
     * Returns index if found, otherwise (-insertion-point - 1)
     */
    public static int search(int[] array, int target) {
        final int length = array.length;

        if (length == 0) {
            return -1;
        }

        // Small arrays -> linear scan
        if (length < BLOCK_SIZE) {
            return linearSearch(array, 0, length, target);
        }

        final int numBlocks = length / BLOCK_SIZE;

        int base = 0;
        int n = numBlocks;
        while (n > 3) {
            final int quarter = n >>> 2;

            final int k1 = array[(base + quarter + 1) * BLOCK_SIZE - 1];
            final int k2 = array[(base + quarter * 2 + 1) * BLOCK_SIZE - 1];
            final int k3 = array[(base + quarter * 3 + 1) * BLOCK_SIZE - 1];

            // Hopefully, bool to int conversions are optimized as branchless in Hotspot C2 / Graal
            final int c1 = (k1 < target) ? 1 : 0;
            final int c2 = (k2 < target) ? 1 : 0;
            final int c3 = (k3 < target) ? 1 : 0;

            base += (c1 + c2 + c3) * quarter;

            n -= quarter * 3;
        }

        while (n > 1) {
            final int half = n >>> 1;
            base = (array[(base + half + 1) * BLOCK_SIZE - 1] < target)
                ? base + half
                : base;
            n -= half;
        }

        final int lo = (array[(base + 1) * BLOCK_SIZE - 1] < target)
            ? base + 1
            : base;

        // Final block may contain the target
        if (lo < numBlocks) {
            final int blockStart = lo * BLOCK_SIZE;
            return simdFriendlyBlockSearch(array, blockStart, target);
        }

        // Remainder tail
        final int remainderStart = numBlocks * BLOCK_SIZE;
        return linearSearch(array, remainderStart, length, target);
    }

    /**
     * Fixed-size 16-element scan.
     * <p>
     * Extremely JIT-friendly:
     * - Fixed trip count
     * - Contiguous loads
     * - Predictable access
     * - No loop branches
     * <p>
     * HotSpot can auto-unroll and sometimes auto-vectorize this.
     */
    private static int simdFriendlyBlockSearch(int[] array, int base, int target) {

        // Fully unrolled for maximum JIT optimization
        if (array[base] == target) return base;
        if (array[base + 1] == target) return base + 1;
        if (array[base + 2] == target) return base + 2;
        if (array[base + 3] == target) return base + 3;

        if (array[base + 4] == target) return base + 4;
        if (array[base + 5] == target) return base + 5;
        if (array[base + 6] == target) return base + 6;
        if (array[base + 7] == target) return base + 7;

        if (array[base + 8] == target) return base + 8;
        if (array[base + 9] == target) return base + 9;
        if (array[base + 10] == target) return base + 10;
        if (array[base + 11] == target) return base + 11;

        if (array[base + 12] == target) return base + 12;
        if (array[base + 13] == target) return base + 13;
        if (array[base + 14] == target) return base + 14;
        if (array[base + 15] == target) return base + 15;

        return -1;
    }

    private static int linearSearch(int[] array, int start, int end, int target) {
        for (int i = start; i < end; i++) {
            if (array[i] == target) {
                return i;
            }
            if (array[i] > target) {
                return -1;
            }
        }
        return -1;
    }

}
