#!/usr/bin/env bash
# Generates the student project from this solutions project.
#
# Every block between `// solution:begin` and `// solution:end` is replaced by `???`
# (same indentation). Tests are copied as-is.
#
# Usage: ./make-stubs.sh [destination]   (default: ../pure-scala-fp)
set -euo pipefail

SRC="$(cd "$(dirname "$0")" && pwd)"
DEST="${1:-$SRC/../pure-scala-fp}"

mkdir -p "$DEST/src/main/scala/fp" "$DEST/src/test/scala/fp"

for f in "$SRC"/src/main/scala/fp/*.scala; do
  awk '
    /^[[:space:]]*\/\/ solution:begin/ {
      if (skip) { print FILENAME ":" NR ": nested solution:begin" > "/dev/stderr"; exit 1 }
      match($0, /^[[:space:]]*/); print substr($0, 1, RLENGTH) "???"; skip = 1; next
    }
    /^[[:space:]]*\/\/ solution:end/ { skip = 0; next }
    !skip { print }
    END { if (skip) { print FILENAME ": unterminated solution:begin" > "/dev/stderr"; exit 1 } }
  ' "$f" > "$DEST/src/main/scala/fp/$(basename "$f")"
done

cp "$SRC"/src/test/scala/fp/*.scala "$DEST/src/test/scala/fp/"

echo "Stubs generated in $DEST"
