package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class TurmaBeanInfo extends SimpleBeanInfo {
	Class beanClass = Turma.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Turma");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Turma");
			nome.setValue("descricao", "Código da Turma");
			nome.setValue("width", "50");
			PropertyDescriptor instituicao_Turma = new PropertyDescriptor("_instituicao_Turma", beanClass, "getInstituicao_Turma", "setInstituicao_Turma");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, instituicao_Turma };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
