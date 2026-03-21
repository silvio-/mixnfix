package mixnfix.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import linsoft.gui.util.DialgoChooseObjects;

/**
 * Atualizar Aluno.
 */
public class PanelModelAluno extends JPanel {
    private JTextField _tfNome = new JTextField();
    private JTextField _tfMatricula = new JTextField();
    private TurmasTextField _tfTurmas = new TurmasTextField();
    boolean _ok;

    private static boolean MODO_ATUALIZACAO = true;
    private static boolean MODO_INSERCAO = false;
    private boolean _modo;

    ModelAluno _modelAluno;
    ModelInstituicao _modelInstituicao;

    private int _state;
    public static final int ATUALIZANDO = 0;
    public static final int ATUALIZADO = 1;

    public static final int INSERINDO = 2;
    public static final int INSERIDO = 3;

    public static final int ENCERRADO = 4;

    public PanelModelAluno(ModelAluno ma) throws SQLException {
        _modelAluno = ma;
        _modelInstituicao = _modelAluno.getModelInstituicao();
        _modo = MODO_ATUALIZACAO;
        _state = ATUALIZANDO;
        buildUI();
        _tfMatricula.setText(ma.getAluno().getMatricula());
        _tfNome.setText(ma.getAluno().getNome());
        for (ModelTurma t: ma.getTurmas()) {
            _tfTurmas.addTurma(t);
        }
    }

    public PanelModelAluno(ModelInstituicao mi) throws SQLException {
        _modelInstituicao = mi;
        _modo = MODO_INSERCAO;
        buildUI();
    }

    // ------------------------------------------------------------------------
    // Listener Support
    interface Listener {
        public void changeState(PanelModelAluno p, int oldState, int newState);
    }

    private ArrayList<PanelModelAluno.Listener> _listeners = new ArrayList<Listener>();

    public void addListener(PanelModelAluno.Listener l) {
        _listeners.add(l);
    }

    public void removeListener(PanelModelAluno.Listener l) {
        _listeners.remove(l);
    }

    private void fireStateChange(int oldState, int newState) {
        for (Listener l: _listeners) {
            l.changeState(this,oldState,newState);
        }
    }
    // Listener Support
    // ------------------------------------------------------------------------

    public void atualizar() throws SQLException {
        _modelAluno.setNome(_tfNome.getText());
        _modelAluno.setMatricula(_tfMatricula.getText());


        ArrayList<ModelTurma> ats = _modelAluno.getTurmas();
        ArrayList<ModelTurma> ts = _tfTurmas.getTurmas();

        // remover turmas
        for (ModelTurma x: ats) {
            if (!ts.contains(x))
                x.removerAluno(_modelAluno,false);
        }

        // incluir turmas
        for (ModelTurma x: ts) {
            if (!ats.contains(x))
                x.matricularAluno(_modelAluno);
        }

        _tfTurmas.repaint();

        // change state
        int oldState = _state;
        _state = ATUALIZADO;
        this.fireStateChange(oldState,_state);
    }

