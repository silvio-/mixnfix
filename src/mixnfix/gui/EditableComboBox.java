package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class EditableComboBox extends JComboBox {
    Vector _values;
    public EditableComboBox(Object[] items) {
        super();
        _values = new Vector();
        for (Object o:items)
            _values.add(o);
        this.setEditable(true);
    }
    public EditableComboBox(Vector labels, Vector values) {
        super(labels);
        this.setEditable(true);
        _values = values;
    }
    public Object getValue() {
        if (!isNewValue())
            return _values.get(this.getSelectedIndex());
        else
            return this.getEditor().getItem();
    }
    public boolean isNewValue() {
        Object editorValue = this.getEditor().getItem();
        if (this.getSelectedIndex() == -1 || this.getSelectedItem() == null)
            return true;
        else if (!editorValue.equals(this.getSelectedItem()))
            return true;
        else
            return false;
    }

    public static void main(String[] args) {
        final EditableComboBox x = new EditableComboBox(new Object[] {"Domingo","Segunda","Terça","Quarta","Quinta","Sexta","Sábado"});
         JPanel p = new JPanel(new GridBagLayout());
         p.add(x, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
         p.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0),"refresh");
         p.getActionMap().put("refresh", new AbstractAction() {
             public void actionPerformed(ActionEvent e) {
                 System.out.println("Is a new value? "+x.isNewValue());
                 System.out.println("Value Class: "+x.getValue().getClass());
                 System.out.println("Value: "+x.getValue());
             }
         });

         JFrame f = new JFrame();
         f.setContentPane(p);
         f.setBounds(0, 0, 200, 100);
         f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         f.setVisible(true);
    }
}

