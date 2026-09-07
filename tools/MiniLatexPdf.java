package mixnfix.gui;

/*
 * A very small stand-in for pdflatex, used only because this sandbox has no
 * LaTeX toolchain (and no network access to install one). It is exposed to the
 * application as an executable named "pdflatex" by
 * scripts/run_correcao_fixes_test.sh, so that the "Relatório PDF" button of the
 * grading module runs its real code path: the report's .tex file is produced by
 * mixnfix.reports.ModelProvaCorrecaoReport, written by
 * mixnfix.gui.PanelProduzirRelatorio and compiled with the command configured
 * on the settings panel (ConfiguracaoMIXnFIX.compileTEX2PDF).
 *
 * It deliberately behaves like TeX Live's pdflatex where the reported bugs are
 * concerned:
 *
 *   * it accepts -halt-on-error, -interaction=..., -output-directory=DIR,
 *     -jobname=NAME and the name of the .tex file, and *rejects* any other
 *     option - in particular the MiKTeX only -include-directory=... and
 *     -aux-directory=..., printing the same "unrecognized option" message and
 *     exiting with a non zero status;
 *
 *   * it reads the .tex file as declared by its inputenc package: if the
 *     document declares latin1 but the bytes are UTF-8 (which is what every
 *     string of the application is), it stops with
 *        ! Package inputenc Error: Keyboard character used is undefined
 *        (inputenc)                in inputencoding `latin1'.
 *     exactly like the real pdflatex did before the fix.
 *
 * It then "typesets" the subset of LaTeX the reports use (tabbing rows,
 * sections, verbatim blocks, \includegraphics of the jpg pictures of the
 * corrected exams, page breaks) into a real, valid PDF file.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MiniLatexPdf {

    // A4 at 72 dpi
    static final double PAGE_W = 595, PAGE_H = 842;
    static final double MARGIN = 42;
    static final double FONT_SIZE = 9;
    static final double LEADING = 11.5;

    // ------------------------------------------------------------------
    // page model
    // ------------------------------------------------------------------

    static class Item {
        String text;        // text line (null for an image)
        File image;         // picture of a corrected exam
        double size = FONT_SIZE;
        boolean bold;
        Item(String t) { text = t; }
        Item(File img) { image = img; }
    }

    static class Page {
        List<Item> items = new ArrayList<Item>();
    }

    public static void main(String args[]) {
        try {
            System.exit(run(args));
        }
        catch (Exception ex) {
            System.out.println("! Emergency stop: " + ex);
            System.exit(1);
        }
    }

    static int run(String args[]) throws Exception {
        String outputDir = null;
        String jobName = null;
        File texFile = null;

        for (String a : args) {
            if (a.startsWith("-")) {
                String opt = a.startsWith("--")? a.substring(2): a.substring(1);
                if (opt.equals("halt-on-error") || opt.startsWith("interaction=") ||
                    opt.equals("file-line-error") || opt.equals("shell-escape") ||
                    opt.equals("recorder"))
                    continue;
                if (opt.startsWith("output-directory=")) {
                    outputDir = opt.substring("output-directory=".length());
                    continue;
                }
                if (opt.startsWith("jobname=")) {
                    jobName = opt.substring("jobname=".length());
                    continue;
                }
                // every other option, in particular the MiKTeX only
                // -include-directory= and -aux-directory=, is unknown here as
                // it is unknown to TeX Live's pdflatex
                System.err.println("pdflatex: unrecognized option '" + a + "'");
                System.err.println("Try `pdflatex --help' for more information.");
                return 1;
            }
            else {
                texFile = new File(a);
            }
        }

        if (texFile == null) {
            System.err.println("pdflatex: no input file given");
            return 1;
        }
        if (!texFile.isAbsolute())
            texFile = new File(System.getProperty("user.dir"), texFile.getPath());
        if (!texFile.exists()) {
            System.out.println("! I can't find file `" + texFile + "'.");
            return 1;
        }

        byte bytes[] = Files.readAllBytes(texFile.toPath());
        String source = decode(bytes);
        if (source == null)
            return 1;   // inputenc error already reported

        String base = texFile.getName().replaceAll("\\.[^.]*$", "");
        if (jobName != null)
            base = jobName;
        File out = new File(outputDir != null? new File(outputDir): texFile.getParentFile(), base + ".pdf");

        List<Page> pages = typeset(source, texFile.getParentFile());
        writePdf(pages, out);

        System.out.println("Output written on " + out.getAbsolutePath() +
                           " (" + pages.size() + " pages).");
        return 0;
    }

    /**
     * Decodes the source according to the input encoding it declares, the way
     * the inputenc package does; returns null (after printing the very error
     * message of the real pdflatex) when the bytes do not belong to the
     * declared encoding.
     */
    static String decode(byte bytes[]) {
        String ascii = new String(bytes, StandardCharsets.ISO_8859_1);
        boolean latin1 = ascii.contains("[latin1]{inputenc}");

        if (latin1) {
            // latin1 has no undefined byte by itself; what the real inputenc
            // complains about is a byte that is not a valid latin1 *character*
            // of the current font encoding: the second (and following) bytes of
            // the UTF-8 sequences of a UTF-8 encoded file.
            for (int i = 0; i < bytes.length; i++) {
                int b = bytes[i] & 0xFF;
                if (b >= 0xC2 && b <= 0xF4 && i + 1 < bytes.length) {
                    int n = bytes[i + 1] & 0xFF;
                    if (n >= 0x80 && n <= 0xBF) {
                        System.out.println("! Package inputenc Error: Keyboard character used is undefined");
                        System.out.println("(inputenc)                in inputencoding `latin1'.");
                        System.out.println("");
                        System.out.println("See the inputenc package documentation for explanation.");
                        return null;
                    }
                }
            }
            return ascii;
        }

        // utf8 (the default of modern LaTeX too)
        CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder();
        dec.onMalformedInput(CodingErrorAction.REPORT);
        dec.onUnmappableCharacter(CodingErrorAction.REPORT);
        try {
            return dec.decode(java.nio.ByteBuffer.wrap(bytes)).toString();
        }
        catch (Exception ex) {
            System.out.println("! Package inputenc Error: Invalid UTF-8 byte sequence.");
            return null;
        }
    }

    // ------------------------------------------------------------------
    // "typesetting"
    // ------------------------------------------------------------------

    static List<Page> typeset(String source, File baseDir) {
        List<Page> pages = new ArrayList<Page>();
        Page page = new Page();
        int linesOnPage = 0;
        int maxLines = (int) ((PAGE_H - 2 * MARGIN) / LEADING);

        int begin = source.indexOf("\\begin{document}");
        if (begin >= 0)
            source = source.substring(begin + "\\begin{document}".length());
        int end = source.indexOf("\\end{document}");
        if (end >= 0)
            source = source.substring(0, end);

        boolean verbatim = false;
        String lines[] = source.split("\n", -1);

        for (String raw : lines) {
            String line = raw;

            if (verbatim) {
                if (line.trim().equals("\\end{verbatim}")) { verbatim = false; continue; }
                Item it = new Item(line);
                it.size = FONT_SIZE - 1;
                page.items.add(it);
                linesOnPage++;
            }
            else if (line.trim().equals("\\begin{verbatim}")) {
                verbatim = true;
            }
            else if (line.contains("\\includegraphics")) {
                String file = between(line, "\\includegraphics", "{", "}");
                File img = file == null? null: new File(baseDir, file);
                if (img != null && img.exists()) {
                    // the picture takes the rest of the page
                    page.items.add(new Item(img));
                    linesOnPage = maxLines;
                }
            }
            else if (line.contains("\\clearpage") || line.contains("\\eject") || line.contains("\\newpage")) {
                linesOnPage = maxLines;
            }
            else {
                if (line.contains("\\kill"))
                    continue;   // tabbing template row
                boolean bold = line.contains("\\bf") || line.contains("\\section");
                double size = FONT_SIZE;
                if (line.contains("\\section"))
                    size = FONT_SIZE + 4;
                else if (line.contains("\\Large"))
                    size = FONT_SIZE + 2;
                else if (line.contains("scriptsize"))
                    size = FONT_SIZE - 1;
                String text = plain(line);
                if (text.trim().length() == 0 && !line.trim().isEmpty() && !line.contains("\\\\"))
                    continue;
                Item it = new Item(text);
                it.bold = bold;
                it.size = size;
                page.items.add(it);
                linesOnPage++;
            }

            if (linesOnPage >= maxLines) {
                pages.add(page);
                page = new Page();
                linesOnPage = 0;
            }
        }
        if (!page.items.isEmpty())
            pages.add(page);
        if (pages.isEmpty())
            pages.add(new Page());
        return pages;
    }

    static String between(String line, String after, String open, String close) {
        int i = line.indexOf(after);
        if (i < 0) return null;
        int a = line.indexOf(open, i);
        int b = line.indexOf(close, a + 1);
        if (a < 0 || b < 0) return null;
        return line.substring(a + 1, b);
    }

    /** removes the LaTeX markup of the report, keeping its text. */
    static String plain(String line) {
        String s = line;
        s = s.replaceAll("\\\\hyperref\\[[^\\]]*\\]\\{([^}]*)\\}", "$1");
        s = s.replaceAll("\\\\label\\{[^}]*\\}", "");
        s = s.replaceAll("\\\\hspace\\*?\\{[^}]*\\}", "");
        s = s.replaceAll("\\\\(sub)*section\\*?\\{([^}]*)\\}", "$2");
        s = s.replaceAll("\\\\begin\\{[^}]*\\}(\\{[^}]*\\})?", "");
        s = s.replaceAll("\\\\end\\{[^}]*\\}", "");
        s = s.replace("\\>", "    ");
        s = s.replace("\\=", " ");
        s = s.replace("\\\\", "");
        s = s.replaceAll("\\\\[a-zA-Z]+\\*?", "");
        s = s.replace("{", "").replace("}", "");
        s = s.replace("~", " ");
        return s;
    }

    // ------------------------------------------------------------------
    // minimal PDF writer (Helvetica + DCTDecode images)
    // ------------------------------------------------------------------

    static class Obj {
        byte[] data;
        Obj(byte[] d) { data = d; }
    }

    static void writePdf(List<Page> pages, File out) throws IOException {
        List<byte[]> objects = new ArrayList<byte[]>();

        // 1 catalog, 2 pages, 3 font: reserved
        objects.add(null);
        objects.add(null);
        objects.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>"));
        objects.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>"));

        List<Integer> pageObjs = new ArrayList<Integer>();

        for (Page page : pages) {
            StringBuilder content = new StringBuilder();
            StringBuilder xobjects = new StringBuilder();
            double y = PAGE_H - MARGIN;
            int imgIndex = 0;

            for (Item it : page.items) {
                if (it.image != null) {
                    byte jpeg[] = Files.readAllBytes(it.image.toPath());
                    int wh[] = jpegSize(jpeg);
                    double iw = wh[0], ih = wh[1];
                    double availW = PAGE_W - 2 * MARGIN;
                    double availH = y - MARGIN;
                    double scale = Math.min(availW / iw, availH / ih);
                    if (scale <= 0) scale = availW / iw;
                    double dw = iw * scale, dh = ih * scale;

                    String header = "<< /Type /XObject /Subtype /Image /Width " + (int) iw +
                                    " /Height " + (int) ih +
                                    " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Filter /DCTDecode /Length " +
                                    jpeg.length + " >>";
                    objects.add(stream(header, jpeg));
                    int objNum = objects.size();
                    String name = "Im" + (imgIndex++);
                    xobjects.append("/").append(name).append(' ').append(objNum).append(" 0 R ");

                    double x = MARGIN + (availW - dw) / 2;
                    content.append("q ").append(fmt(dw)).append(" 0 0 ").append(fmt(dh)).append(' ')
                           .append(fmt(x)).append(' ').append(fmt(y - dh)).append(" cm /")
                           .append(name).append(" Do Q\n");
                    y -= dh + LEADING;
                }
                else {
                    String font = it.bold? "/F2": "/F1";
                    content.append("BT ").append(font).append(' ').append(fmt(it.size))
                           .append(" Tf ").append(fmt(MARGIN)).append(' ').append(fmt(y))
                           .append(" Td (").append(escape(it.text)).append(") Tj ET\n");
                    y -= LEADING * (it.size > FONT_SIZE? 1.4: 1.0);
                }
                if (y < MARGIN) break;
            }

            byte contentBytes[] = content.toString().getBytes(StandardCharsets.ISO_8859_1);
            objects.add(stream("<< /Length " + contentBytes.length + " >>", contentBytes));
            int contentObj = objects.size();

            objects.add(bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) PAGE_W + " " + (int) PAGE_H +
                              "] /Resources << /Font << /F1 3 0 R /F2 4 0 R >> " +
                              (xobjects.length() > 0? "/XObject << " + xobjects + ">> ": "") +
                              ">> /Contents " + contentObj + " 0 R >>"));
            pageObjs.add(objects.size());
        }

        StringBuilder kids = new StringBuilder();
        for (int n : pageObjs) kids.append(n).append(" 0 R ");
        objects.set(0, bytes("<< /Type /Catalog /Pages 2 0 R >>"));
        objects.set(1, bytes("<< /Type /Pages /Kids [" + kids.toString().trim() + "] /Count " + pageObjs.size() + " >>"));

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        buf.write(bytes("%PDF-1.4\n"));
        int offsets[] = new int[objects.size() + 1];
        for (int i = 0; i < objects.size(); i++) {
            offsets[i + 1] = buf.size();
            buf.write(bytes((i + 1) + " 0 obj\n"));
            buf.write(objects.get(i));
            buf.write(bytes("\nendobj\n"));
        }
        int xrefStart = buf.size();
        StringBuilder xref = new StringBuilder();
        xref.append("xref\n0 ").append(objects.size() + 1).append('\n');
        xref.append("0000000000 65535 f \n");
        for (int i = 1; i <= objects.size(); i++)
            xref.append(String.format("%010d 00000 n \n", offsets[i]));
        xref.append("trailer\n<< /Size ").append(objects.size() + 1).append(" /Root 1 0 R >>\n");
        xref.append("startxref\n").append(xrefStart).append("\n%%EOF\n");
        buf.write(bytes(xref.toString()));

        FileOutputStream fos = new FileOutputStream(out);
        buf.writeTo(fos);
        fos.close();
    }

    static byte[] stream(String header, byte data[]) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        b.write(bytes(header + "\nstream\n"));
        b.write(data);
        b.write(bytes("\nendstream"));
        return b.toByteArray();
    }

    static byte[] bytes(String s) {
        return s.getBytes(StandardCharsets.ISO_8859_1);
    }

    static String fmt(double d) {
        return String.format(java.util.Locale.US, "%.2f", d);
    }

    static String escape(String s) {
        StringBuilder b = new StringBuilder();
        Charset cp1252 = Charset.forName("windows-1252");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == ')' || c == '\\') b.append('\\');
            if (c > 255 && !cp1252.canEncode()) c = '?';
            b.append(c);
        }
        // encode to cp1252 and back, so that accented characters survive as
        // single bytes of the WinAnsi encoding
        byte enc[] = b.toString().getBytes(cp1252);
        return new String(enc, StandardCharsets.ISO_8859_1);
    }

    /** width and height of a JPEG file. */
    static int[] jpegSize(byte d[]) {
        int i = 2;
        while (i + 9 < d.length) {
            if ((d[i] & 0xFF) != 0xFF) { i++; continue; }
            int marker = d[i + 1] & 0xFF;
            int len = ((d[i + 2] & 0xFF) << 8) | (d[i + 3] & 0xFF);
            if (marker >= 0xC0 && marker <= 0xCF && marker != 0xC4 && marker != 0xC8 && marker != 0xCC) {
                int h = ((d[i + 5] & 0xFF) << 8) | (d[i + 6] & 0xFF);
                int w = ((d[i + 7] & 0xFF) << 8) | (d[i + 8] & 0xFF);
                return new int[] { w, h };
            }
            i += 2 + len;
        }
        return new int[] { 600, 800 };
    }
}
