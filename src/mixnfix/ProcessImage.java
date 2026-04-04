package mixnfix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessImage {

	/******************************************************************************
	 ** Region ********************************************************************
	 ******************************************************************************/

	public static final boolean __DEBUG_REGIONS = false;        // debug find regions algorithm

	// Find Regions Algorithm needed definitions and data structures
	public static class Region {
	   int index;

	   int size;     // number of points in this region
	   int sumx;
	   int sumy;
	   int minx;
	   int maxx;
	   int miny;
	   int maxy;


	   Region representant;   // if representant=this then we are on 
	                                   // a representant region otherwise 
	                                   // we are on a non-representant region.

	   Region next;           // if we are on a representant region
	                                   // then this will indicate the index
	                                   // of the next representant region.
	                                   // Else it will represent the next non-
	                                   // representant region of the same rep
	                                   // resentant

	   Region previous;       // if we are on a representant region
	                                   // then this will indicate the index
	                                   // of the previous representant region.
	                                   // otherwise this has no use.

	   int numChilds;                  // number of childs

	   Region child;                   // childs of this region will be the 
	                                   // non-representant region of this region

	   double centerx;                  // this will be used only when the region is a valid one
	   double centery;

	   char flag;                      // a flag that can be used by anyone

	   
	   /**
	    * Create new region with a single pixel
	    */
	   public Region(int index, int i, int j) {
		   this.index = index;

		   this.size = 1;

		   this.sumx = j;
		   this.sumy = i;

		   this.minx = j;
		   this.maxx = j;

		   this.miny = i;
		   this.maxy = i;

		   this.representant = this;

		   this.next = null;
		   this.previous = null;

		   this.numChilds = 0;

		   this.flag = 0;
	   }
	   
	   public Region getRepresentant() {
		   return representant;
	   }
	   
	   /**
	    * Test if Region is valid
	    */ 
	   public boolean validRegion() {
		   int pixelWidth = this.maxx - this.minx + 1;      
		   int pixelHeight = this.maxy - this.miny + 1;      
		   if (pixelWidth >= _MIN_PIXEL_WIDTH &&
				   pixelHeight >= _MIN_PIXEL_HEIGHT &&
				   pixelWidth <= _MAX_PIXEL_WIDTH &&
				   pixelHeight <= _MAX_PIXEL_HEIGHT &&
				   this.size >= _MIN_NUM_PIXELS &&
				   this.size <= _MAX_NUM_PIXELS &&
				   ((double)this.size / (pixelWidth*pixelHeight)) >= _PIXEL_DENSITY)  // test density
			   return true;
		   else
			   return false;
	   }


	   public void calculateCenter() {
		   if (this.size > 0) {
			   this.centerx = (double)this.sumx / (double)this.size;
			   this.centery = (double)this.sumy / (double)this.size;                                                                                                                                                                                                                                
		   }   
	   }
	   
	   
	   public void addPointToRegion(int i, int j) {

		   this.size += 1;

		   this.sumx += j;
		   this.sumy += i;

		   this.minx = Math.min(this.minx,j);
		   this.maxx = Math.max(this.maxx,j);

		   this.miny = Math.min(this.miny,i);
		   this.maxy = Math.max(this.maxy,i);

	   }
	

	};

	public static final int MAX_WIDTH=1000;            // maximum width of an image
	public static final int MAX_HEIGHT=1000;           // maximum height of an image
	public static final int MAX_POINTS=1000000;        // maximum number of points
	public static final int MAX_REGIONS=1000000;       // maximum number of regions

	public static final int MAX_THRESHOLDS=8;          // maximum number of thresholds to calculate simultaneously

	/**
	 * clear thresholds
	 */
	public static void resetThresholds() {
	     _NUM_THRESHOLDS = 0;
	}

	/**
	 * add threshold
	 */
	public static void addThresholds(int t) {
	     _thresholds[_NUM_THRESHOLDS % MAX_THRESHOLDS] = t;
	     _NUM_THRESHOLDS++;     
	}

	
	/**
	 * clear thresholds
	 */
	public static void setRegionSearchMargins(double left, double right, double top, double bottom) {
	     _leftMargin = left;
	     _rightMargin = right;
	     _topMargin = top;
	     _bottomMargin = bottom;
	}

	/******************************************************************************
	 ** Valid Regions *************************************************************
	 ******************************************************************************/

	/**
	 * set important parameters to
	 * characterize a valid region.
	 */
	public static void setValidRegionContraints(
	       int minPixelWidth,
	       int maxPixelWidth,
	       int minPixelHeight,
	       int maxPixelHeight,
	       int minNumPixels,
	       int maxNumPixels,
	       double pixelDensity ) {
	    _MIN_PIXEL_WIDTH    = minPixelWidth;
	    _MAX_PIXEL_WIDTH    = maxPixelWidth;
	    _MIN_PIXEL_HEIGHT   = minPixelHeight;
	    _MAX_PIXEL_HEIGHT   = maxPixelHeight;
	    _MIN_NUM_PIXELS     = minNumPixels;
	    _MAX_NUM_PIXELS     = maxNumPixels;
	    _PIXEL_DENSITY    = pixelDensity;
	}
	


	/******************************************************************************
	 ** Candidate Regions *********************************************************
	 ******************************************************************************/

	public static final boolean __DEBUG_CANDIDATES = false;
	 
	public static final int MAX_CLOSEST = 10;   // maximum number of closest points to keep

	/**
	 * Parameter for targets candidates 
	 * identification. This represents the
	 * maximum number of points that are
	 * close enough to a corner to be defined
	 * as candidate.
	 */
	public static void setNumClosestPoints(int numClosest) {
	     _NUM_CLOSEST = Math.min(numClosest,MAX_CLOSEST);
	}

	/******************************************************************************
	 ** Find Targets **************************************************************
	 ******************************************************************************/

	public static final boolean __DEBUG_TARGETS = false;

	public static final int MAX_TARGETS = 5;    // maximum number of valid 4-targets

	/**
	 * set important parameters to
	 * characterize a 4-target.
	 */
	public static void set4TargetContraints(
	   double minSide,          
	   double difAngle,
	   double targetRadius,
	   double correctSideRatio,
	   double sideRatioTolerance,
	   int phase){
	   _MIN_SIDE = minSide;
	   _DIF_ANGLE = difAngle;
	   _TARGET_RADIUS = targetRadius;
	   _CORRECT_SIDE_RATIO = correctSideRatio;
	   _SIDE_RATIO_TOLERANTE = sideRatioTolerance;
	   _PHASE = phase;
	}

	/******************************************************************************
	 ** Find Control Points *******************************************************
	 ******************************************************************************/

	public static final boolean __DEBUG_CONTROL_POINTS = false;

	/**
	 * parameter to localize control point 
	 * on image.
	 */
	public static void setControlPointRadius(double r) {
	     _RADIUS_CONTROL_POINT_TOLERANCE = r;
	}

	/******************************************************************************
	 ** CellMap *******************************************************************
	 ******************************************************************************/

	public static final boolean __DEBUG_CELLSAMPLE = false;
	public static final boolean __DEBUG_READMAP = false;
	public static final boolean __DEBUG_TRIANGLE_CONVEX_COMBINATION = false;

	public static class Cell {
	   // specification of a cell
	   int id;
	   double x;
	   double y;
	   double w0,h0;
	   double w1,h1;
	   double w2,h2;
	   
	   int whiteSampleSet; //NOTE: unsigned char whiteSampleSet;

	   // some useful calculated data
	   double intensity;
	   double white;
	   
	   public Cell(
		   int id, 
		   double x, double y, 
		   double w0, double h0, 
		   double w1, double h1, 
		   double w2, double h2,
		   int whiteSampleSet
	   ) {
		   this.id = id;
		   this.x = x; this.y = y;
		   this.w0 = w0; this.h0 = h0;
		   this.w1 = w1; this.h1 = h1;
		   this.w2 = w2; this.h2 = h2;
	   }
	}
	 
	public static class ControlPoint {
	   public ControlPoint(int id, double x, double y) {
		   this.id = id;
		   this.x = x;
		   this.y = y;
	   }
		
	   int id;         // identification of control point
	   double x,y;      // theorical location of control point

	   double xx,yy;    // practical location of control point found
	   Region region; // region that was used as the
	}

	public static class Triangle {
		public Triangle(
			ControlPoint p1,
			ControlPoint p2,
			ControlPoint p3 ) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.whiteSampleSet = 0;
		}

		public ControlPoint getP1() { return p1; }
		public ControlPoint getP2() { return p2; }
		public ControlPoint getP3() { return p3; }

	   // specification of a cell
	   ControlPoint p1, p2, p3;

	   int whiteSampleSet; // unsigned char whiteSampleSet;
	}

	/**
	 * A quadrilateral defined by 4 control points with a precomputed
	 * projective homography mapping from theoretical to practical coordinates.
	 */
	public static class Quad {
		ControlPoint p0, p1, p2, p3; // TL, TR, BR, BL in theoretical coords
		// Precomputed homography coefficients (theoretical -> practical)
		double ha, hb, hc, hd, he, hf, hg, hh;
		// Theoretical bounding box (axis-aligned in theoretical space)
		double minX, maxX, minY, maxY;
		// Flag: true if the homography is valid (non-degenerate)
		boolean valid;

		public Quad(ControlPoint p0, ControlPoint p1, ControlPoint p2, ControlPoint p3) {
			this.p0 = p0; // TL
			this.p1 = p1; // TR
			this.p2 = p2; // BR
			this.p3 = p3; // BL
			this.minX = Math.min(Math.min(p0.x, p1.x), Math.min(p2.x, p3.x));
			this.maxX = Math.max(Math.max(p0.x, p1.x), Math.max(p2.x, p3.x));
			this.minY = Math.min(Math.min(p0.y, p1.y), Math.min(p2.y, p3.y));
			this.maxY = Math.max(Math.max(p0.y, p1.y), Math.max(p2.y, p3.y));
			this.valid = false;
		}

		/**
		 * Compute projective homography coefficients that map unit square
		 * [0,1]x[0,1] to the practical (xx,yy) positions of the 4 corners.
		 * Call this after control point practical positions are established.
		 * Sets valid=false if the quad is degenerate (e.g., collapsed corners).
		 */
		public void computeHomography() {
			double x0 = p0.xx, y0 = p0.yy; // TL practical
			double x1 = p1.xx, y1 = p1.yy; // TR practical
			double x2 = p2.xx, y2 = p2.yy; // BR practical
			double x3 = p3.xx, y3 = p3.yy; // BL practical

			double dx1 = x1 - x2, dy1 = y1 - y2;
			double dx2 = x3 - x2, dy2 = y3 - y2;
			double dx3 = x0 - x1 + x2 - x3, dy3 = y0 - y1 + y2 - y3;

			double det = dx1 * dy2 - dy1 * dx2;

			if (Math.abs(det) < 1.0e-10) {
				valid = false;
				return;
			}

			hg = (dx3 * dy2 - dy3 * dx2) / det;
			hh = (dx1 * dy3 - dy1 * dx3) / det;

			// Check that w = hg*u + hh*v + 1 > 0 at all 4 unit square corners.
			// w(0,0)=1, w(1,0)=hg+1, w(0,1)=hh+1, w(1,1)=hg+hh+1
			double wMin = Math.min(Math.min(1.0, hg + 1.0),
			                       Math.min(hh + 1.0, hg + hh + 1.0));
			if (wMin < 1.0e-6) {
				valid = false;
				return;
			}

			ha = x1 - x0 + hg * x1;
			hb = x3 - x0 + hh * x3;
			hc = x0;
			hd = y1 - y0 + hg * y1;
			he = y3 - y0 + hh * y3;
			hf = y0;
			valid = true;
		}

		/**
		 * Check if theoretical point (x,y) is inside this quad's bounding box.
		 */
		public boolean contains(double x, double y) {
			return x >= minX - EPSILON && x <= maxX + EPSILON &&
			       y >= minY - EPSILON && y <= maxY + EPSILON;
		}

		/**
		 * Map a theoretical point (x,y) to practical pixel coordinates.
		 * Uses projective homography if valid, otherwise bilinear fallback.
		 */
		public void map(double x, double y, double[] output, int offset) {
			double u = (x - minX) / (maxX - minX);
			double v = (y - minY) / (maxY - minY);

			if (valid) {
				double w = hg * u + hh * v + 1.0;
				output[offset]     = (ha * u + hb * v + hc) / w;
				output[offset + 1] = (hd * u + he * v + hf) / w;
			} else {
				// Bilinear fallback for degenerate quads
				output[offset]     = (1-u)*(1-v)*p0.xx + u*(1-v)*p1.xx + u*v*p2.xx + (1-u)*v*p3.xx;
				output[offset + 1] = (1-u)*(1-v)*p0.yy + u*(1-v)*p1.yy + u*v*p2.yy + (1-u)*v*p3.yy;
			}
		}
	}

	public static class CellMap {
		double width;           // width of the map
		double height;          // height of the map

		int numControlPoints;  // number of control points
		ControlPoint cps[];     // control points (a corner does not appear here)

		int numTriangles;      // number of triangles
		Triangle triangles[];  // triangles

		int numQuads;          // number of quads
		Quad quads[];          // quads for piecewise projective mapping

		int numCells;          // number of cells
		Cell cells[];          // cells

		
		public int getNumberOfTriangles() {
			return numTriangles;
		}
		
		public int getNumberOfCells() {
			return numCells;
		}

		public int getNumberOfControlPoints() {
			return numControlPoints;
		}
		
		public Triangle getTriangle(int index) {
			return triangles[index];
		}
		
		public ControlPoint addControlPoint(int id, double x, double y) {
			ControlPoint cp = new ControlPoint(id,x,y);
			this.cps[this.numControlPoints] = cp;
			this.numControlPoints++;
			return cp;
		}

		public Triangle addTriangle(int cp1, int cp2, int cp3) {
	    	ControlPoint p1=null, p2=null, p3=null;
			
	    	int k=0;
			for (int i=0;i<this.numControlPoints;i++) {
	    		ControlPoint p = this.cps[i];
	            if (cp1 == p.id) { p1 = p; k++; }
	            if (cp2 == p.id) { p2 = p; k++; }
	            if (cp3 == p.id) { p3 = p; k++;}
	    	}
	    	
	        if (k < 3)
	            System.out.print(String.format("Problema com triangulo %d %d %d\n",cp1,cp2,cp3));     

	        Triangle t = new Triangle(p1,p2,p3);
	        this.triangles[this.numTriangles] = t;
	        this.numTriangles++;
	        return t;
		}
		
		public CellMap(String fileName) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			int numCells, numControlPoints, numTriangles;
			double W,H;
			String tokens[];

			// read first line
			tokens= br.readLine().split(" ");
			numCells = Integer.parseInt(tokens[0]);
			numControlPoints = Integer.parseInt(tokens[1]);
			numTriangles = Integer.parseInt(tokens[2]);

			// read second line
			tokens= br.readLine().split(" ");
			W = Double.parseDouble(tokens[0]);
			H = Double.parseDouble(tokens[1]);

			// control points
			cps = new ControlPoint[numControlPoints];
			for (int i=0;i<numControlPoints;i++) {
				tokens = br.readLine().split(" ");
				cps[i] = new ControlPoint(
						Integer.parseInt(tokens[0]),   // id
						Double.parseDouble(tokens[1]), // x
						Double.parseDouble(tokens[2])  // y
				);
			}

			// triangles 
			for (int i=0;i<numTriangles;i++) {
				tokens = br.readLine().split(" ");
				triangles[i] = new Triangle(
						cps[Integer.parseInt(tokens[0])-1],
						cps[Integer.parseInt(tokens[1])-1],
						cps[Integer.parseInt(tokens[2])-1]);
			}		   

			// cellmap 
			for (int i=0;i<numCells;i++) {
				tokens = br.readLine().split(" ");
				cells[i] = new Cell(
						Integer.parseInt(tokens[0]), // id
						Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2]), // x  y
						Double.parseDouble(tokens[3]), Double.parseDouble(tokens[4]), // w1 h1
						Double.parseDouble(tokens[5]), Double.parseDouble(tokens[6]), // w2 h2
						Double.parseDouble(tokens[7]), Double.parseDouble(tokens[8]), // w3 h3
						Integer.parseInt(tokens[9]) // whiteSampleSet
				);
			}		   
		}

		public CellMap(double width, double height, int maxControlPoints, int maxTriangles, int maxCells) {
			this.width = width;
			this.height = height;
			this.cells = new Cell[maxCells];
			this.numCells = 0;
			this.triangles = new Triangle[maxTriangles];
			this.numTriangles = 0;
			this.cps = new ControlPoint[maxControlPoints];
			this.numControlPoints = 0;
			this.quads = new Quad[20]; // max 12 quads for 3x7 grid, with margin
			this.numQuads = 0;
		}

		/**
		 * Build quadrilaterals from the control point grid structure.
		 * Sorts control points into columns (by x) and rows (by y),
		 * then creates quads from consecutive row pairs in adjacent columns.
		 * Call after findControlPoints() has established all practical (xx,yy) positions.
		 */
		public void buildQuadsFromGrid() {
			// Collect distinct x values to identify columns
			java.util.TreeSet<Double> xSet = new java.util.TreeSet<>();
			for (int i = 0; i < numControlPoints; i++)
				xSet.add(cps[i].x);

			double[] xValues = new double[xSet.size()];
			int xi = 0;
			for (double xv : xSet)
				xValues[xi++] = xv;

			int numCols = xValues.length;

			// For each column, collect control points sorted by y
			ControlPoint[][] grid = new ControlPoint[numCols][];
			for (int col = 0; col < numCols; col++) {
				java.util.ArrayList<ControlPoint> colPoints = new java.util.ArrayList<>();
				for (int i = 0; i < numControlPoints; i++) {
					if (Math.abs(cps[i].x - xValues[col]) < EPSILON)
						colPoints.add(cps[i]);
				}
				colPoints.sort((a, b) -> Double.compare(a.y, b.y));
				grid[col] = colPoints.toArray(new ControlPoint[0]);
			}

			// Build quads between adjacent columns
			numQuads = 0;
			for (int col = 0; col < numCols - 1; col++) {
				int numRows = Math.min(grid[col].length, grid[col + 1].length);
				for (int row = 0; row < numRows - 1; row++) {
					// TL = grid[col][row],   TR = grid[col+1][row]
					// BL = grid[col][row+1], BR = grid[col+1][row+1]
					Quad q = new Quad(
						grid[col][row],      // p0 = TL
						grid[col + 1][row],  // p1 = TR
						grid[col + 1][row + 1], // p2 = BR
						grid[col][row + 1]   // p3 = BL
					);
					q.computeHomography();
					quads[numQuads++] = q;
				}
			}
		}
	}
	
	/******************************************************************************
	 ** Image *********************************************************************
	 ******************************************************************************/

	public static class Img {
	   int w;
	   int h;
	   int data[];
	   public int getW() { return w; }
	   public int getH() { return h; }
	   public int getValue(int i, int j) { 
		   return data[i * w + j]; 
	   }
	};


