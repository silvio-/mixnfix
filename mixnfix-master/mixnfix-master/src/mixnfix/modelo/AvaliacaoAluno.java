package mixnfix.modelo;

import java.sql.SQLException;

public class AvaliacaoAluno extends PersistentObject {
	// Construtor
	public AvaliacaoAluno(int id, float nota, String noTotalizado, Avaliacao avaliacao_AvaliacaoAluno, Correcao correcao_AvaliacaoAluno, AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) {
		_id = id;
		_nota = nota;
		_noTotalizado = noTotalizado;
		_avaliacao_AvaliacaoAluno = avaliacao_AvaliacaoAluno;
		_correcao_AvaliacaoAluno = correcao_AvaliacaoAluno;
		_alunoCursoInstancia_AvaliacaoAluno = alunoCursoInstancia_AvaliacaoAluno;
	}

	// Construtor vazio
	public AvaliacaoAluno() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo nota
	private float _nota;
	public float getNota() {
		return _nota;
	}
	public void setNota(float nota) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarNotaEmAvaliacaoAluno(this, nota);
		}
		_nota = nota;
	}

	// Atributo noTotalizado
	private String _noTotalizado;
	public String getNoTotalizado() {
		return _noTotalizado;
	}
	public void setNoTotalizado(String noTotalizado) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarNoTotalizadoEmAvaliacaoAluno(this, noTotalizado);
		}
		_noTotalizado = noTotalizado;
	}

	// Atributo avaliacao_AvaliacaoAluno
	private Avaliacao _avaliacao_AvaliacaoAluno;
	public Avaliacao getAvaliacao_AvaliacaoAluno() {
		return _avaliacao_AvaliacaoAluno;
	}
	public void setAvaliacao_AvaliacaoAluno(Avaliacao avaliacao_AvaliacaoAluno) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAvaliacao_AvaliacaoAlunoEmAvaliacaoAluno(this, avaliacao_AvaliacaoAluno);
		}
		_avaliacao_AvaliacaoAluno = avaliacao_AvaliacaoAluno;
	}

	// Atributo correcao_AvaliacaoAluno
	private Correcao _correcao_AvaliacaoAluno;
	public Correcao getCorrecao_AvaliacaoAluno() {
		return _correcao_AvaliacaoAluno;
	}
	public void setCorrecao_AvaliacaoAluno(Correcao correcao_AvaliacaoAluno) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarCorrecao_AvaliacaoAlunoEmAvaliacaoAluno(this, correcao_AvaliacaoAluno);
		}
		_correcao_AvaliacaoAluno = correcao_AvaliacaoAluno;
	}

	// Atributo alunoCursoInstancia_AvaliacaoAluno
	private AlunoCursoInstancia _alunoCursoInstancia_AvaliacaoAluno;
	public AlunoCursoInstancia getAlunoCursoInstancia_AvaliacaoAluno() {
		return _alunoCursoInstancia_AvaliacaoAluno;
	}
	public void setAlunoCursoInstancia_AvaliacaoAluno(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAlunoCursoInstancia_AvaliacaoAlunoEmAvaliacaoAluno(this, alunoCursoInstancia_AvaliacaoAluno);
		}
		_alunoCursoInstancia_AvaliacaoAluno = alunoCursoInstancia_AvaliacaoAluno;
	}

	// Método para obter a chave do objeto. 
	public AvaliacaoAlunoKey getKey() {
		return new AvaliacaoAlunoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof AvaliacaoAluno) {
			AvaliacaoAluno x = (AvaliacaoAluno) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("AvaliacaoAluno\n");
		b.append("id: " + _id + "\n");
		b.append("nota: " + _nota + "\n");
		b.append("noTotalizado: \"" + _noTotalizado + "\"\n");
		b.append("avaliacao_AvaliacaoAluno: \n" + Util.indent(_avaliacao_AvaliacaoAluno.toString()));
		b.append("correcao_AvaliacaoAluno: \n" + Util.indent(_correcao_AvaliacaoAluno.toString()));
		b.append("alunoCursoInstancia_AvaliacaoAluno: \n" + Util.indent(_alunoCursoInstancia_AvaliacaoAluno.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public AvaliacaoAluno getTransitoryCopy() {
		return new AvaliacaoAluno(_id, _nota, _noTotalizado, _avaliacao_AvaliacaoAluno, _correcao_AvaliacaoAluno, _alunoCursoInstancia_AvaliacaoAluno);
	}
}
