package mixnfix.folharesposta;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

/**
 *
 */
public class CellMap {
	public static final String MULTICAMPO_ID = "ID";
	public static final String MULTICAMPO_CODIGO_MIXNFIX = "CodigoMF";

	private double _x0;
	private double _y0;

	private double _W;
	private double _H;


	private ArrayList<Cell> _cells;
	private ArrayList<ControlPoint> _controls;
	private ArrayList<Quadrilateral> _quads;

	private ArrayList<Field> _allFields;
	private ArrayList<Field> _rootFields;

	public CellMap(double x0, double y0, double W, double H) {
		_x0 = x0;
		_y0 = y0;
		_W = W;
		_H = H;
		_cells = new ArrayList<Cell>();
		_controls = new ArrayList<ControlPoint>();
		_quads = new ArrayList<Quadrilateral>();
		_allFields = new ArrayList<Field>();
		_rootFields = new ArrayList<Field>();

		// these are automatic
		this.addControlPoint(_x0,_y0);         // top left
		this.addControlPoint(_x0+_W,_y0);      // top right
		this.addControlPoint(_x0+_W,_y0+_H);   // bottom right
		this.addControlPoint(_x0,_y0+_H);      // bottom left
	}
	public double getX0() { return _x0; }
	public double getY0() { return _y0; }
	public double getW() { return _W; }
	public double getH() { return _H; }

	public void addCells(double x0, double y0, double dx, double dy, double w, double h, int cols, int rows) {
		for (int i=0;i<rows;i++) {
			for (int j=0;j<cols;j++) {
				Cell c = new Cell(_cells.size()+1,x0 + j * dx, y0 + i * dy, w, h);
				_cells.add(c);
			}
		}
	}

	public Cell addCell(double x0, double y0, double w, double h) {
		Cell c = new Cell(_cells.size()+1,x0, y0, w, h);
		_cells.add(c);
		return c;
	}

	public ControlPoint addControlPoint(double x, double y) {
		ControlPoint cp = new ControlPoint(this._controls.size(),x,y);
		_controls.add(cp);
		return cp;
	}

	public List<Cell> getCells() {
		return (List<Cell>)_cells.clone();
	}

	public List<Quadrilateral> getQuads() {
		return (List<Quadrilateral>)_quads.clone();
	}

	public List<Field> getAllFields() {
		return (List<Field>) _allFields.clone();
	}

	public List<Field> getRootFields() {
		return (List<Field>) _rootFields.clone();
	}


	public Cell findCellById(int id) {
		for (Cell c : _cells) {
			if (c.getId() == id)
				return c;
		}
		return null;
	}

	public void clearAllCells() {
		for (Cell c: _cells) {
			c.setSelected(false);
		}
	}

	public BinaryField addBinaryField(MultiField parent, String tag) {
		int n = _allFields.size();
		BinaryField bf = new BinaryField(n,tag);
		_allFields.add(bf);
		if (parent == null) {
			_rootFields.add(bf);
		}
		else {
			parent.addField(bf);
		}
		return bf;
	}

	public OptionField addOptionField(MultiField parent, String tag) {
		int n = _allFields.size();
		OptionField of = new OptionField(n,tag);
		_allFields.add(of);
		if (parent == null) {
			_rootFields.add(of);
		}
		else {
			parent.addField(of);
		}
		return of;
	}

	public MultiField addMultiField(MultiField parent, String tag) {
		int n = _allFields.size();
		MultiField mf = new MultiField(n,tag);
		_allFields.add(mf);
		if (parent == null) {
			_rootFields.add(mf);
		}
		else {
			parent.addField(mf);
		}
		return mf;
	}

	public Field getField(String tag) {
		for (Field f: _allFields) {
			if (tag.equals(f.getNome())) {
				return f;
			}
		}
		return null;
	}

