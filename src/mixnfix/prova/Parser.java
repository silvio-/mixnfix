package mixnfix.prova;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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
public class Parser extends DefaultHandler implements Tags {

    private Stack<ParserNode> _stack = new Stack<ParserNode>();

    public Parser(String arq) throws SAXException, ParserConfigurationException, FileNotFoundException, IOException {
        javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory.newInstance();
        javax.xml.parsers.SAXParser sp = spf.newSAXParser();
        org.xml.sax.InputSource is = new org.xml.sax.InputSource(new java.io.FileInputStream(arq));
        
        // commented on Apr. 26, 2013
        is.setEncoding("UTF-8");

        // ParserHandler h = new ParserHandler();

        ParserNode root = new ParserNode("root");
        _stack.push(root);
        sp.parse(is, this);

        _prova = this.processProva((ParserNode)root.getChild(0));

        //root.get

        //Node n = h.getProvaNode();
        // return getDocumento(n);
    }

    private ProvaStructure _prova;
    public ProvaStructure getProva() {
        return _prova;
    }


    /**
     * Receive notification of the start of an element.
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        ParserNode parent = _stack.peek();

        ParserNode child = new ParserNode(qName);
        for (int i = 0; i < attributes.getLength(); i++) {
            String qNameAtr = attributes.getQName(i);
            String val = attributes.getValue(i);
            child.setProperty(qNameAtr, val);
        }

        parent.addChild(child);

        _stack.push(child);
    }

    /**
     * Receive notification of the end of an element.
     */
    public void endElement(String uri, String localName, String qName) throws SAXException {
        _stack.pop();
    }

    /**
     * Receive notification of character data inside an element.
     */
    public void characters(char ch[], int start, int length) throws SAXException {
        _stack.peek().addChild(new String(ch,start,length));
    }

    /**
     * Process this node.
     */
    private ProvaStructure processProva(ParserNode n) {
        if (!TAG_PROVA.equals(n.getName()))
            throw new RuntimeException("Not a PROVA");

        ProvaStructure p = new ProvaStructure();
        p.addProperties(n.getProperties());

        for (Object o:n.getChilds()) {
            if (o instanceof ParserNode) {
                ParserNode pn = (ParserNode) o;
                if (pn.getName().equals(Tags.TAG_CONFIG)) {
                    p.addProperties(pn.getProperties());
                }
                else if (pn.getName().equals(Tags.TAG_SIGNATURE)) {
                    p.addProperties(pn.getProperties());
                }
                else {
                    No np = processNode(pn);
                    if (pn.getName().equals(Tags.TAG_CABECALHO)) {
                        p.setCabecalho((Papel) np);
                    }
                    else if (pn.getName().equals(Tags.TAG_BLOCO)) {
                        p.addChild((NoProva)np);
                    }
                    else if (pn.getName().equals(Tags.TAG_QUESITO)) {
                        p.addChild((NoProva)np);
                    }
                }
            }
        }
        return p;
    }

    private No processNode(ParserNode pn) {
        if (pn.getName().equals(Tags.TAG_BLOCO)) {
            Grupo g = processGrupo(pn);
            return g;
        }
        else if (pn.getName().equals(Tags.TAG_QUESITO)) {
            Quesito q = processQuesito(pn);
            return q;
        }
        else if (pn.getName().equals(Tags.TAG_CABECALHO)) {
            Papel p = processPapel(pn);
            return p;
        }
        else if (pn.getName().equals(Tags.TAG_ENUNCIADO)) {
            Papel p = processPapel(pn);
            return p;
        }
        else if (pn.getName().equals(Tags.TAG_SOLUCAO)) {
            Papel p = processPapel(pn);
            return p;
        }
        else if (pn.getName().equals(Tags.TAG_ITEM)) {
            ItemQuesito iq = processItemQuesito(pn);
            return iq;
        }
        else return null;
    }

    /**
     * Process this node.
     */
    private Quesito processQuesito(ParserNode n) {
        Quesito q = new Quesito();
        q.addProperties(n.getProperties());

        for (Object o:n.getChilds()) {
            if (o instanceof ParserNode) {
                ParserNode pn = (ParserNode) o;
                No np = processNode(pn);
                if (pn.getName().equals(TAG_ENUNCIADO)) {
                    q.setEnunciado((Papel) np);
                }
                else if (pn.getName().equals(TAG_SOLUCAO)) {
                    q.setSolucao((Papel) np);
                }
                else if (pn.getName().equals(TAG_ITEM)) {
                    q.addItemQuesito((ItemQuesito)np);
                }
            }
        }
        return q;
    }

