package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AvaliacaoAlunoBeanInfo extends SimpleBeanInfo {
	Class beanClass = AvaliacaoAluno.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor nota = new PropertyDescriptor("_nota", beanClass, "getNota", "setNota");
			nota.setValue("font", "Tahoma,bold,11");
			nota.setValue("title", "Nota");
			nota.setValue("alignment", "0");
			nota.setValue("str", "Nota");
			nota.setValue("descricao", "Nota");
			nota.setValue("width", "50");
			PropertyDescriptor noTotalizado = new PropertyDescriptor("_noTotalizado", beanClass, "getNoTotalizado", "setNoTotalizado");
			noTotalizado.setValue("font", "Tahoma,bold,11");
			noTotalizado.setValue("title", "Nó Totalizado");
			noTotalizado.setValue("alignment", "0");
			noTotalizado.setValue("str", "Nó Total");
			noTotalizado.setValue("descricao", "Nó Totalizado");
			noTotalizado.setValue("width", "50");
			PropertyDescriptor avaliacao_AvaliacaoAluno = new PropertyDescriptor("_avaliacao_AvaliacaoAluno", beanClass, "getAvaliacao_AvaliacaoAluno", "setAvaliacao_AvaliacaoAluno");
			PropertyDescriptor correcao_AvaliacaoAluno = new PropertyDescriptor("_correcao_AvaliacaoAluno", beanClass, "getCorrecao_AvaliacaoAluno", "setCorrecao_AvaliacaoAluno");
			PropertyDescriptor alunoCursoInstancia_AvaliacaoAluno = new PropertyDescriptor("_alunoCursoInstancia_AvaliacaoAluno", beanClass, "getAlunoCursoInstancia_AvaliacaoAluno", "setAlunoCursoInstancia_AvaliacaoAluno");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, nota, noTotalizado, avaliacao_AvaliacaoAluno, correcao_AvaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
