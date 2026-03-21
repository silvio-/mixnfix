package mixnfix.modelo;

import java.sql.SQLException;

public class Curso extends PersistentObject {
	// Construtor
	public Curso(int id, String nome, Instituicao instituicao_Curso) {
		_id = id;
		_nome = nome;
		_instituicao_Curso = instituicao_Curso;
	}

	// Construtor vazio
	public Curso() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmCurso(this, nome);
		}
		_nome = nome;
	}

	// Atributo instituicao_Curso
	private Instituicao _instituicao_Curso;
	public Instituicao getInstituicao_Curso() {
		return _instituicao_Curso;
	}
	public void setInstituicao_Curso(Instituicao instituicao_Curso) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarInstituicao_CursoEmCurso(this, instituicao_Curso);
		}
		_instituicao_Curso = instituicao_Curso;
	}

	// Método para obter a chave do objeto. 
	public CursoKey getKey() {
		return new CursoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Curso) {
			Curso x = (Curso) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Curso\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("instituicao_Curso: \n" + Util.indent(_instituicao_Curso.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Curso getTransitoryCopy() {
		return new Curso(_id, _nome, _instituicao_Curso);
	}
}
