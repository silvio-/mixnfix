package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import linsoft.gui.Input;
import linsoft.gui.InputBoolean;
import linsoft.gui.PanelChooseFile;
import mixnfix.Library;
import mixnfix.composicaoprova.ParametrosDeLayoutDaProva;
import mixnfix.composicaoprova.Prova2TeX;
import mixnfix.prova.Grupo;
import mixnfix.prova.No;
import mixnfix.prova.NoProva;
import mixnfix.prova.PanelProperties;
import mixnfix.prova.PanelQuesito;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;
import mixnfix.prova.Tags;

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
public class PanelEdicaoProva extends JPanel {
    private JTree _tree;
    private DefaultTreeModel _treeModel;
    private JSplitPane _splitPane = new JSplitPane();
    private JTextField _tfSerial;

    private ModelProva _modelProva;
    private ProvaStructure _prova;


    public PanelEdicaoProva(ModelProva modelProva) {
        _modelProva = modelProva;
        _prova = modelProva.getProvaStructure();

        { // setup tree
            _tree = new JTree();
            _tree.setModel(new DefaultTreeModel(new TNode("root",TNode.TYPE_ROOT)));
            _tree.setRootVisible(false);
            _tree.addTreeWillExpandListener(new TreeWillExpandListener() {
                public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                }
                public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                    // throw new ExpandVetoException(event);
                }
            });
            _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            _tree.setFont(new Font(_tree.getFont().getFontName(),Font.PLAIN,14));
            _tree.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    // not a right click?
                    if (e.getButton() != MouseEvent.BUTTON3)
                        return;
                    //
                    // treeMenu(e.getX(), e.getY());
                }
            });

            _tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath p = _tree.getSelectionPath();
                    if (p == null)
                        return;
                    TNode n = (TNode) p.getLastPathComponent();
                    selecionar(n);
                }
            });

            //-----------------------------------------------------
            // Renderizador
            DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
                Icon tutorialIcon;
                public Component getTreeCellRendererComponent(
                    JTree tree,
                    Object value,
                    boolean sel,
                    boolean expanded,
                    boolean leaf,
                    int row,
                    boolean hasFocus) {
                    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                    // user object
                    Object obj = ( (DefaultMutableTreeNode) value).getUserObject();

                    TNode node = (TNode) value;

                    switch (node.getType()) {
                        case TNode.TYPE_PROVA: {
                            ProvaStructure prova = (ProvaStructure) obj;
                            this.setText("Prova");
                            this.setIcon(Images.Instituicao16x16);
                        }
                        break;
                        case TNode.TYPE_GRUPO: {
                            Grupo grupo = (Grupo) obj;
                            this.setText("Grupo: "+grupo.getProperty(Tags.TAG_TAG));
                            this.setIcon(Images.Folder16x16);
                        }
                        break;
                        case TNode.TYPE_QUESITO: {
                            Quesito quesito = (Quesito) obj;
                            this.setText("Quesito: "+quesito.getProperty(Tags.TAG_TAG));
                            this.setIcon(Images.Folder16x16);
                        }
                        break;
                    }

                    // System.out.println("Value class: "+value.getClass());
                    return this;
                }
            };
            _tree.setCellRenderer(renderer);
            // Renderizador
            //-----------------------------------------------------
        } // setup tree

        this.mountTree();

        JPanel btnPanel = new JPanel();
        JButton btnProduzirProva = new JButton("Produzir Prova");
        btnProduzirProva.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                (new MFActionProduzirProva(_modelProva)).actionPerformed(e);
            }
        });
        btnPanel.add(btnProduzirProva);

        // imprimir pdfs
        JButton btnImprimirPDFs = new JButton("Imprimir PDFs");
        btnImprimirPDFs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                imprimirPDFs();
            }
        });
        btnPanel.add(btnImprimirPDFs);

        // imprimir pdfs
        JButton btnGerarPontoProva = new JButton("-> .prova");
        btnGerarPontoProva.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    gerarPontoProva();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        btnPanel.add(btnGerarPontoProva);

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
        btnPanel.add(btnRelatorioPDFLaTeX);


        _splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        _splitPane.add(new JScrollPane(_tree));
        _splitPane.add(new JLabel());
        _splitPane.setDividerLocation(350);


        this.setLayout(new BorderLayout());
        this.add(btnPanel,BorderLayout.NORTH);
        this.add(_splitPane,BorderLayout.CENTER);



        expandTree();
    }

    /**
     * Initializes _tree and everything else that
     * is related to that.
     */
    private void mountTree() {
        TNode rootNode = new TNode("root", TNode.TYPE_ROOT);
        _treeModel = new DefaultTreeModel(rootNode);

        TNode provaNode = new TNode(_prova, TNode.TYPE_PROVA);
        rootNode.add(provaNode);

        Stack<TNode> S = new Stack<TNode>();
        S.push(provaNode);
        while (!S.isEmpty()) {
            TNode n = S.pop();
            Object o = n.getUserObject();
            java.util.List<NoProva> list = null;

            if (o instanceof ProvaStructure) {
                ProvaStructure p = (ProvaStructure) o;
                list = p.getRoot().getChilds();
            }
            else if (o instanceof Grupo) {
                Grupo g = (Grupo) o;
                list = g.getChilds();
            }

            for (NoProva np : list) {
                if (np instanceof Quesito) {
                    TNode nQuesito = new TNode(np, TNode.TYPE_QUESITO);
                    n.add(nQuesito);
                    //_tree.expandPath(new TreePath(nQuesito.getPath()));
                }
                else if (np instanceof Grupo) {
                    TNode nGrupo = new TNode(np, TNode.TYPE_GRUPO);
                    n.add(nGrupo);
                    S.push(nGrupo);
                    //_tree.expandPath(new TreePath(nGrupo.getPath()));
                }
            }
        }
        _tree.setModel(_treeModel);
    }

    private void expandTree() {
        Stack<TNode> S = new Stack<TNode>();
        S.push((TNode)_treeModel.getRoot());
        while (!S.isEmpty()) {
            TNode n = (TNode) S.pop();
            if (n.isLeaf())
                _tree.expandPath(new TreePath(n.getPath()));
            else {
                Enumeration e = n.children();
                while (e.hasMoreElements())
                    S.push( (TNode) e.nextElement());
            }
        }
    }

    private void selecionar(TNode n) {
        Object o = n.getUserObject();
        if (o instanceof Quesito) {
            PanelQuesito pQuesito = new PanelQuesito((Quesito) o);
            int l = _splitPane.getDividerLocation();
            _splitPane.setRightComponent(pQuesito);
            _splitPane.setDividerLocation(l);
        }
        else if (o instanceof No) {
            Properties p = ((No) o).getProperties();
            PanelProperties pp = new PanelProperties(p);
            int l = _splitPane.getDividerLocation();
            _splitPane.setRightComponent(pp);
            _splitPane.setDividerLocation(l);
        }
    }

    private void imprimirPDFs() {
        PanelChooseDirectory pcd = new PanelChooseDirectory();
        pcd.run((JFrame)this.getTopLevelAncestor());
        if (pcd.isOk()) {
            File path = new File(pcd.getOutputDir());

            ArrayList<File> files = new ArrayList<File>();
            for (File f: path.listFiles()) {
                if (f.getName().indexOf(".pdf") >= 0)
                    files.add(f);
            }

            // sort files
            Collections.sort(files,new Comparator() {
                public int compare(Object o1, Object o2) {
                    File f1 = (File) o1;
                    File f2 = (File) o2;
                    return f1.getName().compareTo(f2.getName());
                }
                public boolean equals(Object obj) {
                    return false;
                }
            });

            System.out.println("Printing PDFs in "+path.getAbsolutePath());
            int count = 1;
            for (File f: files) {
                System.out.println("Printing file "+(count++)+": "+f.getName());

                // Print to default printer
                String command = "java -cp . PDFPrint "+path.getAbsolutePath()+" "+f.getName();
                try {
                    Library.executeCommand(command,true);
                    Thread.currentThread().sleep(1500);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,"Problema no envio para impressão");
                    return;
                }
                //ShellExec.shellExecute("print", f.getName(), "", path.getAbsolutePath());
            }
        }
    }

    private void gerarPontoProva() throws Exception  {
        PanelChooseFile pcf = new PanelChooseFile(
            "pontoprova",
            "c:/x.prova",
            App.getConfiguracao(),
            PanelChooseFile.SAVE,
            new String[] {"prova"});

        pcf.run((JFrame)this.getTopLevelAncestor());
        if (pcf.isOk()) {

            // Add ZIP entry to output stream.

            CharsetEncoder x;
            // ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(pcf.getInputFileName()));
            PrintWriter pw = new PrintWriter("c:/prova.xml","utf-8");

            // zos.putNextEntry(new ZipEntry("prova.xml"));
            _modelProva.gerarXML(pw);

            pw.flush();
            // zos.closeEntry();
            pw.close();


            JOptionPane.showMessageDialog(this,"Arquivo "+pcf.getInputFileName()+" gerado!");
        }
    }

    public void relatorioPDFLaTeX() throws Exception {
        JDialog d = new JDialog((JFrame)this.getTopLevelAncestor(),"Relatório da Prova",true);
        d.setContentPane(new PanelProduzirRelatorioModelProva(this._modelProva));
        linsoft.gui.util.Library.resizeAndCenterWindow(d,420,470);
        d.setVisible(true);
    }


}

