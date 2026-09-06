package mixnfix.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import mixnfix.Model;
import mixnfix.ModelListener;

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
public class PanelProva  extends JPanel {
    private ModelProva _modelProva;
    private JTabbedPane _tabPane;

    /** exposes the tabbed pane ("Estrutura" + correction/collection tabs); used by headless test/screenshot tooling. */
    public JTabbedPane getTabPane() { return _tabPane; }
    private HashMap<Model,Component> _map;
    public PanelProva(ModelProva modelProva) throws SQLException {

        _modelProva = modelProva;

        this.setLayout(new BorderLayout());

        _tabPane = new JTabbedPane();

        _map = new HashMap<Model,Component>();

        Component c = _tabPane.add("Estrutura",new PanelModelProva(_modelProva));
        _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.Prova16x16);
        _map.put(_modelProva,c);

        ArrayList<ModelProvaCorrecao> cs = _modelProva.getProvasCorrecoes();
        for (ModelProvaCorrecao mpc: cs) {
            c = _tabPane.add(mpc.getProvaCorrecao().getNome(),new PanelProvaCorrecao(mpc));
            _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.Correcao16x16);
            _map.put(mpc,c);
        }

        ArrayList<ModelColetaQuestionario> qs = _modelProva.getColetasQuestionario();
        for (ModelColetaQuestionario mcq: qs) {
            c = _tabPane.add(mcq.getColetaQuestionario().getNome(),new PanelColetaQuestionario(mcq));
            _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.ColetaQuestionario);
            _map.put(mcq,c);
        }

        this.add(_tabPane,BorderLayout.CENTER);

        _modelProva.addListener(new ModelListener() {
            public void update(Model model){}
            public void nodeAdded(Model model, Model addedModel, int index){
                if (addedModel instanceof ModelProvaCorrecao) {
                    ModelProvaCorrecao mpc = (ModelProvaCorrecao) addedModel;
                    Component c = _tabPane.add(mpc.getProvaCorrecao().getNome(), new PanelProvaCorrecao(mpc));
                    _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.Correcao16x16);
                    _map.put(mpc, c);
                }
                else if (addedModel instanceof ModelColetaQuestionario) {
                    ModelColetaQuestionario mcq = (ModelColetaQuestionario) addedModel;
                    Component c = _tabPane.add(mcq.getColetaQuestionario().getNome(),new PanelColetaQuestionario(mcq));
                    _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.ColetaQuestionario);
                    _map.put(mcq,c);
                }
            }
            public void nodesAdded(Model model, List<Model> addedModels, int index){
                for (Model addedModel: addedModels) {
                    if (addedModel instanceof ModelProvaCorrecao) {
                        ModelProvaCorrecao mpc = (ModelProvaCorrecao) addedModel;
                        Component c = _tabPane.add(mpc.getProvaCorrecao().getNome(), new PanelProvaCorrecao(mpc));
                        _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.Correcao16x16);
                        _map.put(mpc, c);
                    }
                    else if (addedModel instanceof ModelColetaQuestionario) {
                        ModelColetaQuestionario mcq = (ModelColetaQuestionario) addedModel;
                        Component c = _tabPane.add(mcq.getColetaQuestionario().getNome(), new PanelColetaQuestionario(mcq));
                        _tabPane.setIconAt(_tabPane.indexOfComponent(c), Images.ColetaQuestionario);
                        _map.put(mcq, c);
                    }
                }
            }
            public void nodesLoaded(Model model, List<Model> addedModel){}
            public void nodeRemoved(Model model, Model removedModel){
                _tabPane.remove(_map.get(removedModel));
            }
        });
    }
}
