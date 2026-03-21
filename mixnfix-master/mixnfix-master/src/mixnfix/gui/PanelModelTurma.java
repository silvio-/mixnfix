package mixnfix.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Atualizar Aluno.
 */
public class PanelModelTurma extends JPanel {

    private JTextField _tfNome = new JTextField();

    private AlunosList _listAlunosTurma = new AlunosList();
    private AlunosList _listAlunosOutros = new AlunosList();

    boolean _ok;

    private static boolean MODO_ATUALIZACAO = true;
    private static boolean MODO_INSERCAO = false;
    private boolean _modo;

    ModelTurma _modelTurma;
    ModelInstituicao _modelInstituicao;

    private int _state;
    public static final int ATUALIZANDO = 0;
    public static final int ATUALIZADO = 1;

    public static final int INSERINDO = 2;
    public static final int INSERIDO = 3;

    public static final int ENCERRADO = 4;

    public PanelModelTurma(ModelTurma mt) throws SQLException {
        _modelTurma = mt;
        _modelInstituicao = _modelTurma.getModelInstituicao();
        _modo = MODO_ATUALIZACAO;
        _state = ATUALIZANDO;
        buildUI();
        _tfNome.setText(mt.getTurma().getNome());

        ArrayList<ModelAluno> alunosTurma = mt.getAlunos();
        for (ModelAluno a: alunosTurma) {
            _listAlunosTurma.addAluno(a);
        }
        for (ModelAluno a: _modelInstituicao.getAlunos()) {
            if (!alunosTurma.contains(a))
                _listAlunosOutros.addAluno(a);
        }
    }

    public PanelModelTurma(ModelInstituicao mi) throws SQLException {
        _modelInstituicao = mi;
        _modo = MODO_INSERCAO;

        if (_modelInstituicao != null) {
            for (ModelAluno a : _modelInstituicao.getAlunos()) {
                _listAlunosOutros.addAluno(a);
            }
        }
        buildUI();
    }

    // ------------------------------------------------------------------------
    // Listener Support
    interface Listener {
        public void changeState(PanelModelTurma p, int oldState, int newState);
    }

    private ArrayList<PanelModelTurma.Listener> _listeners = new ArrayList<Listener>();

    public void addListener(PanelModelTurma.Listener l) {
        _listeners.add(l);
    }

    public void removeListener(PanelModelTurma.Listener l) {
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
        _modelTurma.setNome(_tfNome.getText());

        ArrayList<ModelAluno> as = _modelTurma.getAlunos();

        // remover turmas
        for (ModelAluno x: as) {
            if (!_listAlunosTurma.contains(x))
                _modelTurma.removerAluno(x,false);
        }

        // incluir turmas
        for (ModelAluno x: _listAlunosTurma.getAlunos()) {
            if (!as.contains(x))
                _modelTurma.matricularAluno(x);
        }

        // change state
        int oldState = _state;
        _state = ATUALIZADO;
        this.fireStateChange(oldState,_state);
    }