	public static CellMap getFolhaCOVEST() {
		// CellMap result = new CellMap(229,216);
		double x0 = 10f;
		double y0 = 10f;
		CellMap result = new CellMap(x0,y0,180,221);

		// result.addControlPoint(new ControlPoint((double) 153, (double) 80));

		result.addControlPoint((double) 19.5 + x0, (double) 21 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 80 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 87 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 120 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 153 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 186 + y0);
		result.addControlPoint((double) 19.5 + x0, (double) 221 + y0);

		result.addControlPoint((double) 91 + x0, (double) 21 + y0);
		result.addControlPoint((double) 91 + x0, (double) 80 + y0);
		result.addControlPoint((double) 99.5 + x0, (double) 87 + y0);
		result.addControlPoint((double) 99.5 + x0, (double) 120 + y0);
		result.addControlPoint((double) 99.5 + x0, (double) 153 + y0);
		result.addControlPoint((double) 99.5 + x0, (double) 186 + y0);
		result.addControlPoint((double) 99.5 + x0, (double) 221 + y0);

		result.addControlPoint((double) 180 + x0, (double) 21 + y0);
		result.addControlPoint((double) 180 + x0, (double) 80 + y0);
		result.addControlPoint((double) 180 + x0, (double) 87 + y0);
		result.addControlPoint((double) 180 + x0, (double) 120 + y0);
		result.addControlPoint((double) 180 + x0, (double) 153 + y0);
		result.addControlPoint((double) 180 + x0, (double) 186 + y0);
		result.addControlPoint((double) 180 + x0, (double) 221 + y0);

		result.addCells(x0+28.5f,y0+23.5f,6,6,4,4,10,10);
		result.addCells(x0+101.5f,y0+23.5f,9,6,4,4,9,10);

		result.addCells(x0+29.5f,y0+91.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+124.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+157.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+190.5f,9,6,4,4,8,5);

		result.addCells(x0+110.5f,y0+91.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+124.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+157.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+190.5f,9,6,4,4,8,5);

		return result;
	}

	public static CellMap createCellMapExample() {
		double x0 = 10f;
		double y0 = 10f;
		CellMap result = new CellMap(x0,y0,180,221);

		// result.addControlPoint(new ControlPoint((double) 153, (double) 80));
		result.addControlPoint(x0+19.5f, y0+21);
		result.addControlPoint(x0+19.5f, y0+80);
		result.addControlPoint(x0+19.5f, y0+87);
		result.addControlPoint(x0+19.5f, y0+120);
		result.addControlPoint(x0+19.5f, y0+153);
		result.addControlPoint(x0+19.5f, y0+186);
		result.addControlPoint(x0+19.5f, y0+221);

		result.addControlPoint(x0+91f,   y0+21);
		result.addControlPoint(x0+91f,   y0+80);
		result.addControlPoint(x0+99.5f, y0+87);
		result.addControlPoint(x0+99.5f, y0+120);
		result.addControlPoint(x0+99.5f, y0+153);
		result.addControlPoint(x0+99.5f, y0+186);
		result.addControlPoint(x0+99.5f, y0+221);

		result.addControlPoint(x0+180, y0+21);
		result.addControlPoint(x0+180, y0+80);
		result.addControlPoint(x0+180, y0+87);
		result.addControlPoint(x0+180, y0+120);
		result.addControlPoint(x0+180, y0+153);
		result.addControlPoint(x0+180, y0+186);
		result.addControlPoint(x0+180, y0+221);

		result.addCells(x0+ 28.5f,y0+23.5f,6,6,4,4,10,10);
		result.addCells(x0+101.5f,y0+23.5f,9,6,4,4,9,10);

		result.addCells(x0+29.5f,y0+91.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+124.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+157.5f,9,6,4,4,8,5);
		result.addCells(x0+29.5f,y0+190.5f,9,6,4,4,8,5);

		result.addCells(x0+110.5f,y0+91.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+124.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+157.5f,9,6,4,4,8,5);
		result.addCells(x0+110.5f,y0+190.5f,9,6,4,4,8,5);


		// add cell
		/*
        try {

            MultiCampo mcc = result.addMultiCampo("Controle");
            for (int i = 0; i < 100; i++) {
                Campo c = mcc.newCampo();
                c.addCell(result.findCellById(i+1));
            }

            MultiCampo mcp = result.addMultiCampo("Prova");
            for (int i = 0; i < 2; i++) {
                Campo c = mcp.newCampo();
                for (int j = 0; j < 10; j++)
                    c.addCell(result.findCellById(101 + i + 9*j));
            }


            MultiCampo mci = result.addMultiCampo("Identificador");
            for (int i = 0; i < 7; i++) {
                Campo c = mci.newCampo();
                for (int j = 0; j < 10; j++)
                    c.addCell(result.findCellById(103 + i + 9*j));
            }

            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    int numQuestao = 8*i + (j + 1);
                    MultiCampo mc = result.addMultiCampo("Q" + numQuestao);
                    Campo c = mc.newCampo();
                    c.addCell(result.findCellById(191 + 40*i + j));
                    c.addCell(result.findCellById(199 + 40*i + j));
                    c.addCell(result.findCellById(207 + 40*i + j));
                    c.addCell(result.findCellById(215 + 40*i + j));
                    c.addCell(result.findCellById(223 + 40*i + j));
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
		 */

		return result;
	}

