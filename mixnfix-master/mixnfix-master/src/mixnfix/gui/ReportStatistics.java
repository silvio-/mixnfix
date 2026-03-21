package mixnfix.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mixnfix.prova.ItemQuesito;
import mixnfix.prova.Quesito;

/**
 * ReportStatistica
 */
public class ReportStatistics {
    ModelProva _modelProva;
    HashMap<ModelProvaCorrecao,ArrayList> _mapModelCorrecao2Statistics = new HashMap<ModelProvaCorrecao,ArrayList>();
    HashMap<ModelProvaCorrecao,NotasStatistics> _mapModelCorrecao2NotasStatistics = new HashMap<ModelProvaCorrecao,NotasStatistics> ();

    public ReportStatistics(ModelProva modelProva) throws SQLException, IOException {
        _modelProva = modelProva;

        for (ModelProvaCorrecao c: modelProva.getProvasCorrecoes()) {
            ArrayList qStats = new ArrayList();

            //
            NotasStatistics nStats = new NotasStatistics();
            _mapModelCorrecao2NotasStatistics.put(c,nStats);


            ArrayList<Quesito> quesitos = modelProva.getProvaStructure().getQuesitos();
            for (Quesito q : quesitos) {
                if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS) {
                    qStats.add(new QuesitoAlternativaStatistics(q));
                }
                else if (q.getTipo() == Quesito.TIPO_NUMERICO_99) {
                    qStats.add(new QuesitoNumericoStatistics(q));
                }
                else if (q.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                    qStats.add(new QuesitoFalsoVerdadeiroStatistics(q));
                }
                else {
                    qStats.add(null);
                }
            }
            _mapModelCorrecao2Statistics.put(c,qStats);

            // contabilizar informacao do aluno para cada entrada da correcao
            for (ModelEntradaProvaCorrecao modelEntradaProvaCorrecao : c.getEntradasProvaCorrecao()) {
                modelEntradaProvaCorrecao.preencherProvaComGabaritoCorrente();

                // add nota
                nStats.addNota(_modelProva.getProvaStructure().avaliarNota());

                for (Object o : qStats) {
                    if (o instanceof QuesitoAlternativaStatistics) {
                        QuesitoAlternativaStatistics qs = (QuesitoAlternativaStatistics) o;
                        List<Integer> r = qs.getQuesito().getRespostaAlunoNormalized();
                        if (r==null || r.size() == 0) {
                            qs.addBranco();
                        }
                        else qs.addAnswer(r.get(0));
                    }
                    else if (o instanceof QuesitoNumericoStatistics) {
                        QuesitoNumericoStatistics qs = (QuesitoNumericoStatistics) o;
                        List<Integer> r = qs.getQuesito().getRespostaAlunoNormalized();
                        if (r==null || r.size() == 0) {
                            qs.addBranco();
                        }
                        else qs.addAnswer(r.get(0));
                    }
                    else if (o instanceof QuesitoFalsoVerdadeiroStatistics) {
                        QuesitoFalsoVerdadeiroStatistics qs = (QuesitoFalsoVerdadeiroStatistics) o;
                        for (ItemQuesitoFalsoVerdadeiroStatistics iqs : qs.getItensStatistics()) {
                            ItemQuesito iq = iqs.getItemQuesito();
                            List<Integer> list = iq.getRespostaAluno();
                            if (list == null || list.size() == 0)
                                iqs.addBranco();
                            else {
                                iqs.addAnswer(list.get(0));
                            }
                        }
                    }
                }
            }
        }
    }

    public String repeat(int n, String st) {
        String result="";
        for (int i=0;i<n;i++)
            result+=st;
        return result;
    }

    public void write(String fileName) throws SQLException, FileNotFoundException, UnsupportedEncodingException {
        StringBuffer report = new StringBuffer();
        //LATEX HEADERS
        report.append("\\documentclass[a4paper,11pt]{article}\n");
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
        report.append("\\usepackage[top=1cm,bottom=1cm,left=1cm,right=1cm,papersize={29.70cm,21.00cm},includehead=false]{geometry}\n");
        report.append("\\setlength{\\parindent}{0cm} \\setlength{\\parskip}{0.1cm}\n");
        report.append("\\setlength{\\columnsep}{0.6cm} \\setlength{\\columnseprule}{0pt}\n");
        report.append("\\renewcommand{\\familydefault}{cmss}\n");
        // report.append("\\pagestyle{empty}\n");
        report.append("\\begin{document}\n");
        report.append("\n");

        String name = _modelProva.getProva().getNome();
        name = name.replace('_',' ');


        report.append("\\centerline{\\huge MIXnFIX: RESULTADO DE AVALIAÇÃO}\n\n");

        report.append("\\centerline{\\Huge "+name+"}\n\n");

        report.append("\\vspace{0.4cm}\n\n");

        report.append("\\begin{footnotesize}\n\n");

        report.append("\\tabcolsep=3pt\n\n");

        ArrayList<Quesito> quesitos = _modelProva.getProvaStructure().getQuesitos();


        report.append("\\begin{center}\n");
        report.append("\\begin{tabular}{cccccc}\n");
        report.append("\\hline \n");
        report.append("Turma & Num. & Média & Min. & Max. & StdDev");
        report.append("\\\\ \n");
        for (ModelProvaCorrecao m : _modelProva.getProvasCorrecoes()) {
            NotasStatistics nStats = _mapModelCorrecao2NotasStatistics.get(m);
            report.append(m.getProvaCorrecao().getNome()); // nome
            report.append(" & "); // sep
            report.append(String.format("%d", nStats.sampleSize())); // nome
            report.append(" & "); // sep
            report.append(String.format("%.3f", nStats.getMean())); // nome
            report.append(" & "); // sep
            report.append(String.format("%.3f", nStats.getMin())); // nome
            report.append(" & "); // sep
            report.append(String.format("%.3f", nStats.getMax())); // nome
            report.append(" & "); // sep
            report.append(String.format("%.3f", nStats.getStdDev())); // nome
            report.append(" \\\\ \n"); // nome
        }
        report.append("\\hline \n"); // nome
        report.append("\\end{tabular} \n"); // nome
        report.append("\\end{center}\n");





        report.append("\\begin{enumerate}\n");
        for (Quesito q: quesitos) {
            int index = quesitos.indexOf(q);

            if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS) {
                report.append("\\item ");
                report.append("\\begin{tabular}{ccccc" + repeat(q.getItensCount(), "c") + "}\n");
                report.append("\\hline \n");
                report.append("Turma & Num. & Acertos & Erros & Brancos");
                List<Integer> corretas = q.getRespostaCorreta();
                for (int i = 0; i < q.getItensCount(); i++) {
                    boolean contains = corretas.contains(i);
                    report.append(" & {" + (contains ? "\\bf " : "") + (char) ( (int) 'A' + i)+"}");
                }
                report.append("\\\\ \n");
                for (ModelProvaCorrecao m : _modelProva.getProvasCorrecoes()) {
                    ArrayList list = (ArrayList) _mapModelCorrecao2Statistics.get(m);
                    Object o = list.get(index);
                    if (o instanceof QuesitoAlternativaStatistics) {
                        QuesitoAlternativaStatistics qs = (QuesitoAlternativaStatistics) o;
                        report.append(m.getProvaCorrecao().getNome()); // nome
                        report.append(" & "); // sep
                        report.append(qs.sampleSize()); // sample size
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", qs.getNumCorrectAnswers(), (100.0 * qs.getNumCorrectAnswers()) / qs.sampleSize())); // nome
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", qs.getNumWrongAnswers(), (100.0 * qs.getNumWrongAnswers()) / qs.sampleSize())); // nome
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", qs.getBlankAnswers(), (100.0 * qs.getBlankAnswers()) / qs.sampleSize())); // nome
                        for (int i = 0; i < qs.getPossibleAnswers(); i++) {
                            report.append(" & "); // sep
                            report.append(String.format("%d (%.0f\\%%)", qs.getAnswerCount(i), (100.0 * qs.getAnswerCount(i)) / qs.sampleSize()));
                        }
                        report.append(" \\\\ \n"); // nome
                    }
                }
                report.append("\\hline \n"); // nome
                report.append("\\end{tabular} \n"); // nome

                if (q.getRespostaCorreta().size() == 0)
                    report.append("{\\sl Esta questão foi ANULADA}"); // nome

            }

            else if (q.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                report.append("\\item ");
                report.append("\\begin{tabular}{cc" + repeat(3*q.getItensCount(), "c") + "}\n");
                report.append("\\hline \n");
                report.append("Turma & Num. ");
                for (int i = 0; i < q.getItensCount(); i++) {
                    report.append(" & " + (char) ( (int) 'A' + i) + " (A)");
                    report.append(" & " + (char) ( (int) 'A' + i) + " (E)");
                    report.append(" & " + (char) ( (int) 'A' + i) + " (B)");
                }
                report.append("\\\\ \n");

                for (ModelProvaCorrecao m : _modelProva.getProvasCorrecoes()) {
                    ArrayList list = (ArrayList) _mapModelCorrecao2Statistics.get(m);
                    QuesitoFalsoVerdadeiroStatistics qs = (QuesitoFalsoVerdadeiroStatistics) list.get(index);
                    report.append(m.getProvaCorrecao().getNome()); // nome
                    report.append(" & "); // sep
                    report.append(qs.sampleSize()); // sample size
                    for (ItemQuesitoFalsoVerdadeiroStatistics iqs : qs.getItensStatistics()) {
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", iqs.getNumCorrectAnswers(), (100.0 * iqs.getNumCorrectAnswers()) / iqs.sampleSize())); // nome
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", iqs.getNumWrongAnswers(), (100.0 * iqs.getNumWrongAnswers()) / iqs.sampleSize())); // nome
                        report.append(" & "); // sep
                        report.append(String.format("%d (%.0f\\%%)", iqs.getBlankAnswers(), (100.0 * iqs.getBlankAnswers()) / iqs.sampleSize())); // nome
                    }
                    report.append(" \\\\ \n"); // nome
                }
                report.append("\\hline \n"); // nome
                report.append("\\end{tabular} \n"); // nome
            }

            else if (q.getTipo() == Quesito.TIPO_NUMERICO_99) {
                report.append("\\item ");
                report.append("\\begin{tabular}{ccccc}\n");
                report.append("\\hline \n");
                report.append("Turma & Num. & Acertos & Erros & Brancos ");
                report.append("\\\\ \n");

                for (ModelProvaCorrecao m : _modelProva.getProvasCorrecoes()) {
                    ArrayList list = (ArrayList) _mapModelCorrecao2Statistics.get(m);
                    QuesitoNumericoStatistics qs = (QuesitoNumericoStatistics) list.get(index);
                    report.append(m.getProvaCorrecao().getNome()); // nome
                    report.append(" & "); // sep
                    report.append(qs.sampleSize()); // sample size
                    report.append(" & "); // sep
                    report.append(String.format("%d (%.0f\\%%)", qs.getNumCorrectAnswers(), (100.0 * qs.getNumCorrectAnswers()) / qs.sampleSize())); // nome
                    report.append(" & "); // sep
                    report.append(String.format("%d (%.0f\\%%)", qs.getNumWrongAnswers(), (100.0 * qs.getNumWrongAnswers()) / qs.sampleSize())); // nome
                    report.append(" & "); // sep
                    report.append(String.format("%d (%.0f\\%%)", qs.getBlankAnswers(), (100.0 * qs.getBlankAnswers()) / qs.sampleSize())); // nome
                    report.append(" \\\\ \n"); // nome
                }
                report.append("\\hline \n"); // nome
                report.append("\\end{tabular} \n"); // nome
            }
        }

        report.append("\\end{enumerate} \n"); // nome

        report.append("\\end{footnotesize}\n");

        report.append("\\end{document} \n"); // nome

        PrintWriter pw = new PrintWriter(fileName,"utf-8");
        pw.print(report.toString());
        pw.flush();
        pw.close();
    }
}

