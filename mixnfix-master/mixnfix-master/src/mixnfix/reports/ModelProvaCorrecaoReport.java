package mixnfix.reports;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.imageio.ImageIO;

import mixnfix.gui.CorrecaoUI;
import mixnfix.gui.ModelEntradaProvaCorrecao;
import mixnfix.gui.ModelProva;
import mixnfix.gui.ModelProvaCorrecao;
import mixnfix.modelo.Aluno;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.NoProva;
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
public class ModelProvaCorrecaoReport {
    public ModelProvaCorrecaoReport() {
    }

    public static String getRelatorioPDFLaTeX2(ModelProvaCorrecao modelProvaCorrecao) throws Exception {
        StringBuffer report = new StringBuffer();
      //LATEX HEADERS
        report.append("\\documentclass[a4paper,12pt]{article}\n");
        report.append("\\usepackage[portuges]{babel}\n");
        report.append("\\usepackage[utf8]{inputenc}\n");
        report.append("\\usepackage{amsfonts,amssymb}\n");
        report.append("\\usepackage[dvips,pdftex]{color}\n");
        report.append("\\usepackage{multicol}\n");
        report.append("\\usepackage[colorlinks=true, pdfstartview=FitV, linkcolor=blue,\n");
        report.append("            citecolor=blue, urlcolor=blue]{hyperref}\n");
        report.append("\\usepackage[pdftex]{graphicx}\n");
        report.append("\n");
        report.append("\n");
        report.append("\\usepackage[top=1.50cm,bottom=1.50cm,left=1.50cm,right=1.50cm,papersize={21.00cm,29.70cm},includehead=true]{geometry}\n");
        report.append("\\setlength{\\parindent}{0cm} \\setlength{\\parskip}{0.2cm}\n");
        report.append("\\setlength{\\columnsep}{0.6cm} \\setlength{\\columnseprule}{0pt}\n");
        report.append("\\renewcommand{\\familydefault}{cmss}\n");
        report.append("\\pagestyle{empty}\n");
        report.append("\\begin{document}\n");
        report.append("\n");

        Date date = new Date(System.currentTimeMillis());


        report.append("\\begin{tabbing}{lll}\n");
        // report.append("xxxxxxxxxxx" + " \\= " + "xxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+ "\\kill\n");
        report.append("\\hspace*{4cm}" + " \\= " + "\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}"+ "\\kill\n");
        //report.append("{\\bf \\Large Instituição} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getInstituicao_Prova().getNome() + "} \\\\\n");
        //report.append("{\\bf \\Large Prova} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getNome() + "} \\\\\n");
        //report.append("{\\bf \\Large Correção} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProvaCorrecao().getNome() + "} \\\\\n");
        //report.append("{\\bf \\Large Data Impressão} \\> {\\bf \\Large :} \\> {\\bf \\Large " + date.toString() + "} \\\\\n");
        report.append("{\\bf Instituição} \\> {\\bf :} \\> {\\bf " +  modelProvaCorrecao.getProva().getInstituicao_Prova().getNome() + "} \\\\\n");
        report.append("{\\bf Prova} \\> {\\bf :} \\> {\\bf " +  modelProvaCorrecao.getProva().getNome() + "} \\\\\n");
        report.append("{\\bf Correção} \\> {\\bf :} \\> {\\bf " +  modelProvaCorrecao.getProvaCorrecao().getNome() + "} \\\\\n");
        report.append("{\\bf Data Impressão} \\> {\\bf :} \\> {\\bf " + date.toString() + "} \\\\\n");
        report.append("\\end{tabbing}\n");


        report.append("{\\bf \\Large Listão} \\label{listao}\n");

        report.append(modelProvaCorrecao.getProva().getNome()+"\n");

        report.append("\\begin{scriptsize}\n");
        report.append("\\begin{tabbing}{lll}\n");

        ArrayList<ModelEntradaProvaCorrecao> entradasProvaCorrecao = modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas();

        report.append("xxxxxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx " + " \\= " + "xxxxxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxxxxxxxx" + "\\kill\n");
        int index = 0;
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();

                report.append(++index + "\\>" + "\\hyperref[aluno:"+aluno.getMatricula()+"]{"+ aluno.getNome() + "} \\> " +
                              modelEntradaProvaCorrecao.getProvaStructure().getTipoProva() + " \\> " +
                              aluno.getMatricula() + " \\> " + String.format("%2.2f",modelEntradaProvaCorrecao.getProvaStructure().getNota()) + "\\\\\n");
        }

