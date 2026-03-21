package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Turma;

/**
 * Turma
 */
public class ModelTurma extends Model{

    private Turma _turma;

    private ArrayList<ModelAluno> _alunos;

    public ModelTurma(ModelInstituicao mi, Turma a) {
        this.setParent(mi);
        _turma = a;
    }

    public Turma getTurma() {
        return _turma;
    }

    public ModelInstituicao getModelInstituicao() {
        return (ModelInstituicao) this.getParent();
    }

    /**
     * Synchronize AlunoTurma with database.
     * @throws SQLException
     */
    private void syncAlunos(boolean force) throws SQLException {
        if (force || _alunos == null) {
            Vector v = App.getRepositorio().consultarAlunoTurmaPorTurma(_turma);
            _alunos = new ArrayList<ModelAluno>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                Aluno a = ((AlunoTurma) o).getAluno_AlunoTurma();
                ModelAluno ma = this.getModelInstituicao().getAlunoById(a.getId());
                if (ma == null)
                    throw new RuntimeException("OOOooooopppppsssss!");
                _alunos.add(ma);
                loadList.add(ma);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    public ArrayList<ModelAluno> getAlunos() throws SQLException {
        this.syncAlunos(false);
        return (ArrayList<ModelAluno>) _alunos.clone();
    }

    public boolean contains(ModelAluno a) throws SQLException {
        this.syncAlunos(false);
        return _alunos.contains(a);
    }

    public void setNome(String nome) throws SQLException {
        _turma.setNome(nome);
        this.fireModelUpdate();
    }

    public void matricularAluno(ModelAluno a) throws SQLException {
        if (this.contains(a))
            throw new RuntimeException("Aluno já matriculado");

        App.getRepositorio().inserirAlunoTurma(a.getAluno(),_turma);
        _alunos.add(a);
        this.fireNodeAdded(a,_alunos.size()-1);
    }

    public void matricularAlunos(ArrayList<ModelAluno> alunos) throws SQLException {

        for (ModelAluno aluno: alunos) {
            if (this.contains(aluno))
                throw new RuntimeException("Aluno já matriculado");

            App.getRepositorio().inserirAlunoTurma(aluno.getAluno(), _turma);
            _alunos.add(aluno);
            this.fireNodeAdded(aluno, _alunos.size() - 1);
        }
    }

    public void removerAluno(ModelAluno ma, boolean cascade) throws SQLException {
        this.syncAlunos(false);

        if (!this.contains(ma))
            throw new RuntimeException("Aluno não matriculado");

        // remover do BD
        App.getRepositorio().removerAlunoTurma(ma.getAluno().getId(),this.getTurma().getId());

        // remover do modelo
        _alunos.remove(ma);

        // sinalizar
        this.fireNodeRemoved(ma);
    }

    public int getNumeroAlunos() {
        return _alunos.size();
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (this.getNumeroAlunos() != 0)
            throw new RuntimeException("Não é possível remover turma " +
                                 this.getTurma().getNome() +
                                 ": existem alunos associados a mesma");
        ModelInstituicao mi = (ModelInstituicao) this.getParent();
        mi.removerTurma(this,cascade);
    }
}
