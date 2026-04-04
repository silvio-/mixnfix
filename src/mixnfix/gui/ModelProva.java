package mixnfix.gui;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

// Transform
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import mixnfix.Controller;
import mixnfix.Model;
import mixnfix.ModelListener;
import mixnfix.modelo.ColetaQuestionario;
import mixnfix.modelo.ProvaCorrecao;
import mixnfix.prova.Estilo;
import mixnfix.prova.Formula;
import mixnfix.prova.Grupo;
import mixnfix.prova.Imagem;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.NoPapel;
import mixnfix.prova.NoProva;
import mixnfix.prova.Papel;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;
import mixnfix.prova.Texto;

// SAX.
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
//JAXP 1.1


/**
 * ModelProva
 */

public class ModelProva extends Model {

    private mixnfix.modelo.Prova _prova;

    // as provas correcao desta Prova. Enquanto for null
    // é pq ainda não foi sincronizado com o DB
    private ArrayList<ModelProvaCorrecao> _provasCorrecao;

    // as provas correcao desta Prova. Enquanto for null
    // é pq ainda não foi sincronizado com o DB
    private ArrayList<ModelColetaQuestionario> _coletasQuestionario;

    /**
     * Get a clone list of the provas correcoes.
     */
    public ArrayList<ModelProvaCorrecao> getProvasCorrecoes() throws SQLException {
        this.syncChilds(false);
        return (ArrayList<ModelProvaCorrecao>) _provasCorrecao.clone();
    }

    /**
     * Get a clone list of the provas correcoes.
     */
    public ArrayList<ModelColetaQuestionario> getColetasQuestionario() throws SQLException {
        this.syncChilds(false);
        return (ArrayList<ModelColetaQuestionario>) _coletasQuestionario.clone();
    }

    /**
     * Synchronize ProvasCorrecoes with database.
     * @throws SQLException
     */
    private void syncChilds(boolean force) throws SQLException {
        if (force || _provasCorrecao == null || _coletasQuestionario == null) {
            { // prova correcao
                Vector v = App.getRepositorio().consultarProvaCorrecaoPorProva(_prova);
                _provasCorrecao = new ArrayList<ModelProvaCorrecao>(v.size());
                ArrayList<Model> loadList = new ArrayList<Model>(v.size());
                for (Object o: v) {
                    ModelProvaCorrecao mp = new ModelProvaCorrecao(this,(ProvaCorrecao) o);
                    _provasCorrecao.add(mp);
                    loadList.add(mp);
                }
                this.fireNodesLoaded(loadList);
            }

            { // coleta de questionarios
                Vector v = App.getRepositorio().consultarColetaQuestionarioPorProva(_prova);
                this._coletasQuestionario = new ArrayList<ModelColetaQuestionario> (v.size());
                ArrayList<Model> loadList = new ArrayList<Model> (v.size());
                for (Object o : v) {
                    ModelColetaQuestionario mp = new ModelColetaQuestionario(this, (ColetaQuestionario) o);
                    _coletasQuestionario.add(mp);
                    loadList.add(mp);
                }
                this.fireNodesLoaded(loadList);
            }
        }
    }

