package mixnfix.folharesposta;

import java.util.ArrayList;
import java.util.List;

public class OptionField extends Field {
    private ArrayList<Cell> _cells = new ArrayList<Cell>();
    public OptionField(int id, String nome) {
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
    public void evaluateFromImageIntensity(double gap) {
        if (this.getNumCells() <= 1)
            throw new RuntimeException("OptionField must have at least two cells!");

        int indexMin = (_cells.get(0).getImageIntensity() <= _cells.get(1).getImageIntensity() ? 0 : 1);
        double min = Math.min(_cells.get(0).getImageIntensity(),_cells.get(1).getImageIntensity());
        double min2nd = Math.max(_cells.get(0).getImageIntensity(),_cells.get(1).getImageIntensity());
        for (int i=2;i<getNumCells();i++) {
            double x = getCell(i).getImageIntensity();
            if (x < min) {
                indexMin = i;
                min2nd = min;
                min = x;
            }
            else if (x < min2nd) {
                min2nd = x;
            }
        }

        for (int i=0;i<getNumCells();i++) {
            getCell(i).setSelected(false);
        }

        if (min2nd - min >= gap) {
            getCell(indexMin).setSelected(true);
        }
    }


    public static final int NOT_EVALUATED = -3;
    public static final int DOUBLE = -2;
    public static final int BLANK = -1;
    public int getValue() {
        int index = NOT_EVALUATED;
        for (int i=0;i<getNumCells();i++) {
            Cell c = getCell(i);
            if (c.isSelected()) {
               if (index == NOT_EVALUATED) {
                   index = i;
               }
               else {
                   index = DOUBLE;
               }
            }
        }
        if (index == NOT_EVALUATED)
            return BLANK;
        else
            return index;
    }


}
