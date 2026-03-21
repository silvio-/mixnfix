package mixnfix.modelo;

import java.sql.SQLException;

public class CursoInstancia extends PersistentObject {
	// Construtor
	public CursoInstancia(int id, Instituicao instituicao_CursoInstancia, Curso curso_CursoInstancia, Periodo periodo_CursoInstancia) {
		_id = id;
		_instituicao_CursoInstancia = instituicao_CursoInstancia;
		_curso_CursoInstancia = curso_CursoInstancia;
		_periodo_CursoInstancia = periodo_CursoInstancia;
	}

	// Construtor vazio
	public CursoInstancia() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo instituicao_CursoInstancia
	private Instituicao _instituicao_CursoInstancia;
	public Instituicao getInstituicao_CursoInstancia() {
		return _instituicao_CursoInstancia;
	}
	public void setInstituicao_CursoInstancia(Instituicao instituicao_CursoInstancia) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarInstituicao_CursoInstanciaEmCursoInstancia(this, instituicao_CursoInstancia);
		}
		_instituicao_CursoInstancia = instituicao_CursoInstancia;
	}

	// Atributo curso_CursoInstancia
	private Curso _curso_CursoInstancia;
	public Curso getCurso_CursoInstancia() {
		return _curso_CursoInstancia;
	}
	public void setCurso_CursoInstancia(Curso curso_CursoInstancia) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarCurso_CursoInstanciaEmCursoInstancia(this, curso_CursoInstancia);
		}
		_curso_CursoInstancia = curso_CursoInstancia;
	}

	// Atributo periodo_CursoInstancia
	private Periodo _periodo_CursoInstancia;
	public Periodo getPeriodo_CursoInstancia() {
		return _periodo_CursoInstancia;
	}
	public void setPeriodo_CursoInstancia(Periodo periodo_CursoInstancia) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarPeriodo_CursoInstanciaEmCursoInstancia(this, periodo_CursoInstancia);
		}
		_periodo_CursoInstancia = periodo_CursoInstancia;
	}

	// Método para obter a chave do objeto. 
	public CursoInstanciaKey getKey() {
		return new CursoInstanciaKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof CursoInstancia) {
			CursoInstancia x = (CursoInstancia) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("CursoInstancia\n");
		b.append("id: " + _id + "\n");
		b.append("instituicao_CursoInstancia: \n" + Util.indent(_instituicao_CursoInstancia.toString()));
		b.append("curso_CursoInstancia: \n" + Util.indent(_curso_CursoInstancia.toString()));
		b.append("periodo_CursoInstancia: \n" + Util.indent(_periodo_CursoInstancia.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public CursoInstancia getTransitoryCopy() {
		return new CursoInstancia(_id, _instituicao_CursoInstancia, _curso_CursoInstancia, _periodo_CursoInstancia);
	}
}
