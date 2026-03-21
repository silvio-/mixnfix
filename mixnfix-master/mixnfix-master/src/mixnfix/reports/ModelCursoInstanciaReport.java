package mixnfix.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import mixnfix.gui.ModelAvaliacao;
import mixnfix.gui.ModelAvaliacaoAluno;
import mixnfix.gui.ModelCursoInstanciaOld;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.Turma;


public class ModelCursoInstanciaReport {

    public ModelCursoInstanciaReport() {
    }

    public static void gerarRelatorio(ModelCursoInstanciaOld modelCursoInstancia, String filename) throws IOException, WriteException {

        /////////////////////////////////////////////////
        // Montar Arquivo Excel e matriz para gerar HTML


        // definir planilha e sheet
        WritableWorkbook workbook = Workbook.createWorkbook(new File(filename));
        WritableSheet sheet = workbook.createSheet("Listão", 0);

        WritableFont arial14Font = new WritableFont(WritableFont.ARIAL, 14);
        WritableFont arial10BoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
        WritableFont arial10Font = new WritableFont(WritableFont.ARIAL, 10);
        WritableFont arial8Font = new WritableFont(WritableFont.ARIAL, 8);

        sheet.mergeCells(0, 0, 2, 0);
        sheet.setPageSetup(jxl.format.PageOrientation.LANDSCAPE, jxl.format.PaperSize.A4, 0, 0);
        addLabelCellInWorksheet(sheet, "MIXnFIX - Listão", 0, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.NONE,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.LEFT, arial14Font);

        // INICIO: definir cabeçalho
        addLabelCellInWorksheet(sheet, "Matrícula", 2, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.BOTTOM,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial10BoldFont);

        CellView cellView = new CellView();
        cellView.setSize(50*256);
        sheet.setColumnView(1, cellView);
        addLabelCellInWorksheet(sheet, "Nome", 2, 1,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.BOTTOM,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.LEFT, arial10BoldFont);

        addLabelCellInWorksheet(sheet, "Turma", 2, 2,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.BOTTOM,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial10BoldFont);

        addLabelCellInWorksheet(sheet, "Avaliações", 2, 3,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.BOTTOM,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial10BoldFont);

        // obter avaliacoes do CursoInstancia
        List<ModelAvaliacao> modelsAvaliacoes = modelCursoInstancia.getAvaliacoes();
        HashMap<ModelAvaliacao,List<ModelAvaliacaoAluno>> mapAvaliacaoAvaliacaoAluno = new HashMap<ModelAvaliacao,List<ModelAvaliacaoAluno>>();
        int i = 0;
        for (ModelAvaliacao modelAvaliacao : modelsAvaliacoes) {
            addLabelCellInWorksheet(sheet, (String) modelAvaliacao.getAvaliacao().getNome(), 2, 4 + 2 * i,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial10BoldFont);
            addLabelCellInWorksheet(sheet, "Norm", 2, 4 + 2 * i + 1,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial10BoldFont);

            // obter avaliacoes da Avaliacao
            List<ModelAvaliacaoAluno> modelsAvaliacaoAlunos = modelAvaliacao.getAvaliacoesAlunos();
            mapAvaliacaoAvaliacaoAluno.put(modelAvaliacao,modelsAvaliacaoAlunos);
            i++;
        }
        // FIM: definir cabeçalho

        // obter alunos matriculados no CursoInstancia
        List<AlunoCursoInstancia> alunosMatriculados = modelCursoInstancia.getAlunosMatriculadosOrdenadosPorNome();

        int linha = 3;
        for (AlunoCursoInstancia alunoCursoInstancia: alunosMatriculados) {

            Aluno aluno = alunoCursoInstancia.getAluno_AlunoCursoInstancia();
            addLabelCellInWorksheet(sheet, aluno.getMatricula(), linha, 0,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);
            addLabelCellInWorksheet(sheet, fillRight(aluno.getNome(), 48, ' '), linha, 1,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.LEFT, arial8Font);
            addLabelCellInWorksheet(sheet, "", linha, 2,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);
            addIntegerCellInWorksheet(sheet, 0, linha, 3,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);

            int coluna = 0;
            int nroAvaliacoes = 0;
            for (ModelAvaliacao modelAvaliacao : modelsAvaliacoes) {

                List<ModelAvaliacaoAluno> avaliacoesAlunos = mapAvaliacaoAvaliacaoAluno.get(modelAvaliacao);
                // ModelAvaliacaoAluno avaliacaoAluno = (ModelAvaliacaoAluno) avaliacoesAlunos.get(alunoCursoInstancia);
                ModelAvaliacaoAluno avaliacaoAluno = null;
                for (ModelAvaliacaoAluno aa: avaliacoesAlunos) {
                    Aluno alunoAA = aa.getAvaliacaoAluno().getAlunoCursoInstancia_AvaliacaoAluno().getAluno_AlunoCursoInstancia();
                    if (alunoAA.getMatricula().equals(aluno.getMatricula())) {
                        avaliacaoAluno = aa;
                        nroAvaliacoes++;
                        break;
                    }
                }
                if (avaliacaoAluno != null) {
                    addDoubleCellInWorksheet(sheet, avaliacaoAluno.getAvaliacaoAluno().getNota(), linha, 2 * coluna + 4,
                                             jxl.format.Colour.WHITE,
                                             jxl.format.Border.NONE,
                                             jxl.format.BorderLineStyle.THIN,
                                             jxl.format.Alignment.RIGHT, arial8Font);
                    addLabelCellInWorksheet(sheet, "", linha, 2 * coluna + 5,
                                            jxl.format.Colour.WHITE,
                                            jxl.format.Border.NONE,
                                            jxl.format.BorderLineStyle.THIN,
                                            jxl.format.Alignment.CENTRE, arial8Font);
                }
                coluna++;
            }

            addIntegerCellInWorksheet(sheet, nroAvaliacoes, linha, 3,
                                     jxl.format.Colour.WHITE,
                                     jxl.format.Border.NONE,
                                     jxl.format.BorderLineStyle.THIN,
                                     jxl.format.Alignment.RIGHT, arial8Font);

            linha++;
        }


        // Fechar a planilha
        workbook.write();
        workbook.close();

        // Montar Arquivo Excel e matriz para gerar HTML
        /////////////////////////////////////////////////

    }

