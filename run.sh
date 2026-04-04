#!/bin/bash
# Run MIXnFIX on Linux
# Usage: ./run.sh [dbname] [datadir]

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Locate JRE: bundled > JAVA_HOME > PATH
if [ -d "$ROOT/jdk" ]; then
    JAVA_HOME="$(find "$ROOT/jdk" -mindepth 1 -maxdepth 1 -type d | head -1)"
fi
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA=java
else
    echo "ERROR: no java found. Install a JRE or set JAVA_HOME." >&2
    exit 1
fi

LIBS="$ROOT/bin:$ROOT/lib/derby.jar:$ROOT/lib/jxl.jar"
DBNAME="${1:-db}"
DATADIR="${2:-data}"

# App creates temp files (p<time>.node etc.) in cwd, so run from src/
cd "$ROOT/src"

echo "Starting MIXnFIX (db=$DBNAME, data=$DATADIR)"
exec "$JAVA" -Xmx1g -cp "$LIBS" mixnfix.bin.MIXnFIX "$DBNAME" "$DATADIR"
