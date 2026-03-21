package mixnfix.folharesposta;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import mixnfix.gui.Images;

public class CellMapEditor extends JFrame {
    CellMapPanel _drawing;
    CellMap _cellMap;
    JTree _tree;

    public CellMapEditor(CellMap cellMap) {
        super("CellMap Editor");
        _cellMap = cellMap;
        _drawing = new CellMapPanel(_cellMap,210,297);
        _drawing.setBackground(Color.DARK_GRAY);
        _drawing.setOpaque(true);
        JScrollPane spd = new JScrollPane(_drawing);

        JPanel btnPanel = new JPanel();
        btnPanel.setPreferredSize(new Dimension(200,200));
        JButton btnSave = new JButton("Save");
        JButton btnLoad = new JButton("Load");
        JButton btnField = new JButton("Field");
        btnPanel.add(btnSave);
        btnPanel.add(btnLoad);
        btnPanel.add(btnField);

        _tree = new JTree(this.createFieldTreeModel());
        JScrollPane spt = new JScrollPane(_tree);

        JPanel toolsPanel = new JPanel();
        toolsPanel.setPreferredSize(new Dimension(200,100));
        toolsPanel.setLayout(new BorderLayout());
        toolsPanel.add(btnPanel,BorderLayout.NORTH);
        toolsPanel.add(spt,BorderLayout.CENTER);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(spd,BorderLayout.CENTER);
        this.getContentPane().add(toolsPanel,BorderLayout.EAST);

        _tree.setRootVisible(false);
        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // not a right click?
                if (e.getButton() != MouseEvent.BUTTON3)
                    return;
                //
                int x = e.getX();
                int y = e.getY();
                TreePath p = _tree.getSelectionPath();
                if (p == null)
                    return;

                FieldTreeNode node = (FieldTreeNode) p.getLastPathComponent();
                Object obj = node.getUserObject();

                switch (node.getType()) {
                    case FieldTreeNode.TYPE_Field: {
                        Field Field = (Field) obj;
                        JPopupMenu popup = new JPopupMenu();
                        //popup.add(new JMenuItem(new MFActionAddCampo(Field)));
                        popup.add(new JMenuItem("-----------"));
                        popup.show(_tree, x, y);
                        _tree.repaint();
                    }
                    break;
                    case FieldTreeNode.TYPE_CELL: {
                        Cell cell = (Cell) obj;
                        JPopupMenu popup = new JPopupMenu();
                        popup.add(new JMenuItem("-----------"));
                        popup.add(new JMenuItem("Remover célula de campo"));
                        // popup.add(new JMenuItem(new MFActionImportarAlunos(instituicao)));
                        popup.show(_tree, x, y);
                    }
                    break;

                }

            }
        });

        _tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath p = _tree.getSelectionPath();
                if (p == null)
                    return;


                FieldTreeNode node = (FieldTreeNode) p.getLastPathComponent();
                Object obj = node.getUserObject();
                switch (node.getType()) {
                    case FieldTreeNode.TYPE_Field: {
                        /*
                        Field Field = (Field) obj;
                        try {
                            if (_cellMap.getFieldsSelecionados().contains(Field))
                                _cellMap.removeFromFieldsSelecionados(Field);
                            else
                                _cellMap.addToFieldsSelecionados(Field);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        invalidate();
                        repaint();
                        */
                    }
                    break;
                    case FieldTreeNode.TYPE_CAMPO: {
                    }
                    break;
                    case FieldTreeNode.TYPE_CELL: {
                    }
                    break;
                }
            }
        });


        //-----------------------------------------------------
        // Renderizador
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            Icon tutorialIcon;
            public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                // user object
                Object obj = ( (DefaultMutableTreeNode) value).getUserObject();

                FieldTreeNode node = (FieldTreeNode) value;

                switch (node.getType()) {
                    case FieldTreeNode.TYPE_ROOT: {
                        this.setText("Gabarito");
                        this.setIcon(Images.Instituicao16x16);
                    }
                    break;
                    case FieldTreeNode.TYPE_Field: {
                        Field Field = (Field) obj;
                        this.setText(Field.getNome());
                        this.setIcon(Images.Folder16x16);
                    }
                    break;
                    case FieldTreeNode.TYPE_CELL: {
                        Cell cell = (Cell) obj;
                        this.setText(""+cell.getId());
                        this.setIcon(Images.Folder16x16);
                    }
                    break;
                }

                // System.out.println("Value class: "+value.getClass());
                return this;
            }
        };
        _tree.setCellRenderer(renderer);
        // Renderizador
        //-----------------------------------------------------

    }

    public TreeModel createFieldTreeModel() {
        FieldTreeNode rootNode = new FieldTreeNode("root", FieldTreeNode.TYPE_ROOT);
        TreeModel treeModel = new DefaultTreeModel(rootNode);
        for (Field m : _cellMap.getRootFields()) {
            FieldTreeNode mcNode = new FieldTreeNode(m, FieldTreeNode.TYPE_Field);
            rootNode.add(mcNode);
            /*
            for (Campo c : m.getCampos()) {
                FieldTreeNode cNode = new FieldTreeNode(c, FieldTreeNode.TYPE_CAMPO);
                mcNode.add(cNode);
                for (Cell cc : c.getCells()) {
                    FieldTreeNode ccNode = new FieldTreeNode(cc, FieldTreeNode.TYPE_CELL);
                    cNode.add(ccNode);
                }
            }*/
        }
        return treeModel;
    }

}

