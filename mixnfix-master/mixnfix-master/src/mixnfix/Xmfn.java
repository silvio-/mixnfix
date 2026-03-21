package mixnfix;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * X MiXnFiX
 *
 * Gabaritos image processor.
 */
public class Xmfn {
    private static final Color COR_MARCA = new Color(1.0f, 1.0f, 0.0f, 0.75f);

    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 1;
    private static final int BOTTOM_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;

    public static final byte LEVEL_DEFINED = (byte)0;
    public static final byte USER_DEFINED_ON = (byte)1;
    public static final byte USER_DEFINED_OFF = (byte)2;

    private BufferedImage _img;

    private double _whiteArea;
    private double _cellArea;

    private boolean _showGrid;
    private boolean _showCurrentSolution;
    private boolean _showWhiteRegions;
    private boolean _showTargets;

    private int[][] _imageData;

    private int _rows;
    private int _columns;

    private int _width;
    private int _height;

    // numero para limite não selecionar
    // as células. Este numero deve ser
    // comparado com a intensidade branca
    // na regiao da celula menos a
    // intensidadea media da celula em valor
    // absoluto.
    private int _specialLevel;

    // gap de intensidades nao marcadas
    // e marcadas
    private int _specialLevelGap;

    private Point.Double[] _corners;
    private double[][][] _data;
    private byte[][] _selection;
    private GeneralPath[][] _regions;
    private GeneralPath[][] _whiteRegions;
    private double[][] _whiteRegionsMean;

    public Xmfn(
       BufferedImage img,
       int rows,
       int columns,
       double whiteArea,
       double cellArea,
       boolean showGrid,
       boolean showCurrentSolution,
       boolean showWhiteRegions,
       boolean showTargets) {

        _rows = rows;
        _columns = columns;
        _img = img;

        _showGrid = showGrid;
        _showCurrentSolution = showCurrentSolution;
        _showWhiteRegions = showWhiteRegions;
        _showTargets = showTargets;

        _whiteArea = whiteArea;
        _cellArea = cellArea;

        _corners = new Point.Double[4];
        _corners[TOP_LEFT] = new Point.Double(100, 100);
        _corners[TOP_RIGHT] = new Point.Double(540, 100);
        _corners[BOTTOM_RIGHT] = new Point.Double(540, 380);
        _corners[BOTTOM_LEFT] = new Point.Double(100, 380);

        _regions = new GeneralPath[_rows][_columns];
        _data = new double[_rows][_columns][4];
        _selection = new byte[_rows][_columns];
        _whiteRegions = new GeneralPath[_rows + 1][_columns + 1];
        _whiteRegionsMean = new double[_rows + 1][_columns + 1];

        _width = _img.getWidth();
        _height = _img.getHeight();

        Raster raster = _img.getRaster();
        int pixel[] = new int[1];
        _imageData = new int[_height][_width];
        for (int i=0;i<_height;i++)
            for (int j=0;j<_width;j++) {
                raster.getPixel(j, i, pixel);
                _imageData[i][j] = pixel[0];
            }

        updateRegions();
    }


    public void setSelectionType(int i, int j, byte type) {
        _selection[i][j]=type;
    }

    public byte getSelectionType(int i, int j) {
        return _selection[i][j];
    }

    public void moveCorner(int x, int y) {
        // see closest point
        int indexMin = 0;
        double dMin = sqrDist(x, y, indexMin);

        for (int i = 1; i < 4; i++) {
            double d = sqrDist(x, y, i);
            if (d < dMin) {
                indexMin = i;
                dMin = d;
            }
        }

        // move it to release point
        _corners[indexMin].setLocation(x, y);
        updateRegions();
    }

