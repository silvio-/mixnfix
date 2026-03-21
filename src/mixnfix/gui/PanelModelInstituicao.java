package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import mixnfix.Model;
import mixnfix.ModelListener;

/**
 *
 */
public class PanelModelInstituicao extends JPanel {
    private ModelInstituicao _modelInstituicao;

    private JList _listAlunos;
    private JList _listProvas;
    private JList _listTurmas;
    private JList _listAlunosTurma;
    private DefaultListModel _listModelAlunos;
    private DefaultListModel _listModelProvas;
    private DefaultListModel _listModelTurmas;
    private DefaultListModel _listModelAlunosTurma;

    // private ProvaCorrecao
    public PanelModelInstituicao(ModelInstituicao modelInstituicao) throws SQLException {
        _modelInstituicao = modelInstituicao;

        // criar e inicializar list models
        _listModelAlunos = new DefaultListModel();
        _listModelProvas = new DefaultListModel();
        _listModelTurmas = new DefaultListModel();

        //
        ArrayList<ModelAluno> listAlunos = _modelInstituicao.getAlunos();
        Collections.sort(listAlunos,new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelAluno a = (ModelAluno) o1;
                ModelAluno b = (ModelAluno) o2;
                return a.getAluno().getNome().compareTo(b.getAluno().getNome());
            }
            public boolean equals(Object obj) {
                return false;
            }
        });

        ArrayList<ModelProva> listProvas = _modelInstituicao.getProvas();

        ArrayList<ModelTurma> listTurmas = _modelInstituicao.getTurmas();

        for (ModelAluno ma: listAlunos) _listModelAlunos.addElement(ma);
        for (ModelProva mp: listProvas) _listModelProvas.addElement(mp);
        for (ModelTurma mp: listTurmas) _listModelTurmas.addElement(mp);

        // criar lists
        _listAlunos = new JList(_listModelAlunos);
        _listProvas = new JList(_listModelProvas);
        _listTurmas = new JList(_listModelTurmas);

        // renderer
        _listAlunos.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelAluno x = (ModelAluno) value;
                this.setIcon(Images.Aluno16x16);
                this.setText("("+x.getAluno().getMatricula()+") "+x.getAluno().getNome());
                return this;
            }
        });
        _listAlunos.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    ModelAluno ma = (ModelAluno) _listAlunos.getSelectedValue();
                    if (ma != null) {
                        (new MFActionAtualizarAluno(ma)).actionPerformed(new ActionEvent(e.getSource(), 1, ""));
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });



        // renderer
        _listProvas.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelProva x = (ModelProva) value;
                this.setIcon(Images.Prova16x16);
                this.setText(x.getProva().getNome()+" ("+x.getProva().getIndice()+")");
                return this;
            }
        });
        _listProvas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    ModelProva mp = (ModelProva) _listProvas.getSelectedValue();
                    if (mp != null) {
                        MainFrame.MAIN_FRAME.select(mp);
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        // renderer
        _listTurmas.setCellRenderer(new DefaultListCellRenderer() {
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                ModelTurma x = (ModelTurma) value;
                this.setIcon(Images.Turma16x16);
                this.setText(x.getTurma().getNome());
                return this;
            }
        });
        _listTurmas.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    ModelTurma ma = (ModelTurma) _listTurmas.getSelectedValue();
                    if (ma != null) {
                        (new MFActionAtualizarTurma(ma)).actionPerformed(new ActionEvent(e.getSource(), 1, ""));
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });



        { // attatch model
            _modelInstituicao.addListener(new ModelListener() {
                public void update(Model model) {}
                public void nodeAdded(Model model, Model addedModel, int index) {
                    if (addedModel instanceof ModelProva) {
                        _listModelProvas.addElement(addedModel);
                    }
                    else if (addedModel instanceof ModelAluno) {
                        _listModelAlunos.addElement(addedModel);
                    }
                    else if (addedModel instanceof ModelTurma) {
                        _listModelTurmas.addElement(addedModel);
                    }
                }
                public void nodesAdded(Model model, java.util.List<Model> addedModel, int index) {
                    for (Model m: addedModel) {
                        if (addedModel instanceof ModelProva) {
                            _listModelProvas.addElement(m);
                        }
                        else if (addedModel instanceof ModelAluno) {
                            _listModelAlunos.addElement(m);
                        }
                        else if (addedModel instanceof ModelTurma) {
                            _listModelTurmas.addElement(m);
                        }
                    }
                }
                public void nodesLoaded(Model model, java.util.List<Model> addedModel) {
                    for (Model m: addedModel) {
                        if (addedModel instanceof ModelProva) {
                            _listModelProvas.addElement(m);
                        }
                        else if (addedModel instanceof ModelAluno) {
                            _listModelAlunos.addElement(m);
                        }
                        else if (addedModel instanceof ModelTurma) {
                            _listModelTurmas.addElement(m);
                        }
                    }
                }
                public void nodeRemoved(Model model, Model removedModel) {
                    if (removedModel instanceof ModelProva) {
                        _listModelProvas.removeElement(removedModel);
                    }
                    else if (removedModel instanceof ModelAluno) {
                        _listModelAlunos.removeElement(removedModel);
                    }
                    else if (removedModel instanceof ModelTurma) {
                        _listModelTurmas.removeElement(removedModel);
                    }
                }
            });
        } // attatch model


        // btn inserir aluno
        JButton btnInserirAluno = new JButton(new MFActionInserirAlunos(this._modelInstituicao));
        JButton btnInserirTurma = new JButton(new MFActionInserirTurmas(this._modelInstituicao));
        JButton btnRelatorioTXT = new JButton(new MFActionRelatorioNotasTXT(this._modelInstituicao));

        // panel alunos
        JPanel alunosPanel = new JPanel();
        alunosPanel.setLayout(new BorderLayout());
        alunosPanel.add(new TitledScrollPane(_listAlunos,"Alunos"),BorderLayout.CENTER);

        //
        JPanel provasPanel = new JPanel();
        provasPanel.setLayout(new BorderLayout());
        provasPanel.add(new TitledScrollPane(_listProvas,"Provas"),BorderLayout.CENTER);

        JPanel turmasPanel = new JPanel();
        turmasPanel.setLayout(new BorderLayout());
        turmasPanel.add(new TitledScrollPane(_listTurmas,"Turmas"),BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(provasPanel);
        splitPane.setRightComponent(alunosPanel);
        splitPane.setDividerLocation(200);

        JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane2.setLeftComponent(splitPane);
        splitPane2.setRightComponent(turmasPanel);
        splitPane2.setDividerLocation(600);

        // panel buttons
        JPanel panelButtons = new JPanel();
        panelButtons.add(btnInserirAluno);
        panelButtons.add(btnInserirTurma);
        panelButtons.add(btnRelatorioTXT);


        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(5,5,5,5));
        this.add(splitPane2,BorderLayout.CENTER);
        this.add(panelButtons,BorderLayout.SOUTH);
        this.add(new JLabel("Pasta: "+_modelInstituicao.getInstituicao().getNome()),BorderLayout.NORTH);
    }
}

class TitledScrollPane extends JScrollPane {
    public TitledScrollPane(Component c, String title) {
        super(c);
        this.setBorder(new TitledBorder(title));
    }
}
