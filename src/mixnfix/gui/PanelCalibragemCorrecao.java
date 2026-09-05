package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mixnfix.MFI2Java;
import mixnfix.composicaoprova.Prova2TeX;
import mixnfix.folharesposta.BinaryField;
import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.Field;
import mixnfix.folharesposta.GeradorFolhaRespostas;
import mixnfix.folharesposta.MultiField;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.ProvaStructure;


public class PanelCalibragemCorrecao extends JPanel {

    ModelProvaCorrecao _provaCorrecao;

    ProvaStructure _prova;

    PanelParametrosProcessamentoImagem _panelControls;
    double _zoom = 1.0f;

    private BufferedImage _image;

    // ----------------------------------------------
    // vars to control threshold
    int _thresholdValue = 128;
    int _thresholdValueOnImage = -1;
    private BufferedImage _thresholdImage;
    boolean _viewThresholdImage = false;
    // vars to control threshold
    // ----------------------------------------------

    ArrayList<Cell> _selectedCells = new ArrayList<Cell>();

    JPanel _panelDesenho;

    JCheckBox _cbThreshold;
    JSlider _sliderThreshold;
    Timer _repaintRequest;


    /**
     * Build the calibration panel directly from the structure of an exam.
     * Useful to run (and to test) the grading module without a database
     * connection, e.g. in batch/headless mode.
     */
    public PanelCalibragemCorrecao(ProvaStructure prova) {
        this(null, prova);
    }

    public PanelCalibragemCorrecao(ModelProvaCorrecao provaCorrecao) {
        this(provaCorrecao, provaCorrecao.getModelProva().getProvaStructure());
    }

