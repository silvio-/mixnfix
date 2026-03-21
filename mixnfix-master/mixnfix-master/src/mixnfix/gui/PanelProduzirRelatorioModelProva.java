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
import mixnfix.reports.ModelProvaReport;


class PanelProduzirRelatorioModelProva extends JPanel {
    ProvaStructure _prova;

    Input _tfInputFileName;

    Input _tfTamanhoFonte;

    Input _tfPaperHeight;
    Input _tfPaperWidth;

    Input _tfTopMargin;
    Input _tfLeftMargin;
    Input _tfBottomMargin;
    Input _tfRightMargin;

    ModelProva _model;

    public PanelProduzirRelatorioModelProva(ModelProva model) {
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
                searchVideo();
            }
        });

        this.add(new JLabel("Arquivo de Saída:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfInputFileName = new Input(App.getConfiguracao(),"reportpdf","c:/"+_model.getProva().getNome()+".pdf",Input.TF_FILE,250);
        this.add(_tfInputFileName,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
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

    private void produzir() throws Exception {

        int i=1;
        for (ModelProvaCorrecao modelProvaCorrecao: _model.getProvasCorrecoes()) {
            for (ModelEntradaProvaCorrecao m : modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas()) {
                ModelProvaCorrecaoReport.gerarImagem(m, Controller.TMP_DIR + "/img" + (i++) + ".jpg");
            }
        }

        File outputFile = this._tfInputFileName.getFile();

        // String baseName = "relatorio"+_model.getProva().getId();
        String baseName = _model.getProva().getNome();
        String baseDir = outputFile.getParentFile().getAbsolutePath().replace('\\','/');


        PrintWriter pw = new PrintWriter(Controller.TMP_DIR+baseName+".tex","utf-8");
        pw.println(ModelProvaReport.getRelatorioPDFLaTeX(_model));
        pw.close();

        ModelProvaReport.gerarRelatoriosTXT(_model,"./");

        
        // compile TEX to PDF
        String command = App.getConfiguracao().getCommandCompileTEX2PDF(Controller.TMP_DIR, baseDir, "\"" + Controller.TMP_DIR +"/"+ baseName + ".tex\"");
        String dir = Controller.TMP_DIR.replace("\\","/").replace(" ","\\ ");
        int status = Library.executeCommand(command,dir,null,true);
        if (status != 0) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
            return;
        }            

        // open PDF
        command = App.getConfiguracao().getCommandOpenPDF(baseDir +"/" +baseName + ".pdf");
        Library.executeCommand(command, false);        
//        
//        // _prova.setProperty();
//        int status = Library.executeCommand("pdflatex -halt-on-error" +
//                                        " -include-directory=" + Controller.TMP_DIR +
//                                        " -output-directory=" + baseDir +
//                                        " -aux-directory=" + Controller.TMP_DIR +
//                                        " " + Controller.TMP_DIR + baseName + ".tex", true);
//
//        if (status != 0) {
//            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
//            return;
//        }
//
//        status = Library.executeCommand("pdflatex -halt-on-error" +
//                                        " -include-directory=" + Controller.TMP_DIR +
//                                        " -output-directory=" + baseDir +
//                                        " -aux-directory=" + Controller.TMP_DIR +
//                                        " " + Controller.TMP_DIR + baseName + ".tex", true);
//
//        if (status != 0) {
//            JOptionPane.showMessageDialog(this.getTopLevelAncestor(), "Problema no arquivo .tex ao rodar o comando pdflatex");
//            return;
//        }
//
//        String st = mixnfix.gui.App.getProperty("acrobat");
//        if (st == null || "".equals(st)) {
//            st = "C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe";
//            mixnfix.gui.App.setProperty("acrobat", st);
//        }
//        Library.executeCommand("\"" + st + "\" " + baseDir+baseName+".pdf", false);

        this.getTopLevelAncestor().setVisible(false); // close window
    }


    public void searchVideo() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(this._tfInputFileName.getText()));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return f.getName().endsWith(".txt");
            }

            public String getDescription() {
                return "Texto (.txt)";
            }
        });
        int result = jfc.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            this._tfInputFileName.setTextAndSave(jfc.getSelectedFile().getAbsolutePath());
        }
    }

}
