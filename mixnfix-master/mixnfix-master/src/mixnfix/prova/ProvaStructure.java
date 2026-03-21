package mixnfix.prova;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import mixnfix.Model;
import app.Permutacao;

/**
 * <p>Title: Prova</p>
 *
 * <p>Description: Esta classe é o ponto de partida para
 * todas as informações referentes a uma prova. Seus quesitos
 * seu gabarito (qual é a alternativa certa) o valor dos
 * quesitos etc.
 * </p>
 */
public class ProvaStructure extends No {
    private Grupo _root = new Grupo();
    private Papel _cabecalho;

    public ProvaStructure() {
        _root.setTotalizado(true);
        _root.setTag("__root");
        _root.setParent(this);
        _cabecalho = new Papel();
        _cabecalho.setParent(this);
        System.out.println("_root: "+_root+" parent "+this);
    }

    public void addChild(NoProva n) {
        _root.addChild(n);
    }

    public Papel getCabecalho() {
        return _cabecalho;
    }

    public void setCabecalho(Papel p) {
        _cabecalho = p;
        _cabecalho.setParent(this);
        this.fireModelUpdate();
    }

    public Grupo getRoot() {
        return _root;
    }

    public int getNumDigitosID() {
        String st=this.getProperty("numerodedigitosid");
        if (st == null)
            return 11;
        else
            return Integer.parseInt(st);
    }

    public int getTamanhoFonte() {
        String st=this.getProperty("fontsize");
        if (st == null)
            return 11;
        else
            return Integer.parseInt(st);
    }

    public float getMarginTop() {
        String st=this.getProperty("margintop");
        if (st == null)
            return 1.5f;
        else
            return Float.parseFloat(st);
    }

    public float getMarginBottom() {
        String st=this.getProperty("marginbottom");
        if (st == null)
            return 1.5f;
        else
            return Float.parseFloat(st);
    }

    public float getMarginLeft() {
        String st=this.getProperty("marginleft");
        if (st == null)
            return 1.5f;
        else
            return Float.parseFloat(st);
    }

    public float getMarginRight() {
        String st=this.getProperty("marginright");
        if (st == null)
            return 1.5f;
        else
            return Float.parseFloat(st);
    }

    public float getPaperWidth() {
        String st=this.getProperty("papelwidth");
        if (st == null)
            return 210f;
        else
            return Float.parseFloat(st);
    }

    public float getPaperHeight() {
        String st=this.getProperty("papelheight");
        if (st == null)
            return 297f;
        else
            return Float.parseFloat(st);
    }

    public String getTiposDeProvas() {
        String st=this.getProperty("numerodeprovas");
        if (st == null)
            return "1";
        else
            return st;
    }

    public void setIndex(int index) {
        this.setProperty("index",""+index);
        this.fireModelUpdate();
    }

    public void setNumDigitosID(int n) {
        if (this.getNumDigitosID() == n)
            return;

        this.setProperty("numerodedigitosid",""+n);
        this.fireModelUpdate();
    }

    public int getIndex() {
        String st=this.getProperty("index");
        if (st == null)
            return 0;
        else
            return Integer.parseInt(st);
    }

    public int getNumeroDeColunas() {
        String st=this.getProperty("numerodecolunas");
        if (st == null)
            return 0;
        else
            return Integer.parseInt(st);
    }

    private int _tipoProva;
    public int getTipoProva() {
        return _tipoProva;
    }

