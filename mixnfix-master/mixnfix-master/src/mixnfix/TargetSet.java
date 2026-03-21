package mixnfix;

/**
 * Used with ImgProcessor
 */
public class TargetSet {
    private double[] _corners = new double[8];
    private int _threshold;
    private boolean _sucess;
    public TargetSet(int threshold, double[] corners, boolean sucess) {
        _threshold = threshold;
        _sucess = sucess;
        for (int i=0;i<8;i++) _corners[i] = corners[i];
    }
    public boolean getSucess() {
        return _sucess;
    }
    public int getThreshold() {
        return _threshold;
    }
    public double get(int index) {
        return _corners[index];
    }
}