        report.append("\\end{tabbing}\n");
        report.append("\\end{scriptsize}\n");
        report.append("\n");
        report.append("\\eject \\clearpage\n");
        report.append("\n");

        int i=1;
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();
            StringBuffer detalhamento = new StringBuffer();
            modelEntradaProvaCorrecao.getCorrecaoProva().getModelProva().getProvaStructure().detalharNota(detalhamento);

            report.append("\\section{" + aluno.getNome() + "} ");
            report.append("\\label{aluno:" + aluno.getMatricula() + "}\n");
            report.append("\n");
            report.append("\\subsection*{Nota}\n");
            report.append(String.format("%3.2f",modelEntradaProvaCorrecao.getProvaStructure().getNota()) + "\n");
            report.append("\n");
            report.append("\\subsection*{Detalhamento}");
            report.append("\\begin{verbatim}\n");
            report.append(detalhamento.toString());
            report.append("\n");
            report.append("\\end{verbatim}\n");
            report.append("\n");

            report.append("\\subsection*{Imagem}");
            report.append("\\centerline{\\includegraphics[width=16cm]{img"+i+".jpg}}\n"); //
            i++;

            report.append("\n");
            report.append("\\vfill\n");
            report.append("\n");
            report.append("\\hfill \\hyperref[listao]{Listão}\n");
            report.append("\n");
            report.append("\\clearpage\n");
            report.append("\n");
        }


        report.append("\\end{document}\n");


        return report.toString();
    }


    public static String getRelatorioPDFLaTeX(ModelProvaCorrecao modelProvaCorrecao) throws Exception {
        StringBuffer report = new StringBuffer();
      //LATEX HEADERS
        report.append("\\documentclass[a4paper,12pt]{article}\n");
        report.append("\\usepackage[portuges]{babel}\n");
        report.append("\\usepackage[utf8]{inputenc}\n");
        report.append("\\usepackage{amsfonts,amssymb}\n");
        report.append("\\usepackage[dvips,pdftex]{color}\n");
        report.append("\\usepackage{multicol}\n");
        report.append("\\usepackage[colorlinks=true, pdfstartview=FitV, linkcolor=blue,\n");
        report.append("            citecolor=blue, urlcolor=blue]{hyperref}\n");
        report.append("\\usepackage[pdftex]{graphicx}\n");
        report.append("\n");
        report.append("\n");
        report.append("\\usepackage[top=1.50cm,bottom=1.50cm,left=1.50cm,right=1.50cm,papersize={21.00cm,29.70cm},includehead=true]{geometry}\n");
        report.append("\\setlength{\\parindent}{0cm} \\setlength{\\parskip}{0.2cm}\n");
        report.append("\\setlength{\\columnsep}{0.6cm} \\setlength{\\columnseprule}{0pt}\n");
        report.append("\\renewcommand{\\familydefault}{cmss}\n");
        report.append("\\pagestyle{empty}\n");
        report.append("\\begin{document}\n");
        report.append("\n");

        Date date = new Date(System.currentTimeMillis());


        report.append(getTrechoRelatorioPDFLaTeX(modelProvaCorrecao,date,0));

        report.append("\\end{document}\n");

        return report.toString();
    }


    public static String getTrechoRelatorioPDFLaTeX(ModelProvaCorrecao modelProvaCorrecao, Date date, int indexInicial) throws Exception {
        StringBuffer report = new StringBuffer();

        report.append("\\begin{tabbing}{lll}\n");
        // report.append("xxxxxxxxxxx" + " \\= " + "xxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+ "\\kill\n");
        report.append("\\hspace*{4cm}" + " \\= " + "\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}"+ "\\kill\n");
        report.append("{\\bf \\Large Instituição} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getInstituicao_Prova().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Prova} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Correção} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProvaCorrecao().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Data Impressão} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  date.toString() + "} \\\\\n");
        report.append("\\end{tabbing}\n");


        String correcaoLink = "listao" + modelProvaCorrecao.getProvaCorrecao().getNome();

        report.append("{\\section*{Listão} \\label{" + correcaoLink + "}\n");

        report.append("\\begin{scriptsize}\n");
        report.append("\\begin{tabbing}{lll}\n");

        ArrayList<ModelEntradaProvaCorrecao> entradasProvaCorrecao = modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas();

//        report.append("xxxxxx" + " \\= " + "xx " + " \\= " + "xxxxxx" + " \\= " + "xxx" + " \\= " + "xxx" + "\\kill\n");
        report.append("\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}" + " \\= " + "\\hspace*{1cm}"+ " \\= " + "\\hspace*{3cm}" + " \\= " + "\\hspace*{2cm}"+ "\\kill\n");
        int index = 0;
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();

            String alunoCorrecaoLink = "aluno:"+aluno.getMatricula()+"-"+correcaoLink;
            report.append(++index + "\\>" + "\\hyperref["+alunoCorrecaoLink+"]{"+ aluno.getNome() + "} \\> " +
                          modelEntradaProvaCorrecao.getProvaStructure().getTipoProva() + " \\> " +
                          aluno.getMatricula() + " \\> " + String.format("%2.2f",modelEntradaProvaCorrecao.getProvaStructure().getNota()) + "\\\\\n");
        }

        report.append("\\end{tabbing}\n");
        report.append("\\end{scriptsize}\n");
        report.append("\n");
