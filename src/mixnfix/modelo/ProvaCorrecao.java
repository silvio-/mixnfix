package mixnfix.modelo;

import java.sql.SQLException;

public class ProvaCorrecao extends PersistentObject {
	// Construtor
	public ProvaCorrecao(int id, String nome, Prova prova_ProvaCorrecao) {
		_id = id;
		_nome = nome;
		_prova_ProvaCorrecao = prova_ProvaCorrecao;
	}

	// Construtor vazio
	public ProvaCorrecao() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmProvaCorrecao(this, nome);
		}
		_nome = nome;
	}

	// Atributo prova_ProvaCorrecao
	private Prova _prova_ProvaCorrecao;
	public Prova getProva_ProvaCorrecao() {
		return _prova_ProvaCorrecao;
	}
	public void setProva_ProvaCorrecao(Prova prova_ProvaCorrecao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarProva_ProvaCorrecaoEmProvaCorrecao(this, prova_ProvaCorrecao);
		}
		_prova_ProvaCorrecao = prova_ProvaCorrecao;
	}

	// Método para obter a chave do objeto. 
	public ProvaCorrecaoKey getKey() {
		return new ProvaCorrecaoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof ProvaCorrecao) {
			ProvaCorrecao x = (ProvaCorrecao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("ProvaCorrecao\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("prova_ProvaCorrecao: \n" + Util.indent(_prova_ProvaCorrecao.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public ProvaCorrecao getTransitoryCopy() {
		return new ProvaCorrecao(_id, _nome, _prova_ProvaCorrecao);
	}
}
