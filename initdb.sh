#!/bin/bash
# Create a fresh empty Derby database at src/db/ using the bundled schema.
# Run once before first use, or whenever you want a clean database.
set -e

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Locate JRE
if [ -d "$ROOT/jdk" ]; then
    JAVA_HOME="$(find "$ROOT/jdk" -mindepth 1 -maxdepth 1 -type d | head -1)"
fi
if [ -n "$JAVA_HOME" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    JAVA="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA=java
else
    echo "ERROR: no java found." >&2; exit 1
fi

DBDIR="$ROOT/src/db"
if [ -d "$DBDIR" ]; then
    echo "Database already exists at $DBDIR"
    read -p "Overwrite it? [y/N] " ans
    [ "$ans" = "y" ] || { echo "Aborted."; exit 0; }
    rm -rf "$DBDIR"
fi

# Must run from src/ — the ij script uses relative paths ('db', 'mixnfix_derby_schema.sql')
cd "$ROOT/src"

DERBY_CP="$ROOT/lib/derby/lib/derby.jar:$ROOT/lib/derby/lib/derbytools.jar"
echo "Creating Derby database at $DBDIR ..."
"$JAVA" -cp "$DERBY_CP" -Dij.showNoConnectionsAtStart=true org.apache.derby.tools.ij script

# Clean up connect-without-create log noise
rm -f "$ROOT/src/derby.log"

if [ -d "$DBDIR" ]; then
    echo "Done. Now run: ./run.sh"
else
    echo "ERROR: database creation failed" >&2; exit 1
fi