//        report.append("\\eject \\clearpage\n");
        report.append("\n");


        report.append("{\\section*{Alunos sem Correção}}\n");

        report.append("\\begin{scriptsize}\n");
        report.append("\\begin{tabbing}{lll}\n");

        ArrayList<Aluno> alunos = modelProvaCorrecao.getAlunosSemEntradas();

        report.append("\\hspace*{0.5cm}" + " \\= " + "\\hspace*{2cm}" + " \\= " + "\\hspace*{9cm}" + "\\kill\n");
        int index1 = 0;
        for (Aluno aluno : alunos) {
            report.append(++index1 + "\\>" + "{" + aluno.getMatricula() + "} \\> " +
                          "{" + aluno.getNome() + "} \\\\\n");
        }

        report.append("\\end{tabbing}\n");
        report.append("\\end{scriptsize}\n");
        report.append("\n");

        report.append("\\eject \\clearpage\n");
        report.append("\n");


        int i=1;
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();
            StringBuffer detalhamento = new StringBuffer();
            modelEntradaProvaCorrecao.getProvaStructure().detalharNota(detalhamento);

            String alunoCorrecaoLink = "aluno:"+aluno.getMatricula()+"-"+correcaoLink;
            report.append("\\section{" + aluno.getNome() + "} ");
            report.append("\\label{"+alunoCorrecaoLink+"}\n");
            report.append("\n");
            report.append("\\subsection*{Nota}\n");
            report.append(String.format("%3.2f",modelEntradaProvaCorrecao.getProvaStructure().getNota()) + "\n");
            report.append("\n");
            report.append("\\subsection*{Detalhamento}");
            report.append("\\begin{verbatim}\n");
            report.append(detalhamento.toString());
            report.append("\n");
            report.append("\\end{verbatim}\n");
            report.append("\n");

            report.append("\\subsection*{Imagem}");
            report.append("\\centerline{\\includegraphics[width=16cm]{img"+(indexInicial+i)+".jpg}}\n"); //
            i++;

            report.append("\n");
            report.append("\\vfill\n");
            report.append("\n");
            report.append("\\hfill \\hyperref[" + correcaoLink + "]{Correção " + modelProvaCorrecao.getProvaCorrecao().getNome() + "}\n");
            // report.append("\\hfill \\hyperref[" + correcaoLink + "]{Início}\n");
            report.append("\n");
            report.append("\\clearpage\n");
            report.append("\n");
        }

        return report.toString();
    }



    public static void gerarImagem(ModelEntradaProvaCorrecao model, String outFileName) throws IOException, SQLException {
        CorrecaoUI c = new CorrecaoUI(model);
        CorrecaoUI.PanelVisualizacaoProva pvp = c.getPanelVisualizacaoProva();
        BufferedImage image = pvp.newImage();
        ImageIO.write(image,"jpg",new File(outFileName));
    }


    public static String getRelatorioTXT(ModelProvaCorrecao modelProvaCorrecao) throws Exception {
        StringBuffer report = new StringBuffer();

        ArrayList<ModelEntradaProvaCorrecao> entradasProvaCorrecao = modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas();
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao: entradasProvaCorrecao) {
            Aluno aluno = modelEntradaProvaCorrecao.getAluno();

            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();
            modelEntradaProvaCorrecao.getProvaStructure().avaliarNota();

            float nota = modelEntradaProvaCorrecao.getProvaStructure().getNota();
            report.append(String.format(Locale.ITALIAN,"%-10s%-10.2f\n", aluno.getMatricula(),nota));
        }

        return report.toString();
    }


    public static String getRelatorioEstatistaPorQuestao(ModelProvaCorrecao modelProvaCorrecao) throws Exception {
        StringBuffer report = new StringBuffer();
        //LATEX HEADERS
        report.append("\\documentclass[a4paper,12pt]{article}\n");
        report.append("\\usepackage[portuges]{babel}\n");
        report.append("\\usepackage[utf8]{inputenc}\n");
        report.append("\\usepackage{amsfonts,amssymb}\n");
        report.append("\\usepackage[dvips,pdftex]{color}\n");
        report.append("\\usepackage{multicol}\n");
        report.append("\\usepackage[colorlinks=true, pdfstartview=FitV, linkcolor=blue,\n");
        report.append("            citecolor=blue, urlcolor=blue]{hyperref}\n");
        report.append("\\usepackage[pdftex]{graphicx}\n");
        report.append("\n");
        report.append("\n");
        report.append("\\usepackage[top=1.50cm,bottom=1.50cm,left=1.50cm,right=1.50cm,papersize={21.00cm,29.70cm},includehead=true]{geometry}\n");
        report.append("\\setlength{\\parindent}{0cm} \\setlength{\\parskip}{0.2cm}\n");
        report.append("\\setlength{\\columnsep}{0.6cm} \\setlength{\\columnseprule}{0pt}\n");
        report.append("\\renewcommand{\\familydefault}{cmss}\n");
        report.append("\\pagestyle{empty}\n");
        report.append("\\begin{document}\n");
        report.append("\n");

        Date date = new Date(System.currentTimeMillis());


        report.append(getTrechoRelatorioEstatisticaPorQuestao(modelProvaCorrecao,date,0));

        report.append("\\end{document}\n");

        return report.toString();
    }


    public static String getTrechoRelatorioEstatisticaPorQuestao(ModelProvaCorrecao modelProvaCorrecao, Date date, int indexInicial) throws Exception {


        // Fazer a contabilidade
        HashMap<NoProva,ContabilidadeNoProvaPermutavel> mapa = new HashMap<NoProva,ContabilidadeNoProvaPermutavel>();

        ModelProva modelProva = modelProvaCorrecao.getModelProva();
        ArrayList<ModelEntradaProvaCorrecao> entradasProvaCorrecao = modelProvaCorrecao.getEntradasProvaCorrecaoOrdenadas();
        for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao : entradasProvaCorrecao) {
            modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();

            ArrayList<Quesito> quesitos = modelProva.getProvaStructure().getQuesitos();

            for (Quesito quesito : quesitos) {
                int indexQ = quesitos.indexOf(quesito);

                if ( (quesito.getTipo() == Quesito.TIPO_ALTERNATIVAS) ||
                     (quesito.getTipo() == Quesito.TIPO_NUMERICO_99)) {

                   ContabilidadeNoProvaPermutavel c = mapa.get(quesito);
                   if (c == null) {
                       c = new ContabilidadeNoProvaPermutavel(quesito,""+(indexQ+1));
                       mapa.put(quesito,c);
                   }

                   c.incNumeroTotal();

                    switch (quesito.avaliarCorretude()) {
                        case Quesito.BLANK:
                            c.incNumeroBrancos();
                            break;
                        case Quesito.CORRECT:
                            c.incNumeroAcertos();
                            break;
                        case Quesito.WRONG:
                            c.incNumeroErros();
                            break;
                    }
                }
                else if (quesito.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                    ArrayList<NoProva> itens = quesito.getChildsNoClone();
                    for (Object item : itens) {
                        ItemQuesito itemQuesito = (ItemQuesito) item;

                        int indexI = itens.indexOf(item);

                        ContabilidadeNoProvaPermutavel c = mapa.get(itemQuesito);
                        if (c == null) {
                            c = new ContabilidadeNoProvaPermutavel(itemQuesito,""+(indexQ+1)+"."+(indexI+1));
                            mapa.put(itemQuesito,c);
                        }

                        c.incNumeroTotal();

                        switch (itemQuesito.avaliarItemFalsoVerdadeiro()) {
                            case ItemQuesito.BLANK:
                                c.incNumeroBrancos();
                                break;
                            case ItemQuesito.CORRECT:
                                c.incNumeroAcertos();
                                break;
                            case ItemQuesito.WRONG:
                                c.incNumeroErros();
                                break;
                        }
                    }
                }
            }
        }




        StringBuffer report = new StringBuffer();

        report.append("\\begin{tabbing}{lll}\n");
        report.append("\\hspace*{4cm}" + " \\= " + "\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}"+ "\\kill\n");
        report.append("{\\bf \\Large Instituição} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getInstituicao_Prova().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Prova} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProva().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Correção} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProvaCorrecao.getProvaCorrecao().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Data Impressão} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  date.toString() + "} \\\\\n");
        report.append("\\end{tabbing}\n");


        String correcaoLink = "estatistica" + modelProvaCorrecao.getProvaCorrecao().getNome();

        report.append("{\\section*{Estatísticas} \\label{" + correcaoLink + "}\n");

        report.append("\\begin{center}\n");
        report.append("\\begin{tabbing}\n");
        report.append("\\hspace*{2cm}" + " \\= " + "\\hspace*{2cm}" + " \\= " + "\\hspace*{2cm}"+ " \\= " + "\\hspace*{2cm}" +
                      "\\= " + "\\hspace*{2cm}"+  "\\= " + "\\hspace*{2cm}" + "\\= " + "\\hspace*{2cm}" + "\\kill\n");
        report.append("Questão" + "\\>" + "Total" + "\\>" + "Acertos" + "\\>" + "Nulos" + "\\>" + "Erros" + "\\>" + "PAcerto" + "\\>" + "PErro" +"\\\\\n");


        ArrayList<Quesito> quesitos = modelProva.getProvaStructure().getQuesitos();
        for (Quesito quesito: quesitos) {
            if ( (quesito.getTipo() == Quesito.TIPO_ALTERNATIVAS) ||
                 (quesito.getTipo() == Quesito.TIPO_NUMERICO_99)) {

                ContabilidadeNoProvaPermutavel c = mapa.get(quesito);
                if (c != null) {
                    report.append(c.getIndice() + "\\>" + c.getNumeroTotal() + "\\>" + c.getNumeroAcertos() +
                                  "\\>" + c.getNumeroBrancos() + "\\>" + c.getNumeroErros() +
                                  "\\>" + c.getPercentualAcerto() +
                                  "\\>" + c.getPercentualErro() + "\\\\\n");
                }
            }
            else if (quesito.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                ArrayList<NoProva> itens = quesito.getChildsNoClone();
                for (Object item : itens) {
                    ItemQuesito itemQuesito = (ItemQuesito) item;

                    ContabilidadeNoProvaPermutavel c = mapa.get(itemQuesito);
                    report.append(c.getIndice() + "\\>" + c.getNumeroTotal() + "\\>" + c.getNumeroAcertos() +
                                  "\\>" + c.getNumeroBrancos() + "\\>" + c.getNumeroErros() +
                                  "\\>" + c.getPercentualAcerto() +
                                  "\\>" + c.getPercentualErro() + "\\\\\n");
                }
            }
        }

        report.append("\\end{tabbing}\n");
        report.append("\\end{center}\n");
        report.append("\n");
        report.append("\\eject \\clearpage\n");
        report.append("\n");

        return report.toString();
    }

}