//	/******************************************************************************
//	 ** High Level Routine ********************************************************
//	 ******************************************************************************/
//
//	public static final boolean __DEBUG_SAVE_CORRECT_IMAGE = false;            // debug find regions algorithm
//	public static final boolean __DEBUG_SAVE_INCORRECT_IMAGES = false;         // debug find regions algorithm
//
	/**
	 * given an array with n pairs of coordinates
	 * tranform these points using the triangles
	 * and write the result in target. source can
	 * be equal to target.
	 */
	public static void findMappingOfPointsByTriangles(CellMap M, double source[], double target[], int n) {
	     int i;
	     for (i=0;i<n;i++) {
	         double x = source[2*i];
	         double y = source[2*i+1];
	         findMappingOfPointByQuads(M,x,y,target,2*i);
	     }
	}

	/**
	 * Make the cellMap control points fit to image.
	 */
	public static int fitToImage(Img img, CellMap cellMap) {

	    System.out.println("threshold\n");

	    thresholdMatrix(img); // calculate regions for the thresholds

	    int k;
	    for (k = 0; k < _NUM_THRESHOLDS; k++) {

	        System.out.format("testing threshold %d\n",_thresholds[k]);
	        Region r = _representantRegionList[k];
	        
	        filterCandidates(img.getW(), img.getH(), r); // find set of points to search for targets
	        sort(_candidates, _numCandidates); // sort _candidates by x coordinate

	        System.out.format("find possible targets (numCandidates: %d)\n",_numCandidates);
	        findPossibleTargets(_candidates, _numCandidates); // find all possible targets

//	        #ifdef   __DEBUG_SAVE_INCORRECT_IMAGES            
//	        System.println("save debug image...\n",_numCandidates);
//	        if (_numTargets == 0) {
//	            char filename[100];
//	            System.out.println(filename,"c:/workspace/mnfimg/c/img/debug-t%d-i%d.eps",_thresholds[k],____COUNT++);
//	            System.out.println("saving file %s\n",filename);
//	            writeEPSwithCandidatesAndTargetsAndMapping(filename,img,_thresholds[k],cellMap,r);                
//	            System.out.println("file saved!\n");
//	        }
//	        #endif

	        // System.println("found %d 4-targets...\n",_numTargets);

	        int kk;
	        double x[] = new double[8];
	        for (kk = 0; kk < _numTargets; kk++) {

	            extractTargetPositions(_candidates, _targets, (4 * kk), x);
	            System.out.format("extracted target positions (%.3f,%.3f), (%.3f,%.3f), (%.3f,%.3f), (%.3f,%.3f)...\n",x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7]);


	            System.out.format("find control points...\n");

	            boolean status = findControlPoints(x, cellMap, r); // find all possible targets

	            // System.println("finished control points\n");

	            if (status == true) {

	                // Build piecewise projective quad grid from control points
	                cellMap.buildQuadsFromGrid();

	                // sampleCellIntensities(img, cellMap); // sample cell intensities

	                // System.println("OK! found everything!\n");


//	                #ifdef   __DEBUG_SAVE_CORRECT_IMAGE
//	                char filename[100];
//	                sSystem.println(filename,"c:/workspace/mnfimg/c/img/debug-t%d-i%d.eps",_thresholds[k],____COUNT++);
//	                System.println("saving file %s\n",filename);
//	                writeEPSwithCandidatesAndTargetsAndMapping(filename,img,_thresholds[k],cellMap,r);
//	                System.println("file saved!\n");
//	                #endif

	                return 1;
	            }
	            else {
//	                #ifdef   __DEBUG_SAVE_INCORRECT_IMAGES            
//	                char filename[100];
//	                sSystem.println(filename,"c:/workspace/mnfimg/c/img/problem-t%d-i%d.eps",_thresholds[k],____COUNT++);
//	                System.println("saving file %s\n",filename);
//	                writeEPSwithCandidatesAndTargetsAndMapping(filename,img,_thresholds[k],cellMap,r);                
//	                System.println("file saved!\n");
//	                #endif
	            }
	        }
	    }
	    // System.println("did not find all control points!\n");
	    return 0;
	}

//	#include "mfi.h"
//	#include <stdio.h>
//	#include <stdlib.h>
//	#include <math.h>
//	#include <time.h>
//	#include <dirent.h>
//	#include <process.h>

	public static final double EPSILON=1.0e-5;

