package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Instituicao;

public class CacheProva {
	class ConsultaInstituicao_Prova {
		public Instituicao _instituicao_Prova;

		public ConsultaInstituicao_Prova(Instituicao instituicao_Prova) {
			_instituicao_Prova = instituicao_Prova;
		}
	}
	private Vector _consultasDoTipoInstituicao_Prova;

	public boolean cacheInstituicao_Prova(Instituicao instituicao_Prova) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoInstituicao_Prova.size(); i++) {
			ConsultaInstituicao_Prova obj = (ConsultaInstituicao_Prova) _consultasDoTipoInstituicao_Prova.get(i);
			achou = ((obj._instituicao_Prova == null && instituicao_Prova == null) || (obj._instituicao_Prova != null && instituicao_Prova != null && obj._instituicao_Prova.equals(instituicao_Prova)));
		}
		return achou;
	}

	public void adicionarInstituicao_Prova(Instituicao instituicao_Prova) {
		_consultasDoTipoInstituicao_Prova.add(new ConsultaInstituicao_Prova(instituicao_Prova));
	}

	public CacheProva() {
		_consultasDoTipoInstituicao_Prova = new Vector();
	}

	public void limpar() {
		_consultasDoTipoInstituicao_Prova.clear();
	}
}
