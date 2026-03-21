package mixnfix.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import linsoft.gui.Input;
import linsoft.gui.SpringUtilities;

class PanelChooseDirectory extends JPanel {
    private boolean _ok;
    private Input _prefix;
    private Input _outputDir;
    public PanelChooseDirectory() {
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        _outputDir = new Input(App.getConfiguracao(),"outputimages","c:/pub/mixnfix/frames/",Input.TF_DIRECTORY,180);

        JButton btnOk = new JButton("OK");
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ok();
            }
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancel();
            }
        });

        JButton btnSearchDirectory = new JButton("...");
        btnSearchDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });

        this.add(new JLabel("Output Dir:"));
        this.add(_outputDir);
        this.add(btnSearchDirectory);

        this.add(btnOk);
        this.add(new JLabel(""));
        this.add(btnCancel);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(this,
                                2, 3, //rows, cols
                                6, 6,        //initX, initY
                                6, 6);       //xPad, yPad
    }

    public void ok() {
        _ok = true;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void cancel() {
        _ok = false;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void run(JFrame parent) {
        JDialog d = new JDialog(parent,"Extract Frames",true);
        linsoft.gui.util.Library.resizeAndCenterWindow(d,580,170);
        d.setContentPane(this);
        d.setVisible(true);
    }

    public boolean isOk() {
        return _ok;
    }

    public String getOutputDir() { return _outputDir.getText(); }

    public void chooseDirectory() {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setSelectedFile(new File(this._outputDir.getText()));
        jfc.setMultiSelectionEnabled(false);
        int result = jfc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            this._outputDir.setTextAndSave(jfc.getSelectedFile().getAbsolutePath());
        }
    }
}
