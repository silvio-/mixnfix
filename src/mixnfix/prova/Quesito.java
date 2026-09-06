package mixnfix.prova;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mixnfix.Model;

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
public class Quesito extends NoProvaPermutavel {
    private Papel _enunciado;
    private Papel _solucao;

    public Quesito() {
        _enunciado = new Papel();
        _enunciado.setParent(this);
        _solucao = new Papel();
        _solucao.setParent(this);
    }

    public int getItensCount() {
        return this.getChildCount();
    }

    public List<ItemQuesito> getItens() {
        ArrayList<ItemQuesito> list = new ArrayList<ItemQuesito>(this.getItensCount());
        for (int i=0;i<this.getItensCount();i++) {
            ItemQuesito iq = this.getItem(i);
            list.add(iq);
        }
        return list;
    }

    public ItemQuesito getItem(int index) {
        return (ItemQuesito) this.getChild(index);
    }

    public ItemQuesito getItemOnPermutation(int index) {
        return (ItemQuesito) this.getItemOnPermutacao(index);
    }

    public Papel getEnunciado() {
        return _enunciado;
    }

    public void setEnunciado(Papel p) {
        _enunciado = p;
        _enunciado.setParent(this);
        this.fireModelUpdate();
    }

    public void setSolucao(Papel p) {
        _solucao = p;
        _solucao.setParent(this);
        this.fireModelUpdate();
    }

    public Papel getSolucao() {
        return _solucao;
    }

    public double getValorAcerto() {
        String st=this.getProperty("acerto");
        if (st == null)
            return 1.0f;
        else
            return mixnfix.Library.parseFloat(st);
    }

    public double getValorFalha() {
        String st=this.getProperty("falha");
        if (st == null)
            return 0.0f;
        else {
            return mixnfix.Library.parseFloat(st);
        }
    }

    public static final int TIPO_DESCONHECIDO = 0;
    public static final int TIPO_ALTERNATIVAS = 1;
    public static final int TIPO_FALSO_VERDADEIRO = 2;
    public static final int TIPO_NUMERICO_99 = 3;
    public static final int TIPO_SUBJETIVA_5 = 4;
    public static final int TIPO_SUBJETIVA_9 = 5;

    private Integer _tipo;
    public int getTipo() {
        if (_tipo == null) {
            String st = this.getProperty("tipo");
            if (st == null)
                _tipo=TIPO_DESCONHECIDO;
            else if ("alternativas".equals(st))
                _tipo=TIPO_ALTERNATIVAS;
            else if ("falso/verdadeiro".equals(st))
                _tipo=TIPO_FALSO_VERDADEIRO;
            else if ("numérico_99".equals(st))
                _tipo=TIPO_NUMERICO_99;
            else if ("subjetiva_5".equals(st))
                _tipo=TIPO_SUBJETIVA_5;
            else if ("subjetiva_9".equals(st))
                _tipo=TIPO_SUBJETIVA_9;
            else
                _tipo=TIPO_DESCONHECIDO;
        }
        return _tipo;
    }

    /**
     * Para os quesitos do tipo numérico.
     * @return int
     */
    public int getNumDigitosQuesitoNumerico() {
        String st = this.getProperty("numerico.digitos");
        if (st != null) {
            try {
                int d = Integer.parseInt(st);
                return d;
            }
            catch (NumberFormatException ex) {
                return 2;
            }
        }
        return 2;
    }

    /**
     * Para os quesitos do tipo numérico.
     * @return int
     */
    public void setNumDigitosQuesitoNumerico(int d) {
        this.setProperty("numerico.digitos",""+d);
        this.fireModelUpdate();
    }

    public void setTipo(int id) {
        switch (id) {
            case TIPO_ALTERNATIVAS:
                _tipo = TIPO_ALTERNATIVAS;
                this.setProperty("tipo","alternativas");
                break;
            case TIPO_FALSO_VERDADEIRO:
                _tipo = TIPO_FALSO_VERDADEIRO;
                this.setProperty("tipo","falso/verdadeiro");
                break;
            case TIPO_NUMERICO_99:
                _tipo = TIPO_NUMERICO_99;
                this.setProperty("tipo","numérico_99");
                break;
            case TIPO_SUBJETIVA_5:
                _tipo = TIPO_SUBJETIVA_5;
                this.setProperty("tipo","subjetiva_5");
                break;
            case TIPO_SUBJETIVA_9:
                _tipo = TIPO_SUBJETIVA_9;
                this.setProperty("tipo","subjetiva_9");
                break;
        }
        this.fireModelUpdate();
    }

