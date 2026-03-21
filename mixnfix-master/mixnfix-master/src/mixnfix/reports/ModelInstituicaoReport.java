package mixnfix.reports;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import jxl.CellView;
import jxl.Workbook;
import jxl.write.NumberFormats;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import mixnfix.gui.ModelAlunoProvaCorrecao;
import mixnfix.gui.ModelEntradaProvaCorrecao;
import mixnfix.gui.ModelInstituicao;
import mixnfix.gui.ModelProva;
import mixnfix.gui.ModelProvaCorrecao;
import mixnfix.modelo.Aluno;

public class ModelInstituicaoReport {
    public ModelInstituicaoReport() {
    }

    private static String fillRight(String s, int size, char ch) {
        String result = s;
        for (int i = 0; i < size - s.length(); i++) {
            result += ch;
        }
        if (result.length() > size)
            result = result.substring(0, size);
        return result;
    }

    private static String fillLeft(String s, int size, char ch) {
        String result = "";
        for (int i = 0; i < size - s.length(); i++) {
            result += ch;
        }
        result += s;
        if (result.length() > size)
            result = result.substring(0, size);
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

    public static void gerarRelatorioExcel(ModelInstituicao instituicao, ArrayList<ModelProvaCorrecao> mpcs, String filename) throws IOException, WriteException {

        // Montar linhas e colunas
        // as linhas sao formadas pelo nome dos alunos e
        // as colunas sao formadas pelas Provas


        T estrutura = new T(mpcs);

        /////////////////////////////////////////////////
        // Montar Arquivo Excel
        // definir planilha e sheet

        WritableWorkbook workbook = Workbook.createWorkbook(new File(filename));
        WritableSheet sheet = workbook.createSheet("Listão", 0);

        WritableFont arial14Font = new WritableFont(WritableFont.ARIAL, 14);
        WritableFont arial10BoldFont = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD, true);
        WritableFont arial10Font = new WritableFont(WritableFont.ARIAL, 10);
        WritableFont arial8BoldFont = new WritableFont(WritableFont.ARIAL, 8, WritableFont.BOLD, false);
        WritableFont arial8Font = new WritableFont(WritableFont.ARIAL, 8);

        //sheet.mergeCells(0, 0, 2, 0);
        sheet.setPageSetup(jxl.format.PageOrientation.LANDSCAPE, jxl.format.PaperSize.A4, 0, 0);
        addLabelCellInWorksheet(sheet, "MIXnFIX - Quadro de Notas", 0, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.NONE,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.LEFT, arial14Font);
        addLabelCellInWorksheet(sheet, "Instituição: " + instituicao.getInstituicao().getNome(), 1, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.NONE,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.LEFT, arial14Font);

        //////////////////////////
        //// Definir cabecalho

        int linha = 3;

        CellView cellView = new CellView();
        cellView.setSize(50 * 256);
        sheet.setColumnView(0, cellView);

        cellView = new CellView();
        cellView.setSize(12 * 256);
        sheet.setColumnView(1, cellView);

        cellView = new CellView();
        cellView.setSize(4 * 256);
        sheet.setColumnView(2, cellView);

        addLabelCellInWorksheet(sheet, "Nome", linha, 0,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.ALL,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial8BoldFont);

        addLabelCellInWorksheet(sheet, "Matrícula", linha, 1,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.ALL,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial8BoldFont);

        addLabelCellInWorksheet(sheet, "#P", linha, 2,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.ALL,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial8BoldFont);

        for (int j = 0; j < estrutura.getProvasNumber(); j++) {
            int nroChars = Math.max(10, estrutura.getColumn(j).getProva().getNome().length());
            cellView = new CellView();
            cellView.setSize(nroChars * 256);
            sheet.setColumnView(3 + j, cellView);
            System.out.println("" + j + ":" + cellView.getSize());
            addLabelCellInWorksheet(sheet, estrutura.getColumn(j).getProva().getNome(), linha, 3 + j,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.ALL,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial8BoldFont);
        }


        /* NAO REMOVER - Parte do Relatorio de Sóstenes
        int nroPartes = 2;
        int[] nroCorrecoesPorParte = new int[] {5, 4};
        int[] nroValidosPorParte   = new int[] {3, 3};
        for (int j = 0; j < nroPartes; j++) {
            addLabelCellInWorksheet(sheet, "Nota "+(j+1), linha, 3 + estrutura.getProvasNumber() + j,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.ALL,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial8BoldFont);
        }

        addLabelCellInWorksheet(sheet, "Média", linha, 3 + estrutura.getProvasNumber() + 2,
                                jxl.format.Colour.WHITE,
                                jxl.format.Border.ALL,
                                jxl.format.BorderLineStyle.THIN,
                                jxl.format.Alignment.CENTRE, arial8BoldFont);
        NAO REMOVER - Parte do Relatorio de Sóstenes
        */

        //// Definir cabecalho
        //////////////////////////



        linha = 4;
        for (int i = 0; i < estrutura.getAlunosNumber(); i++) {
            Aluno aluno = estrutura.getRow(i);
            System.out.println(aluno.getNome());

            addLabelCellInWorksheet(sheet, aluno.getNome(), linha, 0,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.ALL,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.LEFT, arial8Font);

            addLabelCellInWorksheet(sheet, aluno.getMatricula(), linha, 1,
                                    jxl.format.Colour.WHITE,
                                    jxl.format.Border.ALL,
                                    jxl.format.BorderLineStyle.THIN,
                                    jxl.format.Alignment.CENTRE, arial8Font);

            addIntegerCellInWorksheet(sheet, estrutura.getNumberOfProvasRealizadas(aluno), linha, 2,
                                      jxl.format.Colour.WHITE,
                                      jxl.format.Border.ALL,
                                      jxl.format.BorderLineStyle.THIN,
                                      jxl.format.Alignment.CENTRE, arial8Font);

            for (int j = 0; j < estrutura.getProvasNumber(); j++) {
                ArrayList<ModelEntradaProvaCorrecao> entradas = estrutura.get(i, j);
                if (entradas != null) {
                    ModelEntradaProvaCorrecao mepc = entradas.get(0);
                    try {
                        mepc.preencherProvaComGabaritoCorrente();
                        mepc.getProvaStructure().avaliarNota();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    addDoubleCellInWorksheet(sheet, mepc.getProvaStructure().getNota(),
                                             linha, 3 + j,
                                             jxl.format.Colour.WHITE,
                                             jxl.format.Border.ALL,
                                             jxl.format.BorderLineStyle.THIN,
                                             jxl.format.Alignment.CENTRE, arial8Font);
                }
                else {
                    addLabelCellInWorksheet(sheet, "", linha, 3 + j,
                                            jxl.format.Colour.WHITE,
                                            jxl.format.Border.ALL,
                                            jxl.format.BorderLineStyle.THIN,
                                            jxl.format.Alignment.CENTRE, arial8Font);
                }
            }

            /* NAO REMOVER - Parte do Relatorio de Sóstenes
            // float[] notas = estrutura.getConsolidation(estrutura.indexOf(aluno), 2, new int[] {5, 4}, new int[] {3, 3});
            float[] notas = estrutura.getConsolidation(estrutura.indexOf(aluno), nroPartes, nroCorrecoesPorParte, nroValidosPorParte);
            for (int j = 0; j < notas.length; j++) {
                addDoubleCellInWorksheet(sheet, notas[j],
                                         linha, 3 + estrutura.getProvasNumber()+ j,
                                         jxl.format.Colour.WHITE,
                                         jxl.format.Border.ALL,
                                         jxl.format.BorderLineStyle.THIN,
                                         jxl.format.Alignment.CENTRE, arial8Font);

            }

            float media = 0f;
            for (int j = 0; j < notas.length; j++)
                media += notas[j];
            media /= notas.length;

            addDoubleCellInWorksheet(sheet, media,
                                     linha, 3 + estrutura.getProvasNumber()+ notas.length,
                                     jxl.format.Colour.WHITE,
                                     jxl.format.Border.ALL,
                                     jxl.format.BorderLineStyle.THIN,
                                     jxl.format.Alignment.CENTRE, arial8Font);
            NAO REMOVER - Parte do Relatorio de Sóstenes
             */

            linha++;
        }

        // Fechar a planilha
        workbook.write();
        workbook.close();
        // Montar Arquivo Excel e matriz para gerar HTML
        /////////////////////////////////////////////////
    }
}

class T {
    private ArrayList<Aluno>      _alunos;
    private ArrayList<ModelProva> _provas;
    private ArrayList<ModelEntradaProvaCorrecao>[][] _matriz;

