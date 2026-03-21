package mixnfix.prova;

import java.io.PrintWriter;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Imagem extends NoPapel {

    public Imagem() {
    }

    public void printXML(PrintWriter s) {
        s.print("<figura ");
        this.printPropertiesInXML(s);
        s.print("/>");
    }

    public NoPapel getCopy() {
        Imagem i = new Imagem();
        i.addProperties(this.getProperties());
        return i;
    }
}
