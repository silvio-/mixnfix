package mixnfix.gui;



import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoProvaCorrecao;
import mixnfix.modelo.EntradaProvaCorrecao;
import mixnfix.modelo.ProvaCorrecao;

/**
 * CorrecaoProva
 */
public class ModelProvaCorrecao extends Model {
    private ProvaCorrecao _provaCorrecao;
    private ArrayList<ModelEntradaProvaCorrecao> _entradas = new ArrayList<ModelEntradaProvaCorrecao>();
    private ArrayList<ModelAlunoProvaCorrecao> _alunosProvaCorrecao = new ArrayList<ModelAlunoProvaCorrecao>();
    public ModelProvaCorrecao(ModelProva modelProva, ProvaCorrecao provaCorrecao) throws SQLException {
        this.setParent(modelProva);
        _provaCorrecao = provaCorrecao;

        /**
         * @todo these two should be loaded only when needed
         */
        for (AlunoProvaCorrecao apc : (Vector<AlunoProvaCorrecao>)App.getRepositorio().consultarAlunoProvaCorrecaoPorProvaCorrecao(_provaCorrecao)) {
            _alunosProvaCorrecao.add(new ModelAlunoProvaCorrecao(this,apc));
        }
        for (EntradaProvaCorrecao epc : (Vector<EntradaProvaCorrecao>) App.getRepositorio().consultarEntradaProvaCorrecaoporProvaCorrecao(_provaCorrecao)) {
            _entradas.add(new ModelEntradaProvaCorrecao(this,epc));
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
    public void renameProvaCorrecao(String newName) throws SQLException {
        _provaCorrecao.setNome(newName);
        this.fireModelUpdate();
    }

    /**
     * Add alunos to this.
     */
    public List<Model> addAlunos(List<Aluno> listAlunos) throws SQLException {
        List<AlunoProvaCorrecao> listAPC = App.getExtensaoRepositorio().inserirAlunosEmProvaCorrecao(_provaCorrecao,listAlunos);
        ArrayList<Model> listMAPC = new ArrayList<Model>(listAPC.size());
        int nBefore = this._alunosProvaCorrecao.size();
        for (AlunoProvaCorrecao apc: listAPC) {
            ModelAlunoProvaCorrecao mapc = new ModelAlunoProvaCorrecao(this,apc);
            _alunosProvaCorrecao.add(mapc);
            listMAPC.add(mapc);
        }
        this.fireNodesAdded(listMAPC,nBefore);
        return listMAPC;
    }

    /**
     * Add alunos to this.
     */
    public List<Model> addEntradasProvaCorrecao(List<EC> list) throws SQLException, IOException {
        EntradaProvaCorrecao[] listEPC = EC.save(list);
        ArrayList<Model> listMEPC = new ArrayList<Model>(listEPC.length);
        int nBefore = this._entradas.size();
        for (EntradaProvaCorrecao epc: listEPC) {
            ModelEntradaProvaCorrecao mepc = new ModelEntradaProvaCorrecao(this,epc);
            _entradas.add(mepc);
            listMEPC.add(mepc);
        }
        this.fireNodesAdded(listMEPC,nBefore);
        return listMEPC;
    }

    /**
     * Add alunos to this.
     */
    public Model addEntradaProvaCorrecao(EC ec) throws SQLException, IOException {
        ArrayList<EC> list = new ArrayList<EC>();
        list.add(ec);
        return this.addEntradasProvaCorrecao(list).get(0);
    }

    public ProvaCorrecao getProvaCorrecao() { return _provaCorrecao; }

    public mixnfix.modelo.Prova getProva() { return getProvaCorrecao().getProva_ProvaCorrecao(); }

    /**
     * Esta rotina já está otimizada (em termos de SQL).
     */
    public void addAlunos2(List<Aluno> alunos) throws SQLException {
        List<AlunoProvaCorrecao> l = App.getExtensaoRepositorio().inserirAlunosEmProvaCorrecao(_provaCorrecao, alunos);
        for (AlunoProvaCorrecao apc : l)
            _alunosProvaCorrecao.add(new ModelAlunoProvaCorrecao(this, apc));
    }

    public ArrayList<ModelAlunoProvaCorrecao> getAlunosProvaCorrecao() {
        return _alunosProvaCorrecao;
    }

    public ArrayList<ModelEntradaProvaCorrecao> getEntradasProvaCorrecao() {
        return _entradas;
    }

    public ArrayList<ModelEntradaProvaCorrecao> getEntradasProvaCorrecaoOrdenadas() {
        ArrayList<ModelEntradaProvaCorrecao> list = (ArrayList<ModelEntradaProvaCorrecao>) _entradas.clone();
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                ModelEntradaProvaCorrecao e1 = (ModelEntradaProvaCorrecao) o1;
                ModelEntradaProvaCorrecao e2 = (ModelEntradaProvaCorrecao) o2;
                return (e1.getAluno().getNome().compareTo(e2.getAluno().getNome()));
            }
        });
        return list;
    }

    public ArrayList<Aluno> getAlunos() {
        ArrayList<Aluno> result = new ArrayList<Aluno> ();
        for (ModelAlunoProvaCorrecao mapc : this._alunosProvaCorrecao) {
            result.add(mapc.getAluno());
        }
        return result;
    }

    public ArrayList<Aluno> getAlunosComEntradas() {
        ArrayList<Aluno> result = new ArrayList<Aluno> ();
        for (ModelEntradaProvaCorrecao mepc : this._entradas) {
            result.add(mepc.getAluno());
        }
        return result;
    }

    public ArrayList<Aluno> getAlunosSemEntradas() {
        ArrayList<Aluno> result = this.getAlunos();
        result.removeAll(this.getAlunosComEntradas());
        return result;
    }

    /**
     * Devolve alunos sem entradas
     */
    public ArrayList<ModelAlunoProvaCorrecao> getAlunosProvaCorrecaoSemEntradas() {
        ArrayList<ModelAlunoProvaCorrecao> result = new ArrayList<ModelAlunoProvaCorrecao>();
        ArrayList<ModelAlunoProvaCorrecao> list =  this.getAlunosProvaCorrecao();
        for (ModelAlunoProvaCorrecao a: list) {
            if (a.getNumeroDeEntradasProvaCorrecao() == 0)
                result.add(a);
        }
        return result;
    }

    public int getNumAlunos() {
        return this._alunosProvaCorrecao.size();
    }

    public int getNumEntradas() {
        return this._entradas.size();
    }

    public int getNumAlunosComEntrada() {
        HashSet set = new HashSet();
        for (ModelEntradaProvaCorrecao mepc: this._entradas) {
            set.add(mepc.getAluno().getId());
        }
        return set.size();
    }

    /**
     * Add alunos to this.
     */
    public void removeEntradasProvaCorrecao(List<ModelEntradaProvaCorrecao> list) throws SQLException, IOException {
        // criar entradas para remocao
        EntradaProvaCorrecao[] entradas = new EntradaProvaCorrecao[list.size()];
        int i = 0;
        for (Object object : list) {
            ModelEntradaProvaCorrecao modelEntrada = (ModelEntradaProvaCorrecao) object;
            entradas[i++] = modelEntrada.getEntradaProvaCorrecao();
        }

        EC.deleteEntradas(entradas);
        _entradas.removeAll(list);
        for (ModelEntradaProvaCorrecao m: list) {
            this.fireNodeRemoved(m);
        }
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumEntradas() != 0)
                throw new RuntimeException("Não é possível remover correção " +
                                     this.getProvaCorrecao().getNome() +
                                     ": existem entradas associadas a mesma");

            if (this.getNumAlunos() != 0)
                throw new RuntimeException("Não é possível remover correção " +
                                     this.getProvaCorrecao().getNome() +
                                     ": existem alunos associados a mesma");
        }
        ModelProva mp = (ModelProva) this.getParent();
        mp.removerProvaCorrecao(this,cascade);
    }

    /**
     * Add alunos to this.
     */
    public void removeAlunosProvaCorrecao(List<AlunoProvaCorrecao> list) throws SQLException, IOException {
        /**
         * @todo remover os alunos cadastrados na provacorrecao. Antes, porém, é preciso
         * remover as entradas (EntradaProvaCorrecao) relativas aos alunos que serão removidos.
         */
    }

    public void removerAlunoProvaCorrecao(ModelAlunoProvaCorrecao mapc, boolean cascade) throws SQLException, IOException {
        // nao remove se nao for uma prova da instituicao
        if (!_alunosProvaCorrecao.contains(mapc))
            throw new RuntimeException("Oooppps");

        // remover do BD
        App.getRepositorio().removerAlunoProvaCorrecao(mapc.getAlunoProvaCorrecao());

        // remover do modelo
        _alunosProvaCorrecao.remove(mapc);

        // sinaliza
        this.fireNodeRemoved(mapc);

    }

    public ModelAlunoProvaCorrecao getAluno(ModelAluno ma) {
        ModelAlunoProvaCorrecao result = null;
        for (ModelAlunoProvaCorrecao mapc: _alunosProvaCorrecao)
            if (mapc.getAluno().equals(ma.getAluno())) {
                result = mapc;
                break;
            }
        return result;
    }
}
