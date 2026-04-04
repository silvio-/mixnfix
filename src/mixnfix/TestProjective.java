package mixnfix;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 * Standalone test for the piecewise projective deformation correction.
 * Loads an image and a .node/.ele file, runs fitToImage, and reports results.
 *
 * Usage: java mixnfix.TestProjective <image> <node_file> <ele_file>
 */
public class TestProjective {

    public static void main(String[] args) throws Exception {

        if (args.length < 2) {
            System.out.println("Usage: java mixnfix.TestProjective <image> <node_file> [ele_file]");
            System.out.println("  ele_file is optional (quads are built from the grid structure)");
            return;
        }

        String imageFile = args[0];
        String nodeFile = args[1];
        String eleFile = args.length >= 3 ? args[2] : null;

        // --- Load image ---
        System.out.println("Loading image: " + imageFile);
        BufferedImage img = ImageIO.read(new File(imageFile));
        int w = img.getWidth();
        int h = img.getHeight();
        System.out.format("Image size: %d x %d\n", w, h);

        // Convert to grayscale
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
            BufferedImage gray = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
            ColorConvertOp op = new ColorConvertOp(
                img.getColorModel().getColorSpace(),
                ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(img, gray);
            img = gray;
        }

        byte[] data = new byte[w * h];
        img.getRaster().getDataElements(0, 0, w, h, data);

        // --- Read .node file ---
        System.out.println("Loading control points: " + nodeFile);
        BufferedReader br = new BufferedReader(new FileReader(nodeFile));
        String line = br.readLine().trim();
        StringTokenizer st = new StringTokenizer(line);
        int numCP = Integer.parseInt(st.nextToken());
        System.out.format("Number of control points: %d\n", numCP);

        int[] cpIds = new int[numCP];
        double[] cpX = new double[numCP];
        double[] cpY = new double[numCP];

        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (int i = 0; i < numCP; i++) {
            line = br.readLine().trim();
            st = new StringTokenizer(line);
            // format: id x y [boundary_marker]  OR  boundary_marker x y
            String tok1 = st.nextToken();
            String tok2 = st.nextToken();
            String tok3 = st.nextToken();

            cpIds[i] = i + 1; // sequential ID
            cpX[i] = Double.parseDouble(tok2);
            cpY[i] = Double.parseDouble(tok3);

            if (cpX[i] < minX) minX = cpX[i];
            if (cpX[i] > maxX) maxX = cpX[i];
            if (cpY[i] < minY) minY = cpY[i];
            if (cpY[i] > maxY) maxY = cpY[i];
        }
        br.close();

        double mapWidth = maxX - minX;   // should be 176
        double mapHeight = maxY - minY;  // should be 180
        System.out.format("Map dimensions: %.1f x %.1f (from x=[%.1f,%.1f] y=[%.1f,%.1f])\n",
            mapWidth, mapHeight, minX, maxX, minY, maxY);

        // --- Create CellMap ---
        MFI2Java.newCellMap(mapWidth, mapHeight, 50, 1000);

        // Add control points (normalize so origin is at minX, minY)
        for (int i = 0; i < numCP; i++) {
            double x = cpX[i] - minX;
            double y = cpY[i] - minY;
            MFI2Java.addControlPoint(cpIds[i], x, y);
            System.out.format("  CP %2d: (%.1f, %.1f)\n", cpIds[i], x, y);
        }

        // Add triangles from .ele file if provided
        if (eleFile != null) {
            System.out.println("Loading triangles: " + eleFile);
            br = new BufferedReader(new FileReader(eleFile));
            line = br.readLine().trim();
            st = new StringTokenizer(line);
            int numTri = Integer.parseInt(st.nextToken());
            System.out.format("Number of triangles: %d\n", numTri);

            for (int i = 0; i < numTri; i++) {
                line = br.readLine().trim();
                st = new StringTokenizer(line);
                st.nextToken(); // triangle id
                int p1 = Integer.parseInt(st.nextToken());
                int p2 = Integer.parseInt(st.nextToken());
                int p3 = Integer.parseInt(st.nextToken());
                MFI2Java.addTriangle(p1, p2, p3);
            }
            br.close();
        }

        // --- Set constraints (relaxed for severely deformed image) ---
        int[] thresholds = {50, 60, 70, 80, 90, 100, 110, 120};
        MFI2Java.setConstraints(
            thresholds,
            3,    // minPixelWidth
            30,   // maxPixelWidth (relaxed)
            3,    // minPixelHeight
            30,   // maxPixelHeight (relaxed)
            3,    // minNumPixels
            200,  // maxNumPixels (relaxed)
            0.3,  // pixelDensity (relaxed)
            4,    // numClosest (relaxed)
            100,  // minSide (relaxed)
            15,   // difAngle (relaxed for perspective)
            60,   // targetRadius (relaxed)
            1.475,// correctSideRatio
            1.5,  // sideRatioTolerance (relaxed)
            0,    // phase
            30,   // controlPointsRadius (relaxed)
            0, 1, 0, 1 // margins (fractions of image: left, right, top, bottom)
        );

        // --- Run fitToImage ---
        System.out.println("\n=== Running fitToImage ===\n");
        double[] controlPoints = new double[1000];
        MFI2Java.loadImageToBuffer(img, data);
        boolean found = MFI2Java.fitToImage(data, w, h, controlPoints);

        if (found) {
            System.out.println("\n=== SUCCESS: All control points found! ===\n");
            ProcessImage.CellMap cellMap = MFI2Java._cellMap;
            System.out.format("Quads built: %d\n", cellMap.numQuads);

            System.out.println("\nControl point positions (theoretical -> practical):");
            for (int i = 0; i < cellMap.numControlPoints; i++) {
                ProcessImage.ControlPoint cp = cellMap.cps[i];
                System.out.format("  CP %2d: (%.1f, %.1f) -> (%.1f, %.1f)\n",
                    cp.id, cp.x, cp.y, cp.xx, cp.yy);
            }

            // Test mapping a few points
            System.out.println("\nSample point mappings (theoretical -> practical):");
            double[] mapping = new double[2];
            double[] testPoints = {0, 0, mapWidth/2, mapHeight/2, mapWidth, mapHeight};
            for (int i = 0; i < testPoints.length; i += 2) {
                ProcessImage.findMappingOfPointByQuads(cellMap,
                    testPoints[i], testPoints[i+1], mapping, 0);
                System.out.format("  (%.1f, %.1f) -> (%.1f, %.1f)\n",
                    testPoints[i], testPoints[i+1], mapping[0], mapping[1]);
            }
        } else {
            System.out.println("\n=== FAILED: Could not find all control points ===\n");
            System.out.println("Try relaxing constraints or check image quality.");
        }
    }
}
