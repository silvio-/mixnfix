package mixnfix.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import linsoft.gui.Input;
import linsoft.gui.SpringUtilities;

class PanelExtractVideo extends JPanel {
    private boolean _ok;
    private Input _inputFileName;
    private Input _numFrames;
    private Input _prefix;
    private Input _outputDir;
    public PanelExtractVideo() {
        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        _inputFileName = new Input(App.getConfiguracao(),"videofn","c:/pub/mixnfix/video.mpg",Input.TF_FILE,200);
        _numFrames = new Input(App.getConfiguracao(),"numframes","100",Input.TF_INTEIRO,50);
        _prefix = new Input(App.getConfiguracao(),"prefixpix","frame",Input.TF_DIRECTORY,90);
        _outputDir = new Input(App.getConfiguracao(),"outputframes","c:/pub/mixnfix/frames/",Input.TF_DIRECTORY,180);

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

        JButton btnVideo = new JButton("...");
        btnVideo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchVideo();
            }
        });

        JButton btnSearchDirectory = new JButton("...");
        btnSearchDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseDirectory();
            }
        });

        this.add(new JLabel("Video"));
        this.add(_inputFileName);
        this.add(btnVideo);

        this.add(new JLabel("Num. Frames:"));
        this.add(_numFrames);
        this.add(new JLabel(""));

        this.add(new JLabel("Prefix:"));
        this.add(_prefix);
        this.add(new JLabel(""));

        this.add(new JLabel("Output Dir:"));
        this.add(_outputDir);
        this.add(btnSearchDirectory);

        this.add(btnOk);
        this.add(new JLabel(""));
        this.add(btnCancel);

        //Lay out the panel.
        SpringUtilities.makeCompactGrid(this,
                                5, 3, //rows, cols
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

    public int getNumFrames() { return _numFrames.getInt(); }
    public String getInputFileName() { return _inputFileName.getText(); }
    public String getPrefix() { return _prefix.getText(); }
    public String getOutputDir() { return _outputDir.getText(); }

    public void searchVideo() {
        JFileChooser jfc = new JFileChooser();
        jfc.setSelectedFile(new File(this._inputFileName.getText()));
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                StringTokenizer st = new StringTokenizer(f.getName(), ".");
                String last = null;
                while (st.hasMoreTokens()) {
                    last = st.nextToken();
                }
                if (last != null) {
                    last = last.toLowerCase();
                    if ("mpg".equals(last) || "avi".equals(last))
                        return true;
                }
                return false;
            }

            public String getDescription() {
                return "Videos (.mpg ou .avi)";
            }
        });
        int result = jfc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            this._inputFileName.setTextAndSave(jfc.getSelectedFile().getAbsolutePath());
        }
    }

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
