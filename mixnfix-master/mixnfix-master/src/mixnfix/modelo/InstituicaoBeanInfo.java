package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class InstituicaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Instituicao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Nome");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Nome");
			nome.setValue("descricao", "Nome da Instituição");
			nome.setValue("width", "100");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
