package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AlunoTurmaBeanInfo extends SimpleBeanInfo {
	Class beanClass = AlunoTurma.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor aluno_AlunoTurma = new PropertyDescriptor("_aluno_AlunoTurma", beanClass, "getAluno_AlunoTurma", "setAluno_AlunoTurma");
			PropertyDescriptor turma_AlunoTurma = new PropertyDescriptor("_turma_AlunoTurma", beanClass, "getTurma_AlunoTurma", "setTurma_AlunoTurma");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { aluno_AlunoTurma, turma_AlunoTurma };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
