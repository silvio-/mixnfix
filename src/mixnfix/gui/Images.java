package mixnfix.gui;

import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class Images {
    private static final String _root = "images/";
    public static final ImageIcon Aluno16x16 = Images.getImageIcon(_root+"aluno16x16.png");
    public static final ImageIcon Curso16x16 = Images.getImageIcon(_root+"curso16x16.png");
    public static final ImageIcon Turma16x16 = Images.getImageIcon(_root+"turma16x16.png");
    public static final ImageIcon Correcao16x16 = Images.getImageIcon(_root+"correcao-16x16.png");
    public static final ImageIcon Prova16x16 = Images.getImageIcon(_root+"prova-16x16.png");
    public static final ImageIcon Prova16x16_Modificada = Images.getImageIcon(_root+"prova16x16.png");
    //public static final ImageIcon Prova16x16_Modificada = _root+"prova16x16-modificada.png");
    public static final ImageIcon Instituicao16x16 = Images.getImageIcon(_root+"instituicao16x16.png");
    public static final ImageIcon Folder16x16 = Images.getImageIcon(_root+"folder16x16.png");
    public static final ImageIcon Importacao = Images.getImageIcon(_root+"importacao.png");
    public static final ImageIcon Correcao = Images.getImageIcon(_root+"correcao-32x32.png");
    public static final ImageIcon Grupo = Images.getImageIcon(_root+"grupo-16x16.png");
    public static final ImageIcon Quesito = Images.getImageIcon(_root+"quesito-16x16.png");
    public static final ImageIcon Gravar = Images.getImageIcon(_root+"gravar.png");
    public static final ImageIcon Load = Images.getImageIcon(_root+"load.png");
    public static final ImageIcon Mais = Images.getImageIcon(_root+"mais.png");
    public static final ImageIcon Menos = Images.getImageIcon(_root+"menos.png");
    public static final ImageIcon SetaCima = Images.getImageIcon(_root+"setaCima.png");
    public static final ImageIcon SetaBaixo = Images.getImageIcon(_root+"setaBaixo.png");
    public static final ImageIcon SetaDireira = Images.getImageIcon(_root+"setaDireita.png");
    public static final ImageIcon SetaEsquerda = Images.getImageIcon(_root+"setaEsquerda.png");
    public static final ImageIcon SetaDuplaCima = Images.getImageIcon(_root+"setaDuplaCima.png");
    public static final ImageIcon SetaDuplaBaixo = Images.getImageIcon(_root+"setaDuplaBaixo.png");
    public static final ImageIcon SetaDuplaDireira = Images.getImageIcon(_root+"setaDuplaDireita.png");
    public static final ImageIcon SetaDuplaEsquerda = Images.getImageIcon(_root+"setaDuplaEsquerda.png");
    public static final ImageIcon Sort = Images.getImageIcon(_root+"sort.png");
    public static final ImageIcon Update = Images.getImageIcon(_root+"update.png");


    public static final ImageIcon QuesitoAdd = Images.getImageIcon(_root+"quesito-mais-32x32.png");
    public static final ImageIcon QuesitoRemove = Images.getImageIcon(_root+"quesito-menos-32x32.png");
    public static final ImageIcon GrupoAdd = Images.getImageIcon(_root+"grupo-mais-32x32.png");
    public static final ImageIcon GrupoRemove = Images.getImageIcon(_root+"grupo-menos-32x32.png");

    public static final ImageIcon ItemQuesito = Images.getImageIcon(_root+"item-16x16.png");
    public static final ImageIcon ItemQuesitoAdd = Images.getImageIcon(_root+"item-mais-32x32.png");
    public static final ImageIcon ItemQuesitoRemove = Images.getImageIcon(_root+"item-menos-32x32.png");

    public static final ImageIcon ColetaQuestionario = Images.getImageIcon(_root+"coleta16x16.png");

    public static final ImageIcon Corrigir = Images.getImageIcon(_root+"corrigir.png");

    public static final ImageIcon Calibrar = Images.getImageIcon(_root+"calibrar.png");

    public static final ImageIcon Lock = Images.getImageIcon(_root+"lock-32x32.png");

    public static final ImageIcon MIXnFIX_transparent = Images.getImageIcon(_root+"mnf100x100-transparent.png");

    public static final BufferedImage SmallIcon = Images.getBufferedImage(_root + "mnf16x16.png");
    
    // get an image from the given filename
    private static BufferedImage getBufferedImage(String filename) {

        BufferedImage image = null;
		try {
			image = ImageIO.read(new File(filename));
			return image;
		} catch (IOException e1) { /* not in the normal file system */ }
        
		URL url = null;
		try {
			url = Images.class.getResource("/"+filename);
			image = ImageIO.read(url);
		} catch (Exception e) { throw new RuntimeException("[Problem] image " + url + " not found"); }

        return image;
    }
    
    // get an image from the given filename
    private static ImageIcon getImageIcon(String filename) {

        // to read from file
        ImageIcon icon = new ImageIcon(filename);

        // try to read from URL
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            try {
                URL url = new URL(filename);
                icon = new ImageIcon(url);
            } catch (Exception e) { /* not a url */ }
        }

        // in case file is inside a .jar
        if ((icon == null) || (icon.getImageLoadStatus() != MediaTracker.COMPLETE)) {
            URL url = Images.class.getResource("/"+filename);
            if (url == null) throw new RuntimeException("[Problem] image " + filename + " not found");
            icon = new ImageIcon(url);
        }

        return icon;
    }

}
