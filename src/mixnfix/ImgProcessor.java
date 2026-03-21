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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.imageio.ImageIO;

/**
 * X MiXnFiX
 *
 * Gabaritos image processor.
 */
public class ImgProcessor {
    private static final Color COR_MARCA = new Color(0.0f, 0.9f, 0.0f, 0.9f);

    private static final int TOP_LEFT = 0;
    private static final int TOP_RIGHT = 1;
    private static final int BOTTOM_RIGHT = 2;
    private static final int BOTTOM_LEFT = 3;

    public static final byte LEVEL_DEFINED = (byte)0;
    public static final byte USER_DEFINED_ON = (byte)1;
    public static final byte USER_DEFINED_OFF = (byte)2;

    private BufferedImage _img;

    private int[][] _imageData;

    private int _width;
    private int _height;

    private double _targetDelta;

    private Point.Double[] _corners;

    public ImgProcessor(BufferedImage img) throws Exception {
        _img = img;

        _corners = new Point.Double[4];
        _corners[TOP_LEFT] = new Point.Double(100, 100);
        _corners[TOP_RIGHT] = new Point.Double(540, 100);
        _corners[BOTTOM_RIGHT] = new Point.Double(540, 380);
        _corners[BOTTOM_LEFT] = new Point.Double(100, 380);

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
    }

    public void setParameters(
       double whiteArea,
       double cellArea,
       boolean showGrid,
       boolean showCurrentSolution,
       boolean showWhiteRegions,
       boolean showTargets) {
       _showGrid = showGrid;
       _showCurrentSolution = showCurrentSolution;
       _showWhiteRegions = showWhiteRegions;
       _showTargets = showTargets;
       _whiteArea = whiteArea;
       _cellArea = cellArea;
    }

    ///////////////////////////////////////////////////////////////////////////
    // FIND TARGETS

    private static final int _thresholds[] = { 70, 100, 50, 130, 80, 110, 40, 140, 60, 90, 120, 10, 20 };


    private int _lastThreshold;

    private ArrayList<TargetSet> _targetsTried = new ArrayList<TargetSet>();
    public ArrayList<TargetSet> getTargetsTried() {
        return (ArrayList<TargetSet>) _targetsTried.clone();
    }
    public void assign(TargetSet s) {
        int i=0;
        _corners[TOP_LEFT] = new Point.Double(s.get(i++), s.get(i++));
        _corners[TOP_RIGHT] = new Point.Double(s.get(i++), s.get(i++));
        _corners[BOTTOM_RIGHT] = new Point.Double(s.get(i++), s.get(i++));
        _corners[BOTTOM_LEFT] = new Point.Double(s.get(i++), s.get(i++));
        this.set_threshold(s.getThreshold());

        if (this._regionsAreDefined) {
            this.updateRegions();
        }
    }

