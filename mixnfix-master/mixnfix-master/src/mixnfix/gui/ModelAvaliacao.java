package mixnfix.gui;

import java.util.ArrayList;
import java.util.List;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.AvaliacaoAluno;

/**
 * ModelAvaliacao
 */
public class ModelAvaliacao {
    private ModelCursoInstanciaOld _modelCursoInstancia;
    private Avaliacao _avaliacao;
    private ArrayList<ModelAvaliacaoAluno> _avaliacoesAlunos = new ArrayList<ModelAvaliacaoAluno>();

    public ModelAvaliacao(ModelCursoInstanciaOld modelCursoInstancia, Avaliacao avaliacao) {
        _modelCursoInstancia = modelCursoInstancia;
        _avaliacao = avaliacao;
    }

    public ModelAvaliacaoAluno addAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno) {
        if (!avaliacaoAluno.getAvaliacao_AvaliacaoAluno().equals(_avaliacao))
            throw new RuntimeException("Não pode adicionar outra avaliacao");
        ModelAvaliacaoAluno modelAvaliacaoAluno = new ModelAvaliacaoAluno(this,avaliacaoAluno);
        _avaliacoesAlunos.add(modelAvaliacaoAluno);
        return modelAvaliacaoAluno;
    }

    public ModelCursoInstanciaOld getModelCursoInstancia() {
        return _modelCursoInstancia;
    }

    public Avaliacao getAvaliacao() {
        return _avaliacao;
    }

    public List<ModelAvaliacaoAluno> getAvaliacoesAlunos() {
        return (List<ModelAvaliacaoAluno>) _avaliacoesAlunos.clone();
    }

    public ModelAvaliacaoAluno find(Aluno aluno) {
        for (ModelAvaliacaoAluno mAluno : _avaliacoesAlunos) {
            if (aluno.equals(mAluno.getAvaliacaoAluno().getAlunoCursoInstancia_AvaliacaoAluno().getAluno_AlunoCursoInstancia()))
                return mAluno;
        }
        return null;
    }
}
