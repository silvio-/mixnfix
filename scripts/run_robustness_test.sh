#!/bin/bash
# ---------------------------------------------------------------------------
# Robustness / regression test of the control point detection:
#
#  * detects the 21 control dots on the sample pictures (weakly and severely
#    deformed) and writes the composed verification images in /app/output;
#  * warps the same pictures with several strong perspective homographies and
#    checks that the detected control points follow the warp (sub pixel);
#  * repeats the detection on 2x and 3x enlarged pictures (the old code was
#    limited to 1000x1000 pictures).
# ---------------------------------------------------------------------------
set -e
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

[ -d "$ROOT/bin" ] || ./build.sh
javac -encoding UTF-8 -cp "$ROOT/bin" -sourcepath "$ROOT/src" -d "$ROOT/bin" \
      "$ROOT/tools/GradeTest.java" "$ROOT/tools/WarpTest.java" "$ROOT/tools/ScaleTest.java"

echo "=== detection on the sample pictures ==============================="
java -Xmx2g -Dmixnfix.output.dir="$ROOT/output" -cp "$ROOT/bin" \
     mixnfix.GradeTest "$ROOT/AVLC-2-2025-EE3.prova" "$ROOT/img2.jpg" "$ROOT/img1.jpg"

echo "=== strong perspective warps ======================================="
java -Xmx2g -cp "$ROOT/bin" mixnfix.WarpTest "$ROOT/AVLC-2-2025-EE3.prova" "$ROOT/img2.jpg" "$ROOT/img1.jpg"

echo "=== enlarged pictures =============================================="
java -Xmx3g -cp "$ROOT/bin" mixnfix.ScaleTest "$ROOT/AVLC-2-2025-EE3.prova" "$ROOT/img1.jpg" "$ROOT/img2.jpg"
