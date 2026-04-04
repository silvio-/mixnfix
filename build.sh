#!/bin/bash
# Build MIXnFIX for Linux
set -e

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

# Locate JDK: bundled > JAVA_HOME > PATH
if [ -d "$ROOT/jdk" ]; then
    JAVA_HOME="$(find "$ROOT/jdk" -mindepth 1 -maxdepth 1 -type d | head -1)"
fi
if [ -z "$JAVA_HOME" ] || [ ! -x "$JAVA_HOME/bin/javac" ]; then
    if command -v javac >/dev/null 2>&1; then
        JAVAC=javac
    else
        echo "ERROR: no javac found. Install a JDK or set JAVA_HOME." >&2
        exit 1
    fi
else
    JAVAC="$JAVA_HOME/bin/javac"
fi
echo "Using javac: $JAVAC"

SRC="$ROOT/src"
BIN="$ROOT/bin"
LIBS="$ROOT/lib/derby.jar:$ROOT/lib/jxl.jar"

rm -rf "$BIN"
mkdir -p "$BIN"

# Collect sources — exclude default-package test files, duke/ (QuickTime demos)
SOURCES=$(mktemp)
find "$SRC/mixnfix"  -name '*.java' >  "$SOURCES"
find "$SRC/linsoft"  -name '*.java' >> "$SOURCES"
find "$SRC/app"      -name '*.java' >> "$SOURCES"
find "$SRC/templates" -name '*.java' >> "$SOURCES"

echo "Compiling $(wc -l < "$SOURCES") source files..."
"$JAVAC" -encoding UTF-8 -source 8 -target 8 -Xlint:-options \
    -cp "$LIBS" -sourcepath "$SRC" -d "$BIN" @"$SOURCES"
rm -f "$SOURCES"

# Copy resources
echo "Copying resources..."
cp -r "$SRC/images" "$BIN/" 2>/dev/null || true
[ -d "$SRC/data" ] && cp -r "$SRC/data" "$BIN/" 2>/dev/null || true
find "$SRC/mixnfix" -name '*.png' -o -name '*.gif' -o -name '*.jpg' 2>/dev/null | while read -r f; do
    rel="${f#$SRC/}"
    mkdir -p "$BIN/$(dirname "$rel")"
    cp "$f" "$BIN/$rel"
done

echo "Build complete → $BIN"
