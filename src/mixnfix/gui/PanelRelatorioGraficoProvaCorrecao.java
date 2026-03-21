package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JPanel;

import mixnfix.modelo.Aluno;

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
public class PanelRelatorioGraficoProvaCorrecao extends JPanel {
    ModelProvaCorrecao _model;

    ArrayList<PointEPC> _points;

    private double _mean;
    private double _stddev;

    private double _width = 640;
    private double _height = 480;
    private double _margin = 0.05;


    private double sx;
    private double sy;
    private double tx;
    private double ty;

    public PanelRelatorioGraficoProvaCorrecao(ModelProvaCorrecao mpc) throws SQLException, IOException {
        _model = mpc;
        _points = new ArrayList<PointEPC>();
        for (ModelEntradaProvaCorrecao e : mpc.getEntradasProvaCorrecao()) {
            _points.add(new PointEPC(e));
        }

        Collections.sort(_points,new Comparator() {
            public int compare(Object o1, Object o2) {
                PointEPC p1 = (PointEPC) o1;
                PointEPC p2 = (PointEPC) o2;
                if (p1.getNota() < p2.getNota())
                    return -1;
                else if (p1.getNota() > p2.getNota())
                    return 1;
                else return p1.getAluno().getNome().compareTo(p2.getAluno().getNome());
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        // set positions
        int coordx = 0;
        for (PointEPC p: _points) {
            p.setPosition(coordx++,p.getNota());
        }
        // set positions

        // calculate mean
        int n = _points.size();
        double sum = 0.0;
        for (PointEPC p: _points) {
            sum += p.getNota();
        }
        _mean = sum / n;
        // calculate mean

        // calculate stddev
        sum = 0.0;
        for (PointEPC p: _points) {
            double x = p.getNota() - _mean;
            sum += x * x;
        }
        _stddev = Math.sqrt(sum/(n-1));
        // calculate stddev

        JPanel drawPanel = new JPanel() {
            public void paint(Graphics g) {
                super.paint(g);
                setDimensions(getWidth(),getHeight());
                drawHardWork((Graphics2D) g);
            }
        };

        drawPanel.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }
            public void mouseMoved(MouseEvent e) {
                logAluno(e.getX(),e.getY());
            }
        });


        // set label
        _lblAluno.setOpaque(true);
        _lblAluno.setBackground(Color.WHITE);
        _lblAluno.setForeground(Color.BLUE);
        _lblAluno.setPreferredSize(new Dimension(10,25));


        //
        this.setLayout(new BorderLayout());
        this.add(drawPanel,BorderLayout.CENTER);
        this.add(_lblAluno,BorderLayout.SOUTH);

    }
    private JLabel _lblAluno = new JLabel("");

    public double getMean() {
        return _mean;
    }

    public double getMinNota() {
        return (_points.size() > 0 ? _points.get(0).getNota(): 0);
    }

    public double getMaxNota() {
        return (_points.size() > 0 ? _points.get(_points.size()-1).getNota(): 0);
    }

    public double getStdDev() {
        return _stddev;
    }

    public void setDimensions(int w, int h) {
        _width = w;
        _height = h;
    }

    public void drawHardWork(Graphics2D g) {
        int minH = (int)Math.round(Math.min(0,getMinNota()));
        int maxH = (int)Math.round(Math.max(10,getMaxNota()));
        double hh = maxH - minH;

        sx = (_width - 2*(_margin*_width))/(Math.max(1,_points.size()-1));
        sy = -(_height - 2*(_margin*_height))/hh;
        tx = _margin * _width;
        ty = (1.0 - _margin) * _height;
        ty = -sy * minH + ty;

        for (PointEPC p: _points) {
            Ellipse2D.Double c = new Ellipse2D.Double(tx + sx * p.getX(),ty + sy * p.getY(),5,5);
            g.setColor(Color.GREEN);
            g.fill(c);
            g.setColor(Color.BLACK);
            g.draw(c);
        }

        double x0 = tx + 0 * sx;
        double x1 = tx + (_points.size()-1) * sx;
        double y0 = ty + (getMean() - getStdDev()) * sy;
        double y1 = ty + (getMean() + getStdDev()) * sy;

        Rectangle2D.Double r = new Rectangle2D.Double
            (Math.min(x0,x1),Math.min(y0,y1),Math.abs(x1-x0),Math.abs(y1-y0));

        g.setColor(new Color(0,0,255,20));
        g.fill(r);
        g.setColor(new Color(0,0,255,100));
        g.draw(r);

        Line2D.Double lMean = new Line2D.Double(tx + 0 * sx, ty + getMean()*sy,tx + (_points.size()-1) * sx, ty + getMean()*sy);
        g.setColor(new Color(255,0,0,100));
        g.draw(lMean);

        for (int i=minH;i<=maxH;i++) {
            Line2D.Double lTen = new Line2D.Double(tx + 0 * sx, ty + i * sy,tx + (_points.size()-1) * sx, ty + i * sy);
            g.setColor(new Color(0,255,0,100));
            g.draw(lTen);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Tahoma",Font.ITALIC,16));
            g.drawString(String.format("%d",i),(float)(tx + (_points.size()-1) * sx)+10f,(float)(ty + i * sy));
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("Courier New",Font.PLAIN,14));
        g.drawString(String.format("n: %-5d",_points.size()),10,20);
        g.drawString(String.format("Mean: %-5.3f",getMean()),10,40);
        g.drawString(String.format("StdDev: %-5.3f",getStdDev()),10,60);
        g.drawString(String.format("Min: %-5.3f",getMinNota()),10,80);
        g.drawString(String.format("Max: %-5.3f",getMaxNota()),10,100);

    }

    public void logAluno(double xx, double yy) {

        // points
        double distMin = Double.MAX_VALUE;

        PointEPC pMin = null;
        for (PointEPC p: _points) {
            double xp = tx + p.getX() * sx;
            double yp = ty + p.getY() * sy;

            // System.out.println(String.format("(%.3f,%.3f) -> (%.3f,%.3f) - (%.3f,%.3f)",p.getX(),p.getY(),xp,yp,xx,yy));

            double dist = Math.sqrt((xx - xp) * (xx - xp) + (yy - yp) * (yy - yp));
            if (dist < distMin) {
                distMin = dist;
                pMin = p;
            }
        }

        if (pMin != null && distMin < 10) {
            _lblAluno.setText(String.format("%s  %s  %.3f",pMin.getAluno().getMatricula(),pMin.getAluno().getNome(),pMin.getNota()));
            _lblAluno.repaint();
        }
        else {
            _lblAluno.setText("");
            _lblAluno.repaint();
        }
    }
}

class PointEPC {
    private ModelEntradaProvaCorrecao _model;
    private double _x;
    private double _y;
    private double _nota;
    public PointEPC(ModelEntradaProvaCorrecao mpc) throws SQLException, IOException {
        _model = mpc;
        _model.preencherProvaComGabaritoCorrente();
        _nota = _model.getProvaStructure().avaliarNota();
    }
    public void setPosition(double x, double y) {
        _x = x;
        _y = y;
    }
    public double getX() {
        return _x;
    }
    public double getY() {
        return _y;
    }
    public double distance(double x, double y) {
        return Math.sqrt((x - _x) * (x - _x) + (y - _y) * (y - _y));
    }
    public double getNota() {
        return _nota;
    }
    public Aluno getAluno() {
        return _model.getAluno();
    }



}
