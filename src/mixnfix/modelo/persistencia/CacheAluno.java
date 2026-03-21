package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Instituicao;

public class CacheAluno {
	class ConsultaMatricula {
		public String _matricula;

		public ConsultaMatricula(String matricula) {
			_matricula = matricula;
		}
	}
	class ConsultaInstituicao_Aluno {
		public Instituicao _instituicao_Aluno;

		public ConsultaInstituicao_Aluno(Instituicao instituicao_Aluno) {
			_instituicao_Aluno = instituicao_Aluno;
		}
	}
	private Vector _consultasDoTipoMatricula;
	private Vector _consultasDoTipoInstituicao_Aluno;

	public boolean cacheMatricula(String matricula) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoMatricula.size(); i++) {
			ConsultaMatricula obj = (ConsultaMatricula) _consultasDoTipoMatricula.get(i);
			achou = ((obj._matricula == null && matricula == null) || (obj._matricula != null && matricula != null && obj._matricula.equals(matricula)));
		}
		return achou;
	}

	public void adicionarMatricula(String matricula) {
		_consultasDoTipoMatricula.add(new ConsultaMatricula(matricula));
	}

	public boolean cacheInstituicao_Aluno(Instituicao instituicao_Aluno) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoInstituicao_Aluno.size(); i++) {
			ConsultaInstituicao_Aluno obj = (ConsultaInstituicao_Aluno) _consultasDoTipoInstituicao_Aluno.get(i);
			achou = ((obj._instituicao_Aluno == null && instituicao_Aluno == null) || (obj._instituicao_Aluno != null && instituicao_Aluno != null && obj._instituicao_Aluno.equals(instituicao_Aluno)));
		}
		return achou;
	}

	public void adicionarInstituicao_Aluno(Instituicao instituicao_Aluno) {
		_consultasDoTipoInstituicao_Aluno.add(new ConsultaInstituicao_Aluno(instituicao_Aluno));
	}

	public CacheAluno() {
		_consultasDoTipoMatricula = new Vector();
		_consultasDoTipoInstituicao_Aluno = new Vector();
	}

	public void limpar() {
		_consultasDoTipoMatricula.clear();
		_consultasDoTipoInstituicao_Aluno.clear();
	}
}
