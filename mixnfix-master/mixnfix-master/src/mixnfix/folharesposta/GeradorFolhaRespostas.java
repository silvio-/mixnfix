package mixnfix.folharesposta;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import mixnfix.Controller;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;

/**
 * Os pontos de controle devem independer da estrutura da prova.
 * Só assim será possível corrigir provas com estruturas di-
 * ferentes.
 */
public class GeradorFolhaRespostas implements IFolhaResposta {
    private ProvaStructure _prova;
    private FRBloco _id;
    private FRBloco _codigoMF;
    private ArrayList<FRBloco> _questoes = new ArrayList<FRBloco> ();
    private ArrayList<FRBloco> _controles = new ArrayList<FRBloco> ();

    private CellMap _cellMapFixo; // cell map associado
    private CellMap _cellMapVariavel; // cell map associado

    public static final float GRAY_FILL = 1f; // milimeters

    private static final float MARGIN = 0f; // milimeters
    private static final float X0 = 2f; // top left 4-target corner X
    private static final float Y0 = 7.5f; // top left 4-target corner Y
    private static final float WIDTH = 176f; // width
    private static final float HEIGHT = 180f; // height
    public static final float PAPER_WIDTH = 180f; // milimeters
    public static final float PAPER_HEIGHT = 190f; // milimeters

    public static final float CELL_DIAMETER = 4f; // milimeters

    public static final float GAP_CONTROLO_QUESTOES = 2.5f; // milimeters

    public static final float CP_SIZE = 2.4f; // size of control point

    public static final float WIDTH_COLUNA_CONTROLE = 5f; // size of control point
    public static final float WIDTH_COLUNA_CONTROLE_MIDDLE = 8f; // size of control point

    private static final float WIDTH_UTIL_COLUMN = (WIDTH - 2 * WIDTH_COLUNA_CONTROLE - WIDTH_COLUNA_CONTROLE_MIDDLE) / 2.0f; // width
    private static final float X0_COLUMN_1 = X0 + WIDTH_COLUNA_CONTROLE; // width
    private static final float X0_COLUMN_2 = X0 + WIDTH - WIDTH_COLUNA_CONTROLE - WIDTH_UTIL_COLUMN; // width

    public GeradorFolhaRespostas(ProvaStructure prova) {
        _prova = prova;
        _codigoMF = new FRCodigoMixNFix();
        _id = new FRIdentificador(prova.getNumDigitosID());

        float gap = HEIGHT / 6;
        float phase = gap;

        FRColunaControle cL = new FRColunaControle(WIDTH_COLUNA_CONTROLE, HEIGHT - 10, phase, gap, FRColunaControle.LEFT_ALIGNMENT);
        FRColunaControle cM = new FRColunaControle(WIDTH_COLUNA_CONTROLE_MIDDLE, HEIGHT - 10, phase, gap, FRColunaControle.CENTER_ALIGNMENT);
        FRColunaControle cR = new FRColunaControle(WIDTH_COLUNA_CONTROLE, HEIGHT - 10, phase, gap, FRColunaControle.RIGHT_ALIGNMENT);

        cL.setPosition(X0, Y0);
        cR.setPosition(X0 + WIDTH - WIDTH_COLUNA_CONTROLE, Y0);
        cM.setPosition(X0 + (WIDTH - WIDTH_COLUNA_CONTROLE_MIDDLE) / 2.0f, Y0);
        _controles.add(cL);
        _controles.add(cR);
        _controles.add(cM);

        _id.setPosition(X0_COLUMN_1 + (WIDTH_UTIL_COLUMN - _id.getWidth()) / 2.0f, Y0 + MARGIN);
        _codigoMF.setPosition(X0_COLUMN_2 + (WIDTH_UTIL_COLUMN - _codigoMF.getWidth()) / 2.0f, Y0 + MARGIN);

        // inicializar CellMap
        try {
            _cellMapFixo = new CellMap(X0, Y0, WIDTH, HEIGHT);
            _codigoMF.addToCellMap(_cellMapFixo);
            _id.addToCellMap(_cellMapFixo);
            for (FRBloco b : _controles)
                b.addToCellMap(_cellMapFixo);
            _cellMapFixo.addControlPoint(X0 + WIDTH / 2.0f, Y0);
            _cellMapFixo.addControlPoint(X0 + WIDTH / 2.0f, Y0 + HEIGHT);
            _cellMapFixo.autoTriangulation(); // triangulate
        }
        catch (CellMapException ex) {
            ex.printStackTrace();
        }
    }