    public void inserir() throws SQLException {
        String nomeAluno = _tfNome.getText().trim();
        if ( nomeAluno == null || nomeAluno.equals("")) {
            JOptionPane.showMessageDialog(this, "O campo Nome está vazio", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String matricula = _tfMatricula.getText().trim();
        if ( matricula == null || matricula.equals("")) {
            JOptionPane.showMessageDialog(this, "O campo Matrícula está vazio", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ModelAluno aluno= _modelInstituicao.getAlunoByMatricula(matricula);
        if (aluno != null) {
            JOptionPane.showMessageDialog(this, "Já existe aluno com matrícula "+matricula + " (" + aluno.getAluno().getNome() + ")", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ModelAluno a = _modelInstituicao.addNewAluno(_tfMatricula.getText(),_tfNome.getText());
        for (ModelTurma t: _tfTurmas.getTurmas()) {
            t.matricularAluno(a);
        }

        // change state
        int oldState = _state;
        _state = INSERIDO;
        this.fireStateChange(oldState,_state);
    }

    public void inserirOutro() {
        _tfMatricula.setText("");
        _tfNome.setText("");
        _tfTurmas.clear();

        // change state
        int oldState = _state;
        _state = INSERINDO;
        this.fireStateChange(oldState,_state);
    }

    public void encerrar() {
        int oldState = _state;
        _state = ENCERRADO;
        this.fireStateChange(oldState,_state);
    }

    private JDialog _dialog;
    public void run(JFrame parent) {
        _dialog = new JDialog(parent, _modo == MODO_ATUALIZACAO ? "Atualizar Aluno" : "Inserir Aluno", true);
        PanelModelAluno.Listener l = new PanelModelAluno.Listener() {
            public void changeState(PanelModelAluno p, int oldState, int newState) {
                if (newState == ENCERRADO)
                    _dialog.setVisible(false);
                else if (newState == INSERIDO) {
                    int op = JOptionPane.showConfirmDialog(_dialog,"Aluno Inserido com Sucesso!\nDeseja inserir outro?","Inserir outro aluno?",JOptionPane.YES_NO_OPTION);
                    if (op == JOptionPane.OK_OPTION)
                        p.inserirOutro();
                    else
                        _dialog.setVisible(false);
                }
                else if (newState == ATUALIZADO)
                    _dialog.setVisible(false);
            }
        };
        this.addListener(l);
        _dialog.setContentPane(this);
        linsoft.gui.util.Library.resizeAndCenterWindow(_dialog,500,150);
        _dialog.setVisible(true);
    }

    private void buildUI() {

        this.setLayout(new GridBagLayout());

        // add label matricula
        this.add(new JLabel("Matrícula:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add text field matricula
        _tfMatricula.setPreferredSize(new Dimension(80,22));
        this.add(_tfMatricula, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add label nome
        this.add(new JLabel("Nome:"), new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add text field nome
        _tfNome.setPreferredSize(new Dimension(300,22));
        this.add(_tfNome, new GridBagConstraints(1,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add label turma
        this.add(new JLabel("Turma(s):"), new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add text field turma
        _tfTurmas.setPreferredSize(new Dimension(300,22));
        _tfTurmas.setEditable(false);
        this.add(_tfTurmas, new GridBagConstraints(1,2,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // btn turmas
        JButton btnTurmas = new JButton("...");
        btnTurmas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    selecionarTurmas();
                }
                catch (SQLException ex) {
                }
            }
        });
        this.add(btnTurmas, new GridBagConstraints(2,2,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // botoes para atualização
        if (_modo == MODO_ATUALIZACAO) {
            JButton btnOk = new JButton("Atualizar");
            btnOk.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        atualizar();
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // cancel
            JButton btnCancel = new JButton("Cancelar");
            btnCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    encerrar();
                }
            });
            JPanel panelButtons = new JPanel();
            panelButtons.setLayout(new GridBagLayout());
            panelButtons.add(btnOk    ,new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(12,2,10,10),0,0));
            panelButtons.add(btnCancel,new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(12,10,10,2),0,0));
            this.add(panelButtons, new GridBagConstraints(0,3,3,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        }

        // botoes para atualização
        else if (_modo == MODO_INSERCAO) {
            JButton btnOk = new JButton("Inserir");
           btnOk.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   try {
                       inserir();
                   }
                   catch (SQLException ex) {
                       ex.printStackTrace();
                   }
               }
           });

           // cancel
           JButton btnCancel = new JButton("Fechar");
           btnCancel.addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   encerrar();
               }
           });
           JPanel panelButtons = new JPanel();
           panelButtons.setLayout(new GridBagLayout());
           panelButtons.add(btnOk    ,new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(12,2,10,10),0,0));
           panelButtons.add(btnCancel,new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(12,10,10,2),0,0));
           this.add(panelButtons, new GridBagConstraints(0,3,3,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        }

    }

    private void selecionarTurmas() throws SQLException {
        // PanelChooseObjects
        ArrayList<ModelTurma> ats = _tfTurmas.getTurmas();
        ArrayList<ModelTurma> ts = _modelInstituicao.getTurmas();

        ts.removeAll(ats);

        DialgoChooseObjects d = new DialgoChooseObjects(MainFrame.MAIN_FRAME,
            "Turmas para o aluno: " + _tfNome.getText(), true, ats, ts, new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelTurma x = (ModelTurma) value;
                this.setIcon(Images.Turma16x16);
                this.setText(x.getTurma().getNome());
                return this;
            }
        });

        linsoft.gui.util.Library.resizeAndCenterWindow(d,400,270);
        d.setVisible(true);

        if (d.isOk()) {
            List list = d.getSelectedObjects();
            // remover turmas
            for (ModelTurma x: ats) {
                if (!list.contains(x))
                    _tfTurmas.removeTurma(x);
            }

            // incluir turmas
            for (ModelTurma x: (List<ModelTurma>)list) {
                if (!ats.contains(x))
                    _tfTurmas.addTurma(x);
            }
            _tfTurmas.repaint();
        }


    }

    public static void main(String[] args) throws Exception {
       JFrame d = new JFrame("Test");
       d.setContentPane(new PanelModelAluno((ModelInstituicao)null));
       d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       linsoft.gui.util.Library.resizeAndCenterWindow(d, 420, 470);
       d.setVisible(true);
    }
}


class TurmasTextField extends JTextField {
    private ArrayList<ModelTurma> _turmas = new ArrayList<ModelTurma>();
    public TurmasTextField() {
        super();
    }

    public ArrayList<ModelTurma> getTurmas() {
        return (ArrayList<ModelTurma>) _turmas.clone();
    }

    public void addTurma(ModelTurma t) {
        _turmas.add(t);
        this.setText(this.mountText());
    }

    public void removeTurma(ModelTurma t) {
        _turmas.remove(t);
        this.setText(this.mountText());
    }

    public String mountText() {
        String st = "";
        boolean first = true;
        for (ModelTurma t: _turmas) {
            if (!first)
                st += ", ";
            st += t.getTurma().getNome();
            first = false;
        }
        return st;
    }

    public void clear() {
        _turmas.clear();
    }

}
