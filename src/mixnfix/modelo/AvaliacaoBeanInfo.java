package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AvaliacaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Avaliacao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nome = new PropertyDescriptor("_nome", beanClass, "getNome", "setNome");
			nome.setValue("font", "Tahoma,bold,11");
			nome.setValue("title", "Avaliação");
			nome.setValue("alignment", "0");
			nome.setValue("str", "Avaliação");
			nome.setValue("descricao", "Avaliação");
			nome.setValue("width", "50");
			PropertyDescriptor tipo = new PropertyDescriptor("_tipo", beanClass, "getTipo", "setTipo");
			tipo.setValue("tamanho", "20");
			tipo.setValue("title", "Tipo");
			tipo.setValue("alignment", "1");
			tipo.setValue("str", "Tipo");
			tipo.setValue("descricao", "Tipo da Avaliacao");
			tipo.setValue("width", "28");
			PropertyDescriptor cursoInstancia_Avaliacao = new PropertyDescriptor("_cursoInstancia_Avaliacao", beanClass, "getCursoInstancia_Avaliacao", "setCursoInstancia_Avaliacao");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nome, tipo, cursoInstancia_Avaliacao };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
