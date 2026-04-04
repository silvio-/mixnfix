package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;


/**
 * PanelCursoInstancia
 */
public class PanelCursoInstancia extends JPanel {
    private ModelCursoInstanciaOld _modelCursoInstancia;
    public PanelCursoInstancia(ModelCursoInstanciaOld modelCursoInstancia) {
        _modelCursoInstancia = modelCursoInstancia;


        this.setLayout(new BorderLayout());

        JLabel lbl = new JLabel(""+_modelCursoInstancia.getCurso().getNome()+" "+_modelCursoInstancia.getCursoInstancia().getPeriodo_CursoInstancia().getId_periodo());
        lbl.setFont(new Font(lbl.getFont().getFontName(),Font.BOLD,24));
        lbl.setPreferredSize(new Dimension(100,30));
        lbl.setOpaque(true);
        lbl.setBackground(Color.lightGray);
        this.add(lbl,BorderLayout.NORTH);

        //
        TM tm = new TM(_modelCursoInstancia.getAlunosMatriculadosOrdenadosPorNome(),_modelCursoInstancia.getAvaliacoes());
        JTable table = new JTable(tm);
        this.add(new JScrollPane(table),BorderLayout.CENTER);

        //
        JButton btnAssociarCorrecoes = new JButton("Associar Correções");
        btnAssociarCorrecoes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                associarCorrecoes();
            }
        });
        JButton btnGerarRelatorio = new JButton("Gerar Relatório");
        btnGerarRelatorio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gerarRelatorio();
            }
        });

        JPanel btnsPanel = new JPanel();
        btnsPanel.setLayout(new FlowLayout());
        btnsPanel.add(btnAssociarCorrecoes);
        btnsPanel.add(btnGerarRelatorio);
        this.add(btnsPanel,BorderLayout.SOUTH);
    }

    private boolean _ok;
    private void associarCorrecoes() {
        List<ModelAvaliacao> avaliacoes = this._modelCursoInstancia.getAvaliacoes();
        JComboBox cb = new JComboBox(avaliacoes.toArray());
        cb.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                this.setText(((ModelAvaliacao) value).getAvaliacao().getNome());
                this.setIcon(Images.Prova16x16);
                return this;
            }
        });
        JDialog dialog = new JDialog((JFrame)this.getTopLevelAncestor(),"Escolha Avaliação",true);
        JPanel panel = new JPanel();
        _ok = false;
        JButton btn = new JButton("OK");
        panel.add(cb);
        panel.add(btn);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.getTopLevelAncestor().setVisible(false);
                _ok = true;
            }
        });
        dialog.setContentPane(panel);
        linsoft.gui.util.Library.resizeAndCenterWindow(dialog,400,300);
        dialog.setVisible(true);

        if (_ok) {
            /*
            JDialog d = new JDialog(MainFrame.MAIN_FRAME, "Associar Correções", true);
            try {
                d.setContentPane(new PanelAssociarCorrecoes((ModelAvaliacao)cb.getSelectedItem()));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            linsoft.gui.util.Library.resizeAndCenterWindow(d, 800, 400);
            d.setVisible(true);*/
        }
    }

    private void gerarRelatorio() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(App.getProperty("relatoriodir")));
        fc.setMultiSelectionEnabled(false);
        fc.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory())
                    return false;
                return f.getName().endsWith(".xls");
            }
            public String getDescription() {
                return "Excel (.xls)";
            }
        });

        int r = fc.showOpenDialog(MainFrame.MAIN_FRAME);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();

            App.getProperty("relatoriodir",fc.getSelectedFile().getAbsolutePath());

            try {
                System.out.println("Gerando relatório " + f.getAbsolutePath() + "...");
                _modelCursoInstancia.gerarRelatorio(f.getAbsolutePath());

                String command = App.getConfiguracao().getCommandOpenXLS(f.getAbsolutePath());
                mixnfix.Library.executeCommand(command, false);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    }
}

class TM extends AbstractTableModel {
    private List<AlunoCursoInstancia> _alunos;
    private List<ModelAvaliacao> _avaliacoes;
    public TM(List<AlunoCursoInstancia> alunos, List<ModelAvaliacao> avaliacoes) {
        _alunos = alunos;
        _avaliacoes = avaliacoes;
    }
    public String getColumnName(int column) {
        if (column > 0) {
            ModelAvaliacao modelAvaliacao = _avaliacoes.get(column - 1);
            return modelAvaliacao.getAvaliacao().getNome();
        }
        return "Aluno";
    }
    public Object getValueAt(int row, int col) {
        if (col == 0) {
            Aluno aluno = _alunos.get(row).getAluno_AlunoCursoInstancia();
            return aluno.getNome();
        }
        else {
            Aluno aluno = _alunos.get(row).getAluno_AlunoCursoInstancia();
            ModelAvaliacao modelAvaliacao = _avaliacoes.get(col - 1);
            ModelAvaliacaoAluno m =  modelAvaliacao.find(aluno);
            if (m == null)
                return "";
            else
                return String.format("%.2f",m.getAvaliacaoAluno().getNota());
        }
    }
    public int getRowCount() {
        return _alunos.size();
    }
    public int getColumnCount() {
        return _avaliacoes.size()+1;
    }
}
