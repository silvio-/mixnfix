package mixnfix.folharesposta;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Triangle {
    ControlPoint _p1, _p2, _p3;
    public Triangle(ControlPoint p1, ControlPoint p2, ControlPoint p3) {
        _p1 = p1;
        _p2 = p2;
        _p3 = p3;
    }
    public ControlPoint getP1() { return _p1; }
    public ControlPoint getP2() { return _p2; }
    public ControlPoint getP3() { return _p3; }

    public static double EPSILON = 1.0e-4f;
    public boolean triangleConvexCombination(double x, double y, double mapping[]) {
         double a1 = _p1.getX();
         double a2 = _p1.getY();

         double b1 = _p2.getX();
         double b2 = _p2.getY();

         double c1 = _p3.getX();
         double c2 = _p3.getY();

         double denom = -b1*a2+c1*a2-c1*b2+b2*a1-c2*a1+c2*b1;

         double alpha = (-b1*y-c1*b2+c1*y+c2*b1-c2*x+b2*x)/denom;
         if (alpha < -EPSILON || alpha > 1 + EPSILON)
            return false;

         double beta = -(c1*y-c2*x-c1*a2+c2*a1-a1*y+x*a2)/denom;
         if (beta < -EPSILON || beta > 1 + EPSILON)
            return false;

         double gamma = (-b1*a2+b1*y-a1*y-b2*x+x*a2+b2*a1)/denom;
         if (gamma < -EPSILON || gamma > 1 + EPSILON)
            return false;

         mapping[0] = alpha * _p1.getImageX() + beta * _p2.getImageX() + gamma * _p3.getImageX();
         mapping[1] = alpha * _p1.getImageY() + beta * _p2.getImageY() + gamma * _p3.getImageY();
         return true;
     }

}