    public T(List<ModelProvaCorrecao> mpcs) {
        _alunos = new ArrayList<Aluno>();
        _provas = new ArrayList<ModelProva>();

        for (ModelProvaCorrecao mpc: mpcs) {

            ModelProva mp = mpc.getModelProva();
            if (this.indexOf(mp) == -1)
                _provas.add(mp);

            ArrayList<ModelAlunoProvaCorrecao> mapcs = mpc.getAlunosProvaCorrecao();
            for (ModelAlunoProvaCorrecao mapc: mapcs) {
                if (this.indexOf(mapc.getAluno()) == -1) {
                    _alunos.add(mapc.getAluno());
                }
            }
        }

        // ordena alunos
        Collections.sort(_alunos, new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }

            public int compare(Object o1, Object o2) {
                Aluno a1 = (Aluno) o1;
                Aluno a2 = (Aluno) o2;
                return (a1.getNome().compareTo(a2.getNome()));
            }
        });

        _matriz = new ArrayList[_alunos.size()][_provas.size()];

        for (ModelProvaCorrecao mpc: mpcs) {
            ArrayList<ModelEntradaProvaCorrecao> mepcs = mpc.getEntradasProvaCorrecaoOrdenadas();

            for (ModelEntradaProvaCorrecao mepc: mepcs) {
                Aluno aluno = mepc.getAluno();
                ModelProva prova = mepc.getCorrecaoProva().getModelProva();

                int indexA = this.indexOf(aluno);
                int indexP = this.indexOf(prova);
                if ( (indexA != -1) && (indexP != -1)) {
                    if (_matriz[indexA][indexP] == null) {
                        _matriz[indexA][indexP] = new ArrayList<ModelEntradaProvaCorrecao> ();
                    }
                    _matriz[indexA][indexP].add(mepc);
                }
            }
        }
    }

    public ModelProva getColumn(int index) {
        return _provas.get(index);
    }

    public Aluno getRow(int index) {
        return _alunos.get(index);
    }

    public int indexOf(Aluno aluno) {
        int result = -1;
        for (int i=0; i<_alunos.size(); i++) {
            Aluno a = _alunos.get(i);
            if (a.getId()==aluno.getId()) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int indexOf(ModelProva prova) {
        int result = -1;
        for (int i=0; i<_provas.size(); i++) {
            ModelProva p = _provas.get(i);
            if (p.getProva().getId()==prova.getProva().getId()) {
                result = i;
                break;
            }
        }
        return result;
    }

    public int getProvasNumber() {
        return _provas.size();
    }

    public int getAlunosNumber() {
        return _alunos.size();
    }

    public int getNumberOfProvasRealizadas(Aluno aluno) {
        int result = 0;
        int index = this.indexOf(aluno);
        if (index != -1) {
            for (int j = 0; j < this.getProvasNumber(); j++) {
                ArrayList<ModelEntradaProvaCorrecao> entradas = this.get(index,j);
                if (entradas != null)
                    result++;
            }
        }
        return result;
    }

    public ArrayList<ModelEntradaProvaCorrecao> get(int index1, int index2) {
        return _matriz[index1][index2];
    }

    public float[] getConsolidation(int indexAluno, int nroPartes, int[] nroCorrecoesPorParte, int[] nroValidosPorParte) {
        float[] result = new float[nroPartes];

        int indexC = 0;
        for (int i = 0; i < nroPartes; i++) {
            System.out.println("indexC = " + indexC);
            int nroCorrecoesDaParte = nroCorrecoesPorParte[i];
            Vector<Float> notas = new Vector<Float> ();
            for (int j = indexC; j < indexC + nroCorrecoesDaParte; j++) {
                System.out.println("j = "+j);
                ArrayList<ModelEntradaProvaCorrecao> entradas = this.get(indexAluno, j);
                if (entradas != null) {
                    ModelEntradaProvaCorrecao mepc = entradas.get(0);
                    try {
                        mepc.preencherProvaComGabaritoCorrente();
                        mepc.getProvaStructure().avaliarNota();
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    notas.add(mepc.getProvaStructure().getNota());
                }
                else {
                    notas.add(0.0f);
                }
            }

            // ordena alunos
            System.out.print("Notas de "+this.getRow(indexAluno).getNome() + ": ");
            Collections.sort(notas);
            for (int k = 0; k < notas.size(); k++) {
                System.out.print(""+notas.get(k)+" ");

            }
            System.out.println("");

            float soma = 0f;
            System.out.print("Notas de "+this.getRow(indexAluno).getNome() + ": ");
            for (int j = 0; j < nroValidosPorParte[i]; j++) {
                System.out.print(""+notas.get(nroCorrecoesDaParte - 1 - j)+" ");
                soma += notas.get(nroCorrecoesDaParte - 1 - j);
                //soma += notas.get(j);
            }
            System.out.println("");

            result[i] = soma / (float) nroValidosPorParte[i];

            indexC += nroCorrecoesDaParte;
        }

        return result;
    }

}