    public ProvaStructure getProva() {
        return _prova;
    }

    public static final int ID = 1;

    /**
     * Ceulas que são fixas.
     * Vou reservar os IDs de 1 a 100 para
     * as folhas de resposta do tipo prova.
     */
    public int getId() {
        return ID;
    }

    /**
     * Ceulas que são fixas.
     */
    public String getName() {
        return "Folha Básica (pequena) para Prova";
    }

    /**
     * Ceulas que são fixas.
     */
    public String getDescription() {
        return "Folha de resposta básica para prova.\n" +
               "As células têm diâmetro de 4mm, o que dá, numa.\n"+
               "resoluçao de 640x480, 37 pixels por célula.\n";
    }


    /**
     * Usa a permutação corrente da prova para posicionar
     * os blocos que representam os campos (Fields) da
     * prova.
     */
    public void inicializarQuesitos(int tipo) {
        _tipo = tipo;
        ProvaStructure.permuteProva(_prova, tipo);
        inicializarHardWork();
    }

    public void inicializarQuesitos() {
        inicializarHardWork();
    }

    private int _tipo;

    /**
     * Devolve o tipo de gabarito que foi inicializado.
     */
    public int getTipo() {
        return _tipo;
    }

    /**
     * Devolve array com a posição na imagem dos pontos de controle
     * da região fixa do gabarito.
     */
    public double[] getControlPointsImagePositions() {
        List<ControlPoint> l = _cellMapFixo.getControlPoints();
        double[] cps = new double[2 * l.size()];
        int i = 0;
        for (ControlPoint p : l) {
            cps[i++] = p.getImageX();
            cps[i++] = p.getImageY();
        }
        return cps;
    }

    /**
     * O gabarito corrente (<String identificadora de campo> -> <valor>).
     */
    public HashMap<String, Integer> getGabaritoCorrente() {
        HashMap<String, Integer> mapCampoValor = new HashMap<String, Integer> ();
        java.util.List<Field> fields = this._cellMapVariavel.getAllFields();
        for (Field f : fields) {
            if (f instanceof OptionField) { // só inclui no gabarito as questões de OptionField!
                OptionField of = (OptionField) f;
                mapCampoValor.put(of.getNome(), of.getValue());
            }
        }
        return mapCampoValor;
    }

    private void inicializarHardWork() {

        // limpar antigas
        _questoes.clear();

        // adicionar novas e alinhá-las
        List<Quesito> quesitos = _prova.getQuesitosPermutados();
        int numQuesito = 1;
        for (Quesito q : quesitos) {
            if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS)
                _questoes.add(new FRQuesitoAlternativas(numQuesito++, q.getItensCount()));
            else if (q.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO)
                _questoes.add(new FRQuesitoVerdadeiroFalso(numQuesito++, q.getItensCount()));
            else if (q.getTipo() == Quesito.TIPO_NUMERICO_99)
                _questoes.add(new FRNumerico(numQuesito++, 2));
            else if (q.getTipo() == Quesito.TIPO_SUBJETIVA_5)
                _questoes.add(new FRSubjetiva(numQuesito++, 5));
        }

        double y1 = _id.getY1() + GAP_CONTROLO_QUESTOES; //
        double y2 = y1; //

        // _id.setPosition();
        int column = 0;
        int i = 0;
        double U = WIDTH_UTIL_COLUMN;
        while (i < _questoes.size()) {
            int b = i; // base index
            int l = b - 1; // max i such that sum_{k=b}^{i} <= U
            double wacum = 0;
            double maxh = 0;
            while (i < _questoes.size()) {
                FRBloco q = _questoes.get(i);

                if (wacum + q.getWidth() <= U)
                    l++;
                else
                    break; // line overflow

                maxh = Math.max(maxh, q.getHeight());
                wacum += q.getWidth();
                i++;
            }

            if (l < b)
                throw new RuntimeException("Problema");

            double xx = (column == 0 ? X0_COLUMN_1 : X0_COLUMN_2) + (U - wacum) / 2.0f;
            for (int k = b; k <= l; k++) {
                FRBloco q = _questoes.get(k);
                q.setPosition(xx, (column == 0 ? y1 : y2));
                xx += q.getWidth();
            }

            if (column == 0)
                y1 += maxh + 2;
            else
                y2 += maxh + 2;

            column = (column + 1) % 2;
        }