    private PanelCalibragemCorrecao(ModelProvaCorrecao provaCorrecao, ProvaStructure prova) {
        _provaCorrecao = provaCorrecao;
        _prova = prova;

        JButton btnCorrigir = new JButton("Corrigir");
        btnCorrigir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    corrigir();
                    _idAndCodeAvailable = false;
                    _panelDesenho.repaint();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JButton btnFindIDandMFCode = new JButton("ID & Code");
        btnFindIDandMFCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    findIDandMFCode();
                    _panelDesenho.repaint();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        JButton btnInverterSelecao = new JButton("Inverter");
        btnInverterSelecao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alterarSelecao(0);
            }
        });

        JButton btnMarcarSelecao = new JButton("Marcar");
        btnMarcarSelecao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alterarSelecao(1);
            }
        });

        JButton btnDesmarcarSelecao = new JButton("Desmarcar");
        btnDesmarcarSelecao.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                alterarSelecao(2);
            }
        });

        JButton btnFoto = new JButton("Foto");
        btnFoto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    _foto = askForFoto();
                    _gerador = null;
                    _idAndCodeAvailable = false;
                    if (_foto == null) {
                        _image = null;
                    }
                    else {
                        _image = ImageIO.read(_foto);
                    }

                    // say the threshold image is not ready
                    _thresholdValueOnImage=-1;

                    ((javax.swing.JDialog)getTopLevelAncestor()).setTitle("Calibrar Parâmetros de Correção: "+_foto);
                    _panelDesenho.repaint();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });



        JButton btnAdicionarCorrecaoAssociandoAlunoManualmente = new JButton("Add.Manual");
        btnAdicionarCorrecaoAssociandoAlunoManualmente.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                adicionarCorrecaoAssociandoAlunoManualmente();
            }
        });

        _panelControls = new PanelParametrosProcessamentoImagem();
        _panelControls.addListener(new PanelParametrosProcessamentoImagem.Listener() {
            public void change() {
                _panelDesenho.repaint();
            }
        });

        JToolBar panelButtons = new JToolBar();
        // panelButtons.setPreferredSize(new Dimension(200,90));
        panelButtons.add(btnFoto);
        panelButtons.add(btnCorrigir);
        panelButtons.add(btnFindIDandMFCode);
        panelButtons.addSeparator();
        panelButtons.add(btnInverterSelecao);
        panelButtons.add(btnMarcarSelecao);
        panelButtons.add(btnDesmarcarSelecao);
        panelButtons.addSeparator();
        panelButtons.add(btnAdicionarCorrecaoAssociandoAlunoManualmente);

        // --------------------------------------------------------------------
        // threshold things
        _cbThreshold = new JCheckBox("Threshold: "+_thresholdValue);
        _cbThreshold.setPreferredSize(new Dimension(110,22));
        _cbThreshold.setSelected(false);
        _cbThreshold.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _viewThresholdImage = _cbThreshold.isSelected();
                _panelDesenho.repaint();
            }
        });

        _repaintRequest = new Timer(50,new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _panelDesenho.repaint();
            }
        });
        _repaintRequest.setRepeats(false);


        _sliderThreshold = new JSlider(JSlider.HORIZONTAL, 0, 255, 128);
        _sliderThreshold.setPaintTrack(true);
        _sliderThreshold.setPaintTicks(true);
        _sliderThreshold.setPaintLabels(true);

        _sliderThreshold.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                _thresholdValue = _sliderThreshold.getValue();
                _cbThreshold.setText("Threshold: "+_thresholdValue);
                if (!_repaintRequest.isRunning())
                    _repaintRequest.start();
            }
        });

        JButton btnDefineThresholds = new JButton("Use");
        btnDefineThresholds.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _panelControls._tfThresholds.setTextAndSave(""+_thresholdValue+","+(_thresholdValue+10)+","+(_thresholdValue-10)+","+(_thresholdValue+20)+","+(_thresholdValue-20));
            }
        });

        panelButtons.addSeparator(new Dimension(30,20));
        panelButtons.add(_cbThreshold);
        panelButtons.add(btnDefineThresholds);
        panelButtons.add(_sliderThreshold);

        // threshold things
        // --------------------------------------------------------------------


        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BorderLayout());
        panelLeft.add(_panelControls,BorderLayout.CENTER);

        _panelDesenho = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                desenhoPaint(g);
            }
        };
        _panelDesenho.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                addSelection(e.getX(),e.getY());

                if (e.getButton()==MouseEvent.BUTTON3) {
                    adjustTrimmer(e.getX(),e.getY());
                }
            }
        });

        _panelDesenho.setPreferredSize(new Dimension(10000,10000));

        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(_panelDesenho), BorderLayout.CENTER);
        this.add(panelLeft,BorderLayout.WEST);
        this.add(panelButtons, BorderLayout.NORTH);

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, 0), "+");
        getActionMap().put("+", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                _zoom *= 1.2f;
                invalidate();
                repaint();
            }
        });

        getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, 0), "-");
        getActionMap().put("-", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                _zoom /= 1.2f;
                invalidate();
                repaint();
            }
        });

    }

    private BufferedImage thresholdImage() {
        int w = _image.getWidth();
        int h = _image.getHeight();

        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();
        // System.out.println("Number of Components "+number);
        int[] nBits = {8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits,false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(w,h);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();


        boolean color = (_image.getType() != BufferedImage.TYPE_BYTE_GRAY);
        int rgb[] = {0, 0, 0, 0};
        Raster source = _image.getData();

        for (int i=0;i<h;i++) {
            for (int j = 0; j < w; j++) {
                source.getPixel(j, i, rgb);
                int pij;
                if (color) {
                    pij = (rgb[0] * 30 + rgb[1] * 59 + rgb[2] * 11) / 100;
                }
                else {
                    pij = rgb[0];
                }

                dataBuffer.setElem(i*w+j,(pij < _thresholdValue ? 0 : 255));
            }
        }
        WritableRaster wr = Raster.createWritableRaster(sampleModel,dataBuffer, new Point(0,0));
        BufferedImage bf = new BufferedImage(colorModel, wr, true, null);
        return bf;
    }


    private void calculateThresholdImage() {
        if (_image == null)
            throw new RuntimeException("Ooooppsss");

        if (_thresholdValue == _thresholdValueOnImage)
            return;

        _thresholdImage = thresholdImage();

        // update
        _thresholdValueOnImage = _thresholdValue;
    }

    public void alterarSelecao(int s) {
        for (Cell c: this._selectedCells) {
            c.setSelected( s == 0 ? !c.isSelected() : (s==1 ? true : false));
        }
        _panelDesenho.repaint();
    }

    public void addSelection(double x, double y) {
        if (_gerador == null)
            return;
        ArrayList<Cell> cells = new ArrayList<Cell>();
        cells.addAll(_gerador.getCellMapFixo().getCells());
        cells.addAll(_gerador.getCellMapVariavel().getCells());
        double minDistance = Float.MAX_VALUE;
        Cell minCell = null;
        for (Cell c: cells) {
            double xx = c.getImageX() * _zoom;
            double yy = c.getImageY() * _zoom;
            double dist = (x - xx) * (x - xx) + (y - yy) * (y - yy);
            if (dist < minDistance) {
                minCell = c;
                minDistance = dist;
            }
        }
        if (minCell != null && minDistance <= 10) {
            if (this._selectedCells.contains(minCell))
                this._selectedCells.remove(minCell);
            else
                this._selectedCells.add(minCell);
        }
        this._panelDesenho.repaint();
    }


    public void adjustTrimmer(double x, double y) {
        x = x / _zoom;
        y = y / _zoom;

        double xL = _panelControls.getLeftMargin() * _image.getWidth();
        double xR = _panelControls.getRightMargin() * _image.getWidth();
        double yT = _panelControls.getTopMargin() * _image.getHeight();
        double yB = _panelControls.getBottomMargin() * _image.getHeight();

        Point2D.Double ps[] = new Point2D.Double[] {
            new Point2D.Double(xL,yT),
            new Point2D.Double(xL,yB),
            new Point2D.Double(xR,yT),
            new Point2D.Double(xR,yB)
        };

        int index = 0;
        double dist = (ps[0].getX() - x)*(ps[0].getX() - x) + (ps[0].getY() - y)*(ps[0].getY() - y);
        for (int i=1;i<ps.length;i++) {
            double distc = (ps[i].getX() - x)*(ps[i].getX() - x) + (ps[i].getY() - y)*(ps[i].getY() - y);
            if (distc < dist) {
                index = i;
                dist = distc;
            }
        }

        if (index == 0) {
            _panelControls._tfLeftMargin.setTextAndSave(String.format("%.3f",(x / _image.getWidth())));
            _panelControls._tfTopMargin.setTextAndSave(String.format("%.3f",(y / _image.getHeight())));
        }
        else if (index == 1) {
            _panelControls._tfLeftMargin.setTextAndSave(String.format("%.3f",(x / _image.getWidth())));
            _panelControls._tfBottomMargin.setTextAndSave(String.format("%.3f",(y / _image.getHeight())));
        }
        else if (index == 2) {
            _panelControls._tfRightMargin.setTextAndSave(String.format("%.3f",(x / _image.getWidth())));
            _panelControls._tfTopMargin.setTextAndSave(String.format("%.3f",(y / _image.getHeight())));
        }
        else if (index == 3) {
            _panelControls._tfRightMargin.setTextAndSave(String.format("%.3f",(x / _image.getWidth())));
            _panelControls._tfBottomMargin.setTextAndSave(String.format("%.3f",(y / _image.getHeight())));
        }
        this._panelDesenho.repaint();
    }

    public void desenhoPaint(Graphics g) {
        ((Graphics2D) g).scale(_zoom,_zoom);

        // ((Graphics2D) g).rotate(this._tfPhase.getInt() * Math.PI/2.0f);
        // ((Graphics2D) g).rotate(this._tfPhase.getInt() * Math.PI/2.0f);

        if (_image != null) {

            if (_viewThresholdImage) { // show threshold image?
                this.calculateThresholdImage();
                g.drawImage(_thresholdImage, 0, 0, null);
            }
            else { // show original image?
                g.drawImage(_image, 0, 0, null);
            }

            // margins lines
            g.setColor(Color.YELLOW);
            int xmin = (int) (_panelControls.getLeftMargin() * _image.getWidth());
            int xmax = (int) (_panelControls.getRightMargin() * _image.getWidth());
            int ymin = (int) (_panelControls.getTopMargin() * _image.getHeight());
            int ymax = (int) (_panelControls.getBottomMargin() * _image.getHeight());
            g.drawLine(xmin, ymin, xmax, ymin);
            g.drawLine(xmin, ymax, xmax, ymax);
            g.drawLine(xmin, ymin, xmin, ymax);
            g.drawLine(xmax, ymin, xmax, ymax);
        }

        if (_gerador != null) {
            for (ControlPoint cp : _gerador.getCellMapFixo().getControlPoints()) {
                double x = cp.getImageX();
                double y = cp.getImageY();

                int raio = 2;
                Shape shape = new java.awt.geom.Ellipse2D.Double(x-raio, y-raio, 2*raio+1, 2*raio+1);
                g.setColor(Color.YELLOW);
                ((Graphics2D) g).fill(shape);
            }
        }

        if (this._idAndCodeAvailable) {
            Graphics2D g2 = (Graphics2D) g;
            ArrayList<Cell> cells = new ArrayList<Cell>();

            CellMap mapFixo = _gerador.getCellMapFixo();
            mapFixo.getField(CellMap.MULTICAMPO_ID).getCells(cells);
            mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX).getCells(cells);
            for (Cell cell : cells) {
                int raio = 4;
                AffineTransform told = g2.getTransform();
                double x[] = {
                    cell.getImageX(), cell.getImageY()};
                double y[] = {
                    0, 0};
                told.transform(x, 0, y, 0, 1);
                g2.setTransform(new AffineTransform());
                Shape shape = new java.awt.geom.Ellipse2D.Double(y[0] - raio, y[1] - raio, 2 * raio + 1, 2 * raio + 1);

                if (cell.isSelected()) {
                    g.setColor(Color.YELLOW);
                    ( (Graphics2D) g).fill(shape);
                }

                g.setColor(this._selectedCells.contains(cell) ? Color.GREEN : Color.RED);
                ( (Graphics2D) g).draw(shape);

                if (cell.isSelected())
                    g.setColor(Color.YELLOW);
                else
                    g.setColor(Color.BLUE);
                g.drawString(String.format("%.0f",cell.getImageIntensity()),(int)y[0],(int)y[1]);
                g2.setTransform(told);
            }
        }


        if (this._idAndCodeAvailable && _gerador.getCellMapVariavel() != null) { // variable part
            Graphics2D g2 = (Graphics2D) g;
            java.util.List<Cell> cells = _gerador.getCellMapVariavel().getCells();
            for (Cell cell : cells) {
                int raio = 4;
                AffineTransform told = g2.getTransform();
                double x[] = {cell.getImageX(),cell.getImageY()};
                double y[] = {0,0};
                told.transform(x,0,y,0,1);
                g2.setTransform(new AffineTransform());
                Shape shape = new java.awt.geom.Ellipse2D.Double(y[0]-raio, y[1]-raio, 2*raio+1, 2*raio+1);
                if (cell.isSelected()) {
                    g.setColor(Color.YELLOW);
                    ( (Graphics2D) g).fill(shape);
                }

                g.setColor(this._selectedCells.contains(cell) ? Color.GREEN : Color.RED);
                ( (Graphics2D) g).draw(shape);

                if (cell.isSelected())
                    g.setColor(Color.YELLOW);
                else
                    g.setColor(Color.BLUE);
                g.drawString(String.format("%.0f",cell.getImageIntensity()),(int)y[0],(int)y[1]);
                g2.setTransform(told);
            }
        } // variable part
    }




    public File askForFoto() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(App.getProperty("umafoto")));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                String name = f.getName().toLowerCase();
                return (name.endsWith(".jpg")) ||
                       (name.endsWith(".jpeg"));
            }
            public String getDescription() {
                return "Fotos (.jpg ou .jpeg)";
            }
        });

        int result = jfc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            App.setProperty("umafoto",jfc.getSelectedFile().getAbsolutePath());
            File f = jfc.getSelectedFile();
            return f;
        }
        else return null;
    }


    // campos com as informações coletadas da figura
    private File _foto;
    boolean _idAndCodeAvailable = false;
    private GeradorFolhaRespostas _gerador;
    // campos com as informações coletadas da figura


    static byte _data[] = new byte[10000000];

    /** the picture currently loaded in the panel. */
    public File getFoto() { return _foto; }

    /** the answer sheet generator built by the last successful "Corrigir". */
    public GeradorFolhaRespostas getGerador() { return _gerador; }

    /** the image currently loaded in the panel. */
    public BufferedImage getImage() { return _image; }

    /**
     * Load the picture of an exam (same effect as pressing the "Foto"
     * button, but without the file chooser).
     */
    public void setFoto(File foto) throws IOException {
        _foto = foto;
        _gerador = null;
        _idAndCodeAvailable = false;
        _image = (foto == null ? null : ImageIO.read(foto));
        _thresholdValueOnImage = -1;
        _panelDesenho.repaint();
    }

    /** routine associated with the "Corrigir" button. */
    public void corrigir() throws Exception {
        GeradorFolhaRespostas g = new GeradorFolhaRespostas(_prova);
        _gerador = g;

        // a new answer sheet was built: the ID and the marked cells of the
        // previous picture (or of the previous run) are not valid anymore
        _idAndCodeAvailable = false;

        CellMap map = g.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(),map.getH(),50,1000);
        for (ControlPoint cp: map.getControlPoints()) {
            MFI2Java.addControlPoint(cp.getId(),cp.getX()-map.getX0(),cp.getY()-map.getY0());
        }

        for (Quadrilateral q: map.getQuads()) {
            MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
        }

        System.out.println("Set Constraints");
        MFI2Java.setConstraints(
            _panelControls.getThresholds(),
            _panelControls.getMinPixelWidth(),
            _panelControls.getMaxPixelWidth(),
            _panelControls.getMinPixelHeight(),
            _panelControls.getMaxPixelHeight(),
            _panelControls.getMinNumPixels(),
            _panelControls.getMaxNumPixels(),
            _panelControls.getPixelDensity(),
            _panelControls.getNumClosest(),
            _panelControls.getMinSide(),
            _panelControls.getAngleTolerance(),
            _panelControls.getTargetRadius(),
            _panelControls.getCorrectSideRatio(),
            _panelControls.getSideRatioTolerance(),
            _panelControls.getPhase(),
            _panelControls.getControlPointRadius(),
            _panelControls.getLeftMargin(),
            _panelControls.getRightMargin(),
            _panelControls.getTopMargin(),
            _panelControls.getBottomMargin());

        double controlPoints[] = new double[1000];
        _data = MFI2Java.ensureBuffer(_data, _image);
        MFI2Java.loadImageToBuffer(_image,_data);
        boolean b = MFI2Java.fitToImage(_data,_image.getWidth(),_image.getHeight(),controlPoints);

        if (!b) {
            message("Não encontrei os pontos de controle com as restrições dadas!");
            _gerador = null;
            return;
        }

        for (ControlPoint cp: map.getControlPoints()) {
            cp.setImageXY(controlPoints[2*cp.getId()],controlPoints[2*cp.getId()+1]);
        }

        // export the composed verification image (grey scale exam frame +
        // the detected control points painted in yellow)
        saveComposedImage(null);
    }



    /** routine associated with the "ID &amp; Code" button. */
    public void findIDandMFCode() throws Exception {
        if (_gerador == null) {
            message("No CellMap Yet! Ooooops! ");
            return;
        }

        CellMap mapFixo = _gerador.getCellMapFixo();

        { // obter intensidades da imagem
            double[] x = new double[2];
            double[] y = new double[2];
            ArrayList<Cell> cells = new ArrayList<Cell> ();
            mapFixo.getField(CellMap.MULTICAMPO_ID).getCells(cells);
            mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX).getCells(cells);
            for (Cell cell : cells) {
                x[0] = cell.getX() - mapFixo.getX0();
                x[1] = cell.getY() - mapFixo.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                System.out.println(String.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem

        MultiField mfId = (MultiField) mapFixo.getField(CellMap.MULTICAMPO_ID);
        String idAluno = "";
        for (Field f: mfId.getFields()) {
            OptionField of = (OptionField) f;
            of.evaluateFromImageIntensity(_panelControls.getGapLevel());
            int digit = of.getValue();
            if (digit == OptionField.BLANK || digit == OptionField.BLANK || digit == OptionField.NOT_EVALUATED)
                idAluno += "?";
            else
                idAluno += digit;
        }

        BinaryField mfCode = (BinaryField) mapFixo.getField(CellMap.MULTICAMPO_CODIGO_MIXNFIX);
        boolean codeword[] = new boolean[mfCode.getNumCells()];
        mfCode.evaluateFromImageIntensity(_panelControls.getSeparationLevel());
        mfCode.getValue(codeword);
        int indice = Prova2TeX.readnumber(codeword,0,20);
        int tipowithgolay = Prova2TeX.readnumber(codeword,20,24);
        int tipo = Prova2TeX.calcularNumeroNoCodigoDeGolay(tipowithgolay);

        _gerador.inicializarQuesitos(tipo);

        { // obter intensidades da imagem para a parte variável da prova
            CellMap mapVariavel = _gerador.getCellMapVariavel();
            double[] x = new double[2];
            double[] y = new double[2];
            for (Cell cell: mapVariavel.getCells()) {
                x[0] = cell.getX() - mapVariavel.getX0();
                x[1] = cell.getY() - mapVariavel.getY0();
                double intensity = MFI2Java.sampleCell(
                    x[0], x[1],
                    cell.getW0(), cell.getH0(),
                    cell.getW1(), cell.getH1(),
                    cell.getW2(), cell.getH2(),
                    cell.getWhiteSampleSet());
                cell.setImageIntensity(intensity);
                MFI2Java.tranformPoints(x, y, 1);
                cell.setImageXY(y[0], y[1]);
                System.out.println(String.format("Mapping of (%.3f,%.3f) -> (%.3f,%.3f)", x[0], x[1], y[0], y[1]));
            }
        } // obter intensidades da imagem para a parte variável da prova


        { // calcular os campos marcados para cada quesito
            CellMap cellMapVariavel = _gerador.getCellMapVariavel();

            HashMap<String,Integer> mapCampoValor = new HashMap<String,Integer>();

            java.util.List<Field> fields = cellMapVariavel.getAllFields();
            for (Field f: fields) {
                if (f instanceof OptionField) {
                    OptionField of = (OptionField) f;
                    of.evaluateFromImageIntensity(_panelControls.getGapLevel());
                    mapCampoValor.put(of.getNome(),of.getValue());
                    System.out.println(""+of.getNome()+" = "+of.getValue());
                }
                else if (f instanceof BinaryField) {
                    BinaryField bf = (BinaryField) f;
                    bf.evaluateFromImageIntensity(_panelControls.getSeparationLevel());
                }
            }

        } // calcular os campos marcados para cada quesito

        message("O identificador do aluno é: "+idAluno+"\nO indice da prova é: "+indice+"\n o tipo da prova é: "+tipo);
        _idAndCodeAvailable = true;
    }



    private void adicionarCorrecaoAssociandoAlunoManualmente() {

        if (!_idAndCodeAvailable || _gerador==null)  {
            message("Campos não inicializados!");
            return;
        }

        ArrayList<ModelAlunoProvaCorrecao> alunos = _provaCorrecao.getAlunosProvaCorrecaoSemEntradas();

        // renderer
        DefaultListCellRenderer renderer = new DefaultListCellRenderer() {
            public Component getListCellRendererComponent( JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
                Component result = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                ModelAlunoProvaCorrecao a = (ModelAlunoProvaCorrecao) value;
                this.setIcon(Images.Aluno16x16);
                this.setText(a.getAluno().getMatricula()+" "+a.getAluno().getNome());
                return result;
            }
        };

        ModelAlunoProvaCorrecao mapc = (ModelAlunoProvaCorrecao) PanelChooseObjectFromCollection.run(MainFrame.MAIN_FRAME,alunos,renderer,"Escolha aluno para associar prova",400,500);

        if (mapc == null)
            return;

        EC ec = new EC(
           EC.PROCESSAMENTO_SUCESSO,
           _foto,
           100, // threshold
           _panelControls.getPhase(),
           _gerador.getTipo(),
           _gerador.getControlPointsImagePositions(),
           _gerador.getGabaritoCorrente(),
           mapc);


        try {
            _provaCorrecao.addEntradaProvaCorrecao(ec);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

        message("Correção associada ao aluno "+mapc.getAluno().getMatricula()+" "+mapc.getAluno().getNome());

    }



    // ------------------------------------------------------------------
    // helpers used both by the interactive and by the batch/headless usage
    // ------------------------------------------------------------------

    /**
     * Show a message to the user. When there is no display (batch or
     * headless grading) the message is simply logged, so that the grading
     * module can be run without a graphical environment.
     */
    private void message(String text) {
        if (java.awt.GraphicsEnvironment.isHeadless()) {
            System.out.println(text);
            return;
        }
        JOptionPane.showMessageDialog(this, text);
    }

    /**
     * Directory where the verification images are written. It can be
     * changed with the system property <code>mixnfix.output.dir</code>.
     */
    public static String getOutputDir() {
        return mixnfix.VisualizationExporter.getOutputDir();
    }

    /**
     * Compose the grey scale (black and white) picture of the exam with
     * the control points found by the grading module, painted in yellow,
     * and save it as a JPG file inside {@link #getOutputDir()}.
     *
     * @param name file name (without extension); when null the name of
     *             the picture being graded is used
     * @return the file that was written, or null
     */
    public File saveComposedImage(String name) {
        if (_image == null || _gerador == null)
            return null;
        java.util.List<ControlPoint> cps = _gerador.getCellMapFixo().getControlPoints();
        double xy[] = new double[2 * cps.size()];
        int i = 0;
        for (ControlPoint cp: cps) {
            xy[i++] = cp.getImageX();
            xy[i++] = cp.getImageY();
        }
        return mixnfix.VisualizationExporter.saveComposed(
            _image, xy, cps.size(),
            (name != null ? name : (_foto != null ? _foto.getName() : "composed_result")));
    }

    /** recursively lay out a component tree that has no native peer. */
    private static void layoutTree(Component c) {
        if (c instanceof Container) {
            Container container = (Container) c;
            container.doLayout();
            for (Component child : container.getComponents())
                layoutTree(child);
        }
    }

    /**
     * Render this panel (the grading GUI frame) into an image file. Used
     * to document the state of the interface after "Corrigir" and after
     * "ID &amp; Code" without requiring a display.
     */
    public File saveGUIImage(String name, int width, int height) {
        try {
            this.setSize(width, height);
            // Component.validate() only lays out a tree that has native
            // peers; when rendering off screen (batch/headless) the layout
            // has to be triggered explicitly.
            layoutTree(this);
            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
            this.printAll(g2);
            g2.dispose();
            File dir = new File(getOutputDir());
            dir.mkdirs();
            File out = new File(dir, name + ".jpg");
            ImageIO.write(img, "jpg", out);
            System.out.println("GUI image written to " + out.getAbsolutePath());
            return out;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}


