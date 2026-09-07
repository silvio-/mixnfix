package mixnfix.gui;

/*
 * Preparation of the pictures that the "Corrigir" file chooser is given in the
 * headless validation run (see the header of tools/DemoCorrecaoFixes.java).
 *
 * The directory of the original scenario, /home/silvio/mixnfix/TesteMixnFix, is
 * not part of this repository: the only two pictures of answered exams shipped
 * here (img1.jpg and img2.jpg) are, moreover, two photographs of the answer
 * sheet of the *same* student (matrícula 20250025918, the first one of
 * FAL-2-2025-Alunos.txt).
 *
 * So this class builds a directory with four jpg files:
 *
 *   exam_20250025918_a.jpg   img2.jpg (weakly deformed photograph)
 *   exam_<matricula2>.jpg    img2.jpg with the "Identificação" marks repainted
 *   exam_<matricula3>.jpg    img2.jpg with the "Identificação" marks repainted
 *   exam_20250025918_b.jpg   img1.jpg (a second photograph, severely deformed,
 *                            of the exam of the first student: the *copy* that
 *                            must be discarded by the grading module)
 *
 * The marks are repainted using the very same machinery the grading module uses
 * to read them: the answer sheet's fixed CellMap (GeradorFolhaRespostas), the
 * control points located by MFI2Java.fitToImage() and the resulting homography
 * (MFI2Java.tranformPoints()), so the repainted cells fall exactly on the cells
 * that the module samples. Nothing else of the picture is touched.
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import mixnfix.MFI2Java;
import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.Field;
import mixnfix.folharesposta.GeradorFolhaRespostas;
import mixnfix.folharesposta.IFolhaResposta;
import mixnfix.folharesposta.MultiField;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.ProvaStructure;

class FotosDeTeste {

    /** the two other students of FAL-2-2025-Alunos.txt used on the synthetic pictures. */
    static final String MATRICULA_2 = "20230073883";  // ANA LUISA FEITOSA GUIMARAES
    static final String MATRICULA_3 = "20240039437";  // ARTHUR HENRIQUE DOS SANTOS SILVA

    static File[] prepararFotos(ProvaStructure prova, File dir, StringBuilder log) throws Exception {
        dir.mkdirs();
        for (File f : dir.listFiles())
            f.delete();

        File img1 = new File(System.getProperty("mixnfix.img1", "/app/img1.jpg"));
        File img2 = new File(System.getProperty("mixnfix.img2", "/app/img2.jpg"));

        List<File> result = new ArrayList<File>();

        // the original photograph of the exam of 20250025918
        File a = new File(dir, "exam_20250025918_a.jpg");
        copy(img2, a);
        result.add(a);

        // two synthetic pictures: same exam, other students
        result.add(comOutraMatricula(prova, img2, new File(dir, "exam_" + MATRICULA_2 + ".jpg"), MATRICULA_2));
        result.add(comOutraMatricula(prova, img2, new File(dir, "exam_" + MATRICULA_3 + ".jpg"), MATRICULA_3));

        // a second picture of the exam of 20250025918: the copy to be discarded
        File b = new File(dir, "exam_20250025918_b.jpg");
        copy(img1, b);
        result.add(b);

        if (log != null) {
            log.append("Fotos preparadas em " + dir.getAbsolutePath() + ":\n");
            log.append("   exam_20250025918_a.jpg  (img2.jpg, aluno 20250025918)\n");
            log.append("   exam_" + MATRICULA_2 + ".jpg  (img2.jpg com a identificacao repintada)\n");
            log.append("   exam_" + MATRICULA_3 + ".jpg  (img2.jpg com a identificacao repintada)\n");
            log.append("   exam_20250025918_b.jpg  (img1.jpg, COPIA da prova do aluno 20250025918)\n");
        }

        return result.toArray(new File[result.size()]);
    }

    static void copy(File src, File dst) throws Exception {
        java.nio.file.Files.copy(src.toPath(), dst.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Writes a copy of <code>src</code> whose "Identificação" field is marked
     * with <code>matricula</code>.
     */
    static File comOutraMatricula(ProvaStructure prova, File src, File dst, String matricula) throws Exception {
        BufferedImage image = ImageIO.read(src);

        IFolhaResposta fr = mixnfix.folharesposta.FabricaDeFolhaDeResposta.newFolhaResposta(
            prova, GeradorFolhaRespostas.ID);
        CellMap map = fr.getCellMapFixo();

        // same setup of MFActionColetarProvas.coletarHardWork()
        MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
        for (ControlPoint cp : map.getControlPoints())
            MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
        for (Quadrilateral q : map.getQuads())
            MFI2Java.addQuad(q.getP0().getId(), q.getP1().getId(), q.getP2().getId(), q.getP3().getId());

        PanelParametrosProcessamentoImagem params = new PanelParametrosProcessamentoImagem();
        MFI2Java.setConstraints(
            params.getThresholds(), params.getMinPixelWidth(), params.getMaxPixelWidth(),
            params.getMinPixelHeight(), params.getMaxPixelHeight(), params.getMinNumPixels(),
            params.getMaxNumPixels(), params.getPixelDensity(), params.getNumClosest(),
            params.getMinSide(), params.getAngleTolerance(), params.getTargetRadius(),
            params.getCorrectSideRatio(), params.getSideRatioTolerance(), params.getPhase(),
            params.getControlPointRadius(), params.getLeftMargin(), params.getRightMargin(),
            params.getTopMargin(), params.getBottomMargin());

        byte data[] = MFI2Java.ensureBuffer(null, image);
        MFI2Java.loadImageToBuffer(image, data);
        double controlPoints[] = new double[1000];
        if (!MFI2Java.fitToImage(data, image.getWidth(), image.getHeight(), controlPoints))
            throw new RuntimeException("could not locate the control points of " + src);

        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        MultiField mfId = (MultiField) map.getField(CellMap.MULTICAMPO_ID);
        int pos = 0;
        for (Field f : mfId.getFields()) {
            OptionField of = (OptionField) f;
            int digito = pos < matricula.length()? matricula.charAt(pos) - '0': -1;
            for (int i = 0; i < of.getNumCells(); i++) {
                Cell cell = of.getCell(i);
                double xy[] = posicaoNaImagem(map, cell);
                double raio = raioNaImagem(map, cell);
                // erase whatever mark there was, then mark the wanted digit
                pintar(g, xy[0], xy[1], raio, Color.WHITE);
                if (i == digito)
                    pintar(g, xy[0], xy[1], 0.62 * raio, Color.BLACK);
            }
            pos++;
        }
        g.dispose();

        ImageIO.write(out, "jpg", dst);
        return dst;
    }

    /** center of a cell on the picture, through the fitted homography. */
    static double[] posicaoNaImagem(CellMap map, Cell cell) {
        double x[] = { cell.getX() - map.getX0(), cell.getY() - map.getY0() };
        double y[] = { 0, 0 };
        MFI2Java.tranformPoints(x, y, 1);
        return y;
    }

    /** radius, in pixels of the picture, of the white sampling box of a cell. */
    static double raioNaImagem(CellMap map, Cell cell) {
        double c[] = posicaoNaImagem(map, cell);
        double x[] = { cell.getX() - map.getX0() + 0.5 * cell.getW2(), cell.getY() - map.getY0() };
        double y[] = { 0, 0 };
        MFI2Java.tranformPoints(x, y, 1);
        return Math.max(2.0, Math.hypot(y[0] - c[0], y[1] - c[1]));
    }

    static void pintar(Graphics2D g, double x, double y, double raio, Color color) {
        g.setColor(color);
        g.setStroke(new BasicStroke(1f));
        g.fill(new Ellipse2D.Double(x - raio, y - raio, 2 * raio, 2 * raio));
    }
}
