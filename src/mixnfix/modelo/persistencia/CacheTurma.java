package mixnfix.modelo.persistencia;

import java.util.Vector;

import mixnfix.modelo.Instituicao;

public class CacheTurma {
	class ConsultaInstituicao_Turma {
		public Instituicao _instituicao_Turma;

		public ConsultaInstituicao_Turma(Instituicao instituicao_Turma) {
			_instituicao_Turma = instituicao_Turma;
		}
	}
	class ConsultaNomeInstituicao_Turma {
		public String _nome;
		public Instituicao _instituicao_Turma;

		public ConsultaNomeInstituicao_Turma(String nome, Instituicao instituicao_Turma) {
			_nome = nome;
			_instituicao_Turma = instituicao_Turma;
		}
	}
	private Vector _consultasDoTipoInstituicao_Turma;
	private Vector _consultasDoTipoNomeInstituicao_Turma;

	public boolean cacheInstituicao_Turma(Instituicao instituicao_Turma) {
		boolean achou = false;
		for(int i = 0; !achou && i < _consultasDoTipoInstituicao_Turma.size(); i++) {
			ConsultaInstituicao_Turma obj = (ConsultaInstituicao_Turma) _consultasDoTipoInstituicao_Turma.get(i);
			achou = ((obj._instituicao_Turma == null && instituicao_Turma == null) || (obj._instituicao_Turma != null && instituicao_Turma != null && obj._instituicao_Turma.equals(instituicao_Turma)));
		}
		return achou;
	}

	public void adicionarInstituicao_Turma(Instituicao instituicao_Turma) {
		_consultasDoTipoInstituicao_Turma.add(new ConsultaInstituicao_Turma(instituicao_Turma));
	}

	public boolean cacheNomeInstituicao_Turma(String nome, Instituicao instituicao_Turma) {
		boolean achou = false;
		achou = cacheInstituicao_Turma(instituicao_Turma);
		for(int i = 0; !achou && i < _consultasDoTipoNomeInstituicao_Turma.size(); i++) {
			ConsultaNomeInstituicao_Turma obj = (ConsultaNomeInstituicao_Turma) _consultasDoTipoNomeInstituicao_Turma.get(i);
			achou = ((obj._nome == null && nome == null) || (obj._nome != null && nome != null && obj._nome.equals(nome))) && ((obj._instituicao_Turma == null && instituicao_Turma == null) || (obj._instituicao_Turma != null && instituicao_Turma != null && obj._instituicao_Turma.equals(instituicao_Turma)));
		}
		return achou;
	}

	public void adicionarNomeInstituicao_Turma(String nome, Instituicao instituicao_Turma) {
		_consultasDoTipoNomeInstituicao_Turma.add(new ConsultaNomeInstituicao_Turma(nome, instituicao_Turma));
	}

	public CacheTurma() {
		_consultasDoTipoInstituicao_Turma = new Vector();
		_consultasDoTipoNomeInstituicao_Turma = new Vector();
	}

	public void limpar() {
		_consultasDoTipoInstituicao_Turma.clear();
		_consultasDoTipoNomeInstituicao_Turma.clear();
	}
}
