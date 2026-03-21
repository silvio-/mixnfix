package mixnfix.composicaoprova;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import mixnfix.Library;
import mixnfix.folharesposta.IFolhaResposta;
import mixnfix.prova.Estilo;
import mixnfix.prova.Formula;
import mixnfix.prova.Grupo;
import mixnfix.prova.Imagem;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.No;
import mixnfix.prova.NoPapel;
import mixnfix.prova.NoProva;
import mixnfix.prova.Papel;
import mixnfix.prova.ProvaStructure;
import mixnfix.prova.Quesito;
import mixnfix.prova.Texto;


/**
 *
 */
public class Questionario2TeX {
    ProvaStructure _prova;
    ParametrosDeLayoutDaProva _params;
    String _workdir;

    int _tipoFolhaResposta = mixnfix.folharesposta.FolhaRespostaQuestionarioBasico.ID;

    public Questionario2TeX(ProvaStructure p, PrintStream ps, ParametrosDeLayoutDaProva params, String workdir) throws Exception {
        _prova = p;
        _params = params;
        _workdir = workdir;
        gerarQuestionario(p,ps);
    }

    public void gerarQuestionario(ProvaStructure p, PrintStream ps) throws Exception {
    	//LATEX HEADERS
        ps.println("\\documentclass[a4paper,"+_params.getFontSize()+"pt]{article}");
        ps.println("\\usepackage[portuges]{babel}");
        ps.println("\\usepackage[utf8]{inputenc}");
        ps.println("\\usepackage{amsfonts,amssymb}");
        ps.println("\\usepackage[dvips,pdftex]{color}");
        ps.println("\\usepackage{multicol}");
        ps.println("\\usepackage[pdftex]{graphicx}");
        ps.println(String.format("\\usepackage[top=1.5cm,bottom=1.5cm,left=1.5cm,right=1.5cm,papersize={21.00cm,29.70cm},includehead=true]{geometry}"));
        /*
        ps.println(String.format(Locale.ENGLISH,"\\usepackage[top=%.2fcm,bottom=%.2fcm,left=%.2fcm,right=%.2fcm,papersize={%.2fcm,%.2fcm},includehead=true]{geometry}",
                   _params.getTopMargin(),
                   _params.getBottomMargin(),
                   _params.getLeftMargin(),
                   _params.getRightMargin(),
                   _params.getPaperWidth(),
                   _params.getPaperHeight()));*/

        // paragraph
        ps.println("\\setlength{\\parindent}{0cm}");
        ps.println("\\setlength{\\parskip}{0.2cm}");
        ps.println("\\setlength{\\columnsep}{0.6cm}");
        ps.println("\\setlength{\\columnseprule}{0pt}");

        // Sans Serif font
        ps.println("\\renewcommand{\\familydefault}{cmss}");

        // fancy header
        ps.println("\\usepackage{fancyhdr}");
        ps.println("\\pagestyle{fancy}");
        ps.println("\\fancyhf{}");
        ps.println("\\fancyhead[L]{\\textcolor{blue}{}}");
        ps.println("\\fancyhead[R]{\\textcolor{black}{\\small pg.\\thepage, {\\sl documento gerado pelo MIXnFIX}}}");
        ps.println("\\fancyhead[C]{\\textcolor{black}{}}");
        ps.println("\\renewcommand{\\headrule}{}");

        ps.println("\\def\\IR{\\mathbb R}"); // isso nao deveria precisar!
        ps.println("\\everymath{\\displaystyle}"); // isso nao deveria precisar!

        // mapa de itens
        HashMap<Object,Integer> mapElementToIndex = new HashMap<Object,Integer>();

        int index = 0;
        LinkedList<No> L = new LinkedList<No>();
        L.addLast(p);
        while (!L.isEmpty()) {
            No n = L.removeFirst();
            if (n instanceof ProvaStructure) {
                ProvaStructure prova = (ProvaStructure) n;

                ps.println("\\newcommand{\\Item"+(intToSymbol(index))+"}{");
                printPapel(ps,prova.getCabecalho());
                ps.println("}");
                mapElementToIndex.put(prova.getCabecalho(),index++); // use index

                L.addLast(prova.getRoot());
            }

            if (n instanceof Grupo) {
                Grupo grupo = (Grupo) n;

                ps.println("\\newcommand{\\Item"+(intToSymbol(index))+"}{");
                printPapel(ps,grupo.getEnunciado());
                ps.println("}");
                mapElementToIndex.put(grupo.getEnunciado(),index++); // use index

                for (NoProva np : grupo.getChilds()) {
                    L.addLast(np); // push NoProva to Stack
                }
            }

            if (n instanceof Quesito) {
                Quesito quesito = (Quesito) n;

                ps.println("\\newcommand{\\Item"+(intToSymbol(index))+"}{");
                printPapel(ps,quesito.getEnunciado());
                if (_params.colocarValorDaQuestao()) {
                    ps.println(String.format("\\hfill \\textcolor{green}{(%.3f, %.3f)}", quesito.getValorAcerto(), quesito.getValorFalha()));
                }
                ps.println("}");
                mapElementToIndex.put(quesito.getEnunciado(),index++); // use index

                ps.println("\\newcommand{\\Item"+(intToSymbol(index))+"}{");
                printPapel(ps,quesito.getSolucao());
                ps.println("}");
                mapElementToIndex.put(quesito.getSolucao(),index++); // use index

                ps.println("");
                for (NoProva np : quesito.getChilds()) {
                    L.addLast(np); // push NoProva to Stack
                }
            }

            if (n instanceof ItemQuesito) {
                ItemQuesito itemQuesito = (ItemQuesito) n;

                ps.println("\\newcommand{\\Item"+(intToSymbol(index))+"}{");
                printPapel(ps,itemQuesito.getEnunciado());
                ps.println("}");
                mapElementToIndex.put(itemQuesito.getEnunciado(),index++); // use index

            }
        }

        ps.println("\\newcounter{quesito}"); // set counter for quesitos

        // beginning of document
        ps.println("\\begin{document}");

        //
        IFolhaResposta g = mixnfix.folharesposta.FabricaDeFolhaDeResposta.newFolhaResposta(p, this._tipoFolhaResposta);

        // System.out.println(String.format("%x %x %x",valor,codigo,valor2));

        ProvaStructure.permuteProva(p, 0);

        ps.println("\\setcounter{quesito}{0}");
        ps.println("\\setcounter{page}{0}");

        for (int kk=0;kk<_params.getNumFolhasRespostas();kk++) {
            produceFolhaResposta(p, ps, mapElementToIndex);
            ps.println("\\clearpage");
        }

        if (_params.getPaginaEmBrancoAposFolhaResposta())
            ps.println("\\thispagestyle{empty}{\\hbox{}}\\newpage");

        if (_params.getFontSize() > 12) {
            ps.println("\\begin{LARGE}");
        }

        // set first content page to be 1
        ps.println("\\setcounter{page}{1}");

        if (_params.getNumColumns() > 1)
            ps.println("\\begin{multicols}{" + _params.getNumColumns() + "}");
        produce(p, ps, mapElementToIndex);
        if (_params.getNumColumns() > 1)
            ps.println("\\end{multicols}");

        if (_params.getFontSize() > 12) {
            ps.println("\\end{LARGE}");
        }

        ps.println("\\newpage");

        for (int kk = 0; kk < _params.getNumeroDePaginasEmBrancoNoFim(); kk++)
            ps.println("\\thispagestyle{empty}{\\hbox{}}\\eject\\clearpage");


        // end of document
        ps.println("\\end{document}");

    }