        // inicializar CellMap
        try {
            _cellMapVariavel = new CellMap(X0, Y0, WIDTH, HEIGHT);
            for (FRBloco b : _questoes)
                b.addToCellMap(_cellMapVariavel);
        }
        catch (CellMapException ex) {
            ex.printStackTrace();
        }

    }

    public CellMap getCellMapFixo() {
        return _cellMapFixo;
    }

    public CellMap getCellMapVariavel() {
        return _cellMapVariavel;
    }

    public static void main(String[] args) throws Exception {

        // Controller.unzip("C:/mixnfix/clientes/SaoLuis/2005/Fisica(Mecanica&Eletrica030502-2oAno).prova", Controller.TMP_DIR);
        Controller.unzip("C:/mixnfix/clientes/SaoLuis/2004-ano3-simulado4/PROVA4MATFISQUIBIOAB3OS2004.prova", Controller.TMP_DIR);
        // Controller.unzip("C:/mixnfix/clientes/SaoLuis/2004-ano3-simulado3/biologia-exatas/PROVA3BIOLE3OS2004.prova", Controller.TMP_DIR);
        // Controller.unzip("C:/workspace/mnfimg/c/x.prova", Controller.TMP_DIR);
        mixnfix.prova.Parser parser = new mixnfix.prova.Parser(Controller.TMP_DIR + "/prova.xml");
        ProvaStructure prova = parser.getProva();
        // prova.printXML(System.out);
        prova.permute(0);

        GeradorFolhaRespostas g = new GeradorFolhaRespostas(prova);

        // MultiCampo id = g.getCellMap().getMultiCampo(CellMap.MULTICAMPO_ID);
        //id.setValor(new int[] {0,2,5,6,7,0,6,2,4,7,5});

        PrintWriter pw = new PrintWriter(new FileOutputStream("c:/workspace/mnfimg/c/img/fr1.eps"));

        g.writeEPSwithValues(pw);
        pw.close();

        CellMap map = new CellMap(X0, Y0, WIDTH, HEIGHT);
        // g.addToCellMap(map);
        map = g.getCellMapFixo();

        CellMapEditor f = new CellMapEditor(map);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0, 0, 800, 800);
        f.setVisible(true);
    }

    public static void writeEPSControlPoint(PrintWriter pw, double x, double y) {
        pw.println("newpath");
        pw.println(String.format("%.4f %.4f moveto", x - CP_SIZE / 2.0, y - CP_SIZE / 2.0));
        pw.println(String.format("%.4f %.4f lineto", x + CP_SIZE / 2.0, y - CP_SIZE / 2.0));
        pw.println(String.format("%.4f %.4f lineto", x + CP_SIZE / 2.0, y + CP_SIZE / 2.0));
        pw.println(String.format("%.4f %.4f lineto", x - CP_SIZE / 2.0, y + CP_SIZE / 2.0));
        pw.println("closepath");
        pw.println("0 0 0 setrgbcolor");
        pw.println("gsave fill grestore");
        pw.println("0 0 0 setrgbcolor");
        pw.println("stroke");
    }

    public void writeEPS(PrintWriter pw) throws IOException {
        double CONVERSION = 72f / 25.4f;

        int W = (int) (CONVERSION * PAPER_WIDTH);
        int H = (int) (CONVERSION * PAPER_HEIGHT);

        pw.println("%!PS-Adobe-3.0 EPSF-3.0");
        pw.println(String.format("%%%%BoundingBox: 0 0 %d %d", W, H));
        pw.println("/HelveticaItalic findfont dup length dict begin { 1 index /FID ne {def} {pop pop} ifelse} forall /Encoding ISOLatin1Encoding def currentdict end /HelveticaItalic-ISOLatin1 exch definefont pop"); // install ISOLatinEncoding
        pw.println("0.1 setlinewidth");
        pw.println(String.format("0 %d translate", H));
        pw.println(String.format("%.4f %.4f scale", CONVERSION, -CONVERSION));

        writeEPSControlPoint(pw, X0, Y0);
        writeEPSControlPoint(pw, X0 + WIDTH, Y0);
        writeEPSControlPoint(pw, X0 + WIDTH, Y0 + HEIGHT);
        writeEPSControlPoint(pw, X0, Y0 + HEIGHT);
        writeEPSControlPoint(pw, X0 + WIDTH / 2.0f, Y0);
        writeEPSControlPoint(pw, X0 + WIDTH / 2.0f, Y0 + HEIGHT);

        _codigoMF.writeEPS(pw);
        _id.writeEPS(pw);

        for (FRBloco b : _controles)
            b.writeEPS(pw);

        for (FRBloco b : _questoes)
            b.writeEPS(pw);
    }

    public void writeEPSwithValues(PrintWriter pw) throws IOException {
        this.writeEPS(pw);

        /** @todo por as marcações */
        for (Cell cell : _cellMapFixo.getCells()) {
            if (cell.isSelected()) {
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", cell.getX(), cell.getY(), (CELL_DIAMETER / 2.0f)));
                pw.println("0 setgray");
                pw.println("fill");
            }
        }

        for (Cell cell : _cellMapVariavel.getCells()) {
            if (cell.isSelected()) {
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", cell.getX(), cell.getY(), (CELL_DIAMETER / 2.0f)));
                pw.println("0 setgray");
                pw.println("fill");
            }
        }
    }

    abstract class FRBloco {
        private double _x0;
        private double _y0;
        private double _width;
        private double _height;
        public double getX0() {
            return _x0;
        }

        public double getY0() {
            return _y0;
        }

        public double getX1() {
            return _x0 + _width;
        }

        public double getY1() {
            return _y0 + _height;
        }

        public void setPosition(double x0, double y0) {
            _x0 = x0;
            _y0 = y0;
        }

        public double getWidth() {
            return _width;
        }

        public double getHeight() {
            return _height;
        }

        public void setDimensions(double width, double height) {
            _width = width;
            _height = height;
        }

        public abstract void writeEPS(PrintWriter pw) throws IOException;

        public abstract void addToCellMap(CellMap map) throws CellMapException;
    }

    class FRIdentificador
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters

        private static final double TEXT_HORIZONTAL_LENGTH = 2.0f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final double HORIZONTAL_GAP = 1.0f; // milimeters

        private static final double VERTICAL_GAP = 2.0f; // milimeters

        private static final double TEXT_HEIGHT = 3.0f; // milimeters

        private static final double TEXT_HEIGHT_ADJUST = 0.5f; // milimeters

        private static final int ROWS = 10; // milimeters

        private int _numDigitosDecimais;
        public FRIdentificador(int numDigitosDigitosDecimais) {
            _numDigitosDecimais = numDigitosDigitosDecimais;

            // existe uma margem de BOX_MARGIN na esquerda e na direita
            int C = _numDigitosDecimais;
            double w = BOX_MARGIN * 2 + C * (CELL_DIAMETER + TEXT_HORIZONTAL_LENGTH) + (C - 1) * HORIZONTAL_GAP;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + CELL_DIAMETER * ROWS + (ROWS - 1) * VERTICAL_GAP;

            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.5 setgray");
            pw.println("stroke");

            for (int j = 0; j < _numDigitosDecimais; j++) {
                for (int i = 0; i < ROWS; i++) {
                    double xt = getX0() + BOX_MARGIN + j * (HORIZONTAL_GAP + TEXT_HORIZONTAL_LENGTH + CELL_DIAMETER);
                    double yt = getY0() + BOX_MARGIN + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

                    pw.println("gsave");
                    pw.println(String.format("%.4f %.4f translate", xt, yt));
                    pw.println(String.format("/Helvetica findfont %.4f scalefont setfont", TEXT_HEIGHT));
                    pw.println("1 -1 scale 0 0 moveto");
                    pw.println("(" + i + ") show");
                    pw.println("grestore");

                    double xc = getCenterCellX(j);
                    double yc = getCenterCellY(i);
                    pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                    pw.println("0.3 0.3 0.3 setrgbcolor");
                    pw.println("stroke");
                }
            }

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", this.getX0() + this.getWidth() / 2.0f, this.getY0() - 2f));
            pw.println(String.format("/HelveticaItalic-ISOLatin1 findfont %.4f scalefont setfont", 5.0f));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("0.5 setgray");
            pw.println("(IDENTIFICA\\307\\303O ALUNO) dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");
        }

        public double getCenterCellX(int j) {
            return getX0() + BOX_MARGIN + j * (HORIZONTAL_GAP + TEXT_HORIZONTAL_LENGTH + CELL_DIAMETER) + (TEXT_HORIZONTAL_LENGTH + CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo
            MultiField mf = map.addMultiField(null, CellMap.MULTICAMPO_ID);
            ArrayList<Cell> cells = new ArrayList<Cell> ();
            for (int j = 0; j < _numDigitosDecimais; j++) {
                for (int i = 0; i < ROWS; i++) {
                    cells.add(map.addCell(getCenterCellX(j), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER));
                }
                OptionField of = map.addOptionField(mf, CellMap.MULTICAMPO_ID + "." + j);
                for (Cell c : cells) {
                    of.addCell(c);
                }
                cells.clear();
            }
        }

    }

    class FRCodigoMixNFix
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters
        private static final double CELL_HORIZONTAL_GAP = 2.0f; // milimeters
        private static final double CELL_VERTICAL_GAP = 2.0f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final int ROWS = 10; // milimeters
        private static final int COLS = 10; // milimeters

        public FRCodigoMixNFix() {
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double w = BOX_MARGIN * 2 + CELL_DIAMETER * COLS + (COLS - 1) * CELL_HORIZONTAL_GAP;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + CELL_DIAMETER * ROWS + (ROWS - 1) * CELL_VERTICAL_GAP;
            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.5 setgray");
            pw.println("stroke");

            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    double xc = getCenterCellX(j);
                    double yc = getCenterCellY(i);
                    pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                    pw.println("0.3 0.3 0.3 setrgbcolor");
                    pw.println("stroke");
                }
            }

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", this.getX0() + this.getWidth() / 2.0f, this.getY0() - 2f));
            pw.println(String.format("/HelveticaItalic-ISOLatin1 findfont %.4f scalefont setfont", 5.0f));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("0.5 setgray");
            pw.println("(CONTROLE MIXNFIX) dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");

        }

        public double getCenterCellX(int j) {
            return getX0() + BOX_MARGIN + j * (CELL_HORIZONTAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + i * (CELL_VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo
            BinaryField bf = map.addBinaryField(null, CellMap.MULTICAMPO_CODIGO_MIXNFIX);
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    Cell cell = map.addCell(getCenterCellX(j), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER);
                    bf.addCell(cell);
                }
            }
        }
    }

    class FRQuesitoAlternativas
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters

        private static final double VERTICAL_HEADER = 2.0f; // milimeters

        private static final double TEXT_HORIZONTAL_LENGTH = 2.5f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final double HORIZONTAL_GAP = 1.0f; // milimeters

        private static final double VERTICAL_GAP = 2.0f; // milimeters

        private static final double TEXT_HEIGHT = 3.0f; // milimeters

        private static final double TEXT_HEIGHT_ADJUST = 0.5f; // milimeters

        private int _numQuesito;
        private int _numAlternativas;

        public FRQuesitoAlternativas(int numQuesito, int numAlternativas) {
            _numQuesito = numQuesito;
            _numAlternativas = numAlternativas;

            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double w = BOX_MARGIN * 2 + CELL_DIAMETER + TEXT_HORIZONTAL_LENGTH;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + VERTICAL_HEADER + CELL_DIAMETER * numAlternativas + numAlternativas * VERTICAL_GAP;

            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.3 setgray");
            pw.println("stroke");

            // print numero do quesito
            double xh = getX0() + this.getWidth() / 2.0f;
            double yh = getY0() + BOX_MARGIN + (VERTICAL_HEADER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", xh, yh));
            pw.println(String.format("/HelveticaBold findfont %.4f scalefont setfont", TEXT_HEIGHT));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("(" + _numQuesito + ") dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");

            for (int i = 0; i < _numAlternativas; i++) {
                double xt = getX0() + BOX_MARGIN;
                double yt = getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

                pw.println("gsave");
                pw.println(String.format("%.4f %.4f translate", xt, yt));
                pw.println(String.format("/Helvetica findfont %.4f scalefont setfont", TEXT_HEIGHT));
                pw.println("1 -1 scale 0 0 moveto");
                pw.println("(" + (char) (Character.valueOf('A') + i) + ") show");
                pw.println("grestore");

                double xc = getCenterCellX();
                double yc = getCenterCellY(i);
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                pw.println("0.3 0.3 0.3 setrgbcolor");
                pw.println("stroke");
            }
        }

        public double getCenterCellX() {
            return getX0() + BOX_MARGIN + (TEXT_HORIZONTAL_LENGTH + CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo
            OptionField of = map.addOptionField(null, "" + _numQuesito);
            for (int i = 0; i < _numAlternativas; i++) {
                Cell cell = map.addCell(getCenterCellX(), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER);
                of.addCell(cell);
            }
        }

    }

    class FRSubjetiva
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters

        private static final double VERTICAL_HEADER = 2.0f; // milimeters

        private static final double TEXT_HORIZONTAL_LENGTH = 6f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final double HORIZONTAL_GAP = 1.0f; // milimeters

        private static final double VERTICAL_GAP = 2.0f; // milimeters

        private static final double TEXT_HEIGHT = 3.0f; // milimeters

        private static final double TEXT_HEIGHT_ADJUST = 0.5f; // milimeters

        private int _numQuesito;
        private int _numAlternativas;

        public FRSubjetiva(int numQuesito, int numAlternativas) {
            _numQuesito = numQuesito;
            _numAlternativas = numAlternativas;

            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double w = BOX_MARGIN * 2 + CELL_DIAMETER + TEXT_HORIZONTAL_LENGTH;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + VERTICAL_HEADER + CELL_DIAMETER * numAlternativas + numAlternativas * VERTICAL_GAP;

            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.3 setgray");
            pw.println("stroke");

            // print numero do quesito
            double xh = getX0() + this.getWidth() / 2.0f;
            double yh = getY0() + BOX_MARGIN + (VERTICAL_HEADER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", xh, yh));
            pw.println(String.format("/HelveticaBold findfont %.4f scalefont setfont", TEXT_HEIGHT));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("(" + _numQuesito + " Prof.) dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");

            for (int i = 0; i < _numAlternativas; i++) {
                double xt = getX0() + BOX_MARGIN;
                double yt = getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

                pw.println("gsave");
                pw.println(String.format("%.4f %.4f translate", xt, yt));
                pw.println(String.format("/Helvetica findfont %.4f scalefont setfont", TEXT_HEIGHT));
                pw.println("1 -1 scale 0 0 moveto");
                pw.println("(" + i + "/" + (_numAlternativas - 1) + ") show");
                pw.println("grestore");

                double xc = getCenterCellX();
                double yc = getCenterCellY(i);
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                pw.println("0.3 0.3 0.3 setrgbcolor");
                pw.println("stroke");
            }
        }

        public double getCenterCellX() {
            return getX0() + BOX_MARGIN + (TEXT_HORIZONTAL_LENGTH + CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo
            OptionField of = map.addOptionField(null, "" + _numQuesito);
            for (int i = 0; i < _numAlternativas; i++) {
                Cell cell = map.addCell(getCenterCellX(), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER);
                of.addCell(cell);
            }
        }

    }

    class FRQuesitoVerdadeiroFalso
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters

        private static final double VERTICAL_HEADER = 2.0f; // milimeters

        private static final double TEXT_HORIZONTAL_LENGTH = 2.5f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final double HORIZONTAL_GAP = 1.0f; // milimeters

        private static final double CELL_GAP = 1.0f; // milimeters

        private static final double VERTICAL_GAP = 2.0f; // milimeters

        private static final double TEXT_HEIGHT = 3.0f; // milimeters

        private static final double TEXT_HEIGHT_ADJUST = 0.5f; // milimeters

        private int _numQuesito;
        private int _numAlternativas;

        public FRQuesitoVerdadeiroFalso(int numQuesito, int numAlternativas) {
            _numQuesito = numQuesito;
            _numAlternativas = numAlternativas;

            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double w = BOX_MARGIN * 2 + 2 * CELL_DIAMETER + CELL_GAP + TEXT_HORIZONTAL_LENGTH;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + VERTICAL_HEADER + CELL_DIAMETER * numAlternativas + numAlternativas * VERTICAL_GAP;

            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.3 setgray");
            pw.println("stroke");

            // print numero do quesito
            double xh = getX0() + this.getWidth() / 2.0f;
            double yh = getY0() + BOX_MARGIN + (VERTICAL_HEADER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", xh, yh));
            pw.println(String.format("/HelveticaBold findfont %.4f scalefont setfont", TEXT_HEIGHT));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("(" + _numQuesito + " V-F) dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");

            for (int i = 0; i < _numAlternativas; i++) {
                double xt = getX0() + BOX_MARGIN;
                double yt = getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

                pw.println("gsave");
                pw.println(String.format("%.4f %.4f translate", xt, yt));
                pw.println(String.format("/Helvetica findfont %.4f scalefont setfont", TEXT_HEIGHT));
                pw.println("1 -1 scale 0 0 moveto");
                pw.println("(" + (char) (Character.valueOf('A') + i) + ") show");
                pw.println("grestore");

                double xc = getCenterCellX(0);
                double yc = getCenterCellY(i);
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                pw.println("0.3 0.3 0.3 setrgbcolor");
                pw.println("stroke");

                xc = getCenterCellX(1);
                pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                pw.println("0.3 0.3 0.3 setrgbcolor");
                pw.println("stroke");
            }
        }

        public double getCenterCellX(int j) {
            return getX0() + BOX_MARGIN + (TEXT_HORIZONTAL_LENGTH + j * (CELL_DIAMETER + CELL_GAP) + CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo

            MultiField mf = map.addMultiField(null, "" + _numQuesito);
            for (int i = 0; i < _numAlternativas; i++) {
                OptionField of = map.addOptionField(mf, "" + _numQuesito + "." + i);
                of.addCell(map.addCell(getCenterCellX(0), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER));
                of.addCell(map.addCell(getCenterCellX(1), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER));
            }
        }

    }

    class FRNumerico
        extends FRBloco {
        private static final double BOX_MARGIN = 1f; // milimeters

        private static final double VERTICAL_HEADER = 2.0f; // milimeters

        private static final double TEXT_HORIZONTAL_LENGTH = 2.5f; // milimeters
        private static final double CELL_DIAMETER = GeradorFolhaRespostas.CELL_DIAMETER; // milimeters
        private static final double HORIZONTAL_GAP = 1.0f; // milimeters

        private static final double CELL_GAP = 1.0f; // milimeters

        private static final double VERTICAL_GAP = 2.0f; // milimeters

        private static final double TEXT_HEIGHT = 3.0f; // milimeters

        private static final double TEXT_HEIGHT_ADJUST = 0.5f; // milimeters

        private int _numQuesito;
        private int _numDigitosDecimais;

        public FRNumerico(int numQuesito, int numDigitosDecimais) {
            _numQuesito = numQuesito;
            _numDigitosDecimais = numDigitosDecimais;

            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double w = BOX_MARGIN * 2 + numDigitosDecimais * CELL_DIAMETER + (numDigitosDecimais - 1) * CELL_GAP + TEXT_HORIZONTAL_LENGTH;
            // existe uma margem de BOX_MARGIN na esquerda e na direita
            double h = BOX_MARGIN * 2 + VERTICAL_HEADER + CELL_DIAMETER * 10 + 10 * VERTICAL_GAP;

            this.setDimensions(w, h);
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            pw.println("newpath");
            pw.println(String.format("%.4f %.4f moveto", getX0(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0()));
            pw.println(String.format("%.4f %.4f lineto", getX0() + this.getWidth(), getY0() + this.getHeight()));
            pw.println(String.format("%.4f %.4f lineto", getX0(), getY0() + this.getHeight()));
            pw.println("closepath");
            pw.println(String.format("%.4f setgray", GeradorFolhaRespostas.GRAY_FILL));
            pw.println("gsave fill grestore");
            pw.println("0.3 setgray");
            pw.println("stroke");

            // print numero do quesito
            double xh = getX0() + this.getWidth() / 2.0f;
            double yh = getY0() + BOX_MARGIN + (VERTICAL_HEADER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

            pw.println("gsave");
            pw.println(String.format("%.4f %.4f translate", xh, yh));
            pw.println(String.format("/HelveticaBold findfont %.4f scalefont setfont", TEXT_HEIGHT));
            pw.println("1 -1 scale 0 0 moveto");
            pw.println("(" + _numQuesito + ") dup stringwidth pop 2 div neg 0 rmoveto show");
//        pw.println("("+(char)(Character.valueOf('A')+i)+") dup stringwidth pop 2 div neg 0 rmoveto show");
            pw.println("grestore");

            for (int j = 0; j < _numDigitosDecimais; j++) {
                for (int i = 0; i < 10; i++) {
                    if (j == 0) { // write label at row
                        double xt = getX0() + BOX_MARGIN;
                        double yt = getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER - TEXT_HEIGHT) / 2.0f + (TEXT_HEIGHT - TEXT_HEIGHT_ADJUST);

                        pw.println("gsave");
                        pw.println(String.format("%.4f %.4f translate", xt, yt));
                        pw.println(String.format("/Helvetica findfont %.4f scalefont setfont", TEXT_HEIGHT));
                        pw.println("1 -1 scale 0 0 moveto");
                        pw.println("(" + i + ") show");
                        pw.println("grestore");
                    }
                    double xc = getCenterCellX(j);
                    double yc = getCenterCellY(i);
                    pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath", xc, yc, (CELL_DIAMETER / 2.0f)));
                    pw.println("0.3 0.3 0.3 setrgbcolor");
                    pw.println("stroke");
                }
            }
        }

        public double getCenterCellX(int j) {
            return getX0() + BOX_MARGIN + (TEXT_HORIZONTAL_LENGTH + j * (CELL_DIAMETER + CELL_GAP) + CELL_DIAMETER / 2.0f);
        }

        public double getCenterCellY(int i) {
            return getY0() + BOX_MARGIN + (VERTICAL_HEADER + VERTICAL_GAP) + i * (VERTICAL_GAP + CELL_DIAMETER) + (CELL_DIAMETER / 2.0f);
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            // Multi Campo
            MultiField mf = map.addMultiField(null, "" + _numQuesito);
            for (int j = 0; j < _numDigitosDecimais; j++) {
                OptionField of = map.addOptionField(mf, _numQuesito + "." + j);
                for (int i = 0; i < 10; i++)
                    of.addCell(map.addCell(getCenterCellX(j), getCenterCellY(i), CELL_DIAMETER, CELL_DIAMETER));
            }
        }
    }

    class FRColunaControle
        extends FRBloco {
        public static final int CENTER_ALIGNMENT = 1;
        public static final int LEFT_ALIGNMENT = 2;
        public static final int RIGHT_ALIGNMENT = 3;
        private int _alignment;
        private double _phase;
        private double _gap;
        private static final double CP_SIZE = GeradorFolhaRespostas.CP_SIZE; // milimeters

        public FRColunaControle(double w, double h, double phase, double gap, int align) {
            this.setDimensions(w, h);
            _phase = phase;
            _gap = gap;
            _alignment = align;
        }

        public void writeEPS(PrintWriter pw) throws IOException {
            double y = _phase;
            while (y < this.getHeight()) {
                double xx = (_alignment == CENTER_ALIGNMENT ?
                            getX0() + getWidth() / 2.0f :
                            (_alignment == LEFT_ALIGNMENT ?
                             getX0() :
                             getX0() + getWidth()));
                GeradorFolhaRespostas.writeEPSControlPoint(pw, xx, getY0() + y + CP_SIZE / 2.0f);
                y += _gap;
            }
        }

        public void addToCellMap(CellMap map) throws CellMapException {
            double y = _phase;
            while (y < this.getHeight()) {
                double xx = (_alignment == CENTER_ALIGNMENT ?
                            getX0() + getWidth() / 2.0f :
                            (_alignment == LEFT_ALIGNMENT ?
                             getX0() :
                             getX0() + getWidth()));
                map.addControlPoint(xx, getY0() + y + CP_SIZE / 2.0f);
                y += _gap;
            }
        }
    }
}