    /**
     * return number of steps to find targets
     */
    public int findTargets(String saveThresholdImage) {

        int steps = 0;
        int threshold = 0;
        boolean found = false;
        for (int t = 0; t < _thresholds.length && !found; t++) {
            int parent[] = new int[(_height*_width)/2];
            int marks[][] = new int[_height][_width];

            steps++;
            threshold = _thresholds[t];

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
                TargetRegion r = (TargetRegion) mapName2Region.get(root);
                if (r == null) {
                    r = new TargetRegion(root, number++);
                    mapName2Region.put(root, r);
                }
                parent[i] = root;
            }

            // contar dados da região
            for (int i = 0; i < _height; i++) {
                for (int j = 0; j < _width; j++) {
                    if (marks[i][j] == 0)
                        continue;
                    int representante = parent[marks[i][j]];
                    TargetRegion r = (TargetRegion) mapName2Region.get(representante);
                    r.addPixel(j, i);
                }
            }

            // laço definindo regioes prováveis para os alvos
            ArrayList filteredRegions = new ArrayList();
            Iterator it = mapName2Region.keySet().iterator();
            while (it.hasNext()) {
                TargetRegion r = (TargetRegion) mapName2Region.get(it.next());
                double ratio = (double) r.getHeight() / r.getWidth();
                if (r.getSize() >= 16 &&
                    r.getSize() <= 100 &&
                    r.getDensity() >= 0.60 &&
                    ratio <= 0.9 &&
                    ratio >= 0.5) {
                    filteredRegions.add(r);
                }
            }

            // save file for test purposes
            try {
                DataOutputStream fw = new DataOutputStream(new FileOutputStream("c:/workspace/mnfimg/c/data/x" + System.currentTimeMillis() + ".dat"));
                for (TargetRegion tr : (java.util.List<TargetRegion>) filteredRegions) {
                    int xi = Float.floatToIntBits((float)tr.getCenterX());
                    fw.write(0xFF & xi);
                    fw.write(0xFF & (xi >> 8));
                    fw.write(0xFF & (xi >> 16));
                    fw.write(0xFF & (xi >> 24));

                    int yi = Float.floatToIntBits((float)tr.getCenterY());
                    fw.write(0xFF & yi);
                    fw.write(0xFF & (yi >> 8));
                    fw.write(0xFF & (yi >> 16));
                    fw.write(0xFF & (yi >> 24));
                }
                fw.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            // encontrar os alvos!
            double dist[] = new double[4];
            double points[] = new double[8];
            if (filteredRegions.size() > 0) {
                TargetRegion r = (TargetRegion) filteredRegions.get(0);
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
                    r = (TargetRegion) filteredRegions.get(i);
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

                    // add to target set to tried list with sucess
                    this._targetsTried.add(new TargetSet(threshold,points,true));

                    // yes! found
                    found = true;
                }
                else {
                    // add to target set to tried list without sucess
                    this._targetsTried.add(new TargetSet(threshold,points,false));
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
                        TargetRegion r = (TargetRegion) mapName2Region.get(representante);
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

            _lastThreshold = -1; // set last threshold
            return 0;
        }

        _lastThreshold = threshold; // set last threshold

        return steps;
    }

    /**
     *
     */
    public int getLastThreshold() {
        return _lastThreshold;
    }

    private int root(int v, int[] parentTree) {
        while (parentTree[v] != v)
            v = parentTree[v];
        return v;
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
        double delta = difh + difv; // Math.max(difh, difv);

        _targetDelta = delta;

        if (delta < 12)
            return true;
        else
            return false;
    }

    public double getTargetDelta() {
        return _targetDelta;
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

    /**
     * Save grayscaled image file from integer matrix (int[][])
     */
    private void saveImage(int a[][], int w, int h, String fileName) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();
        // System.out.println("Number of Components "+number);
        int[] nBits = {8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits,false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(_width,_height);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        for (int i=0;i<h;i++) {
            for (int j = 0; j < w; j++) {
                dataBuffer.setElem(i*w+j,a[i][j]);
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

    private BufferedImage thresholdImage() {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();
        // System.out.println("Number of Components "+number);
        int[] nBits = {8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits,false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(_width,_height);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        for (int i=0;i<_height;i++) {
            for (int j = 0; j < _width; j++) {
                dataBuffer.setElem(i*_width+j,(_imageData[i][j] < _threshold ? 0 : 255));
            }
        }
        WritableRaster wr = Raster.createWritableRaster(sampleModel,dataBuffer, new Point(0,0));
        BufferedImage bf = new BufferedImage(colorModel, wr, true, null);
        return bf;
    }


    // FIND TARGETS
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // GRID DATA

    // white area sample square side
    private double _whiteArea = 0.1;

    // cell area sample square side
    private double _cellArea = 0.35;

    // min dist
    private double _confidence;

    // level
    private double _nivelParaCorrecao = 50;
    public void setNivelParaCorrecao(double n) {
        _nivelParaCorrecao = n;
        if (this._regionsAreDefined)
            this.calculateData();
    }

    private int _rows;
    private int _columns;

    private byte[][] _selection;
    private GeneralPath[][] _regions;
    private GeneralPath[][] _whiteRegions;
    private double[][] _whiteRegionsMean;

    private boolean _regionsAreDefined = false;

    public boolean[][] matrix(int rows, int columns) {
        _rows = rows;
        _columns = columns;

        _regions = new GeneralPath[_rows][_columns];
        _selection = new byte[_rows][_columns];
        _whiteRegions = new GeneralPath[_rows + 1][_columns + 1];
        _whiteRegionsMean = new double[_rows + 1][_columns + 1];

        updateRegions();

        calculateData();

        return this.getResultMatrix();
    }

    public double getConfidence() {
        return _confidence;
    }

    public double getNivelParaCorrecao() {
        return this._nivelParaCorrecao;
    }

    public void setSelectionType(int i, int j, byte type) {
        _selection[i][j]=type;
    }

    public byte getSelectionType(int i, int j) {
        return _selection[i][j];
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

        // set as true
        _regionsAreDefined = true;

    }

    public double sqrDist(double x, double y, int index) {
        double dx = x - _corners[index].getX();
        double dy = y - _corners[index].getY();
        return dx*dx+dy*dy;
    }

    public void calculateData() {

        // calcular white means
        calculateWhiteRegionMeans();
        int pixel;

        // guardar a menor confiança
        _confidence = Double.POSITIVE_INFINITY;

        // clear
        for (int i=0;i<_rows;i++) {
            for (int j=0;j<_columns;j++) {

                // fazer pela media dos 3 mais brancos
                double x[] = {
                    _whiteRegionsMean[i][j],
                    _whiteRegionsMean[i][j + 1],
                    _whiteRegionsMean[i + 1][j + 1],
                    _whiteRegionsMean[i + 1][j]
                };
                Arrays.sort(x);
                double whitemean = (x[1] + x[2]) / 2.0;

                // System.out.println("célula ("+i+","+j+")");
                Rectangle rect = _regions[i][j].getBounds();

                int x0 = (int) rect.getMinX();
                int x1 = (int) rect.getMaxX();
                int y0 = (int) rect.getMinY();
                int y1 = (int) rect.getMaxY();

                // get samples
                int N=0;
                double sampleSum=0;
                for (int ii=y0;ii<=y1;ii++) {
                    for (int jj=x0;jj<=x1;jj++) {
                        if (_regions[i][j].contains(jj,ii)) {
                            if (ii >= 0 && ii<_height && jj >= 0 && jj < _width) {
                                pixel = _imageData[ii][jj];
                                sampleSum+=pixel;
                                N++;
                            }
                        }
                    }
                }

                //
                double mean = sampleSum / (double) N;

                double level = whitemean-mean;

                _confidence = Math.min(_confidence,Math.abs(level-_nivelParaCorrecao));

                if (level > _nivelParaCorrecao)
                    _selection[i][j] = USER_DEFINED_ON;
                else
                    _selection[i][j] = USER_DEFINED_OFF;
            }
        }
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

    private boolean doesCellLevelImpliesSelection(int x, int y) {
        if (_selection[y][x] == USER_DEFINED_ON)
            return true;
        else
            return false;
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

    // GRID DATA
    ///////////////////////////////////////////////////////////////////////////



    ///////////////////////////////////////////////////////////////////////////
    // Paint Proof Image

    private int _threshold = 128;
    private BufferedImage _thresholdImage = null;
    private boolean _showThreshold = false;
    private boolean _showGrid = false;
    private boolean _showCurrentSolution = true;
    private boolean _showWhiteRegions = false;
    private boolean _showTargets = true;

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


    public void paint(Graphics g) {
        if (_img == null)
            return;

        if (!_showThreshold) {
            g.drawImage(_img, 0, 0, null);
        }
        else {
            if (_thresholdImage == null) {
                _thresholdImage = this.thresholdImage();
            }
            g.drawImage(_thresholdImage, 0, 0, null);
        }

        if (_regionsAreDefined && _showGrid) {
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

        if (_regionsAreDefined &&_showWhiteRegions) {
            for (int i = 0; i < _rows+1; i++) {
                for (int j = 0; j < _columns+1; j++) {
                    g.setColor(Color.YELLOW);
                    ( (Graphics2D) g).draw(_whiteRegions[i][j]);
                }
            }
        }

        if (_regionsAreDefined &&_showCurrentSolution) {
            g.setColor(COR_MARCA);
            for (int i = 0; i < _rows; i++) {
                for (int j = 0; j < _columns; j++) {
                    if (_selection[i][j] == LEVEL_DEFINED) {
                        if (doesCellLevelImpliesSelection(j, i)) {
                            Rectangle r = _regions[i][j].getBounds();
                            double x0 = r.getCenterX();
                            double y0 = r.getCenterY();
                            int raio = 2;
                            Shape shape = new java.awt.geom.Ellipse2D.Double(x0-raio, y0-raio, 2*raio+1, 2*raio+1);
                            ( (Graphics2D) g).fill(shape);
                            // g.fillOval((int)Math.round(x0)-2,(int)Math.round(y0)-2,5,5);
                            // ( (Graphics2D) g).fill(_regions[i][j]);
                        }
                    }
                    else if (_selection[i][j] == USER_DEFINED_ON) {
                        Rectangle r = _regions[i][j].getBounds();
                        double x0 = r.getCenterX();
                        double y0 = r.getCenterY();
                        int raio = 2;
                        Shape shape = new java.awt.geom.Ellipse2D.Double(x0-raio, y0-raio, 2*raio+1, 2*raio+1);
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
                int raio = 2;
                g.fillOval((int)p.getX()-raio,(int)p.getY()-raio,2*raio+1,2*raio+1);
            }
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

    public int get_threshold() {
        return _threshold;
    }

    public boolean is_showThreshold() {
        return _showThreshold;
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

    public void set_threshold(int _threshold) {
        this._threshold = _threshold;
        _thresholdImage =null;
    }

    public void set_showThreshold(boolean _showThreshold) {
        this._showThreshold = _showThreshold;
    }


    /**
     * Basic Routine
     */
    public static BufferedImage toGray(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        int rgb[] = {0, 0, 0, 0};
        Raster source = image.getData();

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();

        // System.out.println("Number of Components "+number);
        int[] nBits = {
            8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(w, h);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                source.getPixel(j, i, rgb);
                dataBuffer.setElem(i * w + j, (rgb[0] * 30 + rgb[1] * 59 + rgb[2] * 11) / 100);
            }
        }
        WritableRaster wr = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
        BufferedImage bf = new BufferedImage(colorModel, wr, true, null);

        //0.3*R + 0.59*G + 0.11*B
        return bf;
    }

    // Paint Proof Image
    ///////////////////////////////////////////////////////////////////////////
}



class TargetRegion {
    int _name;
    int _countPixels;
    int _minX;
    int _minY;
    int _maxX;
    int _maxY;
    int _sumX;
    int _sumY;
    int _number;
    public TargetRegion(int name, int number) {
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
