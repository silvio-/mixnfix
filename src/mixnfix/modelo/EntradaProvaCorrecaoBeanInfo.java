package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class EntradaProvaCorrecaoBeanInfo extends SimpleBeanInfo {
	Class beanClass = EntradaProvaCorrecao.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor status = new PropertyDescriptor("_status", beanClass, "getStatus", "setStatus");
			PropertyDescriptor foto = new PropertyDescriptor("_foto", beanClass, "getFoto", "setFoto");
			foto.setValue("font", "Tahoma,bold,11");
			foto.setValue("title", "Foto");
			foto.setValue("alignment", "0");
			foto.setValue("str", "Foto");
			foto.setValue("descricao", "Foto");
			foto.setValue("width", "50");
			PropertyDescriptor threshold = new PropertyDescriptor("_threshold", beanClass, "getThreshold", "setThreshold");
			PropertyDescriptor phase = new PropertyDescriptor("_phase", beanClass, "getPhase", "setPhase");
			PropertyDescriptor idImageData = new PropertyDescriptor("_idImageData", beanClass, "getIdImageData", "setIdImageData");
			PropertyDescriptor tipo = new PropertyDescriptor("_tipo", beanClass, "getTipo", "setTipo");
			PropertyDescriptor idAnswersData = new PropertyDescriptor("_idAnswersData", beanClass, "getIdAnswersData", "setIdAnswersData");
			PropertyDescriptor provaCorrecao_EntradaProvaCorrecao = new PropertyDescriptor("_provaCorrecao_EntradaProvaCorrecao", beanClass, "getProvaCorrecao_EntradaProvaCorrecao", "setProvaCorrecao_EntradaProvaCorrecao");
			PropertyDescriptor aluno_EntradaProvaCorrecao = new PropertyDescriptor("_aluno_EntradaProvaCorrecao", beanClass, "getAluno_EntradaProvaCorrecao", "setAluno_EntradaProvaCorrecao");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, status, foto, threshold, phase, idImageData, tipo, idAnswersData, provaCorrecao_EntradaProvaCorrecao, aluno_EntradaProvaCorrecao };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
