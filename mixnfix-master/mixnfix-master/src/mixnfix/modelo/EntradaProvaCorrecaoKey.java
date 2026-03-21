package mixnfix.modelo;

public class EntradaProvaCorrecaoKey implements Comparable {
	private int _id_entradaprovacorrecao;

	public EntradaProvaCorrecaoKey(EntradaProvaCorrecao obj) {
		_id_entradaprovacorrecao = obj.getId();
	}

	public EntradaProvaCorrecaoKey(int id_entradaprovacorrecao) {
		_id_entradaprovacorrecao = id_entradaprovacorrecao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		EntradaProvaCorrecaoKey x = (EntradaProvaCorrecaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_entradaprovacorrecao - x._id_entradaprovacorrecao;
		}
		return i;
	}
}
