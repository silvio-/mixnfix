package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import linsoft.gui.AntialiasedLabel;
import mixnfix.Model;
import mixnfix.ModelListener;
import mixnfix.folharesposta.FolhaRespostaQuestionarioBasico;

/**
 *
 */
public class PanelColetaQuestionario extends JPanel {
    private ModelColetaQuestionario _modelColetaQuestionario;

    private JList _listEntradas;
    private DefaultListModel _listModelEntradas;
    private JPanel _panelViewCorrecao;
    private JTextArea _taDetalhamento =  new JTextArea();
    private QuestionarioUI.PanelVisualizacaoProva _pView;

    private AntialiasedLabel _lblContadores;

    // private ColetaQuestionario
    public PanelColetaQuestionario(ModelColetaQuestionario modelColetaQuestionario) {
        _modelColetaQuestionario = modelColetaQuestionario;

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
        JButton btnProcessarFotos = new JButton("Coletar Questionários");
        btnProcessarFotos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MFActionColetarQuestionarios(
                    _modelColetaQuestionario,
                    FolhaRespostaQuestionarioBasico.ID
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


        JButton btnCalibragem = new JButton("Calibrar Correção");
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
        _listModelEntradas = new DefaultListModel();


        ArrayList<ModelEntradaColetaQuestionario> listEntradaColetaQuestionario = new ArrayList<ModelEntradaColetaQuestionario>(_modelColetaQuestionario.getEntradasColetaQuestionario());
        Collections.sort(listEntradaColetaQuestionario,new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelEntradaColetaQuestionario a = (ModelEntradaColetaQuestionario) o1;
                ModelEntradaColetaQuestionario b = (ModelEntradaColetaQuestionario) o2;
                return a.getEntradaColetaQuestionario().getId() - b.getEntradaColetaQuestionario().getId();
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        for (ModelEntradaColetaQuestionario mepc: listEntradaColetaQuestionario) _listModelEntradas.addElement(mepc);

        // criar lists
        _listEntradas = new JList(_listModelEntradas);

        // renderer
        _listEntradas.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelEntradaColetaQuestionario x = (ModelEntradaColetaQuestionario) value;
                this.setIcon(Images.ColetaQuestionario);
                this.setText(""+x.getEntradaColetaQuestionario().getId());
                return this;
            }
        });
        _listEntradas.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    if (!e.getValueIsAdjusting())
                        selectEntrada( (ModelEntradaColetaQuestionario) _listEntradas.getSelectedValue());
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
            _modelColetaQuestionario.addListener(new ModelListener() {
                public void update(Model model) {}

                private Model _addedModel;
                public void nodeAdded(Model model, Model addedModel, int index) {
                    _addedModel = addedModel;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (_addedModel instanceof ModelEntradaColetaQuestionario) {
                                _listModelEntradas.addElement(_addedModel);
                                updateLabelContadores();
                            }
                        }
                    });
                }

                private java.util.List<Model>  _addedModels;
                public void nodesAdded(Model model, java.util.List<Model> addedModels, int index) {
                    _addedModels = addedModels;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (Model m : _addedModels) {
                                if (m instanceof ModelEntradaColetaQuestionario) {
                                    _listModelEntradas.addElement(m);
                                    updateLabelContadores();
                                }
                            }
                        }
                    });
                }

                private java.util.List<Model>  _loadedModels;
                public void nodesLoaded(Model model, java.util.List<Model> addedModel) {
                    _loadedModels = addedModel;
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            for (Model m: _loadedModels) {
                                if (m instanceof ModelEntradaColetaQuestionario) {
                                    _listModelEntradas.addElement(m);
                                    updateLabelContadores();
                                }
                            }
                        }
                    });
                }

                private ArrayList<Model>  _removedModels = new ArrayList<Model>();
                public void nodeRemoved(Model model, Model removedModel) {
                    _removedModels.add(removedModel);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            synchronized (_removedModels) { // modify static fields here
                                for (Model m : _removedModels) {
                                    if (m instanceof ModelEntradaColetaQuestionario) {
                                        _listModelEntradas.removeElement(m);
                                    }
                                }
                                _removedModels.clear();
                                updateLabelContadores();
                            }
                        }
                    });
                }
            });
        } // attatch model


        // panel alunos
        JPanel alunosPanel = new JPanel();
        alunosPanel.setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        alunosPanel.add(panel1,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));

        // panel correcoes
        JPanel correcoesPanel = new JPanel();
        correcoesPanel.setLayout(new BorderLayout());

        JScrollPane spEntradas = new JScrollPane(_listEntradas);
        // spEntradas.setPreferredSize(new Dimension(300,100));
        _panelViewCorrecao = new JPanel();

        JSplitPane splitPaneCorrecoesDetalhamento = new JSplitPane();
        splitPaneCorrecoesDetalhamento.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPaneCorrecoesDetalhamento.add(spEntradas,JSplitPane.TOP);
        splitPaneCorrecoesDetalhamento.add(new JScrollPane(_taDetalhamento),JSplitPane.BOTTOM);

        JPanel leftSidePanel = new JPanel();
        leftSidePanel.setLayout(new BorderLayout());
        leftSidePanel.add(splitPaneCorrecoesDetalhamento,BorderLayout.CENTER);


        JSplitPane splitPane = new JSplitPane();
        // splitPane.add(splitPaneCorrecoesDetalhamento,JSplitPane.LEFT);
        splitPane.add(leftSidePanel,JSplitPane.LEFT);
        splitPane.add(_panelViewCorrecao,JSplitPane.RIGHT);
        splitPane.setDividerLocation(350);

        correcoesPanel.add(splitPane,BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        panel2.add(btnProcessarFotos);
        panel2.add(btnExtractFrames);
        panel2.add(btnRemoverEntradas);
        panel2.add(btnExportarFotos);
        panel2.add(btnCalibragem);
        correcoesPanel.add(panel2,BorderLayout.SOUTH);

        JTabbedPane tabPane = new JTabbedPane();
        tabPane.add("Correções",correcoesPanel);

        this.setLayout(new BorderLayout());
        this.add(tabPane,BorderLayout.CENTER);
        _lblContadores = new AntialiasedLabel(
        _modelColetaQuestionario.getColetaQuestionario().getNome()+" ("+
        _modelColetaQuestionario.getNumEntradas()+")");
        _lblContadores.setFont(new Font("Tahoma",Font.PLAIN,36));
        _lblContadores.setIcon(Images.ColetaQuestionario);

        this.add(_lblContadores,BorderLayout.NORTH);
    }

    private void updateLabelContadores() {
        _lblContadores.setText(
        _modelColetaQuestionario.getColetaQuestionario().getNome()+" ("+
        _modelColetaQuestionario.getNumEntradas()+")");
    }

    private void removerEntradas() throws IOException, SQLException {
        Object[] models = _listEntradas.getSelectedValues();
        if (models.length == 0) {
            JOptionPane.showMessageDialog(this, "Não existem entradas selecionadas!");
            return;
        }
        ArrayList<ModelEntradaColetaQuestionario> list = new ArrayList<ModelEntradaColetaQuestionario>();
        for (Object o: models) {
            list.add((ModelEntradaColetaQuestionario) o);
        }

        if (JOptionPane.showConfirmDialog(MainFrame.MAIN_FRAME, "Deseja remover entradas selecionadas?",
                                          "Confirmação", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            _listEntradas.getSelectionModel().clearSelection();
            new MFActionRemoverEntradasColetaQuestionario(this._modelColetaQuestionario, list).actionPerformed(new ActionEvent(this, 1, ""));
        }
    }

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

    public void extractVideo() {
        PanelExtractVideo p = new PanelExtractVideo();
        p.run((JFrame)this.getTopLevelAncestor());
        if (!p.isOk())
            return;
        mixnfix.Library.extractFrames(p.getInputFileName(),p.getNumFrames(),p.getPrefix(),p.getOutputDir());
    }

    private void exportarFotos() throws IOException {
        PanelExportImages p = new PanelExportImages();
        p.run(MainFrame.MAIN_FRAME);
        int i=1;
        if (p.isOk()) {
            for (ModelEntradaColetaQuestionario mepc : _modelColetaQuestionario.getEntradasColetaQuestionario()) {
                File in = new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+mepc.getEntradaColetaQuestionario().getFoto());
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
        d.setContentPane(new PanelCalibragemQuestionario(_modelColetaQuestionario));
        linsoft.gui.util.Library.resizeAndCenterWindow(d, 1000, 640);
        d.setVisible(true);
    }

    private void selectEntrada(ModelEntradaColetaQuestionario entrada) throws SQLException,IOException {
        if (entrada == null) {
            this._taDetalhamento.setText("");
            _panelViewCorrecao.removeAll();
            _panelViewCorrecao.revalidate();
            _panelViewCorrecao.repaint();
        }
        else {
            QuestionarioUI questionarioUI = new QuestionarioUI(entrada);
            this._taDetalhamento.setText(questionarioUI.getDetalhamentoNota());
            _pView = questionarioUI.getPanelVisualizacaoProva();
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
