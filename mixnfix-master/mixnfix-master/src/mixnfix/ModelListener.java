package mixnfix;

import java.util.List;

public interface ModelListener {
    /**
     * Event telling that there was a update on the item.
     * This update is not a relationship update: not an addition
     * nor a deletion of the item.
     *
     * @param node the node that was updated
     */
    public void update(Model model);

    /**
     * Some child was added to the node.
     *
     * @param node the node where added node was added
     * @param addedNode the added node
     */
    public void nodeAdded(Model model, Model addedModel, int index);

    /**
     * Some child was added to the node.
     *
     * @param node the node where added node was added
     * @param addedNode the added node
     */
    public void nodesAdded(Model model, List<Model> addedModel, int index);

    /**
     * The childs of this node were loaded now.
     *
     * @param node the node where added node was added
     * @param addedNode the added node
     */
    public void nodesLoaded(Model model, List<Model> addedModel);

    /**
     * Some child was removed from the node.
     *
     * @param node the node where added node was added
     * @param removedNode the removed node
     */
    public void nodeRemoved(Model model, Model removedModel);

}
