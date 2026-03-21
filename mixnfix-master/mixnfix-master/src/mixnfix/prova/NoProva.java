package mixnfix.prova;


/**
 * <p>Title: NoProva</p>
 *
 * <p>Description: Lê-se Nó Prova.</p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class NoProva extends No {
    public NoProvaPermutavel getParentAsNoProvaPermutavel() {
        return (NoProvaPermutavel)super.getParent();
    }

    public void moverPraFrente() {
        Object o = this.getParent();
        if (o == null || !(o instanceof NoProvaPermutavel))
            return;
        NoProvaPermutavel npp = (NoProvaPermutavel) o;
        npp.move(this,+1);
    }

    public void moverPraTras() {
        Object o = this.getParent();
        if (o == null || !(o instanceof NoProvaPermutavel))
            return;
        NoProvaPermutavel npp = (NoProvaPermutavel) o;
        npp.move(this,-1);
    }

    public void fireRemoveStructureSimulation() {}
    public void fireInitStructure() {}

}
