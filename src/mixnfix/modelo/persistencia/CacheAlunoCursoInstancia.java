package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.CursoInstancia;

public class CacheAlunoCursoInstancia {
	class ConsultaCursoInstancia_AlunoCursoInstancia {
		public CursoInstancia _cursoInstancia_AlunoCursoInstancia;

		public ConsultaCursoInstancia_AlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) {
			_cursoInstancia_AlunoCursoInstancia = cursoInstancia_AlunoCursoInstancia;
		}
	}
	class ConsultaAluno_AlunoCursoInstancia {
		public Aluno _aluno_AlunoCursoInstancia;

		public ConsultaAluno_AlunoCursoInstancia(Aluno aluno_AlunoCursoInstancia) {
			_aluno_AlunoCursoInstancia = aluno_AlunoCursoInstancia;
		}
	}
	private Vector _consultasDoTipoCursoInstancia_AlunoCursoInstancia;
	private Vector _consultasDoTipoAluno_AlunoCursoInstancia;

	public boolean cacheCursoInstancia_AlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoCursoInstancia_AlunoCursoInstancia.size(); i++) {
			ConsultaCursoInstancia_AlunoCursoInstancia obj = (ConsultaCursoInstancia_AlunoCursoInstancia) _consultasDoTipoCursoInstancia_AlunoCursoInstancia.get(i);
			achou = ((obj._cursoInstancia_AlunoCursoInstancia == null && cursoInstancia_AlunoCursoInstancia == null) || (obj._cursoInstancia_AlunoCursoInstancia != null && cursoInstancia_AlunoCursoInstancia != null && obj._cursoInstancia_AlunoCursoInstancia.equals(cursoInstancia_AlunoCursoInstancia)));
		}
		return achou;
	}

	public void adicionarCursoInstancia_AlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) {
		_consultasDoTipoCursoInstancia_AlunoCursoInstancia.add(new ConsultaCursoInstancia_AlunoCursoInstancia(cursoInstancia_AlunoCursoInstancia));
	}

	public boolean cacheAluno_AlunoCursoInstancia(Aluno aluno_AlunoCursoInstancia) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoAluno_AlunoCursoInstancia.size(); i++) {
			ConsultaAluno_AlunoCursoInstancia obj = (ConsultaAluno_AlunoCursoInstancia) _consultasDoTipoAluno_AlunoCursoInstancia.get(i);
			achou = ((obj._aluno_AlunoCursoInstancia == null && aluno_AlunoCursoInstancia == null) || (obj._aluno_AlunoCursoInstancia != null && aluno_AlunoCursoInstancia != null && obj._aluno_AlunoCursoInstancia.equals(aluno_AlunoCursoInstancia)));
		}
		return achou;
	}

	public void adicionarAluno_AlunoCursoInstancia(Aluno aluno_AlunoCursoInstancia) {
		_consultasDoTipoAluno_AlunoCursoInstancia.add(new ConsultaAluno_AlunoCursoInstancia(aluno_AlunoCursoInstancia));
	}

	public CacheAlunoCursoInstancia() {
		_consultasDoTipoCursoInstancia_AlunoCursoInstancia = new Vector();
		_consultasDoTipoAluno_AlunoCursoInstancia = new Vector();
	}

	public void limpar() {
		_consultasDoTipoCursoInstancia_AlunoCursoInstancia.clear();
		_consultasDoTipoAluno_AlunoCursoInstancia.clear();
	}
}
