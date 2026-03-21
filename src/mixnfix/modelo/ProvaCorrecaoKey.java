package mixnfix.modelo;

public class ProvaCorrecaoKey implements Comparable {
	private int _id_provacorrecao;

	public ProvaCorrecaoKey(ProvaCorrecao obj) {
		_id_provacorrecao = obj.getId();
	}

	public ProvaCorrecaoKey(int id_provacorrecao) {
		_id_provacorrecao = id_provacorrecao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		ProvaCorrecaoKey x = (ProvaCorrecaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_provacorrecao - x._id_provacorrecao;
		}
		return i;
	}
}
