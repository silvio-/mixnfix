package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.CursoInstancia;

public class CacheAvaliacao {
	class ConsultaTipo {
		public String _tipo;

		public ConsultaTipo(String tipo) {
			_tipo = tipo;
		}
	}
	class ConsultaCursoInstancia_Avaliacao {
		public CursoInstancia _cursoInstancia_Avaliacao;

		public ConsultaCursoInstancia_Avaliacao(CursoInstancia cursoInstancia_Avaliacao) {
			_cursoInstancia_Avaliacao = cursoInstancia_Avaliacao;
		}
	}
	private Vector _consultasDoTipoTipo;
	private Vector _consultasDoTipoCursoInstancia_Avaliacao;

	public boolean cacheTipo(String tipo) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoTipo.size(); i++) {
			ConsultaTipo obj = (ConsultaTipo) _consultasDoTipoTipo.get(i);
			achou = ((obj._tipo == null && tipo == null) || (obj._tipo != null && tipo != null && obj._tipo.equals(tipo)));
		}
		return achou;
	}

	public void adicionarTipo(String tipo) {
		_consultasDoTipoTipo.add(new ConsultaTipo(tipo));
	}

	public boolean cacheCursoInstancia_Avaliacao(CursoInstancia cursoInstancia_Avaliacao) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoCursoInstancia_Avaliacao.size(); i++) {
			ConsultaCursoInstancia_Avaliacao obj = (ConsultaCursoInstancia_Avaliacao) _consultasDoTipoCursoInstancia_Avaliacao.get(i);
			achou = ((obj._cursoInstancia_Avaliacao == null && cursoInstancia_Avaliacao == null) || (obj._cursoInstancia_Avaliacao != null && cursoInstancia_Avaliacao != null && obj._cursoInstancia_Avaliacao.equals(cursoInstancia_Avaliacao)));
		}
		return achou;
	}

	public void adicionarCursoInstancia_Avaliacao(CursoInstancia cursoInstancia_Avaliacao) {
		_consultasDoTipoCursoInstancia_Avaliacao.add(new ConsultaCursoInstancia_Avaliacao(cursoInstancia_Avaliacao));
	}

	public CacheAvaliacao() {
		_consultasDoTipoTipo = new Vector();
		_consultasDoTipoCursoInstancia_Avaliacao = new Vector();
	}

	public void limpar() {
		_consultasDoTipoTipo.clear();
		_consultasDoTipoCursoInstancia_Avaliacao.clear();
	}
}
