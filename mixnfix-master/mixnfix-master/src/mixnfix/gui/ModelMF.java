package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mixnfix.Model;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.AvaliacaoAluno;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Prova;

/**
 * Os dados devem todos partir daqui
 */
public class ModelMF extends Model {

    public ModelMF() {
    }

    // as provas correcao desta Prova. Enquanto for null
    // é pq ainda não foi sincronizado com o DB
    private ArrayList<ModelInstituicao> _instituicoes;

    /**
     * Get a clone list of the provas correcoes.
     */
    public ArrayList<ModelInstituicao> getInstituicoes() throws SQLException {
        this.syncInstituicoes(false);
        return (ArrayList<ModelInstituicao>) _instituicoes.clone();
    }

    /**
     * Synchronize ProvasCorrecoes with database.
     * @throws SQLException
     */
    private void syncInstituicoes(boolean force) throws SQLException {
        if (force || _instituicoes == null) {
            Vector v = App.getRepositorio().consultarInstituicao();
            _instituicoes = new ArrayList<ModelInstituicao>(v.size());
            ArrayList<Model> loadList = new ArrayList<Model>(v.size());
            for (Object o: v) {
                ModelInstituicao mi = new ModelInstituicao(this,(Instituicao) o);
                _instituicoes.add(mi);
                loadList.add(mi);
            }
            this.fireNodesLoaded(loadList);
        }
    }

    /**
     * Add a Instituicao
     */
    public ModelInstituicao addInstituicao(String nomeInstituicao) throws SQLException {
        this.syncInstituicoes(false);
        Instituicao instituicao = App.getRepositorio().inserirInstituicao(nomeInstituicao);
        ModelInstituicao mi = new ModelInstituicao(this,instituicao);
        mi.setParent(this);
        _instituicoes.add(mi);
        this.fireNodeAdded(mi,_instituicoes.size()-2);
        return mi;
    }

    /**
     * Remover a Instituicao
     */
    public void removerInstituicao(ModelInstituicao mi, boolean cascade) throws SQLException, IOException {
        this.syncInstituicoes(false);

        // remover provas
        for (ModelProva mp: mi.getProvas())
            mp.remover(cascade);

        // remover alunos
        for (ModelAluno ma: mi.getAlunos())
            ma.remover(cascade);

        // remover turmas
        for (ModelTurma mt: mi.getTurmas())
            mt.remover(cascade);

        // remover do BD
        App.getRepositorio().removerInstituicao(mi.getInstituicao());

        // remover do modelo
        _instituicoes.remove(mi);

        // sinalizar
        this.fireNodeRemoved(mi);
    }

    public static ModelCursoInstanciaOld getModelCursoInstancia(CursoInstancia cursoInstancia) throws SQLException {

        // cria o ModelCursoInstancia
        ModelCursoInstanciaOld result = new ModelCursoInstanciaOld(cursoInstancia.getCurso_CursoInstancia(), cursoInstancia);

        // busca as avaliações desta instância de curso e adiciona ao model criado
        Vector<Avaliacao> avaliacoes = App.getRepositorio().consultarAvaliacaoPorCursoInstancia(cursoInstancia);
        for (Avaliacao avaliacao : avaliacoes) {
            // cria o ModelAvaliacao
            ModelAvaliacao modelAvaliacao = result.addAvaliacao(avaliacao);

            // busca as avaliacoes dos alunos matriculados nesta instância de curso e adiciona ao model criado
            Vector<AvaliacaoAluno> avaliacoesAlunos = App.getRepositorio().consultarAvaliacaoAlunoPorAvaliacao(avaliacao);
            for (AvaliacaoAluno avaliacaoAluno: avaliacoesAlunos) {
                modelAvaliacao.addAvaliacaoAluno(avaliacaoAluno);
            }
        }

        // busca os alunos matriculados nesta instância de curso e adiciona ao model criado
        Vector<AlunoCursoInstancia> alunosCursoInstancia = App.getRepositorio().consultarAlunoCursoInstanciaPorCursoInstancia(cursoInstancia);
        for (AlunoCursoInstancia alunoCursoInstancia: alunosCursoInstancia) {
            result.addAlunoMatriculado(alunoCursoInstancia);
        }

        return result;
    }


    public static ModelProva getModelProva(Prova prova) throws SQLException {
        // cria o ModelProva
        ModelProva result = new ModelProva(null,prova);
        return result;
    }

    public void adjustImageFileNames() throws Exception {
        List<ModelInstituicao> is = this.getInstituicoes();
        for (ModelInstituicao i: is) {
            System.out.println("Instituiao: "+i.getInstituicao().getNome());
            i.adjustImageFileNames();
        }
    }

    public ModelInstituicao getIntituicaoByName(String name) throws SQLException {
        this.syncInstituicoes(false);
        for (ModelInstituicao mi: _instituicoes) {
            if (mi.getInstituicao().getNome().equals(name))
                return mi;
        }
        return null;
    }



}
