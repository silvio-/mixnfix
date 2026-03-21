package mixnfix.modelo;

import java.sql.SQLException;

public class Turma extends PersistentObject {
	// Construtor
	public Turma(int id, String nome, Instituicao instituicao_Turma) {
		_id = id;
		_nome = nome;
		_instituicao_Turma = instituicao_Turma;
	}

	// Construtor vazio
	public Turma() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmTurma(this, nome);
		}
		_nome = nome;
	}

	// Atributo instituicao_Turma
	private Instituicao _instituicao_Turma;
	public Instituicao getInstituicao_Turma() {
		return _instituicao_Turma;
	}
	public void setInstituicao_Turma(Instituicao instituicao_Turma) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarInstituicao_TurmaEmTurma(this, instituicao_Turma);
		}
		_instituicao_Turma = instituicao_Turma;
	}

	// Método para obter a chave do objeto. 
	public TurmaKey getKey() {
		return new TurmaKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Turma) {
			Turma x = (Turma) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Turma\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("instituicao_Turma: \n" + Util.indent(_instituicao_Turma.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Turma getTransitoryCopy() {
		return new Turma(_id, _nome, _instituicao_Turma);
	}
}
