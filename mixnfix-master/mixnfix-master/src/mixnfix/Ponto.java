package mixnfix;



class Ponto {
    int _x;
    int _y;
    int _intensity;
    public Ponto(int x, int y, int intensity) {
        _x = x;
        _y = y;
        _intensity = intensity;
    }
    public int getX() {return _x;}
    public int getY() {return _y;}
    public int getIntensity() {return _intensity;}
    public double getDistance(Ponto p) {
        double dx = (p.getX() - _x);
        double dy = (p.getY() - _y);
        return Math.sqrt(dx*dx + dy*dy);
    }
    public double getDistance(int x, int y) {
        double dx = (x - _x);
        double dy = (y - _y);
        return Math.sqrt(dx*dx + dy*dy);
    }
}
