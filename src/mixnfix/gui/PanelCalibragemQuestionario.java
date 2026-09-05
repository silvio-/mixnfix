package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import mixnfix.folharesposta.BinaryField;
import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.Field;
import mixnfix.folharesposta.FolhaRespostaQuestionarioBasico;
import mixnfix.folharesposta.IFolhaResposta;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.ProvaStructure;

public class PanelCalibragemQuestionario extends JPanel {

    ModelColetaQuestionario _coletaQuestionario;

    ProvaStructure _prova;

    PanelParametrosProcessamentoImagem _panelControls;

    double _zoom = 1.0f;

    // ----------------------------------------------
    // vars to control threshold
    int _thresholdValue = 128;
    int _thresholdValueOnImage = -1;
    private BufferedImage _thresholdImage;
    boolean _viewThresholdImage = false;
    // vars to control threshold
    // ----------------------------------------------

    private BufferedImage _image;

    ArrayList<Cell> _selectedCells = new ArrayList<Cell>();

    JPanel _panelDesenho;

    JCheckBox _cbThreshold;
    JSlider _sliderThreshold;
    Timer _repaintRequest;

    public PanelCalibragemQuestionario(ModelColetaQuestionario ColetaQuestionario) {
        _coletaQuestionario = ColetaQuestionario;
        _prova = ColetaQuestionario.getModelProva().getProvaStructure() ;

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

                    // say threshold image is not ready
                    _thresholdValueOnImage = -1;

                    ((javax.swing.JDialog)getTopLevelAncestor()).setTitle("Calibrar Parâmetros de Correção: "+_foto);
                    _panelDesenho.repaint();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
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

        // --------------------------------------------------------------------
        // threshold things
        _cbThreshold = new JCheckBox("Threshold: "+_thresholdValue);
        _cbThreshold.setPreferredSize(new Dimension(140,22));
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

        panelButtons.addSeparator(new Dimension(20,20));
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
                // addSelection(e.getX(),e.getY());
                if (e.getButton() == MouseEvent.BUTTON3) {
                    adjustTrimmer(e.getX(), e.getY());
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


    public void alterarSelecao(int s) {
        for (Cell c: this._selectedCells) {
            c.setSelected( s == 0 ? !c.isSelected() : (s==1 ? true : false));
        }
        _panelDesenho.repaint();
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

        if (this._idAndCodeAvailable) { // variable part
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
    private IFolhaResposta _gerador;
    // campos com as informações coletadas da figura

    /**
     * Solicitar
     * @param prova ModelProva
     * @return IFolhaResposta
     */
    public IFolhaResposta newGeradorFolhaResposta(ModelProva prova) {
        return new FolhaRespostaQuestionarioBasico(prova.getProvaStructure());
    }

    static byte _data[] = new byte[10000000];

    private void corrigir() throws Exception {
        _gerador = newGeradorFolhaResposta(this._coletaQuestionario.getModelProva());
        IFolhaResposta g = _gerador;

        CellMap map = g.getCellMapFixo();
        MFI2Java.newCellMap(map.getW(),map.getH(),50,1000);
        for (ControlPoint cp: map.getControlPoints()) {
            MFI2Java.addControlPoint(cp.getId(),cp.getX()-map.getX0(),cp.getY()-map.getY0());
        }

        for (Quadrilateral q: map.getQuads()) {
            MFI2Java.addQuad(q.getP0().getId(),q.getP1().getId(),q.getP2().getId(),q.getP3().getId());
        }

        System.out.println("Set Contraints");
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
            JOptionPane.showMessageDialog(this,"Não encontrei os pontos de controle com as restrições dadas!");
            _gerador = null;
            return;
        }

        for (ControlPoint cp: map.getControlPoints()) {
            cp.setImageXY(controlPoints[2*cp.getId()],controlPoints[2*cp.getId()+1]);
        }

    }

    private void findIDandMFCode() throws Exception {
        if (_gerador == null) {
            JOptionPane.showMessageDialog(this, "No CellMap Yet! Ooooops! ");
            return;
        }

        _gerador.inicializarQuesitos(0);

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

        _idAndCodeAvailable = true;
    }
}


