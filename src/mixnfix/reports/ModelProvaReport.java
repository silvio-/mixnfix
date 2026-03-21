package mixnfix.reports;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import mixnfix.gui.ModelProva;
import mixnfix.gui.ModelProvaCorrecao;

public class ModelProvaReport {

    public ModelProvaReport() {
    }


    public static void gerarRelatoriosTXT(ModelProva modelProva, String dirName) throws Exception {
        String provaName = modelProva.getProva().getNome();
        List<ModelProvaCorrecao> modelProvasCorrecoes = modelProva.getProvasCorrecoes();
        for (ModelProvaCorrecao modelProvaCorrecao: modelProvasCorrecoes) {
            String filename = dirName + provaName + "_" + modelProvaCorrecao.getProvaCorrecao().getNome() + ".txt";
            System.out.println("Gerando relatório " + filename + "...");

            PrintWriter file = new PrintWriter(filename,"utf-8");
            file.println(ModelProvaCorrecaoReport.getRelatorioTXT(modelProvaCorrecao));
            file.close();

        }
    }

    private static String format(double value, int decimalPlaces) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMinimumFractionDigits(decimalPlaces);
        df.setMaximumFractionDigits(decimalPlaces);
        df.getDecimalFormatSymbols().setDecimalSeparator(',');
        return df.format(value);
    }

    private static String fillRight(String s, int size, char ch) {
    String result = s;
    for (int i=0;i<size-s.length();i++) {
        result+=ch;
    }
    if (result.length() > size)
        result = result.substring(0,size);
    return result;
}

private static String fillLeft(String s, int size, char ch) {
    String result = "";
    for (int i=0;i<size-s.length();i++) {
        result+=ch;
    }
    result += s;
    if (result.length() > size)
        result = result.substring(0,size);
    return result;
}




    private static void addLabelCellInWorksheet(
        jxl.write.WritableSheet sheet,
        String value,
        int column,
        int row,
        jxl.format.Colour colour,
        jxl.format.Border border,
        jxl.format.BorderLineStyle borderLineStyle,
        jxl.format.Alignment alignment,
        WritableFont font
    ) {
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            // format.setBackground(colour);
            format.setBorder(border, borderLineStyle);
            format.setAlignment(alignment);

            jxl.write.Label label = new jxl.write.Label(row, column, value, format);
            sheet.addCell(label);
        }
        catch (WriteException we) {
            we.printStackTrace();
        }
    }

    private static void addIntegerCellInWorksheet(
        jxl.write.WritableSheet sheet,
        double value,
        int column,
        int row,
        jxl.format.Colour colour,
        jxl.format.Border border,
        jxl.format.BorderLineStyle borderLineStyle,
        jxl.format.Alignment alignment,
        WritableFont font
    ) {

        WritableCellFormat format = new WritableCellFormat(font, NumberFormats.INTEGER);
        try {
            // format.setBackground(colour);
            format.setBorder(border, borderLineStyle);
            format.setAlignment(alignment);

            jxl.write.Number number = new jxl.write.Number(row, column, value, format);
            sheet.addCell(number);
        }
        catch (WriteException we) {
            we.printStackTrace();
        }
    }

    private static void addDoubleCellInWorksheet(
        jxl.write.WritableSheet sheet,
        double value,
        int column,
        int row,
        jxl.format.Colour colour,
        jxl.format.Border border,
        jxl.format.BorderLineStyle borderLineStyle,
        jxl.format.Alignment alignment,
        WritableFont font
    ) {

        WritableCellFormat format = new WritableCellFormat(font, NumberFormats.FLOAT);
        try {
            // format.setBackground(colour);
            format.setBorder(border, borderLineStyle);
            format.setAlignment(alignment);

            jxl.write.Number number = new jxl.write.Number(row, column, value, format);
            sheet.addCell(number);
        }
        catch (WriteException we) {
            we.printStackTrace();
        }
    }


    public static String getRelatorioPDFLaTeX(ModelProva modelProva) throws Exception {
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

        report.append(getTrechoRelatorioPDFLaTeX(modelProva,date));

        report.append("\\end{document}\n");

        return report.toString();
    }


    public static String getTrechoRelatorioPDFLaTeX(ModelProva modelProva, Date date) throws Exception {
        StringBuffer report = new StringBuffer();

        report.append("\\begin{tabbing}{lll}\n");
        // report.append("xxxxxxxxxxx" + " \\= " + "xxx" + " \\= " + "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"+ "\\kill\n");
        report.append("\\hspace*{4cm}" + " \\= " + "\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}"+ "\\kill\n");
        report.append("{\\bf \\Large Instituicao} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProva.getProva().getInstituicao_Prova().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Prova} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  modelProva.getProva().getNome() + "} \\\\\n");
        report.append("{\\bf \\Large Data Impressao} \\> {\\bf \\Large :} \\> {\\bf \\Large " +  date.toString() + "} \\\\\n");
        report.append("\\end{tabbing}\n");


        String correcoesLink = "correcao" + modelProva.getProva().getNome();

        report.append("{\\bf \\Large Correcoes} \\label{" + correcoesLink + "}\n");

        // report.append(modelProvaCorrecao.getProva().getNome()+"\n");

        report.append("\\begin{scriptsize}\n");
        report.append("\\begin{tabbing}{lll}\n");

        List<ModelProvaCorrecao> correcoes = modelProva.getProvasCorrecoes();

        report.append("\\hspace*{0.5cm}" + " \\= " + "\\hspace*{9cm}"+ "\\kill\n");

        int index = 0;
        for (ModelProvaCorrecao modelProvaCorrecao: correcoes) {
            String correcaoLink = "listao" + modelProvaCorrecao.getProvaCorrecao().getNome();
                report.append(++index + "\\>" + "\\hyperref["+correcaoLink+"]{"+ modelProvaCorrecao.getProvaCorrecao().getNome() + "}\\\\\n");
        }

        report.append("\\end{tabbing}\n");
        report.append("\\end{scriptsize}\n");
        report.append("\n");
        report.append("\\eject \\clearpage\n");
        report.append("\n");

        int i=1;
        int indexCorrente = 0;
        for (ModelProvaCorrecao modelProvaCorrecao: correcoes) {
            report.append(ModelProvaCorrecaoReport.getTrechoRelatorioPDFLaTeX(modelProvaCorrecao,date,indexCorrente));
            indexCorrente += modelProvaCorrecao.getNumAlunosComEntrada();
        }

        return report.toString();
    }


    public static String getRelatorioEstatistaPorQuestao(ModelProva modelProva) throws Exception {
        StringBuffer report = new StringBuffer();
        //LATEX HEADERS
        report.append("\\documentclass[a4paper,12pt]{article}\n");
        report.append("\\usepackage[portugese]{babel}\n");
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

        int index = 0;
        List<ModelProvaCorrecao> modelProvasCorrecoes = modelProva.getProvasCorrecoes();
        for (ModelProvaCorrecao modelProvaCorrecao: modelProvasCorrecoes) {
            String correcaoLink = "listao" + modelProvaCorrecao.getProvaCorrecao().getNome();
                report.append(ModelProvaCorrecaoReport.getTrechoRelatorioEstatisticaPorQuestao(modelProvaCorrecao,date,index));
        }

        report.append("\\end{document}\n");

        return report.toString();
    }





}
