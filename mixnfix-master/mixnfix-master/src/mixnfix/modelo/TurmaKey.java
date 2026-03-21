package mixnfix.modelo;

public class TurmaKey implements Comparable {
	private int _id_turma;

	public TurmaKey(Turma obj) {
		_id_turma = obj.getId();
	}

	public TurmaKey(int id_turma) {
		_id_turma = id_turma;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		TurmaKey x = (TurmaKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_turma - x._id_turma;
		}
		return i;
	}
}
