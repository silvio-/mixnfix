package mixnfix;

import java.util.List;
import java.util.Vector;

/**
 * Any node of a tree in a conjugaçao.
 */
public abstract class Model {

    /**
     * Listeners
     */
    private Vector<ModelListener> _listeners;

    /**
     * Parent node
     */
    private Model _parent;

    /**
     * Constructor
     */
    public Model() {
        _parent = null;
        _listeners = new Vector(1);
    }

    /**
     * get parent
     */
    public Model getParent() {
        // System.out.println("getParent: "+this+" -> "+_parent );
        return _parent;
    }

    /**
     * set parent
     */
    public void setParent(Model parent) {
        _parent = parent;
        // System.out.println("setParent: "+this+" -> "+_parent );
    }

    /**
     * Add listener
     * Só pode haver listeners diferentes na lista...
     */
    public void addListener(ModelListener listener) {
        if (_listeners.indexOf(listener) == -1)
            _listeners.add(listener);
    }

    /**
     * Add listener
     */
    public void removeListener(ModelListener listener) {
        _listeners.remove(listener);
    }

    /**
     * Notify update
     */
    public void fireModelUpdate() {
        for (ModelListener ml: _listeners)
            ml.update(this);
    }

    /**
     * Notify addition
     */
    public void fireNodeAdded(Model addedModel, int index) {
        for (ModelListener ml: _listeners)
            ml.nodeAdded(this,addedModel,index);
    }

    /**
     * Notify addition
     */
    public void fireNodesAdded(List<Model> addededModels, int index) {
        for (ModelListener ml: _listeners)
            ml.nodesAdded(this,addededModels,index);
    }

    /**
     * Notify addition
     */
    public void fireNodesLoaded(List<Model> loadedModels) {
        for (ModelListener ml: _listeners)
            ml.nodesLoaded(this,loadedModels);
    }

    /**
     * Notify remove
     */
    public void fireNodeRemoved(Model  removedModel) {
        for (ModelListener ml: _listeners)
            ml.nodeRemoved(this,removedModel);
    }


    public Model getAscendentByClass(Class c) {
        Model p = this.getParent();

        // System.out.println("getAscendentByClass: (base) "+this);
        while (p != null && p.getClass() != c) {
            // System.out.println("getAscendentByClass: "+p+" = "+c+" ?");
            p = p.getParent();
            // System.out.println("getAscendentByClass: (parent) "+p);
        }

        if (p == null) {
            // System.out.println("getAscendentByClass: NULL");
        }

        return p;
    }

    public boolean isAscendent(Model m) {
        Model p = m.getParent();
        // System.out.println("getAscendentByClass: (base) "+this);
        while (p != null && p != this) {
            // System.out.println("getAscendentByClass: "+p+" = "+c+" ?");
            p = p.getParent();
            // System.out.println("getAscendentByClass: (parent) "+p);
        }
        return p == this;
    }

    public boolean isDescendent(Model m) {
        Model p = this.getParent();
        // System.out.println("getAscendentByClass: (base) "+this);
        while (p != null && p != m) {
            // System.out.println("getAscendentByClass: "+p+" = "+c+" ?");
            p = p.getParent();
            // System.out.println("getAscendentByClass: (parent) "+p);
        }
        return p == m;
    }

}
