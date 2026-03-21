package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.TreePath;

import linsoft.gui.util.ViewEspera;
import mixnfix.Model;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.PanelPapel;
import mixnfix.prova.PanelProperties;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

public class PanelCadastro
    extends JPanel {

    private static final String LABEL_PROVAS = "Provas";
    private static final String LABEL_INSTITUICOES = "Instituições";
    private static final String LABEL_CURSOS = "Cursos";
    private static final String LABEL_ALUNOS = "Alunos";
    private static final String LABEL_TURMAS = "Turmas";
    private static final String LABEL_CURSOSINSTANCIAS = "Instâncias de Curso";
    private static final String LABEL_AVALIACOES = "Avaliações";

    private JSplitPane _splitPane;
    private MFTree _tree;

    private HashMap<ModelProva,PanelProva> _mapProvaPanel;



    private Object _object;
    public PanelCadastro() throws Exception {

        _mapProvaPanel = new HashMap<ModelProva,PanelProva>();

        JPanel panelTreeAndButtons = new JPanel();
        panelTreeAndButtons.setLayout(new BorderLayout());

        //---- add the tree ------------------------------
        _tree = new MFTree();
        MFTree mfTree = _tree;
        mfTree.setFont(new Font("Tahoma",Font.PLAIN,14));
        mfTree.setRowHeight(22);
        mfTree.addListener(new MFTreeListener() {
            public void select(Model m) {
                if (m instanceof ModelProva) {
                    ModelProva mp = (ModelProva) m;
                    _object = mp;
                    // PanelEdicaoProva pEdicao = new PanelEdicaoProva(mp);
                    PanelProva p = _mapProvaPanel.get(mp);
                    if (p == null) {

                        // start wait window for possible long task
                        // preparar documento e janela de visualização de
                        // documento
                        ViewEspera ve = new ViewEspera(MainFrame.MAIN_FRAME);
                        p = (PanelProva) ve.doWork(new linsoft.gui.util.IWorker() {
                            public Object doWork() {
                                try {
                                    try {
                                        Thread.sleep(250);
                                    }
                                    catch (InterruptedException ex1) {
                                    }
                                    return new PanelProva( (ModelProva) _object);
                                }
                                catch (SQLException ex) {
                                    ex.printStackTrace();
                                    return null;
                                }
                            }
                        }
                        , "Carregando Prova...", 160, 120, Images.MIXnFIX_transparent);

                        if (p != null)
                            _mapProvaPanel.put(mp, p);
                    }
                    // p.refazStatusBotoes();
                    int x = _splitPane.getDividerLocation();
                    if (p != null) {
                        _splitPane.setRightComponent(p);
                    }
                    else {
                        _splitPane.setRightComponent(new JLabel("Problemas ao carregar PanelProva"));
                    }
                    _splitPane.setDividerLocation(x);
                }
                else if (m instanceof ModelInstituicao) {
                    try {
                        ModelInstituicao modelInstituicao = (ModelInstituicao) m;
                        int x = _splitPane.getDividerLocation();
                        _splitPane.setRightComponent(new PanelModelInstituicao(modelInstituicao));
                        _splitPane.setDividerLocation(x);
                    }
                    catch (SQLException ex2) {
                        ex2.printStackTrace();
                    }
                }
                else if (m instanceof ModelProvaCorrecao) {
                    ModelProvaCorrecao mpc = (ModelProvaCorrecao) m;
                    PanelProvaCorrecao ppc = new PanelProvaCorrecao(mpc);
                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(ppc);
                    _splitPane.setDividerLocation(x);
                }
                else if (m instanceof ModelColetaQuestionario) {
                    ModelColetaQuestionario mpc = (ModelColetaQuestionario) m;
                    PanelColetaQuestionario ppc = new PanelColetaQuestionario(mpc);
                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(ppc);
                    _splitPane.setDividerLocation(x);
                }
                else if (m instanceof Grupo) {
                    Grupo g = (Grupo) m;
                    PanelPapel pp = new PanelPapel(g.getEnunciado());
                    class X implements FocusListener {
                        private PanelPapel _pp;
                        private Grupo _grupo;
                        public X(PanelPapel pp, Grupo grupo) {
                            _pp = pp;
                            _grupo = grupo;
                        }
                        public void focusLost(FocusEvent e) {
                            if (_pp.changed()) {
                                _grupo.setEnunciado(_pp.getPapelFromText());
                            }
                        }
                        public void focusGained(FocusEvent e) {
                        }
                    }
                    pp.getTextPane().addFocusListener(new X(pp,g));

                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(g.getProperties());
                    sp.add(pprop,JSplitPane.TOP);
                    sp.add(pp,JSplitPane.BOTTOM);
                    sp.setDividerLocation(250);

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);
                }

                else if (m instanceof ProvaStructure) {
                    ProvaStructure g = (ProvaStructure) m;
                    PanelPapel pp = new PanelPapel(g.getCabecalho());

                    class X implements FocusListener {
                        private PanelPapel _pp;
                        private ProvaStructure _ps;
                        public X(PanelPapel pp, ProvaStructure ps) {
                            _pp = pp;
                            _ps = ps;
                        }
                        public void focusLost(FocusEvent e) {
                            if (_pp.changed()) {
                                _ps.setCabecalho(_pp.getPapelFromText());
                            }
                        }
                        public void focusGained(FocusEvent e) {
                        }
                    }
                    pp.getTextPane().addFocusListener(new X(pp,g));

                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(g.getProperties());
                    sp.add(pprop,JSplitPane.TOP);
                    sp.add(pp,JSplitPane.BOTTOM);
                    sp.setDividerLocation(250);

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);
                }

                else if (m instanceof Quesito) {
                    Quesito q = (Quesito) m;
                    PanelPapel pp = new PanelPapel(q.getEnunciado());

                    class X implements FocusListener {
                        private PanelPapel _pp;
                        private Quesito _quesito;
                        public X(PanelPapel pp, Quesito quesito) {
                            _pp = pp;
                            _quesito = quesito;
                        }
                        public void focusLost(FocusEvent e) {
                            if (_pp.changed()) {
                                _quesito.setEnunciado(_pp.getPapelFromText());
                            }
                        }
                        public void focusGained(FocusEvent e) {
                        }
                    }
                    pp.getTextPane().addFocusListener(new X(pp,q));

                    // { panel properties
                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(q.getProperties());
                    sp.add(pprop,JSplitPane.TOP);
                    sp.add(pp,JSplitPane.BOTTOM);
                    sp.setDividerLocation(250);
                    // }

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);
                }
                else if (m instanceof ItemQuesito) {
                    ItemQuesito q = (ItemQuesito) m;
                    PanelPapel pp = new PanelPapel(q.getEnunciado());

                    class X implements FocusListener {
                        private PanelPapel _pp;
                        private ItemQuesito _iq;
                        public X(PanelPapel pp, ItemQuesito iq) {
                            _pp = pp;
                            _iq = iq;
                        }
                        public void focusLost(FocusEvent e) {
                            if (_pp.changed()) {
                                _iq.setEnunciado(_pp.getPapelFromText());
                            }
                        }
                        public void focusGained(FocusEvent e) {
                        }
                    }
                    pp.getTextPane().addFocusListener(new X(pp,q));


                    // { panel properties
                    JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    PanelProperties pprop = new PanelProperties(q.getProperties());
                    sp.add(pprop,JSplitPane.TOP);
                    sp.add(pp,JSplitPane.BOTTOM);
                    sp.setDividerLocation(250);
                    // }

                    int x = _splitPane.getDividerLocation();
                    _splitPane.setRightComponent(sp);
                    _splitPane.setDividerLocation(x);
                }
            }








            public void menu(MFTree tree, Model m, int x, int y) {

                if (m instanceof ModelInstituicao) {

                    ModelInstituicao modelInstituicao = (ModelInstituicao) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem("---------- Instituição"));
                    popup.add(new JMenuItem(new MFActionRemoverInstituicao(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionRemoverInstituicaoRecursivamente(modelInstituicao)));
                    popup.add(new JMenuItem("---------- Provas"));
                    popup.add(new JMenuItem(new MFActionNovaProva(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionImportarProvas(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionCopiarPontoProvaDeTodaUmaInstituicao(modelInstituicao)));
                    popup.add(new JMenuItem("---------- Alunos "));
                    popup.add(new JMenuItem(new MFActionImportarAlunos(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionExportarAlunos(modelInstituicao)));
                    popup.add(new JMenuItem("---------- Relatórios "));
                    popup.add(new JMenuItem(new MFActionRelatorioFaltas(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionGerarRelatorio(modelInstituicao)));
                    popup.add(new JMenuItem(new MFActionReportStatisticsAll(modelInstituicao)));
                    popup.show(tree, x, y);
                }
                else if (m instanceof ModelMF) {
                    ModelMF modelMF = (ModelMF) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionAdicionarInstituicao(modelMF)));
                    popup.add(new JMenuItem(new MFActionAdjustImageNames(modelMF)));
                    popup.show(tree, x, y);
                }
                else if (m instanceof ModelProva) {
                    ModelProva modelProva = (ModelProva) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem("---------- Prova"));
                    popup.add(new JMenuItem(new MFActionSalvarProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionProduzirProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionRenomearProva(modelProva.getProva())));
                    popup.add(new JMenuItem(new MFActionRemoverProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionRemoverProvaRecursivamente(modelProva)));
                    popup.add(new JMenuItem(new MFActionCopiarPontoProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionImprimirPDFs()));
                    popup.add(new JMenuItem(new MFActionAddProvaCorrecao(modelProva)));
                    popup.add(new JMenuItem(new MFActionAddColetaQuestionario(modelProva)));
                    popup.add(new JMenuItem(new MFActionRemoverCorrecoesProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionRemoverColetasProva(modelProva)));
                    popup.add(new JMenuItem(new MFActionTrocarIndice(modelProva)));
                    popup.add(new JMenuItem(new MFActionTrocarNumDigitosID(modelProva)));
                    popup.add(new JMenuItem(new MFCopiarEnunciadoDosQuesitos(modelProva)));
                    JMenu subMenuRelatorios = new JMenu("Relatórios...");
                    subMenuRelatorios.add(new JMenuItem(new MFActionGerarRelatorioProvaEstatisticaPorQuestao(modelProva)));
                    subMenuRelatorios.add(new JMenuItem(new MFActionRelatorioPDFCorrecoesProva(modelProva)));
                    subMenuRelatorios.add(new JMenuItem(new MFActionReportStatistics(modelProva)));
                    popup.add(subMenuRelatorios);
                    popup.show(tree, x, y);
                }
                else if (m instanceof ModelProvaCorrecao) {
                    ModelProvaCorrecao modelProvaCorrecao = (ModelProvaCorrecao) m;
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new JMenuItem(new MFActionRenomearProvaCorrecao(modelProvaCorrecao)));
                    popup.add(new JMenuItem(new MFActionRemoverProvaCorrecao(modelProvaCorrecao)));
                    popup.add(new JMenuItem(new MFActionRemoverProvaCorrecaoRecursivamente(modelProvaCorrecao)));
                    popup.add(new JMenuItem(new MFActionGerarRelatorioProvaCorrecaoEstatisticaPorQuestao(modelProvaCorrecao)));
                    popup.show(tree, x, y);
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


        MFTreeModel mfTreeModel = new MFTreeModel(mfTree, MainFrame.getModelMF());
        mfTree.setModel(mfTreeModel);
        JScrollPane spTree = new JScrollPane(mfTree);
        panelTreeAndButtons.add(spTree, BorderLayout.CENTER);
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
        _splitPane.setDividerLocation(0.5);

        this.setLayout(new BorderLayout());
        this.add(_splitPane, BorderLayout.CENTER);
    }

    public void select(Model m) {
        MFTreeModel tm = (MFTreeModel) this._tree.getModel();
        MFTreeNode t = tm.getRoot().findChildRecursevly(m);
        if (t != null)
            _tree.setSelectionPath(new TreePath(t.getPath()));
            // _tree.select(t);
    }
}
