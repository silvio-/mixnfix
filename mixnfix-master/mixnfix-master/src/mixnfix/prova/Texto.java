package mixnfix.prova;

import java.io.PrintWriter;

/**
 * <p>Title: Uma das coisas que pode ir num papel é um Texto</p>
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
public class Texto extends NoPapel {
    private String _texto;
    public Texto() {
    }

    public void setTexto(StringBuffer buf) {
        _texto = buf.toString();
    }

    public void setTexto(String texto) {
        _texto = texto;
    }

    public void printXML(PrintWriter s) {
        s.print(_texto);
    }

    public String getTexto() {
        return _texto;
    }
    public NoPapel getCopy() {
        Texto t = new Texto();
        t.setTexto(_texto);
        t.addProperties(this.getProperties());
        return t;
    }
}
