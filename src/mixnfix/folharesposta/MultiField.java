package mixnfix.folharesposta;

import java.util.ArrayList;
import java.util.List;

public class MultiField extends Field {
    private ArrayList<Field> _fields = new ArrayList<Field>();
    public MultiField(int id, String nome) {
        super(id,nome);
    }
    public void addField(Field f) {
        _fields.add(f);
    }
    public int getNumFields() {
        return _fields.size();
    }
    public List<Field> getFields() {
        return (List<Field>) _fields.clone();
    }
    public Field getField(int index) {
        return _fields.get(index);
    }
    public void getCells(List<Cell> list) {
        for (Field f: _fields)
            f.getCells(list);
    }
}
