package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.ColetaQuestionario;

public class CacheEntradaColetaQuestionario {
	class ConsultaColetaQuestionario_EntradaColetaQuestionario {
		public ColetaQuestionario _coletaQuestionario_EntradaColetaQuestionario;

		public ConsultaColetaQuestionario_EntradaColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) {
			_coletaQuestionario_EntradaColetaQuestionario = coletaQuestionario_EntradaColetaQuestionario;
		}
	}
	private Vector _consultasDoTipoColetaQuestionario_EntradaColetaQuestionario;

	public boolean cacheColetaQuestionario_EntradaColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoColetaQuestionario_EntradaColetaQuestionario.size(); i++) {
			ConsultaColetaQuestionario_EntradaColetaQuestionario obj = (ConsultaColetaQuestionario_EntradaColetaQuestionario) _consultasDoTipoColetaQuestionario_EntradaColetaQuestionario.get(i);
			achou = ((obj._coletaQuestionario_EntradaColetaQuestionario == null && coletaQuestionario_EntradaColetaQuestionario == null) || (obj._coletaQuestionario_EntradaColetaQuestionario != null && coletaQuestionario_EntradaColetaQuestionario != null && obj._coletaQuestionario_EntradaColetaQuestionario.equals(coletaQuestionario_EntradaColetaQuestionario)));
		}
		return achou;
	}

	public void adicionarColetaQuestionario_EntradaColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) {
		_consultasDoTipoColetaQuestionario_EntradaColetaQuestionario.add(new ConsultaColetaQuestionario_EntradaColetaQuestionario(coletaQuestionario_EntradaColetaQuestionario));
	}

	public CacheEntradaColetaQuestionario() {
		_consultasDoTipoColetaQuestionario_EntradaColetaQuestionario = new Vector();
	}

	public void limpar() {
		_consultasDoTipoColetaQuestionario_EntradaColetaQuestionario.clear();
	}
}
