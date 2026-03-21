package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.Correcao;

public class CacheAvaliacaoAluno {
	class ConsultaAvaliacao_AvaliacaoAluno {
		public Avaliacao _avaliacao_AvaliacaoAluno;

		public ConsultaAvaliacao_AvaliacaoAluno(Avaliacao avaliacao_AvaliacaoAluno) {
			_avaliacao_AvaliacaoAluno = avaliacao_AvaliacaoAluno;
		}
	}
	class ConsultaCorrecao_AvaliacaoAluno {
		public Correcao _correcao_AvaliacaoAluno;

		public ConsultaCorrecao_AvaliacaoAluno(Correcao correcao_AvaliacaoAluno) {
			_correcao_AvaliacaoAluno = correcao_AvaliacaoAluno;
		}
	}
	class ConsultaAlunoCursoInstancia_AvaliacaoAluno {
		public AlunoCursoInstancia _alunoCursoInstancia_AvaliacaoAluno;

		public ConsultaAlunoCursoInstancia_AvaliacaoAluno(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) {
			_alunoCursoInstancia_AvaliacaoAluno = alunoCursoInstancia_AvaliacaoAluno;
		}
	}
	private Vector _consultasDoTipoAvaliacao_AvaliacaoAluno;
	private Vector _consultasDoTipoCorrecao_AvaliacaoAluno;
	private Vector _consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno;

	public boolean cacheAvaliacao_AvaliacaoAluno(Avaliacao avaliacao_AvaliacaoAluno) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoAvaliacao_AvaliacaoAluno.size(); i++) {
			ConsultaAvaliacao_AvaliacaoAluno obj = (ConsultaAvaliacao_AvaliacaoAluno) _consultasDoTipoAvaliacao_AvaliacaoAluno.get(i);
			achou = ((obj._avaliacao_AvaliacaoAluno == null && avaliacao_AvaliacaoAluno == null) || (obj._avaliacao_AvaliacaoAluno != null && avaliacao_AvaliacaoAluno != null && obj._avaliacao_AvaliacaoAluno.equals(avaliacao_AvaliacaoAluno)));
		}
		return achou;
	}

	public void adicionarAvaliacao_AvaliacaoAluno(Avaliacao avaliacao_AvaliacaoAluno) {
		_consultasDoTipoAvaliacao_AvaliacaoAluno.add(new ConsultaAvaliacao_AvaliacaoAluno(avaliacao_AvaliacaoAluno));
	}

	public boolean cacheCorrecao_AvaliacaoAluno(Correcao correcao_AvaliacaoAluno) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoCorrecao_AvaliacaoAluno.size(); i++) {
			ConsultaCorrecao_AvaliacaoAluno obj = (ConsultaCorrecao_AvaliacaoAluno) _consultasDoTipoCorrecao_AvaliacaoAluno.get(i);
			achou = ((obj._correcao_AvaliacaoAluno == null && correcao_AvaliacaoAluno == null) || (obj._correcao_AvaliacaoAluno != null && correcao_AvaliacaoAluno != null && obj._correcao_AvaliacaoAluno.equals(correcao_AvaliacaoAluno)));
		}
		return achou;
	}

	public void adicionarCorrecao_AvaliacaoAluno(Correcao correcao_AvaliacaoAluno) {
		_consultasDoTipoCorrecao_AvaliacaoAluno.add(new ConsultaCorrecao_AvaliacaoAluno(correcao_AvaliacaoAluno));
	}

	public boolean cacheAlunoCursoInstancia_AvaliacaoAluno(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno.size(); i++) {
			ConsultaAlunoCursoInstancia_AvaliacaoAluno obj = (ConsultaAlunoCursoInstancia_AvaliacaoAluno) _consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno.get(i);
			achou = ((obj._alunoCursoInstancia_AvaliacaoAluno == null && alunoCursoInstancia_AvaliacaoAluno == null) || (obj._alunoCursoInstancia_AvaliacaoAluno != null && alunoCursoInstancia_AvaliacaoAluno != null && obj._alunoCursoInstancia_AvaliacaoAluno.equals(alunoCursoInstancia_AvaliacaoAluno)));
		}
		return achou;
	}

	public void adicionarAlunoCursoInstancia_AvaliacaoAluno(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) {
		_consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno.add(new ConsultaAlunoCursoInstancia_AvaliacaoAluno(alunoCursoInstancia_AvaliacaoAluno));
	}

	public CacheAvaliacaoAluno() {
		_consultasDoTipoAvaliacao_AvaliacaoAluno = new Vector();
		_consultasDoTipoCorrecao_AvaliacaoAluno = new Vector();
		_consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno = new Vector();
	}

	public void limpar() {
		_consultasDoTipoAvaliacao_AvaliacaoAluno.clear();
		_consultasDoTipoCorrecao_AvaliacaoAluno.clear();
		_consultasDoTipoAlunoCursoInstancia_AvaliacaoAluno.clear();
	}
}
