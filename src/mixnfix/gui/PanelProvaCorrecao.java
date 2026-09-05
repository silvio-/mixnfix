package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linsoft.gui.AntialiasedLabel;
import linsoft.gui.Input;
import linsoft.gui.SpringUtilities;
import linsoft.gui.util.DialgoChooseObjects;
import mixnfix.Controller;
import mixnfix.MFI2Java;
import mixnfix.Model;
import mixnfix.ModelListener;
import mixnfix.composicaoprova.Prova2TeX;
import mixnfix.folharesposta.BinaryField;
import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.Field;
import mixnfix.folharesposta.GeradorFolhaRespostas;
import mixnfix.folharesposta.MultiField;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Turma;

/**
 *
 */
public class PanelProvaCorrecao extends JPanel {
    private ModelProvaCorrecao _modelProvaCorrecao;

    private JList _listAlunos;
    private JList _listEntradas;
    private JList _listAlunosSemEntrada;
    private DefaultListModel _listModelAlunos;
    private DefaultListModel _listModelEntradas;
    private DefaultListModel _listModelAlunosSemEntrada;
    private JPanel _panelViewCorrecao;
    private JTextArea _taDetalhamentoProva =  new JTextArea();
    private CorrecaoUI.PanelVisualizacaoProva _pView;


    private AntialiasedLabel _lblContadores;

    private JPanel _panelContainerRelatorioGrafico;

