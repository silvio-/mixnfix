package mixnfix.folharesposta;

public class Cell {
    private int _id;
    private double _dxText;
    private double _dyText;
    private String _text;
    private double _x;
    private double _y;
    private double _w;
    private double _h;
    private double _w0;
    private double _h0;
    private double _w1;
    private double _h1;
    private double _w2;
    private double _h2;
    private byte _whiteSampleSet;
    public Cell(int id, double x, double y, double w, double h) {
        //_x = x; _y = y; _w = w; _h = h;
        this(id,x,y,w,h,0.5f*w,0.5f*h,1.1f*w,1.1f*h,1.5f*w,1.5f*h,(byte)165);
    }
    public Cell(int id,double x, double y, double w, double h, double w0, double h0, double w1, double h1, double w2, double h2, byte whiteSampleSet) {
        _id = id;
        _x = x; _y = y;
        _w = w;
        _h = h;
        _w0 = w0;
        _h0 = h0;
        _w1 = w1;
        _h1 = h1;
        _w2 = w2;
        _h2 = h2;
        _whiteSampleSet = whiteSampleSet;
    }
    public void setText(String text, double dxText, double dyText) {
        _text = text;
        _dxText = dxText;
        _dyText = dyText;
    }
    public String getText() { return _text; }
    public double getDxText() { return  _dxText; }
    public double getDyText() { return  _dyText; }
    public double getX() { return _x; }
    public double getY() { return _y; }
    public double getH() { return _h; }
    public double getW() { return _w; }
    public double getH0() { return _h0; }
    public double getW0() { return _w0; }
    public double getH1() { return _h1; }
    public double getW1() { return _w1; }
    public double getH2() { return _h2; }
    public double getW2() { return _w2; }
    public byte getWhiteSampleSet() { return _whiteSampleSet; }
    public int getId() { return _id; }


    private double _imageIntensity;
    public void setImageIntensity(double intensity) {
        _imageIntensity = intensity;
    }
    public double getImageIntensity() {
        return _imageIntensity;
    }

    private double _imageX;
    private double _imageY;
    public void setImageXY(double x, double y) {
        _imageX = x;
        _imageY = y;
    }
    public double getImageX() { return _imageX; }
    public double getImageY() { return _imageY; }


    private boolean _isSelected;
    public void setSelected(boolean s) {
        _isSelected = s;
    }
    public boolean isSelected() {
        return _isSelected;
    }
}
