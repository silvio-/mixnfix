package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class CursoInstanciaBeanInfo extends SimpleBeanInfo {
	Class beanClass = CursoInstancia.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor instituicao_CursoInstancia = new PropertyDescriptor("_instituicao_CursoInstancia", beanClass, "getInstituicao_CursoInstancia", "setInstituicao_CursoInstancia");
			PropertyDescriptor curso_CursoInstancia = new PropertyDescriptor("_curso_CursoInstancia", beanClass, "getCurso_CursoInstancia", "setCurso_CursoInstancia");
			PropertyDescriptor periodo_CursoInstancia = new PropertyDescriptor("_periodo_CursoInstancia", beanClass, "getPeriodo_CursoInstancia", "setPeriodo_CursoInstancia");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
