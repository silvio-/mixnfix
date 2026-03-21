package mixnfix.modelo;

import java.sql.SQLException;

public class ColetaQuestionario extends PersistentObject {
	// Construtor
	public ColetaQuestionario(int id, String nome, Prova prova_ColetaQuestionario) {
		_id = id;
		_nome = nome;
		_prova_ColetaQuestionario = prova_ColetaQuestionario;
	}

	// Construtor vazio
	public ColetaQuestionario() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmColetaQuestionario(this, nome);
		}
		_nome = nome;
	}

	// Atributo prova_ColetaQuestionario
	private Prova _prova_ColetaQuestionario;
	public Prova getProva_ColetaQuestionario() {
		return _prova_ColetaQuestionario;
	}
	public void setProva_ColetaQuestionario(Prova prova_ColetaQuestionario) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarProva_ColetaQuestionarioEmColetaQuestionario(this, prova_ColetaQuestionario);
		}
		_prova_ColetaQuestionario = prova_ColetaQuestionario;
	}

	// Método para obter a chave do objeto. 
	public ColetaQuestionarioKey getKey() {
		return new ColetaQuestionarioKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof ColetaQuestionario) {
			ColetaQuestionario x = (ColetaQuestionario) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("ColetaQuestionario\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("prova_ColetaQuestionario: \n" + Util.indent(_prova_ColetaQuestionario.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public ColetaQuestionario getTransitoryCopy() {
		return new ColetaQuestionario(_id, _nome, _prova_ColetaQuestionario);
	}
}
