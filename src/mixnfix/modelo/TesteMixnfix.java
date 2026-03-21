package mixnfix.modelo;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import mixnfix.modelo.persistencia.RepositorioBD;
import mixnfix.modelo.persistencia.RepositorioCache;

public class TesteMixnfix {
	private RepositorioCache _rep;
	private RepositorioBD _rbd;
	private Random _rng;

	// Número de registros gerados para cada entidade.
	private int _numRegs;

	// Registros de Instituicao.
	private Vector _conjuntoInstituicao;

	// Registros de Periodo.
	private Vector _conjuntoPeriodo;

	// Registros de Turma.
	private Vector _conjuntoTurma;

	// Registros de Aluno.
	private Vector _conjuntoAluno;

	// Registros de Curso.
	private Vector _conjuntoCurso;

	// Registros de AlunoTurma.
	private Vector _conjuntoAlunoTurma;

	// Registros de CursoInstancia.
	private Vector _conjuntoCursoInstancia;

	// Registros de AlunoCursoInstancia.
	private Vector _conjuntoAlunoCursoInstancia;

	// Registros de Avaliacao.
	private Vector _conjuntoAvaliacao;

	// Registros de AvaliacaoAluno.
	private Vector _conjuntoAvaliacaoAluno;

	// Registros de Prova.
	private Vector _conjuntoProva;

	// Registros de Correcao.
	private Vector _conjuntoCorrecao;

	// Registros de ProvaCorrecao.
	private Vector _conjuntoProvaCorrecao;

	// Registros de AlunoProvaCorrecao.
	private Vector _conjuntoAlunoProvaCorrecao;

	// Registros de EntradaProvaCorrecao.
	private Vector _conjuntoEntradaProvaCorrecao;

	// Registros de ColetaQuestionario.
	private Vector _conjuntoColetaQuestionario;

	// Registros de EntradaColetaQuestionario.
	private Vector _conjuntoEntradaColetaQuestionario;

	public TesteMixnfix() {
		_numRegs = 100;
		_rng = new Random();
	}

	public void inicializar() throws Exception {
		// inicialização do repositório
		RepositorioCache rc = new RepositorioCache();
		RepositorioBD rbd = new RepositorioBD();
		rc.setRepositorio(rbd);
		rbd.setRepositorio(rc);
		RepositorioLink.init(rc);
		_rep = rc;
		_rbd = rbd;

		// inicializaçao do driver
//		new JDCConnectionDriver(
//			"com.mysql.jdbc.Driver",
//			"jdbc:mysql://localhost/mixnfix",
//			"root",
//			""
//		);
	}

	private String nextString(int tamanho) {
		StringBuffer b = new StringBuffer();
		for(int i = 0; i < tamanho; i++) {
			int ic = Math.abs(_rng.nextInt()) % 26;
			char c = (char) ('a' + ic);
			b.append(c);
		}
		return b.toString();
	}

	private Long nextTimestamp() {
		return new Long(System.currentTimeMillis() + Math.abs(_rng.nextLong()) % (10 * 365 * 24 * 60 * 60 * 1000L));
	}

	private Long nextDate() {
		Calendar c = Calendar.getInstance();
		c.clear();

		int d = Math.abs(_rng.nextInt()) % 365;
		int y = 1970 + Math.abs(_rng.nextInt()) % 40;
		c.set(Calendar.YEAR, y);
		c.set(Calendar.DAY_OF_YEAR, d);

		return new Long(c.getTime().getTime());
	}

	private void construirInstituicao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoInstituicao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_instituicao = _rng.nextInt();
			String nome = nextString(10);

			Instituicao obj = _rep.inserirInstituicao(nome);
			_conjuntoInstituicao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirInstituicao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirPeriodo() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoPeriodo = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			String id_periodo = nextString(10);