//	public static final boolean __DEBUG =false;
//
////	#define MIN(a,b) a <= b ? a : b
////	#define MAX(a,b) a >= b ? a : b

	/******************************************************************************
	 * FIND TARGETS CODE
	 ******************************************************************************/

	public static double _MIN_SIDE = 100;               // minimum side of a valid 4-targets rectangle

	public static double _DIF_ANGLE = 5;                // angle formed by the vertex 0 and the
	                                     // vertex 1 with the vertex 0 and vertex 3 (Counter Clockwise
	                                     // ordered) has a DIF_ANGLE (in degrees) tolerance

	public static double _TARGET_RADIUS = 30;           // the vertex opposed to the first vertex must be inside
	                                     // a circle of radius TARGET_RADIUS centered on
	                                     // the "theoretical" opposed vertex.

	public static double _CORRECT_SIDE_RATIO = 1.475f;  // larger rectangle side divided by smaller rectangle side

	public static double _SIDE_RATIO_TOLERANTE = 1;     // tolerance over the correct ratio

	public static int _PHASE = 0;                      // in clockwise direction, the closest point to the origin 
	                                     // on the original CellMap is, in the picture, closest to
	                                     // what corner of the picture? 
	                                     // (i.e. 0 if in the closest to the origin of the picture
	                                     // (0,0) corner, 1 if closest to the (W,0) corner, 2
	                                     // if closest to the (W,H) corner, 3 if closest to the (0,H) 
	                                     // corner.

	public static int _targets[] = new int[4*MAX_TARGETS];    // (OUTPUT) indexes of the valid 4-targets (CCW sorted) found
	public static int _numTargets;                            // (OUTPUT) total number of valid 4-targets found

	/**
	 * find valid 4-targets rectangles satisfying the 
	 * given constraints.
	 */
	public static void findPossibleTargets(double x[], int n) {
	    int a[] = {-1,-1,-1,-1};
	    int p[] = {1,2,3,4};
	    double xx[] = {0,0,0,0};
	    double yy[] = {0,0,0,0};
	    double costheta[] = {0,0,0,0};
	    double length[] = {0,0,0,0};
	    
	    _numTargets = 0;

	    double dif_costheta = Math.cos((90 - _DIF_ANGLE) * Math.PI / 180.0);

	    double x1,y1,x2,y2,x3,y3,x4,y4;
	    double w12,w13,w42,w43;
	    double costheta1,costheta2;

	    int i=0;
	    while (i >= 0) {
	        
	        a[i]++;
	        
	        
	        if (a[i] > n-(4-i)) {
	           i--;
	           continue;
	        }

//	        #ifdef __DEBUG_TARGETS
//	        int kk;
//	        for (kk=0;kk<=i;kk++)
//	            System.println("%d ",a[kk]);
//	        System.println("\n",a[i]);           
//	        #endif
	        
	        if (i == 0) {

	            xx[i] = x[2*a[i]];   
	            yy[i] = x[2*a[i]+1];
	            costheta[i] = 0;
	             
	            x1 = x[2*a[0]];    // initialize point 1
	            y1 = x[2*a[0]+1];

	            a[i+1] = a[i];
	            i++;
	        }

	        else if (i == 1) { // test what?

	            xx[i] = x[2*a[i]];  // second point
	            yy[i] = x[2*a[i]+1];
	            length[i] = Math.sqrt((xx[i]-xx[0]) * (xx[i]-xx[0]) + (yy[i]-yy[0]) * (yy[i]-yy[0]));
	            costheta[i] = (1 * (yy[i] - yy[0]))/length[i]; // = (0 * (xx[i] - xx[0]) + (1 * (yy[i] - yy[0])))/length[i];

	            if (length[i] < _MIN_SIDE) { // less then the minsize => less then diagonal

//	                #ifdef __DEBUG_TARGETS
//	                System.println("length 01 is too small\n");           
//	                #endif

	                continue;
	            }                

	            a[i+1] = a[i];
	            i++;

	        } // test what?
	                        
	        else if (i == 2) { // test angle and ratio

	            xx[i] = x[2*a[i]];  // third point
	            yy[i] = x[2*a[i]+1];
	            length[i] = Math.sqrt((xx[i]-xx[0]) * (xx[i]-xx[0]) + (yy[i]-yy[0]) * (yy[i]-yy[0]));
	            costheta[i] = (1 * (yy[i] - yy[0]))/length[i]; // = (0 * (xx[i] - xx[0]) + (1 * (yy[i] - yy[0])))/length[i];

	            if (length[2] < _MIN_SIDE) {

	                System.out.format("length 02 is too small\n");           

	                continue;

	            }

	            double length12 = Math.sqrt((xx[2]-xx[1]) * (xx[2]-xx[1]) + (yy[2]-yy[1]) * (yy[2]-yy[1]));
	            if (length12 < _MIN_SIDE) {

	                System.out.format("length 12 is too small\n");           

	                continue;

	            }

	            a[i+1] = a[i];
	            i++;
	                    
	        } // test angle and ratio
	        
	        else if (i == 3) { // test angle and ratio and geometry
	        
	            xx[i] = x[2*a[i]];  // third point
	            yy[i] = x[2*a[i]+1];
	            length[i] = Math.sqrt((xx[i]-xx[0]) * (xx[i]-xx[0]) + (yy[i]-yy[0]) * (yy[i]-yy[0]));
	            costheta[i] = (1 * (yy[i] - yy[0]))/length[i]; // = (0 * (xx[i] - xx[0]) + (1 * (yy[i] - yy[0])))/length[i];

	            if (length[3] < _MIN_SIDE) {

	                System.out.format("length 03 is too small\n");           

	                continue;

	            }
	            
	            // sort permutation vector by costheta
	            // the angle considered is the one formed
	            // by the vector (0,-1) starting at the point 
	            // with smallest x-coordinate
	            p[0] = 0; p[1] = 1; p[2] = 2; p[3] = 3;
	            for (int k=2;k<=3;k++) {
	                int j = k-1;
	                int pk = p[k];                
	                while (j > 0 && costheta[p[j]] > costheta[pk]) {
	                   p[j+1]=p[j];
	                   j--;
	                }
	                j++;
	                p[j] = pk;
	            }

	            System.out.format("permutation was %d %d %d %d\n",a[p[0]],a[p[1]],a[p[2]],a[p[3]]);           
	            

	            // calcular razao AB / AD
	            double r = (length[p[1]] > length[p[3]] ? length[p[1]]/length[p[3]] : length[p[3]]/length[p[1]]);
	            if (r < _CORRECT_SIDE_RATIO- _SIDE_RATIO_TOLERANTE|| r > _CORRECT_SIDE_RATIO + _SIDE_RATIO_TOLERANTE) {

	                System.out.format("ratio is incompatible\n");           

	                continue;
	            }

	            // calcular A = p[0], B = p[1], C = p[2], D = p[3]
	            double senThetaP1 = Math.sqrt(1.0 - costheta[p[1]] * costheta[p[1]]);
	            double senThetaP3 = Math.sqrt(1.0 - costheta[p[3]] * costheta[p[3]]);
	            double cosBAD = costheta[p[1]] * costheta[p[3]] + senThetaP1 * senThetaP3;
	            
	            if (cosBAD > dif_costheta || cosBAD < -dif_costheta) { // the angle must be between 88 and 92 degrees 

	            	System.out.format("AB^AD angle is incompatible\n");           

	               continue;
	            }

	            System.out.format("BÂD angle is %.3f\n", Math.acos(cosBAD));           
	            
	            // calcular o ponto C teórico
	            double xC = xx[p[1]] + xx[p[3]] - xx[p[0]];
	            double yC = yy[p[1]] + yy[p[3]] - yy[p[0]];
	            
	            double dist = Math.sqrt((xC - xx[p[2]]) * (xC - xx[p[2]]) + (yC - yy[p[2]]) * (yC - yy[p[2]]));
	            
	            if (dist > _TARGET_RADIUS) {

	            	System.out.format("C teórico muito distante de C real (%3.4f, %3.4f) e (%3.4f, %3.4f), dist = %3.4f \n",
	                       xC,yC,xx[p[2]],yy[p[2]],dist);           

	               continue;
	            }

	            System.out.format("ponto C está a uma dist de %.3f\n",dist);           
	            
	            System.out.format("....................OK, found targets!\n");
	            
	            // find point that is closes to the origin
	            int closest = 0;
	            int baseIndex = 2*a[p[0]];
	            double minDist = x[baseIndex]*x[baseIndex] + x[baseIndex+1]*x[baseIndex+1];
	            for (int k=1;k<4;k++) {
	                baseIndex = 2*a[p[k]];
	                dist = x[baseIndex]*x[baseIndex] + x[baseIndex+1]*x[baseIndex+1];
	                if (dist < minDist) {
	                     closest = k;
	                     minDist = dist;
	                }
	            }
	            
	            System.out.format("Closest point to the origin was p%d\n",closest);
	            
	            _targets[4*_numTargets+0] = a[p[(closest + _PHASE +0) % 4]];    // save targets
	            _targets[4*_numTargets+1] = a[p[(closest + _PHASE +1) % 4]];
	            _targets[4*_numTargets+2] = a[p[(closest + _PHASE +2) % 4]];
	            _targets[4*_numTargets+3] = a[p[(closest + _PHASE +3) % 4]];
	            
	            _numTargets++;
	            
	            if (_numTargets == MAX_TARGETS) // reached max targets
	               break;
	            
	        } // test angle and ratio
	    }      
	}
//
//	
	public static void extractTargetPositions(
			double allPoints[], 
			int targets[], // 4 positions 
			int targetsOffset,
			double output[] // 8 positions
	) {
	     output[0] = allPoints[2*targets[targetsOffset]];
	     output[1] = allPoints[2*targets[targetsOffset]+1];
	     output[2] = allPoints[2*targets[targetsOffset+1]];
	     output[3] = allPoints[2*targets[targetsOffset+1]+1];
	     output[4] = allPoints[2*targets[targetsOffset+2]];
	     output[5] = allPoints[2*targets[targetsOffset+2]+1];
	     output[6] = allPoints[2*targets[targetsOffset+3]];
	     output[7] = allPoints[2*targets[targetsOffset+3]+1];
	}
//
//	/******************************************************************************
//	 * Miscelaneous
//	 ******************************************************************************/
//
//	int _n;        // number of points
//	double _x[];     // points
//
	/**
	 * sort n pairs of double numbers by the
	 * smaller first coordinate. if there are
	 * ties then the smaller second coordinate
	 * comes first.
	 */
	public static void sort(double x[], int n) {     
	     int j,i;
	     for (i=1;i<n;i++) {
	         j = i-1;
	         double xi = x[2*i];
	         double yi = x[2*i+1];         
	         while ((j >= 0) && ((x[2*j] > xi) || (x[2*j] == xi && x[2*j+1] > yi))) {
	            x[2*(j+1)] = x[2*j];
	            x[2*(j+1)+1] = x[2*j+1];
	            j--;
	         }
	         j++;
	         x[2*j] = xi;
	         x[2*j+1] = yi;
	     }
	}

