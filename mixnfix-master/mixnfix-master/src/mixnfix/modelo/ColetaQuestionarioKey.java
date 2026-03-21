package mixnfix.modelo;

public class ColetaQuestionarioKey implements Comparable {
	private int _id_coletaquestionario;

	public ColetaQuestionarioKey(ColetaQuestionario obj) {
		_id_coletaquestionario = obj.getId();
	}

	public ColetaQuestionarioKey(int id_coletaquestionario) {
		_id_coletaquestionario = id_coletaquestionario;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		ColetaQuestionarioKey x = (ColetaQuestionarioKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_coletaquestionario - x._id_coletaquestionario;
		}
		return i;
	}
}
