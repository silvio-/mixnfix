package mixnfix.prova;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: Algo que vai virar saída. Aqui estão as
 * fotos, os textos etc.</p>
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
public class Papel extends No {
    public ArrayList<NoPapel> _contents = new ArrayList<NoPapel>();
    public Papel() {
    }
    public void addChild(NoPapel np) {
        _contents.add(np);
        np.setParent(this);
    }
    public void printXML(PrintWriter s) {
        for (No n:_contents) {
            n.printXML(s);
        }
    }
    public List<NoPapel> getContents() {
        return (List<NoPapel>) _contents.clone();
    }

    public Papel getCopy() {
        Papel copy = new Papel();
        for (NoPapel np : _contents) {
            copy.addChild(np.getCopy());
        }
        return copy;
    }
}