//	/**
//	 * sort n unsigned chars.
//	 */
//	void sortIntensities(unsigned char *x, int n) {     
//	     int j,i;
//	     for (i=1;i<n;i++) {
//	         j = i-1;
//	         unsigned char xi = x[i];
//	         while ((j >= 0) && (x[j] > xi)) {
//	            x[j+1] = x[j];
//	            j--;
//	         }
//	         j++;
//	         x[j] = xi;
//	     }
//	}
//
//	/**
//	 * sort n floats.
//	 */
//	void sortIntensitiesDouble(double *x, int n) {     
//	     int j,i;
//	     for (i=1;i<n;i++) {
//	         j = i-1;
//	         double xi = x[i];
//	         while ((j >= 0) && (x[j] > xi)) {
//	            x[j+1] = x[j];
//	            j--;
//	         }
//	         j++;
//	         x[j] = xi;
//	     }
//	}
//
//
//	/**
//	 * sort n pairs of double numbers by the
//	 * smaller first coordinate. if there are
//	 * ties then the smaller second coordinate
//	 * comes first.
//	 */
//	void sortByDistance(double *x, int n, double x0, double y0) {     
//	     int j,i;
//	     for (i=1;i<n;i++) {
//	         j = i-1;
//	         double xi = x[2*i];
//	         double yi = x[2*i+1];
//	         double di = (xi - x0)*(xi - x0) + (yi - y0)*(yi - y0);
//	         while ((j >= 0) && ((x[2*j] - x0)*(x[2*j] - x0) + (x[2*j+1] - y0)*(x[2*j+1] - y0))>di) {
//	            x[2*(j+1)] = x[2*j];
//	            x[2*(j+1)+1] = x[2*j+1];
//	            j--;
//	         }
//	         j++;
//	         x[2*j] = xi;
//	         x[2*j+1] = yi;
//	     }
//	}
//
//	/**
//	 * find min distance index
//	 */
//	int findMinDistanceIndex(double *x, int n, double x0, double y0) {     
//	    if (n == 0) return -1;
//	    double xi = x[2*0];
//	    double yi = x[2*0+1];
//	    double d = (xi - x0)*(xi - x0) + (yi - y0)*(yi - y0);
//	    double result = 0;
//	    int i;
//	    for (i=1;i<n;i++) {
//	        xi = x[2*i];
//	        yi = x[2*i+1];
//	        double dCandidate = (xi - x0)*(xi - x0) + (yi - y0)*(yi - y0);
//	        if (dCandidate < d) {
//	           result = i;
//	           d=dCandidate;
//	        }
//	    }
//	    return result;
//	}
//
//
//	/******************************************************************************
//	 ** Image data structure ******************************************************
//	 ******************************************************************************/
//
//	/**
//	 * Create an Img object from a Portable GrayMap (.pgm) file
//	 */
//	Img* readPGM(char *filename) {
//	    // read pgm
//	    FILE *f = fopen(filename,"rb");
//	    char line[50];    
//	    fgets(line, 50, f); // throw away code line
//	    fgets(line, 50, f); // throw away comment line
//	    if (line[0] == '#') { // 
//	        fgets(line, 50, f); // get line with width and height
//	    }
//
//	    Img* result = (Img*) malloc(sizeof(Img));
//
//	    int w,h;
//	    sscanf(line,"%d %d",&w,&h); // get height and width
//	    result.w = w;
//	    result.h = h;
//
//	    fgets(line, 30, f); // throw away next line (maxlevel)
//
//	    result.data = (unsigned char*) malloc(w * h);
//	    int i=0;
//	    int total = w*h;
//	    while (!feof(f) && i < total) {
//	       result.data[i] = fgetc(f);
//	       i++;
//	    }
//	    fclose(f);
//
//	    return result;
//	}
//
//	/**
//	 * Write an EPS file from an Img object
//	 */
//	void writeEPS(char *filename, Img *img) {
//	    FILE *f = fopen(filename,"w");
//	    fSystem.println(f,"%%!PS-Adobe-3.0 EPSF-3.0\n");
//	    fSystem.println(f,"%%%%BoundingBox: 0 0 640 480\n");
//	    fSystem.println(f,"save\n");
//	    fSystem.println(f,"%d %d scale\n",img.w,img.h);
//	    fSystem.println(f,"/DeviceGray setcolorspace\n");
//	    fSystem.println(f,"<< /ImageType 1\n");
//	    fSystem.println(f,"   /Width %d\n",img.w);
//	    fSystem.println(f,"   /Height %d\n",img.h);
//	    fSystem.println(f,"   /ImageMatrix [ %d 0 0 %d 0 %d ]\n",img.w,-img.h,img.h);
//	    fSystem.println(f,"   /DataSource currentfile /ASCIIHexDecode filter\n");
//	    fSystem.println(f,"   /BitsPerComponent 8\n");
//	    fSystem.println(f,"   /Decode [0 1]\n");
//	    fSystem.println(f,">> image\n");
//	    int i,j;   
//	    char num[10];
//	    for (i=0;i<img.h;i++) {
//	        for (j=0;j<img.w;j++) {
//	            sSystem.println(num,"%x",img.data[i*img.w+j]);
//	            if (img.data[i*img.w+j] < 16)
//	                fSystem.println(f,"0");
//	            fSystem.println(f,num);
//	        }
//	        fSystem.println(f,"\n");
//	    }
//	    fSystem.println(f,"%\n");
//	    fSystem.println(f,"restore\n");
//	    fclose(f);
//	}
//
//	void writeEPSwithCenters(char *filename, Img *img, double *x, int n, int t) {
//	    FILE *f = fopen(filename,"w");
//	    fSystem.println(f,"%%!PS-Adobe-3.0 EPSF-3.0\n");
//	    fSystem.println(f,"%%%%BoundingBox: 0 0 640 480\n");
//	    fSystem.println(f,"save\n");
//	    fSystem.println(f,"%d %d scale\n",img.w,img.h);
//	    fSystem.println(f,"/DeviceGray setcolorspace\n");
//	    fSystem.println(f,"<< /ImageType 1\n");
//	    fSystem.println(f,"   /Width %d\n",img.w);
//	    fSystem.println(f,"   /Height %d\n",img.h);
//	    fSystem.println(f,"   /ImageMatrix [ %d 0 0 %d 0 %d ]\n",img.w,-img.h,img.h);
//	    fSystem.println(f,"   /DataSource currentfile /ASCIIHexDecode filter\n");
//	    fSystem.println(f,"   /BitsPerComponent 8\n");
//	    fSystem.println(f,"   /Decode [0 1]\n");
//	    fSystem.println(f,">> image\n");
//	    int i,j;   
//	    char num[10];
//	    for (i=0;i<img.h;i++) {
//	        for (j=0;j<img.w;j++) {
//	            unsigned char level = img.data[i*img.w+j];
//	            if (level <= t)
//	                fSystem.println(f,"00");
//	            else
//	                fSystem.println(f,"ff");
//	            
//	            //sSystem.println(num,"%x",img.data[i*img.w+j]);
//	            //if (img.data[i*img.w+j] < 16)
//	            //    fSystem.println(f,"0");
//	                
//	                
//	            // fSystem.println(f,num);
//	        }
//	        fSystem.println(f,"\n");
//	    }
//	    fSystem.println(f,"%\n");
//	    fSystem.println(f,"restore\n");
//	    fSystem.println(f,"0 %d translate\n",img.h);
//	    fSystem.println(f,"1 -1 scale\n");
//	    fSystem.println(f,"1 1 0 setrgbcolor\n");
//	    for (i =0;i<n;i++) {
//	        fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",x[2*i],x[2*i+1]);
//	    }
//	    fclose(f);
//	}

	/******************************************************************************
	 ** Regions  ******************************************************************
	 ******************************************************************************/

	public static int      _thresholds[] = {50, 60 , 70, 80, 90, 100, 110,120 }; // MAX_THRESHOLDS
	public static int      _numRegions[] = new int[MAX_THRESHOLDS];
	public static int      _numRepresentants[] = new int[MAX_THRESHOLDS];
	public static Region   _representantRegionList[] = new Region[MAX_THRESHOLDS];
	public static Region   _regions[][] = new Region[MAX_THRESHOLDS][MAX_REGIONS];
	public static Region   _R[][][] = new Region[MAX_THRESHOLDS][MAX_HEIGHT][MAX_WIDTH];

	public static int      _NUM_THRESHOLDS = 8;

	public static double    _leftMargin = 0;
	public static double    _rightMargin = 1;
	public static double    _topMargin = 0;
	public static double    _bottomMargin = 1;
//
//
//

	/**
	 * r must be a representant (i.e. r.representant = r)
	 */
	public static void removeRepresentantFromList(int k, Region r) {
	     
//	     #ifdef __DEBUG_REGIONS
//	     System.println("removeRepresentantFromList region %d threshold %d\n",r.index,_thresholds[k]);
//	     #endif
	     
	     Region rNext = r.next;
	     Region rPrevious = r.previous;     

	     if (rPrevious != null && rNext != null) { // middle element
	        rPrevious.next = rNext;
	        rNext.previous = rPrevious;
	     }

	     else if (rPrevious != null && rNext == null) { // r is the last element
	        rPrevious.next = null;
	     }

	     else if (rPrevious == null && rNext != null) { // r is the first element
	        rNext.previous = null;
	        _representantRegionList[k] = rNext;
	     }
	     
	     _numRepresentants[k]--;

	}

	/**
	 * s and r must be representants
	 * s will be a chidld of r
	 */
	public static void mergeRegions(Region r, Region s, int k) {
	     
	     // remove s from representant list
	     removeRepresentantFromList(k,s);

	     // set new representant to s     
	     s.representant = r;

//	     #ifdef __DEBUG_REGIONS
//	     System.println("update childs representants...\n");
//	     #endif

	     // update representant of the childs of s and find lastChild of s
	     Region lastChild = null;
	     if (s.numChilds > 0) {
	         Region child = s.child;
	         while (true) {
	             child.representant = r;
	             if (child.next == null)
	                break;
	             child = child.next;
	         }
	         lastChild = child;
	     }

//	     #ifdef __DEBUG_REGIONS
//	     System.println("update childs lists...\n");
//	     #endif
	          
	     // insert s at the beginning of it's child list
	     if (s.numChilds > 0) {
	         s.next = s.child;
	         s.previous = null;
	         s.next.previous = s;
	         s.child = null;
	     }
	     else { // s has no children set lastChild as s
	         s.next = null;
	         s.previous = null;
	         s.child = null;
	         lastChild = s; 
	     }
	     
	     // append r's childs to lastChild
	     if (r.numChilds > 0) {
	        r.child.previous = lastChild;
	        lastChild.next = r.child;
	     }

	     r.child = s; // set child
	     
	     //
	     r.sumx += s.sumx;
	     r.sumy += s.sumy;
	        
	     r.minx = Math.min(r.minx,s.minx);
	     r.maxx = Math.max(r.maxx,s.maxx);
	        
	     r.miny = Math.min(r.miny,s.miny);
	     r.maxy = Math.max(r.maxy,s.maxy);
	        
	     // update the number that will be ready at the end
	     r.numChilds += 1 + s.numChilds;
	     r.size += s.size;
	     
	     // erase s.numChilds
	     s.numChilds = 0;         

//	     #ifdef __DEBUG_REGIONS
//	     System.println("finished merge...\n");
//	     #endif

	}

	/**
	 * find the regions based on current thresholds
	 */
	public static void thresholdMatrix(Img img) {
	    int i,j,k;

	    for (k=0;k<_NUM_THRESHOLDS;k++) { // reset number of regions
	        _numRegions[k]=0;
	        _numRepresentants[k]=0;
	        _representantRegionList[k] = null;
	    } // reset number of regions

	    // margins
	    int xmin = (int)Math.floor(_leftMargin * img.getW());
	    int xmax = (int)Math.ceil(_rightMargin * img.getW());
	    int ymin = (int)Math.floor(_topMargin * img.getH());
	    int ymax = (int)Math.ceil(_bottomMargin * img.getH());

	    // search regions
	    for (i=ymin;i<ymax;i++) {

	        for (j=xmin;j<xmax;j++) {
	            
	            int level = img.getValue(i, j);

	            for (k=0;k<_NUM_THRESHOLDS;k++) {                
	                
//	                #ifdef __DEBUG_REGIONS
//	                System.println("%3d %3d \n",i,j);
//	                #endif
	                
	                if (level > _thresholds[k]) { // is it a white point? yes => continue
	                    _R[k][i][j] = null;
	                    continue;
	                } // is it a white pointc? yes => continue
	                
	                Region rUp = null;
	                Region rLeft = null;

	                if (j > 0) // left region
	                   rLeft = _R[k][i][j-1];
	                
	                if (i > 0) // up region
	                   rUp = _R[k][i-1][j];
	                
	                if (rUp != null && rLeft == null) { // only up region is defined
//	                    #ifdef __DEBUG_REGIONS
//	                    System.println("=up %p\n",rUp.representant);
//	                    #endif

	                    _R[k][i][j] = rUp.getRepresentant();
	                    rUp.getRepresentant().addPointToRegion(i,j);
	                }

	                else if (rUp == null && rLeft != null) { // only left region is defined
//	                    #ifdef __DEBUG_REGIONS
//	                    System.println("=left %p\n",rLeft.representant);
//	                    #endif

	                    _R[k][i][j] = rLeft.getRepresentant();
	                    rLeft.getRepresentant().addPointToRegion(i,j);
	                }
	                
	                else if (rUp == null && rLeft== null) { // no regions are defined: new region creation

	                   int newRegionIndex = _numRegions[k];

	                   _numRegions[k]++;

	                   Region r = new Region(newRegionIndex,i,j);
	                   
	                   _regions[k][newRegionIndex] = r;
	                   _R[k][i][j] = r;

	                   // link this new region to the representantRegionList
	                   if (_representantRegionList[k] == null) {

	                      _representantRegionList[k] = r;

	                   }

	                   else { // this region will be on the top of _representantRegionList[k]
	                      Region head = _representantRegionList[k];
	                      r.next = head;
	                      head.previous = r;
	                      _representantRegionList[k] = r;
	                   }
	                                      
	                   _numRepresentants[k]++;

	                } // no regions are defined: new region creation
	                
	                else if (rUp != null && rLeft != null) { // no regions are defined: new region creation
	                     Region upRepresentant = rUp.representant;
	                     Region leftRepresentant = rLeft.representant;
	                     
	                     // if these representants are the same then we link directly to it
	                     if (upRepresentant == leftRepresentant) {
	                        _R[k][i][j] = upRepresentant;
	                        upRepresentant.addPointToRegion(i,j);

//	                        #ifdef __DEBUG_REGIONS
//	                        System.println("=up&left %p\n",upRepresentant);
//	                        #endif
	                     }
	                     
	                     // else we will add this point to the least index representant
	                     else {

//	                        #ifdef __DEBUG_REGIONS
//	                        System.println("Merge %d %d\n",i,j);
//	                        System.println("rLeft %p rUp %p\n",rLeft,rUp);
//	                        System.println("rLeftRep %p rUpRep %p\n",leftRepresentant,upRepresentant);
//	                        #endif

	                        Region r = (upRepresentant.numChilds >= leftRepresentant.numChilds ? upRepresentant : leftRepresentant);
	                        Region s = (upRepresentant.numChilds < leftRepresentant.numChilds ? upRepresentant : leftRepresentant);
	                          
	                        // add this point to minRepresentantRegion
	                        _R[k][i][j] = r; 
	                        r.addPointToRegion(i,j);
	                        
	                        // merge minRepresentantRegion to minRepresentantRegion
	                        mergeRegions(r,s,k);
	                        
	                     }
	                }                
	            }
	        }
	    }

//	    for (k=0;k<_NUM_THRESHOLDS;k++) {                
//	        System.out.format("found %d regions for threshold %d\n",_numRepresentants[k],_thresholds[k]);
//	        Region r = _representantRegionList[k];
//	        while (r != null) {
//	            System.out.format("%d(%.3f,%.3f) ",r.index,r.centerx,r.centery);
//	            r = r.next;
//	        }
//	        System.out.print("\n");
//	    }

	}

	/******************************************************************************
	 ** Valid Region definition ***************************************************
	 ******************************************************************************/

	public static int _MIN_PIXEL_WIDTH    = 3;
	public static int _MAX_PIXEL_WIDTH    = 12;
	public static int _MIN_PIXEL_HEIGHT   = 3;
	public static int _MAX_PIXEL_HEIGHT   = 12;
	public static int _MIN_NUM_PIXELS     = 9;
	public static int _MAX_NUM_PIXELS     = 125;
	public static double _PIXEL_DENSITY    = 0.4;


