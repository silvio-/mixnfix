package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import linsoft.gui.Input;
import mixnfix.Controller;
import mixnfix.Library;
import mixnfix.prova.ProvaStructure;
import mixnfix.reports.ModelProvaCorrecaoReport;


class PanelProduzirRelatorio extends JPanel {
    ProvaStructure _prova;

    Input _tfInpuFileName;

    Input _tfTamanhoFonte;

    Input _tfPaperHeight;
    Input _tfPaperWidth;

    Input _tfTopMargin;
    Input _tfLeftMargin;
    Input _tfBottomMargin;
    Input _tfRightMargin;

    ModelProvaCorrecao _model;

    public PanelProduzirRelatorio(ModelProvaCorrecao model) {
        _model = model;

        JButton btnProduzir = new JButton("Produzir");
        btnProduzir.addActionListener(new ActionListener() {
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

        JButton btnVideo = new JButton("...");
        btnVideo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchFile();
            }
        });

        this.add(new JLabel("Nome de Arquivo:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfInpuFileName = new Input(App.getConfiguracao(),"reportpdf","c:/lista.pdf",Input.TF_FILE,250);
        this.add(_tfInpuFileName,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this.add(btnVideo,new GridBagConstraints(2,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
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

        this.add(btnProduzir,new GridBagConstraints(0,i,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(20,2,2,2),0,0));
    }

    /** name of the PDF file to be produced (the "Nome de Arquivo:" field). */
    void setArquivoDeSaida(String fileName) {
        this._tfInpuFileName.setTextAndSave(fileName);
    }

    private void produzir() throws Exception {
        File pdf = produzirPDF();
        if (pdf == null) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
            return;
        }

        abrirPDF(pdf);

        this.getTopLevelAncestor().setVisible(false); // close window
    }

    /**
     * Produces the PDF report of the correction on the file chosen by the user.
     * Returns the produced file, or null if pdflatex failed.
     */
    File produzirPDF() throws Exception {

        int i=1;
        for (ModelEntradaProvaCorrecao m: _model.getEntradasProvaCorrecaoOrdenadas()) {
            ModelProvaCorrecaoReport.gerarImagem(m,Controller.TMP_DIR+"img"+(i++)+".jpg");
        }

        File outputFile = this._tfInpuFileName.getFile();
        File outputDir = outputFile.getAbsoluteFile().getParentFile();
        if (outputDir == null)
            outputDir = new File(".").getAbsoluteFile();
        outputDir.mkdirs();

        String fileName = outputFile.getName();
        String baseName = fileName.toLowerCase().endsWith(".pdf")
                          ? fileName.substring(0,fileName.length()-4)
                          : fileName;
        if ("".equals(baseName))
            baseName = "relatorio"+_model.getProvaCorrecao().getId();

        File texFile = new File(Controller.TMP_DIR, baseName+".tex");

        // The LaTeX source is written in UTF-8 (the encoding of every string of
        // the application: names of students, of the exam, of the folder, the
        // text of the questions) and the preamble asks for the utf8 input
        // encoding accordingly. It used to be written with the platform default
        // encoding while declaring latin1, which made pdflatex stop with
        // "Package inputenc Error: Keyboard character used is undefined".
        PrintWriter pw = new PrintWriter(texFile, "UTF-8");
        pw.println(ModelProvaCorrecaoReport.getRelatorioPDFLaTeX(_model));
        pw.close();

        // The command is the one configured on the settings panel of the
        // application (ConfiguracaoMIXnFIX.compileTEX2PDF), as everywhere else.
        // It used to be a hard coded command line carrying -include-directory
        // and -aux-directory, two MiKTeX only options that TeX Live's pdflatex
        // rejects (and that indeed were nowhere to be seen on the settings
        // panel). pdflatex runs inside the temporary directory, where both the
        // .tex file and the img<n>.jpg pictures generated above live, and
        // writes its results into the directory chosen by the user.
        String command = App.getConfiguracao().getCommandCompileTEX2PDF(
            Controller.TMP_DIR, outputDir.getAbsolutePath(), texFile.getName());
        String argv[] = Library.splitCommandLine(command);
        String envp[] = { "TEXINPUTS=" + Controller.TMP_DIR + ":" };

        // two runs: the second one resolves the hyperref links of the "listão"
        for (int pass = 0; pass < 2; pass++) {
            int status = Library.executeCommand(argv, Controller.TMP_DIR, envp, true);
            if (status != 0)
                return null;
        }

        return new File(outputDir, baseName + ".pdf");
    }

    private void abrirPDF(File pdf) {
        try {
            // viewer configured on the settings panel (ConfiguracaoMIXnFIX.openPDF)
            String command = App.getConfiguracao().getCommandOpenPDF(pdf.getAbsolutePath());
            Library.executeCommand(Library.splitCommandLine(command), null, null, false);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void searchFile() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(this._tfInpuFileName.getText()));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().endsWith(".pdf");
            }

            public String getDescription() {
                return "PDF (.pdf)";
            }
        });
        int result = jfc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            this._tfInpuFileName.setTextAndSave(jfc.getSelectedFile().getAbsolutePath());
        }
    }

}
