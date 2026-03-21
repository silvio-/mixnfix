package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class CorrecaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Correcao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor matriz = new PropertyDescriptor("_matriz", beanClass, "getMatriz", "setMatriz");
			PropertyDescriptor imagem = new PropertyDescriptor("_imagem", beanClass, "getImagem", "setImagem");
			PropertyDescriptor nota = new PropertyDescriptor("_nota", beanClass, "getNota", "setNota");
			nota.setValue("font", "Tahoma,bold,11");
			nota.setValue("title", "Nota");
			nota.setValue("alignment", "0");
			nota.setValue("str", "Nota");
			nota.setValue("descricao", "Nota");
			nota.setValue("width", "50");
			PropertyDescriptor prova_Correcao = new PropertyDescriptor("_prova_Correcao", beanClass, "getProva_Correcao", "setProva_Correcao");
			PropertyDescriptor aluno_Correcao = new PropertyDescriptor("_aluno_Correcao", beanClass, "getAluno_Correcao", "setAluno_Correcao");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, matriz, imagem, nota, prova_Correcao, aluno_Correcao };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
