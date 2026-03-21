package mixnfix.modelo.persistencia;

import java.sql.SQLException;
import java.util.Vector;

import linsoft.rbtree2.RBTree;
import linsoft.rbtree2.RBTreeIterator;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.AlunoCursoInstanciaKey;
import mixnfix.modelo.AlunoKey;
import mixnfix.modelo.AlunoProvaCorrecao;
import mixnfix.modelo.AlunoProvaCorrecaoKey;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.AlunoTurmaKey;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.AvaliacaoAluno;
import mixnfix.modelo.AvaliacaoAlunoKey;
import mixnfix.modelo.AvaliacaoKey;
import mixnfix.modelo.ColetaQuestionario;
import mixnfix.modelo.ColetaQuestionarioKey;
import mixnfix.modelo.Correcao;
import mixnfix.modelo.CorrecaoKey;
import mixnfix.modelo.Curso;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.CursoInstanciaKey;
import mixnfix.modelo.CursoKey;
import mixnfix.modelo.EntradaColetaQuestionario;
import mixnfix.modelo.EntradaColetaQuestionarioKey;
import mixnfix.modelo.EntradaProvaCorrecao;
import mixnfix.modelo.EntradaProvaCorrecaoKey;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.InstituicaoKey;
import mixnfix.modelo.Periodo;
import mixnfix.modelo.PeriodoKey;
import mixnfix.modelo.Prova;
import mixnfix.modelo.ProvaCorrecao;
import mixnfix.modelo.ProvaCorrecaoKey;
import mixnfix.modelo.ProvaKey;
import mixnfix.modelo.Repositorio;
import mixnfix.modelo.Turma;
import mixnfix.modelo.TurmaKey;

public class RepositorioCache implements Repositorio {
	private RBTree _treeInstituicao;
	private RBTree _treePeriodo;
	private RBTree _treeTurma;
	private RBTree _treeAluno;
	private RBTree _treeCurso;
	private RBTree _treeAlunoTurma;
	private RBTree _treeCursoInstancia;
	private RBTree _treeAlunoCursoInstancia;
	private RBTree _treeAvaliacao;
	private RBTree _treeAvaliacaoAluno;
	private RBTree _treeProva;
	private RBTree _treeCorrecao;
	private RBTree _treeProvaCorrecao;
	private RBTree _treeAlunoProvaCorrecao;
	private RBTree _treeEntradaProvaCorrecao;
	private RBTree _treeColetaQuestionario;
	private RBTree _treeEntradaColetaQuestionario;
	private CacheTurma _cacheTurma;
	private CacheAluno _cacheAluno;
	private CacheCurso _cacheCurso;
	private CacheAlunoTurma _cacheAlunoTurma;
	private CacheCursoInstancia _cacheCursoInstancia;
	private CacheAlunoCursoInstancia _cacheAlunoCursoInstancia;
	private CacheAvaliacao _cacheAvaliacao;
	private CacheAvaliacaoAluno _cacheAvaliacaoAluno;
	private CacheProva _cacheProva;
	private CacheCorrecao _cacheCorrecao;
	private CacheProvaCorrecao _cacheProvaCorrecao;
	private CacheAlunoProvaCorrecao _cacheAlunoProvaCorrecao;
	private CacheEntradaProvaCorrecao _cacheEntradaProvaCorrecao;
	private CacheColetaQuestionario _cacheColetaQuestionario;
	private CacheEntradaColetaQuestionario _cacheEntradaColetaQuestionario;
	private boolean _allInstituicao;
	private boolean _allPeriodo;
	private boolean _allTurma;
	private boolean _allAluno;
	private boolean _allCurso;
	private boolean _allAlunoTurma;
	private boolean _allCursoInstancia;
	private boolean _allAlunoCursoInstancia;
	private boolean _allAvaliacao;
	private boolean _allAvaliacaoAluno;
	private boolean _allProva;
	private boolean _allCorrecao;
	private boolean _allProvaCorrecao;
	private boolean _allAlunoProvaCorrecao;
	private boolean _allEntradaProvaCorrecao;
	private boolean _allColetaQuestionario;
	private boolean _allEntradaColetaQuestionario;

	private Repositorio _repositorio;
	private boolean _localMode;

	public RepositorioCache() {
		_treeInstituicao = new RBTree();
		_treePeriodo = new RBTree();
		_treeTurma = new RBTree();
		_treeAluno = new RBTree();
		_treeCurso = new RBTree();
		_treeAlunoTurma = new RBTree();
		_treeCursoInstancia = new RBTree();
		_treeAlunoCursoInstancia = new RBTree();
		_treeAvaliacao = new RBTree();
		_treeAvaliacaoAluno = new RBTree();
		_treeProva = new RBTree();
		_treeCorrecao = new RBTree();
		_treeProvaCorrecao = new RBTree();
		_treeAlunoProvaCorrecao = new RBTree();
		_treeEntradaProvaCorrecao = new RBTree();
		_treeColetaQuestionario = new RBTree();
		_treeEntradaColetaQuestionario = new RBTree();
		_cacheTurma = new CacheTurma();
		_cacheAluno = new CacheAluno();
		_cacheCurso = new CacheCurso();
		_cacheAlunoTurma = new CacheAlunoTurma();
		_cacheCursoInstancia = new CacheCursoInstancia();
		_cacheAlunoCursoInstancia = new CacheAlunoCursoInstancia();
		_cacheAvaliacao = new CacheAvaliacao();
		_cacheAvaliacaoAluno = new CacheAvaliacaoAluno();
		_cacheProva = new CacheProva();
		_cacheCorrecao = new CacheCorrecao();
		_cacheProvaCorrecao = new CacheProvaCorrecao();
		_cacheAlunoProvaCorrecao = new CacheAlunoProvaCorrecao();
		_cacheEntradaProvaCorrecao = new CacheEntradaProvaCorrecao();
		_cacheColetaQuestionario = new CacheColetaQuestionario();
		_cacheEntradaColetaQuestionario = new CacheEntradaColetaQuestionario();
	}

	public void setRepositorio(Repositorio r) {
		_repositorio = r;
	}

	public void setLocalMode(boolean l) {
		_localMode = l;
	}

	public void limpar() {
		_treeInstituicao.clear();
		_treePeriodo.clear();
		_treeTurma.clear();
		_treeAluno.clear();
		_treeCurso.clear();
		_treeAlunoTurma.clear();
		_treeCursoInstancia.clear();
		_treeAlunoCursoInstancia.clear();
		_treeAvaliacao.clear();
		_treeAvaliacaoAluno.clear();
		_treeProva.clear();
		_treeCorrecao.clear();
		_treeProvaCorrecao.clear();
		_treeAlunoProvaCorrecao.clear();
		_treeEntradaProvaCorrecao.clear();
		_treeColetaQuestionario.clear();
		_treeEntradaColetaQuestionario.clear();
		_cacheTurma.limpar();
		_cacheAluno.limpar();
		_cacheCurso.limpar();
		_cacheAlunoTurma.limpar();
		_cacheCursoInstancia.limpar();
		_cacheAlunoCursoInstancia.limpar();
		_cacheAvaliacao.limpar();
		_cacheAvaliacaoAluno.limpar();
		_cacheProva.limpar();
		_cacheCorrecao.limpar();
		_cacheProvaCorrecao.limpar();
		_cacheAlunoProvaCorrecao.limpar();
		_cacheEntradaProvaCorrecao.limpar();
		_cacheColetaQuestionario.limpar();
		_cacheEntradaColetaQuestionario.limpar();
		_allInstituicao = false;
		_allPeriodo = false;
		_allTurma = false;
		_allAluno = false;
		_allCurso = false;
		_allAlunoTurma = false;
		_allCursoInstancia = false;
		_allAlunoCursoInstancia = false;
		_allAvaliacao = false;
		_allAvaliacaoAluno = false;
		_allProva = false;
		_allCorrecao = false;
		_allProvaCorrecao = false;
		_allAlunoProvaCorrecao = false;
		_allEntradaProvaCorrecao = false;
		_allColetaQuestionario = false;
		_allEntradaColetaQuestionario = false;
	}

	//////////////// entidade Instituicao ////////////////

	public Instituicao consultarInstituicao(int id_instituicao) throws SQLException {
		InstituicaoKey key = new InstituicaoKey(id_instituicao);
		Instituicao instituicao = (Instituicao) _treeInstituicao.search(key);
		if(instituicao == null) {
			instituicao = _repositorio.consultarInstituicao(id_instituicao);
			_treeInstituicao.insert(key, instituicao);
		}
		return instituicao;
	}