class QuesitoAlternativaStatistics {
    Quesito _quesito;
    private int _brancos;
    private int _naoBrancos;
    private HashMap<Integer,Integer> _mapAnswer2Count;
    public QuesitoAlternativaStatistics(Quesito quesito) {
        _quesito = quesito;
        //
        if (_quesito.getTipo() != Quesito.TIPO_ALTERNATIVAS)
            throw new RuntimeException("Not a quesito de alternativas!");

        _mapAnswer2Count = new HashMap<Integer,Integer>();
    }

    public int sampleSize() {
        return _brancos + _naoBrancos;
    }

    public int getNumCorrectAnswers() {
        java.util.List<Integer> list = _quesito.getRespostaCorreta();
        int result = 0;
        for (int i: list) {
            Integer x = _mapAnswer2Count.get(i);
            result += (x == null ? 0 : x);
        }
        return result;
    }

    public int getNumWrongAnswers() {
        return _naoBrancos - getNumCorrectAnswers();
    }

    public int getBlankAnswers() {
        return _brancos;
    }

    public int getPossibleAnswers() {
        return _quesito.getItensCount();
    }

    public int getAnswerCount(int a) {
        Integer x = _mapAnswer2Count.get(a);
        if (x == null) return 0;
        else return x;
    }

    public void addAnswer(int a) {
        Integer x = _mapAnswer2Count.get(a);
        if (x == null)
            _mapAnswer2Count.put(a,1);
        else
            _mapAnswer2Count.put(a,x+1);
        _naoBrancos++;
    }
    public void addBranco() {
        _brancos++;
    }
    public Quesito getQuesito() {
        return _quesito;
    }
}