    // private ProvaCorrecao
    public PanelProvaCorrecao(ModelProvaCorrecao modelProvaCorrecao) {
        _modelProvaCorrecao = modelProvaCorrecao;

        // botoes e suas acoes
        JButton btnRelatorioTexto = new JButton("Relatório");
        btnRelatorioTexto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    relatorioTexto();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnAdicionarAlunos = new JButton("+");
        btnAdicionarAlunos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    adicionarAlunos();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnAdicionarTurmas = new JButton("+T");
        btnAdicionarTurmas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    adicionarTurmas();
                }
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnRemoverAlunos = new JButton("-");
        btnRemoverAlunos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    removerAlunos();
                }
                catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnExportarFotos = new JButton("Exportar Fotos");
        btnExportarFotos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    exportarFotos();
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnProcessarFotos = new JButton("");
        btnProcessarFotos.setIcon(Images.Corrigir);
        btnProcessarFotos.setToolTipText("Corrigir");
        btnProcessarFotos.setBorderPainted(false);
        btnProcessarFotos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MFActionColetarProvas(
                    _modelProvaCorrecao,
                    mixnfix.folharesposta.GeradorFolhaRespostas.ID
                    ).actionPerformed(e);
            }
        });

        // botoes e suas acoes
        JButton btnExtractFrames = new JButton("Extract Frames");
        btnExtractFrames.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    extractVideo();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        // botoes e suas acoes
        JButton btnRemoverEntradas = new JButton("Remover Entradas");
        btnRemoverEntradas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    removerEntradas();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        // botoes e suas acoes
        JButton btnRelatorioPDFLaTeX = new JButton("Relatório PDF");
        btnRelatorioPDFLaTeX.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    relatorioPDFLaTeX();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton btnCalibragem = new JButton("");
        btnCalibragem.setIcon(Images.Calibrar);
        btnCalibragem.setToolTipText("Calibrar Correção");
        btnCalibragem.setBorderPainted(false);
        btnCalibragem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    calibragem();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });






        // criar e inicializar list models
        _listModelAlunos = new DefaultListModel();
        _listModelAlunosSemEntrada = new DefaultListModel();
        _listModelEntradas = new DefaultListModel();


        ArrayList<ModelAlunoProvaCorrecao> listAlunoProvaCorrecao = new ArrayList<ModelAlunoProvaCorrecao>(_modelProvaCorrecao.getAlunosProvaCorrecao());
        Collections.sort(listAlunoProvaCorrecao,new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelAlunoProvaCorrecao a = (ModelAlunoProvaCorrecao) o1;
                ModelAlunoProvaCorrecao b = (ModelAlunoProvaCorrecao) o2;
                return a.getAluno().getNome().compareTo(b.getAluno().getNome());
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        ArrayList<Aluno> listAlunoSemEntrada = new ArrayList<Aluno>(_modelProvaCorrecao.getAlunosSemEntradas());
        Collections.sort(listAlunoSemEntrada,new Comparator() {
            public int compare(Object o1, Object o2) {
                Aluno a = (Aluno) o1;
                Aluno b = (Aluno) o2;
                return a.getNome().compareTo(b.getNome());
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        ArrayList<ModelEntradaProvaCorrecao> listEntradaProvaCorrecao = new ArrayList<ModelEntradaProvaCorrecao>(_modelProvaCorrecao.getEntradasProvaCorrecao());
        Collections.sort(listEntradaProvaCorrecao,new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelEntradaProvaCorrecao a = (ModelEntradaProvaCorrecao) o1;
                ModelEntradaProvaCorrecao b = (ModelEntradaProvaCorrecao) o2;
                if (a.getAluno() != null && b.getAluno() != null) {
                    return a.getAluno().getNome().compareTo(b.getAluno().getNome());
                }
                else if (a.getAluno() != null) return -1;
                else if (b.getAluno() != null) return 1;
                else return 0;
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        for (ModelAlunoProvaCorrecao mapc: listAlunoProvaCorrecao) _listModelAlunos.addElement(mapc);
        for (ModelEntradaProvaCorrecao mepc: listEntradaProvaCorrecao) _listModelEntradas.addElement(mepc);
        for (Aluno aluno: listAlunoSemEntrada) _listModelAlunosSemEntrada.addElement(aluno);

        // criar lists
        _listAlunos = new JList(_listModelAlunos);
        _listEntradas = new JList(_listModelEntradas);
        _listAlunosSemEntrada = new JList(_listModelAlunosSemEntrada);

        // renderer
        _listAlunos.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelAlunoProvaCorrecao x = (ModelAlunoProvaCorrecao) value;
                java.util.List<ModelEntradaProvaCorrecao> entradas =  x.getEntradasProvaCorrecao();
                this.setIcon(Images.Aluno16x16);
                this.setText("("+x.getAluno().getMatricula()+") "+x.getAluno().getNome()+" - entradas: "+entradas.size());
                return this;
            }
        });

        // renderer
        _listAlunosSemEntrada.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Aluno aluno = (Aluno) value;
                this.setIcon(Images.Aluno16x16);
                this.setText("("+aluno.getMatricula()+") "+aluno.getNome());
                return this;
            }
        });

        // renderer
        _listEntradas.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelEntradaProvaCorrecao x = (ModelEntradaProvaCorrecao) value;
                this.setIcon(Images.Correcao16x16);

                Aluno a = x.getAluno();
                if (a != null) {
                    this.setText("("+a.getMatricula()+") "+(index+1)+" - "+a.getNome());
                }
                this.setText(this.getText()+" t:"+x.getEntradaProvaCorrecao().getTipo());

                if (x.getStatus() == ModelEntradaProvaCorrecao.MATRICULA_NAO_IDENTIFICADA) {
                    if (!isSelected)
                        this.setBackground(Color.blue);
                    else
                        this.setBackground(Color.CYAN);
                }
                else if (x.getStatus() == ModelEntradaProvaCorrecao.PROVA_NAO_IDENTIFICADA) {
                    if (!isSelected)
                        this.setBackground(Color.red);
                    else
                        this.setBackground(Color.MAGENTA);
                }
                return this;
            }
        });
        _listEntradas.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    if (!e.getValueIsAdjusting())
                        selectEntrada( (ModelEntradaProvaCorrecao) _listEntradas.getSelectedValue());
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });



        // ZOOM IN e ZOOM OUT
        _listEntradas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "+");
        _listEntradas.getActionMap().put("+", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (_pView != null)
                    _pView.zoomin();
            }
        });

        _listEntradas.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "-");
        _listEntradas.getActionMap().put("-", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (_pView != null)
                    _pView.zoomout();
            }
        });

        // laout components


        { // attatch model
            _modelProvaCorrecao.addListener(new ModelListener() {
                public void update(Model model) {}
                public void nodeAdded(Model model, Model addedModel, int index) {
                    if (addedModel instanceof ModelAlunoProvaCorrecao) {
                        _listModelAlunos.addElement(addedModel);
                        updateLabelContadores();
                    }
                    else if (addedModel instanceof ModelEntradaProvaCorrecao) {
                        _listModelEntradas.addElement(addedModel);
                        updateLabelContadores();
                    }
                }
                public void nodesAdded(Model model, java.util.List<Model> addedModel, int index) {
                    for (Model m: addedModel) {
                        if (m instanceof ModelAlunoProvaCorrecao) {
                            _listModelAlunos.addElement(m);
                            updateLabelContadores();
                        }
                        else if (m instanceof ModelEntradaProvaCorrecao) {
                            _listModelEntradas.addElement(m);
                            updateLabelContadores();
                        }
                    }
                }
                public void nodesLoaded(Model model, java.util.List<Model> addedModel) {
                    for (Model m: addedModel) {
                        if (m instanceof ModelAlunoProvaCorrecao) {
                            _listModelAlunos.addElement(m);
                            updateLabelContadores();
                        }
                        else if (m instanceof ModelEntradaProvaCorrecao) {
                            _listModelEntradas.addElement(m);
                            updateLabelContadores();
                        }
                    }
                }
                public void nodeRemoved(Model model, Model removedModel) {
                    if (removedModel instanceof ModelAlunoProvaCorrecao) {
                        _listModelAlunos.removeElement(removedModel);
                        updateLabelContadores();
                    }
                    else if (removedModel instanceof ModelEntradaProvaCorrecao) {
                        _listModelEntradas.removeElement(removedModel);
                        updateLabelContadores();
                    }
                }
            });



        } // attatch model


        // panel alunos
        JPanel alunosPanel = new JPanel();
        alunosPanel.setLayout(new GridBagLayout());

        alunosPanel.add(new JScrollPane(_listAlunos),new GridBagConstraints(0,0,1,1,0.5,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(10,10,10,10),0,0));

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.add(btnAdicionarAlunos,new GridBagConstraints(0,0,1,1,1,0.5,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0));
        panel1.add(btnRemoverAlunos,new GridBagConstraints(0,1,1,1,1,0.5,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0));
        panel1.add(btnAdicionarTurmas,new GridBagConstraints(0,2,1,1,1,0.5,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0));
        alunosPanel.add(panel1,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));

        // panel correcoes
        JPanel correcoesPanel = new JPanel();
        correcoesPanel.setLayout(new BorderLayout());

        JScrollPane spEntradas = new JScrollPane(_listEntradas);
        JScrollPane spAlunosSemEntradas = new JScrollPane(_listAlunosSemEntrada);
        spAlunosSemEntradas.setPreferredSize(new Dimension(100,100));
        // spEntradas.setPreferredSize(new Dimension(300,100));
        _panelViewCorrecao = new JPanel();

        JSplitPane splitPaneCorrecoesDetalhamento = new JSplitPane();
        splitPaneCorrecoesDetalhamento.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPaneCorrecoesDetalhamento.add(spEntradas,JSplitPane.TOP);
        splitPaneCorrecoesDetalhamento.add(new JScrollPane(_taDetalhamentoProva),JSplitPane.BOTTOM);
        _taDetalhamentoProva.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        
        

        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new BorderLayout());
        leftSidePanel.add(spAlunosSemEntradas,BorderLayout.NORTH);
        leftSidePanel.add(splitPaneCorrecoesDetalhamento,BorderLayout.CENTER);


        JSplitPane splitPane = new JSplitPane();
        // splitPane.add(splitPaneCorrecoesDetalhamento,JSplitPane.LEFT);
        splitPane.add(leftSidePanel,JSplitPane.LEFT);
        splitPane.add(_panelViewCorrecao,JSplitPane.RIGHT);
        splitPane.setDividerLocation(350);

        correcoesPanel.add(splitPane,BorderLayout.CENTER);

        JToolBar panel2 = new JToolBar();
        // panel2.setLayout(new FlowLayout());
        panel2.add(btnProcessarFotos);
        panel2.add(btnCalibragem);
        panel2.add(btnExtractFrames);
        panel2.add(btnRemoverEntradas);
        panel2.add(btnExportarFotos);
        panel2.add(btnRelatorioTexto);
        panel2.add(btnRelatorioPDFLaTeX);
        correcoesPanel.add(panel2,BorderLayout.SOUTH);

        // Panel Container Relatorio Grafico
        _panelContainerRelatorioGrafico = new JPanel(new BorderLayout());

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.add("Correções",correcoesPanel);
        tabPane.add("Alunos",alunosPanel);
        tabPane.add("Relatório",_panelContainerRelatorioGrafico);

        tabPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JTabbedPane t = (JTabbedPane) e.getSource();
                if (t.getSelectedIndex() == 2) {
                    try {
                        _panelContainerRelatorioGrafico.removeAll();
                        _panelContainerRelatorioGrafico.add(new PanelRelatorioGraficoProvaCorrecao(_modelProvaCorrecao), BorderLayout.CENTER);
                        _panelContainerRelatorioGrafico.invalidate();
                    }catch (Exception x) { x.printStackTrace();}
                }
            }
        });








        this.setLayout(new BorderLayout());
        this.add(tabPane,BorderLayout.CENTER);
        _lblContadores = new AntialiasedLabel(
        _modelProvaCorrecao.getProvaCorrecao().getNome()+" ("+
        _modelProvaCorrecao.getNumAlunosComEntrada()+"/"+
        _modelProvaCorrecao.getNumAlunos()+"/"+
        _modelProvaCorrecao.getNumEntradas()+")");
        _lblContadores.setFont(new Font("Tahoma",Font.PLAIN,36));
        _lblContadores.setIcon(Images.Correcao);

        this.add(_lblContadores,BorderLayout.NORTH);
    }

    private void updateLabelContadores() {
        _lblContadores.setText(
        _modelProvaCorrecao.getProvaCorrecao().getNome()+" ("+
        _modelProvaCorrecao.getNumAlunosComEntrada()+"/"+
        _modelProvaCorrecao.getNumAlunos()+"/"+
        _modelProvaCorrecao.getNumEntradas()+")");
    }

    private void adicionarAlunos() throws SQLException {
        Instituicao i = this._modelProvaCorrecao.getProva().getInstituicao_Prova();
        Vector v = App.getRepositorio().consultarAlunoPorInstituicao(i);
        v.removeAll(_modelProvaCorrecao.getAlunos());
        ListCellRenderer renderer = new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Aluno x = (Aluno) value;
                this.setIcon(Images.Aluno16x16);
                this.setText("("+x.getMatricula()+") "+x.getNome());
                return this;
            }
        };
        DialgoChooseObjects d = new DialgoChooseObjects((JFrame)this.getTopLevelAncestor(),"Adicionar Alunos",true,new Vector(),v,renderer);
        linsoft.gui.util.Library.resizeAndCenterWindow(d,500,400);
        d.setVisible(true);
        if (!d.isOk())
            return;
        this._modelProvaCorrecao.addAlunos((java.util.List<Aluno>) d.getSelectedObjects());
    }


    private void adicionarTurmas() throws SQLException {
        Instituicao i = this._modelProvaCorrecao.getProva().getInstituicao_Prova();
        Vector v = App.getRepositorio().consultarTurmaPorInstituicao(i);
        v.removeAll(_modelProvaCorrecao.getAlunos());
        ListCellRenderer renderer = new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Turma x = (Turma) value;
                this.setIcon(Images.Turma16x16);
                this.setText(x.getNome());
                return this;
            }
        };
        DialgoChooseObjects d = new DialgoChooseObjects((JFrame)this.getTopLevelAncestor(),"Adicionar Turmas",true,new Vector(),v,renderer);
        linsoft.gui.util.Library.resizeAndCenterWindow(d,500,400);
        d.setVisible(true);
        if (!d.isOk())
            return;


        java.util.List<Turma> turmas = (java.util.List<Turma>) d.getSelectedObjects();
        ArrayList<Integer> counts = new ArrayList<Integer>();
        for (Turma t: turmas) {
            Vector x = App.getRepositorio().consultarAlunoTurmaPorTurma(t);
            ArrayList<Aluno> alunos = new ArrayList<Aluno>();
            for (AlunoTurma at: (java.util.List<AlunoTurma>)x)
                alunos.add(at.getAluno_AlunoTurma());
            counts.add(alunos.size());
            this._modelProvaCorrecao.addAlunos(alunos);
        }

        StringBuffer msg = new StringBuffer();
        msg.append("Turmas adicionadas\n");
        int ii=0;
        for (Turma t: turmas) {
            msg.append("- "+t.getNome()+": "+counts.get(ii++)+" alunos \n");
        }
        JOptionPane.showMessageDialog(this,msg.toString());
    }


    private void removerAlunos() throws SQLException, IOException {
        int indice[] = _listAlunos.getSelectedIndices();
        for (int i = indice.length-1; i >= 0; i--) {
            ModelAlunoProvaCorrecao mapc = (ModelAlunoProvaCorrecao) _listModelAlunos.get(indice[i]);
            mapc.remover(false);
        }
    }


    private void removerEntradas() throws IOException, SQLException {
        Object[] models = _listEntradas.getSelectedValues();
        if (models.length == 0) {
            JOptionPane.showMessageDialog(this, "Não existem entradas selecionadas!");
            return;
        }

        if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover entradas selecionadas?",
                                          "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {


            this._listEntradas.getSelectionModel().clearSelection();
            ArrayList<ModelEntradaProvaCorrecao> list = new ArrayList<ModelEntradaProvaCorrecao>();
            for (Object o: models) {
                list.add((ModelEntradaProvaCorrecao) o);
            }
            this._modelProvaCorrecao.removeEntradasProvaCorrecao(list);
        }

    }
