package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Curso;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Periodo;

public class CacheCursoInstancia {
	class ConsultaInstituicao_CursoInstancia {
		public Instituicao _instituicao_CursoInstancia;

		public ConsultaInstituicao_CursoInstancia(Instituicao instituicao_CursoInstancia) {
			_instituicao_CursoInstancia = instituicao_CursoInstancia;
		}
	}
	class ConsultaCurso_CursoInstancia {
		public Curso _curso_CursoInstancia;

		public ConsultaCurso_CursoInstancia(Curso curso_CursoInstancia) {
			_curso_CursoInstancia = curso_CursoInstancia;
		}
	}
	class ConsultaPeriodo_CursoInstancia {
		public Periodo _periodo_CursoInstancia;

		public ConsultaPeriodo_CursoInstancia(Periodo periodo_CursoInstancia) {
			_periodo_CursoInstancia = periodo_CursoInstancia;
		}
	}
	private Vector _consultasDoTipoInstituicao_CursoInstancia;
	private Vector _consultasDoTipoCurso_CursoInstancia;
	private Vector _consultasDoTipoPeriodo_CursoInstancia;

	public boolean cacheInstituicao_CursoInstancia(Instituicao instituicao_CursoInstancia) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoInstituicao_CursoInstancia.size(); i++) {
			ConsultaInstituicao_CursoInstancia obj = (ConsultaInstituicao_CursoInstancia) _consultasDoTipoInstituicao_CursoInstancia.get(i);
			achou = ((obj._instituicao_CursoInstancia == null && instituicao_CursoInstancia == null) || (obj._instituicao_CursoInstancia != null && instituicao_CursoInstancia != null && obj._instituicao_CursoInstancia.equals(instituicao_CursoInstancia)));
		}
		return achou;
	}

	public void adicionarInstituicao_CursoInstancia(Instituicao instituicao_CursoInstancia) {
		_consultasDoTipoInstituicao_CursoInstancia.add(new ConsultaInstituicao_CursoInstancia(instituicao_CursoInstancia));
	}

	public boolean cacheCurso_CursoInstancia(Curso curso_CursoInstancia) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoCurso_CursoInstancia.size(); i++) {
			ConsultaCurso_CursoInstancia obj = (ConsultaCurso_CursoInstancia) _consultasDoTipoCurso_CursoInstancia.get(i);
			achou = ((obj._curso_CursoInstancia == null && curso_CursoInstancia == null) || (obj._curso_CursoInstancia != null && curso_CursoInstancia != null && obj._curso_CursoInstancia.equals(curso_CursoInstancia)));
		}
		return achou;
	}

	public void adicionarCurso_CursoInstancia(Curso curso_CursoInstancia) {
		_consultasDoTipoCurso_CursoInstancia.add(new ConsultaCurso_CursoInstancia(curso_CursoInstancia));
	}

	public boolean cachePeriodo_CursoInstancia(Periodo periodo_CursoInstancia) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoPeriodo_CursoInstancia.size(); i++) {
			ConsultaPeriodo_CursoInstancia obj = (ConsultaPeriodo_CursoInstancia) _consultasDoTipoPeriodo_CursoInstancia.get(i);
			achou = ((obj._periodo_CursoInstancia == null && periodo_CursoInstancia == null) || (obj._periodo_CursoInstancia != null && periodo_CursoInstancia != null && obj._periodo_CursoInstancia.equals(periodo_CursoInstancia)));
		}
		return achou;
	}

	public void adicionarPeriodo_CursoInstancia(Periodo periodo_CursoInstancia) {
		_consultasDoTipoPeriodo_CursoInstancia.add(new ConsultaPeriodo_CursoInstancia(periodo_CursoInstancia));
	}

	public CacheCursoInstancia() {
		_consultasDoTipoInstituicao_CursoInstancia = new Vector();
		_consultasDoTipoCurso_CursoInstancia = new Vector();
		_consultasDoTipoPeriodo_CursoInstancia = new Vector();
	}

	public void limpar() {
		_consultasDoTipoInstituicao_CursoInstancia.clear();
		_consultasDoTipoCurso_CursoInstancia.clear();
		_consultasDoTipoPeriodo_CursoInstancia.clear();
	}
}
