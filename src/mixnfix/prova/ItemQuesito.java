package mixnfix.prova;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

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
public class ItemQuesito extends NoProva {
    private Papel _enunciado;
    public ItemQuesito() {
        _enunciado = new Papel();
        _enunciado.setParent(this);
    }
    public void setEnunciado(Papel enunciado) {
        _enunciado = enunciado;
        _enunciado.setParent(this);
        this.fireModelUpdate();
    }
    public Papel getEnunciado() {
        return _enunciado;
    }
    public void printXML(PrintWriter s) {
        s.print("<item ");
        this.printPropertiesInXML(s);
        s.println(">");

        s.print("<enunciado ");
        if (_enunciado != null)
            _enunciado.printPropertiesInXML(s);
        s.println(">");
        if (_enunciado != null)
            _enunciado.printXML(s);
        s.println("</enunciado>");

        s.println("</item>");
    }


    //----------------------------------------------
    private ArrayList<Integer> _respostaAluno = new ArrayList<Integer>();
    public List<Integer> getRespostaAluno() {
        return _respostaAluno;
    }
    public void clearRespostaAluno() {
        _respostaAluno.clear();
    }
    public void addRespostaAluno(int i) {
        _respostaAluno.add(i);
    }
    //----------------------------------------------



    public List<Integer> getRespostaCorreta() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        String resposta = getProperty("resposta");
        if (resposta != null) {
            StringTokenizer st = new StringTokenizer(resposta, " ,");
            if (st.hasMoreTokens()) {
                String tk = st.nextToken().toUpperCase();
                if (tk.charAt(0) == 'V') result.add(0);
                else if (tk.charAt(0) == 'F') result.add(1);
            }
        }
        return result;
    }

    public static final int BLANK = 0;
    public static final int CORRECT = 1;
    public static final int WRONG = 2;
    public int avaliarItemFalsoVerdadeiro() { // devolve
        // {---------- falso-verdadeiro ----------
        List<Integer> list = this.getRespostaAluno();
        List<Integer> respostaCorreta = this.getRespostaCorreta();
        if (respostaCorreta.size() == 0) {
            _statusAvaliacao = CORRECT;
        }
        else if (list.size() > 1) {
            _statusAvaliacao = WRONG;
        }
        else if (list.size() == 0) {
            _statusAvaliacao = BLANK; //
        }
        else {
            if (respostaCorreta.get(0) == list.get(0))
                _statusAvaliacao = CORRECT;
            else
                _statusAvaliacao = WRONG;
        }
        return _statusAvaliacao;
        // ---------- falso-verdadeiro ----------}
    }
    private int _statusAvaliacao;
    public int getStatusAvaliacao() {
        return _statusAvaliacao;
    }

    public void fireInitStructure() {}


    public Quesito getQuesito() {
        return (Quesito)this.getParent();
    }

    public void remover() {
        this.getQuesito().removerItemQuesito(this);
    }

    public ItemQuesito getCopy() {
        ItemQuesito copy = new ItemQuesito();
        copy.addProperties(this.getProperties());
        copy.setEnunciado(this.getEnunciado().getCopy());
        return copy;
    }

    /**
     * fire the current structure of the objects
     */
    public void fireRemoveStructureSimulation() {
        this.getParent().fireNodeRemoved(this);
    }

}
