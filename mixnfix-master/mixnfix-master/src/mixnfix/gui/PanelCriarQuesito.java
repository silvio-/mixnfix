package mixnfix.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import linsoft.gui.Input;
import linsoft.gui.InputComboBox;

class PanelCriarQuesito extends JPanel {
    public static final String ALTERNATIVAS = "alternativas";
    public static final String FALSO_VERDADEIRO = "falso/verdadeiro";
    public static final String NUMERICO = "numérico_99";
    public static final String SUBJETIVO = "subjetiva_5";

    public static final int ID_ALTERNATIVAS = 0;
    public static final int ID_FALSO_VERDADEIRO = 1;
    public static final int ID_NUMERICO = 2;
    public static final int ID_SUBJETIVO = 3;

    Input _tfTag;
    InputComboBox _cbInputTipo;
    Input _tfValorAcerto;
    Input _tfValorErro;
    Input _tfNumItems;
    public PanelCriarQuesito() {
        this.setLayout(new GridBagLayout());

        int i=0;

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

        this.add(new JLabel("Nome:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfTag = new Input(App.getConfiguracao(),"tagquesito","1.0",Input.TF_TEXT,70);
        this.add(_tfTag,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Tipo Quesito:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _cbInputTipo = new InputComboBox(App.getConfiguracao(),"tipoquesito",new String[] {ALTERNATIVAS,FALSO_VERDADEIRO,NUMERICO,SUBJETIVO},0,150);
        this.add(_cbInputTipo,new GridBagConstraints(1,i,2,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Valor acerto:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfValorAcerto = new Input(App.getConfiguracao(),"quesitovaloracerto","1.0",Input.TF_FLOAT,50);
        this.add(_tfValorAcerto,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Valor erro:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfValorErro = new Input(App.getConfiguracao(),"quesitovalorerro","0.0",Input.TF_FLOAT,50);
        this.add(_tfValorErro,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(new JLabel("Num Itens:"),new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        _tfNumItems = new Input(App.getConfiguracao(),"numitems","0",Input.TF_INTEIRO,50);
        this.add(_tfNumItems,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

        this.add(btnOk,new GridBagConstraints(0,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        this.add(btnCancel,new GridBagConstraints(1,i,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));
        i++;

    }


    public void set(String tag, int tipoQuesito, double valorAcerto, double valorErro, int numItens) {
        _tfTag.setText(tag);
        _cbInputTipo.setSelectedIndex(tipoQuesito);
        _tfValorAcerto.setText(""+valorAcerto);
        _tfValorErro.setText(""+valorErro);
        _tfNumItems.setText(""+numItens);
        _tfNumItems.setEnabled(false);
    }

    private boolean _ok;
    public void ok() {
        _ok = true;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void cancel() {
        _ok = false;
        this.getTopLevelAncestor().setVisible(false);
    }

    public void run(JFrame parent) {
        JDialog d = new JDialog(parent,"Quesito",true);
        linsoft.gui.util.Library.resizeAndCenterWindow(d,580,170);
        d.setContentPane(this);
        d.setVisible(true);
    }

    public boolean isOk() {
        return _ok;
    }

    public String getTag() {  return _tfTag.getText(); }
    public int getTipoQuesito() {  return _cbInputTipo.getSelectedIndex(); }
    public double getValorAcerto() {  return _tfValorAcerto.getFloat(); }
    public double getValorErro() {  return _tfValorErro.getFloat(); }
    public int getNumItens() {  return _tfNumItems.getInt(); }

    // listeners
    public interface Listener {
        public void change();
    }
    private void fireChange() {
        for (Listener l: _listeners) {
            l.change();
        }
    }
    private ArrayList<Listener> _listeners = new ArrayList<Listener>();
    public void addListener(Listener il) {
        _listeners.add(il);
    }
    public void removeListener(Listener il) {
        _listeners.remove(il);
    }
}
