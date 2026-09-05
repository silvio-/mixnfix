package mixnfix;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Writes the verification images produced by the grading module: the grey
 * scale (black and white) frame of the exam with the detected control
 * points (alignment pixels) painted in yellow on top of it.
 *
 * <p>The destination directory is <code>/app/output</code> by default and
 * can be changed with the system property
 * <code>mixnfix.output.dir</code>. Every processed frame is saved as
 * <code>&lt;name&gt;.jpg</code>, so a whole batch of exams leaves one
 * image per page.</p>
 */
public class VisualizationExporter {

    public static final String OUTPUT_DIR_PROPERTY = "mixnfix.output.dir";

    /** enable/disable the automatic export of the verification images. */
    public static boolean ENABLED = true;

    public static String getOutputDir() {
        return System.getProperty(OUTPUT_DIR_PROPERTY, "/app/output");
    }

    /** grey scale copy of a picture (as used by the image processing). */
    public static BufferedImage toGrayScale(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        boolean color = (image.getType() != BufferedImage.TYPE_BYTE_GRAY);
        int rgb[] = { 0, 0, 0, 0 };
        Raster source = image.getData();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                source.getPixel(j, i, rgb);
                int g = (color ? (rgb[0] * 30 + rgb[1] * 59 + rgb[2] * 11) / 100 : rgb[0]);
                result.setRGB(j, i, (g << 16) | (g << 8) | g);
            }
        }
        return result;
    }

    /**
     * Compose the grey scale exam frame with the control points painted in
     * yellow and save it as a JPG file in the output directory.
     *
     * @param image         the picture of the exam
     * @param controlPoints x,y image coordinates of the control points
     * @param numPoints     number of control points in the array
     * @param name          file name, without extension
     * @return the written file, or null when nothing was written
     */
    public static File saveComposed(BufferedImage image, double controlPoints[], int numPoints, String name) {
        if (!ENABLED || image == null)
            return null;
        try {
            BufferedImage composed = toGrayScale(image);

            Graphics2D g2 = composed.createGraphics();
            g2.setColor(Color.YELLOW);
            for (int i = 0; i < numPoints; i++) {
                int x = (int) Math.round(controlPoints[2 * i]);
                int y = (int) Math.round(controlPoints[2 * i + 1]);
                g2.fillRect(x - 2, y - 2, 5, 5);
                g2.drawLine(x - 6, y, x + 6, y);
                g2.drawLine(x, y - 6, x, y + 6);
            }
            g2.dispose();

            File dir = new File(getOutputDir());
            dir.mkdirs();
            File out = new File(dir, baseName(name) + ".jpg");
            ImageIO.write(composed, "jpg", out);
            System.out.println("composed verification image written to " + out.getAbsolutePath());
            return out;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /** file name without directory and without extension */
    public static String baseName(String name) {
        if (name == null || name.length() == 0)
            return "composed_result";
        String base = new File(name).getName();
        int dot = base.lastIndexOf('.');
        if (dot > 0)
            base = base.substring(0, dot);
        return base;
    }
}