    public void permute(int tipo) {
        // calcular serital
        _tipoProva = tipo;
        int N = this.getNumElementsOnPermutation();
        BigInteger serial = Permutacao.getSerial(BigInteger.ONE,(tipo == 0?0:7+tipo),N);

        //
        ArrayList<NoProva> itemsToPermute = new ArrayList<NoProva>();
        LinkedList<NoProvaPermutavel> S = new LinkedList<NoProvaPermutavel>();
        S.add(_root);
        while (!S.isEmpty()) {
            NoProvaPermutavel n = S.removeFirst();
            n.resetPermutation(); // clear current permutation
            if (n.isTravado()) {
                // System.out.println("TAG: "+n.getProperty("tag")+" está travado.");
                for (NoProva x: n.getChilds()) {
                    n.addItemToPermutacao(x); // colocar na permutação identidade os elementos de um nó travado
                    if (x instanceof NoProvaPermutavel)
                        S.addLast( (NoProvaPermutavel) x);
                }
            }
            else {
                //System.out.println("TAG: "+n.getProperty("tag")+" nao está travado.");
                for (NoProva x: n.getChilds()) {
                    itemsToPermute.add(x); // save this "x" to permute after
                    if (x instanceof NoProvaPermutavel)
                        S.addLast( (NoProvaPermutavel) x);
                }
            }
        }

        //
        int numItemsToPermute = itemsToPermute.size();

        if (numItemsToPermute > 0) {
            app.Permutacao p = new app.Permutacao(numItemsToPermute, serial);
            //System.out.println("" + p);
            int[] v = p.getV();

            /*int count=0;
            for (NoProva np: itemsToPermute) {
                System.out.println(""+(count++)+" -> "+np.getTag());
            }*/

            for (int i = 0; i < v.length; i++) {
                NoProva np = itemsToPermute.get(v[i] - 1);
                if (np.getParent() == null)
                    throw new RuntimeException("Problema");
                // System.out.println("Adding "+np.getTag()+" to "+np.getParent().getTag());
                np.getParentAsNoProvaPermutavel().addItemToPermutacao(np);
            }
        }
    }

    public int getNumElementsOnPermutation() {
        int count=0;
        LinkedList<NoProvaPermutavel> S = new LinkedList<NoProvaPermutavel>();
        S.add(_root);
        while (!S.isEmpty()) {
            NoProvaPermutavel n = S.removeFirst();
            if (n.isTravado()) {
                for (NoProva x: n.getChilds()) {
                    if (x instanceof NoProvaPermutavel)
                        S.addLast( (NoProvaPermutavel) x);
                }
            }
            else {
                for (NoProva x: n.getChilds()) {
                    count++;
                    if (x instanceof NoProvaPermutavel)
                        S.addLast( (NoProvaPermutavel) x);
                }
            }
        }
        return count;
    }

    public ArrayList<Quesito> getQuesitosPermutados() {
        ArrayList<Quesito> result = new ArrayList<Quesito>();
        find(_root,result);
        return result;
    }

    private void find(NoProva no, List<Quesito> quesitos) {
        if (no instanceof Grupo) {
            Grupo g = ((Grupo) no);
            for (NoProva n: g.getPermutacao())
                find(n,quesitos);
        }
        else if (no instanceof Quesito) {
            quesitos.add((Quesito)no);
        }
    }

    public ArrayList<Quesito> getQuesitos() {
        ArrayList<Quesito> result = new ArrayList<Quesito>();
        findQuesitos(_root,result);
        return result;
    }

    private void findQuesitos(NoProva no, List<Quesito> quesitos) {
        if (no instanceof Grupo) {
            Grupo g = ((Grupo) no);
            for (NoProva n: g.getChilds())
                findQuesitos(n,quesitos);
        }
        else if (no instanceof Quesito) {
            quesitos.add((Quesito)no);
        }
    }

    public void printXML(PrintWriter s) {
        s.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        s.println("<?xml-stylesheet type=\"application/xml\" href=\"prova.xsl\"?>");
        s.print("<prova> ");
        s.print("<config> ");
        this.printPropertiesInXML(s);
        s.println(">");
        s.print("<header ");
        if (_cabecalho != null)
            _cabecalho.printPropertiesInXML(s);
        s.println(">");
        if (_cabecalho != null)
            _cabecalho.printXML(s);
        s.println("</header>");
        _root.printXML(s);
        s.println("</prova>");
    }

