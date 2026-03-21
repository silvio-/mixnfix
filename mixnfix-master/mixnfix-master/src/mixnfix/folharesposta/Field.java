package mixnfix.folharesposta;


import java.util.List;

public abstract class Field {
    private int _id;
    private String _nome;
    private MultiField _parent;
    public Field(int id, String nome) {
        _parent = null;
        _id = id;
        _nome = nome;
    }
    public void setParent(MultiField parent) { _parent = parent; }
    public MultiField getParent() { return _parent; }
    public String getNome() { return _nome; }
    public int getId() { return _id; }
    public abstract void getCells(List<Cell> list);

    // public abstract void evaluateFromImageIntensities();
}

