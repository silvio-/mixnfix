package mixnfix;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Test extends JPanel {

    public static final String PROPERTY_ROWS = "rows";
    public static final String PROPERTY_COLUMNS = "columns";

    public static final String PROPERTY_LEFT_MARGIN = "left";
    public static final String PROPERTY_RIGHT_MARGIN = "right";
    public static final String PROPERTY_TOP_MARGIN = "top";
    public static final String PROPERTY_BOTTOM_MARGIN = "bottom";

    public static final String PROPERTY_CLEAN_FACTOR = "cleanfactor";
    public static final String PROPERTY_WHITE_AREA = "whitearea";
    public static final String PROPERTY_CELL_AREA = "cellarea";

    public static final String PROPERTY_SHOW_GRID = "showgrid";
    public static final String PROPERTY_SHOW_SOLUTION = "showsolution";
    public static final String PROPERTY_SHOW_TARGETS = "showtargets";
    public static final String PROPERTY_SHOW_TARGET_AREA = "showtargetarea";
    public static final String PROPERTY_SHOW_WHITE_GRID = "showwhitegrid";

    public static int getIntProperty(String name, int defaultValue) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        return pref.getInt(name,defaultValue);
    }
    public static void setIntProperty(String name, int value) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        pref.putInt(name,value);
    }
    public static double getDoubleProperty(String name, double defaultValue) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        return pref.getDouble(name,defaultValue);
    }
    public static void setDoubleProperty(String name, double value) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        pref.putDouble(name,value);
    }
    public static boolean getBooleanProperty(String name, boolean defaultValue) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        return pref.getBoolean(name,defaultValue);
    }
    public static void setBooleanProperty(String name, boolean value) {
        Preferences pref = Preferences.userRoot().node("mnfimg");
        pref.putBoolean(name,value);
    }

    Xmfn _model;
    public Test(String fileName, int rows, int columns) {

        // caso especial
        if (fileName == null) {
            JButton btnAbrir = new JButton("Abrir");
            btnAbrir.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    abrirFigura();
                }
            });
            this.add(btnAbrir);
            return;
        }

        // construcao normal
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(fileName));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        //
        _model = new Xmfn(
            img,
            rows,
            columns,
            Test.getDoubleProperty(PROPERTY_WHITE_AREA,0.1),
            Test.getDoubleProperty(PROPERTY_CELL_AREA,0.35),
            Test.getBooleanProperty(PROPERTY_SHOW_GRID,true),
            Test.getBooleanProperty(PROPERTY_SHOW_SOLUTION,true),
            Test.getBooleanProperty(PROPERTY_SHOW_WHITE_GRID,true),
            Test.getBooleanProperty(PROPERTY_SHOW_TARGETS,true));


        this.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if(e.isControlDown()) {
                    int[] p = { 0, 0 };
                    if (_model.findRegion(x, y, p)) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (_model.getSelectionType(p[0],p[1]) == Xmfn.USER_DEFINED_ON)
                                _model.setSelectionType(p[0],p[1],Xmfn.LEVEL_DEFINED);
                            else
                                _model.setSelectionType(p[0],p[1],Xmfn.USER_DEFINED_ON);
                        }
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (_model.getSelectionType(p[0],p[1]) == Xmfn.USER_DEFINED_OFF)
                                _model.setSelectionType(p[0],p[1],Xmfn.LEVEL_DEFINED);
                            else
                                _model.setSelectionType(p[0],p[1],Xmfn.USER_DEFINED_OFF);
                        }
                        repaint();
                    }
                }
                else {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        _model.moveCorner(x,y);
                        repaint();
                    }
                    else {
                        int[] p = { 0, 0};
                        if (_model.findRegion(x, y, p)) {
                            System.out.println("pixel (" + x + "," + y + ") está na célula (" + p[0] + "," + p[1] + ")");
                        }
                        else
                            System.out.println("Nao esta em nenhuma célula.");
                    }
                }
            }
        });

        // add buttons
        this.setLayout(new BorderLayout());

        JPanel bottomPanel = new JPanel();
        this.add(bottomPanel,BorderLayout.SOUTH);
        bottomPanel.setLayout(new GridLayout(2,10,2,2));

        JButton btnCalcularDados = new JButton("Calcular Dados");
        btnCalcularDados.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.calculateData();
                try {
                    _model.printData();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
        });
        bottomPanel.add(btnCalcularDados);

        JButton btnSaveCurrentImage = new JButton("Salvar Imagem Corrente");
        btnSaveCurrentImage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    _model.saveImageFileWithCurrentView("resources/view.png");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                repaint();
            }
        });
        bottomPanel.add(btnSaveCurrentImage);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    _model.saveResultMatrix("resources/result.dat");
                }
                catch (Exception ex1) {
                    ex1.printStackTrace();
                }
            }
        });
        bottomPanel.add(btnSalvar);

        JButton btnTargets = new JButton("Targets");
        btnTargets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // _model.findTargets();
                int steps = _model.findTargets2("c:/temp/threshold.jpg");
                if (steps > 0) {
                    System.out.println("Threshold Steps: "+steps);
                }
                repaint();
            }
        });
        bottomPanel.add(btnTargets);

        JButton btnAbrir = new JButton("Abrir");
        btnAbrir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                abrirFigura();
            }
        });
        bottomPanel.add(btnAbrir);

        JCheckBox cbShowGrid = new JCheckBox("Exibig Grid");
        cbShowGrid.setSelected(Test.getBooleanProperty(PROPERTY_SHOW_GRID,true));
        cbShowGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.set_showGrid((((JCheckBox) e.getSource()).isSelected()));
                Test.setBooleanProperty(PROPERTY_SHOW_GRID,_model.is_showGrid());
                repaint();
            }
        });
        bottomPanel.add(cbShowGrid);


        JCheckBox cbShowCurrentSolution = new JCheckBox("Solution");
        cbShowCurrentSolution.setSelected(Test.getBooleanProperty(PROPERTY_SHOW_SOLUTION,true));
        cbShowCurrentSolution.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.set_showCurrentSolution((((JCheckBox) e.getSource()).isSelected()));
                Test.setBooleanProperty(PROPERTY_SHOW_SOLUTION,_model.is_showCurrentSolution());
                repaint();
            }
        });
        bottomPanel.add(cbShowCurrentSolution);


        JCheckBox cbShowWhiteRegions = new JCheckBox("White Regions");
        cbShowWhiteRegions.setSelected(Test.getBooleanProperty(PROPERTY_SHOW_WHITE_GRID,true));
        cbShowWhiteRegions.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.set_showWhiteRegions((((JCheckBox) e.getSource()).isSelected()));
                Test.setBooleanProperty(PROPERTY_SHOW_WHITE_GRID,_model.is_showWhiteRegions());
                repaint();
            }
        });
        bottomPanel.add(cbShowWhiteRegions);


        JCheckBox cbShowTargets = new JCheckBox("Targets");
        cbShowTargets.setSelected(Test.getBooleanProperty(PROPERTY_SHOW_TARGETS,true));
        cbShowTargets.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _model.set_showTargets((((JCheckBox) e.getSource()).isSelected()));
                Test.setBooleanProperty(PROPERTY_SHOW_TARGETS,_model.is_showTargets());
                repaint();
            }
        });
        bottomPanel.add(cbShowTargets);
    }

    private void abrirFigura() {
        JFileChooser f = new JFileChooser();
        Preferences pref = Preferences.userRoot().node("mnfimg");
        String s = pref.get("openpath",".");
        f.setSelectedFile(new File(s));
        int r = f.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            String fileName = f.getSelectedFile().getAbsolutePath();
            pref.put("openpath",fileName);

            Integer rowsObj = this.getInteger("Rows",Test.getIntProperty(PROPERTY_ROWS,20));
            int rows = (rowsObj == null ? 20 : rowsObj.intValue());
            Test.setIntProperty(PROPERTY_ROWS,rows);

            Integer colsObj = this.getInteger("Columns",Test.getIntProperty(PROPERTY_COLUMNS,28));
            int cols = (colsObj == null ? 28 : colsObj.intValue());
            Test.setIntProperty(PROPERTY_COLUMNS,cols);

            Test t = new Test(fileName,rows,cols);
            JFrame frame = (JFrame) this.getTopLevelAncestor();
            frame.setTitle("MIXnFIX (v3) análise: "+fileName);
            frame.setContentPane(t);
            frame.validate();
            frame.repaint();
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        if (_model != null) {
            _model.paint(g);
        }
    }

    /**
     * Obter um inteiro.
     */
    private Integer getInteger(String message, int defaultValue) {
        while (true) {
            Object obj = JOptionPane.showInputDialog(this,message, new Integer(defaultValue));
            if (obj != null && obj instanceof String) {
                try {
                    int x = Integer.parseInt( (String) obj);
                    return new Integer(x);
                }
                catch (Exception ex1) {
                    ex1.printStackTrace();
                }
            }
            else return null;
        }
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

