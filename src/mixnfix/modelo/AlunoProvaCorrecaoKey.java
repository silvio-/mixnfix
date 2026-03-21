package mixnfix.modelo;

public class AlunoProvaCorrecaoKey implements Comparable {
	private int _id_aluno;
	private int _id_provacorrecao;

	public AlunoProvaCorrecaoKey(AlunoProvaCorrecao obj) {
		_id_aluno = obj.getId_aluno();
		_id_provacorrecao = obj.getId_provacorrecao();
	}

	public AlunoProvaCorrecaoKey(int id_aluno, int id_provacorrecao) {
		_id_aluno = id_aluno;
		_id_provacorrecao = id_provacorrecao;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AlunoProvaCorrecaoKey x = (AlunoProvaCorrecaoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_aluno - x._id_aluno;
		}
		if(i == 0) {
			i = _id_provacorrecao - x._id_provacorrecao;
		}
		return i;
	}
}
