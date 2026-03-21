package mixnfix.modelo;

import java.sql.SQLException;

public class AlunoTurma extends PersistentObject {
	// Construtor
	public AlunoTurma(Aluno aluno_AlunoTurma, Turma turma_AlunoTurma) {
		_aluno_AlunoTurma = aluno_AlunoTurma;
		_turma_AlunoTurma = turma_AlunoTurma;
	}

	// Construtor vazio
	public AlunoTurma() {
	}

	// Atributo aluno_AlunoTurma
	private Aluno _aluno_AlunoTurma;
	public Aluno getAluno_AlunoTurma() {
		return _aluno_AlunoTurma;
	}
	public void setAluno_AlunoTurma(Aluno aluno_AlunoTurma) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAluno_AlunoTurmaEmAlunoTurma(this, aluno_AlunoTurma);
		}
		_aluno_AlunoTurma = aluno_AlunoTurma;
	}

	// Atributo turma_AlunoTurma
	private Turma _turma_AlunoTurma;
	public Turma getTurma_AlunoTurma() {
		return _turma_AlunoTurma;
	}
	public void setTurma_AlunoTurma(Turma turma_AlunoTurma) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarTurma_AlunoTurmaEmAlunoTurma(this, turma_AlunoTurma);
		}
		_turma_AlunoTurma = turma_AlunoTurma;
	}

	// Atributo da chave de Aluno
	public int getId_aluno() {
		return _aluno_AlunoTurma.getId();
	}

	// Atributo da chave de Turma
	public int getId_turma() {
		return _turma_AlunoTurma.getId();
	}

	// Método para obter a chave do objeto. 
	public AlunoTurmaKey getKey() {
		return new AlunoTurmaKey(getId_aluno(), getId_turma());
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof AlunoTurma) {
			AlunoTurma x = (AlunoTurma) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("AlunoTurma\n");
		b.append("aluno_AlunoTurma: \n" + Util.indent(_aluno_AlunoTurma.toString()));
		b.append("turma_AlunoTurma: \n" + Util.indent(_turma_AlunoTurma.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public AlunoTurma getTransitoryCopy() {
		return new AlunoTurma(_aluno_AlunoTurma, _turma_AlunoTurma);
	}
}
