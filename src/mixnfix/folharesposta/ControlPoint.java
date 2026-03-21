package mixnfix.folharesposta;

public class ControlPoint {
    private int _id;
    private double _x;
    private double _y;
    private double _imageX;
    private double _imageY;
    public ControlPoint(int id, double x, double y) {
        _id = id; _x = x; _y = y;
    }
    public int getId() { return _id; }
    public double getX() { return _x; }
    public double getY() { return _y; }
    public double getImageX() { return _imageX; }
    public double getImageY() { return _imageY; }
    public void setImageXY(double x, double y) { _imageX = x; _imageY = y; }
}
