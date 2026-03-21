package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AlunoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Aluno.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Nome");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Nome");
			nome.setValue("descricao", "Nome do Aluno");
			nome.setValue("width", "100");
			PropertyDescriptor matricula = new PropertyDescriptor("_matricula", beanClass, "getMatricula", "setMatricula");
			matricula.setValue("font", "Tahoma,bold,11");
			matricula.setValue("title", "Matrícula");
			matricula.setValue("alignment", "0");
			matricula.setValue("str", "Matrícula");
			matricula.setValue("descricao", "Matrícula do Aluno");
			matricula.setValue("width", "100");
			PropertyDescriptor instituicao_Aluno = new PropertyDescriptor("_instituicao_Aluno", beanClass, "getInstituicao_Aluno", "setInstituicao_Aluno");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, matricula, instituicao_Aluno };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
