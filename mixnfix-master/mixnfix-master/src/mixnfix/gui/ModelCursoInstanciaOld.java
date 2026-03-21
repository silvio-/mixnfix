package mixnfix.gui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.Curso;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.Turma;
import mixnfix.reports.ModelCursoInstanciaReport;

/**
 * Model CursoInstancia
 */
public class ModelCursoInstanciaOld {
    private Curso _curso;
    private CursoInstancia _cursoInstancia;
    private ArrayList<AlunoCursoInstancia> _alunosMatriculados = new ArrayList<AlunoCursoInstancia>();
    private ArrayList<ModelAvaliacao> _avaliacoes = new ArrayList<ModelAvaliacao>();

    public ModelCursoInstanciaOld(Curso curso, CursoInstancia cursoInstancia) {
        _curso = curso;
        _cursoInstancia = cursoInstancia;
    }

    public ModelAvaliacao addAvaliacao(Avaliacao avaliacao) {
        if (!avaliacao.getCursoInstancia_Avaliacao().equals(_cursoInstancia))
            throw new RuntimeException("addAvaliacao(Avaliacao avaliacao)");
        ModelAvaliacao modelAvaliacao = new ModelAvaliacao(this,avaliacao);
        _avaliacoes.add(modelAvaliacao);
        return modelAvaliacao;
    }

    public void addAlunoMatriculado(AlunoCursoInstancia alunoCursoInstancia) {
        _alunosMatriculados.add(alunoCursoInstancia);
    }

    public Curso getCurso() {
        return _curso;
    }

    public CursoInstancia getCursoInstancia() {
        return _cursoInstancia;
    }

    public List<ModelAvaliacao> getAvaliacoes() {
        return (List<ModelAvaliacao>) _avaliacoes.clone();
    }

    public List<AlunoCursoInstancia> getAlunosMatriculados() {
        return (List<AlunoCursoInstancia>) _alunosMatriculados.clone();
    }

    public List<AlunoCursoInstancia> getAlunosMatriculadosOrdenadosPorNome() {
        List<AlunoCursoInstancia> result = (List<AlunoCursoInstancia>) _alunosMatriculados.clone();
        Collections.sort(result, new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }
            public int compare(Object o1, Object o2) {
                AlunoCursoInstancia aci1 = (AlunoCursoInstancia) o1;
                AlunoCursoInstancia aci2 = (AlunoCursoInstancia) o2;
                Aluno a1 = aci1.getAluno_AlunoCursoInstancia();
                Aluno a2 = aci2.getAluno_AlunoCursoInstancia();
                return (a1.getNome().compareTo(a2.getNome()));
                // return (a1.getMatricula().compareTo(a2.getMatricula()));
            }
        });
        return result;
    }

    public HashMap<Turma,ArrayList<AlunoCursoInstancia>> getAlunosMatriculadosOrdenadosPorNomePorTurma() throws Exception {
        List<AlunoCursoInstancia> alunos = (List<AlunoCursoInstancia>) _alunosMatriculados.clone();
        Collections.sort(alunos, new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }
            public int compare(Object o1, Object o2) {
                AlunoCursoInstancia aci1 = (AlunoCursoInstancia) o1;
                AlunoCursoInstancia aci2 = (AlunoCursoInstancia) o2;
                Aluno a1 = aci1.getAluno_AlunoCursoInstancia();
                Aluno a2 = aci2.getAluno_AlunoCursoInstancia();
                return (a1.getNome().compareTo(a2.getNome()));
                // return (a1.getMatricula().compareTo(a2.getMatricula()));
            }
        });

        Vector<AlunoTurma> alunosTurma = null;
        // assumindo que o aluno só vai estar em uma turma
        try {
            alunosTurma = App.getRepositorio().consultarAlunoTurma();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        HashMap<Turma,ArrayList<AlunoCursoInstancia>> result = new HashMap<Turma,ArrayList<AlunoCursoInstancia>>();
        for (AlunoCursoInstancia aluno: alunos) {
            Turma turma = null; //aluno.getAluno_AlunoCursoInstancia().getTurma();
            ArrayList<AlunoCursoInstancia> alunosDaTurma = (ArrayList<AlunoCursoInstancia>) result.get(turma);
            if (alunosDaTurma == null) {
                alunosDaTurma = new ArrayList<AlunoCursoInstancia>();
                result.put(turma,alunosDaTurma);
            }
            alunosDaTurma.add(aluno);
        }

        return result;
    }

    public List<AlunoCursoInstancia> getAlunosMatriculadosOrdenadosPorMatricula() {
        List<AlunoCursoInstancia> result = (List<AlunoCursoInstancia>) _alunosMatriculados.clone();
        Collections.sort(result, new Comparator() {
            public boolean equals(Object obj) {
                return false;
            }
            public int compare(Object o1, Object o2) {
                AlunoCursoInstancia aci1 = (AlunoCursoInstancia) o1;
                AlunoCursoInstancia aci2 = (AlunoCursoInstancia) o2;
                Aluno a1 = aci1.getAluno_AlunoCursoInstancia();
                Aluno a2 = aci2.getAluno_AlunoCursoInstancia();
                return (a1.getMatricula().compareTo(a2.getMatricula()));
            }
        });
        return result;
    }

    public void gerarRelatorio(String filename) throws Exception {
        ModelCursoInstanciaReport.gerarRelatorioPotTurma(this, filename);
    }
}
