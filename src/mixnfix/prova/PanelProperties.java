package mixnfix.prova;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import mixnfix.gui.Images;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PanelProperties extends JPanel {
    public PanelProperties(Properties properties) {

        Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.white, Color.white, new Color(103, 101, 98), new Color(148, 145, 140));
        TitledBorder titledBorder = new TitledBorder(border, "Propriedades");
        this.setBorder(titledBorder);
        this.setLayout(new BorderLayout());
        TMProperties tm = new TMProperties(properties);
        LocalTable table = new LocalTable();
        table.setModel(tm);
        this.add(new JScrollPane(table),BorderLayout.CENTER);
    }
}

class LocalTable extends JTable {
    public LocalTable() {
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

class TMProperties extends AbstractTableModel {
    JLabel _lbl = new JLabel();
    List<String> _names;
    Properties _properties;
    public TMProperties(Properties properties) {
        _names = new ArrayList(properties.keySet());
        _properties = properties;
    }
    public int getRowCount() {
        return _names.size();
    }
    public int getColumnCount() {
        return 2;
    }
    public String getColumnName(int column) {
        if (column == 0) {
            return "Propriedade";
        }
        else if (column == 1) {
            return "Valor";
        }
        return "";
    }
    public Object getValueAt(int row, int column) {
        String name = _names.get(row);
        String value = _properties.getProperty(name);
        _lbl.setForeground(Color.BLUE);
        if (column == 0) {
            _lbl.setIcon(Images.Prova16x16);
            _lbl.setText(name);
            _lbl.setHorizontalAlignment(JLabel.LEFT);
        }
        else if (column == 1) {
            _lbl.setIcon(null);
            _lbl.setText(value);
            _lbl.setHorizontalAlignment(JLabel.CENTER);
        }
        else {
            _lbl.setIcon(null);
            _lbl.setText("");
        }
        return _lbl;
    }
}
