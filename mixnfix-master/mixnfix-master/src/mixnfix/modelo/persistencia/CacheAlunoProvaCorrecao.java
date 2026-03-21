package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.ProvaCorrecao;

public class CacheAlunoProvaCorrecao {
	class ConsultaProvaCorrecao_AlunoProvaCorrecao {
		public ProvaCorrecao _provaCorrecao_AlunoProvaCorrecao;

		public ConsultaProvaCorrecao_AlunoProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) {
			_provaCorrecao_AlunoProvaCorrecao = provaCorrecao_AlunoProvaCorrecao;
		}
	}
	private Vector _consultasDoTipoProvaCorrecao_AlunoProvaCorrecao;

	public boolean cacheProvaCorrecao_AlunoProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoProvaCorrecao_AlunoProvaCorrecao.size(); i++) {
			ConsultaProvaCorrecao_AlunoProvaCorrecao obj = (ConsultaProvaCorrecao_AlunoProvaCorrecao) _consultasDoTipoProvaCorrecao_AlunoProvaCorrecao.get(i);
			achou = ((obj._provaCorrecao_AlunoProvaCorrecao == null && provaCorrecao_AlunoProvaCorrecao == null) || (obj._provaCorrecao_AlunoProvaCorrecao != null && provaCorrecao_AlunoProvaCorrecao != null && obj._provaCorrecao_AlunoProvaCorrecao.equals(provaCorrecao_AlunoProvaCorrecao)));
		}
		return achou;
	}

	public void adicionarProvaCorrecao_AlunoProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) {
		_consultasDoTipoProvaCorrecao_AlunoProvaCorrecao.add(new ConsultaProvaCorrecao_AlunoProvaCorrecao(provaCorrecao_AlunoProvaCorrecao));
	}

	public CacheAlunoProvaCorrecao() {
		_consultasDoTipoProvaCorrecao_AlunoProvaCorrecao = new Vector();
	}

	public void limpar() {
		_consultasDoTipoProvaCorrecao_AlunoProvaCorrecao.clear();
	}
}