    public static void gerarRelatorioPotTurma(ModelCursoInstanciaOld modelCursoInstancia, String filename) throws Exception {

        /////////////////////////////////////////////////
        // Montar Arquivo Excel


        // definir planilha e sheet
        WritableWorkbook workbook = Workbook.createWorkbook(new File(filename));
        WritableSheet sheet = workbook.createSheet("Listão", 0);

        WritableFont arial14Font = new WritableFont(WritableFont.ARIAL, 14);
        WritableFont arial10BoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
        WritableFont arial10Font = new WritableFont(WritableFont.ARIAL, 10);
        WritableFont arial8Font = new WritableFont(WritableFont.ARIAL, 10);

        sheet.mergeCells(0, 0, 2, 0);
        sheet.setPageSetup(jxl.format.PageOrientation.LANDSCAPE, jxl.format.PaperSize.A4, 0, 0);
        addLabelCellInWorksheet(sheet, "MIXnFIX - Listão", 0, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.NONE,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.LEFT, arial14Font);


        HashMap<Turma,ArrayList<AlunoCursoInstancia>> mapTurmaAlunos = modelCursoInstancia.getAlunosMatriculadosOrdenadosPorNomePorTurma();

        ArrayList<Turma> turmas = new ArrayList<Turma>();
        Iterator iterator = mapTurmaAlunos.keySet().iterator();
        while (iterator.hasNext()) {
            turmas.add((Turma) iterator.next());
        }
        Collections.sort(turmas, new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }
            public int compare(Object o1, Object o2) {
                Turma t1 = (Turma) o1;
                Turma t2 = (Turma) o2;
                return (t1.getNome().compareTo(t2.getNome()));
            }
        });

        int linha = 1;
        for (Turma turma: turmas) {
            linha++;

            // INICIO: definir cabeçalho
            addLabelCellInWorksheet(sheet, "Matrícula", linha, 0,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial10BoldFont);

            CellView cellView = new CellView();
            cellView.setSize(50 * 256);
            sheet.setColumnView(1, cellView);
            addLabelCellInWorksheet(sheet, "Nome", linha, 1,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.LEFT, arial10BoldFont);

            addLabelCellInWorksheet(sheet, "Turma", linha, 2,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial10BoldFont);

            addLabelCellInWorksheet(sheet, "Avaliações", linha, 3,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.BOTTOM,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial10BoldFont);

            // obter avaliacoes do CursoInstancia
            List<ModelAvaliacao> modelsAvaliacoes = modelCursoInstancia.getAvaliacoes();
            HashMap<ModelAvaliacao, List<ModelAvaliacaoAluno>> mapAvaliacaoAvaliacaoAluno = new HashMap<ModelAvaliacao, List<ModelAvaliacaoAluno>> ();
            int i = 0;
            for (ModelAvaliacao modelAvaliacao : modelsAvaliacoes) {
                addLabelCellInWorksheet(sheet, (String) modelAvaliacao.getAvaliacao().getNome(), linha, 4 + 2 * i,
                                        jxl.format.Colour.WHITE,
                                        jxl.format.Border.BOTTOM,
                                        jxl.format.BorderLineStyle.THIN,
                                        jxl.format.Alignment.CENTRE, arial10BoldFont);
                addLabelCellInWorksheet(sheet, "Norm", linha, 4 + 2 * i + 1,
                                        jxl.format.Colour.WHITE,
                                        jxl.format.Border.BOTTOM,
                                        jxl.format.BorderLineStyle.THIN,
                                        jxl.format.Alignment.CENTRE, arial10BoldFont);

                // obter avaliacoes da Avaliacao
                List<ModelAvaliacaoAluno> modelsAvaliacaoAlunos = modelAvaliacao.getAvaliacoesAlunos();
                mapAvaliacaoAvaliacaoAluno.put(modelAvaliacao, modelsAvaliacaoAlunos);
                i++;
            }
            // FIM: definir cabeçalho

            linha++;
            // obter alunos matriculados no CursoInstancia
            List<AlunoCursoInstancia> alunosMatriculados = modelCursoInstancia.getAlunosMatriculadosOrdenadosPorNome();

            for (AlunoCursoInstancia alunoCursoInstancia : alunosMatriculados) {

                Aluno aluno = alunoCursoInstancia.getAluno_AlunoCursoInstancia();
                Turma turmaAluno = null; //aluno.getTurma();

                if (!turmaAluno.equals(turma))
                    continue;

                addLabelCellInWorksheet(sheet, aluno.getMatricula(), linha, 0,
                                        jxl.format.Colour.WHITE,
                                        jxl.format.Border.NONE,
                                        jxl.format.BorderLineStyle.THIN,
                                        jxl.format.Alignment.CENTRE, arial8Font);
                addLabelCellInWorksheet(sheet, fillRight(aluno.getNome(), 48, ' '), linha, 1,
                                        jxl.format.Colour.WHITE,
                                        jxl.format.Border.NONE,
                                        jxl.format.BorderLineStyle.THIN,
                                        jxl.format.Alignment.LEFT, arial8Font);
                addLabelCellInWorksheet(sheet, turma.getNome(), linha, 2,
                                        jxl.format.Colour.WHITE,
                                        jxl.format.Border.NONE,
                                        jxl.format.BorderLineStyle.THIN,
                                        jxl.format.Alignment.CENTRE, arial8Font);
                addIntegerCellInWorksheet(sheet, 0, linha, 3,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);

                int coluna = 0;
                int nroAvaliacoes = 0;
                for (ModelAvaliacao modelAvaliacao : modelsAvaliacoes) {

                    List<ModelAvaliacaoAluno> avaliacoesAlunos = mapAvaliacaoAvaliacaoAluno.get(modelAvaliacao);
                    // ModelAvaliacaoAluno avaliacaoAluno = (ModelAvaliacaoAluno) avaliacoesAlunos.get(alunoCursoInstancia);
                    ModelAvaliacaoAluno avaliacaoAluno = null;
                    for (ModelAvaliacaoAluno aa : avaliacoesAlunos) {
                        Aluno alunoAA = aa.getAvaliacaoAluno().getAlunoCursoInstancia_AvaliacaoAluno().getAluno_AlunoCursoInstancia();
                        if (alunoAA.getMatricula().equals(aluno.getMatricula())) {
                            avaliacaoAluno = aa;
                            nroAvaliacoes++;
                            break;
                        }
                    }
                    if (avaliacaoAluno != null) {
                        addDoubleCellInWorksheet(sheet, avaliacaoAluno.getAvaliacaoAluno().getNota(), linha, 2 * coluna + 4,
                                                 jxl.format.Colour.WHITE,
                                                 jxl.format.Border.NONE,
                                                 jxl.format.BorderLineStyle.THIN,
                                                 jxl.format.Alignment.RIGHT, arial8Font);
                        float nota = 40f * avaliacaoAluno.getAvaliacaoAluno().getNota() / 35f;
                        addDoubleCellInWorksheet(sheet, nota, linha, 2 * coluna + 5,
                                                jxl.format.Colour.WHITE,
                                                jxl.format.Border.NONE,
                                                jxl.format.BorderLineStyle.THIN,
                                                jxl.format.Alignment.RIGHT, arial8Font);
                       addDoubleCellInWorksheet(sheet, avaliacaoAluno.getAvaliacaoAluno().getNota() + 1.25f, linha, 2 * coluna + 6,
                                               jxl.format.Colour.WHITE,
                                               jxl.format.Border.NONE,
                                               jxl.format.BorderLineStyle.THIN,
                                               jxl.format.Alignment.RIGHT, arial8Font);
                    }
                    coluna++;
                }

                addIntegerCellInWorksheet(sheet, nroAvaliacoes, linha, 3,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.RIGHT, arial8Font);

                linha++;
            }
        }

        // Fechar a planilha
        workbook.write();
        workbook.close();
        // Montar Arquivo Excel e matriz para gerar HTML
        /////////////////////////////////////////////////

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


}


