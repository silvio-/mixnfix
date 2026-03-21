package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Instituicao;

public class CacheCurso {
	class ConsultaInstituicao_Curso {
		public Instituicao _instituicao_Curso;

		public ConsultaInstituicao_Curso(Instituicao instituicao_Curso) {
			_instituicao_Curso = instituicao_Curso;
		}
	}
	private Vector _consultasDoTipoInstituicao_Curso;

	public boolean cacheInstituicao_Curso(Instituicao instituicao_Curso) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoInstituicao_Curso.size(); i++) {
			ConsultaInstituicao_Curso obj = (ConsultaInstituicao_Curso) _consultasDoTipoInstituicao_Curso.get(i);
			achou = ((obj._instituicao_Curso == null && instituicao_Curso == null) || (obj._instituicao_Curso != null && instituicao_Curso != null && obj._instituicao_Curso.equals(instituicao_Curso)));
		}
		return achou;
	}

	public void adicionarInstituicao_Curso(Instituicao instituicao_Curso) {
		_consultasDoTipoInstituicao_Curso.add(new ConsultaInstituicao_Curso(instituicao_Curso));
	}

	public CacheCurso() {
		_consultasDoTipoInstituicao_Curso = new Vector();
	}

	public void limpar() {
		_consultasDoTipoInstituicao_Curso.clear();
	}
}
