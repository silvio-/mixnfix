package mixnfix.gui;

public class GabaritoRetangulo extends GabaritoObject {
    private float _x;
    private float _y;
    private float _w;
    private float _h;
    public GabaritoRetangulo(float x, float y, float w, float h) {
        _x = x; _y = y; _w = w; _h = h;
    }
    public float getX() { return _x; }
    public float getY() { return _y; }
    public float getH() { return _h; }
    public float getW() { return _w; }
}
