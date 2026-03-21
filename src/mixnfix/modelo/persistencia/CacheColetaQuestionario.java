package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Prova;

public class CacheColetaQuestionario {
	class ConsultaProva_ColetaQuestionario {
		public Prova _prova_ColetaQuestionario;

		public ConsultaProva_ColetaQuestionario(Prova prova_ColetaQuestionario) {
			_prova_ColetaQuestionario = prova_ColetaQuestionario;
		}
	}
	private Vector _consultasDoTipoProva_ColetaQuestionario;

	public boolean cacheProva_ColetaQuestionario(Prova prova_ColetaQuestionario) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoProva_ColetaQuestionario.size(); i++) {
			ConsultaProva_ColetaQuestionario obj = (ConsultaProva_ColetaQuestionario) _consultasDoTipoProva_ColetaQuestionario.get(i);
			achou = ((obj._prova_ColetaQuestionario == null && prova_ColetaQuestionario == null) || (obj._prova_ColetaQuestionario != null && prova_ColetaQuestionario != null && obj._prova_ColetaQuestionario.equals(prova_ColetaQuestionario)));
		}
		return achou;
	}

	public void adicionarProva_ColetaQuestionario(Prova prova_ColetaQuestionario) {
		_consultasDoTipoProva_ColetaQuestionario.add(new ConsultaProva_ColetaQuestionario(prova_ColetaQuestionario));
	}

	public CacheColetaQuestionario() {
		_consultasDoTipoProva_ColetaQuestionario = new Vector();
	}

	public void limpar() {
		_consultasDoTipoProva_ColetaQuestionario.clear();
	}
}
