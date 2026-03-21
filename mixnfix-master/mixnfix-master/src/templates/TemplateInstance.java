
package templates;

/**
 * Classe para representar o uso de um template já definido dentro de
 * outro template.
 */
public class TemplateInstance {
	/**
	 * Template instanciado.
	 */
	private Template _template;

	/**
	 * Nome da instância.
	 */
	private String _instanceName;

	/**
	 * Construtor.
	 * @param t template a ser instanciado
	 * @param in nome da instância
	 */
	public TemplateInstance(Template t, String in) {
		_template = t;
		_instanceName = in;
	}

	/**
	 * Método para obter o template instanciado.
	 * @ return o template instanciado
	 */
	public Template getTemplate() {
		return _template;
	}

	/**
	 * Método para obter o nome da instância.
	 * @return o nome
	 */
	public String getInstanceName() {
		String in = _instanceName;
		if(in == null) {
			in = _template.getName();
		}
		return in;
	}
}