    public void produceFolhaResposta(ProvaStructure p, PrintStream ps, HashMap<Object,Integer> mapElementToIndex) throws Exception {
        IFolhaResposta g = mixnfix.folharesposta.FabricaDeFolhaDeResposta.newFolhaResposta(p,this._tipoFolhaResposta);

        // inicializar código mixnfix
        g.inicializarQuesitos(0);
        g.getCellMapFixo().clearAllCells();
        g.getCellMapVariavel().clearAllCells();
        // inicializar campo codigo mixnfix

        System.out.println("Writing EPS");
        PrintWriter pw = new PrintWriter(_workdir+"g.eps");
        g.writeEPS(pw);
        pw.close();

        ps.println("\\thispagestyle{empty}");

        System.out.println("Converting to EPS to PDF ");
        Library.executeCommand("epstopdf \""+_workdir+"g.eps\"",true);
        ps.println("\\raisebox{-26.5cm}[0cm][0cm]{\\makebox[\\textwidth]{\\includegraphics[scale=1]{g.pdf}\\kern-21cm\\Item"+intToSymbol(mapElementToIndex.get(p.getCabecalho()))+"}}");
        // ps.println("\\Item" + intToSymbol(mapElementToIndex.get(p.getCabecalho())));

        // ps.println("\\clearpage");
    }

