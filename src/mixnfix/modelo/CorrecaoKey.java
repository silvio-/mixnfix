package mixnfix.modelo;

public class CorrecaoKey implements Comparable {
	private int _id_correcao;

	public CorrecaoKey(Correcao obj) {
		_id_correcao = obj.getId();
	}

	public CorrecaoKey(int id_correcao) {
		_id_correcao = id_correcao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		CorrecaoKey x = (CorrecaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_correcao - x._id_correcao;
		}
		return i;
	}
}
