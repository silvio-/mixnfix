package mixnfix.modelo;

public class AlunoTurmaKey implements Comparable {
	private int _id_aluno;
	private int _id_turma;

	public AlunoTurmaKey(AlunoTurma obj) {
		_id_aluno = obj.getId_aluno();
		_id_turma = obj.getId_turma();
	}

	public AlunoTurmaKey(int id_aluno, int id_turma) {
		_id_aluno = id_aluno;
		_id_turma = id_turma;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		AlunoTurmaKey x = (AlunoTurmaKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_aluno - x._id_aluno;
		}
		if(i == 0) {
			i = _id_turma - x._id_turma;
		}
		return i;
	}
}
