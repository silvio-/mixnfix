package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ProvaCorrecaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = ProvaCorrecao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Nome");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Nome");
			nome.setValue("descricao", "Nome da correção da prova");
			nome.setValue("width", "50");
			PropertyDescriptor prova_ProvaCorrecao = new PropertyDescriptor("_prova_ProvaCorrecao", beanClass, "getProva_ProvaCorrecao", "setProva_ProvaCorrecao");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, prova_ProvaCorrecao };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
