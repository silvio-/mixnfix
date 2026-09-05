package mixnfix;


import java.awt.image.BufferedImage;
import java.awt.image.Raster;

import mixnfix.ProcessImage.ControlPoint;

public class MFI2Java {

	public static int MAX_PIXELS = 1000000;
	public static int MAX_CONTROL_POINTS = 100;
	public static int MAX_TRANSFORM_POINTS =  50000;

	public static int _imgdata[] = new int[MAX_PIXELS];
	public static double _controlPoints[] = new double[MAX_CONTROL_POINTS*2];

	public static double _points[] = new double[2*MAX_TRANSFORM_POINTS];
	public static double _out[] = new double[2*MAX_TRANSFORM_POINTS];

	public static ProcessImage.CellMap _cellMap; // current CellMap

	public static ProcessImage.Img     _img;      // current image

	public static void setConstraints(
			int[]   thresholds,          // thresholds
			int     minPixelWidth,       // valid region
			int     maxPixelWidth,
			int     minPixelHeight,
			int     maxPixelHeight,
			int     minNumPixels,
			int     maxNumPixels,
			double   pixelDensity,
			int     numClosest,          // candidates closest points
			double   minSide,             // 4-targets
			double   difAngle,
			double   targetRadius,
			double   correctSideRatio,
			double   sideRatioTolerance,
			int     phase,
			double   controlPointsRadius,  // control points
			double   leftMargin,           // regionSearchArea
			double   rightMargin,
			double   topMargin,
			double   bottomMargin
	) {
//		// thresholds
//		// printf("thresholds\n");
//		int cthresholds[MAX_THRESHOLDS];
//		int numThresholds = (*env)->GetArrayLength(env,thresholds);
//		(*env)->GetIntArrayRegion(env, thresholds, 0, numThresholds, (jint *) cthresholds);

		ProcessImage.resetThresholds();
		int i;
		for (int t: thresholds) {
			ProcessImage.addThresholds(t);
			// printf("threshold: %d\n",cthresholds[i]);
		}

		ProcessImage.setRegionSearchMargins(leftMargin, rightMargin, topMargin, bottomMargin);

		/*printf("valid region: %d %d %d %d %d %d %.3f\n",
    		          minPixelWidth,
    		          maxPixelWidth,
    		          minPixelHeight,
    		          maxPixelHeight,
    		          minNumPixels,
    		          maxNumPixels,
    		          pixelDensity);*/
		// valid region
		ProcessImage.setValidRegionContraints(
				minPixelWidth,
				maxPixelWidth,
				minPixelHeight,
				maxPixelHeight,
				minNumPixels,
				maxNumPixels,
				pixelDensity);

		// printf("closest: %d \n",numClosest);
		// candidate closest
		ProcessImage.setNumClosestPoints(numClosest);

		// printf("target constraints: %.2f %.2f %.2f %.2f %.2f %d\n",minSide,difAngle,targetRadius,correctSideRatio,sideRatioTolerance,phase);
		// target constraints
		ProcessImage.set4TargetContraints(
				minSide,
				difAngle,
				targetRadius,
				correctSideRatio,
				sideRatioTolerance,
				phase);

		// printf("control point radius: %.2f\n",controlPointsRadius);
		// control points radius
		ProcessImage.setControlPointRadius(controlPointsRadius);

		// printf("setConstraints\n");
	}

    public static void newCellMap(double width, double height, int maxControlPoints, int maxQuads) {
    	_cellMap = new ProcessImage.CellMap(width, height, maxControlPoints, maxQuads, 1);
    }

    public static void releaseCellMap() {
    	_cellMap = null;
    }

    public static int addControlPoint(int id, double x, double y) {
    	return _cellMap.addControlPoint(id, x, y).id;
    }

    public static int addQuad(
        int cp0, int cp1, int cp2, int cp3
    ) {
    	_cellMap.addQuad(cp0, cp1, cp2, cp3);
    	return 1;
    }

