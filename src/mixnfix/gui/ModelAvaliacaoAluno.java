package mixnfix.gui;

import mixnfix.modelo.AvaliacaoAluno;

/**
 * ModelAvaliacaoAluno
 */
public class ModelAvaliacaoAluno {
    private ModelAvaliacao _modelAvaliacao;
    private AvaliacaoAluno _avaliacaoAluno;
    public ModelAvaliacaoAluno(ModelAvaliacao modelAvaliacao, AvaliacaoAluno avaliacaoAluno) {
        _modelAvaliacao = modelAvaliacao;
        _avaliacaoAluno = avaliacaoAluno;
    }
    public AvaliacaoAluno getAvaliacaoAluno() {
        return _avaliacaoAluno;
    }
    public ModelAvaliacao getModelAvaliacao() {
        return _modelAvaliacao;
    }
}
