package mixnfix.modelo;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class PeriodoBeanInfo extends SimpleBeanInfo {
	Class beanClass = Periodo.class;

	public PropertyDescriptor[] getPropertyDescriptors() {
		try {
			PropertyDescriptor id_periodo = new PropertyDescriptor("_id_periodo", beanClass, "getId_periodo", "setId_periodo");
			PropertyDescriptor[] pds = new PropertyDescriptor[] { id_periodo };
			return pds;
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