	public Vector consultarInstituicao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allInstituicao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeInstituicao);
			while(it.hasNext()) {
				Instituicao instituicao = (Instituicao) it.next();
				vector.add(instituicao);
			}
		}
		else {
			vector = _repositorio.consultarInstituicao();
			adicionarInstituicaoNaCache(vector);
			_allInstituicao = true;
		}
		return vector;
	}

	public void atualizarNomeEmInstituicao(Instituicao instituicao, String nome) throws SQLException {
		_repositorio.atualizarNomeEmInstituicao(instituicao, nome);
	}

	public Instituicao inserirInstituicao(Instituicao obj) throws SQLException {
		Instituicao obj2 = _repositorio.inserirInstituicao(obj);
		_treeInstituicao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Instituicao inserirInstituicao(String nome) throws SQLException {
		Instituicao instituicao = _repositorio.inserirInstituicao(nome);
		_treeInstituicao.insert(instituicao.getKey(), instituicao);
		return instituicao;
	}

	public void removerInstituicao(Instituicao obj) throws SQLException {
		removerInstituicao(obj.getId());
	}

	public void removerInstituicao(int id_instituicao) throws SQLException {
		_repositorio.removerInstituicao(id_instituicao);
		_treeInstituicao.delete(new InstituicaoKey(id_instituicao));
	}

	public void adicionarInstituicaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Instituicao instituicao = (Instituicao) vector.get(i);
			Instituicao temp = (Instituicao) _treeInstituicao.search(instituicao.getKey());
			if(temp == null) {
				_treeInstituicao.insert(instituicao.getKey(), instituicao);
			}
			else {
				vector.set(i, instituicao);
			}
		}
	}

	//////////////// entidade Periodo ////////////////

	public Periodo consultarPeriodo(String id_periodo) throws SQLException {
		PeriodoKey key = new PeriodoKey(id_periodo);
		Periodo periodo = (Periodo) _treePeriodo.search(key);
		if(periodo == null) {
			periodo = _repositorio.consultarPeriodo(id_periodo);
			_treePeriodo.insert(key, periodo);
		}
		return periodo;
	}

	public Vector consultarPeriodo() throws SQLException {
		Vector vector = null;
		if(_localMode || _allPeriodo) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treePeriodo);
			while(it.hasNext()) {
				Periodo periodo = (Periodo) it.next();
				vector.add(periodo);
			}
		}
		else {
			vector = _repositorio.consultarPeriodo();
			adicionarPeriodoNaCache(vector);
			_allPeriodo = true;
		}
		return vector;
	}

	public void atualizarId_periodoEmPeriodo(Periodo periodo, String id_periodo) throws SQLException {
		_repositorio.atualizarId_periodoEmPeriodo(periodo, id_periodo);
	}

	public Periodo inserirPeriodo(Periodo obj) throws SQLException {
		Periodo obj2 = _repositorio.inserirPeriodo(obj);
		_treePeriodo.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Periodo inserirPeriodo(String id_periodo) throws SQLException {
		Periodo periodo = _repositorio.inserirPeriodo(id_periodo);
		_treePeriodo.insert(periodo.getKey(), periodo);
		return periodo;
	}

	public void removerPeriodo(Periodo obj) throws SQLException {
		removerPeriodo(obj.getId_periodo());
	}

	public void removerPeriodo(String id_periodo) throws SQLException {
		_repositorio.removerPeriodo(id_periodo);
		_treePeriodo.delete(new PeriodoKey(id_periodo));
	}

	public void adicionarPeriodoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Periodo periodo = (Periodo) vector.get(i);
			Periodo temp = (Periodo) _treePeriodo.search(periodo.getKey());
			if(temp == null) {
				_treePeriodo.insert(periodo.getKey(), periodo);
			}
			else {
				vector.set(i, periodo);
			}
		}
	}

	//////////////// entidade Turma ////////////////

	public Turma consultarTurma(int id_turma) throws SQLException {
		TurmaKey key = new TurmaKey(id_turma);
		Turma turma = (Turma) _treeTurma.search(key);
		if(turma == null) {
			turma = _repositorio.consultarTurma(id_turma);
			_treeTurma.insert(key, turma);
		}
		return turma;
	}

	public Vector consultarTurma() throws SQLException {
		Vector vector = null;
		if(_localMode || _allTurma) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeTurma);
			while(it.hasNext()) {
				Turma turma = (Turma) it.next();
				vector.add(turma);
			}
		}
		else {
			vector = _repositorio.consultarTurma();
			adicionarTurmaNaCache(vector);
			_allTurma = true;
		}
		return vector;
	}

	public Vector consultarTurmaPorInstituicao(Instituicao instituicao_Turma) throws SQLException {
		Vector vector = null;
		if(_localMode || _allTurma || _cacheTurma.cacheInstituicao_Turma(instituicao_Turma)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeTurma);
			while(it.hasNext()) {
				Turma turma = (Turma) it.next();
				if(((turma.getInstituicao_Turma() == null && instituicao_Turma == null) || (turma.getInstituicao_Turma() != null && instituicao_Turma != null && turma.getInstituicao_Turma().equals(instituicao_Turma)))) {
					vector.add(turma);
				}
			}
		}
		else {
			vector = _repositorio.consultarTurmaPorInstituicao(instituicao_Turma);
			_cacheTurma.adicionarInstituicao_Turma(instituicao_Turma);
			adicionarTurmaNaCache(vector);
		}
		return vector;
	}

	public Vector consultarTurmaPorInstituicaoNome(String nome, Instituicao instituicao_Turma) throws SQLException {
		Vector vector = null;
		if(_localMode || _allTurma || _cacheTurma.cacheNomeInstituicao_Turma(nome, instituicao_Turma)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeTurma);
			while(it.hasNext()) {
				Turma turma = (Turma) it.next();
				if(((turma.getNome() == null && nome == null) || (turma.getNome() != null && nome != null && turma.getNome().equals(nome))) && ((turma.getInstituicao_Turma() == null && instituicao_Turma == null) || (turma.getInstituicao_Turma() != null && instituicao_Turma != null && turma.getInstituicao_Turma().equals(instituicao_Turma)))) {
					vector.add(turma);
				}
			}
		}
		else {
			vector = _repositorio.consultarTurmaPorInstituicaoNome(nome, instituicao_Turma);
			_cacheTurma.adicionarNomeInstituicao_Turma(nome, instituicao_Turma);
			adicionarTurmaNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmTurma(Turma turma, String nome) throws SQLException {
		_repositorio.atualizarNomeEmTurma(turma, nome);
	}

	public void atualizarInstituicao_TurmaEmTurma(Turma turma, Instituicao instituicao_Turma) throws SQLException {
		_repositorio.atualizarInstituicao_TurmaEmTurma(turma, instituicao_Turma);
	}

	public Turma inserirTurma(Turma obj) throws SQLException {
		Turma obj2 = _repositorio.inserirTurma(obj);
		_treeTurma.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Turma inserirTurma(String nome, Instituicao instituicao_Turma) throws SQLException {
		Turma turma = _repositorio.inserirTurma(nome, instituicao_Turma);
		_treeTurma.insert(turma.getKey(), turma);
		return turma;
	}

	public void removerTurma(Turma obj) throws SQLException {
		removerTurma(obj.getId());
	}

	public void removerTurma(int id_turma) throws SQLException {
		_repositorio.removerTurma(id_turma);
		_treeTurma.delete(new TurmaKey(id_turma));
	}

	public void adicionarTurmaNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Turma turma = (Turma) vector.get(i);
			Turma temp = (Turma) _treeTurma.search(turma.getKey());
			if(temp == null) {
				_treeTurma.insert(turma.getKey(), turma);
			}
			else {
				vector.set(i, turma);
			}
		}
	}

	//////////////// entidade Aluno ////////////////

	public Aluno consultarAluno(int id_aluno) throws SQLException {
		AlunoKey key = new AlunoKey(id_aluno);
		Aluno aluno = (Aluno) _treeAluno.search(key);
		if(aluno == null) {
			aluno = _repositorio.consultarAluno(id_aluno);
			_treeAluno.insert(key, aluno);
		}
		return aluno;
	}

	public Vector consultarAluno() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAluno) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAluno);
			while(it.hasNext()) {
				Aluno aluno = (Aluno) it.next();
				vector.add(aluno);
			}
		}
		else {
			vector = _repositorio.consultarAluno();
			adicionarAlunoNaCache(vector);
			_allAluno = true;
		}
		return vector;
	}

	public Vector consultarAlunoPorMatricula(String matricula) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAluno || _cacheAluno.cacheMatricula(matricula)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAluno);
			while(it.hasNext()) {
				Aluno aluno = (Aluno) it.next();
				if(((aluno.getMatricula() == null && matricula == null) || (aluno.getMatricula() != null && matricula != null && aluno.getMatricula().equals(matricula)))) {
					vector.add(aluno);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoPorMatricula(matricula);
			_cacheAluno.adicionarMatricula(matricula);
			adicionarAlunoNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAlunoPorInstituicao(Instituicao instituicao_Aluno) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAluno || _cacheAluno.cacheInstituicao_Aluno(instituicao_Aluno)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAluno);
			while(it.hasNext()) {
				Aluno aluno = (Aluno) it.next();
				if(((aluno.getInstituicao_Aluno() == null && instituicao_Aluno == null) || (aluno.getInstituicao_Aluno() != null && instituicao_Aluno != null && aluno.getInstituicao_Aluno().equals(instituicao_Aluno)))) {
					vector.add(aluno);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoPorInstituicao(instituicao_Aluno);
			_cacheAluno.adicionarInstituicao_Aluno(instituicao_Aluno);
			adicionarAlunoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmAluno(Aluno aluno, String nome) throws SQLException {
		_repositorio.atualizarNomeEmAluno(aluno, nome);
	}

	public void atualizarMatriculaEmAluno(Aluno aluno, String matricula) throws SQLException {
		_repositorio.atualizarMatriculaEmAluno(aluno, matricula);
	}

	public void atualizarInstituicao_AlunoEmAluno(Aluno aluno, Instituicao instituicao_Aluno) throws SQLException {
		_repositorio.atualizarInstituicao_AlunoEmAluno(aluno, instituicao_Aluno);
	}

	public Aluno inserirAluno(Aluno obj) throws SQLException {
		Aluno obj2 = _repositorio.inserirAluno(obj);
		_treeAluno.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Aluno inserirAluno(String nome, String matricula, Instituicao instituicao_Aluno) throws SQLException {
		Aluno aluno = _repositorio.inserirAluno(nome, matricula, instituicao_Aluno);
		_treeAluno.insert(aluno.getKey(), aluno);
		return aluno;
	}

	public void removerAluno(Aluno obj) throws SQLException {
		removerAluno(obj.getId());
	}

	public void removerAluno(int id_aluno) throws SQLException {
		_repositorio.removerAluno(id_aluno);
		_treeAluno.delete(new AlunoKey(id_aluno));
	}

	public void adicionarAlunoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Aluno aluno = (Aluno) vector.get(i);
			Aluno temp = (Aluno) _treeAluno.search(aluno.getKey());
			if(temp == null) {
				_treeAluno.insert(aluno.getKey(), aluno);
			}
			else {
				vector.set(i, aluno);
			}
		}
	}

	//////////////// entidade Curso ////////////////

	public Curso consultarCurso(int id_curso) throws SQLException {
		CursoKey key = new CursoKey(id_curso);
		Curso curso = (Curso) _treeCurso.search(key);
		if(curso == null) {
			curso = _repositorio.consultarCurso(id_curso);
			_treeCurso.insert(key, curso);
		}
		return curso;
	}

	public Vector consultarCurso() throws SQLException {
		Vector vector = null;
		if(_localMode || _allCurso) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCurso);
			while(it.hasNext()) {
				Curso curso = (Curso) it.next();
				vector.add(curso);
			}
		}
		else {
			vector = _repositorio.consultarCurso();
			adicionarCursoNaCache(vector);
			_allCurso = true;
		}
		return vector;
	}

	public Vector consultarCursoPorInstituicao(Instituicao instituicao_Curso) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCurso || _cacheCurso.cacheInstituicao_Curso(instituicao_Curso)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCurso);
			while(it.hasNext()) {
				Curso curso = (Curso) it.next();
				if(((curso.getInstituicao_Curso() == null && instituicao_Curso == null) || (curso.getInstituicao_Curso() != null && instituicao_Curso != null && curso.getInstituicao_Curso().equals(instituicao_Curso)))) {
					vector.add(curso);
				}
			}
		}
		else {
			vector = _repositorio.consultarCursoPorInstituicao(instituicao_Curso);
			_cacheCurso.adicionarInstituicao_Curso(instituicao_Curso);
			adicionarCursoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmCurso(Curso curso, String nome) throws SQLException {
		_repositorio.atualizarNomeEmCurso(curso, nome);
	}

	public void atualizarInstituicao_CursoEmCurso(Curso curso, Instituicao instituicao_Curso) throws SQLException {
		_repositorio.atualizarInstituicao_CursoEmCurso(curso, instituicao_Curso);
	}

	public Curso inserirCurso(Curso obj) throws SQLException {
		Curso obj2 = _repositorio.inserirCurso(obj);
		_treeCurso.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Curso inserirCurso(String nome, Instituicao instituicao_Curso) throws SQLException {
		Curso curso = _repositorio.inserirCurso(nome, instituicao_Curso);
		_treeCurso.insert(curso.getKey(), curso);
		return curso;
	}

	public void removerCurso(Curso obj) throws SQLException {
		removerCurso(obj.getId());
	}

	public void removerCurso(int id_curso) throws SQLException {
		_repositorio.removerCurso(id_curso);
		_treeCurso.delete(new CursoKey(id_curso));
	}

	public void adicionarCursoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Curso curso = (Curso) vector.get(i);
			Curso temp = (Curso) _treeCurso.search(curso.getKey());
			if(temp == null) {
				_treeCurso.insert(curso.getKey(), curso);
			}
			else {
				vector.set(i, curso);
			}
		}
	}

	//////////////// entidade AlunoTurma ////////////////

	public AlunoTurma consultarAlunoTurma(int id_aluno, int id_turma) throws SQLException {
		AlunoTurmaKey key = new AlunoTurmaKey(id_aluno, id_turma);
		AlunoTurma alunoTurma = (AlunoTurma) _treeAlunoTurma.search(key);
		if(alunoTurma == null) {
			alunoTurma = _repositorio.consultarAlunoTurma(id_aluno, id_turma);
			_treeAlunoTurma.insert(key, alunoTurma);
		}
		return alunoTurma;
	}

	public Vector consultarAlunoTurma() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoTurma) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoTurma);
			while(it.hasNext()) {
				AlunoTurma alunoTurma = (AlunoTurma) it.next();
				vector.add(alunoTurma);
			}
		}
		else {
			vector = _repositorio.consultarAlunoTurma();
			adicionarAlunoTurmaNaCache(vector);
			_allAlunoTurma = true;
		}
		return vector;
	}

	public Vector consultarAlunoTurmaPorTurma(Turma turma_AlunoTurma) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoTurma || _cacheAlunoTurma.cacheTurma_AlunoTurma(turma_AlunoTurma)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoTurma);
			while(it.hasNext()) {
				AlunoTurma alunoTurma = (AlunoTurma) it.next();
				if(((alunoTurma.getTurma_AlunoTurma() == null && turma_AlunoTurma == null) || (alunoTurma.getTurma_AlunoTurma() != null && turma_AlunoTurma != null && alunoTurma.getTurma_AlunoTurma().equals(turma_AlunoTurma)))) {
					vector.add(alunoTurma);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoTurmaPorTurma(turma_AlunoTurma);
			_cacheAlunoTurma.adicionarTurma_AlunoTurma(turma_AlunoTurma);
			adicionarAlunoTurmaNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAlunoTurmaPorAluno(Aluno aluno_AlunoTurma) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoTurma || _cacheAlunoTurma.cacheAluno_AlunoTurma(aluno_AlunoTurma)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoTurma);
			while(it.hasNext()) {
				AlunoTurma alunoTurma = (AlunoTurma) it.next();
				if(((alunoTurma.getAluno_AlunoTurma() == null && aluno_AlunoTurma == null) || (alunoTurma.getAluno_AlunoTurma() != null && aluno_AlunoTurma != null && alunoTurma.getAluno_AlunoTurma().equals(aluno_AlunoTurma)))) {
					vector.add(alunoTurma);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoTurmaPorAluno(aluno_AlunoTurma);
			_cacheAlunoTurma.adicionarAluno_AlunoTurma(aluno_AlunoTurma);
			adicionarAlunoTurmaNaCache(vector);
		}
		return vector;
	}

	public void atualizarAluno_AlunoTurmaEmAlunoTurma(AlunoTurma alunoTurma, Aluno aluno_AlunoTurma) throws SQLException {
		_repositorio.atualizarAluno_AlunoTurmaEmAlunoTurma(alunoTurma, aluno_AlunoTurma);
	}

	public void atualizarTurma_AlunoTurmaEmAlunoTurma(AlunoTurma alunoTurma, Turma turma_AlunoTurma) throws SQLException {
		_repositorio.atualizarTurma_AlunoTurmaEmAlunoTurma(alunoTurma, turma_AlunoTurma);
	}

	public AlunoTurma inserirAlunoTurma(AlunoTurma obj) throws SQLException {
		AlunoTurma obj2 = _repositorio.inserirAlunoTurma(obj);
		_treeAlunoTurma.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public AlunoTurma inserirAlunoTurma(Aluno aluno_AlunoTurma, Turma turma_AlunoTurma) throws SQLException {
		AlunoTurma alunoTurma = _repositorio.inserirAlunoTurma(aluno_AlunoTurma, turma_AlunoTurma);
		_treeAlunoTurma.insert(alunoTurma.getKey(), alunoTurma);
		return alunoTurma;
	}

	public void removerAlunoTurma(AlunoTurma obj) throws SQLException {
		removerAlunoTurma(obj.getId_aluno(), obj.getId_turma());
	}

	public void removerAlunoTurma(int id_aluno, int id_turma) throws SQLException {
		_repositorio.removerAlunoTurma(id_aluno, id_turma);
		_treeAlunoTurma.delete(new AlunoTurmaKey(id_aluno, id_turma));
	}

	public void adicionarAlunoTurmaNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			AlunoTurma alunoTurma = (AlunoTurma) vector.get(i);
			AlunoTurma temp = (AlunoTurma) _treeAlunoTurma.search(alunoTurma.getKey());
			if(temp == null) {
				_treeAlunoTurma.insert(alunoTurma.getKey(), alunoTurma);
			}
			else {
				vector.set(i, alunoTurma);
			}
		}
	}

	//////////////// entidade CursoInstancia ////////////////

	public CursoInstancia consultarCursoInstancia(int id_cursoinstancia) throws SQLException {
		CursoInstanciaKey key = new CursoInstanciaKey(id_cursoinstancia);
		CursoInstancia cursoInstancia = (CursoInstancia) _treeCursoInstancia.search(key);
		if(cursoInstancia == null) {
			cursoInstancia = _repositorio.consultarCursoInstancia(id_cursoinstancia);
			_treeCursoInstancia.insert(key, cursoInstancia);
		}
		return cursoInstancia;
	}

	public Vector consultarCursoInstancia() throws SQLException {
		Vector vector = null;
		if(_localMode || _allCursoInstancia) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCursoInstancia);
			while(it.hasNext()) {
				CursoInstancia cursoInstancia = (CursoInstancia) it.next();
				vector.add(cursoInstancia);
			}
		}
		else {
			vector = _repositorio.consultarCursoInstancia();
			adicionarCursoInstanciaNaCache(vector);
			_allCursoInstancia = true;
		}
		return vector;
	}

	public Vector consultarCursoInstanciaPorInstituicao(Instituicao instituicao_CursoInstancia) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCursoInstancia || _cacheCursoInstancia.cacheInstituicao_CursoInstancia(instituicao_CursoInstancia)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCursoInstancia);
			while(it.hasNext()) {
				CursoInstancia cursoInstancia = (CursoInstancia) it.next();
				if(((cursoInstancia.getInstituicao_CursoInstancia() == null && instituicao_CursoInstancia == null) || (cursoInstancia.getInstituicao_CursoInstancia() != null && instituicao_CursoInstancia != null && cursoInstancia.getInstituicao_CursoInstancia().equals(instituicao_CursoInstancia)))) {
					vector.add(cursoInstancia);
				}
			}
		}
		else {
			vector = _repositorio.consultarCursoInstanciaPorInstituicao(instituicao_CursoInstancia);
			_cacheCursoInstancia.adicionarInstituicao_CursoInstancia(instituicao_CursoInstancia);
			adicionarCursoInstanciaNaCache(vector);
		}
		return vector;
	}

	public Vector consultarCursoInstanciaPorCurso(Curso curso_CursoInstancia) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCursoInstancia || _cacheCursoInstancia.cacheCurso_CursoInstancia(curso_CursoInstancia)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCursoInstancia);
			while(it.hasNext()) {
				CursoInstancia cursoInstancia = (CursoInstancia) it.next();
				if(((cursoInstancia.getCurso_CursoInstancia() == null && curso_CursoInstancia == null) || (cursoInstancia.getCurso_CursoInstancia() != null && curso_CursoInstancia != null && cursoInstancia.getCurso_CursoInstancia().equals(curso_CursoInstancia)))) {
					vector.add(cursoInstancia);
				}
			}
		}
		else {
			vector = _repositorio.consultarCursoInstanciaPorCurso(curso_CursoInstancia);
			_cacheCursoInstancia.adicionarCurso_CursoInstancia(curso_CursoInstancia);
			adicionarCursoInstanciaNaCache(vector);
		}
		return vector;
	}

	public Vector consultarCursoInstanciaPorPeriodo(Periodo periodo_CursoInstancia) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCursoInstancia || _cacheCursoInstancia.cachePeriodo_CursoInstancia(periodo_CursoInstancia)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCursoInstancia);
			while(it.hasNext()) {
				CursoInstancia cursoInstancia = (CursoInstancia) it.next();
				if(((cursoInstancia.getPeriodo_CursoInstancia() == null && periodo_CursoInstancia == null) || (cursoInstancia.getPeriodo_CursoInstancia() != null && periodo_CursoInstancia != null && cursoInstancia.getPeriodo_CursoInstancia().equals(periodo_CursoInstancia)))) {
					vector.add(cursoInstancia);
				}
			}
		}
		else {
			vector = _repositorio.consultarCursoInstanciaPorPeriodo(periodo_CursoInstancia);
			_cacheCursoInstancia.adicionarPeriodo_CursoInstancia(periodo_CursoInstancia);
			adicionarCursoInstanciaNaCache(vector);
		}
		return vector;
	}

	public void atualizarInstituicao_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Instituicao instituicao_CursoInstancia) throws SQLException {
		_repositorio.atualizarInstituicao_CursoInstanciaEmCursoInstancia(cursoInstancia, instituicao_CursoInstancia);
	}

	public void atualizarCurso_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Curso curso_CursoInstancia) throws SQLException {
		_repositorio.atualizarCurso_CursoInstanciaEmCursoInstancia(cursoInstancia, curso_CursoInstancia);
	}

	public void atualizarPeriodo_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Periodo periodo_CursoInstancia) throws SQLException {
		_repositorio.atualizarPeriodo_CursoInstanciaEmCursoInstancia(cursoInstancia, periodo_CursoInstancia);
	}

	public CursoInstancia inserirCursoInstancia(CursoInstancia obj) throws SQLException {
		CursoInstancia obj2 = _repositorio.inserirCursoInstancia(obj);
		_treeCursoInstancia.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public CursoInstancia inserirCursoInstancia(Instituicao instituicao_CursoInstancia, Curso curso_CursoInstancia, Periodo periodo_CursoInstancia) throws SQLException {
		CursoInstancia cursoInstancia = _repositorio.inserirCursoInstancia(instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia);
		_treeCursoInstancia.insert(cursoInstancia.getKey(), cursoInstancia);
		return cursoInstancia;
	}

	public void removerCursoInstancia(CursoInstancia obj) throws SQLException {
		removerCursoInstancia(obj.getId());
	}

	public void removerCursoInstancia(int id_cursoinstancia) throws SQLException {
		_repositorio.removerCursoInstancia(id_cursoinstancia);
		_treeCursoInstancia.delete(new CursoInstanciaKey(id_cursoinstancia));
	}

	public void adicionarCursoInstanciaNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			CursoInstancia cursoInstancia = (CursoInstancia) vector.get(i);
			CursoInstancia temp = (CursoInstancia) _treeCursoInstancia.search(cursoInstancia.getKey());
			if(temp == null) {
				_treeCursoInstancia.insert(cursoInstancia.getKey(), cursoInstancia);
			}
			else {
				vector.set(i, cursoInstancia);
			}
		}
	}

	//////////////// entidade AlunoCursoInstancia ////////////////

	public AlunoCursoInstancia consultarAlunoCursoInstancia(int id_alunocursoinstancia) throws SQLException {
		AlunoCursoInstanciaKey key = new AlunoCursoInstanciaKey(id_alunocursoinstancia);
		AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) _treeAlunoCursoInstancia.search(key);
		if(alunoCursoInstancia == null) {
			alunoCursoInstancia = _repositorio.consultarAlunoCursoInstancia(id_alunocursoinstancia);
			_treeAlunoCursoInstancia.insert(key, alunoCursoInstancia);
		}
		return alunoCursoInstancia;
	}

	public Vector consultarAlunoCursoInstancia() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoCursoInstancia) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoCursoInstancia);
			while(it.hasNext()) {
				AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) it.next();
				vector.add(alunoCursoInstancia);
			}
		}
		else {
			vector = _repositorio.consultarAlunoCursoInstancia();
			adicionarAlunoCursoInstanciaNaCache(vector);
			_allAlunoCursoInstancia = true;
		}
		return vector;
	}

	public Vector consultarAlunoCursoInstanciaPorCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoCursoInstancia || _cacheAlunoCursoInstancia.cacheCursoInstancia_AlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoCursoInstancia);
			while(it.hasNext()) {
				AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) it.next();
				if(((alunoCursoInstancia.getCursoInstancia_AlunoCursoInstancia() == null && cursoInstancia_AlunoCursoInstancia == null) || (alunoCursoInstancia.getCursoInstancia_AlunoCursoInstancia() != null && cursoInstancia_AlunoCursoInstancia != null && alunoCursoInstancia.getCursoInstancia_AlunoCursoInstancia().equals(cursoInstancia_AlunoCursoInstancia)))) {
					vector.add(alunoCursoInstancia);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoCursoInstanciaPorCursoInstancia(cursoInstancia_AlunoCursoInstancia);
			_cacheAlunoCursoInstancia.adicionarCursoInstancia_AlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia);
			adicionarAlunoCursoInstanciaNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAlunoCursoInstanciaPorAluno(Aluno aluno_AlunoCursoInstancia) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoCursoInstancia || _cacheAlunoCursoInstancia.cacheAluno_AlunoCursoInstancia(aluno_AlunoCursoInstancia)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoCursoInstancia);
			while(it.hasNext()) {
				AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) it.next();
				if(((alunoCursoInstancia.getAluno_AlunoCursoInstancia() == null && aluno_AlunoCursoInstancia == null) || (alunoCursoInstancia.getAluno_AlunoCursoInstancia() != null && aluno_AlunoCursoInstancia != null && alunoCursoInstancia.getAluno_AlunoCursoInstancia().equals(aluno_AlunoCursoInstancia)))) {
					vector.add(alunoCursoInstancia);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoCursoInstanciaPorAluno(aluno_AlunoCursoInstancia);
			_cacheAlunoCursoInstancia.adicionarAluno_AlunoCursoInstancia(aluno_AlunoCursoInstancia);
			adicionarAlunoCursoInstanciaNaCache(vector);
		}
		return vector;
	}

	public void atualizarCursoInstancia_AlunoCursoInstanciaEmAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia, CursoInstancia cursoInstancia_AlunoCursoInstancia) throws SQLException {
		_repositorio.atualizarCursoInstancia_AlunoCursoInstanciaEmAlunoCursoInstancia(alunoCursoInstancia, cursoInstancia_AlunoCursoInstancia);
	}

	public void atualizarAluno_AlunoCursoInstanciaEmAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia, Aluno aluno_AlunoCursoInstancia) throws SQLException {
		_repositorio.atualizarAluno_AlunoCursoInstanciaEmAlunoCursoInstancia(alunoCursoInstancia, aluno_AlunoCursoInstancia);
	}

	public AlunoCursoInstancia inserirAlunoCursoInstancia(AlunoCursoInstancia obj) throws SQLException {
		AlunoCursoInstancia obj2 = _repositorio.inserirAlunoCursoInstancia(obj);
		_treeAlunoCursoInstancia.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public AlunoCursoInstancia inserirAlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia, Aluno aluno_AlunoCursoInstancia) throws SQLException {
		AlunoCursoInstancia alunoCursoInstancia = _repositorio.inserirAlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia, aluno_AlunoCursoInstancia);
		_treeAlunoCursoInstancia.insert(alunoCursoInstancia.getKey(), alunoCursoInstancia);
		return alunoCursoInstancia;
	}

	public void removerAlunoCursoInstancia(AlunoCursoInstancia obj) throws SQLException {
		removerAlunoCursoInstancia(obj.getId());
	}

	public void removerAlunoCursoInstancia(int id_alunocursoinstancia) throws SQLException {
		_repositorio.removerAlunoCursoInstancia(id_alunocursoinstancia);
		_treeAlunoCursoInstancia.delete(new AlunoCursoInstanciaKey(id_alunocursoinstancia));
	}

	public void adicionarAlunoCursoInstanciaNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			AlunoCursoInstancia alunoCursoInstancia = (AlunoCursoInstancia) vector.get(i);
			AlunoCursoInstancia temp = (AlunoCursoInstancia) _treeAlunoCursoInstancia.search(alunoCursoInstancia.getKey());
			if(temp == null) {
				_treeAlunoCursoInstancia.insert(alunoCursoInstancia.getKey(), alunoCursoInstancia);
			}
			else {
				vector.set(i, alunoCursoInstancia);
			}
		}
	}

	//////////////// entidade Avaliacao ////////////////

	public Avaliacao consultarAvaliacao(int id_avaliacao) throws SQLException {
		AvaliacaoKey key = new AvaliacaoKey(id_avaliacao);
		Avaliacao avaliacao = (Avaliacao) _treeAvaliacao.search(key);
		if(avaliacao == null) {
			avaliacao = _repositorio.consultarAvaliacao(id_avaliacao);
			_treeAvaliacao.insert(key, avaliacao);
		}
		return avaliacao;
	}

	public Vector consultarAvaliacao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacao);
			while(it.hasNext()) {
				Avaliacao avaliacao = (Avaliacao) it.next();
				vector.add(avaliacao);
			}
		}
		else {
			vector = _repositorio.consultarAvaliacao();
			adicionarAvaliacaoNaCache(vector);
			_allAvaliacao = true;
		}
		return vector;
	}

	public Vector consultarAvaliacaoPorTipo(String tipo) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacao || _cacheAvaliacao.cacheTipo(tipo)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacao);
			while(it.hasNext()) {
				Avaliacao avaliacao = (Avaliacao) it.next();
				if(((avaliacao.getTipo() == null && tipo == null) || (avaliacao.getTipo() != null && tipo != null && avaliacao.getTipo().equals(tipo)))) {
					vector.add(avaliacao);
				}
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoPorTipo(tipo);
			_cacheAvaliacao.adicionarTipo(tipo);
			adicionarAvaliacaoNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAvaliacaoPorCursoInstancia(CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacao || _cacheAvaliacao.cacheCursoInstancia_Avaliacao(cursoInstancia_Avaliacao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacao);
			while(it.hasNext()) {
				Avaliacao avaliacao = (Avaliacao) it.next();
				if(((avaliacao.getCursoInstancia_Avaliacao() == null && cursoInstancia_Avaliacao == null) || (avaliacao.getCursoInstancia_Avaliacao() != null && cursoInstancia_Avaliacao != null && avaliacao.getCursoInstancia_Avaliacao().equals(cursoInstancia_Avaliacao)))) {
					vector.add(avaliacao);
				}
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoPorCursoInstancia(cursoInstancia_Avaliacao);
			_cacheAvaliacao.adicionarCursoInstancia_Avaliacao(cursoInstancia_Avaliacao);
			adicionarAvaliacaoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmAvaliacao(Avaliacao avaliacao, String nome) throws SQLException {
		_repositorio.atualizarNomeEmAvaliacao(avaliacao, nome);
	}

	public void atualizarTipoEmAvaliacao(Avaliacao avaliacao, String tipo) throws SQLException {
		_repositorio.atualizarTipoEmAvaliacao(avaliacao, tipo);
	}

	public void atualizarCursoInstancia_AvaliacaoEmAvaliacao(Avaliacao avaliacao, CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		_repositorio.atualizarCursoInstancia_AvaliacaoEmAvaliacao(avaliacao, cursoInstancia_Avaliacao);
	}

	public Avaliacao inserirAvaliacao(Avaliacao obj) throws SQLException {
		Avaliacao obj2 = _repositorio.inserirAvaliacao(obj);
		_treeAvaliacao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Avaliacao inserirAvaliacao(String nome, String tipo, CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		Avaliacao avaliacao = _repositorio.inserirAvaliacao(nome, tipo, cursoInstancia_Avaliacao);
		_treeAvaliacao.insert(avaliacao.getKey(), avaliacao);
		return avaliacao;
	}

	public void removerAvaliacao(Avaliacao obj) throws SQLException {
		removerAvaliacao(obj.getId());
	}

	public void removerAvaliacao(int id_avaliacao) throws SQLException {
		_repositorio.removerAvaliacao(id_avaliacao);
		_treeAvaliacao.delete(new AvaliacaoKey(id_avaliacao));
	}

	public void adicionarAvaliacaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Avaliacao avaliacao = (Avaliacao) vector.get(i);
			Avaliacao temp = (Avaliacao) _treeAvaliacao.search(avaliacao.getKey());
			if(temp == null) {
				_treeAvaliacao.insert(avaliacao.getKey(), avaliacao);
			}
			else {
				vector.set(i, avaliacao);
			}
		}
	}

	//////////////// entidade AvaliacaoAluno ////////////////

	public AvaliacaoAluno consultarAvaliacaoAluno(int id_avaliacaoaluno) throws SQLException {
		AvaliacaoAlunoKey key = new AvaliacaoAlunoKey(id_avaliacaoaluno);
		AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) _treeAvaliacaoAluno.search(key);
		if(avaliacaoAluno == null) {
			avaliacaoAluno = _repositorio.consultarAvaliacaoAluno(id_avaliacaoaluno);
			_treeAvaliacaoAluno.insert(key, avaliacaoAluno);
		}
		return avaliacaoAluno;
	}

	public Vector consultarAvaliacaoAluno() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacaoAluno) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacaoAluno);
			while(it.hasNext()) {
				AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) it.next();
				vector.add(avaliacaoAluno);
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoAluno();
			adicionarAvaliacaoAlunoNaCache(vector);
			_allAvaliacaoAluno = true;
		}
		return vector;
	}

	public Vector consultarAvaliacaoAlunoPorAvaliacao(Avaliacao avaliacao_AvaliacaoAluno) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacaoAluno || _cacheAvaliacaoAluno.cacheAvaliacao_AvaliacaoAluno(avaliacao_AvaliacaoAluno)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacaoAluno);
			while(it.hasNext()) {
				AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) it.next();
				if(((avaliacaoAluno.getAvaliacao_AvaliacaoAluno() == null && avaliacao_AvaliacaoAluno == null) || (avaliacaoAluno.getAvaliacao_AvaliacaoAluno() != null && avaliacao_AvaliacaoAluno != null && avaliacaoAluno.getAvaliacao_AvaliacaoAluno().equals(avaliacao_AvaliacaoAluno)))) {
					vector.add(avaliacaoAluno);
				}
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoAlunoPorAvaliacao(avaliacao_AvaliacaoAluno);
			_cacheAvaliacaoAluno.adicionarAvaliacao_AvaliacaoAluno(avaliacao_AvaliacaoAluno);
			adicionarAvaliacaoAlunoNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAvaliacaoAlunoPorCorrecao(Correcao correcao_AvaliacaoAluno) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacaoAluno || _cacheAvaliacaoAluno.cacheCorrecao_AvaliacaoAluno(correcao_AvaliacaoAluno)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacaoAluno);
			while(it.hasNext()) {
				AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) it.next();
				if(((avaliacaoAluno.getCorrecao_AvaliacaoAluno() == null && correcao_AvaliacaoAluno == null) || (avaliacaoAluno.getCorrecao_AvaliacaoAluno() != null && correcao_AvaliacaoAluno != null && avaliacaoAluno.getCorrecao_AvaliacaoAluno().equals(correcao_AvaliacaoAluno)))) {
					vector.add(avaliacaoAluno);
				}
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoAlunoPorCorrecao(correcao_AvaliacaoAluno);
			_cacheAvaliacaoAluno.adicionarCorrecao_AvaliacaoAluno(correcao_AvaliacaoAluno);
			adicionarAvaliacaoAlunoNaCache(vector);
		}
		return vector;
	}

	public Vector consultarAvaliacaoAlunoPorAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAvaliacaoAluno || _cacheAvaliacaoAluno.cacheAlunoCursoInstancia_AvaliacaoAluno(alunoCursoInstancia_AvaliacaoAluno)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAvaliacaoAluno);
			while(it.hasNext()) {
				AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) it.next();
				if(((avaliacaoAluno.getAlunoCursoInstancia_AvaliacaoAluno() == null && alunoCursoInstancia_AvaliacaoAluno == null) || (avaliacaoAluno.getAlunoCursoInstancia_AvaliacaoAluno() != null && alunoCursoInstancia_AvaliacaoAluno != null && avaliacaoAluno.getAlunoCursoInstancia_AvaliacaoAluno().equals(alunoCursoInstancia_AvaliacaoAluno)))) {
					vector.add(avaliacaoAluno);
				}
			}
		}
		else {
			vector = _repositorio.consultarAvaliacaoAlunoPorAlunoCursoInstancia(alunoCursoInstancia_AvaliacaoAluno);
			_cacheAvaliacaoAluno.adicionarAlunoCursoInstancia_AvaliacaoAluno(alunoCursoInstancia_AvaliacaoAluno);
			adicionarAvaliacaoAlunoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNotaEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, float nota) throws SQLException {
		_repositorio.atualizarNotaEmAvaliacaoAluno(avaliacaoAluno, nota);
	}

	public void atualizarNoTotalizadoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, String noTotalizado) throws SQLException {
		_repositorio.atualizarNoTotalizadoEmAvaliacaoAluno(avaliacaoAluno, noTotalizado);
	}

	public void atualizarAvaliacao_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, Avaliacao avaliacao_AvaliacaoAluno) throws SQLException {
		_repositorio.atualizarAvaliacao_AvaliacaoAlunoEmAvaliacaoAluno(avaliacaoAluno, avaliacao_AvaliacaoAluno);
	}

	public void atualizarCorrecao_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, Correcao correcao_AvaliacaoAluno) throws SQLException {
		_repositorio.atualizarCorrecao_AvaliacaoAlunoEmAvaliacaoAluno(avaliacaoAluno, correcao_AvaliacaoAluno);
	}

	public void atualizarAlunoCursoInstancia_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		_repositorio.atualizarAlunoCursoInstancia_AvaliacaoAlunoEmAvaliacaoAluno(avaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno);
	}

	public AvaliacaoAluno inserirAvaliacaoAluno(AvaliacaoAluno obj) throws SQLException {
		AvaliacaoAluno obj2 = _repositorio.inserirAvaliacaoAluno(obj);
		_treeAvaliacaoAluno.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public AvaliacaoAluno inserirAvaliacaoAluno(float nota, String noTotalizado, Avaliacao avaliacao_AvaliacaoAluno, Correcao correcao_AvaliacaoAluno, AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		AvaliacaoAluno avaliacaoAluno = _repositorio.inserirAvaliacaoAluno(nota, noTotalizado, avaliacao_AvaliacaoAluno, correcao_AvaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno);
		_treeAvaliacaoAluno.insert(avaliacaoAluno.getKey(), avaliacaoAluno);
		return avaliacaoAluno;
	}

	public void removerAvaliacaoAluno(AvaliacaoAluno obj) throws SQLException {
		removerAvaliacaoAluno(obj.getId());
	}

	public void removerAvaliacaoAluno(int id_avaliacaoaluno) throws SQLException {
		_repositorio.removerAvaliacaoAluno(id_avaliacaoaluno);
		_treeAvaliacaoAluno.delete(new AvaliacaoAlunoKey(id_avaliacaoaluno));
	}

	public void adicionarAvaliacaoAlunoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			AvaliacaoAluno avaliacaoAluno = (AvaliacaoAluno) vector.get(i);
			AvaliacaoAluno temp = (AvaliacaoAluno) _treeAvaliacaoAluno.search(avaliacaoAluno.getKey());
			if(temp == null) {
				_treeAvaliacaoAluno.insert(avaliacaoAluno.getKey(), avaliacaoAluno);
			}
			else {
				vector.set(i, avaliacaoAluno);
			}
		}
	}

	//////////////// entidade Prova ////////////////

	public Prova consultarProva(int id_prova) throws SQLException {
		ProvaKey key = new ProvaKey(id_prova);
		Prova prova = (Prova) _treeProva.search(key);
		if(prova == null) {
			prova = _repositorio.consultarProva(id_prova);
			_treeProva.insert(key, prova);
		}
		return prova;
	}

	public Vector consultarProva() throws SQLException {
		Vector vector = null;
		if(_localMode || _allProva) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeProva);
			while(it.hasNext()) {
				Prova prova = (Prova) it.next();
				vector.add(prova);
			}
		}
		else {
			vector = _repositorio.consultarProva();
			adicionarProvaNaCache(vector);
			_allProva = true;
		}
		return vector;
	}

	public Vector consultarProvaPorInstituicao(Instituicao instituicao_Prova) throws SQLException {
		Vector vector = null;
		if(_localMode || _allProva || _cacheProva.cacheInstituicao_Prova(instituicao_Prova)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeProva);
			while(it.hasNext()) {
				Prova prova = (Prova) it.next();
				if(((prova.getInstituicao_Prova() == null && instituicao_Prova == null) || (prova.getInstituicao_Prova() != null && instituicao_Prova != null && prova.getInstituicao_Prova().equals(instituicao_Prova)))) {
					vector.add(prova);
				}
			}
		}
		else {
			vector = _repositorio.consultarProvaPorInstituicao(instituicao_Prova);
			_cacheProva.adicionarInstituicao_Prova(instituicao_Prova);
			adicionarProvaNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmProva(Prova prova, String nome) throws SQLException {
		_repositorio.atualizarNomeEmProva(prova, nome);
	}

	public void atualizarFonteEmProva(Prova prova, String fonte) throws SQLException {
		_repositorio.atualizarFonteEmProva(prova, fonte);
	}

	public void atualizarIndiceEmProva(Prova prova, int indice) throws SQLException {
		_repositorio.atualizarIndiceEmProva(prova, indice);
	}

	public void atualizarInstituicao_ProvaEmProva(Prova prova, Instituicao instituicao_Prova) throws SQLException {
		_repositorio.atualizarInstituicao_ProvaEmProva(prova, instituicao_Prova);
	}

	public Prova inserirProva(Prova obj) throws SQLException {
		Prova obj2 = _repositorio.inserirProva(obj);
		_treeProva.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Prova inserirProva(String nome, String fonte, int indice, Instituicao instituicao_Prova) throws SQLException {
		Prova prova = _repositorio.inserirProva(nome, fonte, indice, instituicao_Prova);
		_treeProva.insert(prova.getKey(), prova);
		return prova;
	}

	public void removerProva(Prova obj) throws SQLException {
		removerProva(obj.getId());
	}

	public void removerProva(int id_prova) throws SQLException {
		_repositorio.removerProva(id_prova);
		_treeProva.delete(new ProvaKey(id_prova));
	}

	public void adicionarProvaNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Prova prova = (Prova) vector.get(i);
			Prova temp = (Prova) _treeProva.search(prova.getKey());
			if(temp == null) {
				_treeProva.insert(prova.getKey(), prova);
			}
			else {
				vector.set(i, prova);
			}
		}
	}

	//////////////// entidade Correcao ////////////////

	public Correcao consultarCorrecao(int id_correcao) throws SQLException {
		CorrecaoKey key = new CorrecaoKey(id_correcao);
		Correcao correcao = (Correcao) _treeCorrecao.search(key);
		if(correcao == null) {
			correcao = _repositorio.consultarCorrecao(id_correcao);
			_treeCorrecao.insert(key, correcao);
		}
		return correcao;
	}

	public Vector consultarCorrecao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allCorrecao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCorrecao);
			while(it.hasNext()) {
				Correcao correcao = (Correcao) it.next();
				vector.add(correcao);
			}
		}
		else {
			vector = _repositorio.consultarCorrecao();
			adicionarCorrecaoNaCache(vector);
			_allCorrecao = true;
		}
		return vector;
	}

	public Vector consultarCorrecaoPorAluno(Aluno aluno_Correcao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCorrecao || _cacheCorrecao.cacheAluno_Correcao(aluno_Correcao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCorrecao);
			while(it.hasNext()) {
				Correcao correcao = (Correcao) it.next();
				if(((correcao.getAluno_Correcao() == null && aluno_Correcao == null) || (correcao.getAluno_Correcao() != null && aluno_Correcao != null && correcao.getAluno_Correcao().equals(aluno_Correcao)))) {
					vector.add(correcao);
				}
			}
		}
		else {
			vector = _repositorio.consultarCorrecaoPorAluno(aluno_Correcao);
			_cacheCorrecao.adicionarAluno_Correcao(aluno_Correcao);
			adicionarCorrecaoNaCache(vector);
		}
		return vector;
	}

	public Vector consultarCorrecaoPorProva(Prova prova_Correcao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allCorrecao || _cacheCorrecao.cacheProva_Correcao(prova_Correcao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeCorrecao);
			while(it.hasNext()) {
				Correcao correcao = (Correcao) it.next();
				if(((correcao.getProva_Correcao() == null && prova_Correcao == null) || (correcao.getProva_Correcao() != null && prova_Correcao != null && correcao.getProva_Correcao().equals(prova_Correcao)))) {
					vector.add(correcao);
				}
			}
		}
		else {
			vector = _repositorio.consultarCorrecaoPorProva(prova_Correcao);
			_cacheCorrecao.adicionarProva_Correcao(prova_Correcao);
			adicionarCorrecaoNaCache(vector);
		}
		return vector;
	}

	public void atualizarMatrizEmCorrecao(Correcao correcao, String matriz) throws SQLException {
		_repositorio.atualizarMatrizEmCorrecao(correcao, matriz);
	}

	public void atualizarImagemEmCorrecao(Correcao correcao, String imagem) throws SQLException {
		_repositorio.atualizarImagemEmCorrecao(correcao, imagem);
	}

	public void atualizarNotaEmCorrecao(Correcao correcao, float nota) throws SQLException {
		_repositorio.atualizarNotaEmCorrecao(correcao, nota);
	}

	public void atualizarProva_CorrecaoEmCorrecao(Correcao correcao, Prova prova_Correcao) throws SQLException {
		_repositorio.atualizarProva_CorrecaoEmCorrecao(correcao, prova_Correcao);
	}

	public void atualizarAluno_CorrecaoEmCorrecao(Correcao correcao, Aluno aluno_Correcao) throws SQLException {
		_repositorio.atualizarAluno_CorrecaoEmCorrecao(correcao, aluno_Correcao);
	}

	public Correcao inserirCorrecao(Correcao obj) throws SQLException {
		Correcao obj2 = _repositorio.inserirCorrecao(obj);
		_treeCorrecao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public Correcao inserirCorrecao(String matriz, String imagem, float nota, Prova prova_Correcao, Aluno aluno_Correcao) throws SQLException {
		Correcao correcao = _repositorio.inserirCorrecao(matriz, imagem, nota, prova_Correcao, aluno_Correcao);
		_treeCorrecao.insert(correcao.getKey(), correcao);
		return correcao;
	}

	public void removerCorrecao(Correcao obj) throws SQLException {
		removerCorrecao(obj.getId());
	}

	public void removerCorrecao(int id_correcao) throws SQLException {
		_repositorio.removerCorrecao(id_correcao);
		_treeCorrecao.delete(new CorrecaoKey(id_correcao));
	}

	public void adicionarCorrecaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			Correcao correcao = (Correcao) vector.get(i);
			Correcao temp = (Correcao) _treeCorrecao.search(correcao.getKey());
			if(temp == null) {
				_treeCorrecao.insert(correcao.getKey(), correcao);
			}
			else {
				vector.set(i, correcao);
			}
		}
	}

	//////////////// entidade ProvaCorrecao ////////////////

	public ProvaCorrecao consultarProvaCorrecao(int id_provacorrecao) throws SQLException {
		ProvaCorrecaoKey key = new ProvaCorrecaoKey(id_provacorrecao);
		ProvaCorrecao provaCorrecao = (ProvaCorrecao) _treeProvaCorrecao.search(key);
		if(provaCorrecao == null) {
			provaCorrecao = _repositorio.consultarProvaCorrecao(id_provacorrecao);
			_treeProvaCorrecao.insert(key, provaCorrecao);
		}
		return provaCorrecao;
	}

	public Vector consultarProvaCorrecao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allProvaCorrecao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeProvaCorrecao);
			while(it.hasNext()) {
				ProvaCorrecao provaCorrecao = (ProvaCorrecao) it.next();
				vector.add(provaCorrecao);
			}
		}
		else {
			vector = _repositorio.consultarProvaCorrecao();
			adicionarProvaCorrecaoNaCache(vector);
			_allProvaCorrecao = true;
		}
		return vector;
	}

	public Vector consultarProvaCorrecaoPorProva(Prova prova_ProvaCorrecao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allProvaCorrecao || _cacheProvaCorrecao.cacheProva_ProvaCorrecao(prova_ProvaCorrecao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeProvaCorrecao);
			while(it.hasNext()) {
				ProvaCorrecao provaCorrecao = (ProvaCorrecao) it.next();
				if(((provaCorrecao.getProva_ProvaCorrecao() == null && prova_ProvaCorrecao == null) || (provaCorrecao.getProva_ProvaCorrecao() != null && prova_ProvaCorrecao != null && provaCorrecao.getProva_ProvaCorrecao().equals(prova_ProvaCorrecao)))) {
					vector.add(provaCorrecao);
				}
			}
		}
		else {
			vector = _repositorio.consultarProvaCorrecaoPorProva(prova_ProvaCorrecao);
			_cacheProvaCorrecao.adicionarProva_ProvaCorrecao(prova_ProvaCorrecao);
			adicionarProvaCorrecaoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmProvaCorrecao(ProvaCorrecao provaCorrecao, String nome) throws SQLException {
		_repositorio.atualizarNomeEmProvaCorrecao(provaCorrecao, nome);
	}

	public void atualizarProva_ProvaCorrecaoEmProvaCorrecao(ProvaCorrecao provaCorrecao, Prova prova_ProvaCorrecao) throws SQLException {
		_repositorio.atualizarProva_ProvaCorrecaoEmProvaCorrecao(provaCorrecao, prova_ProvaCorrecao);
	}

	public ProvaCorrecao inserirProvaCorrecao(ProvaCorrecao obj) throws SQLException {
		ProvaCorrecao obj2 = _repositorio.inserirProvaCorrecao(obj);
		_treeProvaCorrecao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public ProvaCorrecao inserirProvaCorrecao(String nome, Prova prova_ProvaCorrecao) throws SQLException {
		ProvaCorrecao provaCorrecao = _repositorio.inserirProvaCorrecao(nome, prova_ProvaCorrecao);
		_treeProvaCorrecao.insert(provaCorrecao.getKey(), provaCorrecao);
		return provaCorrecao;
	}

	public void removerProvaCorrecao(ProvaCorrecao obj) throws SQLException {
		removerProvaCorrecao(obj.getId());
	}

	public void removerProvaCorrecao(int id_provacorrecao) throws SQLException {
		_repositorio.removerProvaCorrecao(id_provacorrecao);
		_treeProvaCorrecao.delete(new ProvaCorrecaoKey(id_provacorrecao));
	}

	public void adicionarProvaCorrecaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			ProvaCorrecao provaCorrecao = (ProvaCorrecao) vector.get(i);
			ProvaCorrecao temp = (ProvaCorrecao) _treeProvaCorrecao.search(provaCorrecao.getKey());
			if(temp == null) {
				_treeProvaCorrecao.insert(provaCorrecao.getKey(), provaCorrecao);
			}
			else {
				vector.set(i, provaCorrecao);
			}
		}
	}

	//////////////// entidade AlunoProvaCorrecao ////////////////

	public AlunoProvaCorrecao consultarAlunoProvaCorrecao(int id_aluno, int id_provacorrecao) throws SQLException {
		AlunoProvaCorrecaoKey key = new AlunoProvaCorrecaoKey(id_aluno, id_provacorrecao);
		AlunoProvaCorrecao alunoProvaCorrecao = (AlunoProvaCorrecao) _treeAlunoProvaCorrecao.search(key);
		if(alunoProvaCorrecao == null) {
			alunoProvaCorrecao = _repositorio.consultarAlunoProvaCorrecao(id_aluno, id_provacorrecao);
			_treeAlunoProvaCorrecao.insert(key, alunoProvaCorrecao);
		}
		return alunoProvaCorrecao;
	}

	public Vector consultarAlunoProvaCorrecao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoProvaCorrecao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoProvaCorrecao);
			while(it.hasNext()) {
				AlunoProvaCorrecao alunoProvaCorrecao = (AlunoProvaCorrecao) it.next();
				vector.add(alunoProvaCorrecao);
			}
		}
		else {
			vector = _repositorio.consultarAlunoProvaCorrecao();
			adicionarAlunoProvaCorrecaoNaCache(vector);
			_allAlunoProvaCorrecao = true;
		}
		return vector;
	}

	public Vector consultarAlunoProvaCorrecaoPorProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allAlunoProvaCorrecao || _cacheAlunoProvaCorrecao.cacheProvaCorrecao_AlunoProvaCorrecao(provaCorrecao_AlunoProvaCorrecao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeAlunoProvaCorrecao);
			while(it.hasNext()) {
				AlunoProvaCorrecao alunoProvaCorrecao = (AlunoProvaCorrecao) it.next();
				if(((alunoProvaCorrecao.getProvaCorrecao_AlunoProvaCorrecao() == null && provaCorrecao_AlunoProvaCorrecao == null) || (alunoProvaCorrecao.getProvaCorrecao_AlunoProvaCorrecao() != null && provaCorrecao_AlunoProvaCorrecao != null && alunoProvaCorrecao.getProvaCorrecao_AlunoProvaCorrecao().equals(provaCorrecao_AlunoProvaCorrecao)))) {
					vector.add(alunoProvaCorrecao);
				}
			}
		}
		else {
			vector = _repositorio.consultarAlunoProvaCorrecaoPorProvaCorrecao(provaCorrecao_AlunoProvaCorrecao);
			_cacheAlunoProvaCorrecao.adicionarProvaCorrecao_AlunoProvaCorrecao(provaCorrecao_AlunoProvaCorrecao);
			adicionarAlunoProvaCorrecaoNaCache(vector);
		}
		return vector;
	}

	public void atualizarNumEntradasEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, int numEntradas) throws SQLException {
		_repositorio.atualizarNumEntradasEmAlunoProvaCorrecao(alunoProvaCorrecao, numEntradas);
	}

	public void atualizarAluno_AlunoProvaCorrecaoEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, Aluno aluno_AlunoProvaCorrecao) throws SQLException {
		_repositorio.atualizarAluno_AlunoProvaCorrecaoEmAlunoProvaCorrecao(alunoProvaCorrecao, aluno_AlunoProvaCorrecao);
	}

	public void atualizarProvaCorrecao_AlunoProvaCorrecaoEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		_repositorio.atualizarProvaCorrecao_AlunoProvaCorrecaoEmAlunoProvaCorrecao(alunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao);
	}

	public AlunoProvaCorrecao inserirAlunoProvaCorrecao(AlunoProvaCorrecao obj) throws SQLException {
		AlunoProvaCorrecao obj2 = _repositorio.inserirAlunoProvaCorrecao(obj);
		_treeAlunoProvaCorrecao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public AlunoProvaCorrecao inserirAlunoProvaCorrecao(int numEntradas, Aluno aluno_AlunoProvaCorrecao, ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		AlunoProvaCorrecao alunoProvaCorrecao = _repositorio.inserirAlunoProvaCorrecao(numEntradas, aluno_AlunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao);
		_treeAlunoProvaCorrecao.insert(alunoProvaCorrecao.getKey(), alunoProvaCorrecao);
		return alunoProvaCorrecao;
	}

	public void removerAlunoProvaCorrecao(AlunoProvaCorrecao obj) throws SQLException {
		removerAlunoProvaCorrecao(obj.getId_aluno(), obj.getId_provacorrecao());
	}

	public void removerAlunoProvaCorrecao(int id_aluno, int id_provacorrecao) throws SQLException {
		_repositorio.removerAlunoProvaCorrecao(id_aluno, id_provacorrecao);
		_treeAlunoProvaCorrecao.delete(new AlunoProvaCorrecaoKey(id_aluno, id_provacorrecao));
	}

	public void adicionarAlunoProvaCorrecaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			AlunoProvaCorrecao alunoProvaCorrecao = (AlunoProvaCorrecao) vector.get(i);
			AlunoProvaCorrecao temp = (AlunoProvaCorrecao) _treeAlunoProvaCorrecao.search(alunoProvaCorrecao.getKey());
			if(temp == null) {
				_treeAlunoProvaCorrecao.insert(alunoProvaCorrecao.getKey(), alunoProvaCorrecao);
			}
			else {
				vector.set(i, alunoProvaCorrecao);
			}
		}
	}

	//////////////// entidade EntradaProvaCorrecao ////////////////

	public EntradaProvaCorrecao consultarEntradaProvaCorrecao(int id_entradaprovacorrecao) throws SQLException {
		EntradaProvaCorrecaoKey key = new EntradaProvaCorrecaoKey(id_entradaprovacorrecao);
		EntradaProvaCorrecao entradaProvaCorrecao = (EntradaProvaCorrecao) _treeEntradaProvaCorrecao.search(key);
		if(entradaProvaCorrecao == null) {
			entradaProvaCorrecao = _repositorio.consultarEntradaProvaCorrecao(id_entradaprovacorrecao);
			_treeEntradaProvaCorrecao.insert(key, entradaProvaCorrecao);
		}
		return entradaProvaCorrecao;
	}

	public Vector consultarEntradaProvaCorrecao() throws SQLException {
		Vector vector = null;
		if(_localMode || _allEntradaProvaCorrecao) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeEntradaProvaCorrecao);
			while(it.hasNext()) {
				EntradaProvaCorrecao entradaProvaCorrecao = (EntradaProvaCorrecao) it.next();
				vector.add(entradaProvaCorrecao);
			}
		}
		else {
			vector = _repositorio.consultarEntradaProvaCorrecao();
			adicionarEntradaProvaCorrecaoNaCache(vector);
			_allEntradaProvaCorrecao = true;
		}
		return vector;
	}

	public Vector consultarEntradaProvaCorrecaoporProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) throws SQLException {
		Vector vector = null;
		if(_localMode || _allEntradaProvaCorrecao || _cacheEntradaProvaCorrecao.cacheProvaCorrecao_EntradaProvaCorrecao(provaCorrecao_EntradaProvaCorrecao)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeEntradaProvaCorrecao);
			while(it.hasNext()) {
				EntradaProvaCorrecao entradaProvaCorrecao = (EntradaProvaCorrecao) it.next();
				if(((entradaProvaCorrecao.getProvaCorrecao_EntradaProvaCorrecao() == null && provaCorrecao_EntradaProvaCorrecao == null) || (entradaProvaCorrecao.getProvaCorrecao_EntradaProvaCorrecao() != null && provaCorrecao_EntradaProvaCorrecao != null && entradaProvaCorrecao.getProvaCorrecao_EntradaProvaCorrecao().equals(provaCorrecao_EntradaProvaCorrecao)))) {
					vector.add(entradaProvaCorrecao);
				}
			}
		}
		else {
			vector = _repositorio.consultarEntradaProvaCorrecaoporProvaCorrecao(provaCorrecao_EntradaProvaCorrecao);
			_cacheEntradaProvaCorrecao.adicionarProvaCorrecao_EntradaProvaCorrecao(provaCorrecao_EntradaProvaCorrecao);
			adicionarEntradaProvaCorrecaoNaCache(vector);
		}
		return vector;
	}

	public void atualizarStatusEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, byte status) throws SQLException {
		_repositorio.atualizarStatusEmEntradaProvaCorrecao(entradaProvaCorrecao, status);
	}

	public void atualizarFotoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, String foto) throws SQLException {
		_repositorio.atualizarFotoEmEntradaProvaCorrecao(entradaProvaCorrecao, foto);
	}

	public void atualizarThresholdEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int threshold) throws SQLException {
		_repositorio.atualizarThresholdEmEntradaProvaCorrecao(entradaProvaCorrecao, threshold);
	}

	public void atualizarPhaseEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int phase) throws SQLException {
		_repositorio.atualizarPhaseEmEntradaProvaCorrecao(entradaProvaCorrecao, phase);
	}

	public void atualizarIdImageDataEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int idImageData) throws SQLException {
		_repositorio.atualizarIdImageDataEmEntradaProvaCorrecao(entradaProvaCorrecao, idImageData);
	}

	public void atualizarTipoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int tipo) throws SQLException {
		_repositorio.atualizarTipoEmEntradaProvaCorrecao(entradaProvaCorrecao, tipo);
	}

	public void atualizarIdAnswersDataEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int idAnswersData) throws SQLException {
		_repositorio.atualizarIdAnswersDataEmEntradaProvaCorrecao(entradaProvaCorrecao, idAnswersData);
	}

	public void atualizarProvaCorrecao_EntradaProvaCorrecaoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) throws SQLException {
		_repositorio.atualizarProvaCorrecao_EntradaProvaCorrecaoEmEntradaProvaCorrecao(entradaProvaCorrecao, provaCorrecao_EntradaProvaCorrecao);
	}

	public void atualizarAluno_EntradaProvaCorrecaoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, Aluno aluno_EntradaProvaCorrecao) throws SQLException {
		_repositorio.atualizarAluno_EntradaProvaCorrecaoEmEntradaProvaCorrecao(entradaProvaCorrecao, aluno_EntradaProvaCorrecao);
	}

	public EntradaProvaCorrecao inserirEntradaProvaCorrecao(EntradaProvaCorrecao obj) throws SQLException {
		EntradaProvaCorrecao obj2 = _repositorio.inserirEntradaProvaCorrecao(obj);
		_treeEntradaProvaCorrecao.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public EntradaProvaCorrecao inserirEntradaProvaCorrecao(byte status, String foto, int threshold, int phase, int idImageData, int tipo, int idAnswersData, ProvaCorrecao provaCorrecao_EntradaProvaCorrecao, Aluno aluno_EntradaProvaCorrecao) throws SQLException {
		EntradaProvaCorrecao entradaProvaCorrecao = _repositorio.inserirEntradaProvaCorrecao(status, foto, threshold, phase, idImageData, tipo, idAnswersData, provaCorrecao_EntradaProvaCorrecao, aluno_EntradaProvaCorrecao);
		_treeEntradaProvaCorrecao.insert(entradaProvaCorrecao.getKey(), entradaProvaCorrecao);
		return entradaProvaCorrecao;
	}

	public void removerEntradaProvaCorrecao(EntradaProvaCorrecao obj) throws SQLException {
		removerEntradaProvaCorrecao(obj.getId());
	}

	public void removerEntradaProvaCorrecao(int id_entradaprovacorrecao) throws SQLException {
		_repositorio.removerEntradaProvaCorrecao(id_entradaprovacorrecao);
		_treeEntradaProvaCorrecao.delete(new EntradaProvaCorrecaoKey(id_entradaprovacorrecao));
	}

	public void adicionarEntradaProvaCorrecaoNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			EntradaProvaCorrecao entradaProvaCorrecao = (EntradaProvaCorrecao) vector.get(i);
			EntradaProvaCorrecao temp = (EntradaProvaCorrecao) _treeEntradaProvaCorrecao.search(entradaProvaCorrecao.getKey());
			if(temp == null) {
				_treeEntradaProvaCorrecao.insert(entradaProvaCorrecao.getKey(), entradaProvaCorrecao);
			}
			else {
				vector.set(i, entradaProvaCorrecao);
			}
		}
	}

	//////////////// entidade ColetaQuestionario ////////////////

	public ColetaQuestionario consultarColetaQuestionario(int id_coletaquestionario) throws SQLException {
		ColetaQuestionarioKey key = new ColetaQuestionarioKey(id_coletaquestionario);
		ColetaQuestionario coletaQuestionario = (ColetaQuestionario) _treeColetaQuestionario.search(key);
		if(coletaQuestionario == null) {
			coletaQuestionario = _repositorio.consultarColetaQuestionario(id_coletaquestionario);
			_treeColetaQuestionario.insert(key, coletaQuestionario);
		}
		return coletaQuestionario;
	}

	public Vector consultarColetaQuestionario() throws SQLException {
		Vector vector = null;
		if(_localMode || _allColetaQuestionario) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeColetaQuestionario);
			while(it.hasNext()) {
				ColetaQuestionario coletaQuestionario = (ColetaQuestionario) it.next();
				vector.add(coletaQuestionario);
			}
		}
		else {
			vector = _repositorio.consultarColetaQuestionario();
			adicionarColetaQuestionarioNaCache(vector);
			_allColetaQuestionario = true;
		}
		return vector;
	}

	public Vector consultarColetaQuestionarioPorProva(Prova prova_ColetaQuestionario) throws SQLException {
		Vector vector = null;
		if(_localMode || _allColetaQuestionario || _cacheColetaQuestionario.cacheProva_ColetaQuestionario(prova_ColetaQuestionario)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeColetaQuestionario);
			while(it.hasNext()) {
				ColetaQuestionario coletaQuestionario = (ColetaQuestionario) it.next();
				if(((coletaQuestionario.getProva_ColetaQuestionario() == null && prova_ColetaQuestionario == null) || (coletaQuestionario.getProva_ColetaQuestionario() != null && prova_ColetaQuestionario != null && coletaQuestionario.getProva_ColetaQuestionario().equals(prova_ColetaQuestionario)))) {
					vector.add(coletaQuestionario);
				}
			}
		}
		else {
			vector = _repositorio.consultarColetaQuestionarioPorProva(prova_ColetaQuestionario);
			_cacheColetaQuestionario.adicionarProva_ColetaQuestionario(prova_ColetaQuestionario);
			adicionarColetaQuestionarioNaCache(vector);
		}
		return vector;
	}

	public void atualizarNomeEmColetaQuestionario(ColetaQuestionario coletaQuestionario, String nome) throws SQLException {
		_repositorio.atualizarNomeEmColetaQuestionario(coletaQuestionario, nome);
	}

	public void atualizarProva_ColetaQuestionarioEmColetaQuestionario(ColetaQuestionario coletaQuestionario, Prova prova_ColetaQuestionario) throws SQLException {
		_repositorio.atualizarProva_ColetaQuestionarioEmColetaQuestionario(coletaQuestionario, prova_ColetaQuestionario);
	}

	public ColetaQuestionario inserirColetaQuestionario(ColetaQuestionario obj) throws SQLException {
		ColetaQuestionario obj2 = _repositorio.inserirColetaQuestionario(obj);
		_treeColetaQuestionario.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public ColetaQuestionario inserirColetaQuestionario(String nome, Prova prova_ColetaQuestionario) throws SQLException {
		ColetaQuestionario coletaQuestionario = _repositorio.inserirColetaQuestionario(nome, prova_ColetaQuestionario);
		_treeColetaQuestionario.insert(coletaQuestionario.getKey(), coletaQuestionario);
		return coletaQuestionario;
	}

	public void removerColetaQuestionario(ColetaQuestionario obj) throws SQLException {
		removerColetaQuestionario(obj.getId());
	}

	public void removerColetaQuestionario(int id_coletaquestionario) throws SQLException {
		_repositorio.removerColetaQuestionario(id_coletaquestionario);
		_treeColetaQuestionario.delete(new ColetaQuestionarioKey(id_coletaquestionario));
	}

	public void adicionarColetaQuestionarioNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			ColetaQuestionario coletaQuestionario = (ColetaQuestionario) vector.get(i);
			ColetaQuestionario temp = (ColetaQuestionario) _treeColetaQuestionario.search(coletaQuestionario.getKey());
			if(temp == null) {
				_treeColetaQuestionario.insert(coletaQuestionario.getKey(), coletaQuestionario);
			}
			else {
				vector.set(i, coletaQuestionario);
			}
		}
	}

	//////////////// entidade EntradaColetaQuestionario ////////////////

	public EntradaColetaQuestionario consultarEntradaColetaQuestionario(int id_entradacoletaquestionario) throws SQLException {
		EntradaColetaQuestionarioKey key = new EntradaColetaQuestionarioKey(id_entradacoletaquestionario);
		EntradaColetaQuestionario entradaColetaQuestionario = (EntradaColetaQuestionario) _treeEntradaColetaQuestionario.search(key);
		if(entradaColetaQuestionario == null) {
			entradaColetaQuestionario = _repositorio.consultarEntradaColetaQuestionario(id_entradacoletaquestionario);
			_treeEntradaColetaQuestionario.insert(key, entradaColetaQuestionario);
		}
		return entradaColetaQuestionario;
	}

	public Vector consultarEntradaColetaQuestionario() throws SQLException {
		Vector vector = null;
		if(_localMode || _allEntradaColetaQuestionario) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeEntradaColetaQuestionario);
			while(it.hasNext()) {
				EntradaColetaQuestionario entradaColetaQuestionario = (EntradaColetaQuestionario) it.next();
				vector.add(entradaColetaQuestionario);
			}
		}
		else {
			vector = _repositorio.consultarEntradaColetaQuestionario();
			adicionarEntradaColetaQuestionarioNaCache(vector);
			_allEntradaColetaQuestionario = true;
		}
		return vector;
	}

	public Vector consultarEntradaColetaQuestionarioPorColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		Vector vector = null;
		if(_localMode || _allEntradaColetaQuestionario || _cacheEntradaColetaQuestionario.cacheColetaQuestionario_EntradaColetaQuestionario(coletaQuestionario_EntradaColetaQuestionario)) {
			vector = new Vector();
			RBTreeIterator it = new RBTreeIterator(_treeEntradaColetaQuestionario);
			while(it.hasNext()) {
				EntradaColetaQuestionario entradaColetaQuestionario = (EntradaColetaQuestionario) it.next();
				if(((entradaColetaQuestionario.getColetaQuestionario_EntradaColetaQuestionario() == null && coletaQuestionario_EntradaColetaQuestionario == null) || (entradaColetaQuestionario.getColetaQuestionario_EntradaColetaQuestionario() != null && coletaQuestionario_EntradaColetaQuestionario != null && entradaColetaQuestionario.getColetaQuestionario_EntradaColetaQuestionario().equals(coletaQuestionario_EntradaColetaQuestionario)))) {
					vector.add(entradaColetaQuestionario);
				}
			}
		}
		else {
			vector = _repositorio.consultarEntradaColetaQuestionarioPorColetaQuestionario(coletaQuestionario_EntradaColetaQuestionario);
			_cacheEntradaColetaQuestionario.adicionarColetaQuestionario_EntradaColetaQuestionario(coletaQuestionario_EntradaColetaQuestionario);
			adicionarEntradaColetaQuestionarioNaCache(vector);
		}
		return vector;
	}

	public void atualizarStatusEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, byte status) throws SQLException {
		_repositorio.atualizarStatusEmEntradaColetaQuestionario(entradaColetaQuestionario, status);
	}

	public void atualizarFotoEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, String foto) throws SQLException {
		_repositorio.atualizarFotoEmEntradaColetaQuestionario(entradaColetaQuestionario, foto);
	}

	public void atualizarThresholdEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int threshold) throws SQLException {
		_repositorio.atualizarThresholdEmEntradaColetaQuestionario(entradaColetaQuestionario, threshold);
	}

	public void atualizarPhaseEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int phase) throws SQLException {
		_repositorio.atualizarPhaseEmEntradaColetaQuestionario(entradaColetaQuestionario, phase);
	}

	public void atualizarIdImageDataEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int idImageData) throws SQLException {
		_repositorio.atualizarIdImageDataEmEntradaColetaQuestionario(entradaColetaQuestionario, idImageData);
	}

	public void atualizarTipoFolhaRespostaEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int tipoFolhaResposta) throws SQLException {
		_repositorio.atualizarTipoFolhaRespostaEmEntradaColetaQuestionario(entradaColetaQuestionario, tipoFolhaResposta);
	}

	public void atualizarIdAnswersDataEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int idAnswersData) throws SQLException {
		_repositorio.atualizarIdAnswersDataEmEntradaColetaQuestionario(entradaColetaQuestionario, idAnswersData);
	}

	public void atualizarColetaQuestionario_EntradaColetaQuestionarioEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		_repositorio.atualizarColetaQuestionario_EntradaColetaQuestionarioEmEntradaColetaQuestionario(entradaColetaQuestionario, coletaQuestionario_EntradaColetaQuestionario);
	}

	public EntradaColetaQuestionario inserirEntradaColetaQuestionario(EntradaColetaQuestionario obj) throws SQLException {
		EntradaColetaQuestionario obj2 = _repositorio.inserirEntradaColetaQuestionario(obj);
		_treeEntradaColetaQuestionario.insert(obj2.getKey(), obj2);
		return obj2;
	}

	public EntradaColetaQuestionario inserirEntradaColetaQuestionario(byte status, String foto, int threshold, int phase, int idImageData, int tipoFolhaResposta, int idAnswersData, ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		EntradaColetaQuestionario entradaColetaQuestionario = _repositorio.inserirEntradaColetaQuestionario(status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, coletaQuestionario_EntradaColetaQuestionario);
		_treeEntradaColetaQuestionario.insert(entradaColetaQuestionario.getKey(), entradaColetaQuestionario);
		return entradaColetaQuestionario;
	}

	public void removerEntradaColetaQuestionario(EntradaColetaQuestionario obj) throws SQLException {
		removerEntradaColetaQuestionario(obj.getId());
	}

	public void removerEntradaColetaQuestionario(int id_entradacoletaquestionario) throws SQLException {
		_repositorio.removerEntradaColetaQuestionario(id_entradacoletaquestionario);
		_treeEntradaColetaQuestionario.delete(new EntradaColetaQuestionarioKey(id_entradacoletaquestionario));
	}

	public void adicionarEntradaColetaQuestionarioNaCache(Vector vector) {
		for(int i = 0; i < vector.size(); i++) {
			EntradaColetaQuestionario entradaColetaQuestionario = (EntradaColetaQuestionario) vector.get(i);
			EntradaColetaQuestionario temp = (EntradaColetaQuestionario) _treeEntradaColetaQuestionario.search(entradaColetaQuestionario.getKey());
			if(temp == null) {
				_treeEntradaColetaQuestionario.insert(entradaColetaQuestionario.getKey(), entradaColetaQuestionario);
			}
			else {
				vector.set(i, entradaColetaQuestionario);
			}
		}
	}
}
