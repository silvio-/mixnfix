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
public class Estilo extends NoPapel {
    private StringBuffer _buffer = new StringBuffer();
    public Estilo() {
    }
    public void append(StringBuffer buf) {
        _buffer.append(buf);
    }
    public void printXML(PrintWriter s) {
        s.print("<estilo ");
        this.printPropertiesInXML(s);
        s.print(">");
        s.print(_buffer.toString());
        s.println("</estilo>");
    }
    public String getTexto() {
        return _buffer.toString();
    }

    public void setTexto(String texto) {
        _buffer.setLength(0);
        _buffer.append(texto);
    }

    public boolean isBold() {
        String fonte = this.getProperty("fonte");
        if (fonte != null && fonte.indexOf("bold") != -1)
            return true;
        return false;
    }

    public boolean isItalic() {
        String fonte = this.getProperty("fonte");
        if (fonte != null && fonte.indexOf("italic") != -1)
            return true;
        return false;
    }

    public NoPapel getCopy() {
        Estilo e = new Estilo();
        e.setTexto(this.getTexto());
        e.addProperties(this.getProperties());
        return e;
    }
}
