package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class ColetaQuestionarioBeanInfo extends SimpleBeanInfo {
	Class beanClass = ColetaQuestionario.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Nome");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Nome");
			nome.setValue("descricao", "Nome da coleta questionário");
			nome.setValue("width", "50");
			PropertyDescriptor prova_ColetaQuestionario = new PropertyDescriptor("_prova_ColetaQuestionario", beanClass, "getProva_ColetaQuestionario", "setProva_ColetaQuestionario");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, prova_ColetaQuestionario };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
