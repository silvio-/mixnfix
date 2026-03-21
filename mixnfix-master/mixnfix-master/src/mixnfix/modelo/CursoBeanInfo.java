package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class CursoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Curso.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Nome");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Nome");
			nome.setValue("width", "100");
			PropertyDescriptor instituicao_Curso = new PropertyDescriptor("_instituicao_Curso", beanClass, "getInstituicao_Curso", "setInstituicao_Curso");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, instituicao_Curso };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
