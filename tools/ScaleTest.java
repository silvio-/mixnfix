package mixnfix;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.File;
import javax.imageio.ImageIO;
import mixnfix.folharesposta.*;
import mixnfix.prova.ProvaStructure;

public class ScaleTest {
    public static void main(String a[]) throws Exception {
        ProvaStructure prova = WarpTest.loadProva(a[0]);
        WarpTest.prova = prova;
        ProcessImage.VERBOSE = false;
        for (int i=1;i<a.length;i++) {
            BufferedImage src = ImageIO.read(new File(a[i]));
            for (double sc: new double[]{2.0, 3.0}) {
                int w=(int)(src.getWidth()*sc), h=(int)(src.getHeight()*sc);
                BufferedImage big = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
                Graphics2D g = big.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(src,0,0,w,h,null); g.dispose();
                GeradorFolhaRespostas gg = new GeradorFolhaRespostas(prova);
                CellMap map = gg.getCellMapFixo();
                MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
                for (ControlPoint cp : map.getControlPoints())
                    MFI2Java.addControlPoint(cp.getId(), cp.getX()-map.getX0(), cp.getY()-map.getY0());
                for (Quadrilateral q : map.getQuads())
                    MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
                int th[] = {50,60,70,80,90,100,110,120};
                int mw=(int)Math.ceil(15*sc), mnp=(int)Math.ceil(120*sc*sc);
                MFI2Java.setConstraints(th, 2,mw, 2,mw, 4,mnp, 0.4, 2, 150*sc, 5, 30, 1.0, 0.9, 0, 20*sc, 0,1,0,1);
                byte data[] = MFI2Java.ensureBuffer(null, big);
                MFI2Java.loadImageToBuffer(big, data);
                double cps[] = new double[1000];
                long t0=System.nanoTime();
                boolean ok = MFI2Java.fitToImage(data, w, h, cps);
                long t1=System.nanoTime();
                System.out.format("%s scale %.1f (%dx%d): %s  %.1f ms%n", a[i], sc, w, h, ok?"OK":"FAILED",(t1-t0)/1e6);
            }
        }
    }
}
