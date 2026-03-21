package mixnfix.modelo;

public class AlunoKey implements Comparable {
	private int _id_aluno;

	public AlunoKey(Aluno obj) {
		_id_aluno = obj.getId();
	}

	public AlunoKey(int id_aluno) {
		_id_aluno = id_aluno;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AlunoKey x = (AlunoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_aluno - x._id_aluno;
		}
		return i;
	}
}
