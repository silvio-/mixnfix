package mixnfix.modelo;

import java.sql.SQLException;

public class Aluno extends PersistentObject {
	// Construtor
	public Aluno(int id, String nome, String matricula, Instituicao instituicao_Aluno) {
		_id = id;
		_nome = nome;
		_matricula = matricula;
		_instituicao_Aluno = instituicao_Aluno;
	}

	// Construtor vazio
	public Aluno() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo nome
	private String _nome;
	public String getNome() {
		return _nome;
	}
	public void setNome(String nome) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmAluno(this, nome);
		}
		_nome = nome;
	}

	// Atributo matricula
	private String _matricula;
	public String getMatricula() {
		return _matricula;
	}
	public void setMatricula(String matricula) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarMatriculaEmAluno(this, matricula);
		}
		_matricula = matricula;
	}

	// Atributo instituicao_Aluno
	private Instituicao _instituicao_Aluno;
	public Instituicao getInstituicao_Aluno() {
		return _instituicao_Aluno;
	}
	public void setInstituicao_Aluno(Instituicao instituicao_Aluno) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarInstituicao_AlunoEmAluno(this, instituicao_Aluno);
		}
		_instituicao_Aluno = instituicao_Aluno;
	}

	// Método para obter a chave do objeto. 
	public AlunoKey getKey() {
		return new AlunoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Aluno) {
			Aluno x = (Aluno) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Aluno\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("matricula: \"" + _matricula + "\"\n");
		b.append("instituicao_Aluno: \n" + Util.indent(_instituicao_Aluno.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Aluno getTransitoryCopy() {
		return new Aluno(_id, _nome, _matricula, _instituicao_Aluno);
	}
}
