package mixnfix.gui;

import java.sql.SQLException;

import javax.swing.tree.DefaultTreeModel;

import mixnfix.Model;
import mixnfix.prova.Grupo;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

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
public class MFTreeModel extends DefaultTreeModel {

    ModelMF _model;

    MFTreeNode _root;
    MFTree _tree;

    ProvaStructure _structure;

    public static final int TYPE_INSTITUICAO_TREE_MODEL = 0;
    public static final int TYPE_PROVA_STRUCTURE_TREE_MODEL = 1;

    private int _type;
    public int getType() { return _type; }


    public MFTreeModel(MFTree tree, ModelMF model) throws SQLException {
        super(null);

        _type = TYPE_INSTITUICAO_TREE_MODEL;

        _tree = tree;
        _model = model;

        // create only with instituiocoes
        _root = new MFTreeNode(_tree,_model,MFTreeNode.TYPE_ROOT,this);

        // load at startup
        _model.getInstituicoes();

        //
        this.setRoot(_root);
    }

    public MFTreeModel(MFTree tree, ModelMF model, ProvaStructure structure) throws SQLException {
        super(null);

        _type = TYPE_PROVA_STRUCTURE_TREE_MODEL;

        _tree = tree;
        _model = model;
        _structure = structure;

        _root = new MFTreeNode(_tree, _structure.getRoot(), MFTreeNode.TYPE_PROVA_GRUPO, this);
        tree.setRootVisible(false);


        _root.insert(new MFTreeNode(_tree, _structure, MFTreeNode.TYPE_PROVA_STRUCTURE, this),0);

        // create only with instituiocoes
        for (Model m: _structure.getRoot().getChilds()) {
            if (m instanceof Quesito) {
                _root.add(new MFTreeNode(_tree, m, MFTreeNode.TYPE_PROVA_QUESITO, this));
                ((Quesito) m).fireInitStructure();
            }
            else if (m instanceof Grupo) {
                _root.add(new MFTreeNode(_tree, m, MFTreeNode.TYPE_PROVA_GRUPO, this));
                ((Grupo) m).fireInitStructure();
            }

        }
        //
        this.setRoot(_root);
    }

    public MFTreeNode getRoot() {
        return _root;
    }

}
