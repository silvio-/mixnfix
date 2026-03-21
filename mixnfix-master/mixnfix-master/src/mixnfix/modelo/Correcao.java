package mixnfix.modelo;

import java.sql.SQLException;

public class Correcao extends PersistentObject {
	// Construtor
	public Correcao(int id, String matriz, String imagem, float nota, Prova prova_Correcao, Aluno aluno_Correcao) {
		_id = id;
		_matriz = matriz;
		_imagem = imagem;
		_nota = nota;
		_prova_Correcao = prova_Correcao;
		_aluno_Correcao = aluno_Correcao;
	}

	// Construtor vazio
	public Correcao() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo matriz
	private String _matriz;
	public String getMatriz() {
		return _matriz;
	}
	public void setMatriz(String matriz) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarMatrizEmCorrecao(this, matriz);
		}
		_matriz = matriz;
	}

	// Atributo imagem
	private String _imagem;
	public String getImagem() {
		return _imagem;
	}
	public void setImagem(String imagem) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarImagemEmCorrecao(this, imagem);
		}
		_imagem = imagem;
	}

	// Atributo nota
	private float _nota;
	public float getNota() {
		return _nota;
	}
	public void setNota(float nota) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarNotaEmCorrecao(this, nota);
		}
		_nota = nota;
	}

	// Atributo prova_Correcao
	private Prova _prova_Correcao;
	public Prova getProva_Correcao() {
		return _prova_Correcao;
	}
	public void setProva_Correcao(Prova prova_Correcao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarProva_CorrecaoEmCorrecao(this, prova_Correcao);
		}
		_prova_Correcao = prova_Correcao;
	}

	// Atributo aluno_Correcao
	private Aluno _aluno_Correcao;
	public Aluno getAluno_Correcao() {
		return _aluno_Correcao;
	}
	public void setAluno_Correcao(Aluno aluno_Correcao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAluno_CorrecaoEmCorrecao(this, aluno_Correcao);
		}
		_aluno_Correcao = aluno_Correcao;
	}

	// Método para obter a chave do objeto. 
	public CorrecaoKey getKey() {
		return new CorrecaoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Correcao) {
			Correcao x = (Correcao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Correcao\n");
		b.append("id: " + _id + "\n");
		b.append("matriz: \"" + _matriz + "\"\n");
		b.append("imagem: \"" + _imagem + "\"\n");
		b.append("nota: " + _nota + "\n");
		b.append("prova_Correcao: \n" + Util.indent(_prova_Correcao.toString()));
		b.append("aluno_Correcao: \n" + Util.indent(_aluno_Correcao.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public Correcao getTransitoryCopy() {
		return new Correcao(_id, _matriz, _imagem, _nota, _prova_Correcao, _aluno_Correcao);
	}
}
