package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.tree.TreePath;

import mixnfix.Model;
import mixnfix.ModelListener;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.PanelPapel;
import mixnfix.prova.PanelPapelListener;
import mixnfix.prova.PanelProperties;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;


public class PanelModelProva extends JPanel {
    private ModelProva _modelProva;
    private JSplitPane _splitPane;
    private MFTree _tree;

    private JButton _btnLockUnlock = new JButton();
    private JButton _btnAddGrupo = new JButton();
    private JButton _btnAddQuesito = new JButton();
    private JButton _btnAddItemQuesito = new JButton();
    private JButton _btnDeleteGrupo = new JButton();
    private JButton _btnDeleteQuesito = new JButton();
    private JButton _btnDeleteItemQuesito = new JButton();
    private JButton _btnMoverParaCima = new JButton();
    private JButton _btnMoverParaBaixo = new JButton();
    private JButton _btnChangeQuesitoValues = new JButton();
    private JButton _btnChangeQuesitoAnswer = new JButton();
    private JButton _btnChangeGrupoTag = new JButton();
    private JButton _btnChangeItemQuesitoTag = new JButton();
    private JButton _btnCopiarParaClipboard = new JButton();
    private JButton _btnColarDeClipboard = new JButton();
    private JButton _btnDesselecionar = new JButton();