//
//	/******************************************************************************
//	 ** Candidates ****************************************************************
//	 ******************************************************************************/

	public static final int NUM_REFERENCES = 4;

	public static int _NUM_CLOSEST = 1;

//
	public static class Dist {
	    Region region;
	    double distance;  
	    Dist next;      
		public Dist(Region r, double distance) {
			this.region = r;
			this.distance = distance;
			this.next = null;
		}
	}

	public static class Ref {
		double x;
		double y;
		
		int numPoints;
		
		Dist points; // reference to the first point

		public Ref(double x, double y) {
			this.x = x;
			this.y = y;
			this.numPoints = 0;
			this.points = null;     
		}
	
		public boolean addRegionIfCloseEnough(Region region) {
			double x = region.centerx;
			double y = region.centery;
			double sqrDist = (x - this.x) * (x - this.x) + (y - this.y) * (y - this.y);

			//
			if (_NUM_CLOSEST <= 0)
				return false;
			
			// is it the first point?
			if (this.points == null) {
				this.points = new Dist(region,sqrDist);
				this.numPoints++;
				return true;
			}

			// find insertion point
			Dist dd = this.points;  
			Dist previous = null;
			int index = 0;
			while (dd != null && sqrDist < dd.distance) {
				previous = dd;
				dd = dd.next;
				index++;
			}
			
			// is it a point further then the maximum allowed? 
			if (index >= _NUM_CLOSEST)
				return false;
			
			// insert new point
			Dist newd = new Dist(region,sqrDist);
			if (index == 0) {
				newd.next = dd;
				this.points = newd;
				this.numPoints++;
			}
			else {
				newd.next = dd;
				previous.next=newd;
				this.numPoints++;
			}
			
			// delete
			dd = newd;  
			while (dd != null && index < _NUM_CLOSEST) {
				dd = dd.next;
				index++;
			}
			if (dd != null)
				dd.next = null;
			
			//
			return true;
			
		}

	}

	public static Ref       _references[] = new Ref[NUM_REFERENCES];
	public static double    _candidates[] = new double[NUM_REFERENCES * MAX_CLOSEST * 2];
	public static Region    _candidatesRegion[] = new Region[NUM_REFERENCES * MAX_CLOSEST];
	public static int       _numCandidates;

//
	public static void filterCandidates(int w, int h, Region r) {

	    _numCandidates = 0;	   
	    _references[0] = new Ref(0,0);
	    _references[1] = new Ref(w,0);
	    _references[2] = new Ref(w,h);
	    _references[3] = new Ref(0,h);
	    
//	    #ifdef __DEBUG_CANDIDATES
//	    System.println("start filtering...\n");
//	    #endif

	    while (r != null) { // keep valid centers that are closer to the reference points

//	    	#ifdef __DEBUG_CANDIDATES
//	        System.println("region %p\n",r);
//	        #endif

	        if (r.validRegion()) {
	            r.calculateCenter();

//	            #ifdef __DEBUG_CANDIDATES
//	            System.println("=valid (%.3f,%.3f)\n",r.centerx,r.centery);
//	            #endif
	            
	            int i;
	            boolean status;
	            for (i=0;i<NUM_REFERENCES;i++) {
	                status = _references[i].addRegionIfCloseEnough(r);

//	                #ifdef __DEBUG_CANDIDATES
//	                if (status != 0) {
//	                    System.println("=close enough to reference %d (%.3f,%.3f)\n",i,_references[i].x,_references[i].y);
//	                }
//	                #endif

	            }
	        }
	        r = r.next;
	    }

//	    #ifdef __DEBUG_CANDIDATES
//	    System.println("finished filtering...\n");
//	    #endif
	        
	    int i;
	    for (i=0;i<NUM_REFERENCES;i++) { // set _candidate points vector
	        Dist d = _references[i].points;        
	        while (d != null) {
	           Region rr = d.region;  
	           if (rr.flag == 0) {
	              _candidates[2*_numCandidates]   = rr.centerx;
	              _candidates[2*_numCandidates+1] = rr.centery;
	              _candidatesRegion[_numCandidates] = rr;
	              _numCandidates++;
	              rr.flag = 1;
	           }
	           d = d.next;
	        }
	    }

//	    #ifdef __DEBUG_CANDIDATES
//	    System.println("finished candidates...\n");
//	    #endif

	}
