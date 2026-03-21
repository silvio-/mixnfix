
package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mixnfix.ImgProcessor;
import mixnfix.TargetSet;

/**
 * Painel para análise de imagem.
 */
public class PanelImagePattern extends JPanel {
    ImgProcessor _model;


    JLabel _lblThreshold = new JLabel("threshold: ");
    JLabel _lblConfidence = new JLabel("confidence: ");
    JLabel _lblNivel = new JLabel("nível: ");

    JComboBox _cbTargets = new JComboBox();
    private boolean _cbTargetsIsEnabled = true;

   public PanelImagePattern(String image) throws Exception {

        // construcao normal
        BufferedImage img = null;
        img = ImageIO.read(new File(image));

        // obter a imagem em grayscale
        if (img.getType() != BufferedImage.TYPE_BYTE_GRAY) {
             img = ImgProcessor.toGray(img);
        }


        //
        _model = new ImgProcessor(img);

        //
        this.setLayout(new BorderLayout());

        { // panel desenho
            JPanel panelDesenho = new JPanel() {
                public void paint(Graphics g) {
                    super.paint(g);
                    if (_model != null) {
                        _model.paint(g);
                    }
                }
            };
            panelDesenho.setPreferredSize(new Dimension(img.getWidth(),img.getHeight()));
            this.add(new JScrollPane(panelDesenho), BorderLayout.CENTER);
        } // panel desenho

        { // bottom panel
            JPanel bottomPanel = new JPanel();
            this.add(bottomPanel, BorderLayout.SOUTH);

            { // find targets
                JButton btnTargets = new JButton("Targets");
                bottomPanel.add(btnTargets);
                btnTargets.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        findTargets();
                    }
                });
            } // find targets

            { // find targets
                JButton btnGrid = new JButton("Grid");
                bottomPanel.add(btnGrid);
                btnGrid.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        grid();
                    }
                });
            } // find targets

            { // find targets
                JButton btnThreshold = new JButton("Threshold");
                bottomPanel.add(btnThreshold);
                btnThreshold.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        threshold();
                    }
                });
            } // find targets


            { // check boxes
                JCheckBox cbShowGrid = new JCheckBox("Exibig Grid");
                cbShowGrid.setSelected(_model.is_showGrid());
                cbShowGrid.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _model.set_showGrid( ( ( (JCheckBox) e.getSource()).isSelected()));
                        repaint();
                    }
                });
                bottomPanel.add(cbShowGrid);

                JCheckBox cbShowCurrentSolution = new JCheckBox("Solution");
                cbShowCurrentSolution.setSelected(_model.is_showCurrentSolution());
                cbShowCurrentSolution.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _model.set_showCurrentSolution( ( ( (JCheckBox) e.getSource()).isSelected()));
                        repaint();
                    }
                });
                bottomPanel.add(cbShowCurrentSolution);

                JCheckBox cbShowWhiteRegions = new JCheckBox("White Regions");
                cbShowWhiteRegions.setSelected(_model.is_showWhiteRegions());
                cbShowWhiteRegions.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _model.set_showWhiteRegions( ( ( (JCheckBox) e.getSource()).isSelected()));
                        repaint();
                    }
                });
                bottomPanel.add(cbShowWhiteRegions);

                JCheckBox cbShowTargets = new JCheckBox("Targets");
                cbShowTargets.setSelected(_model.is_showTargets());
                cbShowTargets.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _model.set_showTargets( ( ( (JCheckBox) e.getSource()).isSelected()));
                        repaint();
                    }
                });
                bottomPanel.add(cbShowTargets);

                JCheckBox cbThreshold = new JCheckBox("Threshold");
                cbThreshold.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _model.set_showThreshold( ( ( (JCheckBox) e.getSource()).isSelected()));
                        repaint();
                    }
                });
                bottomPanel.add(cbThreshold);
            } //check boxes

            {
                _cbTargets.setRenderer(new DefaultListCellRenderer() {
                    public Component getListCellRendererComponent(JList list,Object value,int index,boolean isSelected,boolean cellHasFocus) {
                        super.getListCellRendererComponent(list,value,index,isSelected,cellHasFocus);
                        if (value != null) {
                            TargetSet S = (TargetSet) value;
                            this.setText("Threshold: " + S.getThreshold());
                            if (S.getSucess()) {
                                this.setForeground(Color.BLUE);
                            }
                            else {
                                this.setForeground(Color.RED);
                            }
                        }
                        return this;
                    }
                });
                _cbTargets.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (_cbTargetsIsEnabled) {
                            TargetSet S = (TargetSet) _cbTargets.getSelectedItem();
                            _model.assign(S);
                            repaint();
                        }
                    }
                });
                bottomPanel.add(_cbTargets);
            }


            {
                JButton btn = new JButton("Mudar Nivel");
                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Object x = JOptionPane.showInputDialog((Component)e.getSource(), "Nível","" + _model.getNivelParaCorrecao());
                        if (x == null)
                            return;
                        try {
                            int nivel = Integer.parseInt("" + x);
                            _model.setNivelParaCorrecao(nivel);
                            _lblNivel.setText("nível: "+nivel);
                            repaint();
                        }
                        catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog((Component)e.getSource(),"Tem que ser um numero");
                        }
                    }
                });
                bottomPanel.add(btn);
            }


        } // bottom panel

        { // top panel
            JPanel topPanel = new JPanel();
            this.add(topPanel, BorderLayout.NORTH);

            _lblThreshold.setHorizontalAlignment(JLabel.CENTER);
            _lblConfidence.setHorizontalAlignment(JLabel.CENTER);
            _lblNivel.setHorizontalAlignment(JLabel.CENTER);
            _lblNivel.setText("nível:"+_model.getNivelParaCorrecao());

            topPanel.setLayout(new GridLayout(1,3));
            topPanel.add(_lblThreshold);
            topPanel.add(_lblConfidence);
            topPanel.add(_lblNivel);
        } // top panel


    }

    private void findTargets() {
        int steps = _model.findTargets(null);
        _lblThreshold.setText("threshold: "+_model.getLastThreshold()+" (steps " + steps + ")");
        if (steps > 0)
            _model.set_threshold(_model.getLastThreshold());

        // fill in targets tried
        _cbTargetsIsEnabled = false;
        _cbTargets.removeAllItems();
        for (TargetSet S: _model.getTargetsTried())
            _cbTargets.addItem(S);
        _cbTargets.setSelectedIndex(_cbTargets.getItemCount()-1);
        _cbTargetsIsEnabled = true;

        this.repaint();
    }

    private void threshold() {
        int threshold = 0;
        while (true) {
            Object o = JOptionPane.showInputDialog(this, "Threshold: ",_model.get_threshold());
            if (o == null)
                return;

            try {
                StringTokenizer t = new StringTokenizer("" + o, " ,-x");
                threshold = Integer.parseInt(t.nextToken());
                break;
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,"Entrada deve ser entre 0 e 255");
            }
        }
        _model.set_threshold(threshold);
        _lblThreshold.setText("threshold: "+threshold);
        this.repaint();
    }

    private void grid() {
        int rows = 0;
        int cols = 0;
        while (true) {
            Object o = JOptionPane.showInputDialog(this, "Linhas e Colunas: ","20 28");
            if (o == null)
                return;

            try {
                StringTokenizer t = new StringTokenizer("" + o, " ,-x");
                rows = Integer.parseInt(t.nextToken());
                cols = Integer.parseInt(t.nextToken());
                break;
            }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,"Entrada deve ser: <Linhas>  <Colunas>. Ex: 20 28");
            }
        }
        _model.matrix(rows,cols);
        _lblConfidence.setText("confidence: "+String.format("%.3f",_model.getConfidence()));
        this.repaint();
    }
}