/*


//
    for (int j = 0; j < e.getNumeroDeEntradasProvas(); j++) {
        EntradaProva ep = e.getEntradaProva(j);
        Correcao correcao = ep.getCorrecao();
        Prova prova = correcao.getProva();
        int col = ((Integer)mapLinear.get(new Integer(prova.getIndex()))).intValue();
        String nota  = format(correcao.getNota(),3);
        String fator = "(d: "+format(ep.getTargetDelta(),2)+" - c: "+format(ep.getConfidence(),2)+")";

        matriz[i][col + 4] = "<a href=\"" + e.getFilenameCorrecaoPage(j) + "\">" + nota + " " + fator + "</a>";
        addDoubleCellInWorksheet(sheet, correcao.getNota(), i + 3, 2 * col + 4,
                                      jxl.format.Colour.WHITE,
                                      jxl.format.Border.NONE,
                                      jxl.format.BorderLineStyle.THIN,
                                      jxl.format.Alignment.RIGHT, arial8Font);
        addLabelCellInWorksheet(sheet, "", i + 3, 2 * col + 5,
                                     jxl.format.Colour.WHITE,
                                     jxl.format.Border.NONE,
                                     jxl.format.BorderLineStyle.THIN,
                                     jxl.format.Alignment.CENTRE, arial8Font);

        Vector gruposTotalizados = prova.getGruposTotalizados();
        for (int k = 0; k < gruposTotalizados.size(); k++) {
            Permutavel permutavel = (Permutavel) gruposTotalizados.get(k);
            IndexAndTag iat = new IndexAndTag(prova.getIndex(),permutavel.getTag());
            col = ((Integer)mapLinear.get(iat)).intValue();
            String valor = ""+format(permutavel.getTotal(),3);
            matriz[i][col+4] = valor;
            addLabelCellInWorksheet(sheet, valor, i + 3, 2*col+4,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);
            addLabelCellInWorksheet(sheet,"", i + 3, 2*col+4+1,
                                          jxl.format.Colour.WHITE,
                                          jxl.format.Border.NONE,
                                          jxl.format.BorderLineStyle.THIN,
                                          jxl.format.Alignment.CENTRE, arial8Font);
        }
    }
}

*/