class TNode extends DefaultMutableTreeNode {
    public static final byte TYPE_ROOT = (byte) -1;
    public static final byte TYPE_PROVA = (byte) 0;
    public static final byte TYPE_GRUPO = (byte) 1;
    public static final byte TYPE_QUESITO = (byte) 2;

    private byte _type;
    public TNode(Object obj, byte type) {
        super(obj);
        _type = type;
    }

    public int getType() {
        return _type;
    }
}

class PanelProduzirProva extends JPanel {
    ModelProva _prova;
    ProvaStructure _provaStructure;

    Input _tfTamanhoFonte;

    Input _tfPaperHeight;
    Input _tfPaperWidth;

    Input _tfTopMargin;
    Input _tfLeftMargin;
    Input _tfBottomMargin;
    Input _tfRightMargin;

    Input _tfTiposDeProvas;

    Input _tfTamMatricuala;

    Input _tfNumColumns;

    Input _tfOutputDir;

    Input _tfLogoImage;

    InputBoolean _ibFolhaEmBrancoAposFR;

    InputBoolean _ibMultipleFiles;

    Input _ibNumeroDePaginasEmBrancoNoFim;

    InputBoolean _ibColocarValorDaQuestao;
    InputBoolean _ibColocarGabarito;
    InputBoolean _ibGoodbreak;
    JButton _btnProduzir;

