package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import linsoft.gui.Input;


class PanelEditarSettings extends JPanel {
    private Input _tfAcrobatReader;
    private Input _tfExcel;
    private Input _tfBrowser;

    public PanelEditarSettings() {

        int i = 1;

        this.add(new JLabel("Caminho para Acrobat Reader:"),new GridBagConstraints(0,i++,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfAcrobatReader = new Input(App.getConfiguracao(),"acrobat","C:/Program Files/Adobe/Acrobat 7.0/Reader/AcroRd32.exe",Input.TF_FILE,250);
        this.add(_tfAcrobatReader,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        this.add(new JLabel("Caminho para Excel:"),new GridBagConstraints(0,i++,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfExcel = new Input(App.getConfiguracao(),"excel","C:/Program Files/MSOffice/Office10/Excel.exe",Input.TF_FILE,250);
        this.add(_tfExcel,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        this.add(new JLabel("Caminho para Browser:"),new GridBagConstraints(0,i++,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this._tfBrowser = new Input(App.getConfiguracao(),"browser","C:/Program Files/Internet Explorer/iexplore.exe",Input.TF_FILE,250);
        this.add(_tfBrowser,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
    }
}
