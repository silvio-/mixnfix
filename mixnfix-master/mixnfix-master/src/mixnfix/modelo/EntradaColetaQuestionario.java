package mixnfix.modelo;

import java.sql.SQLException;

public class EntradaColetaQuestionario extends PersistentObject {
	// Construtor
	public EntradaColetaQuestionario(int id, byte status, String foto, int threshold, int phase, int idImageData, int tipoFolhaResposta, int idAnswersData, ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) {
		_id = id;
		_status = status;
		_foto = foto;
		_threshold = threshold;
		_phase = phase;
		_idImageData = idImageData;
		_tipoFolhaResposta = tipoFolhaResposta;
		_idAnswersData = idAnswersData;
		_coletaQuestionario_EntradaColetaQuestionario = coletaQuestionario_EntradaColetaQuestionario;
	}

	// Construtor vazio
	public EntradaColetaQuestionario() {
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
			RepositorioLink.getInstance().getRepositorio().atualizarStatusEmEntradaColetaQuestionario(this, status);
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
			RepositorioLink.getInstance().getRepositorio().atualizarFotoEmEntradaColetaQuestionario(this, foto);
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
			RepositorioLink.getInstance().getRepositorio().atualizarThresholdEmEntradaColetaQuestionario(this, threshold);
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
			RepositorioLink.getInstance().getRepositorio().atualizarPhaseEmEntradaColetaQuestionario(this, phase);
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
			RepositorioLink.getInstance().getRepositorio().atualizarIdImageDataEmEntradaColetaQuestionario(this, idImageData);
		}
		_idImageData = idImageData;
	}

	// Atributo tipoFolhaResposta
	private int _tipoFolhaResposta;
	public int getTipoFolhaResposta() {
		return _tipoFolhaResposta;
	}
	public void setTipoFolhaResposta(int tipoFolhaResposta) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarTipoFolhaRespostaEmEntradaColetaQuestionario(this, tipoFolhaResposta);
		}
		_tipoFolhaResposta = tipoFolhaResposta;
	}

	// Atributo idAnswersData
	private int _idAnswersData;
	public int getIdAnswersData() {
		return _idAnswersData;
	}
	public void setIdAnswersData(int idAnswersData) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarIdAnswersDataEmEntradaColetaQuestionario(this, idAnswersData);
		}
		_idAnswersData = idAnswersData;
	}

	// Atributo coletaQuestionario_EntradaColetaQuestionario
	private ColetaQuestionario _coletaQuestionario_EntradaColetaQuestionario;
	public ColetaQuestionario getColetaQuestionario_EntradaColetaQuestionario() {
		return _coletaQuestionario_EntradaColetaQuestionario;
	}
	public void setColetaQuestionario_EntradaColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarColetaQuestionario_EntradaColetaQuestionarioEmEntradaColetaQuestionario(this, coletaQuestionario_EntradaColetaQuestionario);
		}
		_coletaQuestionario_EntradaColetaQuestionario = coletaQuestionario_EntradaColetaQuestionario;
	}

	// Método para obter a chave do objeto. 
	public EntradaColetaQuestionarioKey getKey() {
		return new EntradaColetaQuestionarioKey(_id);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof EntradaColetaQuestionario) {
			EntradaColetaQuestionario x = (EntradaColetaQuestionario) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("EntradaColetaQuestionario\n");
		b.append("id: " + _id + "\n");
		b.append("status: " + _status + "\n");
		b.append("foto: \"" + _foto + "\"\n");
		b.append("threshold: " + _threshold + "\n");
		b.append("phase: " + _phase + "\n");
		b.append("idImageData: " + _idImageData + "\n");
		b.append("tipoFolhaResposta: " + _tipoFolhaResposta + "\n");
		b.append("idAnswersData: " + _idAnswersData + "\n");
		b.append("coletaQuestionario_EntradaColetaQuestionario: \n" + Util.indent(_coletaQuestionario_EntradaColetaQuestionario.toString()));
		return b.toString();
	}

	// Método para copiar o objeto.
	public EntradaColetaQuestionario getTransitoryCopy() {
		return new EntradaColetaQuestionario(_id, _status, _foto, _threshold, _phase, _idImageData, _tipoFolhaResposta, _idAnswersData, _coletaQuestionario_EntradaColetaQuestionario);
	}
}
