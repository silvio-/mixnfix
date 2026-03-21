package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AlunoProvaCorrecaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = AlunoProvaCorrecao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor numEntradas = new PropertyDescriptor("_numEntradas", beanClass, "getNumEntradas", "setNumEntradas");
			PropertyDescriptor aluno_AlunoProvaCorrecao = new PropertyDescriptor("_aluno_AlunoProvaCorrecao", beanClass, "getAluno_AlunoProvaCorrecao", "setAluno_AlunoProvaCorrecao");
			PropertyDescriptor provaCorrecao_AlunoProvaCorrecao = new PropertyDescriptor("_provaCorrecao_AlunoProvaCorrecao", beanClass, "getProvaCorrecao_AlunoProvaCorrecao", "setProvaCorrecao_AlunoProvaCorrecao");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { numEntradas, aluno_AlunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
