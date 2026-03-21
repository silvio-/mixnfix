package mixnfix.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mixnfix.Model;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

/**
 * JTree extension
 */
public class MFTree extends JTree {

    private ArrayList<MFTreeListener> _listeners = new ArrayList<MFTreeListener>();

    public void addListener(MFTreeListener l) { _listeners.add(l); }

    public void fireSelect(Model m) {
        for (MFTreeListener l: _listeners)
            l.select(m);
    }

    public void fireMenu(Model m, int x, int y) {
        for (MFTreeListener l: _listeners)
            l.menu(this,m,x,y);
    }

    public MFTree() {
        super();
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        this.setCellRenderer(new MFTreeCellRenderer());
        this.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                MFTreeNode tn = (MFTreeNode) e.getPath().getLastPathComponent();
                select(tn);
            }
        });
        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // not a right click?
                if (e.getButton() != MouseEvent.BUTTON3)
                    return;
                //
                Object o = getSelectionPath();
                if (o != null) {
                    MFTreeNode mftn = (MFTreeNode) getSelectionPath().getLastPathComponent();
                    fireMenu( (Model) mftn.getUserObject(), e.getX(), e.getY());
                }
            }
        });

        this.setRootVisible(true);
    }

    public void select(MFTreeNode n) {
        if (n.getType() == MFTreeNode.TYPE_PROVA) {
            ModelProva mp = (ModelProva) n.getUserObject();
            try {
                mp.getProvaStructure();
                mp.getProvasCorrecoes();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            fireSelect(mp);
        }
        else if (n.getType() == MFTreeNode.TYPE_INSTITUICAO) {
            ModelInstituicao mp = (ModelInstituicao) n.getUserObject();
            try {
                mp.getProvas();
            }
            catch (SQLException ex) {
                ex.printStackTrace();
            }
            fireSelect(mp);
        }
        else if (n.getType() == MFTreeNode.TYPE_PROVA_CORRECAO) {
            ModelProvaCorrecao mp = (ModelProvaCorrecao) n.getUserObject();
            fireSelect(mp);
        }
        else if (n.getType() == MFTreeNode.TYPE_COLETA_QUESTIONARIO) {
            ModelColetaQuestionario mp = (ModelColetaQuestionario) n.getUserObject();
            fireSelect(mp);
        }
        else if (n.getType() == MFTreeNode.TYPE_PROVA_STRUCTURE) {
            ProvaStructure g = (ProvaStructure) n.getUserObject();
            fireSelect(g);
        }
        else if (n.getType() == MFTreeNode.TYPE_PROVA_GRUPO) {
            Grupo g = (Grupo) n.getUserObject();
            fireSelect(g);
        }
        else if (n.getType() == MFTreeNode.TYPE_PROVA_QUESITO) {
            Quesito q = (Quesito) n.getUserObject();
            fireSelect(q);
        }
        else if (n.getType() == MFTreeNode.TYPE_PROVA_ITEM_QUESITO) {
            ItemQuesito iq = (ItemQuesito) n.getUserObject();
            fireSelect(iq);
        }
    }

    public int getNumberOfSelectedNodes() {
        TreePath[] tps = this.getSelectionModel().getSelectionPaths();
        return tps.length;
    }

    public ModelSelection getSelectedNodesModels() {
        ArrayList<Model> result = new ArrayList<Model>();
        TreePath[] tps = this.getSelectionModel().getSelectionPaths();
        for (TreePath tp : tps) {
            Model model = (Model) ((MFTreeNode) tp.getLastPathComponent()).getUserObject();
            result.add(model);
        }
        return new ModelSelection(result);
    }
}
