package mixnfix.folharesposta;

import java.util.ArrayList;
import java.util.List;

public class BinaryField extends Field {
    private ArrayList<Cell> _cells = new ArrayList<Cell>();
    public BinaryField(int id, String nome) {
        super(id,nome);
    }
    public void addCell(Cell c) {
        _cells.add(c);
    }
    public int getNumCells() {
        return _cells.size();
    }
    public List<Cell> getCells() {
        return (List<Cell>) _cells.clone();
    }
    public Cell getCell(int index) {
        return _cells.get(index);
    }
    public void getCells(List<Cell> list) {
        list.addAll(_cells);
    }

    public void getValue(boolean bits[]) {
        if (this.getNumCells() <= 0 || bits.length != this.getNumCells())
            throw new RuntimeException("OptionField must have at least one cell and bits must have the same size!");

        // find separation level
        for (int i = 0; i < bits.length; i++) {
            bits[i] = getCell(i).isSelected();
        }
    }

    public void setValue(boolean bits[]) {
        if (this.getNumCells() <= 0 || bits.length != this.getNumCells())
            throw new RuntimeException("OptionField must have at least one cell and bits must have the same size!");

        // find separation level
        for (int i = 0; i < bits.length; i++) {
            getCell(i).setSelected(bits[i]);
        }
    }

    public void evaluateFromImageIntensity(float separationLevel) {
        for (Cell c: _cells) {
            if (c.getImageIntensity() < separationLevel)
                c.setSelected(true);
            else
                c.setSelected(false);
        }
    }
}