class FieldTreeNode extends DefaultMutableTreeNode {
    public static final byte TYPE_ROOT = (byte) - 1;
    public static final byte TYPE_Field = (byte) 0;
    public static final byte TYPE_CAMPO = (byte) 1;
    public static final byte TYPE_CELL = (byte) 2;
    private byte _type;
    public FieldTreeNode(Object obj, byte type) {
        super(obj);
        _type = type;
    }
    public int getType() {
        return _type;
    }
}

/*
class MFActionAddCampo
    extends AbstractAction {
    Field _Field;
    EditableComboBox _comboBox;
    public MFActionAddCampo(Field Field) {
        super("Adicionar Campo", Images.Curso16x16);
        _Field = Field;
    }

    public void actionPerformed(ActionEvent e) {
        // Campo campo = _Field.newCampo();
        //System.out.println("Adicionando um campo");
    }
}

class MFActionAddCelulas
    extends AbstractAction {
    Campo _campo;
    CellMap _cellMap;
    EditableComboBox _comboBox;
    public MFActionAddCelulas(Campo campo, CellMap cellMap) {
        super("Adicionar Células", Images.Curso16x16);
        _campo = campo;
        _cellMap = cellMap;
    }

    private boolean _ok;
    public void actionPerformed(ActionEvent e) {
        List<Cell> cells = _cellMap.getCells();

        JComboBox comboBox = new JComboBox();
        for (Cell cell : cells) {
            comboBox.addItem(cell);
        }
        comboBox.setPreferredSize(new Dimension(150, 20));

        comboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Cell cell = (Cell) value;
                this.setText(""+cell.getId());
                return this;
            }
        });


        _ok = false;
        JPanel panel = new JPanel(new GridBagLayout());
        JButton btn = new JButton("OK");
        panel.add(new JLabel("Células: "), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 10, 2), 0, 0));
        panel.add(comboBox, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.WEST, new Insets(2, 2, 8, 2), 0, 0));
        panel.add(btn, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JButton btn = (JButton) e.getSource();
                btn.getTopLevelAncestor().setVisible(false);
                _ok = true;
            }
        });

        while (true) {
            JDialog d = new JDialog(MainFrame.MAIN_FRAME, "Células", true);
            d.setContentPane(panel);
            linsoft.gui.util.Library.resizeAndCenterWindow(d, 250, 140);
            d.setVisible(true);

            if (!_ok)
                return;

            Cell cell = null;
            if (_comboBox.isNewValue()) {
                if ("".equals(_comboBox.getValue())) {
                    JOptionPane.showMessageDialog(d, "Célula não pode ser vazia!");
                    continue;
                }
                cell = (Cell)_comboBox.getValue();
            }
            else {
                cell = (Cell)_comboBox.getValue();
            }

            // inserir
            _campo.addCell(cell);
            break;
        }
    }

    public static void main(String[] args) throws Exception {
        Locale.setDefault(Locale.US);

        // CellMap m = getGabaritoCOVEST();
        // CellMap m = getFolhaCOVEST();

        CellMap m = CellMap.createCellMapExample();
        m.writeEPS("./c/img/x.eps");

        CellMapEditor f = new CellMapEditor(CellMap.createCellMapExample());
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setBounds(0,0,800,800);
        f.setVisible(true);

        m.writeNormalizedControlPoints("./c/x.txt");
    }


}*/