    /**
     * Process this node.
     */
    private ItemQuesito processItemQuesito(ParserNode n) {
        ItemQuesito i = new ItemQuesito();
        i.addProperties(n.getProperties());

        Papel enunciado = new Papel();
        for (Object o:n.getChilds()) {
            if (o instanceof ParserNode) {
                ParserNode pn2 = (ParserNode) o;
                if (Tags.TAG_FIGURA.equals(pn2.getName())) {
                    Imagem img = new Imagem();
                    img.addProperties(pn2.getProperties());
                    enunciado.addChild(img);
                }
                else if (Tags.TAG_MATEMATICA.equals(pn2.getName())) {
                    Formula f = processFormula(pn2);
                    enunciado.addChild(f);
                }
                else if (Tags.TAG_TEX.equals(pn2.getName())) {
                    Formula f = processFormula(pn2);
                    f.setProperty(Tags.TAG_MODO,Tags.TAG_MODO_TEX);
                    enunciado.addChild(f);
                }
                else if (Tags.TAG_ESTILO.equals(pn2.getName())) {
                    Estilo e = processEstilo(pn2);
                    enunciado.addChild(e);
                }
            }
            else if (o instanceof StringBuffer) {
                Texto t = new Texto();
                t.setTexto((StringBuffer) o);
                enunciado.addChild(t);
            }
        }
        i.setEnunciado(enunciado);

        return i;
    }

    private Papel processPapel(ParserNode pn) {
        Papel p = new Papel();
        p.addProperties(pn.getProperties());

        for (Object o:pn.getChilds()) {
            if (o instanceof ParserNode) {
                ParserNode pn2 = (ParserNode) o;
                if (Tags.TAG_FIGURA.equals(pn2.getName())) {
                    Imagem i = new Imagem();
                    i.addProperties(pn2.getProperties());
                    p.addChild(i);
                }
                else if (Tags.TAG_MATEMATICA.equals(pn2.getName())) {
                    Formula f = processFormula(pn2);
                    p.addChild(f);
                }
                else if (Tags.TAG_TEX.equals(pn2.getName())) {
                    Formula f = processFormula(pn2);
                    f.setProperty(Tags.TAG_MODO,Tags.TAG_MODO_TEX);
                    p.addChild(f);
                }
                else if (Tags.TAG_ESTILO.equals(pn2.getName())) {
                    Estilo e = processEstilo(pn2);
                    p.addChild(e);
                }
            }
            else if (o instanceof StringBuffer) {
                Texto t = new Texto();
                t.setTexto((StringBuffer) o);
                p.addChild(t);
            }
        }
        return p;
    }

    private Formula processFormula(ParserNode pn) {
        Formula f = new Formula();
        f.addProperties(pn.getProperties());
        for (Object o:pn.getChilds()) {
            if (o instanceof StringBuffer) {
                f.append((StringBuffer) o);
            }
        }
        return f;
    }

    private Estilo processEstilo(ParserNode pn) {
        Estilo e = new Estilo();
        e.addProperties(pn.getProperties());
        for (Object o:pn.getChilds()) {
            if (o instanceof StringBuffer) {
                e.append((StringBuffer) o);
            }
        }
        return e;
    }

    private Grupo processGrupo(ParserNode n) {
        if (!TAG_BLOCO.equals(n.getName()))
            throw new RuntimeException("Not a Grupo");

        Grupo g = new Grupo();
        g.addProperties(n.getProperties());

        for (Object o:n.getChilds()) {
            if (o instanceof ParserNode) {
                ParserNode pn = (ParserNode) o;
                No noProva = processNode((ParserNode) o);
                if (noProva instanceof Papel) {
                    g.setEnunciado((Papel) noProva);
                }
                else if (noProva instanceof Quesito) {
                    g.addQuesito((Quesito) noProva);
                }
                else if (noProva instanceof Grupo) {
                    g.addGrupo((Grupo) noProva);
                }
            }
        }

        return g;
    }
}

class ParserNode {
    String _name;
    Properties _properties = new Properties();
    List _childs = new ArrayList();

    public String getName() {
        return _name;
    }

    public ParserNode(String name) {
        _name = name;
    }

    public void setProperty(String name, String value) {
        _properties.put(name,value);
    }

    public Properties getProperties() {
        return (Properties) _properties.clone();
    }

    public void addChild(Object obj) {
        { // convert String to StringBuffer or append it to the last element if it is a StringBuffer
            if (obj instanceof String) {
                Object last = null;
                if (_childs.size() > 0)
                    last = _childs.get(_childs.size() - 1);
                if (last != null && last instanceof StringBuffer) {
                    ( (StringBuffer) last).append( (String) obj);
                    obj = null;
                }
                else {
                    obj = new StringBuffer( (String) obj);
                }
            }
        } // convert String to StringBuffer or append it to the last element if it is a StringBuffer

        if (obj != null)
            _childs.add(obj);
    }

    public Object getChild(int index) {
        return _childs.get(index);
    }

    public int getChildCount() {
        return _childs.size();
    }

    public List getChilds() {
        return (List) ((ArrayList)_childs).clone();
    }
}