class ContabilidadeNoProvaPermutavel{
    private NoProva _noProva;
    private String _indice;
    private int _numeroTotal   = 0;
    private int _numeroAcertos = 0;
    private int _numeroErros   = 0;
    private int _numeroBrancos = 0;

    public ContabilidadeNoProvaPermutavel(NoProva noProva, String indice) {
        _noProva = noProva;
        _indice = indice;
        _numeroTotal   = 0;
        _numeroAcertos = 0;
        _numeroErros   = 0;
        _numeroBrancos = 0;
    }

    public String getIndice() {
        return _indice;
    }

    public int getNumeroTotal() {
        return _numeroTotal;
    }

    public int getNumeroAcertos() {
        return _numeroAcertos;
    }

    public int getNumeroErros() {
        return _numeroErros;
    }

    public int getNumeroBrancos() {
        return _numeroBrancos;
    }

    public void incNumeroTotal() {
        _numeroTotal++;
    }

    public void incNumeroAcertos() {
        _numeroAcertos++;
    }

    public void incNumeroErros() {
        _numeroErros++;
    }

    public void incNumeroBrancos() {
        _numeroBrancos++;
    }

    public int getPercentualAcerto() {
        return ((int) Math.round(100.0*_numeroAcertos/_numeroTotal));
    }

    public int getPercentualErro() {
        return ((int) Math.round(100.0*_numeroErros/_numeroTotal));
    }
}