    private void updateRegions() {

        { // cell regions
            double[] p = new double[8];
            double k = _cellArea;
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    point(i - k, j - k, p, 0);
                    point(i - k, j + k, p, 2);
                    point(i + k, j + k, p, 4);
                    point(i + k, j - k, p, 6);
                    GeneralPath path = new GeneralPath();
                    path.moveTo( (float) p[0], (float) p[1]);
                    path.lineTo( (float) p[2], (float) p[3]);
                    path.lineTo( (float) p[4], (float) p[5]);
                    path.lineTo( (float) p[6], (float) p[7]);
                    path.closePath();
                    _regions[i][j] = path;
                }
            }
        }
        { // white regions
            double[] p = new double[8];
            double t = -0.5;
            double k = _whiteArea;
            for (int i = 0; i < _rows + 1; i++) {
                for (int j = 0; j < _columns + 1; j++) {
                    point(i + t - k, j + t - k, p, 0);
                    point(i + t - k, j + t + k, p, 2);
                    point(i + t + k, j + t + k, p, 4);
                    point(i + t + k, j + t - k, p, 6);

                    GeneralPath path = new GeneralPath();
                    path.moveTo( (float) p[0], (float) p[1]);
                    path.lineTo( (float) p[2], (float) p[3]);
                    path.lineTo( (float) p[4], (float) p[5]);
                    path.lineTo( (float) p[6], (float) p[7]);
                    path.closePath();
                    _whiteRegions[i][j] = path;
                }
            }
        }
    }

    public double sqrDist(double x, double y, int index) {
        double dx = x - _corners[index].getX();
        double dy = y - _corners[index].getY();
        return dx*dx+dy*dy;
    }

    public void paint(Graphics g) {
        if (_img == null)
            return;
        g.drawImage(_img,0,0,null);

        if (_showGrid) {
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    if (_selection[i][j] == LEVEL_DEFINED) {
                        g.setColor(Color.YELLOW);
                        ( (Graphics2D) g).draw(_regions[i][j]);
                    }
                    else if (_selection[i][j] == USER_DEFINED_OFF) {
                        g.setColor(Color.RED);
                        ( (Graphics2D) g).draw(_regions[i][j]);
                    }
                    else if (_selection[i][j] == USER_DEFINED_ON) {
                        g.setColor(Color.GREEN);
                        ( (Graphics2D) g).draw(_regions[i][j]);
                    }

                }
            }
        }

        if (_showWhiteRegions) {
            for (int i = 0; i < _rows+1; i++) {
                for (int j = 0; j < _columns+1; j++) {
                    g.setColor(Color.YELLOW);
                    ( (Graphics2D) g).draw(_whiteRegions[i][j]);
                }
            }
        }

        if (_showCurrentSolution) {
            g.setColor(COR_MARCA);
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    if (_selection[i][j] == LEVEL_DEFINED) {
                        if (doesCellLevelImpliesSelection(j, i)) {
                            Rectangle r = _regions[i][j].getBounds();
                            double x0 = r.getCenterX();
                            double y0 = r.getCenterY();
                            Shape shape = new java.awt.geom.Ellipse2D.Double(x0-2, y0-2, 4, 4);
                            ( (Graphics2D) g).fill(shape);
                            // g.fillOval((int)Math.round(x0)-2,(int)Math.round(y0)-2,5,5);
                            // ( (Graphics2D) g).fill(_regions[i][j]);
                        }
                    }
                    else if (_selection[i][j] == USER_DEFINED_ON) {
                        Rectangle r = _regions[i][j].getBounds();
                        double x0 = r.getCenterX();
                        double y0 = r.getCenterY();
                        Shape shape = new java.awt.geom.Ellipse2D.Double(x0-2, y0-2, 4, 4);
                        ( (Graphics2D) g).fill(shape);
                        // g.fillOval((int)Math.round(x0)-2,(int)Math.round(y0)-2,5,5);
                    }
                }
            }
        }

        if (_showTargets) {
            for (int i = 0; i < 4; i++) {
                Point.Double p = _corners[i];
                g.setColor(Color.CYAN);
                g.fillOval((int)p.getX()-2,(int)p.getY()-2,5,5);
            }
        }
    }

    public void calculateData() {

        // calcular white means
        calculateWhiteRegionMeans();

        int pixel;
        int index[] = new int[2];

        // clear
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {
                _data[i][j][0]=0;
                _data[i][j][1]=0;
                _data[i][j][2]=0;
            }
        }

        // samples of abs(whiteMean - mean)
        int contrast[] = new int[256];

        // clear
        double samples[] = new double[10000];
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {
                // System.out.println("célula ("+i+","+j+")");
                Rectangle rect = _regions[i][j].getBounds();

                int x0 = (int) rect.getMinX();
                int x1 = (int) rect.getMaxX();
                int y0 = (int) rect.getMinY();
                int y1 = (int) rect.getMaxY();

                int samplesCount=0;
                int samplesUpperBound=(x1-x0+1)*(y1-y0+1);
                if (samples.length < samplesUpperBound) {
                    samples = new double[samplesUpperBound];
                }

                // get samples
                for (int ii=y0;ii<=y1;ii++) {
                    for (int jj=x0;jj<=x1;jj++) {
                        if (_regions[i][j].contains(jj,ii)) {
                            if (ii >= 0 && ii<_height && jj >= 0 && jj < _width) {
                                pixel = _imageData[ii][jj];
                                _data[i][j][0] += 1;
                                _data[i][j][1] += pixel;
                                samples[samplesCount++] = pixel;
                            }
                        }
                    }
                }

                // calculate mean and standard deviation
                double mean = _data[i][j][1]/_data[i][j][0];
                _data[i][j][1] = mean;
                double var = 0;
                for (int k=0;k<samplesCount;k++) {
                    var+=(samples[k]-mean)*(samples[k]-mean);
                }
                _data[i][j][2] = Math.sqrt(var)/(samplesCount-1);

                // fazer pela media dos 3 mais brancos
                double x[] = {
                    _whiteRegionsMean[i][j],
                    _whiteRegionsMean[i][j + 1],
                    _whiteRegionsMean[i][j + 1],
                    _whiteRegionsMean[i + 1][j + 1]
                };
                Arrays.sort(x);
                _data[i][j][3] = /*(x[2]+*/x[3]/*)/2.0*/;

                // save contrast samples
                contrast[(int) Math.abs(mean - _data[i][j][3])]++;
            }
        }

        //
        // Find special level based on contrast array
        int l=0;
        while (contrast[l]==0 && l < 256)
            l++;
        l = (int) Math.max(20,l);

        int r=255;
        while (contrast[r]==0 && r >= 0)
            r--;
        r = (int) Math.min(200,r);

        if (l < r) {
            int bestL=-1;
            int bestSize=0;
            int ll=l;

            while(true) {
                int ii = ll;
                if (ii > r) {
                    break;
                }

                while (ii<=r && contrast[ii] == 0)
                    ii++;

                if (ii - ll > bestSize) {
                    bestL=ll;
                    bestSize=ii-ll;
                }

                ll=ii+1;
            }

            if (bestSize == 0) {
                _specialLevel = 50;
                _specialLevelGap = 0;
                System.out.println("Did't find any special level. It was set to "+_specialLevel);
            }
            else {
                _specialLevel = bestL + bestSize/2;
                _specialLevelGap = bestSize;
                System.out.println("Special level set to "+_specialLevel+" (interval size "+bestSize+")");
            }
        }
        else {
            _specialLevel = 0;
            _specialLevelGap = 0;
        }

        //
        // _specialLevel = 32;
    }

    public int getSpecialLevelGap() {
        return _specialLevelGap;
    }

    public void calculateWhiteRegionMeans() {
        int pixel;
        int index[] = new int[2];

        // clear
        for (int i=0;i<_rows+1;i++) {
            for (int j=0;j<_columns+1;j++) {
                _whiteRegionsMean[i][j]=0;
            }
        }

        // clear
        double samples[] = new double[10000];
        for (int i=0;i<_rows+1;i++) {
            for (int j=0;j<_columns+1;j++) {
                // System.out.println("white célula ("+i+","+j+")");
                Rectangle rect = _whiteRegions[i][j].getBounds();

                int x0 = (int) rect.getMinX();
                int x1 = (int) rect.getMaxX();
                int y0 = (int) rect.getMinY();
                int y1 = (int) rect.getMaxY();

                int samplesCount=0;
                int samplesUpperBound=(x1-x0+1)*(y1-y0+1);
                if (samples.length < samplesUpperBound) {
                    samples = new double[samplesUpperBound];
                }

                // get samples
                double sum = 0;
                for (int ii=y0;ii<=y1;ii++) {
                    for (int jj=x0;jj<=x1;jj++) {
                        if (_whiteRegions[i][j].contains(jj,ii)) {
                            if (ii >= 0 && ii<_height && jj >= 0 && jj < _width) {
                                pixel = _imageData[ii][jj];
                            }
                            else {
                                // se o ponto estiver fora da regiao, é por definição branco!
                                pixel = 255;
                            }
                            // System.out.println("Pixel ("+ii+","+jj+") com intensidade "+pixel[0]+" está na célula ("+i+","+j+")");
                            sum += pixel;
                            samples[samplesCount++] = pixel;
                        }
                    }
                }

                // calculate mean and standard deviation
                _whiteRegionsMean[i][j] = sum/samplesCount;
            }
        }
    }

    private void write(String fileName, String title, int index) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(fileName));
        System.out.println(title);
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {
                System.out.print(""+_data[i][j][index]);
                pw.print(""+_data[i][j][index]);
                if (j < _columns-1) {
                    System.out.print("\t");
                    pw.print("\t");
                }
            }
            pw.println("");
            System.out.println("");
        }
        pw.flush();
        pw.close();
    }

    public void printData() throws Exception {

        // samples
        write("resources/size.dat","Sample Sizes",0);
        write("resources/mean.dat","Mean",1);
        write("resources/stddev.dat","StdDev",2);
        write("resources/white.dat","White",3);
    }

    private boolean doesCellLevelImpliesSelection(int x, int y) {
        return (Math.abs(_data[y][x][1]-_data[y][x][3]) > _specialLevel);
    }

    /**
     * Save current result matrix on a file
     */
    public void saveResultMatrix(String fileName) throws Exception {
        PrintWriter pw = new PrintWriter(new FileWriter(fileName));
        System.out.println("Saving "+fileName);
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {
                if (_selection[i][j] == LEVEL_DEFINED) {
                    if (doesCellLevelImpliesSelection(j,i))
                        pw.print("1");
                    else
                        pw.print("0");
                }
                else if (_selection[i][j] == USER_DEFINED_OFF) {
                    pw.print("0");
                }
                else if (_selection[i][j] == USER_DEFINED_ON) {
                    pw.print("1");
                }
                if (j< _columns-1)
                    pw.print("\t");
            }
            pw.println();
        }
        pw.flush();
        pw.close();
    }

    /**
     * Save current result matrix on a file
     */
    public boolean[][] getResultMatrix() {
        boolean result[][] = new boolean[_rows][_columns];
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {
                if (_selection[i][j] == LEVEL_DEFINED) {
                    if (doesCellLevelImpliesSelection(j,i))
                        result[i][j]=true;
                }
                else if (_selection[i][j] == USER_DEFINED_ON) {
                    result[i][j]=true;
                }
            }
        }
        return result;
    }

    public boolean findRegion(int x, int y, int index[]) {
        for (int i=0;i<_rows;i++)
            for (int j=0;j<_columns;j++) {
                if (_regions[i][j].contains(x,y)) {
                    index[0] = i;
                    index[1] = j;
                    return true;
                }
            }
        return false;
    }

    private void point(double i, double j, double[] output, int offset) {

        // os centros das células dos cantos sao definidas
        // a partir das esquinas. A célula (i,j) tem centro
        // na interseção da reta que passa pelo centro da i-esima
        // celula do canto esquerdo e pelo centro da i-ésima celula do canto
        // direito com a reta que passa pelo centro da j-ésima celula
        // superiores e pelo centro da j-ésima celula inferior.

        double h1x = _corners[TOP_RIGHT].getX() - _corners[TOP_LEFT].getX();
        double h1y = _corners[TOP_RIGHT].getY() - _corners[TOP_LEFT].getY();

        double h2x = _corners[BOTTOM_RIGHT].getX() - _corners[BOTTOM_LEFT].getX();
        double h2y = _corners[BOTTOM_RIGHT].getY() - _corners[BOTTOM_LEFT].getY();

        double v1x = _corners[BOTTOM_LEFT].getX() - _corners[TOP_LEFT].getX();
        double v1y = _corners[BOTTOM_LEFT].getY() - _corners[TOP_LEFT].getY();

        double v2x = _corners[BOTTOM_RIGHT].getX() - _corners[TOP_RIGHT].getX();
        double v2y = _corners[BOTTOM_RIGHT].getY() - _corners[TOP_RIGHT].getY();

        double I1x = _corners[TOP_LEFT].getX() + (double)i/(_rows - 1) * v1x;
        double I1y = _corners[TOP_LEFT].getY() + (double)i/(_rows - 1) * v1y;

        double I2x = _corners[TOP_RIGHT].getX() + (double)i/(_rows - 1) * v2x;
        double I2y = _corners[TOP_RIGHT].getY() + (double)i/(_rows - 1) * v2y;

        double IIx = I2x - I1x;
        double IIy = I2y - I1y;

        double J1x = _corners[TOP_LEFT].getX() + (double)j/(_columns - 1) * h1x;
        double J1y = _corners[TOP_LEFT].getY() + (double)j/(_columns - 1) * h1y;

        double J2x = _corners[BOTTOM_LEFT].getX() + (double)j/(_columns - 1) * h2x;
        double J2y = _corners[BOTTOM_LEFT].getY() + (double)j/(_columns - 1) * h2y;

        double JJx = J2x - J1x;
        double JJy = J2y - J1y;

        double delta = JJx*IIy-IIx*JJy;
        double cx = -(J1x*IIx*JJy-JJx*IIy*I1x+JJx*IIx*I1y-JJx*IIx*J1y)/delta;
        double cy = (J1y*JJx*IIy+JJy*IIy*I1x-JJy*IIy*J1x-JJy*IIx*I1y)/delta;

        output[offset] = (int)Math.round(cx);
        output[offset+1] = (int)Math.round(cy);
    }

    private int root(int v, int[] parentTree) {
        while (parentTree[v] != v)
            v = parentTree[v];
        return v;
    }

    private static final int _thresholds[] = { 70, 100, 50, 130, 80, 110, 40, 140, 60, 90, 120};
    /**
     * return number of steps to find targets
     */
    public int findTargets2(String saveThresholdImage) {

        int steps = 0;
        boolean found = false;
        for (int t = 0; t < _thresholds.length && !found; t++) {
            int parent[] = new int[(_height*_width)/2];
            int marks[][] = new int[_height][_width];

            steps++;
            int threshold = _thresholds[t];

            int freeName = 1;

            for (int i = 0; i < _height; i++) {
                for (int j = 0; j < _width; j++) {

                    // continue...
                    if (_imageData[i][j] > threshold)
                        continue;

                    // check above
                    if (i > 0) {
                        if (marks[i - 1][j] != 0) {
                            // same connected component as it's top neihgbour.
                            marks[i][j] = marks[i - 1][j];
                        }
                    }

                    // check left
                    if (j > 0) {

                        // vizinho da esquerda esta em algum componente?
                        int indexNameLeft = marks[i][j - 1];

                        if (indexNameLeft != 0) {
                            int indexName = marks[i][j];

                            // merge it's connected components with
                            // it's left neihgbour connected component.
                            if (indexName != 0) {
                                int root = root(indexName, parent);
                                int rootLeft = root(indexNameLeft, parent);
                                if (root < rootLeft) {
                                    parent[rootLeft] = root;
                                }
                                else if (root > rootLeft) {
                                    parent[root] = rootLeft;
                                }
                            }

                            // same connected component as it's left neihgbour.
                            else {
                                marks[i][j] = marks[i][j - 1];
                            }
                        }

                    }

                    // new connected component
                    if (marks[i][j] == 0) {
                        marks[i][j] = freeName;
                        parent[freeName] = freeName;
                        freeName++;
                    }
                }
            }

            // one level tree and count roots
            int number = 0;
            HashMap mapName2Region = new HashMap();
            for (int i = 1; i < freeName; i++) {
                int root = root(i, parent);
                Region r = (Region) mapName2Region.get(new Integer(root));
                if (r == null) {
                    r = new Region(root, number++);
                    mapName2Region.put(new Integer(root), r);
                }
                parent[i] = root;
            }

            // contar dados da região
            for (int i = 0; i < _height; i++) {
                for (int j = 0; j < _width; j++) {
                    if (marks[i][j] == 0)
                        continue;
                    int representante = parent[marks[i][j]];
                    Region r = (Region) mapName2Region.get(new Integer(representante));
                    r.addPixel(j, i);
                }
            }

            // laço definindo regioes prováveis para os alvos
            ArrayList filteredRegions = new ArrayList();
            Iterator it = mapName2Region.keySet().iterator();
            while (it.hasNext()) {
                Region r = (Region) mapName2Region.get(it.next());
                double ratio = (double) r.getHeight() / r.getWidth();
                if (r.getSize() >= 30 &&
                    r.getSize() <= 120 &&
                    r.getDensity() >= 0.75 &&
                    ratio <= 0.9 &&
                    ratio >= 0.5) {
                    filteredRegions.add(r);
                }
            }

            // encontrar os alvos!
            double dist[] = new double[4];
            double points[] = new double[8];
            if (filteredRegions.size() > 0) {
                Region r = (Region) filteredRegions.get(0);
                double cx = r.getCenterX();
                double cy = r.getCenterY();
                points[0] = cx;
                points[1] = cy; // top left
                points[2] = cx;
                points[3] = cy; // top right
                points[4] = cx;
                points[5] = cy; // bottom right
                points[6] = cx;
                points[7] = cy; // bottom left
                dist[0] = cx * cx + cy * cy;
                dist[1] = (_width - cx) * (_width - cx) + cy * cy;
                dist[2] = (_width - cx) * (_width - cx) +
                    (_height - cy) * (_height - cy);
                dist[3] = cx * cx + (_height - cy) * (_height - cy);
                for (int i = 1; i < filteredRegions.size(); i++) {
                    r = (Region) filteredRegions.get(i);
                    cx = r.getCenterX();
                    cy = r.getCenterY();
                    double dist0 = cx * cx + cy * cy;
                    double dist1 = (_width - cx) * (_width - cx) + cy * cy;
                    double dist2 = (_width - cx) * (_width - cx) +
                        (_height - cy) * (_height - cy);
                    double dist3 = cx * cx + (_height - cy) * (_height - cy);
                    if (dist0 < dist[0]) {
                        dist[0] = dist0;
                        points[0] = cx;
                        points[1] = cy;
                    }
                    if (dist1 < dist[1]) {
                        dist[1] = dist1;
                        points[2] = cx;
                        points[3] = cy;
                    }
                    if (dist2 < dist[2]) {
                        dist[2] = dist2;
                        points[4] = cx;
                        points[5] = cy;
                    }
                    if (dist3 < dist[3]) {
                        dist[3] = dist3;
                        points[6] = cx;
                        points[7] = cy;
                    }
                }

                // geometry test
                if (geometryTest(points)) {
                    _corners[TOP_LEFT].setLocation(points[0], points[1]);
                    _corners[TOP_RIGHT].setLocation(points[2], points[3]);
                    _corners[BOTTOM_RIGHT].setLocation(points[4], points[5]);
                    _corners[BOTTOM_LEFT].setLocation(points[6], points[7]);

                    this.updateRegions();

                    //
                    found = true;
                }
            }

            //
            if (found && saveThresholdImage != null) {

                // gerar imagem
                for (int i = 0; i < _height; i++) {
                    for (int j = 0; j < _width; j++) {
                        if (marks[i][j] == 0) {
                            marks[i][j] = 255;
                            continue;
                        }
                        int representante = parent[marks[i][j]];
                        Region r = (Region) mapName2Region.get(new Integer(representante));
                        if (!filteredRegions.contains(r)) {
                            marks[i][j] = 255;
                        }
                        else {
                            marks[i][j] = 0;
                        }
                    }
                }

                // save
                saveImage(marks, _width, _height, saveThresholdImage);
            }


        }

        if (!found) {
            System.out.println("WARNING: Didn't find targets!");
            return 0;
        }

        return steps;
    }

    private boolean geometryTest(double corners[]) {
        double top = Math.sqrt(
            (corners[0] - corners[2]) * (corners[0] - corners[2]) +
            (corners[1] - corners[3]) * (corners[1] - corners[3])
            );
        double bottom = Math.sqrt(
            (corners[6] - corners[4]) * (corners[6] - corners[4]) +
            (corners[7] - corners[5]) * (corners[7] - corners[5]));
        double left = Math.sqrt(
            (corners[0] - corners[6]) * (corners[0] - corners[6]) +
            (corners[1] - corners[7]) * (corners[1] - corners[7])
            );
        double right = Math.sqrt(
            (corners[2] - corners[4]) * (corners[2] - corners[4]) +
            (corners[3] - corners[5]) * (corners[3] - corners[5])
            );

        double min_side = 30;
        if (top < min_side || bottom < min_side || left < min_side ||
            right < min_side)
            return false;

        double difh = Math.abs(top - bottom);
        double difv = Math.abs(left - right);
        double delta = Math.max(difh, difv);

        if (delta < 10)
            return true;
        else
            return false;
    }

    public double[] getCorners() {
        double[] d = new double[8];
        int i=0;
        for (int k=0;k<4;k++) {
            d[i++]=_corners[k].getX();
            d[i++]=_corners[k].getY();
        }
        return d;
    }

    private void saveImage(int a[][],int w, int h, String fileName) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();
        // System.out.println("Number of Components "+number);
        int[] nBits = {8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits,false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(_width,_height);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        for (int i=0;i<_height;i++) {
            for (int j = 0; j < _width; j++) {
                dataBuffer.setElem(i*_width+j,a[i][j]);
            }
        }
        WritableRaster wr = Raster.createWritableRaster(sampleModel,dataBuffer, new Point(0,0));
        BufferedImage bf = new BufferedImage(colorModel, wr, true, null);
        try {
            ImageIO.write(bf, "jpg",new File(fileName));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveImageFileWithCurrentView(String fileName) {
        BufferedImage img = new BufferedImage(_width,_height,BufferedImage.TYPE_4BYTE_ABGR);
        this.paint(img.getGraphics());
        try {
            ImageIO.write(img, "png",new File(fileName));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean is_showCurrentSolution() {
        return _showCurrentSolution;
    }
    public boolean is_showGrid() {
        return _showGrid;
    }
    public boolean is_showTargets() {
        return _showTargets;
    }
    public boolean is_showWhiteRegions() {
        return _showWhiteRegions;
    }
    public void set_showWhiteRegions(boolean _showWhiteRegions) {
        this._showWhiteRegions = _showWhiteRegions;
    }
    public void set_showTargets(boolean _showTargets) {
        this._showTargets = _showTargets;
    }
    public void set_showGrid(boolean _showGrid) {
        this._showGrid = _showGrid;
    }
    public void set_showCurrentSolution(boolean _showCurrentSolution) {
        this._showCurrentSolution = _showCurrentSolution;
    }


    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        JFrame f = new JFrame("Teste...");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new Test(null,20,28));
        f.setBounds(0,0,800,600);
        f.setVisible(true);
    }
}



class Region {
    int _name;
    int _countPixels;
    int _minX;
    int _minY;
    int _maxX;
    int _maxY;
    int _sumX;
    int _sumY;
    int _number;
    public Region(int name, int number) {
        _name = name;
        _number = number;
    }
    public int getNumber() {
        return _number;
    }
    public void addPixel(int x, int y) {
        if (_countPixels == 0) {
            _minX = x;
            _minY = y;
            _maxX = x;
            _maxY = y;
            _sumX = x;
            _sumY = y;
        }
        else {
            if (x < _minX) _minX = x;
            if (x > _maxX) _maxX = x;
            if (y < _minY) _minY = y;
            if (y > _maxY) _maxY = y;
            _sumX += x;
            _sumY += y;
        }
        _countPixels++;
    }

    public int getWidth() {
        return _maxX - _minX + 1;
    }

    public int getHeight() {
        return _maxY - _minY + 1;
    }

    public int getSize() {
        return _countPixels;
    }

    public int getName() {
        return _name;
    }

    public double getDensity() {
        return ((double)_countPixels/(getWidth() * getHeight()));
    }

    public double getCenterX() {
        return (double)_sumX/_countPixels;
    }

    public double getCenterY() {
        return (double)_sumY/_countPixels;
    }

    public String toString() {
        String result = "";
        result += _name+"\t";
        result += _countPixels+"\t";
        result += ((double)_sumX/_countPixels)+"\t";
        result += ((double)_sumY/_countPixels)+"\t";
        result += getDensity();
        return result;
    }
}
