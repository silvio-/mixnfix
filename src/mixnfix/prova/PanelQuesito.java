package mixnfix.prova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import mixnfix.Controller;

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
public class PanelQuesito extends JPanel {
    private Quesito _quesito;
    private JTextPane _textPane = new JTextPane();

    public static final Style STYLE_REGULAR = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
    public static Style STYLE_REGULAR_UNEDITABLE;
    public static Style STYLE_FORMULA;
    public static Style STYLE_BOLD;
    public static Style STYLE_ITALIC;

    public PanelQuesito(Quesito quesito) {
        _quesito = quesito;

        try {

            _textPane.setPreferredSize(new Dimension(600,300));

            //
            StyledDocument doc = _textPane.getStyledDocument();
            STYLE_FORMULA = doc.addStyle("formula", STYLE_REGULAR);
            STYLE_REGULAR_UNEDITABLE = doc.addStyle("uneditable",STYLE_REGULAR);
            StyleConstants.setBackground(STYLE_FORMULA, Color.YELLOW);
            STYLE_BOLD = doc.addStyle("boldstyle",STYLE_REGULAR);
            StyleConstants.setBold(STYLE_BOLD, true);
            STYLE_ITALIC = doc.addStyle("italicstyle",STYLE_REGULAR);
            StyleConstants.setBold(STYLE_ITALIC, true);

            // ------------------------------------------------------
            DefaultStyledDocument d = (DefaultStyledDocument) doc;
            DF df = new DF(d,STYLE_REGULAR);
            d.setDocumentFilter(df);
            // ------------------------------------------------------

            Style s = doc.addStyle("enunciado", STYLE_REGULAR);
            StyleConstants.setComponent(s, newLabel("Enunciado"));
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), "\n", s);
            this.addPapel(doc, _quesito.getEnunciado());

            int j = 0;
            for (ItemQuesito i : _quesito.getItens()) {
                doc.insertString(doc.getLength(), "\n", STYLE_REGULAR);
                s = doc.addStyle("item: " + (j + 1), STYLE_REGULAR);
                StyleConstants.setComponent(s, newLabel("item: " + (j + 1)));
                StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
                doc.insertString(doc.getLength(), "\n", s);
                this.addPapel(doc, i.getEnunciado());
                j++;
            }

            doc.insertString(doc.getLength(), "\n", STYLE_REGULAR);

            s = doc.addStyle("solucao", STYLE_REGULAR);
            StyleConstants.setComponent(s, newLabel("Solução"));
            StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
            doc.insertString(doc.getLength(), "\n", s);
            doc.insertString(doc.getLength(), "\n", STYLE_REGULAR);
            this.addPapel(doc, _quesito.getSolucao());

        }
        catch (BadLocationException ex) {
            ex.printStackTrace();
        }

        this.setLayout(new BorderLayout());
        // panel com as propriedades do quesito
        PanelProperties pp = new PanelProperties(_quesito.getProperties());
        pp.setPreferredSize(new Dimension(100,200));
        this.add(pp,BorderLayout.NORTH);
        this.add(new JScrollPane(_textPane),BorderLayout.CENTER);

        JScrollPane p = new JScrollPane(_taStatus);
        p.setPreferredSize(new Dimension(200,100));
        this.add(p,BorderLayout.SOUTH);

        _textPane.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                writeStatus();
            }
        });




    }
    private JTextArea _taStatus = new JTextArea();


    private void writeStatus() {
        _taStatus.setText("");
        int dot = _textPane.getCaret().getDot();
        StyledDocument doc = _textPane.getStyledDocument();
        Element e = doc.getCharacterElement(dot);
        _taStatus.append("["+e.getStartOffset()+","+e.getEndOffset()+"]\n");
        AttributeSet as = e.getAttributes();

        Enumeration enumeration = as.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            Object an = enumeration.nextElement();
            _taStatus.append(""+an+" -> "+as.getAttribute(an)+"\n");
        }
    }

    private JComponent newLabel(String value) {
        JCheckBox l = new JCheckBox(value);
        l.setFont(new Font(l.getFont().getFontName(),Font.BOLD,l.getFont().getSize()+2));
        return l;
    }

    private void addPapel(StyledDocument doc, Papel p) {
        if (p != null) {
            for (NoPapel n : p.getContents()) {
                try {
                    if (n instanceof Texto) {
                        doc.insertString(doc.getLength(), ( (Texto) n).getTexto(), STYLE_REGULAR);
                    }
                    else if (n instanceof Imagem) {
                        File f = new File(n.getProperty("original"));
                        ImageIcon i = new ImageIcon(Controller.TMP_DIR+"/"+f.getName());
                        Style s = doc.addStyle(""+System.currentTimeMillis(),STYLE_REGULAR);
                        StyleConstants.setIcon(s, i);
                        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
                        doc.insertString(doc.getLength()," ",s);
                    }
                    else if (n instanceof Formula) {
                        doc.insertString(doc.getLength(), ( (Formula) n).getTexto(), STYLE_FORMULA);
                    }
                    else if (n instanceof Estilo) {
                        Estilo e = (Estilo) n;
                        if (e.isBold()) {
                            doc.insertString(doc.getLength(), e.getTexto(), STYLE_BOLD);
                        }
                        else if (e.isItalic()) {
                            doc.insertString(doc.getLength(), e.getTexto(), STYLE_ITALIC);
                        }
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}


class DF extends DocumentFilter {
    private StyledDocument _doc;
    private Style _regular;

    public DF(StyledDocument doc, Style regular) {
        _doc = doc;
        _regular = regular;
    }

    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
        if (this.containsSpecialElement(offset,length))
            return;
        super.remove(fb, offset, length);
    }

    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        super.insertString(fb, offset, string, attr);
    }

    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        boolean ok = false;
        if (offset > 0 && length == 0 && "\n".equals(text)) {
            attrs = _regular;
            ok = true;
        }
        else if (this.containsSpecialElement(offset,length))
            ok = false;
        else
            ok = true;
        if (ok)
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

    private boolean isAdjacentToSpecialElement(Element e) {
        /*
        Element next = _doc.getCharacterElement(e.getEndOffset()+1);
        Element previous = (e.getStartOffset()>0 ? _doc.getCharacterElement(e.getStartOffset()-1) : null);
        return (previous != null ? checkSpecialElement(previous) : false) || (next != null ? checkSpecialElement(next) : false);
        */
       return true;
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
