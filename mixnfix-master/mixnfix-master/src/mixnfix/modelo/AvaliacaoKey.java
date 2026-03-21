package mixnfix.modelo;

public class AvaliacaoKey implements Comparable {
	private int _id_avaliacao;

	public AvaliacaoKey(Avaliacao obj) {
		_id_avaliacao = obj.getId();
	}

	public AvaliacaoKey(int id_avaliacao) {
		_id_avaliacao = id_avaliacao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AvaliacaoKey x = (AvaliacaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_avaliacao - x._id_avaliacao;
		}
		return i;
	}
}
