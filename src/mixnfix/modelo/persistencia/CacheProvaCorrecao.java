package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Prova;

public class CacheProvaCorrecao {
	class ConsultaProva_ProvaCorrecao {
		public Prova _prova_ProvaCorrecao;

		public ConsultaProva_ProvaCorrecao(Prova prova_ProvaCorrecao) {
			_prova_ProvaCorrecao = prova_ProvaCorrecao;
		}
	}
	private Vector _consultasDoTipoProva_ProvaCorrecao;

	public boolean cacheProva_ProvaCorrecao(Prova prova_ProvaCorrecao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoProva_ProvaCorrecao.size(); i++) {
			ConsultaProva_ProvaCorrecao obj = (ConsultaProva_ProvaCorrecao) _consultasDoTipoProva_ProvaCorrecao.get(i);
			achou = ((obj._prova_ProvaCorrecao == null && prova_ProvaCorrecao == null) || (obj._prova_ProvaCorrecao != null && prova_ProvaCorrecao != null && obj._prova_ProvaCorrecao.equals(prova_ProvaCorrecao)));
		}
		return achou;
	}

	public void adicionarProva_ProvaCorrecao(Prova prova_ProvaCorrecao) {
		_consultasDoTipoProva_ProvaCorrecao.add(new ConsultaProva_ProvaCorrecao(prova_ProvaCorrecao));
	}

	public CacheProvaCorrecao() {
		_consultasDoTipoProva_ProvaCorrecao = new Vector();
	}

	public void limpar() {
		_consultasDoTipoProva_ProvaCorrecao.clear();
	}
}
