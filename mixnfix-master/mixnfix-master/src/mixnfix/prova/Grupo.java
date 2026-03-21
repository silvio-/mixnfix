package mixnfix.prova;

import java.io.PrintWriter;
import java.util.ArrayList;

import mixnfix.Model;

/**
 * <p>Title: Grupo</p>
 *
 * <p>Description: esta classe representa um agrupamento de coleção de NoProva.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Grupo extends NoProvaPermutavel {

    private Papel _enunciado;
    public Grupo() {
        _enunciado = new Papel();
        _enunciado.setParent(this);
    }

    public void addGrupo(Grupo g) {
        this.addChild(g);
    }

    public void addQuesito(Quesito q) {
        this.addChild(q);
    }

    public Papel getEnunciado() {
        return _enunciado;
    }
    public void setEnunciado(Papel p) {
        _enunciado = p;
        _enunciado.setParent(this);
        this.fireModelUpdate();
    }

    public void printXML(PrintWriter s) {
        s.print("<grupo ");
        this.printPropertiesInXML(s);
        s.println(">");
        s.print("<enunciado ");
        if (_enunciado != null)
            _enunciado.printPropertiesInXML(s);
        s.println(">");
        if (_enunciado != null)
            _enunciado.printXML(s);
        s.println("</enunciado>");
        for (NoProva n:this.getChilds()) {
            n.printXML(s);
        }
        s.println("</grupo>");
    }

    public static final String TOTALIZADO = "totalizado";

    public boolean isTotalizado() {
        boolean result = false;
        String value = this.getProperty(TOTALIZADO);
        if (value != null && "1".equals(value))
            result = true;
        return result;
    }

    public void setTotalizado(boolean b) {
        setProperty(TOTALIZADO,b ? "1" : "0");
    }

    private float _nota;
    public float getNota() { return _nota; }
    public float avaliarNota() {
        _nota = 0;
        for (NoProva n: this.getPermutacao()) {
            if (n instanceof Quesito) {
                _nota += ((Quesito) n).avaliarNota();
            }
            else if (n instanceof Grupo) {
                _nota += ((Grupo) n).avaliarNota();
            }
        }
        return _nota;
    }

    //
    public void detalharNota(StringBuffer buffer) {
        buffer.append(this.getTag()+"\n");
        for (NoProva n: this.getPermutacao()) {
            if (n instanceof Grupo)
                ((Grupo) n).detalharNota(buffer);
            else if (n instanceof Quesito)
                ((Quesito) n).detalharNota(buffer);
        }
    }

    public void getAllNodes(ArrayList<Model> list) {
        list.add(this);
        for (Model m:this.getChilds()) {
            if (m instanceof Grupo)
                ((Grupo)m).getAllNodes(list);
            else if (m instanceof Quesito)
                ((Quesito)m).getAllNodes(list);
            else throw new RuntimeException("Problemas");
        }
    }

    public void removerQuesito(Quesito q) {
        this.removeChild(q);
    }

    public void removerGrupo(Grupo g) {
        this.removeChild(g);
    }

    public void remover() {
        Grupo g = (Grupo) this.getParent();
        g.removerGrupo(this);
    }

    public Grupo getCopy() {
        Grupo copy = new Grupo();
        copy.addProperties(this.getProperties());
        copy.setEnunciado(this.getEnunciado().getCopy());
        for (Model m: this.getChilds()) {
            if (m instanceof Quesito)
                copy.addQuesito(((Quesito) m).getCopy());
            else if (m instanceof Grupo)
                copy.addGrupo(((Grupo) m).getCopy());
        }
        return copy;
    }



    /**
     * fire the current structure of the objects
     */
    public void fireInitStructure() {
        // System.out.println("My tag is: "+this.getTag());
        ArrayList<Model> l = new ArrayList<Model>(this.getChilds());
        this.fireNodesLoaded(l);
        for (Model m: l) {
           ((NoProva) m).fireInitStructure();
        }
    }

    /**
     * fire the current structure of the objects
     */
    public void fireRemoveStructureSimulation() {
        for (NoProva m : this.getChilds()) {
            m.fireRemoveStructureSimulation();
        }
        this.getParent().fireNodeRemoved(this);
    }
}
