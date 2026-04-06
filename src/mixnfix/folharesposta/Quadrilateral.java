package mixnfix.folharesposta;

/**
 * A quadrilateral in the answer-sheet coordinate space, built from four
 * {@link ControlPoint}s given in theoretical order TL, TR, BR, BL (clockwise
 * starting from the top-left corner).
 *
 * Each Quadrilateral carries a precomputed projective homography that maps
 * the quadrilateral's theoretical axis-aligned bounding box (normalized to
 * the unit square (u,v) in [0,1]x[0,1]) to the image (practical) positions
 * of its four corners, using the practical positions stored on each
 * {@code ControlPoint} via {@link ControlPoint#getImageX()} and
 * {@link ControlPoint#getImageY()}.
 *
 * This replaces the previous piecewise-affine (Delaunay triangle) mapping
 * with a piecewise-projective one: since the exam image captured by a cell
 * camera is subject to a genuine perspective distortion, one homography per
 * quadrilateral models the local deformation more accurately than two
 * affine pieces.
 */
public class Quadrilateral {
    private ControlPoint _p0, _p1, _p2, _p3; // TL, TR, BR, BL

    // Theoretical axis-aligned bounding box
    private double _minX, _maxX, _minY, _maxY;

    // Homography coefficients: (u,v) in unit square -> (image_x, image_y)
    //   image_x = (ha*u + hb*v + hc) / (hg*u + hh*v + 1)
    //   image_y = (hd*u + he*v + hf) / (hg*u + hh*v + 1)
    private double _ha, _hb, _hc, _hd, _he, _hf, _hg, _hh;
    private boolean _valid;

    public static final double EPSILON = 1.0e-4;

    public Quadrilateral(ControlPoint p0, ControlPoint p1, ControlPoint p2, ControlPoint p3) {
        _p0 = p0;
        _p1 = p1;
        _p2 = p2;
        _p3 = p3;
        _minX = Math.min(Math.min(p0.getX(), p1.getX()), Math.min(p2.getX(), p3.getX()));
        _maxX = Math.max(Math.max(p0.getX(), p1.getX()), Math.max(p2.getX(), p3.getX()));
        _minY = Math.min(Math.min(p0.getY(), p1.getY()), Math.min(p2.getY(), p3.getY()));
        _maxY = Math.max(Math.max(p0.getY(), p1.getY()), Math.max(p2.getY(), p3.getY()));
        _valid = false;
    }

    public ControlPoint getP0() { return _p0; }
    public ControlPoint getP1() { return _p1; }
    public ControlPoint getP2() { return _p2; }
    public ControlPoint getP3() { return _p3; }

    public double getMinX() { return _minX; }
    public double getMaxX() { return _maxX; }
    public double getMinY() { return _minY; }
    public double getMaxY() { return _maxY; }

    public boolean isValid() { return _valid; }

    /**
     * True if the theoretical point (x,y) is inside the quadrilateral's
     * axis-aligned bounding box (with a small EPSILON slack at the borders).
     */
    public boolean contains(double x, double y) {
        return x >= _minX - EPSILON && x <= _maxX + EPSILON &&
               y >= _minY - EPSILON && y <= _maxY + EPSILON;
    }

    /**
     * Compute the projective homography that maps the unit square (u,v)
     * in [0,1]x[0,1] to the four corners' image (practical) positions in
     * the order TL(0,0), TR(1,0), BR(1,1), BL(0,1). Must be called after
     * the control points' image coordinates are known.
     */
    public void computeHomography() {
        double x0 = _p0.getImageX(), y0 = _p0.getImageY(); // TL
        double x1 = _p1.getImageX(), y1 = _p1.getImageY(); // TR
        double x2 = _p2.getImageX(), y2 = _p2.getImageY(); // BR
        double x3 = _p3.getImageX(), y3 = _p3.getImageY(); // BL

        double dx1 = x1 - x2, dy1 = y1 - y2;
        double dx2 = x3 - x2, dy2 = y3 - y2;
        double dx3 = x0 - x1 + x2 - x3, dy3 = y0 - y1 + y2 - y3;

        double det = dx1 * dy2 - dy1 * dx2;
        if (Math.abs(det) < 1.0e-12) {
            _valid = false;
            return;
        }

        _hg = (dx3 * dy2 - dy3 * dx2) / det;
        _hh = (dx1 * dy3 - dy1 * dx3) / det;

        double wMin = Math.min(Math.min(1.0, _hg + 1.0),
                               Math.min(_hh + 1.0, _hg + _hh + 1.0));
        if (wMin < 1.0e-6) {
            _valid = false;
            return;
        }

        _ha = x1 - x0 + _hg * x1;
        _hb = x3 - x0 + _hh * x3;
        _hc = x0;
        _hd = y1 - y0 + _hg * y1;
        _he = y3 - y0 + _hh * y3;
        _hf = y0;
        _valid = true;
    }

    /**
     * Map a theoretical point (x,y) that lies inside this quadrilateral
     * to its image (practical) coordinates, writing the result to
     * {@code mapping[0]} and {@code mapping[1]}.
     *
     * @return true if this quadrilateral's bounding box contains (x,y)
     *         and the mapping was applied, false otherwise (in which
     *         case {@code mapping} is left untouched).
     */
    public boolean mapToImage(double x, double y, double mapping[]) {
        if (!contains(x, y))
            return false;

        double u = (x - _minX) / (_maxX - _minX);
        double v = (y - _minY) / (_maxY - _minY);

        if (_valid) {
            double w = _hg * u + _hh * v + 1.0;
            mapping[0] = (_ha * u + _hb * v + _hc) / w;
            mapping[1] = (_hd * u + _he * v + _hf) / w;
        } else {
            // Bilinear fallback for degenerate quads (should not happen
            // for a well-formed control-point grid).
            mapping[0] = (1-u)*(1-v)*_p0.getImageX() + u*(1-v)*_p1.getImageX()
                       + u*v*_p2.getImageX()         + (1-u)*v*_p3.getImageX();
            mapping[1] = (1-u)*(1-v)*_p0.getImageY() + u*(1-v)*_p1.getImageY()
                       + u*v*_p2.getImageY()         + (1-u)*v*_p3.getImageY();
        }
        return true;
    }
}