    public PanelModelProva(ModelProva m) throws SQLException {
        _modelProva = m;

        //Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(103, 101, 98), new Color(148, 145, 140));
        // TitledBorder titledBorder = new TitledBorder(border, m.getProva().getNome());
        TitledBorder titledBorder = new TitledBorder(m.getProva().getNome());
        this.setBorder(titledBorder);

        m.addListener(new ModelListener() {
            public void update(Model model) {
                if (_modelProva.getStructureChanged())
                    setBackground(Color.RED);
                else
                    setBackground(_btnAddGrupo.getBackground());
            }
            public void nodeAdded(Model model, Model addedModel, int index){}
            public void nodesAdded(Model model, List<Model> addedModel, int index){}
            public void nodesLoaded(Model model, List<Model> addedModel){}
            public void nodeRemoved(Model model, Model removedModel){}
        });


        //----------------------------------------------------------
        // Início: Árvore

        JPanel panelTreeAndButtons = new JPanel();
        panelTreeAndButtons.setLayout(new BorderLayout());

        //---- add the tree ------------------------------
        _tree = new MFTree();
        MFTree mfTree = _tree;

        mfTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.isControlDown() && e.getButton() == MouseEvent.BUTTON3) {
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionAdicionarGrupo(_modelProva.getProvaStructure().getRoot())));
                    popup.add(new JMenuItem(new MFActionAdicionarQuesito(_modelProva.getProvaStructure().getRoot())));
                    popup.add(new JMenuItem(new MFActionSetDefaultTags(_modelProva.getProvaStructure())));
                    popup.add(new JMenuItem(new MFActionSetPrefixoNumericoNosTagsDosQuesitos(_modelProva.getProvaStructure())));
                    popup.show(_tree, e.getX(), e.getY());
                }
            }
        });


        mfTree.addListener(new MFTreeListener() {

            public void select(Model m) {

                if (_tree.getNumberOfSelectedNodes() > 1) {
                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(new JLabel("Seleção Múltipla",JLabel.CENTER));
                    _splitPane.setDividerLocation(x);
                }

                else if (m instanceof Grupo) {

                    // habilita botões grupo
                    enableGrupoButtons();

                    Grupo g = (Grupo) m;
                    PanelPapel pp = new PanelPapel(g.getEnunciado());

                    /**
                     * @todo melhorar a eficiencia da alteraçao de enunciado.
                     * Esta soluçao não é muito eficiente uma vez que
                     * sempre que há uma alteração no enunciado corrente
                     * o método pp.getPapelFromText() constrói um novo Papel
                     * com todo o novo enunciado (isso pra cada letrinha nova
                     * no enunciado). Mas como os enunciados nao sao tao grandes,
                     * ainda está com uma boa performance.
                     */
                    class Y implements PanelPapelListener {
                        private PanelPapel _pp;
                        private Grupo _grupo;
                        public Y(PanelPapel pp, Grupo g) {
                            _pp = pp;
                            _grupo = g;
                        }
                        public void changed(PanelPapel pp){
                            // System.out.println("Atualizando enunciado...");
                            _grupo.setEnunciado(pp.getPapelFromText());
                        }
                    }
                    pp.addListener(new Y(pp,g));

                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(g.getProperties());
                    sp.add(pprop,JSplitPane.BOTTOM);
                    sp.add(pp,JSplitPane.TOP);
                    sp.setDividerLocation(500);

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);

                    // focus
                    pp.focusTextArea();

                }

                else if (m instanceof ProvaStructure) {

                    // habilita botões do cabeçalho
                    enableCabecalhoButtons();

                    ProvaStructure g = (ProvaStructure) m;
                    PanelPapel pp = new PanelPapel(g.getCabecalho());

                    /**
                     * @todo melhorar a eficiencia da alteraçao de enunciado.
                     * Esta soluçao não é muito eficiente uma vez que
                     * sempre que há uma alteração no enunciado corrente
                     * o método pp.getPapelFromText() constrói um novo Papel
                     * com todo o novo enunciado (isso pra cada letrinha nova
                     * no enunciado). Mas como os enunciados nao sao tao grandes,
                     * ainda está com uma boa performance.
                     */
                    class Y implements PanelPapelListener {
                        private PanelPapel _pp;
                        private ProvaStructure _provaStructure;
                        public Y(PanelPapel pp, ProvaStructure provaStructure) {
                            _pp = pp;
                            _provaStructure = provaStructure;
                        }
                        public void changed(PanelPapel pp){
                            // System.out.println("Atualizando enunciado...");
                            _provaStructure.setCabecalho(pp.getPapelFromText());
                        }
                    }
                    pp.addListener(new Y(pp,g));

                    //
                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(g.getProperties());
                    sp.add(pprop,JSplitPane.BOTTOM);
                    sp.add(pp,JSplitPane.TOP);
                    sp.setDividerLocation(500);

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);

                    // focus
                    pp.focusTextArea();

                }

                else if (m instanceof Quesito) {

                    // habilita botões grupo
                    enableQuesitoButtons();

                    Quesito q = (Quesito) m;
                    PanelPapel pp = new PanelPapel(q.getEnunciado());

                    /**
                     * @todo melhorar a eficiencia da alteraçao de enunciado.
                     * Esta soluçao não é muito eficiente uma vez que
                     * sempre que há uma alteração no enunciado corrente
                     * o método pp.getPapelFromText() constrói um novo Papel
                     * com todo o novo enunciado (isso pra cada letrinha nova
                     * no enunciado). Mas como os enunciados nao sao tao grandes,
                     * ainda está com uma boa performance.
                     */
                    class Y implements PanelPapelListener {
                        private PanelPapel _pp;
                        private Quesito _quesito;
                        public Y(PanelPapel pp, Quesito quesito) {
                            _pp = pp;
                            _quesito = quesito;
                        }
                        public void changed(PanelPapel pp){
                            // System.out.println("Atualizando enunciado...");
                            _quesito.setEnunciado(pp.getPapelFromText());
                        }
                    }
                    pp.addListener(new Y(pp,q));

                    // { panel properties
                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(q.getProperties());
                    sp.add(pprop,JSplitPane.BOTTOM);
                    sp.add(pp,JSplitPane.TOP);
                    sp.setDividerLocation(500);
                    // }

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);

                    // focus
                    pp.focusTextArea();

                }
                else if (m instanceof ItemQuesito) {

                    // habilita botões do itemQuesito
                    enableItemQuesitoButtons();


                    ItemQuesito q = (ItemQuesito) m;
                    PanelPapel pp = new PanelPapel(q.getEnunciado());

                    /**
                     * @todo melhorar a eficiencia da alteraçao de enunciado.
                     * Esta soluçao não é muito eficiente uma vez que
                     * sempre que há uma alteração no enunciado corrente
                     * o método pp.getPapelFromText() constrói um novo Papel
                     * com todo o novo enunciado (isso pra cada letrinha nova
                     * no enunciado). Mas como os enunciados nao sao tao grandes,
                     * ainda está com uma boa performance.
                     */
                    class Y implements PanelPapelListener {
                        private PanelPapel _pp;
                        private ItemQuesito _itemQuesito;
                        public Y(PanelPapel pp, ItemQuesito itemQuesito) {
                            _pp = pp;
                            _itemQuesito = itemQuesito;
                        }
                        public void changed(PanelPapel pp){
                            // System.out.println("Atualizando enunciado...");
                            _itemQuesito.setEnunciado(pp.getPapelFromText());
                        }
                    }
                    pp.addListener(new Y(pp,q));


                    // { panel properties
                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(q.getProperties());
                    sp.add(pprop,JSplitPane.BOTTOM);
                    sp.add(pp,JSplitPane.TOP);
                    sp.setDividerLocation(500);
                    // }

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);

                    // focus
                    pp.focusTextArea();

                }
            }

            // Fim: Árvore
            //----------------------------------------------------------



            public void menu(MFTree tree, Model m, int x, int y) {


                if (_tree.getNumberOfSelectedNodes() > 1) {
                    ModelSelection s = _tree.getSelectedNodesModels();
                    if (s.conainsOnlyQuesitos()) {
                        JPopupMenu popup = new JPopupMenu();
                        popup.add(new JMenuItem(new MFActionTrocarValorAcerto(s.getQuesitos())));
                        popup.add(new JMenuItem(new MFActionTrocarValorErro(s.getQuesitos())));
                        popup.show(tree, x, y);
                    }
                }

                else if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionLockNoProvaPermutavel(grupo)));
                    popup.add(new JMenuItem(new MFActionAdicionarGrupo(grupo)));
                    popup.add(new JMenuItem(new MFActionAdicionarQuesito(grupo)));
                    popup.add(new JMenuItem(new MFActionCopyModelToClipboard(grupo)));
                    popup.add(new JMenuItem(new MFActionPasteModelOnClipboard(grupo)));
                    popup.add(new JMenuItem(new MFActionRemoverGrupo(grupo)));
                    popup.add(new JMenuItem(new MFActionMoverPraFrente(grupo)));
                    popup.add(new JMenuItem(new MFActionMoverPraTras(grupo)));
                    popup.show(tree, x, y);
                }
                else if (m instanceof ProvaStructure) {
                    ProvaStructure provaStructure = (ProvaStructure) m;
                    Grupo grupo = provaStructure.getRoot();
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionLockNoProvaPermutavel(grupo)));
                    popup.add(new JMenuItem(new MFActionAdicionarGrupo(grupo)));
                    popup.add(new JMenuItem(new MFActionAdicionarQuesito(grupo)));
                    popup.add(new JMenuItem(new MFActionSetDefaultTags(provaStructure)));
                    popup.add(new JMenuItem(new MFActionSetPrefixoNumericoNosTagsDosQuesitos(provaStructure)));
                    popup.add(new JMenuItem(new MFActionTrocarValorAcertoDeTodosOsQuesitos(provaStructure)));
                    popup.add(new JMenuItem(new MFActionTrocarValorErroDeTodosOsQuesitos(provaStructure)));
                    popup.show(tree, x, y);
                }
                else if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionLockNoProvaPermutavel(quesito)));
                    popup.add(new JMenuItem(new MFActionTrocarResposta(quesito)));
                    popup.add(new JMenuItem(new MFActionTrocarValorAcerto(quesito)));
                    popup.add(new JMenuItem(new MFActionRemoverQuesito(quesito)));
                    popup.add(new JMenuItem(new MFActionCopyModelToClipboard(quesito)));
                    popup.add(new JMenuItem(new MFActionChangeQuesitoValues(quesito)));
                    popup.add(new JMenuItem(new MFActionMoverPraFrente(quesito)));
                    popup.add(new JMenuItem(new MFActionMoverPraTras(quesito)));
                    if (quesito.getTipo() == Quesito.TIPO_NUMERICO_99) {
                        popup.add(new JMenuItem(new MFActionTrocarNumeroDeDigitosQuesitoNumerico(quesito)));
                    }
                    popup.show(tree, x, y);
                }
                else if (m instanceof ItemQuesito) {
                    ItemQuesito i = (ItemQuesito) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionRemoverItemQuesito(i)));
                    popup.add(new JMenuItem(new MFActionMoverPraFrente(i)));
                    popup.add(new JMenuItem(new MFActionMoverPraTras(i)));
                    popup.show(tree, x, y);
                }
            }
        });

        // registry key listener
        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteProva");
        mfTree.getActionMap().put("deleteProva", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof ModelProva) {
                    MFActionRemoverProva a = new MFActionRemoverProva((ModelProva) o);
                    a.actionPerformed(e);
                }
            }
        });

        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "removerSelectedNode");
        mfTree.getActionMap().put("removerSelectedNode", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Quesito) {
                    MFActionRemoverQuesito a = new MFActionRemoverQuesito((Quesito) o);
                    a.actionPerformed(e);
                }
                else if (o instanceof Grupo) {
                    MFActionRemoverGrupo a = new MFActionRemoverGrupo((Grupo) o);
                    a.actionPerformed(e);
                }
                else if (o instanceof ItemQuesito) {
                    MFActionRemoverItemQuesito a = new MFActionRemoverItemQuesito((ItemQuesito) o);
                    a.actionPerformed(e);
                }



            }
        });


        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0), "trocarGabarito");
        mfTree.getActionMap().put("trocarGabarito", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Quesito) {
                    MFActionTrocarResposta a = new MFActionTrocarResposta((Quesito) o);
                    a.actionPerformed(e);
                }
            }
        });

        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "trocarAcerto");
        mfTree.getActionMap().put("trocarAcerto", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Quesito) {
                    MFActionTrocarValorAcerto a = new MFActionTrocarValorAcerto((Quesito) o);
                    a.actionPerformed(e);
                }
            }
        });

        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), "trocarErro");
        mfTree.getActionMap().put("trocarErro", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Quesito) {
                    MFActionTrocarValorErro a = new MFActionTrocarValorErro((Quesito) o);
                    a.actionPerformed(e);
                }
            }
        });


        mfTree.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, 0), "importarProva");
        mfTree.getActionMap().put("importarProva", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = ((JTree)e.getSource()).getSelectionModel().getSelectionPath();
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof ModelProva) {
                    MFActionImportarProvas a = new MFActionImportarProvas(((ModelProva) o).getModeInstituicao());
                    a.actionPerformed(e);
                }
            }
        });

        MFTreeModel mfTreeModel = new MFTreeModel(mfTree, new ModelMF(), _modelProva.getProvaStructure());
        mfTree.setModel(mfTreeModel);
        mfTree.setFont(new Font("Tahoma",Font.PLAIN,18));
        mfTree.setRowHeight(26);
        expandeGrupos(mfTree);

        JScrollPane spTree = new JScrollPane(mfTree);

        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BorderLayout());
        panelLeft.add(spTree,BorderLayout.CENTER);

        Border border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(103, 101, 98), new Color(148, 145, 140));
        TitledBorder titledBorder2 = new TitledBorder(border2, "Estrutura");
        panelLeft.setBorder(titledBorder2);

        /*
        spTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                    if (tn == null)
                        tn = (MFTreeNode) ( (MFTreeModel) _tree.getModel()).getRoot();
                    _tree.clearSelection();
                    _tree.removeSelectionPath(new TreePath(tn.getPath()));
                }
            }
        });
        */

        panelTreeAndButtons.add(panelLeft, BorderLayout.CENTER);

        // habilita botões do grupo
        enableGrupoRootButtons();


        //------------------------------------------------


        // ----
        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "produzirProva");
        this.getActionMap().put("produzirProva", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = (_tree.getSelectionModel().getSelectionPath());
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Model) {
                    Model m = (Model) o;
                    ModelProva mp = null;
                    if (m instanceof ModelProva) {
                        mp = (ModelProva) m;
                    }
                    else {
                        mp = (ModelProva) m.getAscendentByClass(ModelProva.class);
                    }
                    if (mp != null) {
                        MFActionProduzirProva a = new MFActionProduzirProva(mp);
                        a.actionPerformed(new ActionEvent(MainFrame.MAIN_FRAME,0,""));
                    }
                }
            }
        });


        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "produzirQuestionario");
        this.getActionMap().put("produzirQuestionario", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = (_tree.getSelectionModel().getSelectionPath());
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Model) {
                    Model m = (Model) o;
                    ModelProva mp = null;
                    if (m instanceof ModelProva) {
                        mp = (ModelProva) m;
                    }
                    else {
                        mp = (ModelProva) m.getAscendentByClass(ModelProva.class);
                    }
                    if (mp != null) {
                        MFActionProduzirQuestionario a = new MFActionProduzirQuestionario(mp);
                        a.actionPerformed(new ActionEvent(MainFrame.MAIN_FRAME,0,""));
                    }
                }
            }
        });


        this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "salvarProva");
        this.getActionMap().put("salvarProva", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                TreePath tp = (_tree.getSelectionModel().getSelectionPath());
                if (tp == null) return;
                Object o  =((MFTreeNode) tp.getLastPathComponent()).getUserObject();
                if (o instanceof Model) {
                    Model m = (Model) o;
                    ModelProva mp = null;
                    if (m instanceof ModelProva) {
                        mp = (ModelProva) m;
                    }
                    else {
                        mp = (ModelProva) m.getAscendentByClass(ModelProva.class);
                    }
                    if (mp != null) {
                        MFActionSalvarProva a = new MFActionSalvarProva(mp);
                        a.actionPerformed(new ActionEvent(MainFrame.MAIN_FRAME,0,""));
                    }
                }
            }
        });
        // ---

        //
        _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _splitPane.add(panelTreeAndButtons, JSplitPane.LEFT);
        _splitPane.add(new JLabel(), JSplitPane.RIGHT);
        _splitPane.setDividerLocation(300);

        this.setLayout(new BorderLayout());
        this.add(_splitPane, BorderLayout.CENTER);
        this.add(this.getPanelButtons(),BorderLayout.NORTH);



    }

    // Traverse all nodes in tree
    public static MFTreeNode findObject(MFTree tree, Object object) {
        MFTreeNode root = (MFTreeNode)tree.getModel().getRoot();
        return findObject(root,object);
    }
    public static MFTreeNode findObject(MFTreeNode node, Object object) {
        MFTreeNode result = null;
        if (node.getUserObject() == object)
            result = node;
        else {
            if (node.getChildCount() >= 0) {
                for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                    MFTreeNode n = (MFTreeNode) e.nextElement();
                    result = findObject(n, object);
                    if (result != null)
                       break;
                }
            }
        }
        return result;
    }


    // Traverse all nodes in tree
    public static void expandeGrupos(MFTree tree) {
        MFTreeNode root = (MFTreeNode)tree.getModel().getRoot();
        expandeGrupos(tree,root);
    }
    public static void expandeGrupos(MFTree tree, MFTreeNode node) {
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                MFTreeNode n = (MFTreeNode) e.nextElement();
                expandeGrupos(tree,n);
            }
        }
        if (node.getUserObject() instanceof Grupo) {
            tree.expandPath(new TreePath(node.getPath()));
        }
    }


    public JToolBar getPanelButtons() {

        //----------------------------------------------------------
        // Início: Panel com botões para alteração da estrutura


        //_btnLockUnlock.setPreferredSize(new Dimension(30, 25));
        _btnLockUnlock.setToolTipText("Travar/Destravar Grupo/Quesito");
        _btnLockUnlock.setBorderPainted(false);
        _btnLockUnlock.setMargin(new Insets(2, 2, 2, 2));
        _btnLockUnlock.setIcon(Images.Lock);
        _btnLockUnlock.setText("");
        _btnLockUnlock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionLockNoProvaPermutavel(grupo)).actionPerformed(e);
                }
                else if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionLockNoProvaPermutavel(quesito)).actionPerformed(e);
                }
            }
        });


        //_btnAddGrupo.setPreferredSize(new Dimension(25, 25));
        _btnAddGrupo.setToolTipText("Adicionar Grupo");
        _btnAddGrupo.setBorderPainted(false);
        _btnAddGrupo.setMargin(new Insets(2, 2, 2, 2));
        _btnAddGrupo.setIcon(Images.GrupoAdd);
        _btnAddGrupo.setText("");
        _btnAddGrupo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                if (tn == null)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                else if (tn.getType() == MFTreeNode.TYPE_PROVA_STRUCTURE)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionAdicionarGrupo(grupo)).actionPerformed(e);
                    _tree.expandPath(new TreePath(tn.getPath()));
                }
            }
        });

        //_btnAddQuesito.setPreferredSize(new Dimension(25, 25));
        _btnAddQuesito.setToolTipText("Adicionar Quesito");
        _btnAddQuesito.setBorderPainted(false);
        _btnAddQuesito.setMargin(new Insets(2, 2, 2, 2));
        _btnAddQuesito.setIcon(Images.QuesitoAdd);
        _btnAddQuesito.setText("");
        _btnAddQuesito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                if (tn == null)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                else if (tn.getType() == MFTreeNode.TYPE_PROVA_STRUCTURE)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionAdicionarQuesito(grupo)).actionPerformed(e);
                    _tree.expandPath(new TreePath(tn.getPath()));
                }
            }
        });


        //_btnAddItemQuesito.setPreferredSize(new Dimension(25, 25));
        _btnAddItemQuesito.setToolTipText("Adicionar Item Quesito");
        _btnAddItemQuesito.setBorderPainted(false);
        _btnAddItemQuesito.setMargin(new Insets(2, 2, 2, 2));
        _btnAddItemQuesito.setIcon(Images.ItemQuesitoAdd);
        _btnAddItemQuesito.setText("");
        _btnAddItemQuesito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                if (tn == null)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionAdicionarItemQuesito(quesito)).actionPerformed(e);
                    _tree.expandPath(new TreePath(tn.getPath()));
                }
            }
        });


        //_btnDeleteGrupo.setPreferredSize(new Dimension(25, 25));
        _btnDeleteGrupo.setToolTipText("Remover Grupo");
        _btnDeleteGrupo.setBorderPainted(false);
        _btnDeleteGrupo.setMargin(new Insets(2, 2, 2, 2));
        _btnDeleteGrupo.setIcon(Images.GrupoRemove);
        _btnDeleteGrupo.setText("");
        _btnDeleteGrupo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionRemoverGrupo(grupo)).actionPerformed(e);
                }
            }
        });


        //_btnDeleteQuesito.setPreferredSize(new Dimension(25, 25));
        _btnDeleteQuesito.setToolTipText("Remover Quesito");
        _btnDeleteQuesito.setBorderPainted(false);
        _btnDeleteQuesito.setMargin(new Insets(2, 2, 2, 2));
        _btnDeleteQuesito.setIcon(Images.QuesitoRemove);
        _btnDeleteQuesito.setText("");
        _btnDeleteQuesito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionRemoverQuesito(quesito)).actionPerformed(e);
                }
            }
        });

        //_btnDeleteItemQuesito.setPreferredSize(new Dimension(25, 25));
        _btnDeleteItemQuesito.setToolTipText("Remover Item Quesito");
        _btnDeleteItemQuesito.setBorderPainted(false);
        _btnDeleteItemQuesito.setMargin(new Insets(2, 2, 2, 2));
        _btnDeleteItemQuesito.setIcon(Images.ItemQuesitoRemove);
        _btnDeleteItemQuesito.setText("");
        _btnDeleteItemQuesito.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof ItemQuesito) {
                    ItemQuesito itemQuesito = (ItemQuesito) m;
                    (new MFActionRemoverItemQuesito(itemQuesito)).actionPerformed(e);
                }
            }
        });

        //_btnMoverParaCima.setPreferredSize(new Dimension(25, 25));
        _btnMoverParaCima.setToolTipText("Mover para Cima");
        _btnMoverParaCima.setBorderPainted(false);
        _btnMoverParaCima.setMargin(new Insets(2, 2, 2, 2));
        _btnMoverParaCima.setIcon(Images.SetaCima);

        _btnMoverParaCima.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                boolean expanded = _tree.isExpanded(new TreePath(tn.getPath()));
                Model m = (Model) tn.getUserObject();
                try {
                    if (m instanceof Grupo) {
                        Grupo grupo = (Grupo) m;
                        (new MFActionMoverPraTras(grupo)).actionPerformed(e);
                    }
                    else if (m instanceof Quesito) {
                        Quesito quesito = (Quesito) m;
                        (new MFActionMoverPraTras(quesito)).actionPerformed(e);
                    }
                    else if (m instanceof ItemQuesito) {
                        ItemQuesito itemQuesito = (ItemQuesito) m;
                        (new MFActionMoverPraTras(itemQuesito)).actionPerformed(e);
                    }
                    MFTreeNode mftn = findObject(_tree,m);
                    if (mftn != null) {
                        TreePath tp = new TreePath(mftn.getPath());
                        _tree.setSelectionPath(tp);
                        if (expanded)
                            _tree.expandPath(tp);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //_btnMoverParaBaixo.setPreferredSize(new Dimension(25, 25));
        _btnMoverParaBaixo.setToolTipText("Mover para Baixo");
        _btnMoverParaBaixo.setBorderPainted(false);
        _btnMoverParaBaixo.setMargin(new Insets(2, 2, 2, 2));
        _btnMoverParaBaixo.setIcon(Images.SetaBaixo);

        _btnMoverParaBaixo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                boolean expanded = _tree.isExpanded(new TreePath(tn.getPath()));
                Model m = (Model) tn.getUserObject();
                try {
                    if (m instanceof Grupo) {
                        Grupo grupo = (Grupo) m;
                        (new MFActionMoverPraFrente(grupo)).actionPerformed(e);
                    }
                    else if (m instanceof Quesito) {
                        Quesito quesito = (Quesito) m;
                        (new MFActionMoverPraFrente(quesito)).actionPerformed(e);
                    }
                    else if (m instanceof ItemQuesito) {
                        ItemQuesito itemQuesito = (ItemQuesito) m;
                        (new MFActionMoverPraFrente(itemQuesito)).actionPerformed(e);
                    }
                    MFTreeNode mftn = findObject(_tree,m);
                    if (mftn != null) {
                        TreePath tp = new TreePath(mftn.getPath());
                        _tree.setSelectionPath(tp);
                        if (expanded)
                            _tree.expandPath(tp);
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        //_btnChangeQuesitoValues.setPreferredSize(new Dimension(25, 25));
        _btnChangeQuesitoValues.setToolTipText("Alterar valores do Quesito (tag,tipo,acerto,erro");
        _btnChangeQuesitoValues.setBorderPainted(false);
        _btnChangeQuesitoValues.setMargin(new Insets(2, 2, 2, 2));
        _btnChangeQuesitoValues.setText("AQ");
        _btnChangeQuesitoValues.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionChangeQuesitoValues(quesito)).actionPerformed(e);
                }
            }
        });


        //_btnChangeQuesitoAnswer.setPreferredSize(new Dimension(30, 25));
        _btnChangeQuesitoAnswer.setToolTipText("Alterar resposta do Quesito");
        _btnChangeQuesitoAnswer.setBorderPainted(false);
        _btnChangeQuesitoAnswer.setMargin(new Insets(2, 2, 2, 2));
        _btnChangeQuesitoAnswer.setText("ARQ");
        _btnChangeQuesitoAnswer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionTrocarResposta(quesito)).actionPerformed(e);
                }
            }
        });

        //_btnChangeGrupoTag.setPreferredSize(new Dimension(30, 25));
        _btnChangeGrupoTag.setToolTipText("Alterar tag do Grupo");
        _btnChangeGrupoTag.setBorderPainted(false);
        _btnChangeGrupoTag.setMargin(new Insets(2, 2, 2, 2));
        _btnChangeGrupoTag.setText("ATG");
        _btnChangeGrupoTag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionAlterarGrupo(grupo)).actionPerformed(e);
                }
            }
        });

        //_btnChangeItemQuesitoTag.setPreferredSize(new Dimension(30, 25));
        _btnChangeItemQuesitoTag.setToolTipText("Alterar tag do Item Quesito");
        _btnChangeItemQuesitoTag.setBorderPainted(false);
        _btnChangeItemQuesitoTag.setMargin(new Insets(2, 2, 2, 2));
        _btnChangeItemQuesitoTag.setText("ATIQ");
        _btnChangeItemQuesitoTag.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof ItemQuesito) {
                    ItemQuesito itemQuesito = (ItemQuesito) m;
                    (new MFActionAlterarItemQuesito(itemQuesito)).actionPerformed(e);
                }
            }
        });


        //_btnCopiarParaClipboard.setPreferredSize(new Dimension(30, 25));
        _btnCopiarParaClipboard.setToolTipText("Copiar para Clipboard");
        _btnCopiarParaClipboard.setBorderPainted(false);
        _btnCopiarParaClipboard.setMargin(new Insets(2, 2, 2, 2));
        _btnCopiarParaClipboard.setText(">C");
        _btnCopiarParaClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionCopyModelToClipboard(grupo)).actionPerformed(e);
                }
                if (m instanceof Quesito) {
                    Quesito quesito = (Quesito) m;
                    (new MFActionCopyModelToClipboard(quesito)).actionPerformed(e);
                }
                if (m instanceof ItemQuesito) {
                    ItemQuesito itemQuesito = (ItemQuesito) m;
                    (new MFActionCopyModelToClipboard(itemQuesito)).actionPerformed(e);
                }
            }
        });

        // _btnColarDeClipboard.setPreferredSize(new Dimension(30, 25));
        _btnColarDeClipboard.setToolTipText("Colar para Clipboarder");
        _btnColarDeClipboard.setBorderPainted(false);
        _btnColarDeClipboard.setMargin(new Insets(2, 2, 2, 2));
        _btnColarDeClipboard.setText("C>");
        _btnColarDeClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
                if (tn == null)
                    tn = (MFTreeNode) ((MFTreeModel)_tree.getModel()).getRoot();
                Model m = (Model) tn.getUserObject();
                if (m instanceof Grupo) {
                    Grupo grupo = (Grupo) m;
                    (new MFActionPasteModelOnClipboard(grupo)).actionPerformed(e);
                    _tree.expandPath(new TreePath(tn.getPath()));
                }
            }
        });


        // _btnDesselecionar.setPreferredSize(new Dimension(30, 25));
        _btnDesselecionar.setToolTipText("Desselecionar");
        _btnDesselecionar.setBorderPainted(false);
        _btnDesselecionar.setMargin(new Insets(2, 2, 2, 2));
        _btnDesselecionar.setText("D");
        _btnDesselecionar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _tree.clearSelection();
                refazStatusBotoes();
            }
        });


        JToolBar panelButtonsEdition = new JToolBar("Operações",JToolBar.HORIZONTAL);
        //panelButtonsEdition.setLayout(new FlowLayout());
        panelButtonsEdition.add(_btnLockUnlock,null);
        panelButtonsEdition.add(_btnAddGrupo,null);
        panelButtonsEdition.add(_btnDeleteGrupo,null);
        panelButtonsEdition.add(_btnAddQuesito,null);
        panelButtonsEdition.add(_btnDeleteQuesito,null);
        panelButtonsEdition.add(_btnAddItemQuesito,null);
        panelButtonsEdition.add(_btnDeleteItemQuesito,null);
        panelButtonsEdition.add(_btnMoverParaCima,null);
        panelButtonsEdition.add(_btnMoverParaBaixo,null);
        panelButtonsEdition.add(_btnChangeGrupoTag,null);
        panelButtonsEdition.add(_btnChangeQuesitoValues,null);
        panelButtonsEdition.add(_btnChangeQuesitoAnswer,null);
        panelButtonsEdition.add(_btnChangeItemQuesitoTag,null);
        panelButtonsEdition.add(_btnCopiarParaClipboard,null);
        panelButtonsEdition.add(_btnColarDeClipboard,null);
        panelButtonsEdition.add(_btnDesselecionar,null);

        return panelButtonsEdition;

        // Fim: Panel com botões para alteração da estrutura
        //----------------------------------------------------------

    }

    private void enableGrupoRootButtons() {
        _btnLockUnlock.setEnabled(false);
        _btnAddGrupo.setEnabled(true);
        _btnAddQuesito.setEnabled(true);
        _btnAddItemQuesito.setEnabled(false);
        _btnDeleteGrupo.setEnabled(false);
        _btnDeleteQuesito.setEnabled(false);
        _btnDeleteItemQuesito.setEnabled(false);
        _btnMoverParaCima.setEnabled(false);
        _btnMoverParaBaixo.setEnabled(false);
        _btnChangeQuesitoValues.setEnabled(false);
        _btnChangeQuesitoAnswer.setEnabled(false);
        _btnCopiarParaClipboard.setEnabled(false);
        if (MFActionCopyModelToClipboard.getClipboard() != null)
            _btnColarDeClipboard.setEnabled(true);
        else
            _btnColarDeClipboard.setEnabled(false);
        _btnChangeGrupoTag.setEnabled(false);
        _btnChangeItemQuesitoTag.setEnabled(false);
    }

    private void enableGrupoButtons() {
        _btnLockUnlock.setEnabled(true);
        _btnAddGrupo.setEnabled(true);
        _btnAddQuesito.setEnabled(true);
        _btnAddItemQuesito.setEnabled(false);
        _btnDeleteGrupo.setEnabled(true);
        _btnDeleteQuesito.setEnabled(false);
        _btnDeleteItemQuesito.setEnabled(false);
        _btnMoverParaCima.setEnabled(true);
        _btnMoverParaBaixo.setEnabled(true);
        _btnChangeQuesitoValues.setEnabled(false);
        _btnChangeQuesitoAnswer.setEnabled(false);
        _btnCopiarParaClipboard.setEnabled(true);
        if (MFActionCopyModelToClipboard.getClipboard() != null) {
            Model m = MFActionCopyModelToClipboard.getClipboard();
            if ( (m instanceof Grupo) ||
                 (m instanceof Quesito) )
                _btnColarDeClipboard.setEnabled(true);
            else
                _btnColarDeClipboard.setEnabled(false);
            _btnColarDeClipboard.setEnabled(true);
        } else
            _btnColarDeClipboard.setEnabled(false);
        _btnChangeGrupoTag.setEnabled(true);
        _btnChangeItemQuesitoTag.setEnabled(false);
    }

    private void enableQuesitoButtons() {
        _btnLockUnlock.setEnabled(true);
        _btnAddGrupo.setEnabled(false);
        _btnAddQuesito.setEnabled(false);
        _btnAddItemQuesito.setEnabled(true);
        _btnDeleteGrupo.setEnabled(false);
        _btnDeleteQuesito.setEnabled(true);
        _btnDeleteItemQuesito.setEnabled(false);
        _btnMoverParaCima.setEnabled(true);
        _btnMoverParaBaixo.setEnabled(true);
        _btnChangeQuesitoValues.setEnabled(true);
        _btnChangeQuesitoAnswer.setEnabled(true);
        _btnCopiarParaClipboard.setEnabled(true);
        if (MFActionCopyModelToClipboard.getClipboard() != null) {
            Model m = MFActionCopyModelToClipboard.getClipboard();
            if (m instanceof ItemQuesito)
                _btnColarDeClipboard.setEnabled(true);
            else
                _btnColarDeClipboard.setEnabled(false);
    } else
            _btnColarDeClipboard.setEnabled(false);
        _btnChangeGrupoTag.setEnabled(false);
        _btnChangeItemQuesitoTag.setEnabled(false);
    }

    private void enableCabecalhoButtons() {
        _btnLockUnlock.setEnabled(false);
        _btnAddGrupo.setEnabled(false);
        _btnAddQuesito.setEnabled(false);
        _btnAddItemQuesito.setEnabled(false);
        _btnDeleteGrupo.setEnabled(false);
        _btnDeleteQuesito.setEnabled(false);
        _btnDeleteItemQuesito.setEnabled(false);
        _btnMoverParaCima.setEnabled(false);
        _btnMoverParaBaixo.setEnabled(false);
        _btnChangeQuesitoValues.setEnabled(false);
        _btnChangeQuesitoAnswer.setEnabled(false);
        _btnCopiarParaClipboard.setEnabled(false);
        _btnColarDeClipboard.setEnabled(false);
        _btnChangeGrupoTag.setEnabled(false);
        _btnChangeItemQuesitoTag.setEnabled(false);
    }

    private void enableItemQuesitoButtons() {
            _btnLockUnlock.setEnabled(false);
            _btnAddGrupo.setEnabled(false);
            _btnAddQuesito.setEnabled(false);
            _btnAddItemQuesito.setEnabled(false);
            _btnDeleteGrupo.setEnabled(false);
            _btnDeleteQuesito.setEnabled(false);
            _btnDeleteItemQuesito.setEnabled(true);
            _btnMoverParaCima.setEnabled(true);
            _btnMoverParaBaixo.setEnabled(true);
            _btnChangeQuesitoValues.setEnabled(false);
            _btnChangeQuesitoAnswer.setEnabled(false);
            _btnCopiarParaClipboard.setEnabled(false);
            _btnColarDeClipboard.setEnabled(false);
            _btnChangeGrupoTag.setEnabled(false);
            _btnChangeItemQuesitoTag.setEnabled(true);
    }

    public void refazStatusBotoes() {
        MFTreeNode tn = (MFTreeNode) _tree.getLastSelectedPathComponent();
        if (tn == null)
            enableGrupoRootButtons();
        else {
            Model m = (Model) tn.getUserObject();
            if (m instanceof Grupo)
                enableGrupoButtons();
            else if (m instanceof Quesito)
                enableQuesitoButtons();
            else if (m instanceof ItemQuesito)
                enableItemQuesitoButtons();
        }
    }
}