	public String getEPSFormattedString(double x0, double y0, double w, double h) {
		return String.format("newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath fill",
				(x0-w/2.0f),
				(y0-h/2.0f),
				(x0+w/2.0f),
				(y0-h/2.0f),
				(x0+w/2.0f),
				(y0+h/2.0f),
				(x0-w/2.0f),
				(y0+h/2.0f));
	}

	public void writeEPS(String fileName) throws Exception {
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));


		double C = 72.0f/25.4f; // escrever em pontos
		// double C = 1.0f;

		// get bounding box of the gabarito
		double x0 = -10;
		double y0 = -10;
		double xf = this.getW()+10;
		double yf = this.getH()+10;
		double W = this.getW();
		double H = this.getH();

		// get bounding box of the gabarito
		pw.println("%!PS-Adobe-3.0 EPSF-3.0");
		pw.println(String.format("%%%%BoundingBox: %d %d %d %d",(int)(C*x0),(int)(C*y0),(int)(C*xf),(int)(C*yf)));
		pw.println(String.format("%%%%HiResBoundingBox: %4.3f %4.3f %4.3f %4.3f",C*x0,C*y0,C*(xf),C*(yf)));
		pw.println(String.format("0 %4.3f translate",this.getH()*C));
		pw.println(String.format("1 -1 scale"));
		pw.println(String.format("%.4f %.4f scale",C,C));

		pw.println(String.format("0.2 setlinewidth"));

		double color[] = {0.9568f ,0.7568f, 0.5098f};

		/*
        double targetW = 1.5f;
        double targetH = 1.5f;


        for (int k=0;k<5;k++) {
            pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath fill", (k/4.0f)*W , (double)0, targetW / 2.0f));
            pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath fill", (k/4.0f)*W , (double)H, targetW / 2.0f));
        }

        for (int k=1;k<4;k++) {
            pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath fill",(double)0,(k/4.0f)*H,targetW/2.0f));
            pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath fill",(double)W,(k/4.0f)*H,targetW/2.0f));
        }
		 */

		double targetW = 2.5f;
		double targetH = 2.5f;

		for (ControlPoint cp: _controls) {
			pw.println(String.format("newpath %.4f %.4f %.4f 0 360 arc closepath fill",cp.getX(),cp.getY(),targetW/2.0f));
		}

		for (Cell c: _cells) {
			pw.println(String.format("%.3f %.3f %.3f setrgbcolor",color[0],color[1],color[2]));

			/*
            pw.println(String.format("newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath stroke",
                          (c.getX()-c.getW()/2.0f),
                          (c.getY()-c.getH()/2.0f),
                          (c.getX()+c.getW()/2.0f),
                          (c.getY()-c.getH()/2.0f),
                          (c.getX()+c.getW()/2.0f),
                          (c.getY()+c.getH()/2.0f),
                          (c.getX()-c.getW()/2.0f),
                          (c.getY()+c.getH()/2.0f))); */

			pw.println(String.format("newpath %.2f %.2f %.2f 0 360 arc closepath stroke",c.getX(),c.getY(),c.getW()/2.0f));

			if (Math.random() > 0.5) {
				pw.println(String.format("0 0 0 setrgbcolor"));
				pw.println(String.format("newpath %.2f %.2f %.2f 0 360 arc closepath fill",c.getX(),c.getY(),c.getW()/2.0f));
				/*
                pw.println(String.format("newpath %5.2f %5.2f moveto %5.2f %5.2f lineto %5.2f %5.2f lineto %5.2f %5.2f lineto closepath fill",
                                         (c.getX() - c.getW() / 2.0f),
                                         (c.getY() - c.getH() / 2.0f),
                                         (c.getX() + c.getW() / 2.0f),
                                         (c.getY() - c.getH() / 2.0f),
                                         (c.getX() + c.getW() / 2.0f),
                                         (c.getY() + c.getH() / 2.0f),
                                         (c.getX() - c.getW() / 2.0f),
                                         (c.getY() + c.getH() / 2.0f)));*/
			}

			if (c.getText() != null) {
				pw.println("save");
				pw.println(String.format("%.3f %.3f %.3f setrgbcolor",color[0],color[1],color[2]));
				pw.println(String.format("%.3f %.3f translate",c.getX()+c.getDxText(),c.getY()+c.getDyText()));
				pw.println("0 0 moveto");
				pw.println("initmatrix");
				pw.println("/Helvetica findfont");
				pw.println("8 scalefont setfont");
				pw.println("("+c.getText()+") show");
				pw.println("restore");
			}
		}
		pw.close();
	}

	public List<ControlPoint> getControlPoints() {
		return (List<ControlPoint>)_controls.clone();
	}

	public int getNumControlPoints() {
		return _controls.size();
	}


	/**
	 * Maps a theoretical point (x,y) on the answer sheet to its image
	 * (practical) coordinates by locating the quadrilateral that contains
	 * it and applying that quad's projective homography (piecewise
	 * projective mapping). Replaces the previous piecewise-affine
	 * (triangle-based) mapping.
	 *
	 * @return true if a quadrilateral containing the point was found and
	 *         the mapping was applied, false otherwise.
	 */
	public boolean getImageCoordinateByQuads(double x, double y, double mapping[]) {
		for (Quadrilateral q : _quads) {
			if (q.mapToImage(x, y, mapping))
				return true;
		}
		return false;
	}

	/**
	 * Build the piecewise-projective quad grid from the current control
	 * points. The control points are expected to lie on a rectangular grid
	 * with several columns (same x within each column) and the same number
	 * of rows in every column. Quads are formed between pairs of adjacent
	 * columns and consecutive rows, in theoretical order TL, TR, BR, BL.
	 *
	 * After the practical (image) positions of the control points are
	 * known, call {@link #computeQuadHomographies()} to finalize the
	 * homography of each quad.
	 */
	public void buildQuadsFromGrid() {
		_quads.clear();

		// Collect distinct x values (columns) with an EPSILON tolerance.
		TreeSet<Double> xSet = new TreeSet<Double>();
		for (ControlPoint cp : _controls)
			xSet.add(cp.getX());

		double[] xValues = new double[xSet.size()];
		int xi = 0;
		for (double xv : xSet)
			xValues[xi++] = xv;

		int numCols = xValues.length;
		if (numCols < 2)
			return;

		// For each column, collect control points sorted by y.
		ControlPoint[][] grid = new ControlPoint[numCols][];
		for (int col = 0; col < numCols; col++) {
			ArrayList<ControlPoint> colPoints = new ArrayList<ControlPoint>();
			for (ControlPoint cp : _controls) {
				if (Math.abs(cp.getX() - xValues[col]) < Quadrilateral.EPSILON)
					colPoints.add(cp);
			}
			colPoints.sort((a, b) -> Double.compare(a.getY(), b.getY()));
			grid[col] = colPoints.toArray(new ControlPoint[0]);
		}

		// Build quads between adjacent columns and consecutive rows.
		for (int col = 0; col < numCols - 1; col++) {
			int numRows = Math.min(grid[col].length, grid[col + 1].length);
			for (int row = 0; row < numRows - 1; row++) {
				// TL = grid[col][row],     TR = grid[col+1][row]
				// BR = grid[col+1][row+1], BL = grid[col][row+1]
				Quadrilateral q = new Quadrilateral(
					grid[col][row],         // p0 = TL
					grid[col + 1][row],     // p1 = TR
					grid[col + 1][row + 1], // p2 = BR
					grid[col][row + 1]      // p3 = BL
				);
				_quads.add(q);
			}
		}
	}

	/**
	 * Recompute the homography of every quad from the current practical
	 * (image) positions of its control points. Call this once the
	 * control-point image positions have been loaded/updated (e.g. after
	 * {@code ControlPoint.setImageXY} on every point).
	 */
	public void computeQuadHomographies() {
		for (Quadrilateral q : _quads)
			q.computeHomography();
	}

	/**
	 * Write a file with normalized control points and quads.
	 *
	 * Format:
	 *   numCells numControlPoints numQuads
	 *   W H
	 *   <numControlPoints> lines of: id x y
	 *   <numQuads> lines of: p0.id p1.id p2.id p3.id   (TL, TR, BR, BL)
	 *   <numCells> lines of: id x y w0 h0 w1 h1 w2 h2 whiteSampleSet
	 */
	public void writeNormalizedControlPoints(String fileName) throws Exception {

		double x0 = this.getX0();
		double y0 = this.getY0();

		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));

		int numQuads = this._quads.size();
		pw.append(String.format("%d %d %d\n",_cells.size(),_controls.size(),numQuads));

		// print map dimension
		pw.append(String.format("%.3f %.3f\n",this.getW(),this.getH()));

		// print control points
		int numControls = 0;
		for (ControlPoint pc: _controls) {
			pw.append(String.format("%d %.3f %.3f\n",++numControls,pc.getX()-x0,pc.getY()-y0));
		}

		// print quads (TL TR BR BL)
		for (Quadrilateral q : _quads) {
			pw.append(String.format("%d %d %d %d\n",
				q.getP0().getId(),
				q.getP1().getId(),
				q.getP2().getId(),
				q.getP3().getId()));
		}

		// print cells
		for (Cell c: _cells) {
			pw.append(String.format("%d %.3f %.3f %.3f %.3f %.3f %.3f %.3f %.3f %d\n",
					c.getId(),c.getX()-x0,c.getY()-y0,
					c.getW0(),c.getH0(),
					c.getW1(),c.getH1(),
					c.getW2(),c.getH2(),
					c.getWhiteSampleSet()));
		}

		pw.close();
	}

	public void gerarCelulasEquidistantes(int rows, int cols, double w, double h) throws Exception {

		double deltaX = (double)this.getW()/(cols-1);
		double deltaY = (double)this.getH()/(rows-1);
		for (int i=0;i<rows;i++) {
			for (int j=0;j<cols;j++) {
				Cell c = new Cell(_cells.size()+1,j * deltaX, i * deltaY, w, h);
				_cells.add(c);
			}
		}
	}
}