    public PanelProduzirProva(ModelProva prova) {
        _prova = prova;
        _provaStructure = prova.getProvaStructure();

        _btnProduzir = new JButton("Produzir");
        _btnProduzir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    produzir();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        this.setLayout(new GridBagLayout());

        int i=0;

        this.add(new JLabel("Diretório de Saída:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfOutputDir = new Input(App.getConfiguracao(),"outputdir","c:/",Input.TF_DIRECTORY,250);
        this.add(_tfOutputDir,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Tipos:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfTiposDeProvas = new Input(App.getConfiguracao(),"tipos","0-4",Input.TF_INTERVALOS,90);
        this.add(_tfTiposDeProvas,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Logo:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfLogoImage = new Input(App.getConfiguracao(),"logo-image","",Input.TF_FILE,90);
        this.add(_tfLogoImage,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Tam. Matrícula:"), new GridBagConstraints(0, i, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        this._tfTamMatricuala = new Input(App.getConfiguracao(), "tammatricula", "11", Input.TF_INTEIRO, 50);
        this.add(_tfTamMatricuala, new GridBagConstraints(1, i, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        i++;

        this.add(new JLabel("Tamanho da Fonte:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfTamanhoFonte = new Input(App.getConfiguracao(),"tamanhofonte","11",Input.TF_INTEIRO,50);
        this.add(_tfTamanhoFonte,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Margem Esquerda:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfLeftMargin = new Input(App.getConfiguracao(),"leftmargin","1.50",Input.TF_FLOAT,50);
        this.add(_tfLeftMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Margem Direita:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfRightMargin = new Input(App.getConfiguracao(),"rightmargin","1.50",Input.TF_FLOAT,50);
        this.add(_tfRightMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Margem Topo:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfTopMargin = new Input(App.getConfiguracao(),"topmargin","1.50",Input.TF_FLOAT,50);
        this.add(_tfTopMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Margem de Baixo:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfBottomMargin = new Input(App.getConfiguracao(),"bottommargin","1.50",Input.TF_FLOAT,50);
        this.add(_tfBottomMargin,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Largura Papel:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfPaperWidth = new Input(App.getConfiguracao(),"largurapapel","21.0",Input.TF_FLOAT,50);
        this.add(_tfPaperWidth,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Altura Papel:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfPaperHeight = new Input(App.getConfiguracao(),"alturapapel","29.7",Input.TF_FLOAT,50);
        this.add(_tfPaperHeight,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Num Columns:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfNumColumns = new Input(App.getConfiguracao(),"numcolumns","2",Input.TF_INTEIRO,50);
        this.add(_tfNumColumns,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Num.Pág.Brancas no Fim:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._ibNumeroDePaginasEmBrancoNoFim = new Input(App.getConfiguracao(),"folhaEmBrancoNoFimFR","0",Input.TF_INTEIRO,50);
        this.add(_ibNumeroDePaginasEmBrancoNoFim,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibFolhaEmBrancoAposFR = new InputBoolean(App.getConfiguracao(),"folhaEmBrancoAposFR",true,"Folha em branco após folha resposta");
        this.add(_ibFolhaEmBrancoAposFR,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibMultipleFiles = new InputBoolean(App.getConfiguracao(),"multipleFilesPDF",true,"Quebrar em múltiplos arquivos");
        this.add(_ibMultipleFiles,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibColocarValorDaQuestao = new InputBoolean(App.getConfiguracao(),"colocarValorDasQuestoes",true,"Colocar valor das questões.");
        this.add(_ibColocarValorDaQuestao,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibColocarGabarito = new InputBoolean(App.getConfiguracao(),"colocarGabarito",true,"Colocar gabarito.");
        this.add(_ibColocarGabarito,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibGoodbreak = new InputBoolean(App.getConfiguracao(),"goodbreak",true,"Colocar pontos de quebra de página após quesitos");
        this.add(_ibGoodbreak,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(_btnProduzir,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(20,2,2,2),0,0));
    }

    public void installDefaultButton() {
        JRootPane r = this.getRootPane();
        if (r != null)
            r.setDefaultButton(_btnProduzir);
    }

    private void produzir() throws Exception {
        ParametrosDeLayoutDaProva params = new ParametrosDeLayoutDaProva(
        Integer.parseInt(this._tfTamanhoFonte.getText()),
        Float.parseFloat(this._tfPaperWidth.getText()),
        Float.parseFloat(this._tfPaperHeight.getText()),
        Float.parseFloat(this._tfTopMargin.getText()),
        Float.parseFloat(this._tfBottomMargin.getText()),
        Float.parseFloat(this._tfLeftMargin.getText()),
        Float.parseFloat(this._tfRightMargin.getText()),
        Integer.parseInt(this._tfNumColumns.getText()),
        this._tfTiposDeProvas.getIntervalos(),
        this._tfOutputDir.getText(),
        this._ibFolhaEmBrancoAposFR.isSelected(),  // pg em branco
        this._ibMultipleFiles.isSelected(),
        this._tfLogoImage.getFile(), // multifiles
        this._ibNumeroDePaginasEmBrancoNoFim.getInt(),
        this._ibColocarValorDaQuestao.isSelected(),
        this._ibColocarGabarito.isSelected(),
        this._ibGoodbreak.isSelected(),
        1);  // pg em branco

        String workdir = _prova.getPath().getAbsolutePath() + "/";

        if (!params.getMultipleFiles()) {  // multiple files

            String baseName = "prova" + _provaStructure.getIndex() + "-" + _tfTiposDeProvas.getText();

            // -----------------------------------------------------------------
            // Verificar se arquivo já está aberto
            if (Library.isLocked(new File(params.getOutputDir()+"/"+baseName+".pdf"))) {
                JOptionPane.showMessageDialog(this, "Arquivo está travado por outro programa!");
                return;
            }
            // Verificar se arquivo já está aberto
            // -----------------------------------------------------------------

            
            //OutputStreamWriter out = new OutputStreamWriter(bout, "utf-8")
            
            
            PrintStream ps = new PrintStream(workdir + baseName + ".tex", "utf-8");
            new Prova2TeX(_provaStructure, ps, params, workdir);
            ps.close();

            
//            // Mostrar propriedades default
//            for (Object o: System.getProperties().keySet()) {
//            	System.out.println(o+" -> "+System.getProperties().getProperty((String) o));
//            }
            
//            // Mostrar variáveis de ambiente default
//            Map<String, String> env = System.getenv();
//            for (String envName : env.keySet()) {
//                System.out.format("%s=%s%n", envName, env.get(envName));
//            }
            
            // compile TEX to PDF
            String command = App.getConfiguracao().getCommandCompileTEX2PDF(workdir, params.getOutputDir(), "\"" + workdir + baseName + ".tex\"");
            String dir = workdir.replace("\\","/").replace(" ","\\ ");
            int status = Library.executeCommand(command,dir,null,true);
            if (status != 0) {
                JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
                return;
            }            

            // open PDF
            command = App.getConfiguracao().getCommandOpenPDF(params.getOutputDir() +"/" +baseName + ".pdf");
            Library.executeCommand(command, false);
            
//            
//            String[] tokens = command.split(" ");
//            
//            ProcessBuilder pb = new ProcessBuilder(tokens);
//            env = pb.environment();
//            for (String envName : env.keySet()) {
//                env.put(envName, env.get(envName));
//            	System.out.format("%s=%s%n", envName, env.get(envName));
//                
//            }
//             
//            //            env.put("VAR1", "myValue");
////            env.remove("OTHERVAR");
////            env.put("VAR2", env.get("VAR1") + "suffix");
//            pb.directory();
//            Process p = pb.start();
            
//            ProcessBuilder p = new ProcessBuilder(command);
//            p.environment().put("TEXINPUTS", workdir.replace("\\","/").replace(" ","\\ "));
//            p.
            
           // status = Library.executeCommand(command,"TEXINPUTS="+workdir.replace("\\","/").replace(" ","\\ "),true);
            
//            status = Library.executeCommand("pdflatex -halt-on-error" +
//                                            " -include-directory=" + workdir +
//                                            " -output-directory=" + params.getOutputDir() +
//                                            " -aux-directory=" + workdir +
//                                            " \"" + workdir + baseName + ".tex\" ", true);

//
//            String st = mixnfix.gui.App.getProperty("acrobat");
//            if (st == null || "".equals(st)) {
//                st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
//                mixnfix.gui.App.setProperty("acrobat", st);
//            }




        }
        else { // multiple files for each prova

            int intervalos[] = _tfTiposDeProvas.getIntervalos();
            int count = 0;

            for (int i=0;i<intervalos.length;i+=2) {
                int i1 = intervalos[i];
                int i2 = intervalos[i+1];
                for (int j = i1; j<=i2;j++) {
                    count++;
                    String baseName = "prova" + Library.fillLeft(""+_provaStructure.getIndex(),3,'0') + "-" + Library.fillLeft(""+j,3,'0');
                    PrintStream ps = new PrintStream(_prova.getPath().getCanonicalPath() + "/" + baseName + ".tex", "utf-8");
                    params.setTiposDeProva(new int[] {j,j});
                    new Prova2TeX(_provaStructure, ps, params, workdir);
                    ps.close();

                    
                    // _provaStructure.setProperty();
                    // @todo windows and linux
                    // windows
//                  // _provaStructure.setProperty();
//                  status = Library.executeCommand("pdflatex -halt-on-error" +
//                  " -include-directory=" + workdir +
//                  " -output-directory=" + params.getOutputDir() +
//                  " -aux-directory=" + workdir +
//                  " " + workdir + baseName + ".tex", true);

//                    // linux
//                    String command = App.getConfiguracao().getCommandCompileTEX2PDF(workdir, params.getOutputDir(), "\"" + workdir + baseName + ".tex\"");
//                    status = Library.executeCommand(command,"TEXINPUTS="+workdir.replace("\\","/").replace(" ","\\ "),true);
////                    status = Library.executeCommand(
////                    		"pdflatex -halt-on-error -output-directory=" + params.getOutputDir() +" "+ workdir.replace(" ","\\ ")+baseName + ".tex",
////                    		workdir.replace(" ", "\\ "),
////                    		true);                    
//
//
//                    if (status != 0) {
//                    	JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
//                    	return;
//                    }
                    
                    
                    String command = App.getConfiguracao().getCommandCompileTEX2PDF(workdir, params.getOutputDir(), "\"" + workdir + baseName + ".tex\"");
                    String dir = workdir.replace("\\","/").replace(" ","\\ ");
                    int status = Library.executeCommand(command,dir,null,true);
                    if (status != 0) {
                        JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
                        return;
                    }            
                }
            }
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Gerados "+count+" arquivos na pasta "+this._tfOutputDir.getText());
        }
    }
}