class QuesitoNumericoStatistics {
    Quesito _quesito;
    private int _brancos;
    private int _naoBrancos;
    private HashMap<Integer,Integer> _mapAnswer2Count;
    public QuesitoNumericoStatistics(Quesito quesito) {
        _quesito = quesito;
        //
        if (_quesito.getTipo() != Quesito.TIPO_NUMERICO_99)
            throw new RuntimeException("Not a quesito de alternativas!");
        _mapAnswer2Count = new HashMap<Integer,Integer>();
    }

    public void addAnswer(int a) {
        Integer x = _mapAnswer2Count.get(a);
        if (x == null)
            _mapAnswer2Count.put(a,1);
        else
            _mapAnswer2Count.put(a,x+1);
        _naoBrancos++;
    }
    public void addBranco() {
        _brancos++;
    }
    public Quesito getQuesito() {
        return _quesito;
    }

    public int getNumCorrectAnswers() {
        java.util.List<Integer> list = _quesito.getRespostaCorreta();
        int result = 0;
        for (int i: list) {
            Integer imap = _mapAnswer2Count.get(i);
            if (imap != null)  {
                result += imap;
            }
        }
        return result;
    }

    public int getNumWrongAnswers() {
        return _naoBrancos - getNumCorrectAnswers();
    }

