package mixnfix.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import mixnfix.folharesposta.Cell;
import mixnfix.folharesposta.CellMap;
import mixnfix.folharesposta.ControlPoint;
import mixnfix.folharesposta.FabricaDeFolhaDeResposta;
import mixnfix.folharesposta.IFolhaResposta;
import mixnfix.folharesposta.OptionField;
import mixnfix.folharesposta.Quadrilateral;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.Quesito;

/**
 * Correcao
 */
public class QuestionarioUI {

    private ModelEntradaColetaQuestionario _model;
    private mixnfix.prova.ProvaStructure _prova;

    private CellMap _cellMapFixo;
    private CellMap _cellMapVariavel;

    private PanelVisualizacaoProva _panelVisualizacaoProva;

    public QuestionarioUI(ModelEntradaColetaQuestionario model) throws IOException, SQLException  {
        _model = model;
        _panelVisualizacaoProva = new PanelVisualizacaoProva();

        // esta operação permuta a mixnfix.prova e preenche as questoes na
        // extrutura mixnfix.prova com o gabariro gravado no banco.
        _model.preencherProvaComGabaritoCorrente();

        // specific
        mixnfix.prova.ProvaStructure p = _model.getProvaStructure();
        _prova = p;

        //
        IFolhaResposta g = FabricaDeFolhaDeResposta.newFolhaResposta(p,_model.getEntradaColetaQuestionario().getTipoFolhaResposta());
        g.inicializarQuesitos(0);

        _cellMapFixo = g.getCellMapFixo();
        _cellMapVariavel = g.getCellMapVariavel();

        double[] cps = null;
        try {
            cps = model.getControlPointsImagePosition();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            throw new RuntimeException("OOOOPSS");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("OOOOPSS");
        }

        // --------------------------------------------------------------------
        // a parit dos pontos de controle definir o centro das células e o
        // centro dos pontos de controle na imagem
        for (ControlPoint cp : _cellMapFixo.getControlPoints()) {
            cp.setImageXY(cps[2 * cp.getId()], cps[2 * cp.getId() + 1]);
        }

        // Now that every control point has its practical (image) position,
        // compute the projective homography of each quad in the piecewise
        // projective grid before mapping any cell position.
        _cellMapFixo.computeQuadHomographies();

        for (Cell cell : _cellMapFixo.getCells()) {
            double mapping[] = {0,0};
            if (!_cellMapFixo.getImageCoordinateByQuads(cell.getX(),cell.getY(),mapping))
                throw new RuntimeException("Quad mapping problem!");
            cell.setImageXY(mapping[0],mapping[1]);
        }

        for (Cell cell : _cellMapVariavel.getCells()) {
            double mapping[] = {0,0};
            if (!_cellMapFixo.getImageCoordinateByQuads(cell.getX(),cell.getY(),mapping))
                throw new RuntimeException("Quad mapping problem!");
            cell.setImageXY(mapping[0],mapping[1]);
        }
        // --------------------------------------------------------------------


        // --------------------------------------------------------------------
        // A partir do gabarito do aluno selecionar no CellMap
        for (Cell c: _cellMapVariavel.getCells()) {
            c.setSelected(false);
        }

        java.util.List<Quesito> quesitos = _prova.getQuesitosPermutados();
        int i = 1;
        for (Quesito q : quesitos) {
            if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS || q.getTipo() == Quesito.TIPO_SUBJETIVA_5 || q.getTipo() == Quesito.TIPO_SUBJETIVA_9) {
                java.util.List<Integer> l = q.getRespostaAluno();
                OptionField f = (OptionField) _cellMapVariavel.getField(""+i);
                for (int value: l) {
                    f.getCell(value).setSelected(true);
                }
            }
            else if (q.getTipo() == Quesito.TIPO_NUMERICO_99) {
                java.util.List<Integer> l = q.getRespostaAluno();
                if (!l.isEmpty()) {
                    int value = l.get(0);
                    int d = q.getNumDigitosQuesitoNumerico();
                    for (int j=0;j<d;j++) {
                        int digit = value % 10;
                        value =  (value / 10);
                        OptionField ft = (OptionField) _cellMapVariavel.getField(""+i+"."+(d-j-1));
                        if (digit < 0)
                            digit = digit + 10;
                        ft.getCell(digit).setSelected(true);
                    }
                }
            }
            else if (q.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                int j = 0;
                for (Object obj: q.getPermutacao()) {
                    ItemQuesito itq = (ItemQuesito) obj;
                    java.util.List<Integer> l = itq.getRespostaAluno();
                    OptionField f = (OptionField) _cellMapVariavel.getField(""+i+"."+j);
                    for (int value: l)
                        f.getCell(value).setSelected(true);
                    j++;
                }
            }
            i++;
        }
        // A partir do gabarito do aluno selecionar no CellMap
        // --------------------------------------------------------------------

    }

    public String getDetalhamentoNota() {
        StringBuffer detalhamento = new StringBuffer();
        ArrayList<Quesito> qs = _prova.getQuesitos();
        int i = 1;
        for (Quesito q: qs) {
            detalhamento.append(String.format("%3d. %s\n",i++,q.getTextRespostaAluno()));
        }
        return detalhamento.toString();
    }

    public PanelVisualizacaoProva getPanelVisualizacaoProva() {
        return _panelVisualizacaoProva;
    }

    public class PanelVisualizacaoProva extends JPanel {
        private BufferedImage _image;
        private double _zoom = 1.0;

        public BufferedImage newImage() {
            int phase = _model.getEntradaColetaQuestionario().getPhase();
            int w = _image.getWidth();
            int h = _image.getHeight();
            if (phase == 1 || phase == 3) {
                int aux = w;
                w = h;
                h = aux;
            }
            BufferedImage newImage = new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
            this.drawHardWork(newImage.getGraphics());
            return newImage;
        }

        public PanelVisualizacaoProva() throws IOException {
            super();
            _image = ImageIO.read(new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir)+"/"+_model.getEntradaColetaQuestionario().getFoto()));
        }

        public void zoomin() {
            _zoom *= 1.20;
            this.revalidate();
            this.repaint();
        }

        public void zoomout() {
            _zoom /= 1.20;
            this.revalidate();
            this.repaint();
        }


        public void paint(Graphics g) {
            super.paint(g);
            drawHardWork(g);
        }

        public void drawHardWork(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;

            g2.scale(_zoom,_zoom);

            if (_model.getEntradaColetaQuestionario().getPhase() == 1) {
                g2.transform(new AffineTransform(0,-1,1,0,0,_image.getWidth()));
            }
            else if (_model.getEntradaColetaQuestionario().getPhase() == 3) {
                g2.transform(new AffineTransform(0,1,-1,0,_image.getHeight(),0));
            }
            g.drawImage(_image,0,0,null);

            for (Quadrilateral q : _cellMapFixo.getQuads()) {
                GeneralPath shape = new GeneralPath();
                shape.moveTo(q.getP0().getImageX(), q.getP0().getImageY());
                shape.lineTo(q.getP1().getImageX(), q.getP1().getImageY());
                shape.lineTo(q.getP2().getImageX(), q.getP2().getImageY());
                shape.lineTo(q.getP3().getImageX(), q.getP3().getImageY());
                shape.closePath();
                g2.setColor(new Color(0.0f,0.0f,1.0f,0.5f));
                g2.draw(shape);
            }

            // draw control points
            for (ControlPoint cp : _cellMapFixo.getControlPoints()) {
                double x = cp.getImageX();
                double y = cp.getImageY();

                int raio = 2;
                Shape shape = new java.awt.geom.Ellipse2D.Double(x-raio, y-raio, 2*raio+1, 2*raio+1);
                g.setColor(Color.YELLOW);
                ((Graphics2D) g).fill(shape);
            }

            java.util.List<Cell> cells = _cellMapVariavel.getCells();
            for (Cell cell : cells) {
                double x = cell.getImageX();
                double y = cell.getImageY();

                int raio = 4;
                Shape shape = new java.awt.geom.Ellipse2D.Double(x-raio, y-raio, 2*raio+1, 2*raio+1);
                g.setColor(Color.MAGENTA);
                ((Graphics2D) g).draw(shape);

                raio = 1;
                shape = new java.awt.geom.Ellipse2D.Double(x-raio, y-raio, 2*raio+1, 2*raio+1);
                if (cell.isSelected()) {
                    g.setColor(Color.YELLOW);
                    ( (Graphics2D) g).fill(shape);
                }
            }
        }

    }
}

