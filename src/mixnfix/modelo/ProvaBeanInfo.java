package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ProvaBeanInfo extends SimpleBeanInfo {
	Class beanClass = Prova.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Prova");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Prova");
			nome.setValue("descricao", "Prova");
			nome.setValue("width", "50");
			PropertyDescriptor fonte = new PropertyDescriptor("_fonte", beanClass, "getFonte", "setFonte");
			fonte.setValue("font", "Tahoma,bold,11");
			fonte.setValue("title", "Fonte");
			fonte.setValue("alignment", "0");
			fonte.setValue("str", "Fonte");
			fonte.setValue("descricao", "Arquivo da Prova");
			fonte.setValue("width", "100");
			PropertyDescriptor indice = new PropertyDescriptor("_indice", beanClass, "getIndice", "setIndice");
			indice.setValue("font", "Tahoma,bold,11");
			indice.setValue("title", "Índice");
			indice.setValue("alignment", "0");
			indice.setValue("str", "Índice");
			indice.setValue("descricao", "Índice da Prova");
			indice.setValue("width", "30");
			PropertyDescriptor instituicao_Prova = new PropertyDescriptor("_instituicao_Prova", beanClass, "getInstituicao_Prova", "setInstituicao_Prova");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, fonte, indice, instituicao_Prova };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
