package mixnfix.gui;

import mixnfix.Model;

public interface MFTreeListener {
    public void select(Model m);
    public void menu(MFTree tree, Model m, int x, int y);
}
