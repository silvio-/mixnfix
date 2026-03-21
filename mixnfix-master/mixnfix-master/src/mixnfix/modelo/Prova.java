package mixnfix.modelo;

import java.sql.SQLException;

public class Prova extends PersistentObject {
	// Construtor
	public Prova(int id, String nome, String fonte, int indice, Instituicao instituicao_Prova) {
		_id = id;
		_nome = nome;
		_fonte = fonte;
		_indice = indice;
		_instituicao_Prova = instituicao_Prova;
	}

	// Construtor vazio
	public Prova() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmProva(this, nome);
		}
		_nome = nome;
	}

	// Atributo fonte
	private String _fonte;
	public String getFonte() {
		return _fonte;
	}
	public void setFonte(String fonte) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarFonteEmProva(this, fonte);
		}
		_fonte = fonte;
	}

	// Atributo indice
	private int _indice;
	public int getIndice() {
		return _indice;
	}
	public void setIndice(int indice) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarIndiceEmProva(this, indice);
		}
		_indice = indice;
	}

	// Atributo instituicao_Prova
	private Instituicao _instituicao_Prova;
	public Instituicao getInstituicao_Prova() {
		return _instituicao_Prova;
	}
	public void setInstituicao_Prova(Instituicao instituicao_Prova) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarInstituicao_ProvaEmProva(this, instituicao_Prova);
		}
		_instituicao_Prova = instituicao_Prova;
	}

	// Método para obter a chave do objeto. 
	public ProvaKey getKey() {
		return new ProvaKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Prova) {
			Prova x = (Prova) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Prova\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("fonte: \"" + _fonte + "\"\n");
		b.append("indice: " + _indice + "\n");
		b.append("instituicao_Prova: \n" + Util.indent(_instituicao_Prova.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Prova getTransitoryCopy() {
		return new Prova(_id, _nome, _fonte, _indice, _instituicao_Prova);
	}
}