    public static boolean fitToImage(
        byte[] data, int width, int height, double[] controlPoints
    ) {
 
        // initialize image
        _img = new ProcessImage.Img();

        // pictures taken with modern cameras easily exceed the old fixed
        // 1 megapixel buffer: grow it on demand instead of crashing
        int numPixels = width * height;
        if (_imgdata.length < numPixels)
            _imgdata = new int[numPixels];

        for (int i=0;i<height;i++)
            for (int j=0;j<width;j++)
            	_imgdata[i*width+j] = 0xFF & data[i*width+j];
        
        _img.data = _imgdata;
        _img.w = width;
        _img.h = height;
        
        //printf("entering fitToImage\n");      
        int status = ProcessImage.fitToImage(_img, _cellMap);
        
        //printf("found?\n");      
        if (status != 0) {

           // printf("yes\n");      
            int maxControlPointId = 0;
            int i;
            for (i=0;i<_cellMap.numControlPoints;i++) {
                ControlPoint cp = _cellMap.cps[i];        
                _controlPoints[2*cp.id]   = cp.xx;
                _controlPoints[2*cp.id+1] = cp.yy;
                maxControlPointId = (maxControlPointId >= cp.id ? maxControlPointId : cp.id);
            }    
            System.arraycopy(_controlPoints, 0, controlPoints, 0, _cellMap.numControlPoints*2);
        }
        
        return status != 0;              	
    }

    public static double sampleCell(
        double x, double y, double w0, double h0, double w1, double h1, double w2, double h2, byte whiteSampleSet
    ){
        
        // initialize cell
        ProcessImage.Cell c = new ProcessImage.Cell(0,x,y,w0,h0,w1,h1,w2,h2,whiteSampleSet);
        
        // with the current processed CellMap
        // and the current image sample this Cell
        ProcessImage.sample(_img, _cellMap, c);
        
        return c.intensity;    
    }

    public static void tranformPoints(double[] source, double[] target, int n) {
        ProcessImage.findMappingOfPointsByQuads(_cellMap,source,target,n);
    }

    static byte _data[] = new byte[10000000];

    /**
     * Make sure the grey level buffer is big enough for this picture,
     * allocating a new one when it is not (pictures of modern cameras can
     * be much bigger than the historical fixed size buffers).
     */
    public static byte[] ensureBuffer(byte[] data, BufferedImage image) {
        int needed = image.getWidth() * image.getHeight();
        if (data == null || data.length < needed)
            return new byte[needed];
        return data;
    }

    public static void loadImageToBuffer(BufferedImage image, byte[] data) {
        int w = image.getWidth();
        int h = image.getHeight();

        boolean color = (image.getType() != BufferedImage.TYPE_BYTE_GRAY);

        int rgb[] = {0, 0, 0, 0};
        Raster source = image.getData();
        int k=0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                source.getPixel(j, i, rgb);
                if (color) {
                    data[k++] = (byte) ( (rgb[0] * 30 + rgb[1] * 59 + rgb[2] * 11) / 100);
                }
                else {
                    data[k++] = (byte) rgb[0];
                }
            }
        }
    }

    public static void main(String argv[]) throws Exception {
        int[] thresholds = {50, 40, 60, 70, 80};

        MFI2Java.sampleCell(0,0,0,0,0,0,0,0,(byte) 0);
        MFI2Java.sampleCell(0,0,0,0,0,0,0,0,(byte) 0);

        // loop time
        long t0 = System.currentTimeMillis();
        int N = 100000000;
        for (int i=0;i<N;i++) {
            i = i + 1;
            i = i - 1;
        }
        long t1 = System.currentTimeMillis();
        long loopTime = t1-t0;


        // call sampleCell with loop time
        t0 = System.currentTimeMillis();
        for (int i=0;i<N;i++) {
            MFI2Java.sampleCell(0,0,0,0,0,0,0,0,(byte) 0);
        }
        t1 = System.currentTimeMillis();
        long loopAndSampleTime = t1-t0;

        /* debug
        System.out.println(
        String.format("sample cell: %.12fseg. tempo por chamada: %.12f",
        (double)(loopAndSampleTime-loopTime)/(double)1000.0,
        (double)(loopAndSampleTime-loopTime)/1000.0/(double)N)); */

    }
}
