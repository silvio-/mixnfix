package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Aluno;
import mixnfix.modelo.Prova;

public class CacheCorrecao {
	class ConsultaAluno_Correcao {
		public Aluno _aluno_Correcao;

		public ConsultaAluno_Correcao(Aluno aluno_Correcao) {
			_aluno_Correcao = aluno_Correcao;
		}
	}
	class ConsultaProva_Correcao {
		public Prova _prova_Correcao;

		public ConsultaProva_Correcao(Prova prova_Correcao) {
			_prova_Correcao = prova_Correcao;
		}
	}
	private Vector _consultasDoTipoAluno_Correcao;
	private Vector _consultasDoTipoProva_Correcao;

	public boolean cacheAluno_Correcao(Aluno aluno_Correcao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoAluno_Correcao.size(); i++) {
			ConsultaAluno_Correcao obj = (ConsultaAluno_Correcao) _consultasDoTipoAluno_Correcao.get(i);
			achou = ((obj._aluno_Correcao == null && aluno_Correcao == null) || (obj._aluno_Correcao != null && aluno_Correcao != null && obj._aluno_Correcao.equals(aluno_Correcao)));
		}
		return achou;
	}

	public void adicionarAluno_Correcao(Aluno aluno_Correcao) {
		_consultasDoTipoAluno_Correcao.add(new ConsultaAluno_Correcao(aluno_Correcao));
	}

	public boolean cacheProva_Correcao(Prova prova_Correcao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoProva_Correcao.size(); i++) {
			ConsultaProva_Correcao obj = (ConsultaProva_Correcao) _consultasDoTipoProva_Correcao.get(i);
			achou = ((obj._prova_Correcao == null && prova_Correcao == null) || (obj._prova_Correcao != null && prova_Correcao != null && obj._prova_Correcao.equals(prova_Correcao)));
		}
		return achou;
	}

	public void adicionarProva_Correcao(Prova prova_Correcao) {
		_consultasDoTipoProva_Correcao.add(new ConsultaProva_Correcao(prova_Correcao));
	}

	public CacheCorrecao() {
		_consultasDoTipoAluno_Correcao = new Vector();
		_consultasDoTipoProva_Correcao = new Vector();
	}

	public void limpar() {
		_consultasDoTipoAluno_Correcao.clear();
		_consultasDoTipoProva_Correcao.clear();
	}
}
