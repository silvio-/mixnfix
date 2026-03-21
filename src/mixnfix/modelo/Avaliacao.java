package mixnfix.modelo;

import java.sql.SQLException;

public class Avaliacao extends PersistentObject {
	// Construtor
	public Avaliacao(int id, String nome, String tipo, CursoInstancia cursoInstancia_Avaliacao) {
		_id = id;
		_nome = nome;
		_tipo = tipo;
		_cursoInstancia_Avaliacao = cursoInstancia_Avaliacao;
	}

	// Construtor vazio
	public Avaliacao() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarNomeEmAvaliacao(this, nome);
		}
		_nome = nome;
	}

	// Atributo tipo
	private String _tipo;
	public String getTipo() {
		return _tipo;
	}
	public void setTipo(String tipo) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarTipoEmAvaliacao(this, tipo);
		}
		_tipo = tipo;
	}

	// Atributo cursoInstancia_Avaliacao
	private CursoInstancia _cursoInstancia_Avaliacao;
	public CursoInstancia getCursoInstancia_Avaliacao() {
		return _cursoInstancia_Avaliacao;
	}
	public void setCursoInstancia_Avaliacao(CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarCursoInstancia_AvaliacaoEmAvaliacao(this, cursoInstancia_Avaliacao);
		}
		_cursoInstancia_Avaliacao = cursoInstancia_Avaliacao;
	}

	// Método para obter a chave do objeto. 
	public AvaliacaoKey getKey() {
		return new AvaliacaoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Avaliacao) {
			Avaliacao x = (Avaliacao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Avaliacao\n");
		b.append("id: " + _id + "\n");
		b.append("nome: \"" + _nome + "\"\n");
		b.append("tipo: \"" + _tipo + "\"\n");
		b.append("cursoInstancia_Avaliacao: \n" + Util.indent(_cursoInstancia_Avaliacao.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Avaliacao getTransitoryCopy() {
		return new Avaliacao(_id, _nome, _tipo, _cursoInstancia_Avaliacao);
	}
}
