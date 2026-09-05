package mixnfix;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;

import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.GeradorFolhaRespostas;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.Parser;
import mixnfix.prova.ProvaStructure;

/** Robustness test: warp an exam picture with strong perspectives and check the detection. */
public class WarpTest {

    public static ProvaStructure loadProva(String provaFile) throws Exception {
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

    public static ProvaStructure prova;

    static double[] detect(BufferedImage image) {
        GeradorFolhaRespostas g = new GeradorFolhaRespostas(prova);
        CellMap map = g.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
        for (ControlPoint cp : map.getControlPoints())
            MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
        for (Quadrilateral q : map.getQuads())
            MFI2Java.addQuad(q.getP0().getId(), q.getP1().getId(), q.getP2().getId(), q.getP3().getId());
        int thresholds[] = {50,60,70,80,90,100,110,120};
        MFI2Java.setConstraints(thresholds, 2,15, 2,15, 4,120, 0.4, 2, 150, 5, 30, 1.0, 0.9, 0, 20, 0,1,0,1);
        byte data[] = new byte[image.getWidth()*image.getHeight()];
        MFI2Java.loadImageToBuffer(image, data);
        double cps[] = new double[1000];
        boolean b = MFI2Java.fitToImage(data, image.getWidth(), image.getHeight(), cps);
        if (!b) return null;
        double result[] = new double[2*map.getNumControlPoints()];
        System.arraycopy(cps, 0, result, 0, result.length);
        return result;
    }

    /** warp image with the homography sending the image corners to dst (8 values) */
    static BufferedImage warp(BufferedImage src, double dst[], int w, int h) {
        double s[] = {0,0, src.getWidth(),0, src.getWidth(),src.getHeight(), 0,src.getHeight()};
        double H[] = ControlPointDetector.homographyFrom4Points(dst, s); // inverse mapping
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        double p[] = new double[2];
        for (int y=0;y<h;y++) for (int x=0;x<w;x++) {
            ControlPointDetector.apply(H, x+0.5, y+0.5, p);
            int sx = (int)Math.floor(p[0]), sy = (int)Math.floor(p[1]);
            int rgb = 0xFFFFFF;
            if (sx>=0 && sy>=0 && sx<src.getWidth()-1 && sy<src.getHeight()-1) {
                double fx = p[0]-sx, fy = p[1]-sy;
                int v = 0;
                for (int c=0;c<3;c++) {
                    double a = ((src.getRGB(sx,sy)>>(8*c))&255)*(1-fx)*(1-fy)
                             + ((src.getRGB(sx+1,sy)>>(8*c))&255)*fx*(1-fy)
                             + ((src.getRGB(sx,sy+1)>>(8*c))&255)*(1-fx)*fy
                             + ((src.getRGB(sx+1,sy+1)>>(8*c))&255)*fx*fy;
                    v |= ((int)Math.round(a)) << (8*c);
                }
                rgb = v;
            }
            out.setRGB(x,y,rgb);
        }
        return out;
    }

    public static void main(String args[]) throws Exception {
        prova = loadProva(args[0]);
        ProcessImage.VERBOSE = false;
        for (int a=1;a<args.length;a++) {
            BufferedImage src = ImageIO.read(new File(args[a]));
            double truth[] = detect(src);
            System.out.println("== " + args[a] + " reference detection " + (truth!=null ? "ok" : "FAILED"));
            if (truth == null) continue;
            int W = src.getWidth(), H = src.getHeight();
            double warps[][] = {
                {0,0, W,0, W,H, 0,H},                                   // identity
                {0.25*W,0, W,0.10*H, 0.85*W,H, 0.05*W,0.92*H},           // strong perspective
                {0,0.20*H, 0.80*W,0, W,0.85*H, 0.15*W,H},                // strong perspective 2
                {0.35*W,0.05*H, W,0.25*H, 0.70*W,0.95*H, 0,0.75*H},      // very strong
                {0.02*W,0.10*H, 0.95*W,0.02*H, 0.99*W,0.90*H, 0.08*W,H}, // mild
                {0.30*W,0.02*H, 0.98*W,0.30*H, 0.72*W,0.99*H, 0.02*W,0.70*H} // rotated+perspective
            };
            for (int k=0;k<warps.length;k++) {
                BufferedImage img = warp(src, warps[k], W, H);
                double found[] = detect(img);
                if (found == null) { System.out.println("   warp "+k+": FAILED"); continue; }
                // expected: apply the same homography to the reference points
                double s[] = {0,0, W,0, W,H, 0,H};
                double Hm[] = ControlPointDetector.homographyFrom4Points(s, warps[k]);
                double p[] = new double[2];
                double maxErr = 0, sum = 0;
                int n = truth.length/2;
                for (int i=0;i<n;i++) {
                    ControlPointDetector.apply(Hm, truth[2*i], truth[2*i+1], p);
                    double d = Math.hypot(p[0]-found[2*i], p[1]-found[2*i+1]);
                    maxErr = Math.max(maxErr, d); sum += d*d;
                }
                System.out.format("   warp %d: OK  rms=%.2f px  max=%.2f px%n", k, Math.sqrt(sum/n), maxErr);
                ImageIO.write(img, "jpg", new File("/tmp/warp_"+new File(args[a]).getName()+"_"+k+".jpg"));
            }
        }
    }
}