    public void printXML(PrintWriter s) {
        s.print("<quesito ");
        this.printPropertiesInXML(s);
        s.println(">");

        s.print("<enunciado ");
        if (_enunciado != null)
            _enunciado.printPropertiesInXML(s);
        s.println(">");
        if (_enunciado != null)
            _enunciado.printXML(s);
        s.println("</enunciado>");

        s.print("<solucao ");
        if (_solucao != null)
            _solucao.printPropertiesInXML(s);
        s.println(">");
        if (_solucao != null)
            _solucao.printXML(s);
        s.println("</solucao>");
        for (ItemQuesito i: getItens()) {
            i.printXML(s);
        }
        s.println("</quesito>");
    }

    //--- Resposta do Aluno ------------------------
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
    //--- Resposta do Aluno ------------------------

    /**
     * Obter a resposta do aluno normalizada
     */
    public List<Integer> getRespostaAlunoNormalized() {

        // {--------- ALTERNATIVAS --------
        // inteiros correspondentes às alternativas permutadas corretas
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            ArrayList<Integer> respostaAlunoNormalizada = new ArrayList<Integer>();
            for (int i:_respostaAluno) {
                ItemQuesito iq = this.getItemOnPermutation(i);
                int in = this.index(iq);
                respostaAlunoNormalizada.add(in);
            }
            return respostaAlunoNormalizada;
        }
        //  -------- ALTERNATIVAS --------- }

        // {-------- NUMERICO_99 ----------
        if (this.getTipo() == TIPO_NUMERICO_99) {
            return getRespostaAluno();
        }
        // -------- NUMERICO_99 ----------}

