package mixnfix.gui;

import java.util.ArrayList;

import mixnfix.Model;
import mixnfix.prova.Grupo;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.Quesito;

/**
 * ModelSelection
 */
public class ModelSelection {

    private ArrayList<Model> _selection;

    public ModelSelection(ArrayList<Model> selection) {
        _selection = selection;
    }

    public int size() {
        return _selection.size();
    }

    public boolean conainsOnlyQuesitos() {
        for (Model m: _selection) {
            if (!(m instanceof Quesito))
                return false;
        }
        return true;
    }

    public boolean conainsOnlyItemQuesito() {
        for (Model m: _selection) {
            if (!(m instanceof ItemQuesito))
                return false;
        }
        return true;
    }

    public ArrayList<Quesito> getQuesitos() {
        ArrayList<Quesito> result = new ArrayList<Quesito>();
        for (Model m: _selection) {
            if (m instanceof Quesito)
                result.add((Quesito)m);
        }
        return result;
    }

    public boolean containsNoParentChildRelationship() {
        for (int i=0;i<_selection.size();i++){
            Model mi = _selection.get(i);
            for (int j=i+1;j<_selection.size();j++){
                Model mj = _selection.get(j);
                if (mi.isAscendent(mj) || mi.isDescendent(mj))
                    return false;
            }
        }
        return true;
    }

    public ModelSelection getCopy() {
        ArrayList<Model> selectionCopy = new ArrayList<Model>();
        for (Model m: _selection) {
            if (m instanceof Grupo)
                selectionCopy.add(((Grupo) m).getCopy());
            else if (m instanceof Quesito)
                selectionCopy.add(((Quesito) m).getCopy());
            else if (m instanceof ItemQuesito)
                selectionCopy.add(((ItemQuesito) m).getCopy());
        }
        return new ModelSelection(selectionCopy);
    }
}
