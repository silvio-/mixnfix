
package templates;

public class RepeatedTemplate extends Template {
	private String _string;

	/**
	 * Método para acessar o resultado do template. A diferença em relação
	 * ao método da superclasse é que aqui não se apaga o buffer.
	 * @return string com as substituições dos ips e os valores dos
	 * subtemplates
	 */
	public String getString() {
		if(_string == null) {
			_string = super.getString();
		}
		return _string;
	}

	/**
	 * Método para limpar o template.
	 */
	public void clear() {
		super.clear();
		_string = null;
	}

	/**
	 * Construtor.
	 * @param t template que será copiado como RepeatedTemplate.
	 */
	public RepeatedTemplate(Template tem) {
		setParent(tem.getParent());
		setName(tem.getName());
		for(int i = 0; i < tem.getNumObjects(); i++) {
			Object obj = tem.getObject(i);
			addObject(obj);
		}
	}

	/**
	 * Método para copiar um objeto.
	 * @return uma cópia
	 */
	public Object clone() {
		return new RepeatedTemplate(this);
	}
}