;
    private File[] getFotos() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(App.getProperty("fotosdir")));
        jfc.setMultiSelectionEnabled(true);
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

        int result = jfc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {

            App.setProperty("fotosdir",jfc.getSelectedFile().getAbsolutePath());

            File[] fs = jfc.getSelectedFiles();
            return fs;
        }
        return null;
    }


    HashMap<String,ModelAlunoProvaCorrecao> _mapMatricula2MAPC = new HashMap<String,ModelAlunoProvaCorrecao>();
    private void mountMapMatricula2MAPC() {
        _mapMatricula2MAPC.clear();
        java.util.List<ModelAlunoProvaCorrecao> list = _modelProvaCorrecao.getAlunosProvaCorrecao();
        for (ModelAlunoProvaCorrecao mapc: list) {
            _mapMatricula2MAPC.put(mapc.getAluno().getMatricula(),mapc);
        }
    }

    private void processarFotos() throws IOException, SQLException  {
        mixnfix.prova.ProvaStructure pp = this.getProvaStructure(_modelProvaCorrecao.getProva());
        mountMapMatricula2MAPC();


        // {......... do once setup
        GeradorFolhaRespostas g = new GeradorFolhaRespostas(pp);

        CellMap map = g.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(), map.getH(), 50, 1000);
        for (ControlPoint cp : map.getControlPoints()) {
            MFI2Java.addControlPoint(cp.getId(), cp.getX() - map.getX0(), cp.getY() - map.getY0());
        }

        for (Quadrilateral q: map.getQuads()) {
            MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
        }

        System.out.println("Set Contraints");
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        MFI2Java.setConstraints(
            _params.getThresholds(),
            _params.getMinPixelWidth(),
            _params.getMaxPixelWidth(),
            _params.getMinPixelHeight(),
            _params.getMaxPixelHeight(),
            _params.getMinNumPixels(),
            _params.getMaxNumPixels(),
            _params.getPixelDensity(),
            _params.getNumClosest(),
            _params.getMinSide(),
            _params.getAngleTolerance(),
            _params.getTargetRadius(),
            _params.getCorrectSideRatio(),
            _params.getSideRatioTolerance(),
            _params.getPhase(),
            _params.getControlPointRadius(),
            _params.getLeftMargin(),
            _params.getRightMargin(),
            _params.getTopMargin(),
            _params.getBottomMargin());
        // do once setup .......}

        File[] fs = getFotos();
        int sucessos = 0;
        int falhamatricula = 0;
        int falhatipo = 0;
        int falhaprocessamento = 0;
        int duplicatas = 0;

        _matriculasOk.clear();
        _matriculasFalhas.clear();
        _bufferEC.clear();

        long t0 = System.currentTimeMillis();
        for (int i=0;i<fs.length;i++) {
            System.out.println((i+1)+"-th processing...");
            try {
                byte status = this.processarFoto(fs[i], pp, g);
                if (status == EC.PROCESSAMENTO_SUCESSO)
                    sucessos++;
                else if (status == EC.PROCESSAMENTO_MATRICULA_FALHOU)
                    falhamatricula++;
                else if (status == EC.PROCESSAMENTO_TIPO_PROVA_FALHOU)
                    falhatipo++;
                else if (status == EC.PROCESSAMENTO_IMAGEM_FALHOU)
                    falhaprocessamento++;
                else if (status == EC.PROCESSAMENTO_DUPLICATA)
                    duplicatas++;
            }
            catch (IOException ex) {
            }


            int batchSize = 20;
            if (_bufferEC.size() >= batchSize) {
                System.out.println(String.format("Saving..."));
                long tSave = System.currentTimeMillis();
                _modelProvaCorrecao.addEntradasProvaCorrecao(_bufferEC);
                long tf = System.currentTimeMillis();
                tSave = tf - tSave;
                System.out.println(String.format("Saved %d registers on database on %.3f seg. Total time until now %.3f seg. Time per reg. %.3f seg.",batchSize,tSave/1000.0,(tf-t0)/1000.0,(tf-t0)/(1000.0*i)));
                _bufferEC.clear();
            }

        }
        long t = System.currentTimeMillis() - t0;


        if (_bufferEC.size() > 0) {
            System.out.println("Saving on database");
            _modelProvaCorrecao.addEntradasProvaCorrecao(_bufferEC);
            _bufferEC.clear();
        }


        System.out.println("Matriculas encontradas");
        for (String s: _matriculasOk) {
            System.out.println(""+s);
        }
        System.out.println("Matriculas falhas");
        for (String s: _matriculasFalhas) {
            System.out.println(""+s);
        }

        JOptionPane.showMessageDialog(this,"Fotos processadas com sucesso: "+sucessos+
                                      "\nTotal de fotos: "+fs.length+
                                      "\nTempo: "+t+
                                      "\nFalha pontos de controle: "+falhaprocessamento+
                                      "\nFalha decodificacao codigo MIXnFIX: "+falhatipo+
                                      "\nMatriculas OK: "+this._matriculasOk.size()+
                                      "\nMatriculas Duplicatas: "+duplicatas+
                                      "\nMatriculas Ruins: "+this._matriculasFalhas.size());





    }

    HashSet<String> _matriculasOk = new HashSet<String>();
    HashSet<String> _matriculasFalhas = new HashSet<String>();
    ArrayList<EC> _bufferEC = new ArrayList<EC>();

    private mixnfix.prova.ProvaStructure getProvaStructure(mixnfix.modelo.Prova prova) {
        try {
            Controller.unzip(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+prova.getFonte(), Controller.TMP_DIR);
            mixnfix.prova.Parser parser = new mixnfix.prova.Parser(Controller.TMP_DIR + "/prova.xml");
            mixnfix.prova.ProvaStructure pp = parser.getProva();
            return pp;
        }
        catch (Exception x) {
            x.printStackTrace();
        }
        return null;
    }

    static byte _data[] = new byte[10000000];

    public String reverse(String st) {
        String result = "";
        for (int i=st.length()-1;i>=0;i--) {
            result += st.charAt(i);
        }
        return result;
    }

    /**
     * Devolve um flag e acumula dados da correcao no buffer: _bufferEC.
     */
    private byte processarFoto(File fotoFile, mixnfix.prova.ProvaStructure p, GeradorFolhaRespostas g) throws IOException {

        BufferedImage image = ImageIO.read(fotoFile);

        double controlPoints[] = new double[1000];
        _data = MFI2Java.ensureBuffer(_data, image);
        MFI2Java.loadImageToBuffer(image, _data);
        boolean b = MFI2Java.fitToImage(_data, image.getWidth(), image.getHeight(), controlPoints);

        if (!b) {
            return EC.PROCESSAMENTO_IMAGEM_FALHOU;
        }

        CellMap mapFixo = g.getCellMapFixo();

        for (ControlPoint cp : mapFixo.getControlPoints()) {
            cp.setImageXY(controlPoints[2 * cp.getId()], controlPoints[2 * cp.getId() + 1]);
        }

        // save the composed verification image of this frame: the grey
        // scale exam with the control points painted in yellow
        mixnfix.VisualizationExporter.saveComposed(image, controlPoints,
            mapFixo.getNumControlPoints(), fotoFile.getName());

        // data to save on buffer
        PanelParametrosProcessamentoImagem _params = new PanelParametrosProcessamentoImagem();
        int threshold = 0;
        int phase = _params.getPhase();
        int ncps = mapFixo.getNumControlPoints();
        double cps[] = new double[2*ncps];
        System.arraycopy(controlPoints,0,cps,0,2*ncps);
        // data to save on buffer

        { // obter intensidades da imagem
            double[] x = new double[2];
            double[] y = new double[2];
            ArrayList<Cell> cells = new ArrayList<Cell> ();
            mapFixo.getField(CellMap.MULTICAMPO_ID).getCells(cells);
            mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX).getCells(cells);
            for (Cell cell : cells) {
                x[0] = cell.getX() - mapFixo.getX0();
                x[1] = cell.getY() - mapFixo.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                // System.out.println(String.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem

        // verificar se é possível obter o código da prova
        BinaryField mfCode = (BinaryField) mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX);
        boolean codeword[] = new boolean[mfCode.getNumCells()];
        mfCode.evaluateFromImageIntensity(_params.getSeparationLevel());
        mfCode.getValue(codeword);
        int indice = Prova2TeX.readnumber(codeword, 0, 20);
        int tipowithgolay = Prova2TeX.readnumber(codeword, 20, 24);
        int tipo = Prova2TeX.calcularNumeroNoCodigoDeGolay(tipowithgolay);

        // obter o identificador do aluno
        MultiField mfId = (MultiField) mapFixo.getField(CellMap.MULTICAMPO_ID);
        String idAluno = "";
        for (Field f : mfId.getFields()) {
            OptionField of = (OptionField) f;
            of.evaluateFromImageIntensity(_params.getGapLevel());
            int digit = of.getValue();
            if (digit == OptionField.BLANK || digit == OptionField.BLANK || digit == OptionField.NOT_EVALUATED)
                idAluno += "?";
            else
                idAluno += digit;
        }

        //@todo gambiarra para matrículas não perfeitas...
        
        // gambiarra para casar nao a matricula completa, mas substrings
//        int count = 0;
//        for (String matricula: _mapMatricula2MAPC.keySet()) {
//            if (idAluno.indexOf(matricula) != -1) {
//                System.out.println("Matricula: "+matricula+" casa com "+ idAluno);
//                idAluno = matricula;
//                count++;
//            }
//            else if (reverse(idAluno).indexOf(matricula) != -1) {
//                System.out.println("Matricula: "+matricula+" casa com "+ idAluno);
//                idAluno = matricula;
//                count++;
//            }
//        }
//        if (count > 1) {
//            System.out.println("PROBLEMA! marcacao aluno é substring de mais de uma matricula");
//            return EC.PROCESSAMENTO_MATRICULA_FALHOU;
//        }
        // gambiarra para casar nao a matricula completa, mas substrings


        ModelAlunoProvaCorrecao mapc = this._mapMatricula2MAPC.get(idAluno);

        if (indice != p.getIndex()) {
            // data to save on buffer
            // _bufferEC.add(new EC(EC.PROCESSAMENTO_TIPO_PROVA_FALHOU, fotoFile, threshold, phase, -1, cps, null, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_TIPO_PROVA_FALHOU;
        }

        if (mapc == null) {
            _matriculasFalhas.add(idAluno);
            System.out.println("Matricula Falhou: "+idAluno);

            // data to save on buffer
            // _bufferEC.add(new EC(EC.PROCESSAMENTO_MATRICULA_FALHOU, fotoFile, threshold, phase, tipo, cps, null, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_MATRICULA_FALHOU;
        }
        /*
        else if (_matriculasOk.contains(idAluno)) {
            return EC.PROCESSAMENTO_DUPLICATA;
        }*/

        _matriculasOk.add(idAluno);

        g.inicializarQuesitos(tipo);

        { // obter intensidades da imagem para a parte variável da prova
            CellMap mapVariavel = g.getCellMapVariavel();
            double[] x = new double[2];
            double[] y = new double[2];
            for (Cell cell : mapVariavel.getCells()) {
                x[0] = cell.getX() - mapVariavel.getX0();
                x[1] = cell.getY() - mapVariavel.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                // System.out.println(String.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem para a parte variável da prova

        { // calcular os campos marcados para cada quesito
            CellMap cellMapVariavel = g.getCellMapVariavel();

            HashMap<String, Integer> mapCampoValor = new HashMap<String, Integer> ();

            java.util.List<Field> fields = cellMapVariavel.getAllFields();
            for (Field f : fields) {
                if (f instanceof OptionField) {
                    OptionField of = (OptionField) f;
                    of.evaluateFromImageIntensity(_params.getGapLevel());
                    mapCampoValor.put(of.getNome(), of.getValue());
                    // System.out.println("" + of.getNome() + " = " + of.getValue());
                }
                else if (f instanceof BinaryField) {
                    BinaryField bf = (BinaryField) f;
                    bf.evaluateFromImageIntensity(_params.getSeparationLevel());
                }
            }

            // data to save on buffer
            _bufferEC.add(new EC(EC.PROCESSAMENTO_SUCESSO, fotoFile, threshold, phase, tipo, cps, mapCampoValor, mapc));
            // data to save on buffer

            return EC.PROCESSAMENTO_SUCESSO;

        } // calcular os campos marcados para cada quesito

    }

    public void extractVideo() {
        PanelExtractVideo p = new PanelExtractVideo();
        p.run((JFrame)this.getTopLevelAncestor());
        if (!p.isOk())
            return;
        mixnfix.Library.extractFrames(p.getInputFileName(),p.getNumFrames(),p.getPrefix(),p.getOutputDir());
    }


    public void relatorioTexto() throws Exception {
        PanelRelatorioTexto p = new PanelRelatorioTexto();
        p.run(MainFrame.MAIN_FRAME);
        if (p.isOk()) {
            PrintWriter pw = new PrintWriter(new FileOutputStream(p.getInputFileName()));




            ArrayList<ModelEntradaProvaCorrecao> listEntradaProvaCorrecao = new ArrayList<ModelEntradaProvaCorrecao>(_modelProvaCorrecao.getEntradasProvaCorrecao());
            Collections.sort(listEntradaProvaCorrecao,new Comparator() {
                public int compare(Object o1, Object o2) {
                    ModelEntradaProvaCorrecao a = (ModelEntradaProvaCorrecao) o1;
                    ModelEntradaProvaCorrecao b = (ModelEntradaProvaCorrecao) o2;
                    if (a.getAluno() != null && b.getAluno() != null) {
                        return a.getAluno().getNome().compareTo(b.getAluno().getNome());
                    }
                    else if (a.getAluno() != null) return -1;
                    else if (b.getAluno() != null) return 1;
                    else return 0;
                }
                public boolean equals(Object obj) {
                    return false;
                }
            });


            int i=1;
            for (ModelEntradaProvaCorrecao mepc : listEntradaProvaCorrecao) {
                System.out.println(""+mepc.getAluno().getNome());
                pw.println("___________________________________________________________________________________________");
                pw.println((i++)+". "+mepc.getAluno().getNome()+" ("+mepc.getAluno().getMatricula()+")");
                StringBuffer detalhamento = new StringBuffer();
                mepc.preencherProvaComGabaritoCorrente();
                mepc.getProvaStructure().avaliarNota();
                mepc.getProvaStructure().detalharNota(detalhamento);
                pw.println(detalhamento.toString());
            }
            pw.close();
        }
    }

    public void relatorioPDFLaTeX() throws Exception {

        JDialog d = new JDialog((JFrame)this.getTopLevelAncestor(),"Produzir Prova",true);
        d.setContentPane(new PanelProduzirRelatorio(this._modelProvaCorrecao));
        linsoft.gui.util.Library.resizeAndCenterWindow(d,420,470);
        d.setVisible(true);




        //PrintWriter pw = new PrintWriter(new FileOutputStream("listao.tex"));
        //pw.println(ModelProvaCorrecaoReport.gerarRelatorioPDFLaTeX(_modelProvaCorrecao));
        //pw.close();
    }

    private void exportarFotos() throws IOException {
        PanelExportImages p = new PanelExportImages();
        p.run(MainFrame.MAIN_FRAME);
        int i=1;
        if (p.isOk()) {
            for (ModelEntradaProvaCorrecao mepc : _modelProvaCorrecao.getEntradasProvaCorrecao()) {
                File in = new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+mepc.getEntradaProvaCorrecao().getFoto());
                File dirout = new File(p.getOutputDir());
                File out = new File(dirout.getAbsolutePath()+"/"+p.getPrefix()+(i++)+".jpg");
                System.out.println(""+in.getName()+" -> "+out.getName());

                // copy file
                mixnfix.Library.copyFile(in,out);
            }
        }
    }

    public void calibragem() throws Exception {
        JDialog d = new JDialog( (JFrame)this.getTopLevelAncestor(), "Calibrar Parâmetros de Correção ", true);
        d.setContentPane(new PanelCalibragemCorrecao(_modelProvaCorrecao));
        linsoft.gui.util.Library.resizeAndCenterWindow(d, 1000, 640);
        d.setVisible(true);
    }

    private void selectEntrada(ModelEntradaProvaCorrecao entrada) throws SQLException,IOException {
        if (entrada == null) {
            this._taDetalhamentoProva.setText("");
            _panelViewCorrecao.removeAll();
            _panelViewCorrecao.revalidate();
            _panelViewCorrecao.repaint();
        }
        else {

            CorrecaoUI correcaoUI = new CorrecaoUI(entrada);
            this._taDetalhamentoProva.setText(correcaoUI.getDetalhamentoNota());
            _pView = correcaoUI.getPanelVisualizacaoProva();
            _pView.setPreferredSize(new Dimension(4800, 6400));
            JScrollPane sp = new JScrollPane(_pView);
            _panelViewCorrecao.removeAll();
            _panelViewCorrecao.setLayout(new BorderLayout());
            _panelViewCorrecao.add(sp, BorderLayout.CENTER);
            _panelViewCorrecao.revalidate();
            _panelViewCorrecao.repaint();
        }
    }
}



