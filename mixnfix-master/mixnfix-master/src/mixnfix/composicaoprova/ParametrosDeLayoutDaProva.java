package mixnfix.composicaoprova;

import java.io.File;

/**
 *
 */
public class ParametrosDeLayoutDaProva {
    private int _fontSize=11;
    private float _paperWidth = 21.0f;
    private float _paperHeight = 29.7f;
    private float _topMargin = 1.5f;
    private float _bottomMargin = 1.5f;
    private float _leftMargin = 1.5f;
    private float _rightMargin = 1.5f;
    private int _numColumns = 2;
    private int[] _tiposDeProvas = new int[] {0,4};
    private String _outputDir = "c:/";
    private boolean _paginaEmBrancoAposFolhaResposta = false;
    private boolean _multipleFiles = false;
    private int _numeroDePaginasEmBrancoNoFim = 0;
    private File _logo;
    private boolean _colocarValorDaQuestao = true;
    private boolean _colocarGabarito = false;
    private boolean _goodbreak = false;
    private int _numFolhasRespostas = 1;

    public ParametrosDeLayoutDaProva(){
    }
    public ParametrosDeLayoutDaProva(
        int fontSize,
        float paperWidth,
        float paperHeight,
        float topMargin,
        float bottomMargin,
        float leftMargin,
        float rightMargin,
        int numColumns,
        int[] tiposDeProvas,
        String outputDir,
        boolean paginaEmBrancoAposFolhaResposta,
        boolean multipleFiles,
        File logo,
        int numeroDePaginasEmBrancoNoFim,
        boolean colocarValorDaQuestao,
        boolean colocarGabarito,
        boolean goodbreak,
        int numFolhasRespostas
        ) {
        _fontSize = fontSize;
        _paperWidth = paperWidth;
        _paperHeight = paperHeight;
        _topMargin = topMargin;
        _bottomMargin = bottomMargin;
        _leftMargin = leftMargin;
        _rightMargin = rightMargin;
        _numColumns = numColumns;
        _tiposDeProvas = tiposDeProvas;
        _outputDir = outputDir;
        _paginaEmBrancoAposFolhaResposta = paginaEmBrancoAposFolhaResposta;
        _multipleFiles = multipleFiles;
        _logo = logo;
        _numeroDePaginasEmBrancoNoFim = numeroDePaginasEmBrancoNoFim;
        _colocarValorDaQuestao = colocarValorDaQuestao;
        _colocarGabarito = colocarGabarito;
        _goodbreak = goodbreak;
        _numFolhasRespostas = numFolhasRespostas;
    }
    public int getFontSize() { return _fontSize; }
    public float getPaperWidth() { return _paperWidth; }
    public float getPaperHeight() { return _paperHeight; }
    public float getTopMargin() { return _topMargin; }
    public float getBottomMargin() { return _bottomMargin; }
    public float getLeftMargin() { return _leftMargin; }
    public float getRightMargin() { return _rightMargin; }
    public int getNumColumns() { return _numColumns; }
    public int[] getTiposDeProvas() { return _tiposDeProvas; }
    public String getOutputDir() { return _outputDir; }
    public boolean getPaginaEmBrancoAposFolhaResposta() { return _paginaEmBrancoAposFolhaResposta; }
    public boolean getMultipleFiles() { return _multipleFiles; }
    public void setTiposDeProva(int tipos[]) { _tiposDeProvas = (int[])tipos.clone();}
    public File getLogoImage() { return _logo; }
    public int getNumeroDePaginasEmBrancoNoFim() { return _numeroDePaginasEmBrancoNoFim; }
    public boolean colocarValorDaQuestao() { return _colocarValorDaQuestao; }
    public boolean colocarGabaraito() { return _colocarGabarito; }
    public boolean goodbreak() { return _goodbreak; }
    public int getNumFolhasRespostas() { return _numFolhasRespostas; }
}
