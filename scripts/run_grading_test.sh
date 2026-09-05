#!/bin/bash
# ---------------------------------------------------------------------------
# Runs the grading (correction) module on the two sample exams and produces,
# in /app/output:
#
#   <picture>.jpg                  grey scale exam frame + control points in yellow
#   composed_result.jpg            copy of the last composed frame
#   gui_<picture>_corrigir.jpg     grading GUI right after the "Corrigir" routine
#   gui_<picture>_id_code.jpg      grading GUI right after the "ID & Code" routine
#   grading_cpu_times.txt          cpu/wall times of the grading module
#
# Usage: ./scripts/run_grading_test.sh [prova] [picture ...]
# ---------------------------------------------------------------------------
set -e
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

PROVA="${1:-$ROOT/AVLC-2-2025-EE3.prova}"
shift || true
IMAGES=("$@")
if [ ${#IMAGES[@]} -eq 0 ]; then
    IMAGES=("$ROOT/img2.jpg" "$ROOT/img1.jpg")   # weakly deformed, severely deformed
fi

[ -d "$ROOT/bin" ] || ./build.sh
[ -d "$ROOT/src/db" ] || echo "warning: database not created (run ./initdb.sh); not needed for grading"

# the tools (test drivers) live outside src/
javac -encoding UTF-8 -cp "$ROOT/bin:$ROOT/lib/derby.jar:$ROOT/lib/jxl.jar" \
      -sourcepath "$ROOT/src" -d "$ROOT/bin" "$ROOT/tools/GradingGUIRun.java"

mkdir -p "$ROOT/output"
cd "$ROOT/src"
exec java -Djava.awt.headless=true -Dmixnfix.output.dir="$ROOT/output" \
     -cp "$ROOT/bin:$ROOT/lib/derby.jar:$ROOT/lib/jxl.jar" \
     mixnfix.gui.GradingGUIRun "$PROVA" "${IMAGES[@]}"