        return null;
    }


    public List<Integer> getRespostaCorretaWithinPermutation() {
        ArrayList<Integer> result = new ArrayList<Integer>();

        // check if there is resposta
        String respostaSt = getProperty("resposta");
        if (respostaSt == null)
            return result;
        // check if there is resposta

        // {--------- ALTERNATIVAS --------
        // inteiros correspondentes às alternativas permutadas corretas
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            StringTokenizer st = new StringTokenizer(respostaSt, " ,");
            while (st.hasMoreTokens()) {
                String tk = st.nextToken().toUpperCase();
                int index = (int)tk.charAt(0) - (int)'A';
                if (index < this.getItensCount()) {
                    int i = this.indexOnPermutacao(this.getItem(index));
                    if (i >= 0)
                        result.add(i);
                }
            }
        }
        //  -------- ALTERNATIVAS --------- }


        // {-------- NUMERICO_99 ----------
        // inteiros correspondentes aos valores corretos
        if (this.getTipo() == TIPO_NUMERICO_99) {
            StringTokenizer st = new StringTokenizer(respostaSt, " ,");
            while (st.hasMoreTokens()) {
                try {
                    int value = Integer.parseInt(st.nextToken());
                    result.add(value);
                }
                catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        }
        // -------- NUMERICO_99 ----------}

        return result;
    }


    public List<Integer> getRespostaCorreta() {
        ArrayList<Integer> result = new ArrayList<Integer>();

        // {--------- ALTERNATIVAS --------
        // inteiros correspondentes às alternativas permutadas corretas
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            String resposta = getProperty("resposta");
            if (resposta != null) {
                StringTokenizer st = new StringTokenizer(resposta, " ,");
                while (st.hasMoreTokens()) {
                    String tk = st.nextToken().toUpperCase();
                    int index = (int) tk.charAt(0) - (int) 'A';
                    result.add(index);
                }
            }
        }
        //  -------- ALTERNATIVAS --------- }


        // {-------- NUMERICO_99 ----------
        // inteiros correspondentes aos valores corretos
        if (this.getTipo() == TIPO_NUMERICO_99) {
            String resposta = getProperty("resposta");

            if (resposta != null) {
                StringTokenizer st = new StringTokenizer(resposta, " ,");
                while (st.hasMoreTokens()) {
                    try {
                        int value = Integer.parseInt(st.nextToken());
                        result.add(value);
                    }
                    catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        // -------- NUMERICO_99 ----------}

        return result;
    }



    private double _nota;
    public double getNota() { return _nota; }
    public double avaliarNota() {
        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            List<Integer> listCorreta = this.getRespostaCorretaWithinPermutation();
            if (listCorreta.size() == 0) { // not defined
                _nota = this.getValorAcerto(); // SE NAO HÁ GABARITO CONSIDERAR CERTO
            }
            else if (_respostaAluno.size() == 0) {
                _nota = 0;
            }
            else {
                if (listCorreta.containsAll(_respostaAluno)) { // acerto
                    _nota = this.getValorAcerto();
                }
                else { // erro
                    _nota = this.getValorFalha();
                }
            }
        }
        // ----------- ALTERNATIVAS ---------------}

        // {----------- NUMERICO ---------------
        if (this.getTipo() == TIPO_NUMERICO_99) {
            List<Integer> listCorreta = this.getRespostaCorretaWithinPermutation();
            if (listCorreta.size() == 0) {
                _nota = this.getValorAcerto(); // SE NAO HÁ GABARITO CONSIDERAR CERTO
            }
            else {
                if (_respostaAluno.size() == 0) { // not defined
                    _nota = 0;
                }
                else if (listCorreta.containsAll(_respostaAluno)) { // acerto
                    _nota = this.getValorAcerto();
                }
                else { // erro
                    _nota = this.getValorFalha();
                }
            }
        }
        // ----------- NUMERICO ---------------}

        // {----------- SUBJETIVO ---------------
        else if (this.getTipo() == TIPO_SUBJETIVA_5 || this.getTipo() == TIPO_SUBJETIVA_9) {
            if (_respostaAluno.size() != 1) {
                _nota = 0;
            }
            else {
                int i = _respostaAluno.get(0);
                int divisor = (this.getTipo() == TIPO_SUBJETIVA_9) ? 8 : 4;
                _nota = (i * this.getValorAcerto()) / (double) (divisor);
            }
        }
        //  ----------- SUBJETIVO ---------------}

        // {----------- FALSO_VERDADEIRO ---------------
        else if (this.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            int erros = 0;
            int acertos = 0;
            int brancos = 0;
            for (Object o: this.getChildsNoClone()) {
                ItemQuesito iq = (ItemQuesito) o;
                int status = iq.avaliarItemFalsoVerdadeiro();
                if (status == ItemQuesito.BLANK)
                    brancos++;
                else if (status == ItemQuesito.CORRECT)
                    acertos++;
                else if (status == ItemQuesito.WRONG)
                    erros++;
            }
			
			_nota = (acertos * this.getValorAcerto())/ (double) this.getItensCount();
			_nota += (erros * this.getValorFalha())/ (double) this.getItensCount();
			if(_nota < 0) _nota = 0;
        }
        //  ----------- FALSO_VERDADEIRO ---------------}
        return _nota;
    }


    public static final int BLANK = 0;
    public static final int CORRECT = 1;
    public static final int WRONG = 2;
    public int avaliarCorretude() { // devolve

        //  ----------- FALSO_VERDADEIRO ---------------}
        if (this.getTipo() == TIPO_FALSO_VERDADEIRO)
            throw new RuntimeException("Quesito falso verdadeiro não tem status único");
        //  ----------- FALSO_VERDADEIRO ---------------}

        int result=WRONG;

        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            List<Integer> listCorreta = this.getRespostaCorretaWithinPermutation();
            if (listCorreta.size() == 0) { // not defined
                result = CORRECT;
            }
            else if (_respostaAluno.size() == 0) {
                result = BLANK;
            }
            else {
                if (listCorreta.containsAll(_respostaAluno)) { // acerto
                    result = CORRECT;
                }
                else { // erro
                    result = WRONG;
                }
            }
        }
        // ----------- ALTERNATIVAS ---------------}

        // {----------- NUMERICO ---------------
        else if (this.getTipo() == TIPO_NUMERICO_99) {
            List<Integer> listCorreta = this.getRespostaCorretaWithinPermutation();
            if (listCorreta.size() == 0) {
                result = CORRECT; // SE NAO HÁ GABARITO CONSIDERAR CERTO
            }
            else {
                if (_respostaAluno.size() == 0) { // not defined
                    result = BLANK;
                }
                else if (listCorreta.containsAll(_respostaAluno)) { // acerto
                    result = CORRECT;
                }
                else { // erro
                    result = WRONG;
                }
            }
        }
        // ----------- NUMERICO ---------------}

        return result;
    }



    //
    public void detalharNota(StringBuffer buffer) {
        String quesito =(ProvaStructure.DETALHAR_NOTA_QUESITO++)+".";
        String student = "";
        String correct = "";
        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            for (int i:_respostaAluno) {
                student += (char)('A'+i);
            }

            correct+="(";
            for (int i:getRespostaCorretaWithinPermutation()) {
                correct+=(char)('A'+i);
            }
            correct+=")";
        }
        // ----------- ALTERNATIVAS ---------------}


        // {----------- TIPO_NUMERICO_99 ---------------
        if (this.getTipo() == TIPO_NUMERICO_99) {
            boolean first = true;
            for (int i:_respostaAluno) {
                if (!first) student+=',';
                student+=i;
                first = false;
            }

            correct+="(";
            first = true;
            for (int i:getRespostaCorretaWithinPermutation()) {
                if (!first) correct+=',';
                correct+=i;
                first = false;
            }
            correct+=")";
        }
        // ----------- TIPO_NUMERICO_99 ---------------}

        // {----------- SUBJETIVO ---------------
        if (this.getTipo() == TIPO_SUBJETIVA_5 || this.getTipo() == TIPO_SUBJETIVA_9) {
            if (_respostaAluno.size() > 0) {
                int i = _respostaAluno.get(0);
                int divisor = (this.getTipo() == TIPO_SUBJETIVA_9) ? 8 : 4;
                student+=i+"/"+divisor;
            }
        }
        // ----------- SUBJETIVO ---------------}

        // {----------- TIPO_FALSO_VERDADEIRO ---------------
        if (this.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            for (NoProva n: this.getPermutacao()) {
                ItemQuesito i = (ItemQuesito) n;
                List<Integer> list = i.getRespostaAluno();
                if (list.size() == 0)
                    student+='_';
                else {
                    int v = list.get(0);
                    if (v == 0) student+='V';
                    else if (v == 1) student+='F';
                    else student+='?';
                }
            }

            correct+='(';
            for (NoProva n: this.getPermutacao()) {
                ItemQuesito i = (ItemQuesito) n;
                List<Integer> list = i.getRespostaCorreta();
                if (list.size() == 0)
                    correct+='_';
                else {
                    int v = list.get(0);
                    if (v == 0) correct+='V';
                    else if (v == 1) correct+='F';
                    else correct+='?';
                }
            }
            correct+=')';
        }
        // ----------- TIPO_FALSO_VERDADEIRO ---------------}

        buffer.append(String.format("%-5s%-10s%-10s%5.3f",quesito,student,correct,this.getNota()));
        buffer.append("\n");
    }

    public String getTextRespostaAluno() {
        String correct ="";

        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            for (int i:getRespostaAluno()) {
                correct+=(char)('A'+i);
            }
        }
        // ----------- ALTERNATIVAS ---------------}

        // {----------- TIPO_NUMERICO_99 ---------------
        else if (this.getTipo() == TIPO_NUMERICO_99) {
            boolean first = true;
            for (int i:getRespostaAluno()) {
                if (!first) correct+=',';
                correct+=i;
                first = false;
            }
        }
        // ----------- TIPO_NUMERICO_99 ---------------}

        // {----------- SUBJETIVO ---------------
        else if (this.getTipo() == TIPO_SUBJETIVA_5 || this.getTipo() == TIPO_SUBJETIVA_9) {
        }
        // ----------- SUBJETIVO ---------------}

        // {----------- TIPO_FALSO_VERDADEIRO ---------------
        if (this.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            for (NoProva n: this.getChildsNoClone()) {
                ItemQuesito i = (ItemQuesito) n;
                List<Integer> list = i.getRespostaAluno();
                if (list.size() == 0)
                    correct+='_';
                else {
                    int v = list.get(0);
                    if (v == 0) correct+='V';
                    else if (v == 1) correct+='F';
                    else correct+='?';
                }
            }
        }
        // ----------- TIPO_FALSO_VERDADEIRO ---------------}

        return correct;
    }

    public String getTextRespostaCorreta() {
        String correct ="";

        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            for (int i:getRespostaCorreta()) {
                correct+=(char)('A'+i);
            }
        }
        // ----------- ALTERNATIVAS ---------------}

        // {----------- TIPO_NUMERICO_99 ---------------
        else if (this.getTipo() == TIPO_NUMERICO_99) {
            boolean first = true;
            for (int i:getRespostaCorreta()) {
                if (!first) correct+=',';
                correct+=i;
                first = false;
            }
        }
        // ----------- TIPO_NUMERICO_99 ---------------}

        // {----------- SUBJETIVO ---------------
        else if (this.getTipo() == TIPO_SUBJETIVA_5 || this.getTipo() == TIPO_SUBJETIVA_9) {
        }
        // ----------- SUBJETIVO ---------------}

        // {----------- TIPO_FALSO_VERDADEIRO ---------------
        if (this.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            for (NoProva n: this.getChildsNoClone()) {
                ItemQuesito i = (ItemQuesito) n;
                List<Integer> list = i.getRespostaCorreta();
                if (list.size() == 0)
                    correct+='_';
                else {
                    int v = list.get(0);
                    if (v == 0) correct+='V';
                    else if (v == 1) correct+='F';
                    else correct+='?';
                }
            }
        }
        // ----------- TIPO_FALSO_VERDADEIRO ---------------}

        return correct;
    }


    //
    public String getTextRespostaCorretaWithinPermutation() {
        String correct = "";
        // {----------- ALTERNATIVAS ---------------
        if (this.getTipo() == TIPO_ALTERNATIVAS) {
            for (int i:getRespostaCorretaWithinPermutation()) {
                correct+=(char)('A'+i);
            }
        }
        // ----------- ALTERNATIVAS ---------------}


        // {----------- TIPO_NUMERICO_99 ---------------
        if (this.getTipo() == TIPO_NUMERICO_99) {
            boolean first = true;
            first = true;
            for (int i:getRespostaCorretaWithinPermutation()) {
                if (!first) correct+=',';
                correct+=i;
                first = false;
            }
        }
        // ----------- TIPO_NUMERICO_99 ---------------}

        // {----------- SUBJETIVO ---------------
        // ----------- SUBJETIVO ---------------}

        // {----------- TIPO_FALSO_VERDADEIRO ---------------
        if (this.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
            for (NoProva n: this.getPermutacao()) {
                ItemQuesito i = (ItemQuesito) n;
                List<Integer> list = i.getRespostaCorreta();
                if (list.size() == 0)
                    correct+='_';
                else {
                    int v = list.get(0);
                    if (v == 0) correct+='V';
                    else if (v == 1) correct+='F';
                    else correct+='?';
                }
            }
        }
        // ----------- TIPO_FALSO_VERDADEIRO ---------------}

        return correct;
    }




    public void getAllNodes(ArrayList<Model> list) {
        list.add(this);
        for (Model m:this.getItens()) {
            list.add(m);
        }
    }

    public void setValorAcerto(double v) {
        setProperty("acerto",String.format("%.3f",v));
        this.fireModelUpdate();
    }

    public void setValorErro(double v) {
        setProperty("falha",String.format("%.3f",v));
        this.fireModelUpdate();
    }

    public void addItemQuesito(ItemQuesito iq) {
        this.addChild(iq);
    }

    public void removerItemQuesito(ItemQuesito i) {
        this.removeChild(i);
    }

    public void remover() {
        for (ItemQuesito iq: this.getItens()) {
            removerItemQuesito(iq);
        }
        Object o = this.getParent();
        if (o != null && o instanceof Grupo) {
            Grupo g = (Grupo) o;
            g.removerQuesito(this);
        }
    }

    public Quesito getCopy() {
        Quesito copy = new Quesito();
        copy.addProperties(this.getProperties());
        copy.setEnunciado(this.getEnunciado().getCopy());
        copy.setSolucao(this.getSolucao().getCopy());
        for (ItemQuesito iq: this.getItens()) {
            copy.addItemQuesito(iq.getCopy());
        }
        return copy;
    }

    /**
     * fire the current structure of the objects
     */
    public void fireInitStructure() {
        ArrayList<Model> l = new ArrayList<Model>(this.getItens());
        this.fireNodesLoaded(l);
    }

    /**
     * fire the current structure of the objects
     */
    public void fireRemoveStructureSimulation() {
        ArrayList<Model> list = new ArrayList<Model>(this.getChilds());
        for (Model m: list) {
            ((ItemQuesito) m).fireRemoveStructureSimulation();
        }
        this.getParent().fireNodeRemoved(this);
    }

}
