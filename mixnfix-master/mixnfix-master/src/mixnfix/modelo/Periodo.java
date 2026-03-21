package mixnfix.modelo;

import java.sql.SQLException;

public class Periodo extends PersistentObject {
	// Construtor
	public Periodo(String id_periodo) {
		_id_periodo = id_periodo;
	}

	// Construtor vazio
	public Periodo() {
	}

	// Atributo id_periodo
	private String _id_periodo;
	public String getId_periodo() {
		return _id_periodo;
	}
	public void setId_periodo(String id_periodo) throws SQLException {
		if(isPersistent()) {
			RepositorioLink.getInstance().getRepositorio().atualizarId_periodoEmPeriodo(this, id_periodo);
		}
		_id_periodo = id_periodo;
	}

	// Método para obter a chave do objeto. 
	public PeriodoKey getKey() {
		return new PeriodoKey(_id_periodo);
	}

	// Método para determinar se outro objeto é igual a este.
	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Periodo) {
			Periodo x = (Periodo) obj;
			eq = getKey().equals(x.getKey());
		}
		return eq;
	}

	// Método para gerar uma string descrevendo o objeto.
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Periodo\n");
		b.append("id_periodo: \"" + _id_periodo + "\"\n");
		return b.toString();
	}

	// Método para copiar o objeto.
	public Periodo getTransitoryCopy() {
		return new Periodo(_id_periodo);
	}
}
