package mixnfix.modelo;

public class CursoInstanciaKey implements Comparable {
	private int _id_cursoinstancia;

	public CursoInstanciaKey(CursoInstancia obj) {
		_id_cursoinstancia = obj.getId();
	}

	public CursoInstanciaKey(int id_cursoinstancia) {
		_id_cursoinstancia = id_cursoinstancia;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		CursoInstanciaKey x = (CursoInstanciaKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_cursoinstancia - x._id_cursoinstancia;
		}
		return i;
	}
}