    private void produce(No n, PrintStream ps, HashMap<Object,Integer> mapElementToIndex) throws IOException {

        if (n instanceof ProvaStructure) {
            ProvaStructure prova = (ProvaStructure) n;
            produce(prova.getRoot(),ps,mapElementToIndex); // recursive call
        }

        if (n instanceof Grupo) {
            Grupo grupo = (Grupo) n;

            ps.println("\\Item"+intToSymbol(mapElementToIndex.get(grupo.getEnunciado())));

            for (NoProva np : grupo.getPermutacao()) {
                produce(np,ps,mapElementToIndex); // recursive call
            }
        }

        if (n instanceof Quesito) {
            Quesito quesito = (Quesito) n;

            // ps.println("\\begin{minipage}{7cm}");
            ps.println("\\begin{enumerate}");
            ps.println("\\renewcommand{\\labelenumi}{{\\Large \\bf \\arabic{enumi}.}}");
            ps.println("\\setcounter{enumi}{\\value{quesito}}");
            ps.println("\\item \\Item"+intToSymbol(mapElementToIndex.get(quesito.getEnunciado())));

            if (_params.colocarGabaraito()) {
                String gabarito = quesito.getTextRespostaCorretaWithinPermutation();
                gabarito = gabarito.replaceAll("_","x");
                ps.println("\n\n \\smallskip \\centerline{\\large Resposta: "+gabarito+"} \\smallskip" );
            }

            if (quesito.getItensCount() > 0) {
                ps.println("\\begin{enumerate}");
                ps.println("\\renewcommand{\\labelenumii}{({\\tt \\Alph{enumii}})}");
                ps.println("\\setcounter{enumi}{0}");
                for (NoProva np : quesito.getPermutacao()) {
                    ItemQuesito iq = (ItemQuesito) np;
                    ps.println("\\item \\Item"+intToSymbol(mapElementToIndex.get(iq.getEnunciado())));
                }
                ps.println("\\end{enumerate}");
            }
            ps.println("\\end{enumerate}");
            //ps.println("\\end{minipage}");
            ps.println("\\addtocounter{quesito}{1}");

            if (_params.goodbreak()) {
                ps.println("\\goodbreak");
            }

            // ps.println("\\goodbreak");
            ps.println("");
        }
    }

