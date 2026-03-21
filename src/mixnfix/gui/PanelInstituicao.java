package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Prova;

/**
 * PanelInstituicao
 */
public class PanelInstituicao extends JPanel {
    private Instituicao _instituicao;
    JTabbedPane _tabbedPane = new JTabbedPane();

    private PanelInstituicaoCursosEProvas _panelInstituicaoCursosEProvas;
    public PanelInstituicaoCursosEProvas getPanelInstituicaoCursosEProvas() throws SQLException {
        if (_panelInstituicaoCursosEProvas == null) {
            _panelInstituicaoCursosEProvas = new PanelInstituicaoCursosEProvas(_instituicao);
        }
        return _panelInstituicaoCursosEProvas;
    }

    public PanelInstituicao(Instituicao instituicao) throws SQLException {
        _instituicao = instituicao;
        _tabbedPane.setPreferredSize(new Dimension(100,24));
        _tabbedPane.addTab("Principal",new JLabel());
        _tabbedPane.addTab("Turmas",new JLabel());
        _tabbedPane.addTab("Alunos",new JLabel());
        _tabbedPane.addTab("Cursos",new JLabel());
        _tabbedPane.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                removeAll();
                add(_tabbedPane,BorderLayout.NORTH);
                if (_tabbedPane.getSelectedIndex() == 0) {
                    try {
                        add(getPanelInstituicaoCursosEProvas(), BorderLayout.CENTER);
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                repaint();
            }
        });

        this.setLayout(new BorderLayout());
        this.add(_tabbedPane,BorderLayout.NORTH);

        _tabbedPane.setSelectedIndex(1);
        _tabbedPane.setSelectedIndex(0);
    }
}

class PanelInstituicaoCursosEProvas extends JPanel {
    private Instituicao _instituica;
    MFTable _tbCursos = new MFTable();
    MFTable _tbProvas = new MFTable();
    TMProva _modelProvas;
    TMCursoInstancia _modelCursos;
    public PanelInstituicaoCursosEProvas(Instituicao instituicao) throws SQLException {
        this.setLayout(new BorderLayout());
        JSplitPane p = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        { // cursos
            JScrollPane spCursos = new JScrollPane(_tbCursos);
            Vector v = App.getRepositorio().consultarCursoInstanciaPorInstituicao(instituicao);
            _modelCursos = new TMCursoInstancia(v);
            _tbCursos.setModel(_modelCursos);
            spCursos = new JScrollPane(_tbCursos);
            spCursos.setPreferredSize(new Dimension(100, 300));
            p.add(spCursos);
        }

        { // provas
            Vector v = App.getRepositorio().consultarProvaPorInstituicao(instituicao);
            _modelProvas = new TMProva(v);
            _tbProvas.setModel(_modelProvas);
            p.add(new JScrollPane(_tbProvas));
        }

        p.setDividerLocation(200);

        this.add(p,BorderLayout.CENTER);

    }
}

class MFTable extends JTable {
    public MFTable() {
        super();
        this.setShowGrid(false);
    }
    private TableCellRenderer _renderer = new TableCellRenderer() {
        public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
            JLabel lbl = (JLabel) value;
            lbl.setOpaque(true);
            if (isSelected) {
                lbl.setBackground(table.getSelectionBackground());
                lbl.setForeground(table.getSelectionForeground());
            }
            else {
                lbl.setBackground(table.getBackground());
                lbl.setForeground(table.getForeground());
            }
            return lbl;
        }
    };
    public TableCellRenderer getCellRenderer( int row, int col ) {
        return _renderer;
    }
}

class TMProva extends AbstractTableModel {
    JLabel _lbl = new JLabel();
    private ArrayList<Prova> _provas;
    public TMProva(List provas) {
        _provas = new ArrayList<Prova>(provas);
    }
    public int getRowCount() {
        return _provas.size();
    }
    public int getColumnCount() {
        return 2;
    }
    public String getColumnName(int column) {
        if (column == 0) {
            return "Prova";
        }
        else if (column == 1) {
            return "Indice";
        }
        return "";
    }
    public Object getValueAt(int row, int column) {
        Prova prova = (Prova) _provas.get(row);
        _lbl.setForeground(Color.BLUE);
        if (column == 0) {
            _lbl.setIcon(Images.Prova16x16);
            _lbl.setText(prova.getNome());
            _lbl.setHorizontalAlignment(JLabel.LEFT);
        }
        else if (column == 1) {
            _lbl.setIcon(null);
            _lbl.setText(""+prova.getIndice());
            _lbl.setHorizontalAlignment(JLabel.CENTER);
        }
        else {
            _lbl.setIcon(null);
            _lbl.setText("");
        }
        return _lbl;
    }
}

class TMCursoInstancia extends AbstractTableModel {
    JLabel _lbl = new JLabel();
    private ArrayList<CursoInstancia> _cursosInstancias;
    public TMCursoInstancia(List cursosInstancias) {
        _cursosInstancias = new ArrayList<CursoInstancia>(cursosInstancias);
    }
    public int getRowCount() {
        return _cursosInstancias.size();
    }
    public int getColumnCount() {
        return 2;
    }
    public String getColumnName(int column) {
        if (column == 0) {
            return "Curso";
        }
        else if (column == 1) {
            return "Periodo";
        }
        return "";
    }
    public Object getValueAt(int row, int column) {
        CursoInstancia cursoInstancia = _cursosInstancias.get(row);
        _lbl.setForeground(Color.DARK_GRAY);
        if (column == 0) {
            _lbl.setIcon(Images.Curso16x16);
            _lbl.setText(cursoInstancia.getCurso_CursoInstancia().getNome());
            _lbl.setHorizontalAlignment(JLabel.LEFT);
        }
        else if (column == 1) {
            _lbl.setIcon(null);
            _lbl.setText(""+cursoInstancia.getPeriodo_CursoInstancia().getId_periodo());
            _lbl.setHorizontalAlignment(JLabel.CENTER);
        }
        else {
            _lbl.setIcon(null);
            _lbl.setText("");
        }
        return _lbl;
    }
}


