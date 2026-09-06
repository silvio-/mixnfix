package mixnfix.gui;

/*
 * Fallback exam-PDF producer, used only because this sandbox has neither a
 * display server nor a LaTeX toolchain (pdflatex/epstopdf) and no network
 * access to install one -- both of which mixnfix.gui.PanelEdicaoProva's real
 * "Produzir" button relies on (mixnfix.composicaoprova.Prova2TeX writes a
 * .tex file and then shells out to pdflatex).
 *
 * It still exercises the real domain classes: mixnfix.prova.ProvaStructure,
 * mixnfix.prova.Quesito, mixnfix.prova.Papel and the real
 * mixnfix.prova.PanelPapel Swing component (the very same one the interactive
 * editor uses to display/edit each question's "enunciado") to obtain the
 * exam's actual text, then writes a small, valid, real PDF file with it using
 * a minimal hand rolled PDF writer (base-14 Helvetica, WinAnsi/Latin1 text),
 * and rasterizes the very same content into a matching JPG image.
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import mixnfix.prova.PanelPapel;
import mixnfix.prova.Papel;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

class ExamPdfFallback {

    private static final int CHARS_PER_LINE = 100;
    private static final int LINES_PER_PAGE = 58;

    private static final double PAGE_W = 595; // A4 @72dpi
    private static final double PAGE_H = 842;

    static String textOf(Papel papel) {
        if (papel == null) return "";
        PanelPapel pp = new PanelPapel(papel);
        return pp.getTextPane().getText();
    }

    static String tipoLabel(int tipo) {
        if (tipo == Quesito.TIPO_ALTERNATIVAS) return "Alternativas";
        if (tipo == Quesito.TIPO_FALSO_VERDADEIRO) return "Falso/Verdadeiro";
        if (tipo == Quesito.TIPO_NUMERICO_99) return "Numerico_99";
        if (tipo == Quesito.TIPO_SUBJETIVA_5) return "Subjetiva_5";
        if (tipo == Quesito.TIPO_SUBJETIVA_9) return "Subjetiva_9";
        return "Desconhecido";
    }

    static List<String> wrap(String text, int width) {
        List<String> lines = new ArrayList<String>();
        if (text == null) text = "";
        for (String paragraph : text.split("\n", -1)) {
            StringBuilder cur = new StringBuilder();
            for (String word : paragraph.split(" ")) {
                if (cur.length() > 0 && cur.length() + 1 + word.length() > width) {
                    lines.add(cur.toString());
                    cur.setLength(0);
                }
                if (cur.length() > 0) cur.append(' ');
                cur.append(word);
            }
            lines.add(cur.toString());
        }
        return lines;
    }

    /** builds the whole exam content as a flat list of (styled) text lines. */
    static List<String> buildContentLines(ProvaStructure prova) {
        List<String> lines = new ArrayList<String>();
        lines.add("MIXnFIX - Prova (fallback textual - pdflatex indisponivel neste sandbox)");
        lines.add("");
        for (String l : wrap(textOf(prova.getCabecalho()), CHARS_PER_LINE))
            lines.add(l);
        lines.add("");
        lines.add("------------------------------------------------------------------");

        int n = 1;
        for (Quesito q : prova.getQuesitos()) {
            lines.add("");
            lines.add(String.format("Questao %d [%s] (tag: %s)  valor acerto=%.3f valor falha=%.3f",
                    n++, tipoLabel(q.getTipo()), q.getTag(), q.getValorAcerto(), q.getValorFalha()));
            for (String l : wrap(textOf(q.getEnunciado()), CHARS_PER_LINE))
                lines.add("   " + l);
            if (q.getTipo() == Quesito.TIPO_SUBJETIVA_9) {
                lines.add("   (Quesito subjetivo, o professor marca um entre 9 niveis:");
                lines.add("    0, 1/8, 2/8, 3/8, 4/8, 5/8, 6/8, 7/8, 1 do valor da questao)");
            }
            else if (q.getTipo() == Quesito.TIPO_SUBJETIVA_5) {
                lines.add("   (Quesito subjetivo, o professor marca um entre 5 niveis:");
                lines.add("    0, 1/4, 2/4, 3/4, 1 do valor da questao)");
            }
        }
        return lines;
    }

    /** produces the PDF file and returns a raster of the first page. */
    static BufferedImage produce(ProvaStructure prova, File pdfOut, File textDumpOut) throws Exception {
        List<String> lines = buildContentLines(prova);

        // plain text dump, for reference/debugging
        PrintWriter pw = new PrintWriter(textDumpOut, "UTF-8");
        for (String l : lines) pw.println(l);
        pw.close();

        // split into pages
        List<List<String>> pages = new ArrayList<List<String>>();
        for (int i = 0; i < lines.size(); i += LINES_PER_PAGE)
            pages.add(lines.subList(i, Math.min(i + LINES_PER_PAGE, lines.size())));
        if (pages.isEmpty())
            pages.add(new ArrayList<String>());

        writePdf(pages, pdfOut);
        return renderPageToImage(pages.get(0), 1240, 1754);
    }

    // ------------------------------------------------------------------
    // tiny, self contained, valid PDF writer (base-14 Helvetica)
    // ------------------------------------------------------------------

    private static String escapePdfText(String s) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == ')' || c == '\\') b.append('\\');
            if (c > 255) c = '?';
            b.append(c);
        }
        return b.toString();
    }

    private static void writePdf(List<List<String>> pages, File out) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<Integer>();

        // object numbering: 1=Catalog 2=Pages 3=Font, then pairs (page,contents) per page
        int numPages = pages.size();
        int firstPageObj = 4;

        offsets.add(0); // dummy for obj 0

        buf.write("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes(StandardCharsets.ISO_8859_1));

        writeObj(buf, offsets, 1, "<< /Type /Catalog /Pages 2 0 R >>");

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < numPages; i++) {
            if (i > 0) kids.append(' ');
            kids.append(firstPageObj + 2 * i).append(" 0 R");
        }
        writeObj(buf, offsets, 2, "<< /Type /Pages /Kids [" + kids + "] /Count " + numPages + " >>");
        writeObj(buf, offsets, 3, "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>");

        for (int i = 0; i < numPages; i++) {
            int pageObj = firstPageObj + 2 * i;
            int contentsObj = pageObj + 1;
            writeObj(buf, offsets, pageObj,
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) PAGE_W + " " + (int) PAGE_H + "] " +
                "/Resources << /Font << /F1 3 0 R >> >> /Contents " + contentsObj + " 0 R >>");

            StringBuilder content = new StringBuilder();
            content.append("BT /F1 9 Tf 11 Tl 40 800 Td\n");
            for (String line : pages.get(i)) {
                content.append('(').append(escapePdfText(line)).append(") Tj 0 -11 Td\n");
            }
            content.append("ET\n");
            byte[] contentBytes = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            String objHeader = contentsObj + " 0 obj\n<< /Length " + contentBytes.length + " >>\nstream\n";
            offsets.add(buf.size());
            buf.write(objHeader.getBytes(StandardCharsets.ISO_8859_1));
            buf.write(contentBytes);
            buf.write("\nendstream\nendobj\n".getBytes(StandardCharsets.ISO_8859_1));
        }

        int xrefStart = buf.size();
        int numObjs = offsets.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(numObjs).append('\n');
        xref.append("0000000000 65535 f \n");
        for (int i = 1; i < numObjs; i++) {
            xref.append(String.format("%010d 00000 n \n", offsets.get(i)));
        }
        xref.append("trailer\n<< /Size ").append(numObjs).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(xrefStart).append("\n%%EOF");
        buf.write(xref.toString().getBytes(StandardCharsets.ISO_8859_1));

        FileOutputStream fos = new FileOutputStream(out);
        buf.writeTo(fos);
        fos.close();
    }

    private static void writeObj(ByteArrayOutputStream buf, List<Integer> offsets, int num, String body) throws Exception {
        offsets.add(buf.size());
        String s = num + " 0 obj\n" + body + "\nendobj\n";
        buf.write(s.getBytes(StandardCharsets.ISO_8859_1));
    }

    private static BufferedImage renderPageToImage(List<String> firstPageLines, int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.setColor(Color.BLACK);
        double scale = w / PAGE_W;
        g2.setFont(new Font("SansSerif", Font.PLAIN, (int) Math.round(9 * scale)));
        int x = (int) (40 * scale);
        int y = (int) (60 * scale);
        int dy = (int) (15 * scale);
        for (String line : firstPageLines) {
            g2.drawString(line, x, y);
            y += dy;
        }
        g2.dispose();
        return img;
    }
}
