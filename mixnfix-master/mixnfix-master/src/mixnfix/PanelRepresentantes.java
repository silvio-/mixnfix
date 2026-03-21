package mixnfix;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

class PanelRepresentantes extends JPanel {
    private BufferedImage _img;
    private Representantes _representantes;
    public PanelRepresentantes(String fileName) {
        try {
            _img = ImageIO.read(new File(fileName));
        }
        catch (Exception ex) {
        }

        // mount representantes set
        _representantes = new Representantes(20);
        Raster r = _img.getRaster();
        int[] pixel = new int[1];
        for (int i = 0; i < _img.getHeight(); i++)
            for (int j = 0; j < _img.getWidth(); j++) {
                r.getPixel(j, i, pixel);
                int intensity = pixel[0];
                if (intensity < 255) {
                    _representantes.addPontoSeForBom(j,i,intensity);
                }
            }
    }


    public void paint(Graphics g) {
        super.paint(g);

        if (_img == null)
            return;
        g.drawImage(_img, 0, 0, null);

        for (int i=0;i<_representantes.getSize();i++) {
            Ponto p = _representantes.getPonto(i);
            int raio = (int) _representantes.getRaio();
            g.setColor(Color.red);
            g.fillArc(p.getX()-2,p.getY()-2,5,5,0,360);
            g.drawArc(p.getX()-raio/2,p.getY()-raio/2,raio,raio,0,360);
        }
    }
}
