package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import linsoft.gui.Input;
import linsoft.gui.InputBoolean;
import mixnfix.Library;
import mixnfix.composicaoprova.ParametrosDeLayoutDaProva;
import mixnfix.composicaoprova.Questionario2TeX;
import mixnfix.prova.ProvaStructure;

class PanelProduzirQuestionario extends JPanel {
    ModelProva _prova;
    ProvaStructure _provaStructure;

    Input _tfTamanhoFonte;

    Input _tfPaperHeight;
    Input _tfPaperWidth;

    Input _tfTopMargin;
    Input _tfLeftMargin;
    Input _tfBottomMargin;
    Input _tfRightMargin;

    Input _tfNumColumns;

    Input _tfOutputDir;

    Input _ibNumeroDePaginasEmBrancoNoFim;

    InputBoolean _ibFolhaEmBrancoAposFR;

    Input _ibNumFolhasRespostas;

    InputBoolean _ibGoodbreak;

    JButton _btnProduzir;

    public PanelProduzirQuestionario(ModelProva prova) {
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

        this.add(new JLabel("Num.Folhas Resposta:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._ibNumFolhasRespostas = new Input(App.getConfiguracao(),"numFolhasRespostas","1",Input.TF_INTEIRO,50);
        this.add(_ibNumFolhasRespostas,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this._ibFolhaEmBrancoAposFR = new InputBoolean(App.getConfiguracao(),"folhaEmBrancoAposFR",true,"Folha em branco após folha resposta");
        this.add(_ibFolhaEmBrancoAposFR,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
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
        new int[] { 0 } ,
        this._tfOutputDir.getText(),
        this._ibFolhaEmBrancoAposFR.isSelected(),  // pg em branco
        false,
        null, // multifiles
        this._ibNumeroDePaginasEmBrancoNoFim.getInt(),
        false,
        false,
        this._ibGoodbreak.isSelected(),
        this._ibNumFolhasRespostas.getInt());  // pg em branco

        String workdir = _prova.getPath().getAbsolutePath() + "/";

        String baseName = "questionario" + _provaStructure.getIndex();

        // -----------------------------------------------------------------
        // Verificar se arquivo já está aberto
        if (Library.isLocked(new File(params.getOutputDir() + "/" + baseName + ".pdf"))) {
            JOptionPane.showMessageDialog(this, "Arquivo está travado por outro programa!");
            return;
        }
        // Verificar se arquivo já está aberto
        // -----------------------------------------------------------------

        PrintStream ps = new PrintStream(new FileOutputStream(workdir + baseName + ".tex"));
        new Questionario2TeX(_provaStructure, ps, params, workdir);
        ps.close();
        int status;

        // _provaStructure.setProperty();
        status = Library.executeCommand("pdflatex -halt-on-error" +
                                        " -include-directory=" + workdir +
                                        " -output-directory=" + params.getOutputDir() +
                                        " -aux-directory=" + workdir +
                                        " \"" + workdir + baseName + ".tex\" ", true);

        if (status != 0) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
            return;
        }

        String st = mixnfix.gui.App.getProperty("acrobat");
        if (st == null || "".equals(st)) {
            st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
            mixnfix.gui.App.setProperty("acrobat", st);
        }

        Library.executeCommand("\"" + st + "\" " + params.getOutputDir() + baseName + ".pdf", false);
    }
}