    public static void printPapel(PrintStream ps, Papel p) {
        if (p == null)
            return;

        for (NoPapel np: p.getContents()) {
            if (np instanceof Texto) {
                Texto t = (Texto) np;
                ps.print(texEncode(t.getTexto(),true));
            }
            else if (np instanceof Formula) {
                Formula f = (Formula) np;
                String modo = np.getProperty("modo");
                if (modo != null && "tex".equals(modo))
                    ps.print(texEncode(f.getTexto(),false));
                else
                    ps.print("$" + texEncode(f.getTexto(),false) + "$");
            }
            else if (np instanceof Estilo) {
                Estilo e = (Estilo) np;
                if (e.isBold()) {
                    ps.print("{\\bf ");
                    ps.print(texEncode(e.getTexto(), true));
                    ps.print("}");
                }
                else if (e.isItalic()) {
                    ps.print("{\\it ");
                    ps.print(texEncode(e.getTexto(), true));
                    ps.print("}");
                }
                else ps.print(texEncode(e.getTexto(), true));
            }
            else if (np instanceof Imagem) {
                Imagem imagem = (Imagem) np;
                File f = new File(np.getProperty("src"));
                double scale = 1;
                String str = (String) np.getProperty("zoom");
                if(str != null) {
                    scale = Double.parseDouble(str);
                }
                ps.print("\\includegraphics[scale="+ scale + "]{" + f.getName() + "}");
            }
        }
    }

    private static final char[] numbers = {'A','B','C','D','E','F','G','H','I','J'};
    public static String intToSymbol(int i) {
        String st = "";
        while (i != 0) {
            int lastDigit = i % 10;
            i = i / 10;
            st = numbers[lastDigit] + st;
        }
        if (st.equals(""))
            st = "A";
        return st;
    }

    private static String texEncode(String s, boolean textMode) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            String str = "" + c;