			Periodo obj = _rep.inserirPeriodo(id_periodo);
			_conjuntoPeriodo.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirPeriodo " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoTurma = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_turma = _rng.nextInt();
			String nome = nextString(10);
			Instituicao instituicao_Turma = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);

			Turma obj = _rep.inserirTurma(nome, instituicao_Turma);
			_conjuntoTurma.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirTurma " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAluno = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_aluno = _rng.nextInt();
			String nome = nextString(10);
			String matricula = nextString(10);
			Instituicao instituicao_Aluno = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);

			Aluno obj = _rep.inserirAluno(nome, matricula, instituicao_Aluno);
			_conjuntoAluno.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAluno " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirCurso() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoCurso = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_curso = _rng.nextInt();
			String nome = nextString(10);
			Instituicao instituicao_Curso = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);

			Curso obj = _rep.inserirCurso(nome, instituicao_Curso);
			_conjuntoCurso.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirCurso " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAlunoTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAlunoTurma = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			Aluno aluno_AlunoTurma = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			Turma turma_AlunoTurma = (Turma) _conjuntoTurma.get(Math.abs(_rng.nextInt()) % _numRegs);

			AlunoTurma obj = _rep.inserirAlunoTurma(aluno_AlunoTurma, turma_AlunoTurma);
			_conjuntoAlunoTurma.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAlunoTurma " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoCursoInstancia = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_cursoinstancia = _rng.nextInt();
			Instituicao instituicao_CursoInstancia = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			Curso curso_CursoInstancia = (Curso) _conjuntoCurso.get(Math.abs(_rng.nextInt()) % _numRegs);
			Periodo periodo_CursoInstancia = (Periodo) _conjuntoPeriodo.get(Math.abs(_rng.nextInt()) % _numRegs);

			CursoInstancia obj = _rep.inserirCursoInstancia(instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia);
			_conjuntoCursoInstancia.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirCursoInstancia " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAlunoCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAlunoCursoInstancia = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_alunocursoinstancia = _rng.nextInt();
			CursoInstancia cursoInstancia_AlunoCursoInstancia = (CursoInstancia) _conjuntoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);
			Aluno aluno_AlunoCursoInstancia = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);

			AlunoCursoInstancia obj = _rep.inserirAlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia, aluno_AlunoCursoInstancia);
			_conjuntoAlunoCursoInstancia.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAlunoCursoInstancia " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAvaliacao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAvaliacao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_avaliacao = _rng.nextInt();
			String nome = nextString(10);
			String tipo = nextString(10);
			CursoInstancia cursoInstancia_Avaliacao = (CursoInstancia) _conjuntoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);

			Avaliacao obj = _rep.inserirAvaliacao(nome, tipo, cursoInstancia_Avaliacao);
			_conjuntoAvaliacao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAvaliacao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAvaliacaoAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAvaliacaoAluno = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_avaliacaoaluno = _rng.nextInt();
			float nota = _rng.nextFloat();
			String noTotalizado = nextString(10);
			Avaliacao avaliacao_AvaliacaoAluno = (Avaliacao) _conjuntoAvaliacao.get(Math.abs(_rng.nextInt()) % _numRegs);
			Correcao correcao_AvaliacaoAluno = (Correcao) _conjuntoCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);
			AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno = (AlunoCursoInstancia) _conjuntoAlunoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);

			AvaliacaoAluno obj = _rep.inserirAvaliacaoAluno(nota, noTotalizado, avaliacao_AvaliacaoAluno, correcao_AvaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno);
			_conjuntoAvaliacaoAluno.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAvaliacaoAluno " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirProva() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoProva = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_prova = _rng.nextInt();
			String nome = nextString(10);
			String fonte = nextString(10);
			int indice = _rng.nextInt();
			Instituicao instituicao_Prova = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);

			Prova obj = _rep.inserirProva(nome, fonte, indice, instituicao_Prova);
			_conjuntoProva.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirProva " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoCorrecao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_correcao = _rng.nextInt();
			String matriz = nextString(10);
			String imagem = nextString(10);
			float nota = _rng.nextFloat();
			Prova prova_Correcao = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);
			Aluno aluno_Correcao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);

			Correcao obj = _rep.inserirCorrecao(matriz, imagem, nota, prova_Correcao, aluno_Correcao);
			_conjuntoCorrecao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoProvaCorrecao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_provacorrecao = _rng.nextInt();
			String nome = nextString(10);
			Prova prova_ProvaCorrecao = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);

			ProvaCorrecao obj = _rep.inserirProvaCorrecao(nome, prova_ProvaCorrecao);
			_conjuntoProvaCorrecao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirAlunoProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoAlunoProvaCorrecao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int numEntradas = _rng.nextInt();
			Aluno aluno_AlunoProvaCorrecao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			ProvaCorrecao provaCorrecao_AlunoProvaCorrecao = (ProvaCorrecao) _conjuntoProvaCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);

			AlunoProvaCorrecao obj = _rep.inserirAlunoProvaCorrecao(numEntradas, aluno_AlunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao);
			_conjuntoAlunoProvaCorrecao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirAlunoProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirEntradaProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoEntradaProvaCorrecao = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_entradaprovacorrecao = _rng.nextInt();
			byte status = (byte) _rng.nextInt();
			String foto = nextString(10);
			int threshold = _rng.nextInt();
			int phase = _rng.nextInt();
			int idImageData = _rng.nextInt();
			int tipo = _rng.nextInt();
			int idAnswersData = _rng.nextInt();
			ProvaCorrecao provaCorrecao_EntradaProvaCorrecao = (ProvaCorrecao) _conjuntoProvaCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);
			Aluno aluno_EntradaProvaCorrecao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);

			EntradaProvaCorrecao obj = _rep.inserirEntradaProvaCorrecao(status, foto, threshold, phase, idImageData, tipo, idAnswersData, provaCorrecao_EntradaProvaCorrecao, aluno_EntradaProvaCorrecao);
			_conjuntoEntradaProvaCorrecao.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirEntradaProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoColetaQuestionario = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_coletaquestionario = _rng.nextInt();
			String nome = nextString(10);
			Prova prova_ColetaQuestionario = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);

			ColetaQuestionario obj = _rep.inserirColetaQuestionario(nome, prova_ColetaQuestionario);
			_conjuntoColetaQuestionario.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirColetaQuestionario " + ((double) (tf - ti) / 1000) + "s");
	}

	private void construirEntradaColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		_conjuntoEntradaColetaQuestionario = new Vector();

		for(int i = 0; i < _numRegs; i++) {
			int id_entradacoletaquestionario = _rng.nextInt();
			byte status = (byte) _rng.nextInt();
			String foto = nextString(10);
			int threshold = _rng.nextInt();
			int phase = _rng.nextInt();
			int idImageData = _rng.nextInt();
			int tipoFolhaResposta = _rng.nextInt();
			int idAnswersData = _rng.nextInt();
			ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario = (ColetaQuestionario) _conjuntoColetaQuestionario.get(Math.abs(_rng.nextInt()) % _numRegs);

			EntradaColetaQuestionario obj = _rep.inserirEntradaColetaQuestionario(status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, coletaQuestionario_EntradaColetaQuestionario);
			_conjuntoEntradaColetaQuestionario.add(obj);
		}
		long tf = System.currentTimeMillis();
		System.out.println("construirEntradaColetaQuestionario " + ((double) (tf - ti) / 1000) + "s");
	}

	private boolean compararDate(Long l1, Long l2, String campo) {
		boolean eq = true;
		java.sql.Date d1 = new java.sql.Date(l1.longValue());
		java.sql.Date d2 = new java.sql.Date(l2.longValue());
		if(!d1.toString().equals(d2.toString())) {
			System.out.println("obj.get" + campo + "(): " + d1);
			System.out.println("obj2.get" + campo + "(): " + d2);
			System.out.println();
			eq = false;
		}
		return eq;
	}

	public static final double ERRO_MAX_DOUBLE = 10e-12;

	private boolean compararDouble(double d1, double d2, String campo) {
		boolean eq = false;
		if(Math.abs(d1  - d2) < ERRO_MAX_DOUBLE) {
			eq = true;
		}
		else {
			System.out.println("obj.get" + campo + "(): " + d1);
			System.out.println("obj2.get" + campo + "(): " + d2);
			eq = false;
		}
		return eq;
	}

	public static final float ERRO_MAX_FLOAT = (float) 10e-6;

	private boolean compararFloat(float f1, float f2, String campo) {
		boolean eq = false;
		if(Math.abs(f1  - f2) < ERRO_MAX_FLOAT) {
			eq = true;
		}
		else {
			System.out.println("obj.get" + campo + "(): " + f1);
			System.out.println("obj2.get" + campo + "(): " + f2);
			eq = false;
		}
		return eq;
	}
	private void consultarInstituicao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoInstituicao.size(); i++) {
			Instituicao obj = (Instituicao) _conjuntoInstituicao.get(i);
			Instituicao obj2 = _rep.consultarInstituicao(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarInstituicao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarPeriodo() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoPeriodo.size(); i++) {
			Periodo obj = (Periodo) _conjuntoPeriodo.get(i);
			Periodo obj2 = _rep.consultarPeriodo(obj.getId_periodo());

			if(obj2 != null) {
				if(obj2.getId_periodo() != null) {
					if(!obj.getId_periodo().equals(obj2.getId_periodo())) {
						System.out.println("obj.getId_periodo(): " + obj.getId_periodo());
						System.out.println("obj2.getId_periodo(): " + obj2.getId_periodo());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getId_periodo() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarPeriodo ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoTurma.size(); i++) {
			Turma obj = (Turma) _conjuntoTurma.get(i);
			Turma obj2 = _rep.consultarTurma(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarTurma ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAluno.size(); i++) {
			Aluno obj = (Aluno) _conjuntoAluno.get(i);
			Aluno obj2 = _rep.consultarAluno(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
				if(obj2.getMatricula() != null) {
					if(!obj.getMatricula().equals(obj2.getMatricula())) {
						System.out.println("obj.getMatricula(): " + obj.getMatricula());
						System.out.println("obj2.getMatricula(): " + obj2.getMatricula());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getMatricula() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAluno ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarCurso() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoCurso.size(); i++) {
			Curso obj = (Curso) _conjuntoCurso.get(i);
			Curso obj2 = _rep.consultarCurso(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarCurso ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAlunoTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAlunoTurma.size(); i++) {
			AlunoTurma obj = (AlunoTurma) _conjuntoAlunoTurma.get(i);
			AlunoTurma obj2 = _rep.consultarAlunoTurma(obj.getId_aluno(), obj.getId_turma());

			if(obj2 != null) {
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAlunoTurma ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoCursoInstancia.size(); i++) {
			CursoInstancia obj = (CursoInstancia) _conjuntoCursoInstancia.get(i);
			CursoInstancia obj2 = _rep.consultarCursoInstancia(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarCursoInstancia ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAlunoCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAlunoCursoInstancia.size(); i++) {
			AlunoCursoInstancia obj = (AlunoCursoInstancia) _conjuntoAlunoCursoInstancia.get(i);
			AlunoCursoInstancia obj2 = _rep.consultarAlunoCursoInstancia(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAlunoCursoInstancia ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAvaliacao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAvaliacao.size(); i++) {
			Avaliacao obj = (Avaliacao) _conjuntoAvaliacao.get(i);
			Avaliacao obj2 = _rep.consultarAvaliacao(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
				if(obj2.getTipo() != null) {
					if(!obj.getTipo().equals(obj2.getTipo())) {
						System.out.println("obj.getTipo(): " + obj.getTipo());
						System.out.println("obj2.getTipo(): " + obj2.getTipo());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getTipo() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAvaliacao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAvaliacaoAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAvaliacaoAluno.size(); i++) {
			AvaliacaoAluno obj = (AvaliacaoAluno) _conjuntoAvaliacaoAluno.get(i);
			AvaliacaoAluno obj2 = _rep.consultarAvaliacaoAluno(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(!compararFloat(obj.getNota(), obj2.getNota(), "Nota")) {
					ok = false;
				}
				if(obj2.getNoTotalizado() != null) {
					if(!obj.getNoTotalizado().equals(obj2.getNoTotalizado())) {
						System.out.println("obj.getNoTotalizado(): " + obj.getNoTotalizado());
						System.out.println("obj2.getNoTotalizado(): " + obj2.getNoTotalizado());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNoTotalizado() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAvaliacaoAluno ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarProva() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoProva.size(); i++) {
			Prova obj = (Prova) _conjuntoProva.get(i);
			Prova obj2 = _rep.consultarProva(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
				if(obj2.getFonte() != null) {
					if(!obj.getFonte().equals(obj2.getFonte())) {
						System.out.println("obj.getFonte(): " + obj.getFonte());
						System.out.println("obj2.getFonte(): " + obj2.getFonte());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getFonte() == null");
					ok = false;
				}
				if(obj.getIndice() != obj2.getIndice()) {
					System.out.println("obj.getIndice(): " + obj.getIndice());
					System.out.println("obj2.getIndice(): " + obj2.getIndice());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarProva ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoCorrecao.size(); i++) {
			Correcao obj = (Correcao) _conjuntoCorrecao.get(i);
			Correcao obj2 = _rep.consultarCorrecao(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getMatriz() != null) {
					if(!obj.getMatriz().equals(obj2.getMatriz())) {
						System.out.println("obj.getMatriz(): " + obj.getMatriz());
						System.out.println("obj2.getMatriz(): " + obj2.getMatriz());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getMatriz() == null");
					ok = false;
				}
				if(obj2.getImagem() != null) {
					if(!obj.getImagem().equals(obj2.getImagem())) {
						System.out.println("obj.getImagem(): " + obj.getImagem());
						System.out.println("obj2.getImagem(): " + obj2.getImagem());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getImagem() == null");
					ok = false;
				}
				if(!compararFloat(obj.getNota(), obj2.getNota(), "Nota")) {
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoProvaCorrecao.size(); i++) {
			ProvaCorrecao obj = (ProvaCorrecao) _conjuntoProvaCorrecao.get(i);
			ProvaCorrecao obj2 = _rep.consultarProvaCorrecao(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarAlunoProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoAlunoProvaCorrecao.size(); i++) {
			AlunoProvaCorrecao obj = (AlunoProvaCorrecao) _conjuntoAlunoProvaCorrecao.get(i);
			AlunoProvaCorrecao obj2 = _rep.consultarAlunoProvaCorrecao(obj.getId_aluno(), obj.getId_provacorrecao());

			if(obj2 != null) {
				if(obj.getNumEntradas() != obj2.getNumEntradas()) {
					System.out.println("obj.getNumEntradas(): " + obj.getNumEntradas());
					System.out.println("obj2.getNumEntradas(): " + obj2.getNumEntradas());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarAlunoProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarEntradaProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoEntradaProvaCorrecao.size(); i++) {
			EntradaProvaCorrecao obj = (EntradaProvaCorrecao) _conjuntoEntradaProvaCorrecao.get(i);
			EntradaProvaCorrecao obj2 = _rep.consultarEntradaProvaCorrecao(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj.getStatus() != obj2.getStatus()) {
					System.out.println("obj.getStatus(): " + obj.getStatus());
					System.out.println("obj2.getStatus(): " + obj2.getStatus());
					System.out.println();
					ok = false;
				}
				if(obj2.getFoto() != null) {
					if(!obj.getFoto().equals(obj2.getFoto())) {
						System.out.println("obj.getFoto(): " + obj.getFoto());
						System.out.println("obj2.getFoto(): " + obj2.getFoto());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getFoto() == null");
					ok = false;
				}
				if(obj.getThreshold() != obj2.getThreshold()) {
					System.out.println("obj.getThreshold(): " + obj.getThreshold());
					System.out.println("obj2.getThreshold(): " + obj2.getThreshold());
					System.out.println();
					ok = false;
				}
				if(obj.getPhase() != obj2.getPhase()) {
					System.out.println("obj.getPhase(): " + obj.getPhase());
					System.out.println("obj2.getPhase(): " + obj2.getPhase());
					System.out.println();
					ok = false;
				}
				if(obj.getIdImageData() != obj2.getIdImageData()) {
					System.out.println("obj.getIdImageData(): " + obj.getIdImageData());
					System.out.println("obj2.getIdImageData(): " + obj2.getIdImageData());
					System.out.println();
					ok = false;
				}
				if(obj.getTipo() != obj2.getTipo()) {
					System.out.println("obj.getTipo(): " + obj.getTipo());
					System.out.println("obj2.getTipo(): " + obj2.getTipo());
					System.out.println();
					ok = false;
				}
				if(obj.getIdAnswersData() != obj2.getIdAnswersData()) {
					System.out.println("obj.getIdAnswersData(): " + obj.getIdAnswersData());
					System.out.println("obj2.getIdAnswersData(): " + obj2.getIdAnswersData());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarEntradaProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoColetaQuestionario.size(); i++) {
			ColetaQuestionario obj = (ColetaQuestionario) _conjuntoColetaQuestionario.get(i);
			ColetaQuestionario obj2 = _rep.consultarColetaQuestionario(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj2.getNome() != null) {
					if(!obj.getNome().equals(obj2.getNome())) {
						System.out.println("obj.getNome(): " + obj.getNome());
						System.out.println("obj2.getNome(): " + obj2.getNome());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getNome() == null");
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarColetaQuestionario ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	private void consultarEntradaColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		boolean ok = true;

		for(int i = 0; ok && i < _conjuntoEntradaColetaQuestionario.size(); i++) {
			EntradaColetaQuestionario obj = (EntradaColetaQuestionario) _conjuntoEntradaColetaQuestionario.get(i);
			EntradaColetaQuestionario obj2 = _rep.consultarEntradaColetaQuestionario(obj.getId());

			if(obj2 != null) {
				if(obj.getId() != obj2.getId()) {
					System.out.println("obj.getId(): " + obj.getId());
					System.out.println("obj2.getId(): " + obj2.getId());
					System.out.println();
					ok = false;
				}
				if(obj.getStatus() != obj2.getStatus()) {
					System.out.println("obj.getStatus(): " + obj.getStatus());
					System.out.println("obj2.getStatus(): " + obj2.getStatus());
					System.out.println();
					ok = false;
				}
				if(obj2.getFoto() != null) {
					if(!obj.getFoto().equals(obj2.getFoto())) {
						System.out.println("obj.getFoto(): " + obj.getFoto());
						System.out.println("obj2.getFoto(): " + obj2.getFoto());
						System.out.println();
						ok = false;
					}
				}
				else {
					System.out.println("obj2.getFoto() == null");
					ok = false;
				}
				if(obj.getThreshold() != obj2.getThreshold()) {
					System.out.println("obj.getThreshold(): " + obj.getThreshold());
					System.out.println("obj2.getThreshold(): " + obj2.getThreshold());
					System.out.println();
					ok = false;
				}
				if(obj.getPhase() != obj2.getPhase()) {
					System.out.println("obj.getPhase(): " + obj.getPhase());
					System.out.println("obj2.getPhase(): " + obj2.getPhase());
					System.out.println();
					ok = false;
				}
				if(obj.getIdImageData() != obj2.getIdImageData()) {
					System.out.println("obj.getIdImageData(): " + obj.getIdImageData());
					System.out.println("obj2.getIdImageData(): " + obj2.getIdImageData());
					System.out.println();
					ok = false;
				}
				if(obj.getTipoFolhaResposta() != obj2.getTipoFolhaResposta()) {
					System.out.println("obj.getTipoFolhaResposta(): " + obj.getTipoFolhaResposta());
					System.out.println("obj2.getTipoFolhaResposta(): " + obj2.getTipoFolhaResposta());
					System.out.println();
					ok = false;
				}
				if(obj.getIdAnswersData() != obj2.getIdAnswersData()) {
					System.out.println("obj.getIdAnswersData(): " + obj.getIdAnswersData());
					System.out.println("obj2.getIdAnswersData(): " + obj2.getIdAnswersData());
					System.out.println();
					ok = false;
				}
			}
			else {
				System.out.println("obj2 == null");
				ok = false;
			}
		}

		if(ok) {
			long tf = System.currentTimeMillis();
			System.out.println("consultarEntradaColetaQuestionario ok " + ((double) (tf - ti) / 1000) + "s");
		}
	}

	interface Filter {
		public boolean matches(Object obj);
	}

	private Vector applyFilter(Vector v, Filter f) {
		Vector vf = new Vector();
		Iterator it = v.iterator();
		while(it.hasNext()) {
			Object obj = it.next();
			if(f.matches(obj)) {
				vf.add(obj);
			}
		}
		return vf;
	}

	class FilterConsultarTurmaPorInstituicaoNome implements Filter {
		private Turma _obj;
		public FilterConsultarTurmaPorInstituicaoNome(Turma obj) {
			_obj = obj;
		}
		public boolean matches(Object o) {
			Turma obj2 = (Turma) o;
			boolean m =
				 _obj.getNome().equals(obj2.getNome())
				 && _obj.getInstituicao_Turma().equals(obj2.getInstituicao_Turma());
			return m;
		}
	}

	public void testeConsultarTurmaPorInstituicaoNome() throws SQLException {
		Turma obj = (Turma) _conjuntoTurma.get(Math.abs(_rng.nextInt()) % _numRegs);

		Filter f = new FilterConsultarTurmaPorInstituicaoNome(obj);
		Vector v1 = _rep.consultarTurmaPorInstituicaoNome(obj.getNome(), obj.getInstituicao_Turma());
		Vector v2 = applyFilter(_conjuntoTurma, f);
		if(v1.containsAll(v2) && v2.containsAll(v1)) {
			System.out.println("testeConsultarTurmaPorInstituicaoNome ok");
		}
		else {
			System.out.println();
			System.out.println("testeConsultarTurmaPorInstituicaoNome v1.size() = " + v1.size());
			System.out.println("--------");
			Iterator it = v1.iterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}

			System.out.println("v2.size() = " + v2.size());
			System.out.println("--------");
			it = v2.iterator();
			while(it.hasNext()) {
				System.out.println(it.next());
			}
		}
	}


	private void atualizarInstituicao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoInstituicao.size(); i++) {
			Instituicao obj = (Instituicao) _conjuntoInstituicao.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarInstituicao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarPeriodo() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoPeriodo.size(); i++) {
			Periodo obj = (Periodo) _conjuntoPeriodo.get(i);
			String id_periodo = nextString(10);
			obj.setId_periodo(id_periodo);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarPeriodo " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoTurma.size(); i++) {
			Turma obj = (Turma) _conjuntoTurma.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			Instituicao instituicao_Turma = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setInstituicao_Turma(instituicao_Turma);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarTurma " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAluno.size(); i++) {
			Aluno obj = (Aluno) _conjuntoAluno.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			String matricula = nextString(10);
			obj.setMatricula(matricula);
			Instituicao instituicao_Aluno = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setInstituicao_Aluno(instituicao_Aluno);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAluno " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarCurso() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoCurso.size(); i++) {
			Curso obj = (Curso) _conjuntoCurso.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			Instituicao instituicao_Curso = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setInstituicao_Curso(instituicao_Curso);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarCurso " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAlunoTurma() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAlunoTurma.size(); i++) {
			AlunoTurma obj = (AlunoTurma) _conjuntoAlunoTurma.get(i);
			Aluno aluno_AlunoTurma = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAluno_AlunoTurma(aluno_AlunoTurma);
			Turma turma_AlunoTurma = (Turma) _conjuntoTurma.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setTurma_AlunoTurma(turma_AlunoTurma);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAlunoTurma " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoCursoInstancia.size(); i++) {
			CursoInstancia obj = (CursoInstancia) _conjuntoCursoInstancia.get(i);
			Instituicao instituicao_CursoInstancia = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setInstituicao_CursoInstancia(instituicao_CursoInstancia);
			Curso curso_CursoInstancia = (Curso) _conjuntoCurso.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setCurso_CursoInstancia(curso_CursoInstancia);
			Periodo periodo_CursoInstancia = (Periodo) _conjuntoPeriodo.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setPeriodo_CursoInstancia(periodo_CursoInstancia);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarCursoInstancia " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAlunoCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAlunoCursoInstancia.size(); i++) {
			AlunoCursoInstancia obj = (AlunoCursoInstancia) _conjuntoAlunoCursoInstancia.get(i);
			CursoInstancia cursoInstancia_AlunoCursoInstancia = (CursoInstancia) _conjuntoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setCursoInstancia_AlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia);
			Aluno aluno_AlunoCursoInstancia = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAluno_AlunoCursoInstancia(aluno_AlunoCursoInstancia);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAlunoCursoInstancia " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAvaliacao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAvaliacao.size(); i++) {
			Avaliacao obj = (Avaliacao) _conjuntoAvaliacao.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			String tipo = nextString(10);
			obj.setTipo(tipo);
			CursoInstancia cursoInstancia_Avaliacao = (CursoInstancia) _conjuntoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setCursoInstancia_Avaliacao(cursoInstancia_Avaliacao);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAvaliacao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAvaliacaoAluno() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAvaliacaoAluno.size(); i++) {
			AvaliacaoAluno obj = (AvaliacaoAluno) _conjuntoAvaliacaoAluno.get(i);
			float nota = _rng.nextFloat();
			obj.setNota(nota);
			String noTotalizado = nextString(10);
			obj.setNoTotalizado(noTotalizado);
			Avaliacao avaliacao_AvaliacaoAluno = (Avaliacao) _conjuntoAvaliacao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAvaliacao_AvaliacaoAluno(avaliacao_AvaliacaoAluno);
			Correcao correcao_AvaliacaoAluno = (Correcao) _conjuntoCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setCorrecao_AvaliacaoAluno(correcao_AvaliacaoAluno);
			AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno = (AlunoCursoInstancia) _conjuntoAlunoCursoInstancia.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAlunoCursoInstancia_AvaliacaoAluno(alunoCursoInstancia_AvaliacaoAluno);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAvaliacaoAluno " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarProva() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoProva.size(); i++) {
			Prova obj = (Prova) _conjuntoProva.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			String fonte = nextString(10);
			obj.setFonte(fonte);
			int indice = _rng.nextInt();
			obj.setIndice(indice);
			Instituicao instituicao_Prova = (Instituicao) _conjuntoInstituicao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setInstituicao_Prova(instituicao_Prova);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarProva " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoCorrecao.size(); i++) {
			Correcao obj = (Correcao) _conjuntoCorrecao.get(i);
			String matriz = nextString(10);
			obj.setMatriz(matriz);
			String imagem = nextString(10);
			obj.setImagem(imagem);
			float nota = _rng.nextFloat();
			obj.setNota(nota);
			Prova prova_Correcao = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setProva_Correcao(prova_Correcao);
			Aluno aluno_Correcao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAluno_Correcao(aluno_Correcao);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoProvaCorrecao.size(); i++) {
			ProvaCorrecao obj = (ProvaCorrecao) _conjuntoProvaCorrecao.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			Prova prova_ProvaCorrecao = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setProva_ProvaCorrecao(prova_ProvaCorrecao);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarAlunoProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoAlunoProvaCorrecao.size(); i++) {
			AlunoProvaCorrecao obj = (AlunoProvaCorrecao) _conjuntoAlunoProvaCorrecao.get(i);
			int numEntradas = _rng.nextInt();
			obj.setNumEntradas(numEntradas);
			Aluno aluno_AlunoProvaCorrecao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAluno_AlunoProvaCorrecao(aluno_AlunoProvaCorrecao);
			ProvaCorrecao provaCorrecao_AlunoProvaCorrecao = (ProvaCorrecao) _conjuntoProvaCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setProvaCorrecao_AlunoProvaCorrecao(provaCorrecao_AlunoProvaCorrecao);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarAlunoProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarEntradaProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoEntradaProvaCorrecao.size(); i++) {
			EntradaProvaCorrecao obj = (EntradaProvaCorrecao) _conjuntoEntradaProvaCorrecao.get(i);
			byte status = (byte) _rng.nextInt();
			obj.setStatus(status);
			String foto = nextString(10);
			obj.setFoto(foto);
			int threshold = _rng.nextInt();
			obj.setThreshold(threshold);
			int phase = _rng.nextInt();
			obj.setPhase(phase);
			int idImageData = _rng.nextInt();
			obj.setIdImageData(idImageData);
			int tipo = _rng.nextInt();
			obj.setTipo(tipo);
			int idAnswersData = _rng.nextInt();
			obj.setIdAnswersData(idAnswersData);
			ProvaCorrecao provaCorrecao_EntradaProvaCorrecao = (ProvaCorrecao) _conjuntoProvaCorrecao.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setProvaCorrecao_EntradaProvaCorrecao(provaCorrecao_EntradaProvaCorrecao);
			Aluno aluno_EntradaProvaCorrecao = (Aluno) _conjuntoAluno.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setAluno_EntradaProvaCorrecao(aluno_EntradaProvaCorrecao);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarEntradaProvaCorrecao " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoColetaQuestionario.size(); i++) {
			ColetaQuestionario obj = (ColetaQuestionario) _conjuntoColetaQuestionario.get(i);
			String nome = nextString(10);
			obj.setNome(nome);
			Prova prova_ColetaQuestionario = (Prova) _conjuntoProva.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setProva_ColetaQuestionario(prova_ColetaQuestionario);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarColetaQuestionario " + ((double) (tf - ti) / 1000) + "s");
	}

	private void atualizarEntradaColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();
		for(int i = 0; i < _conjuntoEntradaColetaQuestionario.size(); i++) {
			EntradaColetaQuestionario obj = (EntradaColetaQuestionario) _conjuntoEntradaColetaQuestionario.get(i);
			byte status = (byte) _rng.nextInt();
			obj.setStatus(status);
			String foto = nextString(10);
			obj.setFoto(foto);
			int threshold = _rng.nextInt();
			obj.setThreshold(threshold);
			int phase = _rng.nextInt();
			obj.setPhase(phase);
			int idImageData = _rng.nextInt();
			obj.setIdImageData(idImageData);
			int tipoFolhaResposta = _rng.nextInt();
			obj.setTipoFolhaResposta(tipoFolhaResposta);
			int idAnswersData = _rng.nextInt();
			obj.setIdAnswersData(idAnswersData);
			ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario = (ColetaQuestionario) _conjuntoColetaQuestionario.get(Math.abs(_rng.nextInt()) % _numRegs);
			obj.setColetaQuestionario_EntradaColetaQuestionario(coletaQuestionario_EntradaColetaQuestionario);
		}
		long tf = System.currentTimeMillis();
		System.out.println("atualizarEntradaColetaQuestionario " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerInstituicao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoInstituicao.size(); i++) {
			Instituicao obj = (Instituicao) _conjuntoInstituicao.get(i);
			_rep.removerInstituicao(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerInstituicao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerPeriodo() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoPeriodo.size(); i++) {
			Periodo obj = (Periodo) _conjuntoPeriodo.get(i);
			_rep.removerPeriodo(obj.getId_periodo());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerPeriodo ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerTurma() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoTurma.size(); i++) {
			Turma obj = (Turma) _conjuntoTurma.get(i);
			_rep.removerTurma(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerTurma ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAluno() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAluno.size(); i++) {
			Aluno obj = (Aluno) _conjuntoAluno.get(i);
			_rep.removerAluno(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAluno ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerCurso() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoCurso.size(); i++) {
			Curso obj = (Curso) _conjuntoCurso.get(i);
			_rep.removerCurso(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerCurso ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAlunoTurma() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAlunoTurma.size(); i++) {
			AlunoTurma obj = (AlunoTurma) _conjuntoAlunoTurma.get(i);
			_rep.removerAlunoTurma(obj.getId_aluno(), obj.getId_turma());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAlunoTurma ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoCursoInstancia.size(); i++) {
			CursoInstancia obj = (CursoInstancia) _conjuntoCursoInstancia.get(i);
			_rep.removerCursoInstancia(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerCursoInstancia ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAlunoCursoInstancia() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAlunoCursoInstancia.size(); i++) {
			AlunoCursoInstancia obj = (AlunoCursoInstancia) _conjuntoAlunoCursoInstancia.get(i);
			_rep.removerAlunoCursoInstancia(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAlunoCursoInstancia ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAvaliacao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAvaliacao.size(); i++) {
			Avaliacao obj = (Avaliacao) _conjuntoAvaliacao.get(i);
			_rep.removerAvaliacao(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAvaliacao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAvaliacaoAluno() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAvaliacaoAluno.size(); i++) {
			AvaliacaoAluno obj = (AvaliacaoAluno) _conjuntoAvaliacaoAluno.get(i);
			_rep.removerAvaliacaoAluno(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAvaliacaoAluno ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerProva() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoProva.size(); i++) {
			Prova obj = (Prova) _conjuntoProva.get(i);
			_rep.removerProva(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerProva ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoCorrecao.size(); i++) {
			Correcao obj = (Correcao) _conjuntoCorrecao.get(i);
			_rep.removerCorrecao(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoProvaCorrecao.size(); i++) {
			ProvaCorrecao obj = (ProvaCorrecao) _conjuntoProvaCorrecao.get(i);
			_rep.removerProvaCorrecao(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerAlunoProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoAlunoProvaCorrecao.size(); i++) {
			AlunoProvaCorrecao obj = (AlunoProvaCorrecao) _conjuntoAlunoProvaCorrecao.get(i);
			_rep.removerAlunoProvaCorrecao(obj.getId_aluno(), obj.getId_provacorrecao());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerAlunoProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerEntradaProvaCorrecao() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoEntradaProvaCorrecao.size(); i++) {
			EntradaProvaCorrecao obj = (EntradaProvaCorrecao) _conjuntoEntradaProvaCorrecao.get(i);
			_rep.removerEntradaProvaCorrecao(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerEntradaProvaCorrecao ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoColetaQuestionario.size(); i++) {
			ColetaQuestionario obj = (ColetaQuestionario) _conjuntoColetaQuestionario.get(i);
			_rep.removerColetaQuestionario(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerColetaQuestionario ok " + ((double) (tf - ti) / 1000) + "s");
	}

	private void removerEntradaColetaQuestionario() throws SQLException {
		long ti = System.currentTimeMillis();

		for(int i = 0; i < _conjuntoEntradaColetaQuestionario.size(); i++) {
			EntradaColetaQuestionario obj = (EntradaColetaQuestionario) _conjuntoEntradaColetaQuestionario.get(i);
			_rep.removerEntradaColetaQuestionario(obj.getId());
		}
		long tf = System.currentTimeMillis();
		System.out.println("removerEntradaColetaQuestionario ok " + ((double) (tf - ti) / 1000) + "s");
	}

	public void criarTabelas() throws SQLException {
		Connection cn = _rbd.getConnection();
		Statement st = cn.createStatement();

		String sql = "create table Instituicao(" +
			"nome varchar(128)," +
			"id_instituicao int not null auto_increment," +
			"primary key(id_instituicao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Periodo(" +
			"id_periodo varchar(255) not null," +
			"primary key(id_periodo)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Turma(" +
			"nome varchar(128)," +
			"id_instituicao int," +
			"id_turma int not null auto_increment," +
			"primary key(id_turma)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Aluno(" +
			"nome varchar(128)," +
			"matricula varchar(64)," +
			"id_instituicao int," +
			"id_aluno int not null auto_increment," +
			"primary key(id_aluno)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Curso(" +
			"nome varchar(128)," +
			"id_instituicao int," +
			"id_curso int not null auto_increment," +
			"primary key(id_curso)" +
			");";
		st.executeUpdate(sql);

		sql = "create table AlunoTurma(" +
			"id_aluno int not null," +
			"id_turma int not null," +
			"primary key(id_aluno, id_turma)" +
			");";
		st.executeUpdate(sql);

		sql = "create table CursoInstancia(" +
			"id_instituicao int," +
			"id_curso int," +
			"id_periodo varchar(255)," +
			"id_cursoinstancia int not null auto_increment," +
			"primary key(id_cursoinstancia)" +
			");";
		st.executeUpdate(sql);

		sql = "create table AlunoCursoInstancia(" +
			"id_cursoinstancia int," +
			"id_aluno int," +
			"id_alunocursoinstancia int not null auto_increment," +
			"primary key(id_alunocursoinstancia)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Avaliacao(" +
			"nome varchar(128)," +
			"tipo varchar(32)," +
			"id_cursoinstancia int," +
			"id_avaliacao int not null auto_increment," +
			"primary key(id_avaliacao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table AvaliacaoAluno(" +
			"nota float," +
			"noTotalizado varchar(128)," +
			"id_avaliacao int," +
			"id_correcao int," +
			"id_alunocursoinstancia int," +
			"id_avaliacaoaluno int not null auto_increment," +
			"primary key(id_avaliacaoaluno)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Prova(" +
			"nome varchar(128)," +
			"fonte varchar(128)," +
			"indice int," +
			"id_instituicao int," +
			"id_prova int not null auto_increment," +
			"primary key(id_prova)" +
			");";
		st.executeUpdate(sql);

		sql = "create table Correcao(" +
			"matriz varchar(128)," +
			"imagem varchar(128)," +
			"nota float," +
			"id_prova int," +
			"id_aluno int," +
			"id_correcao int not null auto_increment," +
			"primary key(id_correcao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table ProvaCorrecao(" +
			"nome varchar(128)," +
			"id_prova int," +
			"id_provacorrecao int not null auto_increment," +
			"primary key(id_provacorrecao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table AlunoProvaCorrecao(" +
			"numEntradas int," +
			"id_aluno int not null," +
			"id_provacorrecao int not null," +
			"primary key(id_aluno, id_provacorrecao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table EntradaProvaCorrecao(" +
			"status tinyint," +
			"foto varchar(128)," +
			"threshold int," +
			"phase int," +
			"idImageData int," +
			"tipo int," +
			"idAnswersData int," +
			"id_provacorrecao int," +
			"id_aluno int," +
			"id_entradaprovacorrecao int not null auto_increment," +
			"primary key(id_entradaprovacorrecao)" +
			");";
		st.executeUpdate(sql);

		sql = "create table ColetaQuestionario(" +
			"nome varchar(128)," +
			"id_prova int," +
			"id_coletaquestionario int not null auto_increment," +
			"primary key(id_coletaquestionario)" +
			");";
		st.executeUpdate(sql);

		sql = "create table EntradaColetaQuestionario(" +
			"status tinyint," +
			"foto varchar(128)," +
			"threshold int," +
			"phase int," +
			"idImageData int," +
			"tipoFolhaResposta int," +
			"idAnswersData int," +
			"id_coletaquestionario int," +
			"id_entradacoletaquestionario int not null auto_increment," +
			"primary key(id_entradacoletaquestionario)" +
			");";
		st.executeUpdate(sql);
	}

	public void destruirTabelas() throws SQLException {
		Connection cn = _rbd.getConnection();
		Statement st = cn.createStatement();
		st.executeUpdate("drop table Instituicao;");
		st.executeUpdate("drop table Periodo;");
		st.executeUpdate("drop table Turma;");
		st.executeUpdate("drop table Aluno;");
		st.executeUpdate("drop table Curso;");
		st.executeUpdate("drop table AlunoTurma;");
		st.executeUpdate("drop table CursoInstancia;");
		st.executeUpdate("drop table AlunoCursoInstancia;");
		st.executeUpdate("drop table Avaliacao;");
		st.executeUpdate("drop table AvaliacaoAluno;");
		st.executeUpdate("drop table Prova;");
		st.executeUpdate("drop table Correcao;");
		st.executeUpdate("drop table ProvaCorrecao;");
		st.executeUpdate("drop table AlunoProvaCorrecao;");
		st.executeUpdate("drop table EntradaProvaCorrecao;");
		st.executeUpdate("drop table ColetaQuestionario;");
		st.executeUpdate("drop table EntradaColetaQuestionario;");
	}

	public void rodar() {
		try {
			long ti = System.currentTimeMillis();
			inicializar();
			criarTabelas();

			construirInstituicao();
			_rep.limpar();
			consultarInstituicao();
			atualizarInstituicao();
			_rep.limpar();
			consultarInstituicao();

			construirPeriodo();
			_rep.limpar();
			consultarPeriodo();
			atualizarPeriodo();
			_rep.limpar();
			consultarPeriodo();

			construirTurma();
			_rep.limpar();
			consultarTurma();
			atualizarTurma();
			_rep.limpar();
			consultarTurma();
			testeConsultarTurmaPorInstituicaoNome();

			construirAluno();
			_rep.limpar();
			consultarAluno();
			atualizarAluno();
			_rep.limpar();
			consultarAluno();

			construirCurso();
			_rep.limpar();
			consultarCurso();
			atualizarCurso();
			_rep.limpar();
			consultarCurso();

			construirAlunoTurma();
			_rep.limpar();
			consultarAlunoTurma();
			atualizarAlunoTurma();
			_rep.limpar();
			consultarAlunoTurma();

			construirCursoInstancia();
			_rep.limpar();
			consultarCursoInstancia();
			atualizarCursoInstancia();
			_rep.limpar();
			consultarCursoInstancia();

			construirAlunoCursoInstancia();
			_rep.limpar();
			consultarAlunoCursoInstancia();
			atualizarAlunoCursoInstancia();
			_rep.limpar();
			consultarAlunoCursoInstancia();

			construirAvaliacao();
			_rep.limpar();
			consultarAvaliacao();
			atualizarAvaliacao();
			_rep.limpar();
			consultarAvaliacao();

			construirAvaliacaoAluno();
			_rep.limpar();
			consultarAvaliacaoAluno();
			atualizarAvaliacaoAluno();
			_rep.limpar();
			consultarAvaliacaoAluno();

			construirProva();
			_rep.limpar();
			consultarProva();
			atualizarProva();
			_rep.limpar();
			consultarProva();

			construirCorrecao();
			_rep.limpar();
			consultarCorrecao();
			atualizarCorrecao();
			_rep.limpar();
			consultarCorrecao();

			construirProvaCorrecao();
			_rep.limpar();
			consultarProvaCorrecao();
			atualizarProvaCorrecao();
			_rep.limpar();
			consultarProvaCorrecao();

			construirAlunoProvaCorrecao();
			_rep.limpar();
			consultarAlunoProvaCorrecao();
			atualizarAlunoProvaCorrecao();
			_rep.limpar();
			consultarAlunoProvaCorrecao();

			construirEntradaProvaCorrecao();
			_rep.limpar();
			consultarEntradaProvaCorrecao();
			atualizarEntradaProvaCorrecao();
			_rep.limpar();
			consultarEntradaProvaCorrecao();

			construirColetaQuestionario();
			_rep.limpar();
			consultarColetaQuestionario();
			atualizarColetaQuestionario();
			_rep.limpar();
			consultarColetaQuestionario();

			construirEntradaColetaQuestionario();
			_rep.limpar();
			consultarEntradaColetaQuestionario();
			atualizarEntradaColetaQuestionario();
			_rep.limpar();
			consultarEntradaColetaQuestionario();

			removerInstituicao();
			removerPeriodo();
			removerTurma();
			removerAluno();
			removerCurso();
			removerAlunoTurma();
			removerCursoInstancia();
			removerAlunoCursoInstancia();
			removerAvaliacao();
			removerAvaliacaoAluno();
			removerProva();
			removerCorrecao();
			removerProvaCorrecao();
			removerAlunoProvaCorrecao();
			removerEntradaProvaCorrecao();
			removerColetaQuestionario();
			removerEntradaColetaQuestionario();

			construirInstituicao();
			construirPeriodo();
			construirTurma();
			construirAluno();
			construirCurso();
			construirAlunoTurma();
			construirCursoInstancia();
			construirAlunoCursoInstancia();
			construirAvaliacao();
			construirAvaliacaoAluno();
			construirProva();
			construirCorrecao();
			construirProvaCorrecao();
			construirAlunoProvaCorrecao();
			construirEntradaProvaCorrecao();
			construirColetaQuestionario();
			construirEntradaColetaQuestionario();


			destruirTabelas();
			long tf = System.currentTimeMillis();
			System.out.println("tempo: " + ((double) (tf - ti) / 1000) + "s");
		}
		catch(Exception e) {
			try {
				destruirTabelas();
			}
			catch(Exception e2) {}
			e.printStackTrace();
			System.exit(0);
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Model Tester (option -c to only create database)");
		TesteMixnfix t = new TesteMixnfix();
		if(args.length == 1 && args[0].equals("-c")) { 
			t.inicializar(); 
			try { t.destruirTabelas(); }
			catch (Exception e) {}
			t.criarTabelas(); 
			System.out.println("Tabelas criadas com sucesso!");
		}
		else t.rodar();
		System.exit(0);
	}
}
