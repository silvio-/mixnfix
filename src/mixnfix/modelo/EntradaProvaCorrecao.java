package mixnfix.modelo;

import java.sql.SQLException;

public class EntradaProvaCorrecao extends PersistentObject {
	// Construtor
	public EntradaProvaCorrecao(int id, byte status, String foto, int threshold, int phase, int idImageData, int tipo, int idAnswersData, ProvaCorrecao provaCorrecao_EntradaProvaCorrecao, Aluno aluno_EntradaProvaCorrecao) {
		_id = id;
		_status = status;
		_foto = foto;
		_threshold = threshold;
		_phase = phase;
		_idImageData = idImageData;
		_tipo = tipo;
		_idAnswersData = idAnswersData;
		_provaCorrecao_EntradaProvaCorrecao = provaCorrecao_EntradaProvaCorrecao;
		_aluno_EntradaProvaCorrecao = aluno_EntradaProvaCorrecao;
	}

	// Construtor vazio
	public EntradaProvaCorrecao() {
	}

	// Atributo id
	private int _id;
	public int getId() {
		return _id;
	}
	public void setId(int id) {
		_id = id;
	}

	// Atributo status
	private byte _status;
	public byte getStatus() {
		return _status;
	}
	public void setStatus(byte status) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarStatusEmEntradaProvaCorrecao(this, status);
		}
		_status = status;
	}

	// Atributo foto
	private String _foto;
	public String getFoto() {
		return _foto;
	}
	public void setFoto(String foto) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarFotoEmEntradaProvaCorrecao(this, foto);
		}
		_foto = foto;
	}

	// Atributo threshold
	private int _threshold;
	public int getThreshold() {
		return _threshold;
	}
	public void setThreshold(int threshold) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarThresholdEmEntradaProvaCorrecao(this, threshold);
		}
		_threshold = threshold;
	}

	// Atributo phase
	private int _phase;
	public int getPhase() {
		return _phase;
	}
	public void setPhase(int phase) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarPhaseEmEntradaProvaCorrecao(this, phase);
		}
		_phase = phase;
	}

	// Atributo idImageData
	private int _idImageData;
	public int getIdImageData() {
		return _idImageData;
	}
	public void setIdImageData(int idImageData) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarIdImageDataEmEntradaProvaCorrecao(this, idImageData);
		}
		_idImageData = idImageData;
	}

	// Atributo tipo
	private int _tipo;
	public int getTipo() {
		return _tipo;
	}
	public void setTipo(int tipo) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarTipoEmEntradaProvaCorrecao(this, tipo);
		}
		_tipo = tipo;
	}

	// Atributo idAnswersData
	private int _idAnswersData;
	public int getIdAnswersData() {
		return _idAnswersData;
	}
	public void setIdAnswersData(int idAnswersData) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarIdAnswersDataEmEntradaProvaCorrecao(this, idAnswersData);
		}
		_idAnswersData = idAnswersData;
	}

	// Atributo provaCorrecao_EntradaProvaCorrecao
	private ProvaCorrecao _provaCorrecao_EntradaProvaCorrecao;
	public ProvaCorrecao getProvaCorrecao_EntradaProvaCorrecao() {
		return _provaCorrecao_EntradaProvaCorrecao;
	}
	public void setProvaCorrecao_EntradaProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarProvaCorrecao_EntradaProvaCorrecaoEmEntradaProvaCorrecao(this, provaCorrecao_EntradaProvaCorrecao);
		}
		_provaCorrecao_EntradaProvaCorrecao = provaCorrecao_EntradaProvaCorrecao;
	}

	// Atributo aluno_EntradaProvaCorrecao
	private Aluno _aluno_EntradaProvaCorrecao;
	public Aluno getAluno_EntradaProvaCorrecao() {
		return _aluno_EntradaProvaCorrecao;
	}
	public void setAluno_EntradaProvaCorrecao(Aluno aluno_EntradaProvaCorrecao) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarAluno_EntradaProvaCorrecaoEmEntradaProvaCorrecao(this, aluno_EntradaProvaCorrecao);
		}
		_aluno_EntradaProvaCorrecao = aluno_EntradaProvaCorrecao;
	}

	// Método para obter a chave do objeto. 
	public EntradaProvaCorrecaoKey getKey() {
		return new EntradaProvaCorrecaoKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof EntradaProvaCorrecao) {
			EntradaProvaCorrecao x = (EntradaProvaCorrecao) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("EntradaProvaCorrecao\n");
		b.append("id: " + _id + "\n");
		b.append("status: " + _status + "\n");
		b.append("foto: \"" + _foto + "\"\n");
		b.append("threshold: " + _threshold + "\n");
		b.append("phase: " + _phase + "\n");
		b.append("idImageData: " + _idImageData + "\n");
		b.append("tipo: " + _tipo + "\n");
		b.append("idAnswersData: " + _idAnswersData + "\n");
		b.append("provaCorrecao_EntradaProvaCorrecao: \n" + Util.indent(_provaCorrecao_EntradaProvaCorrecao.toString()));
		b.append("aluno_EntradaProvaCorrecao: \n" + Util.indent(_aluno_EntradaProvaCorrecao.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public EntradaProvaCorrecao getTransitoryCopy() {
		return new EntradaProvaCorrecao(_id, _status, _foto, _threshold, _phase, _idImageData, _tipo, _idAnswersData, _provaCorrecao_EntradaProvaCorrecao, _aluno_EntradaProvaCorrecao);
	}
}
