package mixnfix.modelo;

public class CursoKey implements Comparable {
	private int _id_curso;

	public CursoKey(Curso obj) {
		_id_curso = obj.getId();
	}

	public CursoKey(int id_curso) {
		_id_curso = id_curso;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		CursoKey x = (CursoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_curso - x._id_curso;
		}
		return i;
	}
}
