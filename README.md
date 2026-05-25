# Java Quad Search – A Faster Alternative to Binary Search
An SIMD-powered search algorithm in Java that beats binary search for larger arrays.

Inspired by Lemire's blog post: https://lemire.me/blog/2026/04/27/you-can-beat-the-binary-search/

Thanks to Daniel Lemire for this interesting idea!

## Motivation
Binary search is theoretically optimal. Its runtime complexity is `O(log n)`. What it means by example: 
- Minimal comparisons, only 30 operations for 1 billion entries.

So, we can think of this, is there still any room for improvement? Probably less but still possible. Because:
- Modern CPUs are not comparison-bound anymore.
- Memory hierarchy and branch prediction dominate performance.

In this experiment, we take `Arrays.binarySearch` in Java as reference. We tried 2 alternative implementations:
1. `QuadSearch`: Quaternary narrowing -- predefined blocks are grouped into 4 regions until max. 3 blocks left and then continue with normal sequential search
2. `SIMDQuadSearch`: Quaternary narrowing -- the same process above -- and then SIMD powered sequential search 

In both implementations, we keep the `Arrays.binarySearch` semantics the same:
- If the search value is found, we return the index which could be `[0, n) when array size is n` 
- If the search value is no found, we return the insertion point as `(-insertion-point - 1)`

## TL;DR: Main Observation
A cache-friendly hybrid **quaternary search with SIMD instructions** can outperform `Arrays.binarySearch` by:
- up to **19%** on Oracle JDK, up to **17%** on GraalVM on Apple M4 ARM64 (128 bit Neon) hardware
- up to **X%** on Oracle JDK, up to **X%** on GraalVM on x86-64 AMD (256 bit AVX2) hardware

## Why Classic Binary Search is not Hardware-Friendly

### Binary Search Access Pattern
To understand better why Binary Search is not Hardware-Friendly, let's have a look at Binary search's access pattern. Binary search works fundemantally based on accessing mid-points which consequently leads to:
- random memory jumps
- poor cache locality
- difficult branch prediction

![](assets/binary-search-access-pattern.jpg)

As we notice, each access jumps to a distant location, so very little (or none) of the data stays in the cache.

Therefore, cache locality is almost never preserved.

### Theory vs Real Hardware
In theory, binary search is very optimal that makes fewer comparisons all the time. Considering an array of 1 billion entries, it's still only **30** operations (2^30 = 1,073,741,824 ~= 1B) to scan the entire array. 

However, fewer comparisons do not necessarily mean lower latency. Modern CPUs prefer:
- sequential memory access
- predictable control flow
- cache locality

over minimal comparison count.

## Why Small Linear Scans Are Surprisingly Fast

### Locality Wins
In modern CPUs scanning 16–32 contiguous integers is extremely cache-friendly. There are many advantages of contiguous traversal, like:
- sequential loads
- hardware prefetch efficiency
- minimal branch entropy
- high Out-of-Order utilization

Thus, sometimes a tiny linear scan can surprisingly outperform logarithmic search locally.

## The Hybrid Quaternary Search Design

### Core Idea
We split the search into two phases:
1. Quaternary narrowing
2. Local block scan (via SIMD)

Architecture:
```
Large array
↓
Quaternary narrowing
↓
1 to 3 small blocks remain
↓
Linear block scan with SIMD
```
### Quaternary Interpolation
// TODO

### Applying SIMD instructions
In Java by using Project Panama's Vector API, we can instruct the compiler to use SIMD registers and possibly get the benefit of instruction level parallelism. Thus, we can read and process more than 8 bytes, like 16 or 32 bytes depending on the underlying hardware.

Hence, the last remaining blocks (one to three, each has a size of 16) can be read by using Vector API:
```java
// TODO sample code
```
