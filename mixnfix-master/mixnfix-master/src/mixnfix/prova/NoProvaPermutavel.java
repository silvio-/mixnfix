package mixnfix.prova;

import java.util.ArrayList;

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
public abstract class NoProvaPermutavel extends NoProva {

    private ArrayList<NoProva> _permutacao = new ArrayList<NoProva>();

    public void resetPermutation() {
        _permutacao.clear();
    }

    public void addItemToPermutacao(NoProva n) {
        _permutacao.add(n);
    }

    public int indexOnPermutacao(NoProva n) {
        return _permutacao.indexOf(n);
    }

    public int index(NoProva n) {
        return _childs.indexOf(n);
    }

    public ArrayList<NoProva> getPermutacao() {
        return _permutacao;
    }

    public NoProva getItemOnPermutacao(int i) {
        return _permutacao.get(i);
    }

    public boolean hasPermutacao() {
        return (_permutacao != null);
    }

    private ArrayList<NoProva> _childs = new ArrayList<NoProva>();
    public ArrayList<NoProva> getChilds() {
        return (ArrayList<NoProva>) _childs.clone();
    }

    public ArrayList<NoProva> getChildsNoClone() {
        return (ArrayList<NoProva>) _childs;
    }

    public int getChildCount() {
        return _childs.size();
    }

    public NoProva getChild(int index) {
        return _childs.get(index);
    }

    public void addChild(NoProva n) {
        n.setParent(this);
        _childs.add(n);
        this.fireNodeAdded(n, _childs.size() - 1);
    }

    public void removeChild(No n) {
        this._childs.remove(n);
        this.fireNodeRemoved(n);
    }


    public static final String TRAVADO = "travado";

    public boolean isTravado() {
        boolean result = false;
        String value = this.getProperty(TRAVADO);
        if (value != null && "1".equals(value))
            result = true;
        return result;
    }

    public void setTravado(boolean t) {
        this.setProperty(TRAVADO,(t ? "1": "0"));
        this.fireModelUpdate();
    }

    public void move(NoProva n, int delta) {
        int index = _childs.indexOf(n);

        // not a valid movement (do nothing!)
        if (index < 0) return;
        else if (delta == 0) return;
        else if (index + delta < 0) return;
        else if (index + delta > this.getChildCount()-1) return;

        // simulate removal
        n.fireRemoveStructureSimulation();

        _childs.remove(index);
        _childs.add(index + delta, n);

        //
        this.fireNodeAdded(n, index + delta);
        n.fireInitStructure();

    }
}
