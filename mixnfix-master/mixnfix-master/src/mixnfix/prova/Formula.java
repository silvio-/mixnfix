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
public class Formula extends NoPapel {
    private StringBuffer _buffer = new StringBuffer();
    public Formula() {
    }

    public void append(StringBuffer buf) {
        _buffer.append(buf);
    }

    public void printXML(PrintWriter s) {
        s.print("<mat ");
        this.printPropertiesInXML(s);
        s.print(">");
        s.print(_buffer.toString());
        s.println("</mat>");
    }

    public String getTexto() {
        return _buffer.toString();
    }
    public void setTexto(String texto) {
        _buffer.setLength(0);
        _buffer.append(texto);
    }

    public NoPapel getCopy() {
        Formula f = new Formula();
        f.setTexto(_buffer.toString());
        f.addProperties(this.getProperties());
        return f;
    }

}