    public void inserir() throws SQLException {
        String nomeTurma = _tfNome.getText().trim();

        if ( nomeTurma == null || nomeTurma.equals("")) {
            JOptionPane.showMessageDialog(this, "O campo Nome está vazio", "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ModelTurma turma = _modelInstituicao.getTurmaByName(nomeTurma);
        Vector turmas = App.getRepositorio().consultarTurmaPorInstituicaoNome(nomeTurma, _modelInstituicao.getInstituicao());
        if (turmas.size() > 0) {
            JOptionPane.showMessageDialog(this, "Já existe turma com o nome "+nomeTurma, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ModelTurma t = _modelInstituicao.addNewTurma(_tfNome.getText());
        for (ModelAluno a : _listAlunosTurma.getAlunos()) {
            t.matricularAluno(a);
        }

        // change state
        int oldState = _state;
        _state = INSERIDO;
        this.fireStateChange(oldState, _state);
    }

    public void inserirOutro() {
        _tfNome.setText("");
        _listAlunosTurma.clear();
        _listAlunosOutros.clear();
        try {
            ArrayList<ModelAluno> alunos = _modelInstituicao.getAlunos();
            for (ModelAluno a : alunos) {
                _listAlunosOutros.addAluno(a);
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }

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
        _dialog = new JDialog(parent, _modo == MODO_ATUALIZACAO ? "Atualizar Turma" : "Inserir Turma", true);
        PanelModelTurma.Listener l = new PanelModelTurma.Listener() {
            public void changeState(PanelModelTurma p, int oldState, int newState) {
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
        linsoft.gui.util.Library.resizeAndCenterWindow(_dialog,450,500);
        _dialog.setVisible(true);
    }

    private void buildUI() {

        this.setLayout(new GridBagLayout());

        // add label nome
        this.add(new JLabel("Nome:"), new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        // add text field nome
        _tfNome.setPreferredSize(new Dimension(300,22));
        this.add(_tfNome, new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,2,2,2),0,0));

        //
        JScrollPane spAlunosTurma = new JScrollPane(_listAlunosTurma);
        spAlunosTurma.setBorder(new TitledBorder("Alunos na Turma"));
        this.add(spAlunosTurma, new GridBagConstraints(0,1,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(20,2,2,2),0,0));


        {
            // create buttons and their actions
            JButton btnUP = new JButton("cima");
            btnUP.setMargin(new Insets(2,2,2,2));
            btnUP.setPreferredSize(new Dimension(60,25));
            btnUP.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                   selecionar();
                }
            });

            JButton btnDOWN = new JButton("baixo");
            btnDOWN.setMargin(new Insets(2,2,2,2));
            btnDOWN.setPreferredSize(new Dimension(60,25));
            btnDOWN.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                  remover();
                }
            });

            // Middle Buttons Panel
            JPanel panelUpDown = new JPanel();
            panelUpDown.setLayout(new GridBagLayout());
            panelUpDown.add(btnUP,new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(2,2,2,10),0,0));
            panelUpDown.add(btnDOWN,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(2,10,2,2),0,0));
            this.add(panelUpDown, new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));


        }

        //
        JScrollPane spAlunosOutros = new JScrollPane(_listAlunosOutros);
        spAlunosOutros.setBorder(new TitledBorder("Outros Alunos"));
        this.add(spAlunosOutros, new GridBagConstraints(0,3,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(2,2,10,2),0,0));

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
            this.add(panelButtons, new GridBagConstraints(0,4,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
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
           panelButtons.add(btnOk    ,new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(12,2,10,10),0,0));
           panelButtons.add(btnCancel,new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.NONE,new Insets(12,10,10,2),0,0));
           this.add(panelButtons, new GridBagConstraints(0,4,2,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(0,0,0,0),0,0));
        }

    }

    public void selecionar() {
        move(_listAlunosOutros,_listAlunosTurma);
    }

    public void remover() {
        move(_listAlunosTurma,_listAlunosOutros);
    }

    private void move(JList fromList, JList toList) {
        DefaultListModel fromModel = (DefaultListModel) fromList.getModel();
        DefaultListModel toModel  = (DefaultListModel) toList.getModel();
        int[] indices = fromList.getSelectedIndices();
        int n = indices.length;
        for (int i=n-1;i>=0;i--) {
            int index = indices[i];
            Object o = fromModel.getElementAt(index);
            fromModel.removeElementAt(index);
            toModel.addElement(o);
        }
        int nn = toModel.size();
        toList.clearSelection();
        toList.addSelectionInterval(nn-n,nn-1);
        this.repaint();
    }

    public static void main(String[] args) throws Exception {
       JFrame d = new JFrame("Test");
       d.setContentPane(new PanelModelTurma((ModelInstituicao)null));
       d.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       linsoft.gui.util.Library.resizeAndCenterWindow(d, 420, 500);
       d.setVisible(true);
    }
}

class AlunosList extends JList {
    private javax.swing.DefaultListModel _listModel = new javax.swing.DefaultListModel();
    public AlunosList() {
        super();
        this.setModel(_listModel);
        this.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelAluno x = (ModelAluno) value;
                this.setIcon(Images.Aluno16x16);
                this.setText("("+x.getAluno().getMatricula()+") "+x.getAluno().getNome());
                return this;
            }
        });
    }

    public ArrayList<ModelAluno> getAlunos() {
        ArrayList<ModelAluno> list = new ArrayList<ModelAluno>();
        for (int i=0;i<_listModel.size();i++) {
            list.add((ModelAluno) _listModel.elementAt(i));
        }
        return list;
    }

    public void addAluno(ModelAluno a) {
        _listModel.addElement(a);
    }

    public void removeTurma(ModelAluno t) {
        _listModel.removeElement(t);
    }

    public boolean contains(ModelAluno t) {
        return  _listModel.contains(t);
    }

    public void clear() {
        _listModel.clear();
    }
}