class PanelRelatorioTexto extends JPanel {
    private boolean _ok;
    private Input _inputFileName;
    public PanelRelatorioTexto() {
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        _inputFileName = new Input(App.getConfiguracao(),"relatorio","c:/pub/mixnfix/video.mpg",Input.TF_FILE,200);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        JButton btnVideo = new JButton("...");
        btnVideo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchVideo();
            }
        });

        this.add(new JLabel("Video"));
        this.add(_inputFileName);
        this.add(btnVideo);

        this.add(btnOk);
        this.add(new JLabel(""));
        this.add(btnCancel);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(this,
                                2, 3, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad
    }

    public void ok() {
        _ok = true;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void cancel() {
        _ok = false;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void run(JFrame parent) {
        JDialog d = new JDialog(parent,"Extract Frames",true);
        linsoft.gui.util.Library.resizeAndCenterWindow(d,580,170);
        d.setContentPane(this);
        d.setVisible(true);
    }

    public boolean isOk() {
        return _ok;
    }

    public String getInputFileName() { return _inputFileName.getText(); }

    public void searchVideo() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(this._inputFileName.getText()));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                StringTokenizer st = new StringTokenizer(f.getName(), ".");
                String last = null;
                while (st.hasMoreTokens()) {
                    last = st.nextToken();
                }
                if (last != null) {
                    last = last.toLowerCase();
                    if ("txt".equals(last))
                        return true;
                }
                return false;
            }

            public String getDescription() {
                return "Texto (.txt)";
            }
        });
        int result = jfc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            this._inputFileName.setTextAndSave(jfc.getSelectedFile().getAbsolutePath());
        }
    }
}

