package mixnfix.modelo;

import java.sql.SQLException;

public class AlunoProvaCorrecao extends PersistentObject {
	// Construtor
	public AlunoProvaCorrecao(int numEntradas, Aluno aluno_AlunoProvaCorrecao, ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) {
		_numEntradas = numEntradas;
		_aluno_AlunoProvaCorrecao = aluno_AlunoProvaCorrecao;
		_provaCorrecao_AlunoProvaCorrecao = provaCorrecao_AlunoProvaCorrecao;
	}

	// Construtor vazio
	public AlunoProvaCorrecao() {
	}

	// Atributo numEntradas
	private int _numEntradas;
	public int getNumEntradas() {
		return _numEntradas;
	}
	public void setNumEntradas(int numEntradas) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarNumEntradasEmAlunoProvaCorrecao(this, numEntradas);
		}
		_numEntradas = numEntradas;
	}

	// Atributo aluno_AlunoProvaCorrecao
	private Aluno _aluno_AlunoProvaCorrecao;
	public Aluno getAluno_AlunoProvaCorrecao() {
		return _aluno_AlunoProvaCorrecao;
	}
	public void setAluno_AlunoProvaCorrecao(Aluno aluno_AlunoProvaCorrecao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAluno_AlunoProvaCorrecaoEmAlunoProvaCorrecao(this, aluno_AlunoProvaCorrecao);
		}
		_aluno_AlunoProvaCorrecao = aluno_AlunoProvaCorrecao;
	}

	// Atributo provaCorrecao_AlunoProvaCorrecao
	private ProvaCorrecao _provaCorrecao_AlunoProvaCorrecao;
	public ProvaCorrecao getProvaCorrecao_AlunoProvaCorrecao() {
		return _provaCorrecao_AlunoProvaCorrecao;
	}
	public void setProvaCorrecao_AlunoProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarProvaCorrecao_AlunoProvaCorrecaoEmAlunoProvaCorrecao(this, provaCorrecao_AlunoProvaCorrecao);
		}
		_provaCorrecao_AlunoProvaCorrecao = provaCorrecao_AlunoProvaCorrecao;
	}

	// Atributo da chave de Aluno
	public int getId_aluno() {
		return _aluno_AlunoProvaCorrecao.getId();
	}

	// Atributo da chave de ProvaCorrecao
	public int getId_provacorrecao() {
		return _provaCorrecao_AlunoProvaCorrecao.getId();
	}

	// Método para obter a chave do objeto. 
	public AlunoProvaCorrecaoKey getKey() {
		return new AlunoProvaCorrecaoKey(getId_aluno(), getId_provacorrecao());
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof AlunoProvaCorrecao) {
			AlunoProvaCorrecao x = (AlunoProvaCorrecao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("AlunoProvaCorrecao\n");
		b.append("numEntradas: " + _numEntradas + "\n");
		b.append("aluno_AlunoProvaCorrecao: \n" + Util.indent(_aluno_AlunoProvaCorrecao.toString()));
		b.append("provaCorrecao_AlunoProvaCorrecao: \n" + Util.indent(_provaCorrecao_AlunoProvaCorrecao.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public AlunoProvaCorrecao getTransitoryCopy() {
		return new AlunoProvaCorrecao(_numEntradas, _aluno_AlunoProvaCorrecao, _provaCorrecao_AlunoProvaCorrecao);
	}
}
