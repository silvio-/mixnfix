package mixnfix;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;

import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.GeradorFolhaRespostas;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.Parser;
import mixnfix.prova.ProvaStructure;

public class GradeTest {

    static ProvaStructure loadProva(String provaFile) throws Exception {
        File tmp = File.createTempFile("prova", ".xml");
        ZipFile zf = new ZipFile(provaFile);
        InputStream in = zf.getInputStream(zf.getEntry("prova.xml"));
        FileOutputStream out = new FileOutputStream(tmp);
        byte buf[] = new byte[8192];
        int r;
        while ((r = in.read(buf)) > 0) out.write(buf, 0, r);
        out.close(); in.close(); zf.close();
        Parser p = new Parser(tmp.getAbsolutePath());
        tmp.delete();
        return p.getProva();
    }

    public static void main(String args[]) throws Exception {
        String provaFile = args[0];
        ProvaStructure prova = loadProva(provaFile);
        ControlPointDetector.VERBOSE = true;

        for (int a = 1; a < args.length; a++) {
            String imgFile = args[a];
            BufferedImage image = ImageIO.read(new File(imgFile));

            GeradorFolhaRespostas g = new GeradorFolhaRespostas(prova);
            CellMap map = g.getCellMapFixo();
            MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
            for (ControlPoint cp : map.getControlPoints())
                MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
            for (Quadrilateral q : map.getQuads())
                MFI2Java.addQuad(q.getP0().getId(), q.getP1().getId(), q.getP2().getId(), q.getP3().getId());

            int thresholds[] = {50,60,70,80,90,100,110,120};
            MFI2Java.setConstraints(thresholds,
                2, 15,      // min-max pixel width
                2, 15,      // min-max pixel height
                4, 120,     // min-max num pixels
                0.4,        // pixel density
                2,          // num closest
                150,        // min side
                5,          // angle tolerance
                30,         // target radius
                1.0,        // correct side ratio
                0.9,        // side ratio tolerance
                0,          // phase
                20,         // control point radius
                0, 1, 0, 1);// margins

            byte data[] = new byte[image.getWidth() * image.getHeight()];
            MFI2Java.loadImageToBuffer(image, data);

            double controlPoints[] = new double[1000];
            long t0 = System.nanoTime();
            boolean b = MFI2Java.fitToImage(data, image.getWidth(), image.getHeight(), controlPoints);
            long t1 = System.nanoTime();

            System.out.println("=== " + imgFile + " found=" + b + " time=" + ((t1 - t0) / 1.0e6) + " ms");
            if (!b) continue;
            for (ControlPoint cp : map.getControlPoints()) {
                cp.setImageXY(controlPoints[2 * cp.getId()], controlPoints[2 * cp.getId() + 1]);
                System.out.format("  cp %2d theo (%6.2f,%6.2f) -> img (%7.2f,%7.2f)%n",
                    cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0(), cp.getImageX(), cp.getImageY());
            }

            // compose visualization
            BufferedImage composed = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D gg = composed.createGraphics();
            gg.drawImage(image, 0, 0, null);
            gg.setColor(Color.YELLOW);
            for (ControlPoint cp : map.getControlPoints()) {
                int x = (int) Math.round(cp.getImageX());
                int y = (int) Math.round(cp.getImageY());
                gg.fillOval(x - 3, y - 3, 7, 7);
            }
            gg.dispose();
            new File(mixnfix.VisualizationExporter.getOutputDir()).mkdirs();
            String name = new File(imgFile).getName().replaceAll("\\.[^.]*$", "");
            ImageIO.write(composed, "jpg", new File(mixnfix.VisualizationExporter.getOutputDir(), name + ".jpg"));
        }
    }
}
