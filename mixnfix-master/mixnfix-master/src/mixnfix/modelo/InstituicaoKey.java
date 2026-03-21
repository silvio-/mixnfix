package mixnfix.modelo;

public class InstituicaoKey implements Comparable {
	private int _id_instituicao;

	public InstituicaoKey(Instituicao obj) {
		_id_instituicao = obj.getId();
	}

	public InstituicaoKey(int id_instituicao) {
		_id_instituicao = id_instituicao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		InstituicaoKey x = (InstituicaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_instituicao - x._id_instituicao;
		}
		return i;
	}
}
