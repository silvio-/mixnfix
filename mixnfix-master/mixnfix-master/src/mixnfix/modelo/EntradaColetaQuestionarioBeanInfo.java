package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class EntradaColetaQuestionarioBeanInfo extends SimpleBeanInfo {
	Class beanClass = EntradaColetaQuestionario.class;

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
			PropertyDescriptor tipoFolhaResposta = new PropertyDescriptor("_tipoFolhaResposta", beanClass, "getTipoFolhaResposta", "setTipoFolhaResposta");
			PropertyDescriptor idAnswersData = new PropertyDescriptor("_idAnswersData", beanClass, "getIdAnswersData", "setIdAnswersData");
			PropertyDescriptor coletaQuestionario_EntradaColetaQuestionario = new PropertyDescriptor("_coletaQuestionario_EntradaColetaQuestionario", beanClass, "getColetaQuestionario_EntradaColetaQuestionario", "setColetaQuestionario_EntradaColetaQuestionario");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, coletaQuestionario_EntradaColetaQuestionario };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
