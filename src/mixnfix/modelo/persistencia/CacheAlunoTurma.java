package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.Turma;

public class CacheAlunoTurma {
	class ConsultaTurma_AlunoTurma {
		public Turma _turma_AlunoTurma;

		public ConsultaTurma_AlunoTurma(Turma turma_AlunoTurma) {
			_turma_AlunoTurma = turma_AlunoTurma;
		}
	}
	class ConsultaAluno_AlunoTurma {
		public Aluno _aluno_AlunoTurma;

		public ConsultaAluno_AlunoTurma(Aluno aluno_AlunoTurma) {
			_aluno_AlunoTurma = aluno_AlunoTurma;
		}
	}
	private Vector _consultasDoTipoTurma_AlunoTurma;
	private Vector _consultasDoTipoAluno_AlunoTurma;

	public boolean cacheTurma_AlunoTurma(Turma turma_AlunoTurma) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoTurma_AlunoTurma.size(); i++) {
			ConsultaTurma_AlunoTurma obj = (ConsultaTurma_AlunoTurma) _consultasDoTipoTurma_AlunoTurma.get(i);
			achou = ((obj._turma_AlunoTurma == null && turma_AlunoTurma == null) || (obj._turma_AlunoTurma != null && turma_AlunoTurma != null && obj._turma_AlunoTurma.equals(turma_AlunoTurma)));
		}
		return achou;
	}

	public void adicionarTurma_AlunoTurma(Turma turma_AlunoTurma) {
		_consultasDoTipoTurma_AlunoTurma.add(new ConsultaTurma_AlunoTurma(turma_AlunoTurma));
	}

	public boolean cacheAluno_AlunoTurma(Aluno aluno_AlunoTurma) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoAluno_AlunoTurma.size(); i++) {
			ConsultaAluno_AlunoTurma obj = (ConsultaAluno_AlunoTurma) _consultasDoTipoAluno_AlunoTurma.get(i);
			achou = ((obj._aluno_AlunoTurma == null && aluno_AlunoTurma == null) || (obj._aluno_AlunoTurma != null && aluno_AlunoTurma != null && obj._aluno_AlunoTurma.equals(aluno_AlunoTurma)));
		}
		return achou;
	}

	public void adicionarAluno_AlunoTurma(Aluno aluno_AlunoTurma) {
		_consultasDoTipoAluno_AlunoTurma.add(new ConsultaAluno_AlunoTurma(aluno_AlunoTurma));
	}

	public CacheAlunoTurma() {
		_consultasDoTipoTurma_AlunoTurma = new Vector();
		_consultasDoTipoAluno_AlunoTurma = new Vector();
	}

	public void limpar() {
		_consultasDoTipoTurma_AlunoTurma.clear();
		_consultasDoTipoAluno_AlunoTurma.clear();
	}
}