    public int getBlankAnswers() {
        return _brancos;
    }

    public int sampleSize() {
        return _brancos + _naoBrancos;
    }
}

class ItemQuesitoFalsoVerdadeiroStatistics {
    ItemQuesito _itemQuesito;
    private int _brancos;
    private int _naoBrancos;
    private HashMap<Integer,Integer> _mapAnswer2Count;
    public ItemQuesitoFalsoVerdadeiroStatistics(ItemQuesito itemQuesito) {
        _itemQuesito = itemQuesito;
        _mapAnswer2Count = new HashMap<Integer,Integer>();
        //
        if (_itemQuesito.getQuesito().getTipo() != Quesito.TIPO_FALSO_VERDADEIRO)
            throw new RuntimeException("Not a quesito de alternativas!");
    }

    public void addAnswer(int i) {
        Integer x = _mapAnswer2Count.get(i);
        if (x == null)
            _mapAnswer2Count.put(i,1);
        else
            _mapAnswer2Count.put(i,x+1);
        _naoBrancos++;
    }
    public void addBranco() {
        _brancos++;
    }

    public ItemQuesito getItemQuesito() {
        return _itemQuesito;
    }

    public int sampleSize() {
        return  _brancos + _naoBrancos;
    }

    public int getNumCorrectAnswers() {
        java.util.List<Integer> list = _itemQuesito.getRespostaCorreta();
        int result = 0;
        for (int i: list) {
            Integer imap = _mapAnswer2Count.get(i);
            if (imap != null)  {
                result += imap;
            }
        }
        return result;
    }

    public int getNumWrongAnswers() {
        return _naoBrancos - getNumCorrectAnswers();
    }

    public int getBlankAnswers() {
        return _brancos;
    }

}

class QuesitoFalsoVerdadeiroStatistics {
    private Quesito _quesito;
    private ArrayList<ItemQuesitoFalsoVerdadeiroStatistics> _items;
    public QuesitoFalsoVerdadeiroStatistics(Quesito quesito) {
        _quesito = quesito;
        // quesito
        if (_quesito.getTipo() != Quesito.TIPO_FALSO_VERDADEIRO)
            throw new RuntimeException("Not a quesito de alternativas!");

        _items = new ArrayList<ItemQuesitoFalsoVerdadeiroStatistics> ();
        for (ItemQuesito iq : _quesito.getItens()) {
            _items.add(new ItemQuesitoFalsoVerdadeiroStatistics(iq));
        }
    }

    public int sampleSize() {
        if (_items.size() == 0)
            return 0;
        else
            return _items.get(0).sampleSize();
    }

    public ArrayList<ItemQuesitoFalsoVerdadeiroStatistics> getItensStatistics() {
        return (ArrayList<ItemQuesitoFalsoVerdadeiroStatistics>) _items.clone();
    }
    public Quesito getQuesito() {
        return _quesito;
    }
}

class NotasStatistics {
    ArrayList<Double> _notas = new ArrayList<Double>();

    public NotasStatistics() {
    }

    public void addNota(double nota) {
        _notas.add(nota);
        Collections.sort(_notas);
    }

    public int sampleSize() {
        return _notas.size();
    }

    public double getMax() {
        if (_notas.size() > 0)
            return _notas.get(_notas.size() - 1);
        else
            return 0;
    }

    public double getMin() {
        if (_notas.size() > 0)
            return _notas.get(0);
        else
            return 0;
    }

    public double getMean() {
        if (sampleSize() == 0)
            return 0;
        double sum = 0;
        for (double d : _notas) {
            sum += d;
        }
        return sum / sampleSize();
    }

    public double getStdDev() {
        if (sampleSize() == 0)
            return 0;
        double sumdif = 0;
        double mean = getMean();
        for (double d : _notas) {
            sumdif += (d - mean) * (d - mean);
        }
        double stddev = Math.sqrt(sumdif/(sampleSize()-1));
        return stddev;
    }
}
