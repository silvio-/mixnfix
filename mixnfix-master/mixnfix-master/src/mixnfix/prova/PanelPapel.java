package mixnfix.prova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;

import mixnfix.gui.App;
import mixnfix.gui.Images;
import mixnfix.gui.MainFrame;
import mixnfix.gui.ModelProva;

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
public class PanelPapel extends JPanel {

    JTextPane _textPane = new JTextPane();

    public static final Style STYLE_REGULAR = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

    public static Style STYLE_FORMULA;

    private Papel _papel;

    private ModelProva _provaModel;

    public JTextPane getTextPane() {
        return _textPane;
    }

    public void focusTextArea() {
        _textPane.requestFocus();
    }

    public PanelPapel(Papel papel) {
        if (papel == null)
            papel = new Papel();


        _papel = papel; // papel;

        Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(103, 101, 98), new Color(148, 145, 140));
        TitledBorder titledBorder = new TitledBorder(border, "Enunciado: ");
        this.setBorder(titledBorder);


        JButton btnImportarFigura = new JButton();
        btnImportarFigura.setPreferredSize(new Dimension(30, 25));
        btnImportarFigura.setToolTipText("Colar para Clipboarder");
        btnImportarFigura.setBorderPainted(false);
        btnImportarFigura.setMargin(new Insets(2, 2, 2, 2));
        btnImportarFigura.setText("C>");
        btnImportarFigura.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
/*
                                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionPasteModelOnClipboard(grupo)).actionPerformed(e);
                }
 */
            }
        });

        JPanel panelButtonsPapel = new JPanel();
        panelButtonsPapel.setLayout(new FlowLayout());
        panelButtonsPapel.add(btnImportarFigura,null);

        _provaModel = (ModelProva) _papel.getAscendentByClass(ModelProva.class);

        this.setLayout(new BorderLayout());
        // this.add(panelButtonsPapel, BorderLayout.NORTH);
        this.add(new JScrollPane(_textPane), BorderLayout.CENTER);

        StyledDocument doc = _textPane.getStyledDocument();

        InputMap inputMap = _textPane.getInputMap();
        doc.setCharacterAttributes(0,doc.getLength(),getStyle("normal"),true);
        try {
            doc.insertString(doc.getLength(), " ", getStyle("normal"));
            doc.remove(0,1);
        }
        catch (BadLocationException ex1) {
            ex1.printStackTrace();
        }

        // ------------------------------------------------------
        DefaultStyledDocument d = (DefaultStyledDocument) doc;
        DocFilter df = new DocFilter(d);
        d.setDocumentFilter(df);
        // ------------------------------------------------------


        KeyStroke key;

        // key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        // inputMap.put(key, new UndoAction());

        key = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);
        inputMap.put(key, DefaultEditorKit.downAction);


        key = KeyStroke.getKeyStroke(KeyEvent.VK_T, Event.CTRL_MASK);
        inputMap.put(key, new ChangeStyleAction("tex",getStyle("tex")));

        key = KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK);
        inputMap.put(key, new ChangeStyleAction("normal",getStyle("normal")));

        key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
        inputMap.put(key, new ChangeStyleAction("bold",getStyle("bold")));

        key = KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK);
        inputMap.put(key, new ChangeStyleAction("italic",getStyle("italic")));

        key = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);
        inputMap.put(key, new MFActionChooseImage());

        key = KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK);
        inputMap.put(key, new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                read();
            }
        });

        Papel p = _papel;
        if (p != null) {
            for (NoPapel n : p.getContents()) {
                try {
                    if (n instanceof Texto) {
                        doc.insertString(doc.getLength(), ( (Texto) n).getTexto(), getStyle("normal"));
                    }
                    else if (n instanceof Estilo) {
                        Estilo e = (Estilo) n;
                        boolean bold = e.isBold();
                        boolean italic = e.isItalic();
                        if (bold) {
                            doc.insertString(doc.getLength(), e.getTexto(), getStyle("bold"));
                        }
                        else if (italic) {
                            doc.insertString(doc.getLength(), e.getTexto(), getStyle("italic"));
                        }
                        else {
                            doc.insertString(doc.getLength(), e.getTexto(), getStyle("normal"));
                        }
                    }
                    else if (n instanceof Imagem) {
                        String fileName = n.getProperty("src");

                        // gamibarra para provas antigas
                        fileName = fileName.replaceFirst(".eps",".jpg");

                        SimpleAttributeSet style = getStyleFigura(new File(_provaModel.getPath().getAbsolutePath()+"/"+fileName),n.getProperty("zoom"));
                        doc.insertString(doc.getLength()," ",style);
                    }
                    else if (n instanceof Formula) {
                        Formula f = (Formula) n;
                        Object o = f.getProperty(Tags.TAG_MODO);
                        if (o != null && o.equals(Tags.TAG_MODO_TEX))
                            doc.insertString(doc.getLength(), ( (Formula) n).getTexto(), getStyle("tex"));
                        else
                            doc.insertString(doc.getLength(), "$"+( (Formula) n).getTexto()+"$", getStyle("tex"));
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        _textPane.getStyledDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                _changed = true;
                fireChange();
            }
            public void removeUpdate(DocumentEvent e) {
                _changed = true;
                fireChange();
            }
            public void changedUpdate(DocumentEvent e) {
                _changed = true;
                fireChange();
            }
        });
    }

    private boolean _changed = false;
    public boolean changed() {
        return _changed;
    }


    ArrayList<PanelPapelListener> _listeners = new ArrayList<PanelPapelListener>();
    public void addListener(PanelPapelListener l) {
        _listeners.add(l);
    }
    public void removeListener(PanelPapelListener l) {
        _listeners.remove(l);
    }
    public void clearListeners() {
        _listeners.clear();
    }
    private void fireChange() {
        for (PanelPapelListener ppl: _listeners)
            ppl.changed(this);
    }


    public void readDFS(Element e, int gap) {
        if (e.getElementCount() > 0) {
            for (int i = 0; i < e.getElementCount(); i++) {
                for (int k=0;k<gap;k++)
                    System.out.print("---");
                System.out.println("-- internal node");
                readDFS(e.getElement(i),gap+1);
            }
        }
        else {
            Object o = e.getAttributes().getAttribute("_id");
            for (int i=0;i<gap;i++)
                System.out.print("---");
            if (o != null && "tex".equals(o)) {
                System.out.println( "-- tex: "+e.getStartOffset()+" "+e.getEndOffset());
            }
            else {
                System.out.println("-- normal: "+e.getStartOffset()+" "+e.getEndOffset());
            }
        }
    }

    public void read() {
        StyledDocument doc = _textPane.getStyledDocument();
        Element[] rootElements = doc.getRootElements();
        for (Element e: rootElements) {
            readDFS(e,0);
        }
    }

    /**
     * A hash table containing the text styles.
     * Simple attribute sets are hashed by name (String)
     */
    private  static Hashtable styles = new Hashtable();

    static {
        initStyles();
    }

    /**
     * retrieve the style for the given type of text.
     *
     * @param styleName the label for the type of text ("tag" for example)
     *      or null if the styleName is not known.
     * @return the style
     */
    public static SimpleAttributeSet getStyle(String styleName){
        return ((SimpleAttributeSet)styles.get(styleName));
    }

    /**
     * Create the styles and place them in the hash table.
     */
    public SimpleAttributeSet getStyleFigura(File file, String zoom){
        SimpleAttributeSet style = new SimpleAttributeSet();
        style.addAttribute("_id","figura");
        try {
            float fzoom = 0.0f;
            try {
                fzoom = Float.parseFloat(zoom);
            }
            catch (Exception ex) {
                fzoom = 1.0f;
            }
            ImagePanel ip = new ImagePanel(file,fzoom);
            style.addAttribute("component",ip);
            StyleConstants.setComponent(style, ip);
        }
        catch (Exception ex1) {
            ex1.printStackTrace();
        }
        return style;
    }

    /**
     * Create the styles and place them in the hash table.
     */
    public static void initStyles(){
        int baseFontSize = 16;


        SimpleAttributeSet style;

        style = new SimpleAttributeSet();
        style.addAttribute("_id","tex");
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, baseFontSize);
        StyleConstants.setBackground(style, Color.pink);
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        styles.put("tex", style);

        style = new SimpleAttributeSet();
        style.addAttribute("_id","normal");
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, baseFontSize);
        StyleConstants.setBackground(style, Color.white);
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, false);
        styles.put("normal", style);

        style = new SimpleAttributeSet();
        style.addAttribute("_id","bold");
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, baseFontSize);
        StyleConstants.setBackground(style, Color.white);
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, true);
        StyleConstants.setItalic(style, false);
        styles.put("bold", style);

        style = new SimpleAttributeSet();
        style.addAttribute("_id","italic");
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, baseFontSize);
        StyleConstants.setBackground(style, Color.white);
        StyleConstants.setForeground(style, Color.black);
        StyleConstants.setBold(style, false);
        StyleConstants.setItalic(style, true);
        styles.put("italic", style);
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("Teste");
        Papel p = new Papel();
        f.setContentPane(new PanelPapel(p));
        linsoft.gui.util.Library.resizeAndCenterWindow(f,800,600);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }


    /**
     * An action to toggle the bold attribute.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    public static class ChangeStyleAction
        extends StyledEditorKit.StyledTextAction {

        private SimpleAttributeSet _sas;
        /**
         * Constructs a new BoldAction.
         */
        public ChangeStyleAction(String name, SimpleAttributeSet sas) {
            super(name);
            _sas = sas;
        }

        /**
         * Toggles the bold attribute.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JEditorPane editor = getEditor(e);
            if (editor != null) {
                setCharacterAttributes(editor, _sas, true);
            }
        }
    }


    private ArrayList<Element> _elements = new ArrayList<Element>();

    public boolean hit(Element e) {
        for (Element ee: _elements) {
            if (!(e.getEndOffset() <= ee.getStartOffset() ||
                  ee.getEndOffset() <= e.getStartOffset()))
                return true;
        }
        return false;
    }

    public void elementsDFS(Element e) {
        HashSet<String> set = new HashSet<String>();
        set.add("figura");
        set.add("normal");
        set.add("bold");
        set.add("italic");
        set.add("tex");

        // System.out.println("DFS: "+e+" "+e.getStartOffset()+" "+e.getEndOffset()+" "+e.getAttributes().getAttribute("_id"));
        if (e.getElementCount() > 0) {
            for (int i = 0; i < e.getElementCount(); i++) {
                elementsDFS(e.getElement(i));
            }
        }
        else {
            if (set.contains(e.getAttributes().getAttribute("_id")))
                _elements.add(e);
        }
    }

    public Papel getPapelFromText() {
        StyledDocument doc = _textPane.getStyledDocument();
        _elements.clear();
        Element[] rootElements = doc.getRootElements();
        for (Element e: rootElements) {
            elementsDFS(e);
        }

        Papel p = new Papel();

        // now mount the thing
        for (Element e: _elements) {
            String id = (String) e.getAttributes().getAttribute("_id");

            System.out.println(""+e+" "+id+" "+e.getStartOffset()+" "+e.getEndOffset());

            if (id == null) {
                Texto t = new Texto();
                try {
                    t.setTexto(doc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()));
                }
                catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                p.addChild(t);
            }

            else if ("normal".equals(id)) {
                Texto t = new Texto();
                try {
                    t.setTexto(doc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()));
                }
                catch (BadLocationException ex1) {
                    ex1.printStackTrace();
                }
                p.addChild(t);
            }

            else if ("bold".equals(id)) {
                Estilo es = new Estilo();
                try {
                    es.setTexto(doc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()));
                    es.setProperty("fonte","bold");
                }
                catch (BadLocationException ex1) {
                    ex1.printStackTrace();
                }
                p.addChild(es);
            }

            else if ("italic".equals(id)) {
                Estilo es = new Estilo();
                try {
                    es.setTexto(doc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()));
                    es.setProperty("fonte","italic");
                }
                catch (BadLocationException ex1) {
                    ex1.printStackTrace();
                }
                p.addChild(es);
            }

            else if ("tex".equals(id)) {
                Formula f = new Formula();
                try {
                    f.setTexto(doc.getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset()));
                }
                catch (BadLocationException ex2) {
                    ex2.printStackTrace();
                }
                f.setProperty("modo", "tex");
                p.addChild(f);
            }

            else if ("figura".equals(id)) {
                Imagem i = new Imagem();
                ImagePanel ip = (ImagePanel) e.getAttributes().getAttribute("component");

                System.out.println("src: "+ip.getSrc());
                // System.out.println("original: "+ip.getOriginal());

                i.setProperty("src",ip.getSrc());
                // i.setProperty("original",ip.getOriginal());
                i.setProperty("zoom", String.format("%.3f",ip.getZoom()));
                p.addChild(i);
            }
        }
        return p;
    }


    class MFActionChooseImage extends AbstractAction {
        public MFActionChooseImage() {
            super("Choose Image", Images.Correcao16x16);
        }

        public void actionPerformed(ActionEvent e) {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    if (f.isDirectory())
                        return true;
                    String name = f.getName().toLowerCase();
                    return (name.endsWith(".jpg")) ||
                           (name.endsWith(".jpeg"));
                }
                public String getDescription() {
                    return "Fotos (.jpg ou .jpeg)";
                }
            });

            { // define default file
                String file = App.getProperty("lastfotoforedition");
                if (file != null)
                    jfc.setSelectedFile(new File(file));
            }

            int result = jfc.showOpenDialog(MainFrame.MAIN_FRAME);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {

                    File f = jfc.getSelectedFile();

                    App.setProperty("lastfotoforedition", f.getCanonicalPath());

                    String ff = _provaModel.assureImageFileName(f.getName());
                    File targetFile = new File(_provaModel.getPath().getCanonicalPath()+"/"+ff.toLowerCase());

                    mixnfix.Library.copyFile(f, targetFile);

                    //
                    SimpleAttributeSet style = getStyleFigura(targetFile,"1.0");
                    System.out.println("Caret Position: "+_textPane.getCaretPosition());
                    _textPane.getStyledDocument().insertString(_textPane.getCaretPosition()," ", style);
                    System.out.println("Caret Position (after image): "+_textPane.getCaretPosition());
                    _textPane.getStyledDocument().insertString(_textPane.getCaretPosition()," ", getStyle("normal"));
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    class ImagePanel extends JPanel {
        private File _imgFile;
        private double _zoom;
        private BufferedImage _img;
        public ImagePanel(File imgFile, double zoom) throws IOException {
            _imgFile = imgFile;
            _zoom = zoom;

            System.out.println("img file: "+imgFile.getAbsolutePath());
            _img = ImageIO.read(imgFile);
            adjustZoom();
            this.setOpaque(true);
            this.setBackground(Color.WHITE);
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1)
                        changeZoom();
                }
            });

        }

        private void adjustZoom() {
            this.setPreferredSize(new Dimension((int)(_zoom * _img.getWidth()),(int)(_zoom * _img.getHeight())));
            this.setSize(new Dimension((int)(_zoom * _img.getWidth()),(int)(_zoom * _img.getHeight())));
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D)g;
            g2.scale(_zoom,_zoom);
            g2.drawImage(_img,0,0,null);
        }

        public void changeZoom() {
            double value;
            while (true) {
                Object o = JOptionPane.showInputDialog( this, "Change zoom", String.format("%.3f", _zoom));
                if (o == null) {
                    return;
                }
                try {
                    value = Double.parseDouble( (String) o);
                    break;
                }
                catch (NumberFormatException ex1) {
                    JOptionPane.showMessageDialog(this, "Numero inválido (e.g. 1.0 é válido). Tente de novo ou cancele.");
                }
            }
            _zoom = value;

            // signal change
            _changed = true;

            this.adjustZoom();
            this.revalidate();
        }

        public double getZoom() {
            return _zoom;
        }

        public String getOriginal() {
            return _imgFile.getAbsolutePath();
        }

        public String getSrc() {
            return _imgFile.getName();
        }
    }

}



class DocFilter extends DocumentFilter {
    private StyledDocument _doc;

    public DocFilter(StyledDocument doc) {
        _doc = doc;
    }

    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        super.remove(fb, offset, length);
    }

    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (attr.getAttribute("_id").equals("figura")) {
            System.out.println("Estou no contexto de uma figura!!!");
        }
        super.insertString(fb, offset, string, attr);
    }

    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        super.replace(fb, offset, length, text, attrs);
    }

    private boolean containsSpecialElement(int offset, int length) {
        Element e = _doc.getCharacterElement(offset);
        while (e != null) {
            if (checkSpecialElement(e))
                return true;
            else if (e.getEndOffset()>=offset+length-1)
                break;
            else
                e = _doc.getCharacterElement(e.getEndOffset());
        }
        return false;
    }

    private boolean checkSpecialElement(Element e) {
        AttributeSet set = e.getAttributes();
        if (set.getAttribute(javax.swing.text.StyleConstants.ComponentAttribute) != null) {
            return true;
        }
        else {
            return false;
        }
    }
}