    public int getNumeroDeCorrecoes() {
        try {
            return this.getProvasCorrecoes().size();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    public int getNumeroDeColetas() {
        try {
            return this.getColetasQuestionario().size();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * Add a prova correcao.
     */
    public ModelProvaCorrecao addProvaCorrecao(String nomeCorrecao) throws SQLException {
        this.syncChilds(false);
        ProvaCorrecao provaCorrecao = App.getRepositorio().inserirProvaCorrecao(nomeCorrecao,_prova);
        ModelProvaCorrecao mpc = new ModelProvaCorrecao(this,provaCorrecao);
        mpc.setParent(this);
        _provasCorrecao.add(mpc);
        this.fireNodeAdded(mpc,_provasCorrecao.size()+_coletasQuestionario.size()-1);
        return mpc;
    }

    /**
     * Add a coleta questionario.
     */
    public ModelColetaQuestionario addColetaQuestionario(String nomeColeta) throws SQLException {
        this.syncChilds(false);
        ColetaQuestionario provaCorrecao = App.getRepositorio().inserirColetaQuestionario(nomeColeta,_prova);
        ModelColetaQuestionario mpc = new ModelColetaQuestionario(this,provaCorrecao);
        mpc.setParent(this);
        _coletasQuestionario.add(mpc);
        this.fireNodeAdded(mpc,_provasCorrecao.size()+_coletasQuestionario.size()-1);
        return mpc;
    }

    public ModelProva(ModelInstituicao mi, mixnfix.modelo.Prova prova) {
        this.setParent(mi);
        _prova = prova;
    }

    public ModelInstituicao getModeInstituicao() {
        return (ModelInstituicao) this.getParent();
    }

    public mixnfix.modelo.Prova getProva() {
        return _prova;
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumeroDeCorrecoes() != 0)
                throw new RuntimeException("Não é possível remover prova " +
                                     this.getProva().getNome() +
                                     ": existem correções associadas a mesma");

            if (this.getNumeroDeColetas() != 0)
                throw new RuntimeException("Não é possível remover correção " +
                                     this.getProva().getNome() +
                                     ": existem coletas associadas a mesma");
        }
        ModelInstituicao mi = (ModelInstituicao) this.getParent();
        mi.removerProva(this,cascade);
    }

    /**
     * Remover a prova correcao.
     */
    public void removerProvaCorrecao(ModelProvaCorrecao mpc, boolean cascade) throws IOException, SQLException {
        /**
         * @todo implementar isso aqui do mesmo jeito que remover
         * existe o removerEntradaProvaCorrecao na classe ProvaCorrecao.
         */
        this.syncChilds(false);

        // nao remove se nao for uma prova da instituicao
        if (!_provasCorrecao.contains(mpc))
            throw new RuntimeException("Oooppps");

        // remover EntradasProvasCorrecoes
        mpc.removeEntradasProvaCorrecao(mpc.getEntradasProvaCorrecao());

        // remover AlunosProvasCorrecoes
        ArrayList<ModelAlunoProvaCorrecao> alunos = mpc.getAlunosProvaCorrecao();
        for (int i = alunos.size()-1; i >= 0; i--) {
            ModelAlunoProvaCorrecao mapc = alunos.get(i);
            mapc.remover(cascade);
        }

        // remover do BD
        App.getRepositorio().removerProvaCorrecao(mpc.getProvaCorrecao());

        // remover do modelo
        _provasCorrecao.remove(mpc);

        // sinaliza
        this.fireNodeRemoved(mpc);
    }

    /**
     * Remover a ColetaQuestionario.
     */
    public void removerColetaQuestionario(ModelColetaQuestionario mcq, boolean cascade) throws IOException, SQLException {
        this.syncChilds(false);

        // nao remove se nao for uma prova da instituicao
        if (!_coletasQuestionario.contains(mcq))
            throw new RuntimeException("Oooppps");

        // remover EntradasColetaQuestionario
        mcq.removeEntradasColetaQuestionario(mcq.getEntradasColetaQuestionario());

        // remover do BD
        App.getRepositorio().removerColetaQuestionario(mcq.getColetaQuestionario());

        // remove do modelo
        _coletasQuestionario.remove(mcq);

        // sinaliza
        this.fireNodeRemoved(mcq);
    }

    private boolean _structureChanged = false;
    public void resetStructureChanged() {
        _structureChanged = false;
        fireModelUpdate();
    }

    public boolean getStructureChanged() {
        return _structureChanged;
    }

    private ProvaStructure _provaStructure;
    public ProvaStructure getProvaStructure() {
        if (_provaStructure == null) {
            System.out.println("Loading prova structure: "+this._prova.getNome());

            loadProvaStructure(); // load prova structure

            // send signal
            this.fireNodeAdded(_provaStructure,0);
            _provaStructure.fireInitStructure();

            ArrayList<Model> list = new ArrayList<Model>();
            _provaStructure.getAllNodes(list);


            ModelListener ml = new ModelListener() {
                public void update(Model model) {
                    _structureChanged = true;
                    fireModelUpdate();
                }
                public void nodeAdded(Model model, Model addedModel, int index) {
                    _structureChanged = true;
                    addedModel.addListener(this);
                    fireModelUpdate();
                }
                public void nodesAdded(Model model, List<Model> addedModel, int index) {
                    _structureChanged = true;
                    for (Model m: (List<Model>) addedModel)
                        m.addListener(this);
                    fireModelUpdate();
                }
                public void nodesLoaded(Model model, List<Model> addedModel) {
                }
                public void nodeRemoved(Model model, Model removedModel) {
                    _structureChanged = true;
                    removedModel.removeListener(this);
                    fireModelUpdate();
                }
            };

            for (Model m: list) {
                m.addListener(ml);
            }
        }
        return _provaStructure;
    }

    public void gravar() throws IOException, Exception {

        if (_path == null)
            throw new RuntimeException("problema na atualização do XML");

        PrintWriter pw = new PrintWriter(_path.getCanonicalPath()+"/prova.xml", "utf-8");
        gerarXML(pw);
        pw.flush();
        pw.close();

        List<String> names = getImageFileNames();
        names.add("prova.xml");

        // zip directory

        File preprova = new File(_path.getCanonicalPath()+"/preprova.prova");

        Controller.zipFiles(_path,names,preprova.getAbsolutePath());

        mixnfix.Library.copyFile(preprova,new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+_prova.getFonte()));

        this.resetStructureChanged();
    }

    public List<String> getImageFileNames() {

        ArrayList<Model> nodes = new ArrayList<Model>();
        ArrayList<String> names = new ArrayList<String>();

        ProvaStructure ps = getProvaStructure();
        ps.getAllNodes(nodes);

        ArrayList<Papel> papeis = new ArrayList<Papel>();
        papeis.add(ps.getCabecalho());

        for (Model m: nodes) {
            if (m instanceof Quesito) {
                Quesito q = (Quesito) m;
                Papel p;
                p = q.getEnunciado();
                if (p != null)
                    papeis.add(p);
                p = q.getSolucao();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof ItemQuesito) {
                ItemQuesito iq = (ItemQuesito) m;
                Papel p;
                p = iq.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof Grupo) {
                Grupo g = (Grupo) m;
                Papel p = g.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
        }

        for (Papel p:papeis) {
            for (Model m : p.getContents()) {
                if (m instanceof Imagem) {
                    Imagem i = (Imagem) m;
                    String src = i.getProperty("src");

                    // gambiarra para provas geradas com figuras em EPS
                    if (src.toUpperCase().endsWith("EPS")) {
                        src = src.substring(0,src.length()-3)+"jpg";
                        i.setProperty("src",src);
                    }

                    if (src != null && !names.contains(src))
                        names.add(src);
                }
            }
        }
        return names;
    }


    public File getPath() {
        return _path;
    }

    /**
     * Image file name that is valid!
     */
    public String assureImageFileName(String fileName) throws IOException, Exception {
        boolean flag = false;
        int i=2;
        while (!flag) {
            for (File f : _path.listFiles()) {
                if (fileName.equals(f.getName())) {
                    fileName = "-"+(i++)+"-"+fileName;
                    break;
                }
            }
            break;
        }
        return fileName;
    }

    public void gerarXML(PrintWriter out) throws Exception, IOException {
        // ProvaStructure s = this.getProvaStructure();
        // s.printXML(ps);

        ProvaStructure ps = this.getProvaStructure();

        StreamResult streamResult = new StreamResult(out);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

        // SAX2.0 ContentHandler.
        TransformerHandler hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        // serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"prova.dtd");
        serializer.setOutputProperty(OutputKeys.INDENT,"no");
        // OutputKeys.

        hd.setResult(streamResult);
        hd.startDocument();

        String st = "<?xml-stylesheet type=\"application/xml\" href=\"prova.xsl\"?>\n";

        AttributesImpl atts = new AttributesImpl();

        // prova tag.
        hd.startElement("","","prova",atts);

        // config
        atts.clear();
        for (Object key :ps.getProperties().keySet()) {
            Object value = ps.getProperty((String)key);
            atts.addAttribute("","",""+key,"CDATA",""+value);
        }
        hd.startElement("","","config",atts);
        hd.endElement("","","config");

        // cabecalho
        atts.clear();
        hd.startElement("","","cabeçalho",atts);
        xml_producePapel(ps.getCabecalho(),hd);
        hd.endElement("","","cabeçalho");

        //
        for (NoProva np :ps.getRoot().getChilds()) {
            xml_produceNode(np, hd);
        }

        // end prova
        hd.endElement("","","prova");
    }

    /**
     * Serialize a single Quesito as a standalone &lt;prova&gt; XML document
     * so that {@link mixnfix.prova.Parser} can round-trip it.
     * Used by the question bank (banco de questões).
     */
    static void gerarXMLQuesito(Quesito q, PrintWriter out) throws Exception {
        StreamResult streamResult = new StreamResult(out);
        SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler hd = tf.newTransformerHandler();
        Transformer serializer = hd.getTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        serializer.setOutputProperty(OutputKeys.INDENT,"no");
        hd.setResult(streamResult);
        hd.startDocument();
        AttributesImpl atts = new AttributesImpl();
        hd.startElement("","","prova",atts);
        xml_produceNode(q, hd);
        hd.endElement("","","prova");
        hd.endDocument();
    }

    static void xml_produceNode(NoProva n, TransformerHandler hd) throws SAXException {
        if (n instanceof Grupo) {
            Grupo g = (Grupo) n;

            AttributesImpl atts = new AttributesImpl();
            for (Object key :n.getProperties().keySet()) {
                Object value = n.getProperty((String)key);
                atts.addAttribute("","",""+key,"CDATA",""+value);
            }
            hd.startElement("","","grupo",atts);

            atts.clear();
            hd.startElement("","","enunciado",atts);
            xml_producePapel(g.getEnunciado(),hd);
            hd.endElement("","","enunciado");

            for (NoProva x: g.getChilds()) {
                xml_produceNode(x,hd);
            }
            hd.endElement("","","grupo");
        }
        else if (n instanceof Quesito) {
            Quesito q = (Quesito) n;

            AttributesImpl atts = new AttributesImpl();
            for (Object key :n.getProperties().keySet()) {
                Object value = n.getProperty((String)key);
                atts.addAttribute("","",""+key,"CDATA",""+value);
            }
            hd.startElement("","","quesito",atts);

            atts.clear();
            hd.startElement("","","enunciado",atts);
            xml_producePapel(q.getEnunciado(),hd);
            hd.endElement("","","enunciado");

            for (NoProva x: q.getChilds()) {
                xml_produceNode(x,hd);
            }
            hd.endElement("","","quesito");
        }
        else if (n instanceof ItemQuesito) {
            ItemQuesito q = (ItemQuesito) n;

            AttributesImpl atts = new AttributesImpl();
            for (Object key :n.getProperties().keySet()) {
                Object value = n.getProperty((String)key);
                atts.addAttribute("","",""+key,"CDATA",""+value);
            }
            hd.startElement("","","item",atts);
            xml_producePapel(q.getEnunciado(),hd);
            hd.endElement("","","item");
        }
    }

    static void xml_producePapel(Papel p, TransformerHandler hd) throws SAXException {
        if (p == null)
            return;

        for (NoPapel n: p.getContents()) {
            if (n instanceof Texto) {
                Texto t = (Texto) n;
                String st = t.getTexto();
                hd.characters(st.toCharArray(),0,st.length());
            }
            else if (n instanceof Formula) {
                Formula f = (Formula) n;
                String st = f.getTexto();
                AttributesImpl atts = new AttributesImpl();
                for (Object key :n.getProperties().keySet()) {
                    Object value = n.getProperty((String)key);
                    atts.addAttribute("","",""+key,"CDATA",""+value);
                }
                hd.startElement("","","mat",atts);
                hd.characters(st.toCharArray(),0,st.length());
                hd.endElement("","","mat");
            }
            else if (n instanceof Imagem) {
                Imagem i = (Imagem) n;
                AttributesImpl atts = new AttributesImpl();
                for (Object key :n.getProperties().keySet()) {
                    Object value = n.getProperty((String)key);
                    atts.addAttribute("","",""+key,"CDATA",""+value);
                }
                hd.startElement("","","figura",atts);
                hd.endElement("","","figura");
            }
            else if (n instanceof Estilo) {
                Estilo e = (Estilo) n;
                String st = e.getTexto();
                AttributesImpl atts = new AttributesImpl();
                for (Object key :n.getProperties().keySet()) {
                    Object value = n.getProperty((String)key);
                    atts.addAttribute("","",""+key,"CDATA",""+value);
                }
                hd.startElement("","","estilo",atts);
                hd.characters(st.toCharArray(),0,st.length());
                hd.endElement("","","estilo");
            }
        }
    }

    private File _path;
    private void loadProvaStructure() {
        _path = new File(Controller.TMP_DIR+"/__mixnfix_provas/"+"p+"+_prova.getId());
        try {
            _path.mkdirs(); // assure that the directory exists

            for (File fs: _path.listFiles()) { // delete all files
                fs.delete();
            }

            Controller.unzip(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+_prova.getFonte(), _path.getCanonicalPath());
            mixnfix.prova.Parser parser = new mixnfix.prova.Parser(_path.getCanonicalPath() + "/prova.xml");

            this._provaStructure = parser.getProva();
            _provaStructure.setParent(this);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get prova strcture.
     */
    public static mixnfix.prova.ProvaStructure getProvaStructure(mixnfix.modelo.Prova p) {
        return getProvaStructure(new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+p.getFonte()));
    }

    /**
     * Get prova strcture.
     */
    public static mixnfix.prova.ProvaStructure getProvaStructure(File f) {
        try {
            Controller.unzip(f.getAbsolutePath(), Controller.TMP_DIR);
            mixnfix.prova.Parser parser = new mixnfix.prova.Parser(Controller.TMP_DIR + "/prova.xml");
            mixnfix.prova.ProvaStructure pp = parser.getProva();
            return pp;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void adjustImageFileNames() {

        ArrayList<Model> nodes = new ArrayList<Model>();
        ArrayList<String> names = new ArrayList<String>();

        ProvaStructure ps = getProvaStructure();
        ps.getAllNodes(nodes);

        ArrayList<Papel> papeis = new ArrayList<Papel>();
        papeis.add(ps.getCabecalho());

        for (Model m: nodes) {
            if (m instanceof Quesito) {
                Quesito q = (Quesito) m;
                Papel p;
                p = q.getEnunciado();
                if (p != null)
                    papeis.add(p);
                p = q.getSolucao();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof ItemQuesito) {
                ItemQuesito iq = (ItemQuesito) m;
                Papel p;
                p = iq.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
            else if (m instanceof Grupo) {
                Grupo g = (Grupo) m;
                Papel p = g.getEnunciado();
                if (p != null)
                    papeis.add(p);
            }
        }

        for (Papel p:papeis) {
            for (Model m : p.getContents()) {
                if (m instanceof Imagem) {
                    Imagem i = (Imagem) m;
                    // String original = i.getProperty("original");
                    // if (original != null) {
                    //    String name = new File(i.getProperty("original")).getName();
                    //    i.setProperty("src",name);
                    //}
                }
            }
        }
    }

    public ModelProvaCorrecao getProvaCorrecaoByName(String name) throws SQLException {
        this.syncChilds(false);
        for (ModelProvaCorrecao mpc: _provasCorrecao) {
            if (mpc.getProvaCorrecao().getNome().equals(name))
                return mpc;
        }
        return null;
    }

    public ModelColetaQuestionario getColetaQuestionarioByName(String name) throws SQLException {
        this.syncChilds(false);
        for (ModelColetaQuestionario mcq: _coletasQuestionario) {
            if (mcq.getColetaQuestionario().getNome().equals(name))
                return mcq;
        }
        return null;
    }

}
