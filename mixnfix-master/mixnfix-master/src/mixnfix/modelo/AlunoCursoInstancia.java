package mixnfix.modelo;

import java.sql.SQLException;

public class AlunoCursoInstancia extends PersistentObject {
	// Construtor
	public AlunoCursoInstancia(int id, CursoInstancia cursoInstancia_AlunoCursoInstancia, Aluno aluno_AlunoCursoInstancia) {
		_id = id;
		_cursoInstancia_AlunoCursoInstancia = cursoInstancia_AlunoCursoInstancia;
		_aluno_AlunoCursoInstancia = aluno_AlunoCursoInstancia;
	}

	// Construtor vazio
	public AlunoCursoInstancia() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo cursoInstancia_AlunoCursoInstancia
	private CursoInstancia _cursoInstancia_AlunoCursoInstancia;
	public CursoInstancia getCursoInstancia_AlunoCursoInstancia() {
		return _cursoInstancia_AlunoCursoInstancia;
	}
	public void setCursoInstancia_AlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarCursoInstancia_AlunoCursoInstanciaEmAlunoCursoInstancia(this, cursoInstancia_AlunoCursoInstancia);
		}
		_cursoInstancia_AlunoCursoInstancia = cursoInstancia_AlunoCursoInstancia;
	}

	// Atributo aluno_AlunoCursoInstancia
	private Aluno _aluno_AlunoCursoInstancia;
	public Aluno getAluno_AlunoCursoInstancia() {
		return _aluno_AlunoCursoInstancia;
	}
	public void setAluno_AlunoCursoInstancia(Aluno aluno_AlunoCursoInstancia) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAluno_AlunoCursoInstanciaEmAlunoCursoInstancia(this, aluno_AlunoCursoInstancia);
		}
		_aluno_AlunoCursoInstancia = aluno_AlunoCursoInstancia;
	}

	// Método para obter a chave do objeto. 
	public AlunoCursoInstanciaKey getKey() {
		return new AlunoCursoInstanciaKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof AlunoCursoInstancia) {
			AlunoCursoInstancia x = (AlunoCursoInstancia) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("AlunoCursoInstancia\n");
		b.append("id: " + _id + "\n");
		b.append("cursoInstancia_AlunoCursoInstancia: \n" + Util.indent(_cursoInstancia_AlunoCursoInstancia.toString()));
		b.append("aluno_AlunoCursoInstancia: \n" + Util.indent(_aluno_AlunoCursoInstancia.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public AlunoCursoInstancia getTransitoryCopy() {
		return new AlunoCursoInstancia(_id, _cursoInstancia_AlunoCursoInstancia, _aluno_AlunoCursoInstancia);
	}
}
