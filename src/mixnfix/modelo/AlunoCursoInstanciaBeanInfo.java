package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class AlunoCursoInstanciaBeanInfo extends SimpleBeanInfo {
	Class beanClass = AlunoCursoInstancia.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id = new PropertyDescriptor("_id", beanClass, "getId", null);
			PropertyDescriptor cursoInstancia_AlunoCursoInstancia = new PropertyDescriptor("_cursoInstancia_AlunoCursoInstancia", beanClass, "getCursoInstancia_AlunoCursoInstancia", "setCursoInstancia_AlunoCursoInstancia");
			PropertyDescriptor aluno_AlunoCursoInstancia = new PropertyDescriptor("_aluno_AlunoCursoInstancia", beanClass, "getAluno_AlunoCursoInstancia", "setAluno_AlunoCursoInstancia");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id, cursoInstancia_AlunoCursoInstancia, aluno_AlunoCursoInstancia };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
