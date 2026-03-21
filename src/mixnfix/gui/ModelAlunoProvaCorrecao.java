package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoProvaCorrecao;
import mixnfix.modelo.ProvaCorrecao;

/**
 * AlunoProvaCorrecao
 */
public class ModelAlunoProvaCorrecao extends Model {
    private ModelProvaCorrecao _modelProvaCorrecao;
    private AlunoProvaCorrecao _alunoProvaCorrecao;

    public ModelAlunoProvaCorrecao(ModelProvaCorrecao modelProvaCorrecao, AlunoProvaCorrecao alunoProvaCorrecao) {
        _modelProvaCorrecao = modelProvaCorrecao;
        _alunoProvaCorrecao = alunoProvaCorrecao;

        // data
        this.setParent(_modelProvaCorrecao);
    }

    public ModelProvaCorrecao getModelProvaCorrecao() {
        return _modelProvaCorrecao;
    }

    public AlunoProvaCorrecao getAlunoProvaCorrecao() {
        return _alunoProvaCorrecao;
    }

    public Aluno getAluno() {
        return _alunoProvaCorrecao.getAluno_AlunoProvaCorrecao();
    }

    public ProvaCorrecao getProvaCorrecao() {
        return _alunoProvaCorrecao.getProvaCorrecao_AlunoProvaCorrecao();
    }

    private ArrayList<ModelEntradaProvaCorrecao> _entradasProvaCorrecao;
    public List<ModelEntradaProvaCorrecao> getEntradasProvaCorrecao() {
        if (_entradasProvaCorrecao == null) {
            _entradasProvaCorrecao = new ArrayList<ModelEntradaProvaCorrecao>();
            for (ModelEntradaProvaCorrecao m: _modelProvaCorrecao.getEntradasProvaCorrecao())
                if (m.getAluno() != null && m.getAluno().getId() == _alunoProvaCorrecao.getAluno_AlunoProvaCorrecao().getId())
                    _entradasProvaCorrecao.add(m);
        }
        return _entradasProvaCorrecao;
    }

    public int getNumeroDeEntradasProvaCorrecao() {
        return getEntradasProvaCorrecao().size();
    }

    public void remover(boolean cascade) throws IOException, SQLException {
        if (!cascade) {
            if (this.getNumeroDeEntradasProvaCorrecao() != 0) {
                throw new RuntimeException("Não é possível remover aluno " +
                                     this.getAluno().getNome() + " da correção " +
                                     this.getModelProvaCorrecao().getProvaCorrecao().getNome() +
                                     ": existem correções para o mesmo");
            }

        }
        ModelProvaCorrecao mpc = (ModelProvaCorrecao) this.getParent();
        mpc.removerAlunoProvaCorrecao(this,cascade);
    }
}
