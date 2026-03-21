package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import mixnfix.Model;
import mixnfix.modelo.Aluno;

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
public class ModelAluno extends Model {

    private Aluno _aluno;

    public ModelAluno(ModelInstituicao mi, Aluno a) {
        this.setParent(mi);
        _aluno = a;
    }

    public Aluno getAluno() {
        return _aluno;
    }

    public void setNome(String nome) throws SQLException {
        _aluno.setNome(nome);
        this.fireModelUpdate();
    }

    public void setMatricula(String matricula) throws SQLException {
        _aluno.setMatricula(matricula);
        this.fireModelUpdate();
    }

    public ModelInstituicao getModelInstituicao() {
        return (ModelInstituicao)this.getParent();
    }

    public ArrayList<ModelTurma> getTurmas() throws SQLException {
        return this.getModelInstituicao().getTurmas(this);
    }

    public int getNumeroTurmas() {
        int result = 0;
        try {
            ArrayList<ModelTurma> turmas = this.getTurmas();
            result = turmas.size();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumeroTurmas() != 0)
                throw new RuntimeException("Não é possível remover aluno " +
                                           this.getAluno().getNome() +
                                           ": aluno associado a uma ou mais turma");

            ModelInstituicao mi = (ModelInstituicao)this.getParent();
            for (ModelProva mp : mi.getProvas()) {
                for (ModelProvaCorrecao mpc : mp.getProvasCorrecoes()) {
                    if (mpc.getAluno(this) != null) {
                        throw new RuntimeException("Não é possível remover aluno " +
                            this.getAluno().getNome() +
                            ": aluno associado a uma ou mais correções");
                    }
                }

            }
        }

        ModelInstituicao mi = (ModelInstituicao)this.getParent();
        mi.removerAluno(this, cascade);

    }
}
