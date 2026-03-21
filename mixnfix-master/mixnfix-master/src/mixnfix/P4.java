package mixnfix;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;

public class P4 {
    private static final int TEST_VERTICAL_GREATER = 1;
    private static final int TEST_VERTICAL_SMALLER = 2;
    private static final int TEST_HORIZONTAL_GREATER = 3;
    private static final int TEST_HORIZONTAL_SMALLER = 4;
    private static final int TEST_OBLIQUE_GREATER = 5;
    private static final int TEST_OBLIQUE_SMALLER = 6;
    private double[] _points = new double[8];
    private double _minX = Double.POSITIVE_INFINITY;
    private double _minY = Double.POSITIVE_INFINITY;
    private double _maxX = Double.NEGATIVE_INFINITY;
    private double _maxY = Double.NEGATIVE_INFINITY;
    private int _tests[] = new int[4];

    public P4(double[] points) {

        // exception
        if (points.length < 8)
            throw new RuntimeException();

        // copy data
        System.arraycopy(points,0,_points,0,8);

        //
        for (int i=0;i<4;i++) {
            int j = (i + 1) % 4;
            int k = (i + 2) % 4;

            double x1 = _points[2*i];
            double y1 = _points[2*i+1];
            double x2 = _points[2*j];
            double y2 = _points[2*j+1];
            double dx = x2 - x1;
            double dy = y2 - y1;

            { // keep bounds
                _minX = Math.min(_minX,x1);
                _minY = Math.min(_minY,y1);
                _maxX = Math.max(_maxX,x1);
                _maxY = Math.max(_maxY,y1);
            } // keep bounds

            double x = _points[2*k];
            double y = _points[2*k+1];

            if ((dx==dy) && (dx == 0)) {
                throw new RuntimeException();
            }

            // é vertical
            if (dx == 0) {
                if (x < x1) {
                    _tests[i] = TEST_VERTICAL_SMALLER;
                }
                else if (x > x1) {
                    _tests[i] = TEST_VERTICAL_GREATER;
                }
                else throw new RuntimeException();
            }

            // é horizontal
            else if (dy == 0) {
                if (y < y1) {
                    _tests[i] = TEST_HORIZONTAL_SMALLER;
                }
                else if (y > y1) {
                    _tests[i] = TEST_HORIZONTAL_GREATER;
                }
                else throw new RuntimeException();
            }

            // é obliqua
            else {
                double a = dy / dx;
                double b = (y1 * x2 - x1 * y2) / dx;
                if (y < a*x + b) {
                    _tests[i] = TEST_OBLIQUE_SMALLER;
                }
                else if (y > a*x + b) {
                    _tests[i] = TEST_OBLIQUE_GREATER;
                }
                else throw new RuntimeException();
            }
        }
    }

    public boolean contains(double x0, double y0) {
        //
        for (int i=0;i<4;i++) {

            int j = (i + 1) % 4;

            double x1 = _points[2*i];
            double y1 = _points[2*i+1];
            double x2 = _points[2*j];
            double y2 = _points[2*j+1];

            double dx = x2 - x1;
            double dy = y2 - y1;

            double a = dy / dx;
            double b = (y1 * x2 - x1 * y2) / dx;

            switch (_tests[i]) {
                case TEST_OBLIQUE_SMALLER:
                    if (y0 > a * x0 + b)
                        return false;
                    break;
                case TEST_OBLIQUE_GREATER:
                    if (y0 < a * x0 + b)
                        return false;
                    break;
                case TEST_HORIZONTAL_SMALLER:
                    if (y0 > y1)
                        return false;
                    break;
                case TEST_HORIZONTAL_GREATER:
                    if (y0 < y1)
                        return false;
                    break;
                case TEST_VERTICAL_SMALLER:
                    if (x0 > x1)
                        return false;
                    break;
                case TEST_VERTICAL_GREATER:
                    if (x0 < x1)
                        return false;
                    break;
            }
        }
        return true;
    }

    public double getX(int index) {
        return _points[2*index];
    }

    public double getY(int index) {
        return _points[2*index+1];
    }

    public double getMinX() {
        return _minX;
    }

    public double getMinY() {
        return _minY;
    }

    public double getMaxX() {
        return _maxX;
    }

    public double getMaxY() {
        return _maxY;
    }

    public double getCenterX() {
        return (_maxX + _minX) /  2.0;
    }

    public double getCenterY() {
        return (_maxY + _minY) /  2.0;
    }

    public static void main(String[] args) {
        /*
        double[] points = new double[] {
            20,20,
            10,300,
            400,450,
            480,30};*/
        double[] points = new double[] {
            20,50,
            20,400,
            600,400,
            600,20};
        P4 p = new P4(points);

        int N = 500;
        double samples[] = new double[2*N];
        System.arraycopy(points,0,samples,0,8);
        for (int i=0;i<N-8;i++) {
            samples[2*(i+8)] = Math.random() * 640;
            samples[2*(i+8)+1] = Math.random() * 480;
        }

        F f = new F(p,samples);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0,0,640,480);
        f.setVisible(true);

    }

}

class F extends JFrame {
    P4 _p;
    double _points[];
    public F(P4 p, double[] points) {
        _p = p;
        _points = points;
        this.setUndecorated(true);
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        GeneralPath path = new GeneralPath();
        path.moveTo( (float) _p.getX(0), (float) _p.getY(0));
        path.lineTo( (float) _p.getX(1), (float) _p.getY(1));
        path.lineTo( (float) _p.getX(2), (float) _p.getY(2));
        path.lineTo( (float) _p.getX(3), (float) _p.getY(3));
        path.closePath();
        g2d.draw(path);

        for (int i=0;i<_points.length;i+=2) {
            double x0 = _points[i];
            double y0 = _points[i+1];
            double raio = 2;
            Shape shape = new java.awt.geom.Ellipse2D.Double(x0-raio, y0-raio, 2*raio+1, 2*raio+1);

            if (_p.contains(x0,y0)) {
                g2d.setColor(Color.GREEN);
            }
            else {
                g2d.setColor(Color.RED);
            }
            ( (Graphics2D) g).fill(shape);
        }




    }
}