    public static void permuteProva(ProvaStructure prova, int tipo) {
        prova.permute(tipo);
    }

    private ArrayList<Grupo> _nosTotalizados;
    public List<Grupo> getNosTotalizados() {
        if (_nosTotalizados == null) {
            _nosTotalizados = new ArrayList<Grupo>();
            dfsNosTotalizados(_root, _nosTotalizados);
        }
        return _nosTotalizados;
    }
    private void dfsNosTotalizados(Grupo n, ArrayList<Grupo> nosTotalizados) {
        if (n.isTotalizado()) {
            nosTotalizados.add(n);
        }
        for (NoProva x: n.getPermutacao()) {
            if (x instanceof Grupo) {
                dfsNosTotalizados((Grupo)x,nosTotalizados);
            }
        }
    }

    // nota
    private float _nota;
    public float getNota() { return _nota; }
    public float avaliarNota() {
        _nota = _root.avaliarNota();
        return _nota;
    }

    //
    public static int DETALHAR_NOTA_QUESITO = 1;
    public void detalharNota(StringBuffer buffer) {
        DETALHAR_NOTA_QUESITO = 1;
        buffer.append("Índice da prova: "+this.getIndex()+"\n");
        buffer.append("Tipo da prova: "+this.getTipoProva()+"\n");
        buffer.append("----\n");
        List<Grupo> nosTotalizados = this.getNosTotalizados();
        for (Grupo g: nosTotalizados) {
            buffer.append(String.format("%-20s %5.3f\n",g.getTag(),g.getNota()));
        }
        buffer.append("----\n");
        _root.detalharNota(buffer);

    }


    /**
     * fire the current structure of the objects
     */
    public void fireInitStructure() {
        ArrayList<Model> l = new ArrayList<Model>();
        l.add(_root);
        this.fireNodesLoaded(l);
        _root.fireInitStructure();
    }

    public void getAllNodes(ArrayList<Model> list) {
        list.add(this);
        _root.getAllNodes(list);
    }


    // ---------------------------------------------
    int _nGrupo;
    int _nQuesito;
    int _nItemQuesito;
    public void setDefaultsTags() {
        _nGrupo = 1;
        _nQuesito = 1;
        _nItemQuesito = 1;
        dfsDefaultTags(_root);
    }

    public void dfsDefaultTags(Model m) {
        if (m instanceof Grupo) {
            Grupo g = (Grupo) m;
            if (g.getParent() instanceof Grupo) {
                g.setTag("Grupo" + _nGrupo);
                _nGrupo++;
            }
            for (Model c: g.getChilds()) {
                dfsDefaultTags(c);
            }
        }
        else if (m instanceof Quesito) {
            Quesito q = (Quesito) m;
            for (Model c: q.getChilds()) {
                dfsDefaultTags(c);
            }
            q.setTag("Quesito"+_nQuesito);
            _nQuesito++;
            _nItemQuesito = 1;
        }
        else if (m instanceof ItemQuesito) {
            ItemQuesito iq = (ItemQuesito) m;
            iq.setTag("Item"+_nItemQuesito);
            _nItemQuesito++;
        }
    }
    // ---------------------------------------------


    // ---------------------------------------------
    int __nQuesito;
    public void setPrefixoNumericoNosTagsDosQuesitos() {
        __nQuesito = 1;
        dfsPrefixoNumericoNosTagsDosQuesitos(_root);
    }

    public void dfsPrefixoNumericoNosTagsDosQuesitos(Model m) {
        if (m instanceof Grupo) {
            Grupo g = (Grupo) m;
            for (Model c: g.getChilds()) {
                dfsPrefixoNumericoNosTagsDosQuesitos(c);
            }
        }
        else if (m instanceof Quesito) {
            Quesito q = (Quesito) m;
            q.setTag(__nQuesito+" "+q.getTag());
            __nQuesito++;
        }
    }
    // ---------------------------------------------


}
