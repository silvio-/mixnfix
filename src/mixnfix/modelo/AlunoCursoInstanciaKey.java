package mixnfix.modelo;

public class AlunoCursoInstanciaKey implements Comparable {
	private int _id_alunocursoinstancia;

	public AlunoCursoInstanciaKey(AlunoCursoInstancia obj) {
		_id_alunocursoinstancia = obj.getId();
	}

	public AlunoCursoInstanciaKey(int id_alunocursoinstancia) {
		_id_alunocursoinstancia = id_alunocursoinstancia;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AlunoCursoInstanciaKey x = (AlunoCursoInstanciaKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_alunocursoinstancia - x._id_alunocursoinstancia;
		}
		return i;
	}
}
