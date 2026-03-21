package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.ProvaCorrecao;

public class CacheEntradaProvaCorrecao {
	class ConsultaProvaCorrecao_EntradaProvaCorrecao {
		public ProvaCorrecao _provaCorrecao_EntradaProvaCorrecao;

		public ConsultaProvaCorrecao_EntradaProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) {
			_provaCorrecao_EntradaProvaCorrecao = provaCorrecao_EntradaProvaCorrecao;
		}
	}
	private Vector _consultasDoTipoProvaCorrecao_EntradaProvaCorrecao;

	public boolean cacheProvaCorrecao_EntradaProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoProvaCorrecao_EntradaProvaCorrecao.size(); i++) {
			ConsultaProvaCorrecao_EntradaProvaCorrecao obj = (ConsultaProvaCorrecao_EntradaProvaCorrecao) _consultasDoTipoProvaCorrecao_EntradaProvaCorrecao.get(i);
			achou = ((obj._provaCorrecao_EntradaProvaCorrecao == null && provaCorrecao_EntradaProvaCorrecao == null) || (obj._provaCorrecao_EntradaProvaCorrecao != null && provaCorrecao_EntradaProvaCorrecao != null && obj._provaCorrecao_EntradaProvaCorrecao.equals(provaCorrecao_EntradaProvaCorrecao)));
		}
		return achou;
	}

	public void adicionarProvaCorrecao_EntradaProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) {
		_consultasDoTipoProvaCorrecao_EntradaProvaCorrecao.add(new ConsultaProvaCorrecao_EntradaProvaCorrecao(provaCorrecao_EntradaProvaCorrecao));
	}

	public CacheEntradaProvaCorrecao() {
		_consultasDoTipoProvaCorrecao_EntradaProvaCorrecao = new Vector();
	}

	public void limpar() {
		_consultasDoTipoProvaCorrecao_EntradaProvaCorrecao.clear();
	}
}