class CellMapPanel extends JPanel {
	double _conversion = 2.834f;
	CellMap _map;
	double _W;
	double _H;
	public CellMapPanel(CellMap m, double W, double H) {
		_W = W;
		_H = H;
		_map = m;
		this.setPreferredSize(new Dimension( (int) (_conversion * _W), (int) (_conversion * _H)));
		JButton b = new JButton("");
		b.setVisible(true);
		this.add(b);

		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "+");
		getActionMap().put("+", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_conversion *= 2.0f;
				setPreferredSize(new Dimension( (int) (_conversion * _W), (int) (_conversion * _H)));
				invalidate();
				repaint();
			}
		});

		getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "-");
		getActionMap().put("-", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				_conversion /= 2.0f;
				setPreferredSize(new Dimension( (int) (_conversion * _W), (int) (_conversion * _H)));
				invalidate();
				repaint();
			}
		});

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int x = e.getX();
				int y = e.getY();
				System.out.println(String.format("%.3f %.3f",x/_conversion,y/_conversion));
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// g2d.setRenderingHint();
		Rectangle2D.Double r = new Rectangle2D.Double(0,0,_conversion*_W,_conversion*_H);
		g2d.setColor(Color.WHITE);
		g2d.fill(r);


		Ellipse2D.Double circle = new Ellipse2D.Double(0,0,1,1);
		for (ControlPoint cp: _map.getControlPoints()) {
			circle.setFrame(_conversion*(cp.getX()-1),_conversion*(cp.getY()-1),_conversion*2,_conversion*2);
			g2d.setColor(Color.GREEN);
			g2d.draw(circle);
		}

		/*
        for (MultiCampo mc: _map.getMultiCampos()) {
            boolean first = true;
            double minx = 0;
            double miny = 0;
            double maxx = 0;
            double maxy = 0;
            for (Campo c: mc.getCampos()) {
                for (Cell cc: c.getCells()) {
                    if (first) {
                        minx = cc.getX();
                        maxx = cc.getX();
                        miny = cc.getY();
                        maxy = cc.getY();
                        first = false;
                    }
                    if (minx > cc.getX()-cc.getW2()/2f)
                        minx = cc.getX()-cc.getW2()/2f;
                    if (maxx < cc.getX()+cc.getW2()/2f)
                        maxx = cc.getX()+cc.getW2()/2f;
                    if (miny > cc.getY()-cc.getH2()/2f)
                        miny = cc.getY()-cc.getH2()/2f;
                    if (maxy < cc.getY()+cc.getH2()/2f)
                        maxy = cc.getY()+cc.getH2()/2f;
                }
            }
            r.setFrame(_conversion*minx,_conversion*miny,_conversion*(maxx-minx),_conversion*(maxy-miny));

            if (_map.getMultiCamposSelecionados().contains(mc))
                g2d.setColor(Color.YELLOW);
            else
                g2d.setColor(Color.WHITE);
            g2d.fill(r);
        }*/

		for (Cell c: _map.getCells()) {
			r.setFrame(_conversion*(c.getX() - c.getW()/2.0),_conversion*(c.getY() - c.getH()/2.0),_conversion*c.getW(),_conversion*c.getH());
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.draw(r);
			String st = c.getText();
			if (st != null) {
				TextLayout textLayout = new TextLayout(st,g2d.getFont(),g2d.getFontRenderContext());
				double textWidth = textLayout.getVisibleAdvance();
				double textAscent = textLayout.getAscent();
				textLayout.draw(g2d,(float) (_conversion*(c.getX() + c.getDxText())- textWidth/2.0f) , (float) (_conversion*(c.getY() + c.getDyText())));
			}


			TextLayout textLayout = new TextLayout(""+c.getId(),g2d.getFont(),g2d.getFontRenderContext());
			double textWidth = textLayout.getVisibleAdvance();
			double textAscent = textLayout.getAscent();
			g2d.setColor(Color.red);
			textLayout.draw(g2d, (float) (_conversion*c.getX() - textWidth/2.0f) , (float) (_conversion*c.getY() + textAscent/2.0f));
			g2d.setColor(Color.black);

		}
	}
}
