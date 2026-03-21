package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import mixnfix.Model;
import mixnfix.modelo.ColetaQuestionario;
import mixnfix.modelo.EntradaColetaQuestionario;

/**
 * CorrecaoProva
 */
public class ModelColetaQuestionario extends Model {
    private ColetaQuestionario _coletaQuestionario;
    private ArrayList<ModelEntradaColetaQuestionario> _entradas = new ArrayList<ModelEntradaColetaQuestionario>();
    public ModelColetaQuestionario(ModelProva modelProva, ColetaQuestionario ColetaQuestionario) throws SQLException {
        this.setParent(modelProva);
        _coletaQuestionario = ColetaQuestionario;

        for (EntradaColetaQuestionario epc : (Vector<EntradaColetaQuestionario>) App.getRepositorio().consultarEntradaColetaQuestionarioPorColetaQuestionario(_coletaQuestionario)) {
            _entradas.add(new ModelEntradaColetaQuestionario(this,epc));
        }
    }

    /**
     * getModelProva(): Model Prova.
     */
    public ModelProva getModelProva() {
        return (ModelProva) this.getParent();
    }

    /**
     * Update the name of this node.
     * @param newName String
     * @throws SQLException
     */
    public void renameColetaQuestionario(String newName) throws SQLException {
        _coletaQuestionario.setNome(newName);
        this.fireModelUpdate();
    }

    /**
     * Add alunos to this.
     */
    public List<Model> addEntradasColetaQuestionario(List<EQ> list) throws SQLException, IOException {
        EntradaColetaQuestionario[] listEPC = EQ.save(list);
        ArrayList<Model> listMEPC = new ArrayList<Model>(listEPC.length);
        int nBefore = this._entradas.size();
        for (EntradaColetaQuestionario epc: listEPC) {
            ModelEntradaColetaQuestionario mepc = new ModelEntradaColetaQuestionario(this,epc);
            _entradas.add(mepc);
            listMEPC.add(mepc);
        }
        this.fireNodesAdded(listMEPC,nBefore);
        return listMEPC;
    }

    /**
     * Add alunos to this.
     */
    public Model addEntradaColetaQuestionario(EQ ec) throws SQLException, IOException {
        ArrayList<EQ> list = new ArrayList<EQ>();
        list.add(ec);
        return this.addEntradasColetaQuestionario(list).get(0);
    }

    public ColetaQuestionario getColetaQuestionario() { return _coletaQuestionario; }

    public mixnfix.modelo.Prova getProva() { return getColetaQuestionario().getProva_ColetaQuestionario(); }

    public ArrayList<ModelEntradaColetaQuestionario> getEntradasColetaQuestionario() {
        return _entradas;
    }

    public ArrayList<ModelEntradaColetaQuestionario> getEntradasColetaQuestionarioOrdenadas() {
        ArrayList<ModelEntradaColetaQuestionario> list = (ArrayList<ModelEntradaColetaQuestionario>) _entradas.clone();
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelEntradaColetaQuestionario e1 = (ModelEntradaColetaQuestionario) o1;
                ModelEntradaColetaQuestionario e2 = (ModelEntradaColetaQuestionario) o2;
                return e1.getEntradaColetaQuestionario().getId() - e2.getEntradaColetaQuestionario().getId();
            }
        });
        return list;
    }

    public int getNumEntradas() {
        return this._entradas.size();
    }

    /**
     * Add alunos to this.
     */
    public void removeEntradasColetaQuestionario(List<ModelEntradaColetaQuestionario> list) throws SQLException, IOException {
        // criar entradas para remocao
        EntradaColetaQuestionario[] entradas = new EntradaColetaQuestionario[list.size()];
        int i = 0;
        for (Object object : list) {
            ModelEntradaColetaQuestionario modelEntrada = (ModelEntradaColetaQuestionario) object;
            entradas[i++] = modelEntrada.getEntradaColetaQuestionario();
        }

        EQ.deleteEntradas(entradas);
        _entradas.removeAll(list);
        for (ModelEntradaColetaQuestionario m: list) {
            this.fireNodeRemoved(m);
        }
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumEntradas() != 0)
                throw new RuntimeException("Não é possível remover coleta " +
                                     this.getColetaQuestionario().getNome() +
                                     ": existem entradas associadas a mesma");
        }
        ModelProva mp = (ModelProva) this.getParent();
        mp.removerColetaQuestionario(this,cascade);
    }

}
