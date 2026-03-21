package mixnfix.modelo;

public class EntradaColetaQuestionarioKey implements Comparable {
	private int _id_entradacoletaquestionario;

	public EntradaColetaQuestionarioKey(EntradaColetaQuestionario obj) {
		_id_entradacoletaquestionario = obj.getId();
	}

	public EntradaColetaQuestionarioKey(int id_entradacoletaquestionario) {
		_id_entradacoletaquestionario = id_entradacoletaquestionario;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		EntradaColetaQuestionarioKey x = (EntradaColetaQuestionarioKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_entradacoletaquestionario - x._id_entradacoletaquestionario;
		}
		return i;
	}
}
