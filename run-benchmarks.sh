#!/usr/bin/env bash

set -euo pipefail

if [[ -z "${JDK_VENDOR:-}" ]]; then
  echo "Error: JDK_VENDOR environment variable is not set."
  echo "Example:"
  echo "  JDK_VENDOR=oracle-openjdk"
  exit 1
fi

SIZES=(
  "4k:4096"
  "8k:8192"
  "16k:16384"
  "32k:32768"
  "64k:65536"
  "128k:131072"
  "256k:262144"
  "512k:524288"
  "1m:1048576"
)

SOURCE_FILE="$HOME/git/java-fast/quad-search/build/results/jmh/results.txt"

for ENTRY in "${SIZES[@]}"; do
  LABEL="${ENTRY%%:*}"
  SIZE="${ENTRY##*:}"

  echo "Running benchmark for ${LABEL} (${SIZE})..."

  ./gradlew clean jmh \
    -PjmhIncludes=ArraySearchColdCacheBenchmark \
    -Pbenchmark.array.size="${SIZE}"

  TARGET_FILE="./benchmark-${JDK_VENDOR}-${LABEL}-cold.txt"

  cp "$SOURCE_FILE" "$TARGET_FILE"

  echo "Saved result to: $TARGET_FILE"
  echo

  ./gradlew clean jmh \
      -PjmhIncludes=ArraySearchHotCacheBenchmark \
      -Pbenchmark.array.size="${SIZE}"

  TARGET_FILE="./benchmark-${JDK_VENDOR}-${LABEL}-hot.txt"

  cp "$SOURCE_FILE" "$TARGET_FILE"

  echo "Saved result to: $TARGET_FILE"
  echo
done

echo "All benchmarks completed."
