

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

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
public class X {
    public X() {
    }

    public static void main(String[] args) throws Exception {
        DataOutputStream fw = new DataOutputStream(new FileOutputStream("c:/workspace/mnfimg/c/data/x_test.dat"));
        int i = Float.floatToIntBits(1);
        fw.write(0xFF & i);
        fw.write(0xFF & (i >> 8));
        fw.write(0xFF & (i >> 16));
        fw.write(0xFF & (i >> 24));
        // fw.writeInt(i);
        fw.close();
    }

    public static void main2(String[] args) {
        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
            e.printStackTrace();
        }        ImageIcon i1 = new ImageIcon("resources/images/importacao.png");
        /*
        ImageIcon i2 = new ImageIcon("resources/images/correcao.png");
        JFrame f = new JFrame();
        JButton btn1 = new JButton(i1);
        JButton btn2 = new JButton(i2);
        f.getContentPane().setLayout(new FlowLayout());
        f.getContentPane().add(btn1);
        f.getContentPane().add(btn2);
        f.setBounds(0,0,200,100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        */



        final JComboBox x = new JComboBox();// new String[] {"Sábado", "Domingo", "Segunda"});
        x.setEditable(true);
        JPanel p = new JPanel(new GridBagLayout());
        p.add(x, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        p.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5,0),"refresh");
        p.getActionMap().put("refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.out.println(""+x.getSelectedItem());
                System.out.println(""+x.getSelectedIndex());
                System.out.println(""+x.getPrototypeDisplayValue());
                System.out.println("Value:"+x.getEditor().getItem());
            }
        });



        JFrame f = new JFrame();
        f.setContentPane(p);
        f.setBounds(0,0,200,100);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}

class EditableComboBox extends JComboBox {
    public EditableComboBox(Object[] items) {
        super(items);
        this.setEditable(true);
    }
    public Object getValue() {
        if (!isNewValue())
            return this.getSelectedItem();
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
}
