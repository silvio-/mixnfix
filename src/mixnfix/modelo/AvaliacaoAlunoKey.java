package mixnfix.modelo;

public class AvaliacaoAlunoKey implements Comparable {
	private int _id_avaliacaoaluno;

	public AvaliacaoAlunoKey(AvaliacaoAluno obj) {
		_id_avaliacaoaluno = obj.getId();
	}

	public AvaliacaoAlunoKey(int id_avaliacaoaluno) {
		_id_avaliacaoaluno = id_avaliacaoaluno;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AvaliacaoAlunoKey x = (AvaliacaoAlunoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_avaliacaoaluno - x._id_avaliacaoaluno;
		}
		return i;
	}
}
