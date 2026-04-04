package mixnfix.gui;

import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import mixnfix.Model;
import mixnfix.ModelListener;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

public class MFTreeNode
    extends DefaultMutableTreeNode {
    public static final byte TYPE_ROOT = (byte) -1;
    public static final byte TYPE_INSTITUICAO = (byte) 0;
    public static final byte TYPE_PROVAS = (byte) 1;
    public static final byte TYPE_ALUNOS = (byte) 2;
    public static final byte TYPE_TURMAS = (byte) 3;
    public static final byte TYPE_CURSOS = (byte) 4;
    public static final byte TYPE_PROVA = (byte) 5;
    public static final byte TYPE_ALUNO = (byte) 6;
    public static final byte TYPE_TURMA = (byte) 7;
    public static final byte TYPE_CURSO = (byte) 8;
    public static final byte TYPE_CURSO_INSTANCIA = (byte) 9;
    public static final byte TYPE_CORRECAO = (byte) 10;
    public static final byte TYPE_AVALIACAO = (byte) 11;
    public static final byte TYPE_ALUNO_CURSO_INSTANCIA = (byte) 12;
    public static final byte TYPE_AVALIACAO_ALUNO = (byte) 13;
    public static final byte TYPE_ALUNO_TURMA = (byte) 14;
    public static final byte TYPE_PROVA_CORRECAO = (byte) 15;
    public static final byte TYPE_CORRECOES_ANTIGAS = (byte) 16;
    public static final byte TYPE_DESCONHECIDO = (byte) 17;

    public static final byte TYPE_PROVA_STRUCTURE = (byte) 18;
    public static final byte TYPE_PROVA_GRUPO = (byte) 19;
    public static final byte TYPE_PROVA_QUESITO = (byte) 20;
    public static final byte TYPE_PROVA_ITEM_QUESITO = (byte) 21;

    public static final byte TYPE_COLETA_QUESTIONARIO = (byte) 22;

    private byte _type;
    private ModelListener _listener;
    private MFTreeModel _treeModel;
    private MFTree _tree;

    public MFTreeNode(MFTree tree, Model model, byte type, MFTreeModel treeModel) {
        super(model);
        _tree = tree;
        _treeModel = treeModel;

        // define listener
        _listener = new ModelListener() {

            public void update(Model model) {
                _tree.repaint();
            }

            public void nodeAdded(Model model, Model addedModel, int index) {
                if (model == _treeModel.getRoot().getUserObject()) { // gambiarra pra que o cabeçalho nao se misure na arvore
                    index++;
                }
                insert(addedModel,index);
            }

            public void nodesAdded(Model model, List<Model> addedModel, int index) {
                if (model == _treeModel.getRoot().getUserObject()) { // gambiarra pra que o cabeçalho nao se misure na arvore
                    index++;
                }
                insert(addedModel,index);
            }

            public void nodesLoaded(Model model, List<Model> addedModel) {
                insert(addedModel,0);
            }

            public void nodeRemoved(Model model, Model removedModel) {
                remove(removedModel);
            }
        };

        _type = type;
        model.addListener(_listener);
    }

    public byte getType(Object m) {
        byte type = TYPE_DESCONHECIDO;
        if (m instanceof ModelProva) type = TYPE_PROVA;
        else if (m instanceof ModelColetaQuestionario) type = TYPE_COLETA_QUESTIONARIO;
        else if (m instanceof ModelProvaCorrecao) type = TYPE_PROVA_CORRECAO;
        else if (m instanceof ModelInstituicao) type = TYPE_INSTITUICAO;
        else if (m instanceof ModelCursoInstancia) type = TYPE_CURSO_INSTANCIA;
        else if (m instanceof ProvaStructure) type = TYPE_PROVA_STRUCTURE;
        else if (m instanceof Grupo) type = TYPE_PROVA_GRUPO;
        else if (m instanceof Quesito) type = TYPE_PROVA_QUESITO;
        else if (m instanceof ItemQuesito) type = TYPE_PROVA_ITEM_QUESITO;
        return type;
    }

    public static boolean ADD_ACTION = true;
    public static boolean NO_ACTION = false;
    public boolean getAction(byte childType) {
        boolean result = NO_ACTION;

        if (_treeModel.getType() == MFTreeModel.TYPE_INSTITUICAO_TREE_MODEL) {
            if (this._type == TYPE_INSTITUICAO && childType == TYPE_PROVA) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA && childType == TYPE_PROVA_CORRECAO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA && childType == TYPE_COLETA_QUESTIONARIO) result = ADD_ACTION;
            else if (this._type == TYPE_ROOT && childType == TYPE_INSTITUICAO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVAS && childType == TYPE_PROVA) result = ADD_ACTION;
        }
        else if (_treeModel.getType() == MFTreeModel.TYPE_PROVA_STRUCTURE_TREE_MODEL) {
            if (this._type == TYPE_PROVA_STRUCTURE && childType == TYPE_PROVA_GRUPO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA_STRUCTURE && childType == TYPE_PROVA_QUESITO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA_GRUPO && childType == TYPE_PROVA_GRUPO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA_GRUPO && childType == TYPE_PROVA_QUESITO) result = ADD_ACTION;
            else if (this._type == TYPE_PROVA_QUESITO && childType == TYPE_PROVA_ITEM_QUESITO) result = ADD_ACTION;
        }


        return result;
    }

    public void insert(List<Model> childs, int index) {
        if (childs.size() == 0)
            return;

        int i = index;
        for (Model x : childs) {
            byte type = getType(x);
            if (getAction(type) == ADD_ACTION) {
                MFTreeNode tn = new MFTreeNode(_tree, x, type, _treeModel);
                _treeModel.insertNodeInto(tn, this, i++);
            }
        }
    }

    public void insert(Model child, int index) {
        byte type = getType(child);
        if (getAction(type) == ADD_ACTION) {
            MFTreeNode tn = new MFTreeNode(_tree,child, type, _treeModel);
            _treeModel.insertNodeInto(tn, this, index);
            _tree.expandPath(new TreePath(this.getPath()));
        }
    }

    public void remove(Model child) {
        MFTreeNode x = findChild(child);
        if (x != null)
            _treeModel.removeNodeFromParent(x);
    }

    public MFTreeNode findChild(Model m) {
        for (int i=0;i<this.getChildCount();i++) {
            MFTreeNode tn = (MFTreeNode) this.getChildAt(i);
            if (m == tn.getUserObject())
                return tn;
        }
        return null;
    }

    public MFTreeNode findChildRecursevly(Model m) {
        for (int i=0;i<this.getChildCount();i++) {
            MFTreeNode tn = (MFTreeNode) this.getChildAt(i);
            if (m == tn.getUserObject())
                return tn;
            MFTreeNode tn2 = tn.findChild(m);
            if (tn2 != null)
                return tn2;
        }
        return null;
    }


    public int getType() {
        return _type;
    }
}
