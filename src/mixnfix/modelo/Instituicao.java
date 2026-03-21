package mixnfix.modelo;

import java.sql.SQLException;

public class Instituicao extends PersistentObject {
	// Construtor
	public Instituicao(int id, String nome) {
		_id = id;
		_nome = nome;
	}

	// Construtor vazio
	public Instituicao() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmInstituicao(this, nome);
		}
		_nome = nome;
	}

	// Método para obter a chave do objeto. 
	public InstituicaoKey getKey() {
		return new InstituicaoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Instituicao) {
			Instituicao x = (Instituicao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Instituicao\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		return b.toString();
	}

	// Método para copiar o objeto.
	public Instituicao getTransitoryCopy() {
		return new Instituicao(_id, _nome);
	}
}