            if (textMode) {
                if (c == '\\') {
                    str = "$\\backslash$";
                }
                else if (c == '$') {
                    str = "\\$";
                }
                else if (c == '%') {
                    str = "\\%";
                }
                else if (c == '&') {
                    str = "\\&";
                }
                else if (c == '#') {
                    str = "\\#";
                }
                else if (c == '_') {
                    str = "\\_";
                }
                else if (c == '{') {
                    str = "\\{";
                }
                else if (c == '}') {
                    str = "\\}";
                }
                else if (c == '^') {
                    str = "\\^";
                }
                else if (c == '~') {
                    str = "$\\sim$";
                }
            }
            b.append(str);
        }
        return b.toString();
    }

    /**
     * Matriz para gerar o código.
     */
    private static int[][] MATRIZ_B = {
        { 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1 },
        { 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0 },
        { 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 0, 1 },
        { 1, 0, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0 },
        { 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0 },
        { 1, 0, 1, 0, 1, 1, 1, 0, 0, 1, 0, 1 },
        { 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1, 0 },
        { 1, 0, 0, 1, 1, 0, 1, 1, 1, 0, 0, 1 },
        { 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 1 },
        { 1, 0, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0 },
        { 0, 1, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1 },
        { 1, 0, 1, 0, 0, 1, 0, 1, 1, 0, 1, 1 }
    };

    /**
     * Matriz que é a MATRIZ_B misturada com a identidade.
     */
    private static int[][] MATRIZ_C;

    /**
     * Inicialização de MATRIZ_C.
     */
    static {
        MATRIZ_C = new int[12][24];
        for(int i = 0; i < 12; i++) {
            for(int j = 0; j < 24; j++) {
                int x = 0;
                if(i == j / 2) {
                    x = 1;
                }

                if(j % 2 == 0) {
                    x = MATRIZ_B[i][j / 2];
                }
                MATRIZ_C[i][j] = x;
                //System.out.print(" " + x);
            }
            //System.out.println();
        }
    }

    /**
     * Calcula o código de Golay para os 12 bits menos
     * valiosos do inteiro "x" passado como parametro.
     * O resultado tem 24 bits que é o código de golay
     * do número "x".
     */
    public static int calcularCodigoGolay(int x) {
        int[] soma = new int[24];
        for(int i = 0; i < 12; i++) {
            // somar linha
            if(((1 << i) & x) > 0) { // o i-esimo bit do numero x está aceso?
                for(int j = 0; j < 24; j++) {
                    soma[j] = soma[j] ^ MATRIZ_C[i][j];
                }
            }
        }

        int codigo = 0;
        for(int i = 0; i < 24; i++) {
            if(soma[i] != 0) {
                codigo = codigo + (1 << i);
            }
        }

        //System.out.println("t: " + t + " cod: " + codigo + " " + calcularTipo(codigo));
        return codigo;
    }

    /**
     * Obter o número de 12 bits que está no código de golay.
     */
    public static int calcularNumeroNoCodigoDeGolay(int c) {
        int numero = 0;
        for(int i = 1; i <= 24; i += 2) {
            if(((1 << i) & c) > 0) {
                numero += (1 << i / 2);
            }
        }
        return numero;
    }

    public static void fillbits(int source, int sourceFirstBit, boolean bits[], int targetFirstBit, int numBits) {
        for (int i=0;i<numBits;i++) {
            boolean b = ((source >> (sourceFirstBit + i)) & 1) != 0;
            bits[targetFirstBit + i] = b;
        }
    }

    public static int readnumber(boolean bits[], int firstBit, int numBits) {
        int result = 0;
        for (int i=numBits-1;i>=0;i--) {
            result = 2*result + (bits[firstBit + i] ? 1 : 0);
        }
        return result;
    }

    public static String fillleftzeros(String s, int size) {
        String result = "";
        for (int i=0;i<size-s.length();i++)
            result+="0";
        result = result + s;
        return result;
    }

    public static void rename() throws Exception {
        File target = new File("c:/mixnfix/clientes/SaoLuis/fotos14052005/");
        target.mkdirs();
        Stack<File> S = new Stack<File>();
        S.push(new File("c:/mixnfix/clientes/SaoLuis/14-05-05(FotosGabaritos)"));
        int i = 1;
        byte[] data = new byte[4096];
        while (!S.isEmpty()) {
            File f = S.pop();
            if (f.isFile() && f.getName().toLowerCase().indexOf(".jpg") >= 0) {
                String targetFileName = target.getAbsolutePath()+"\\f"+fillleftzeros(""+(i++),3)+".jpg";
                System.out.println(""+f.getAbsolutePath()+" -> "+targetFileName);
                FileInputStream fis = new FileInputStream(f);
                FileOutputStream fos = new FileOutputStream(targetFileName);
                while (fis.available()>0) {
                    int n = fis.read(data);
                    fos.write(data,0,n);
                }
                fis.close();
                fos.close();
            }
            else if (f.isDirectory()) {
                for (File ff: f.listFiles())
                    S.push(ff);
            }
        }
    }


    public static void makelist() throws Exception {
        String prefixes[] = {"030","300"};
        PrintWriter w = new PrintWriter("c:/mixnfix/clientes/SaoLuis/14-05-05(FotosGabaritos)/list1oAno.txt");
        for (int i=0;i<prefixes.length;i++) {
            for (int j=0;j<10000;j++) {
                w.println(prefixes[i]+fillleftzeros(""+j,4)+"\tJuarez da Silva\t1A");
            }
        }
        w.close();
    }


    public static void main(String[] args) throws Exception {
        try {
            makelist();
            //rename();
            /*
            int valor = 14;
            int codigo = calcularCodigoGolay(valor);
            int valor2 = calcularNumeroNoCodigoDeGolay(codigo);
            System.out.println(String.format("%x %x %x",valor,codigo,valor2));
*/
            /*
            Controller.unzip("C:/mixnfix/clientes/SaoLuis/2005/Fisica(Mecanica&Eletrica030502-2oAno).prova",Controller.TMP_DIR);
            Parser p = new Parser(Controller.TMP_DIR+"prova.xml");
            PrintStream ps = new PrintStream(new FileOutputStream(Controller.TMP_DIR+"x.tex"));
            new Questionario2TeX(p.getProva(),ps,new ParametrosDeLayoutDaProva());
            ps.close();
            */
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
