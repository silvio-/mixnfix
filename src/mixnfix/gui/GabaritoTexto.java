package mixnfix.gui;

public class GabaritoTexto extends GabaritoObject {
    private float _x;
    private float _y;
    private String _text;
    public GabaritoTexto(float x, float y, String text) {
        _x = x; _y = y; _text = text;
    }
    public float getX() { return _x; }
    public float getY() { return _y; }
    public String getText() { return _text; }

}
