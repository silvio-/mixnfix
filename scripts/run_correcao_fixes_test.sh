#!/bin/bash
# ---------------------------------------------------------------------------
# Validation run of the four fixes of the grading (correction) module.
#
# Reproduces, headlessly, the scenario:
#
#   "Adicionar Pasta" -> "Importar Prova" (AVLC-2-2025-EE3.prova) ->
#   "Adicionar Correção" ("c1") ->
#   1. right click on the folder -> "Importar Alunos" (FAL-2-2025-Alunos.txt),
#      then "c1" -> tab "Alunos" -> "+"          (snapshot)
#   2. "Corrigir" over four jpg files -> "Fechar"  (snapshot)
#   3. "Relatório PDF" -> pdf written in the output directory
#
# Output (in /app/output):
#
#   correcao_fix1_adicionar_alunos.jpg
#   correcao_fix2_e_fix3_apos_corrigir.jpg
#   correcao_fix4_relatorio_pdf.jpg
#   relatorio_c1.pdf
#   correcao_fixes_report.txt
#
# This sandbox has no LaTeX installed, so a stand-in for pdflatex
# (tools/MiniLatexPdf.java, see its header) is put on the PATH: it accepts and
# rejects exactly the options TeX Live's pdflatex does and reproduces the
# inputenc error of the bug report, so the application's real "Relatório PDF"
# code path is the one being exercised.
# ---------------------------------------------------------------------------
set -e
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"

OUT="${MIXNFIX_OUTPUT_DIR:-$ROOT/output}"
LIBS="$ROOT/bin:$ROOT/lib/derby.jar:$ROOT/lib/jxl.jar"

[ -d "$ROOT/bin" ] || ./build.sh

# fresh database, so the run always starts from a known state
rm -rf "$ROOT/src/db" "$ROOT/src/data"
( cd "$ROOT/src" && java -cp "$ROOT/lib/derby/lib/derby.jar:$ROOT/lib/derby/lib/derbytools.jar" \
      -Dij.showNoConnectionsAtStart=true org.apache.derby.tools.ij script > /dev/null )
rm -f "$ROOT/src/derby.log"

# the test drivers live outside src/
javac -encoding UTF-8 -cp "$LIBS" -sourcepath "$ROOT/src" -d "$ROOT/bin" \
      "$ROOT/tools/DemoCorrecaoFixes.java" "$ROOT/tools/FotosDeTeste.java" "$ROOT/tools/MiniLatexPdf.java"

# pdflatex stand-in
FAKEBIN="${TMPDIR:-/tmp}/mixnfix-fakebin"
mkdir -p "$FAKEBIN"
cat > "$FAKEBIN/pdflatex" <<EOF
#!/bin/bash
exec java -cp "$LIBS" mixnfix.gui.MiniLatexPdf "\$@"
EOF
chmod +x "$FAKEBIN/pdflatex"
export PATH="$FAKEBIN:$PATH"

mkdir -p "$OUT"
cd "$ROOT/src"
exec java -Djava.awt.headless=true \
     -Dmixnfix.output.dir="$OUT" \
     -Dmixnfix.fotos.dir="${MIXNFIX_FOTOS_DIR:-/tmp/TesteMixnFix}" \
     -cp "$LIBS" mixnfix.gui.DemoCorrecaoFixes