//
//
//
//
//
//
//
//
//
//
//
//	/******************************************************************************
//	 * CellMap
//	 ******************************************************************************/
//
//	inline unsigned char intensity(Img *img,int i,int j) {
//	   return img.data[i*img.w+j];
//	}
//
	
	public static int MAX_SAMPLES = 9;
	public static double _samples[] = { 
	   // P0     P1     P2     P3 
	      0.250, 0.250, 0.250, 0.250, // C
	      0.625, 0.125, 0.125, 0.125, // CP0
	      0.125, 0.625, 0.125, 0.125, // CP1
	      0.125, 0.125, 0.625, 0.125, // CP2
	      0.125, 0.125, 0.125, 0.625, // CP3
	      0.375, 0.375, 0.125, 0.125, // CP0P1
	      0.125, 0.125, 0.375, 0.375, // CP2P3
	      0.375, 0.125, 0.125, 0.375, // CP0P3
	      0.125, 0.375, 0.375, 0.125, // CP1P2
	      0.2, 0.2, 0.3, 0.3,    // CP0P3
	      0.2, 0.3, 0.2, 0.3,    // CP0P3
	      0.2, 0.3, 0.3, 0.2,    // CP0P3
	      0.3, 0.2, 0.2, 0.3,    // CP0P3
	      0.3, 0.3, 0.2, 0.2,    // CP0P3
	      0.7, 0.1, 0.1, 0.1,    // CP0P3
	      0.1, 0.7, 0.1, 0.1,    // CP0P3
	      0.1, 0.1, 0.7, 0.1,    // CP0P3
	      0.1, 0.1, 0.1, 0.7,    // CP0P3
	      0.7, 0.1, 0.1, 0.1,    // CP0P3
	      0.1, 0.7, 0.1, 0.1,    // CP0P3
	      0.1, 0.1, 0.7, 0.1,    // CP0P3
	      0.1, 0.2, 0.3, 0.4,    // CP0P3
	      0.1, 0.2, 0.4, 0.3,    // CP0P3
	      0.1, 0.3, 0.2, 0.4,    // CP0P3
	      0.1, 0.3, 0.4, 0.2     // CP0P3
	};


	/**
	 * This finds the mapped position of a theorical point on the image
	 * based on the targets given by P, using a projective (homography)
	 * transformation from the 4 corner targets.
	 */
	public static void findMappingOfPoint(CellMap M, double x, double y, double P[], double mapping[]) {

	   double x0 = P[0], y0 = P[1]; // TL
	   double x1 = P[2], y1 = P[3]; // TR
	   double x2 = P[4], y2 = P[5]; // BR
	   double x3 = P[6], y3 = P[7]; // BL

	   double dx1 = x1 - x2, dy1 = y1 - y2;
	   double dx2 = x3 - x2, dy2 = y3 - y2;
	   double dx3 = x0 - x1 + x2 - x3, dy3 = y0 - y1 + y2 - y3;

	   double det = dx1 * dy2 - dy1 * dx2;

	   double g = (dx3 * dy2 - dy3 * dx2) / det;
	   double h = (dx1 * dy3 - dy1 * dx3) / det;

	   double a = x1 - x0 + g * x1;
	   double b = x3 - x0 + h * x3;
	   double c = x0;
	   double d = y1 - y0 + g * y1;
	   double e = y3 - y0 + h * y3;
	   double f = y0;

	   double u = x / M.width;
	   double v = y / M.height;

	   double w = g * u + h * v + 1.0;
	   mapping[0] = (a * u + b * v + c) / w;
	   mapping[1] = (d * u + e * v + f) / w;

	}

	private static boolean triangleConvexCombination(Triangle t, double x, double y, double mapping[], int mappingOffset) {
	     
		 double a1 = t.getP1().x;
	     double a2 = t.getP1().y;
	     
	     double b1 = t.getP2().x;
	     double b2 = t.getP2().y;
	     
	     double c1 = t.getP3().x;
	     double c2 = t.getP3().y;
	     
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
	        
	     mapping[mappingOffset] = alpha * t.getP1().xx + beta * t.getP2().xx + gamma * t.getP3().xx;
	     mapping[mappingOffset+1] = alpha * t.getP1().yy + beta * t.getP2().yy + gamma * t.getP3().yy;

//	     #ifdef __DEBUG_TRIANGLE_CONVEX_COMBINATION
//	     System.println("convex combination was %.3f %.3f %.3f for point %.3f %.3f on triangle %d(%.3f %.3f) %d(%.3f %.3f) %d(%.3f %.3f)\n",
//	     alpha,beta,gamma,x,y,t.p1.id,t.p1.x,t.p1.y,t.p2.id,t.p2.x,t.p2.y,t.p3.id,t.p3.x,t.p3.y);
//	     #endif

	     return true;
	}

	/**
	 * @param M
	 * @param x
	 * @param y
	 * @param mapping two position double
	 */
	public static void findMappingOfPointByTriangles(CellMap M, double x, double y, double mapping[], int mappingOffset) {
	     int i;
	     for (i=0;i<M.getNumberOfTriangles();i++) {
	         Triangle t = M.getTriangle(i);
	         if (triangleConvexCombination(t,x,y,mapping,mappingOffset)) {
//	            #ifdef __DEBUG_TRIANGLE_CONVEX_COMBINATION
//	            System.println("point %.3f %.3f is inside triangle %d %d %d and will be mapped to %.3f %.3f\n",
//	            x,y,t.p1.id,t.p2.id,t.p3.id,mapping[0],mapping[1]);
//	            #endif
	            return;
	         }
	     }
	     mapping[0] = 0.0;
	     mapping[1] = 0.0;
	     System.out.println("FATAL ERROR: did not find a triangle, mapping will be 0 0\n");
	}

	/**
	 * Map a theoretical point (x,y) to practical pixel coordinates using
	 * the piecewise projective (homography) mapping over the quad grid.
	 * Falls back to triangle-based mapping if no quad contains the point.
	 */
	public static void findMappingOfPointByQuads(CellMap M, double x, double y, double mapping[], int mappingOffset) {
	    for (int i = 0; i < M.numQuads; i++) {
	        Quad q = M.quads[i];
	        if (q.contains(x, y)) {
	            q.map(x, y, mapping, mappingOffset);
	            return;
	        }
	    }
	    // Fallback to triangles if point is outside all quads
	    findMappingOfPointByTriangles(M, x, y, mapping, mappingOffset);
	}

	public static void mapCorners(CellMap M, double x, double y, double w, double h, double out[]) {
	    findMappingOfPointByQuads(M, x - w/2.0, y - h/2.0, out, 0);
	    findMappingOfPointByQuads(M, x + w/2.0, y - h/2.0, out, 2);
	    findMappingOfPointByQuads(M, x + w/2.0, y + h/2.0, out, 4);
	    findMappingOfPointByQuads(M, x - w/2.0, y + h/2.0, out, 6);
	}

	public static double sampleIntensities(Cell c, Img img, double corners[], int sampleSize) {
	    double result = 0.0;
	    int realSampleSize = Math.min(sampleSize,MAX_SAMPLES);
	    int j;
	    for (j=0;j<realSampleSize;j++) {
	        double x = 0.0; double y = 0.0;
	        x += _samples[4*j] * corners[0];
	        y += _samples[4*j] * corners[1];
	        x += _samples[4*j+1] * corners[2];
	        y += _samples[4*j+1] * corners[3];
	        x += _samples[4*j+2] * corners[4];
	        y += _samples[4*j+2] * corners[5];
	        x += _samples[4*j+3] * corners[6];
	        y += _samples[4*j+3] * corners[7];
	        
	        int pixelx = (int)x;
	        int pixely = (int)y;
	    
	        double dx = x - pixelx;
	        double dy = y - pixely;
	        
//	        #ifdef __DEBUG_CELLSAMPLE
//	        char fw = (pixelx < img.w -1 ? 1 : 0);
//	        char fh = (pixely < img.h -1 ? 1 : 0);
//	        System.println("- cell %d (dx = %.2f, dy =%.2f) pixel (%d %d = %d) (%d %d = %d) (%d %d = %d) (%d %d = %d)\n",c.id,dx,dy,
//	        pixelx,pixely,intensity(img,pixely,pixelx),
//	        pixelx+fw,pixely,intensity(img,pixely,pixelx+fw),
//	        pixelx+fw,pixely+fh,intensity(img,pixely+fh,pixelx+fw),
//	        pixelx,pixely+fh,intensity(img,pixely+fh,pixelx));
//	        #endif
	        
	        // c.intensity += intensity(img,pixely,pixelx);
	    
	        result += (1 - dx - dy + dx*dy) * img.getValue(pixely,pixelx);
	    
	        if (pixelx < img.w-1) {
	             result += (dx - dx*dy) * img.getValue(pixely,pixelx+1);
	        }
	        else {
	             result += (dx - dx*dy) * img.getValue(pixely,pixelx);
	        }
	    
	        if (pixelx < img.w-1 && pixely < img.h-1) {
	             result += (dx*dy) * img.getValue(pixely+1,pixelx+1);
	        }
	        else {
	             result += (dx*dy) * img.getValue(pixely,pixelx);
	        }
	    
	        if (pixely < img.h-1) {
	             result += (dy-dx*dy) * img.getValue(pixely+1,pixelx);
	        }
	        else {
	             result += (dy-dx*dy) * img.getValue(pixely,pixelx);
	        }
	    
	    }
	    result /= realSampleSize;
	    return result;
	}

	public static void sampleCellIntensities(Img img, CellMap M) {
	    double cellCorners[] = new double[8]; 
	    int whites[] = new int[4]; 
	    int i;

//	    #ifdef __DEBUG_CELLSAMPLE
//	    System.println("Sample cell intensities starting...\n");
//	    #endif

	    for (i=0;i<M.numCells;i++) {
	        Cell c = M.cells[i];        

	        // internal region of the cell
	        mapCorners(M,c.x, c.y, c.w0, c.h0, cellCorners);
	        c.intensity = sampleIntensities(c,img,cellCorners,9);
	        
	        // white region of the cell
	        int whiteSamples = 0;
	        c.white = 0.0;
	        if ((c.whiteSampleSet & 0x01) != 0) {
	           mapCorners(M,
	           c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	           c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	           (c.w2 - c.w1)/2.0, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x02) != 0) {
	           mapCorners(M,
	           c.x, 
	           c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	           c.w1, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x04) != 0) {
	           mapCorners(M,
	           c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	           c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	           (c.w2 - c.w1)/2.0, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x08) != 0) {
	           mapCorners(M,
	           c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	           c.y, 
	           (c.w2 - c.w1)/2.0, 
	           c.h1, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x10) != 0) {
	           mapCorners(M,
	           c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	           c.y, 
	           (c.w2 - c.w1)/2.0, 
	           c.h1, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x20) != 0) {
	           mapCorners(M,
	           c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	           c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	           (c.w2 - c.w1)/2.0, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x40) != 0) {
	           mapCorners(M,
	           c.x, 
	           c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	           c.w1, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        if ((c.whiteSampleSet & 0x80) != 0) {
	           mapCorners(M,
	           c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	           c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	           (c.w2 - c.w1)/2.0, 
	           (c.h2 - c.h1)/2.0, 
	           cellCorners);
	           c.white += sampleIntensities(c,img,cellCorners,1);
	           whiteSamples++;                  
	        }
	        c.white /= whiteSamples;

	        /*
	        int d = 3;        
	        whites[0] = intensity(img,(int)(cellCorners[0]-d),(int)(cellCorners[1]-d));
	        whites[1] = intensity(img,(int)(cellCorners[2]+d),(int)(cellCorners[3]-d));
	        whites[2] = intensity(img,(int)(cellCorners[4]+d),(int)(cellCorners[5]+d));
	        whites[3] = intensity(img,(int)(cellCorners[6]-d),(int)(cellCorners[7]+d));
	        sortIntensities(whites,4); */
	                 
	        // c.white = 255; // whites[3]; // + whites[2]) / 2.0;


//	        #ifdef __DEBUG_CELLSAMPLE
//	        System.println("cell %d has intensity %.2f and white %.2f\n",c.id,c.intensity,c.white);
//	        #endif
	    }
	}

	public static void sample(Img img, CellMap M, Cell c) {
	    double cellCorners[] = new double[8]; 
	    int whites[] = new int[4]; 
	    int i;

//	    #ifdef __DEBUG_CELLSAMPLE
//	    System.println("Sample cell intensities starting...\n");
//	    #endif

	    // internal region of the cell
	    mapCorners(M,c.x, c.y, c.w0, c.h0, cellCorners);
	    c.intensity = sampleIntensities(c,img,cellCorners,9);
	    
	    // white region of the cell
	    int whiteSamples = 0;
	    c.white = 0.0;
	    if ((c.whiteSampleSet & 0x01) != 0) {
	       mapCorners(M,
	       c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	       c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	       (c.w2 - c.w1)/2.0, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x02) != 0) {
	       mapCorners(M,
	       c.x, 
	       c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	       c.w1, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x04) != 0) {
	       mapCorners(M,
	       c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	       c.y - c.h2/2.0 + (c.h2 - c.h1)/4.0, 
	       (c.w2 - c.w1)/2.0, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x08) != 0) {
	       mapCorners(M,
	       c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	       c.y, 
	       (c.w2 - c.w1)/2.0, 
	       c.h1, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x10) != 0) {
	       mapCorners(M,
	       c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	       c.y, 
	       (c.w2 - c.w1)/2.0, 
	       c.h1, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x20) != 0) {
	       mapCorners(M,
	       c.x - c.w2/2.0 + (c.w2 - c.w1)/4.0, 
	       c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	       (c.w2 - c.w1)/2.0, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x40) != 0) {
	       mapCorners(M,
	       c.x, 
	       c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	       c.w1, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    if ((c.whiteSampleSet & 0x80) != 0) {
	       mapCorners(M,
	       c.x + c.w2/2.0 - (c.w2 - c.w1)/4.0, 
	       c.y + c.h2/2.0 - (c.h2 - c.h1)/4.0, 
	       (c.w2 - c.w1)/2.0, 
	       (c.h2 - c.h1)/2.0, 
	       cellCorners);
	       c.white += sampleIntensities(c,img,cellCorners,1);
	       whiteSamples++;                  
	    }
	    c.white /= whiteSamples;

	    /*
	    int d = 3;        
	    whites[0] = intensity(img,(int)(cellCorners[0]-d),(int)(cellCorners[1]-d));
	    whites[1] = intensity(img,(int)(cellCorners[2]+d),(int)(cellCorners[3]-d));
	    whites[2] = intensity(img,(int)(cellCorners[4]+d),(int)(cellCorners[5]+d));
	    whites[3] = intensity(img,(int)(cellCorners[6]-d),(int)(cellCorners[7]+d));
	    sortIntensities(whites,4); */
	             
	    // c.white = 255; // whites[3]; // + whites[2]) / 2.0;

//	    #ifdef __DEBUG_CELLSAMPLE
//	    System.println("cell %d has intensity %.2f and white %.2f\n",c.id,c.intensity,c.white);
//	    #endif
	}


	public static Cell findCell(CellMap m, int id) {
	     int i;
	     for (i=0;i<m.numCells;i++)
	         if (m.cells[i].id == id)
	            return m.cells[i];
	     return null;
	}

	/******************************************************************************
	 * After finding targets find all control points image
	 ******************************************************************************/
	 
	public static double _RADIUS_CONTROL_POINT_TOLERANCE=10;    // image control point will be the 
	                                           // closest valid region center at a distance 
	                                           // at most of _RADIUS_CONTROL_POINT_TOLERANCE

	 
	public static boolean findControlPoints(double targets[], CellMap map, Region regions) {
	          
	     // the first 4 targets will be assigned
	     // to the first 4 control points. The
	     // point sequence is top left, top right,
	     // bottom right, bottom left.
	     map.cps[0].xx = targets[0];
	     map.cps[0].yy = targets[1];
	     map.cps[1].xx = targets[2];
	     map.cps[1].yy = targets[3];     
	     map.cps[2].xx = targets[4];
	     map.cps[2].yy = targets[5];     
	     map.cps[3].xx = targets[6];
	     map.cps[3].yy = targets[7];
	     
	     boolean foundAllControlPoints = true;
	     
	     int i;
	     for (i=4;i<map.numControlPoints;i++) {
	         ControlPoint p = map.cps[i];

	         double x=p.x, y=p.y;

	         double mapping[] = {0,0};

	         findMappingOfPoint(map,x,y,targets,mapping);

	         double xx = mapping[0], yy = mapping[1];
	         
	         // find closest region to the mapped point
	         double minDist = 1.0e10;
	         Region minRegion = null;
	         Region r = regions;
	         while (r != null) {
	            if (r.validRegion()) {
	               double dist = Math.sqrt((r.centerx - xx) * (r.centerx - xx) + (r.centery - yy) * (r.centery - yy));
	               if (dist < minDist) {
	                    minDist = dist;
	                    minRegion = r;
	               }
	            }
	            r = r.next;
	         }
	         
	         if (minRegion == null || minDist > _RADIUS_CONTROL_POINT_TOLERANCE) {
	             foundAllControlPoints = false;
	             p. xx = xx;
	             p. yy = yy;
//	             #ifdef __DEBUG_CONTROL_POINTS
//	             System.println("unsafe control point. no region found for control point %d (%.3f %.3f)\n",p.id,p.x,p.y);
//	             #endif
	         }
	         else {
	             p.xx = minRegion.centerx;
	             p.yy = minRegion.centery;
	             p.region = minRegion;
//	             #ifdef __DEBUG_CONTROL_POINTS
//	             System.println("safe control point. control point %d (%.3f %.3f) theorical %.3f %.3f corrected %.3f %.3f distance %.3f\n",
//	             p.id,p.x,p.y,xx,yy,p.xx,p.yy,minDist);
//	             #endif
	         }
	     }
	     return foundAllControlPoints;     
	}

//	/******************************************************************************
//	 * Output routines
//	 ******************************************************************************/
//	 
//	void writeEPSwithCandidatesAndTargetsAndMapping(char *filename, Img *img, int t, CellMap *cellMap, Region *allRegions) {
//	    FILE *f = fopen(filename,"w");
//	    fSystem.println(f,"%%!PS-Adobe-3.0 EPSF-3.0\n");
//	    fSystem.println(f,"%%%%BoundingBox: 0 0 1280 960\n");
//	    fSystem.println(f,"save\n");
//	    fSystem.println(f,"%d %d scale\n",img.w,img.h);
//	    fSystem.println(f,"/DeviceGray setcolorspace\n");
//	    fSystem.println(f,"<< /ImageType 1\n");
//	    fSystem.println(f,"   /Width %d\n",img.w);
//	    fSystem.println(f,"   /Height %d\n",img.h);
//	    fSystem.println(f,"   /ImageMatrix [ %d 0 0 %d 0 %d ]\n",img.w,-img.h,img.h);
//	    fSystem.println(f,"   /DataSource currentfile /ASCIIHexDecode filter\n");
//	    fSystem.println(f,"   /BitsPerComponent 8\n");
//	    fSystem.println(f,"   /Decode [0 1]\n");
//	    fSystem.println(f,">> image\n");
//	    int i,j;   
//	    char num[10];
//	    for (i=0;i<img.h;i++) {
//	        for (j=0;j<img.w;j++) {
//	            unsigned char level = img.data[i*img.w+j];
//	            if (level <= t)
//	                fSystem.println(f,"00");
//	            else
//	                fSystem.println(f,"ff");
//	            
//	            // sSystem.println(num,"%x",level);
//	            // if (img.data[i*img.w+j] < 16)
//	            //    fSystem.println(f,"0");
//	            // fSystem.println(f,num);
//	        }
//	        fSystem.println(f,"\n");
//	    }
//	    fSystem.println(f,"%\n");
//	    fSystem.println(f,"restore\n");
//
//
//	    //---------------------------------------------
//	    // bloco de adereços...
//
//	    fSystem.println(f,"save\n",img.h);
//	    fSystem.println(f,"0 %d translate\n",img.h);
//	    fSystem.println(f,"1 -1 scale\n");
//	    
//	    double P[8];
//	    double Q[8];
//
//	    fSystem.println(f,"0 0 1 setrgbcolor\n");
//	    for (i =0;i<_numTargets;i++) {
//	        fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	        _candidates[2*_targets[4*i]],
//	        _candidates[2*_targets[4*i]+1],
//	        _candidates[2*_targets[4*i+1]],
//	        _candidates[2*_targets[4*i+1]+1],
//	        _candidates[2*_targets[4*i+2]],
//	        _candidates[2*_targets[4*i+2]+1],
//	        _candidates[2*_targets[4*i+3]],
//	        _candidates[2*_targets[4*i+3]+1]
//	        );
//
//	        //
//	        if (i == 0) {
//	            P[0] = _candidates[2*_targets[4*i+0]]; P[1] = _candidates[2*_targets[4*i+0]+1];
//	            P[2] = _candidates[2*_targets[4*i+1]]; P[3] = _candidates[2*_targets[4*i+1]+1];
//	            P[4] = _candidates[2*_targets[4*i+2]]; P[5] = _candidates[2*_targets[4*i+2]+1];
//	            P[6] = _candidates[2*_targets[4*i+3]]; P[7] = _candidates[2*_targets[4*i+3]+1];
//	            int index = findMinDistanceIndex(P,4,0,0);
//	            int ii;
//	            for (ii=0;ii<4;ii++) {
//	                Q[2*ii] = P[2*((ii+index)%4)];
//	                Q[2*ii+1] = P[2*((ii+index)%4)+1];                
//	            }
//	        }
//	    }
//
//	    if (_numTargets > 0) {
//
//	        fSystem.println(f,"0.1 setlinewidth\n");
//	        fSystem.println(f,"0 1 0 setrgbcolor\n");
//	        for (i=0;i<cellMap.numCells;i++) {
//	            double x[8];
//	            Cell c = cellMap.cells[i];
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w0/2.0, c.y - c.h0/2.0,x+0);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w0/2.0, c.y - c.h0/2.0,x+2);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w0/2.0, c.y + c.h0/2.0,x+4);
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w0/2.0, c.y + c.h0/2.0,x+6);
//	            fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	            x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7]);
//	            fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",0.25*(x[0] +x[2] + x[4] + x[6]),0.25*(x[1] +x[3] + x[5] + x[7]));
//	        }
//
//	    }
//
//	    for (i =0;i<_numCandidates;i++) {
//
//	        fSystem.println(f,"1 1 0 setrgbcolor\n");
//	        fSystem.println(f,"newpath %3.4f %3.4f 0.8 0 360 arc closepath fill\n",_candidates[2*i],_candidates[2*i+1]);
//	        
//	        fSystem.println(f,"0 0.3 1 setrgbcolor\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"12 scalefont setfont\n");
//	        fSystem.println(f,"%3.4f %3.4f moveto\n",_candidates[2*i],_candidates[2*i+1]);
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"1 -1 scale\n");
//	        fSystem.println(f,"(%d) show\n",i);
//	        fSystem.println(f,"restore\n");
//
//	    }
//	    fSystem.println(f,"restore\n");
//
//	    fSystem.println(f,"0 0.8 1 setrgbcolor\n");
//	    fSystem.println(f,"/Helvetica findfont\n");
//	    fSystem.println(f,"28 scalefont setfont\n");
//	    fSystem.println(f,"20 20 moveto\n");
//	    fSystem.println(f,"(threshold: %d) show\n",t);
//
//	    // bloco de adereços...
//	    //---------------------------------------------
//
//	    //---------------------------------------------
//	    // second image...
//	    fSystem.println(f,"save\n");
//	    fSystem.println(f,"%d %d translate\n",0,480);
//	    fSystem.println(f,"%d %d scale\n",img.w,img.h);
//	    fSystem.println(f,"/DeviceGray setcolorspace\n");
//	    fSystem.println(f,"<< /ImageType 1\n");
//	    fSystem.println(f,"   /Width %d\n",img.w);
//	    fSystem.println(f,"   /Height %d\n",img.h);
//	    fSystem.println(f,"   /ImageMatrix [ %d 0 0 %d 0 %d ]\n",img.w,-img.h,img.h);
//	    fSystem.println(f,"   /DataSource currentfile /ASCIIHexDecode filter\n");
//	    fSystem.println(f,"   /BitsPerComponent 8\n");
//	    fSystem.println(f,"   /Decode [0 1]\n");
//	    fSystem.println(f,">> image\n");
//	    i,j;   
//	    for (i=0;i<img.h;i++) {
//	        for (j=0;j<img.w;j++) {
//	            unsigned char level = img.data[i*img.w+j];
//	            // if (level <= t)
//	            //    fSystem.println(f,"00");
//	            // else
//	            //    fSystem.println(f,"ff");
//	            
//	            sSystem.println(num,"%x",level);
//	            if (img.data[i*img.w+j] < 16)
//	                fSystem.println(f,"0");
//	            fSystem.println(f,num);
//	        }
//	        fSystem.println(f,"\n");
//	    }
//	    fSystem.println(f,"%\n");
//	    fSystem.println(f,"restore\n");
//	    // second image...
//	    //---------------------------------------------
//
//
//	    //---------------------------------------------
//	    // bloco de adereços...
//
//	    fSystem.println(f,"save\n",img.h);
//	    fSystem.println(f,"0 %d translate\n",2*img.h);
//	    fSystem.println(f,"1 -1 scale\n");
//	    
//	    fSystem.println(f,"0 0 1 setrgbcolor\n");
//	    for (i =0;i<_numTargets;i++) {
//	        fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	        _candidates[2*_targets[4*i]],
//	        _candidates[2*_targets[4*i]+1],
//	        _candidates[2*_targets[4*i+1]],
//	        _candidates[2*_targets[4*i+1]+1],
//	        _candidates[2*_targets[4*i+2]],
//	        _candidates[2*_targets[4*i+2]+1],
//	        _candidates[2*_targets[4*i+3]],
//	        _candidates[2*_targets[4*i+3]+1]
//	        );
//
//	        //
//	        if (i == 0) {
//	            P[0] = _candidates[2*_targets[4*i+0]]; P[1] = _candidates[2*_targets[4*i+0]+1];
//	            P[2] = _candidates[2*_targets[4*i+1]]; P[3] = _candidates[2*_targets[4*i+1]+1];
//	            P[4] = _candidates[2*_targets[4*i+2]]; P[5] = _candidates[2*_targets[4*i+2]+1];
//	            P[6] = _candidates[2*_targets[4*i+3]]; P[7] = _candidates[2*_targets[4*i+3]+1];
//	            int index = findMinDistanceIndex(P,4,0,0);
//	            int ii;
//	            for (ii=0;ii<4;ii++) {
//	                Q[2*ii] = P[2*((ii+index)%4)];
//	                Q[2*ii+1] = P[2*((ii+index)%4)+1];                
//	            }
//	        }
//	    }
//
//	    if (_numTargets > 0) {
//
//	        fSystem.println(f,"0.1 setlinewidth\n");
//	        for (i=0;i<cellMap.numCells;i++) {
//	            Cell c = cellMap.cells[i];
//	            double x[8];
//	            
//	            // white region internal border
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w1/2.0, c.y - c.h1/2.0,x+0);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w1/2.0, c.y - c.h1/2.0,x+2);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w1/2.0, c.y + c.h1/2.0,x+4);
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w1/2.0, c.y + c.h1/2.0,x+6);
//
//	            fSystem.println(f,"0 1 1 setrgbcolor\n");
//	            fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	            x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7]);
//
//	            // white region external border
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w2/2.0, c.y - c.h2/2.0,x+0);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w2/2.0, c.y - c.h2/2.0,x+2);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w2/2.0, c.y + c.h2/2.0,x+4);
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w2/2.0, c.y + c.h2/2.0,x+6);
//
//	            fSystem.println(f,"0 1 1 setrgbcolor\n");
//	            fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	            x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7]);
//
//	            // internal region
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w0/2.0, c.y - c.h0/2.0,x+0);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w0/2.0, c.y - c.h0/2.0,x+2);
//	            findMappingOfPointByTriangles(cellMap,c.x + c.w0/2.0, c.y + c.h0/2.0,x+4);
//	            findMappingOfPointByTriangles(cellMap,c.x - c.w0/2.0, c.y + c.h0/2.0,x+6);
//
//	            fSystem.println(f,"0 1 0 setrgbcolor\n");
//	            fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	            x[0],x[1],x[2],x[3],x[4],x[5],x[6],x[7]);
//	            fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",0.25*(x[0] +x[2] + x[4] + x[6]),0.25*(x[1] +x[3] + x[5] + x[7]));
//
//	            // write down the cell intensity
//	            fSystem.println(f,"0.8 0.3 0.8 setrgbcolor\n");
//	            fSystem.println(f,"/Helvetica findfont\n");
//	            fSystem.println(f,"3 scalefont setfont\n");
//	            fSystem.println(f,"%3.4f %3.4f moveto\n",0.25*(x[0] +x[2] + x[4] + x[6])-4,0.25*(x[1] +x[3] + x[5] + x[7]));
//	            fSystem.println(f,"save\n");
//	            fSystem.println(f,"1 -1 scale\n");
//	            fSystem.println(f,"(%4.0f) show\n",c.intensity);
//	            fSystem.println(f,"restore\n");
//
//	        }
//	        
//	        
//	        // triangulos
//	        for (i=0;i<cellMap.numTriangles;i++) {
//	            Triangle *t = &cellMap.triangles[i];
//	            fSystem.println(f,"1 0 1 setrgbcolor\n");
//	            fSystem.println(f,"newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke\n",
//	            t.p1.xx,t.p1.yy,t.p2.xx,t.p2.yy,t.p3.xx,t.p3.yy);
//	            
//	        }
//	    }
//
//	    Region *r = allRegions;
//	    while (r != NULL) {
//	        calculateCenter(r);
//	        if (validRegion(r)) {
//	            fSystem.println(f,"1 1 0 setrgbcolor\n");
//	        }
//	        else {
//	            fSystem.println(f,"1 0 0 setrgbcolor\n");
//	        }
//	        fSystem.println(f,"newpath %3.4f %3.4f 0.8 0 360 arc closepath fill\n",r.centerx,r.centery);
//	        r = r.next;
//	    }
//
//	    for (i =0;i<_numCandidates;i++) {
//
//	        fSystem.println(f,"1 1 0 setrgbcolor\n");
//	        fSystem.println(f,"newpath %3.4f %3.4f 0.8 0 360 arc closepath fill\n",_candidates[2*i],_candidates[2*i+1]);
//	        
//	        fSystem.println(f,"0 0.3 1 setrgbcolor\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"12 scalefont setfont\n");
//	        fSystem.println(f,"%3.4f %3.4f moveto\n",_candidates[2*i],_candidates[2*i+1]);
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"1 -1 scale\n");
//	        fSystem.println(f,"(%d) show\n",i);
//	        fSystem.println(f,"restore\n");
//
//	    }
//
//	    fSystem.println(f,"restore\n");
//
//	    // bloco de adereços...
//	    //---------------------------------------------
//
//
//	    //---------------------------------------------
//	    // scatter plot of intensities
//
//	    if (_numTargets > 0 && cellMap.numCells > 0) {
//	                    
//	        int n = cellMap.numCells;
//	        double *intensities = (double *) malloc(n * sizeof(double));
//	                    
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"640 480 translate\n");
//	        fSystem.println(f,"32 24 translate\n");
//	        fSystem.println(f,"%.4f %.4f scale\n",(640.0-64.0)/n,(480.0-48.0)/256.0);
//
//	        fSystem.println(f,"0.05 setlinewidth\n");
//	        fSystem.println(f,"newpath 0 0 moveto 0 256 lineto stroke\n");
//	        fSystem.println(f,"newpath 0 0 moveto %d 0 lineto stroke\n",n);
//
//	        // intensity
//	        for (i=0;i<n;i++) {
//	            Cell c = cellMap.cells[i];
//	            intensities[i] = c.intensity;
//	        }
//	        sortIntensitiesDouble(intensities,n);
//	        
//	        // intensities ------
//	        double maxDistanceIntensity = -1;
//	        double maxAIntensity = 0, maxBIntensity = 0;
//	        for (i=0;i<n-1;i++) {
//	            double d = intensities[i+1] - intensities[i];
//	            if (d > maxDistanceIntensity) {
//	               maxDistanceIntensity = d;
//	               maxAIntensity =intensities[i];
//	               maxBIntensity =intensities[i+1];
//	            }
//	        }
//	        // ------
//	        
//	        fSystem.println(f,"0.2 0.4 0.3 setrgbcolor\n");
//	        for (i=0;i<n;i++) {
//	            fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",(double)i,intensities[i]);
//	        }
//
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"initmatrix\n");
//	        fSystem.println(f,"700 490 translate\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"8 scalefont setfont\n");
//	        fSystem.println(f,"0 0 moveto\n");
//	        fSystem.println(f,"(Intensity %.2f, %.2f-%.2f) show\n",maxDistanceIntensity,maxAIntensity,maxBIntensity);
//	        fSystem.println(f,"restore\n");
//
//
//	        fSystem.println(f,"\n");
//
//	        // white
//	        for (i=0;i<n;i++) {
//	            Cell c = cellMap.cells[i];
//	            intensities[i] = c.white;
//	        }
//	        sortIntensitiesDouble(intensities,n);
//
//	        fSystem.println(f,"0.5 0.3 0.2 setrgbcolor\n");
//	        for (i=0;i<n;i++) {
//	            fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",(double)i,intensities[i]);
//	        }
//
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"initmatrix\n");
//	        fSystem.println(f,"900 490 translate\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"8 scalefont setfont\n");
//	        fSystem.println(f,"0 0 moveto\n");
//	        fSystem.println(f,"(White) show\n");
//	        fSystem.println(f,"restore\n");
//
//	        fSystem.println(f,"\n");
//
//
//	        // white - intensity
//	        for (i=0;i<n;i++) {
//	            Cell c = cellMap.cells[i];
//	            intensities[i] = c.white - c.intensity;
//	        }
//	        sortIntensitiesDouble(intensities,n);
//
//	        // intensities ------
//	        double maxDistanceContrast = -1;
//	        double maxAContrast = 0, maxBContrast = 0;
//	        for (i=0;i<n-1;i++) {
//	            double d = intensities[i+1] - intensities[i];
//	            if (d > maxDistanceContrast) {
//	               maxDistanceContrast = d;
//	               maxAContrast =intensities[i];
//	               maxBContrast =intensities[i+1];
//	            }
//	        }
//	        // ------public static 
//
//	        fSystem.println(f,"0.1 0.3 0.6 setrgbcolor\n");
//	        for (i=0;i<n;i++) {
//	            fSystem.println(f,"newpath %3.4f %3.4f 0.5 0 360 arc closepath fill\n",(double)i,intensities[i]);
//	        }
//
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"initmatrix\n");
//	        fSystem.println(f,"980 490 translate\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"8 scalefont setfont\n");
//	        fSystem.println(f,"0 0 moveto\n");
//	        fSystem.println(f,"(White - Intensity %.2f, %.2f-%.2f) show\n",maxDistanceContrast,maxAContrast,maxBContrast);
//	        fSystem.println(f,"restore\n");
//
//	        fSystem.println(f,"\n");
//
//
//	        fSystem.println(f,"0 0 0 setrgbcolor\n");
//	        fSystem.println(f,"save\n");
//	        fSystem.println(f,"initmatrix\n");
//	        fSystem.println(f,"1220 490 translate\n");
//	        fSystem.println(f,"/Helvetica findfont\n");
//	        fSystem.println(f,"8 scalefont setfont\n");
//	        fSystem.println(f,"0 0 moveto\n");
//	        fSystem.println(f,"(MaxSamples %d) show\n",MAX_SAMPLES);
//	        fSystem.println(f,"restore\n");
//
//	    }
//
//	    // scatter plot of intensities
//	    //---------------------------------------------
//
//	    fclose(f);
//
//	}
//
//	/******************************************************************************
//	 * interface
//	 ******************************************************************************/
//
//	int ____COUNT = 0;
//
//
//	/******************************************************************************
//	 * Main
//	 ******************************************************************************/
//
//	int main(int argc, char *argv[]) {    
//	    
//	    if (argc < 3) {
//	        System.println("Usage: mfi <pgm_image> <cell_map> (<phase>)?\n");
//	        return 1;
//	    }
//	    
//	    if (argc >= 4) { // user gave a phase number (alignment)
//	        int p;
//	        sscanf(argv[3],"%d",&p);
//	        _PHASE = p % 4;
//	    }
//	    
//	    long t = 0;
//	    long tSave = 0;
//	    long tLoad = 0;
//
//	    clock_t t0 = clock();
//	    Img* img = readPGM(argv[1]); // read image
//	    clock_t tf = clock();
//	    tLoad = tf - t0;
//	    
//	    //int ii,jj;
//	    //for (ii=0;ii<img.h;ii++)
//	    //for (jj=0;jj<img.w;jj++)
//	    //System.println("%d %d = %d\n",ii,jj,intensity(img,ii,jj));
//
//	    System.println("Image loaded...\n");
//	        
//	    t0 = clock();
//	    thresholdMatrix(img); // calculate thresholds    
//	    tf = clock();
//	    t += tf - t0;
//
//	    System.println("Threshold Regions Found...\n");
//
//	    int k;
//	    for (k=0;k<_NUM_THRESHOLDS;k++) {
//
//	        t0 = clock();
//	        
//	        System.println("Threshold %d...\n",_thresholds[k]);
//
//	        Region *r = _representantRegionList[k];
//	                
//	        filterCandidates(img.w,img.h,r); // find set of points to search for targets
//
//	        #ifdef __DEBUG
//	        System.println("sorting candidates...\n");
//	        #endif
//
//	        sort(_candidates,_numCandidates); // sort _candidates by x coordinate
//	        
//	        #ifdef __DEBUG
//	        System.println("find possible targets...\n");
//	        #endif
//
//	        findPossibleTargets(_candidates,_numCandidates); // find all possible targets
//
//	        tf = clock();
//	        t += tf - t0;
//
//	        // load cell map
//	        CellMap *cellMap = readMap(argv[2]);
//
//	        #ifdef __DEBUG
//	        System.println("control points...\n");
//	        #endif
//	 
//	        if (_numTargets > 0) {
//	           double x[8];
//	           
//	           extractTargetPositions(_candidates,_targets,x);
//	           
//	           findControlPoints(x,cellMap,r); // find all possible targets
//
//	           sampleCellIntensities(img,cellMap); // sample cell intensities
//
//	        }
//
//	        #ifdef __DEBUG
//	        System.println("Saving EPS file...\n",k);
//	        #endif
//
//	        t0 = clock();
//	        char filename[50];
//	        sSystem.println(filename,"img/targets-%d.eps",_thresholds[k]);
//	        writeEPSwithCandidatesAndTargetsAndMapping(filename,img,_thresholds[k],cellMap,r);                
//	        tf = clock();
//	        tSave = tf-t0;
//
//	    }
//	    
//	    System.println("thresholds: %d loadImage: %3.6f saveImages %3.6f timeProcess: %3.6f",
//	    _NUM_THRESHOLDS,
//	    (double)tLoad/CLOCKS_PER_SEC,
//	    (double)tSave/CLOCKS_PER_SEC,
//	    (double)t/CLOCKS_PER_SEC
//	    );
//	    
//	    getch();
//
//	}

	
}


