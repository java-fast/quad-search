package dev.yavuztas.javafast.search;

public final class QuadSearch {

    private static final int BLOCK_SIZE = 16;

    private QuadSearch() {
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

        int lo = 0;
        int hi = numBlocks - 1;

        // Quaternary search over block boundaries
        while ((hi - lo) > 2) {

            final int range = hi - lo;

            final int q1 = lo + (range >>> 2);
            final int q2 = lo + (range >>> 1);
            final int q3 = lo + ((range * 3) >>> 2);

            final int v1 = array[(q1 + 1) * BLOCK_SIZE - 1];
            final int v2 = array[(q2 + 1) * BLOCK_SIZE - 1];
            final int v3 = array[(q3 + 1) * BLOCK_SIZE - 1];

            if (target <= v1) {
                hi = q1;
            } else if (target <= v2) {
                lo = q1 + 1;
                hi = q2;
            } else if (target <= v3) {
                lo = q2 + 1;
                hi = q3;
            } else {
                lo = q3 + 1;
            }
        }

        // Final block checks
        for (int block = lo; block <= hi; block++) {
            final int blockEndValue = array[(block + 1) * BLOCK_SIZE - 1];
            // This block may contain target
            if (target <= blockEndValue) {
                final int blockStart = block * BLOCK_SIZE;
                return blockSearch(array, blockStart, target);
            }
        }

        // Remainder tail
        final int remainderStart = numBlocks * BLOCK_SIZE;
        return linearSearch(array, remainderStart, length, target);
    }

    /**
     * Search inside one 16-element block.
     * <p>
     * Returns:
     * found index
     * OR -(insertionPoint + 1)
     */
    private static int blockSearch(int[] array, int base, int target) {
        for (int i = 0; i < BLOCK_SIZE; i++) {
            final int index = base + i;
            final int value = array[index];
            if (value > target) {
                return -(index + 1);
            }
            if (value == target) {
                return index;
            }
        }
        return -((base + BLOCK_SIZE) + 1);
    }

    /**
     * Linear search with Arrays.binarySearch semantics.
     */
    private static int linearSearch(int[] array, int start, int end, int target) {
        for (int i = start; i < end; i++) {
            if (array[i] > target) {
                return -(i + 1);
            }
            if (array[i] == target) {
                return i;
            }
        }
        return -(end + 1);
    }

}
