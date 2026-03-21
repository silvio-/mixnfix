package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 * PanelChooseObjectFromCollection
 */
public class PanelChooseObjectFromCollection extends JPanel {
    JList _list = new JList();
    DefaultListModel _model = new DefaultListModel();
    boolean _ok = false;
    public PanelChooseObjectFromCollection(Collection c, ListCellRenderer renderer) {
        _ok = false;
        _list.setModel(_model);
        for (Object o:c)
            _model.addElement(o);
        if (renderer != null)
            _list.setCellRenderer(renderer);
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // create buttons and their actions
        JButton btnOk = new JButton("OK");
        JButton btnCancel = new JButton("Cancel");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        JPanel p = new JPanel();
        p.add(btnOk);
        p.add(btnCancel);

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(_list),BorderLayout.CENTER);
        this.add(p,BorderLayout.SOUTH);

    }

    public void ok() {
        _ok = true;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void cancel() {
        _ok = false;
        this.getTopLevelAncestor().setVisible(false);
    }

    public Object getSelectedObject() {
        if (_ok)
            return _list.getSelectedValue();
        else
            return null;
    }

    public static Object run(JFrame parent, Collection c, ListCellRenderer renderer, String title, int width, int height) {
        JDialog d = new JDialog(parent, title, true);
        linsoft.gui.util.Library.resizeAndCenterWindow(d, width, height);
        PanelChooseObjectFromCollection p = new PanelChooseObjectFromCollection(c,renderer);
        d.setContentPane(p);
        d.setVisible(true);
        return p.getSelectedObject();
    }
}
