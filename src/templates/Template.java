
package templates;

import java.util.Vector;

/**
 * Template é a string com espaços vazios que serão preenchidos pelo
 * controller.
 */
public class Template {
	/**
	 * Constantes, pontos de inserção e templates.
	 */
	private Vector _objects;

	/**
	 * Contador usado para colocar um subtemplate no array
	 * _objects.
	 */
	private int _counter;

	/**
	 * Nome do template.
	 */
	private String _name;

	/**
	 * Usado se o template for iterativo.
	 */
	protected StringBuffer _buffer;

	/**
	 * Usado se o template for opcional ou alternativo.
	 */
	protected boolean _selected;

	/**
	 * Template que contem esta instância.
	 */
	private Template _parent;

	/**
	 * Conjunto de objetos TempalteInstance que ocorrem mais de uma
	 * vez na lista de objetos do template.
	 */
	private Vector _repetitions;

	/**
	 * Construtor.
	 */
	public Template() {
		_objects = new Vector();
		_counter = 0;
		_selected = true;
		_repetitions = new Vector();
	}

	/**
	 * Método para nomear o template.
	 * @param n nome do template
	 */
	public void setName(String n) {
		_name = n;
	}

	/**
	 * Método para obter o nome do template.
	 * @return o nome do template
	 */
	public String getName() {
		return _name;
	}

	/**
	 * Método para jogar um objeto (string, insertion point, template)
	 * no template. O template é uma lista de objetos, e cada objeto
	 * adicionado vai para o final da lista.
	 * @param n novo objeto
	 */
	public void addObject(Object obj) {
		if(obj instanceof TemplateInstance && _objects.contains(obj) && !_repetitions.contains(obj)) {
			_repetitions.add(obj);
		}

		_objects.add(obj);
		_counter++;
	}

	/**
	 * Método para retornar o número de objetos no template.
	 * @return o número de objetos
	 */
	public int getNumObjects() {
		return _objects.size();
	}

	/**
	 * Método para pegar o objeto na posição dada.
	 * @param i a posição do objeto
	 * @return o objeto
	 */
	public Object getObject(int i) {
		return _objects.get(i);
	}

	/**
	 * Método para pegar o template na posição dada. Para ser usado no
	 * método write() dos controllers, substituindo o argumento pelo
	 * nome do template em maiúsculas (que corresponde a uma constante
	 * cujo valor é a posição do template).
	 * @param t a posição do template (nome do template em maiúsculas)
	 * @return o template
	 */
	public Template getTemplate(int t) {
		return (Template) _objects.get(t);
	}

	/**
	 * Método para ativar e desativar o template. Deve ser usado para
	 * templates opcionais.
	 */
	public void select(boolean s) {
		_selected = s;
	}

	public void addIP() {
		_objects.add(null);
		_counter++;
	}

	/**
	 * Método para substituir um valor em um ponto de inserção. O
	 * inteiro deve ser uma constante do controller (nome do ip
	 * em maiúsculas).
	 * @param ip posição do ponto de inserção (nome em maiúsculas)
	 * @param str string a ser escrita
	 */
	public void set(int ip, String str) {
		_objects.set(ip, str);
	}

	/**
	 * Método para substituir um valor para um nome que corresponde a um
	 * ponto de inserção que ocorre mais de uma vez. O array deve ser uma
	 * constante do controller.
	 * @param ips constante com as posições (definida no controller)
	 * @param str string a ser escrita
	 */
	public void set(int[] ips, String str) {
		for(int i = 0; i < ips.length; i++) {
			_objects.set(ips[i], str);
		}
	}

	/**
	 * Método chamado por getString(), que produz o resultado do
	 * template quando ele não é iterativo.
	 * @return string com as substituições feitas
	 */
	protected String write() {
		StringBuffer buffer = new StringBuffer();

		for(int i = 0; i < _objects.size(); i++) {
			Object obj = _objects.get(i);

			if(obj instanceof String) {
				String str = (String) obj;
				buffer.append(str);
			}

			else if(obj instanceof Template) {
				Template t = (Template) obj;
				String str = t.getString();
				if(str != null) {
					buffer.append(str);
				}
			}
		}

		String r = "";
		if(_selected) {
			r = buffer.toString(); 
		}

		return r;
	}

	/** 
	 * Método para guardar o resultado de uma iteração de um
	 * template iterativo.
	 */
	public void store() {
		if(_buffer == null) {
			_buffer = new StringBuffer();
		}
		_buffer.append(write());
	}

	/**
	 * Método para acessar o resultado do template.
	 * @return string com as substituições dos ips e os valores dos
	 * subtemplates
	 */
	public String getString() {
		String str = null;
		if(_selected) {
			if(_buffer == null) {
				str = write();
			}
			else {
				str = _buffer.toString();
			}
		}
		clear();
		return str;
	}

	/**
	 * Destrói o conteúdo do buffer.
	 */
	public void clear() {
		if(_buffer != null) {
			_buffer.delete(0, _buffer.length());
		}
	}

	/**
	 * Método para atribuir um template pai.
	 * @param t template pai
	 */
	public void setParent(Template t) {
		_parent = t;
	}

	/**
	 * Método para obter o template pai.
	 * @return o template pai
	 */
	public Template getParent() {
		return _parent;
	}

	public String toString() {
		String str = _name + " num objs: " + _objects.size();// + " counter " + _counter;
		return str;
	}

	/**
	 * Retorna uma cópia do objeto.
	 * @return cópia do objeto
	 */
	public Object clone() {
		Template tpl = new Template();
		tpl.setParent(_parent);
		tpl.setName(_name);
		for(int i = 0; i < _objects.size(); i++) {
			Object obj = _objects.get(i);
			if(obj instanceof Template) {
				Template t = (Template) obj;
				obj = t.clone();
			}
			tpl.addObject(obj);
		}
		return tpl;
	}

	/**
	 * Método para procurar por um subtemplate dado seu nome.
	 * @param n nome do subtemplate
	 * @return o subtemplate
	 */
	public Template getTemplate(String n) {
		boolean achou = false;
		Template t = null;
		for(int i = 0; !achou && i < _objects.size(); i++) {
			Object obj = _objects.get(i);
			if(obj instanceof Template) {
				t = (Template) obj;
				if(t.getName().equals(n)) {
					achou = true;
				}
			}
		}
		if(!achou) {
			t = null;
		}
		return t;
	}

	/**
	 * Método para determinar se um template instance se repete
	 * na lista de objetos do template.
	 * @return true se ti ocorre mais de uma vez na lista de objetos;
	 * false caso contrário
	 */
	public boolean repeats(TemplateInstance ti) {
		int c = 0;
		for(int i = 0; c < 2 && i < _objects.size(); i++) {
			Object obj = _objects.get(i);
			if(obj instanceof TemplateInstance) {
				TemplateInstance ti2 = (TemplateInstance) obj;
				String n = ti.getTemplate().getName();
				String in = ti.getInstanceName();
				String n2 = ti2.getTemplate().getName();
				String in2 = ti2.getInstanceName();
				if(n.equals(n2) && in.equals(in2)) {
					c++;
				}
			}
		}
		return c >= 2;
	}

	/**
	 * Método para determinar se um template instance está ocorrendo
	 * pela primeira vez.
	 * @return true se não ocorre uma instância igual a ti antes dele
	 * false caso contrário
	 */
	public boolean firstRepetition(TemplateInstance ti) {
		int c = 0;
		boolean achou = false;

		for(int i = 0; !achou && i < _objects.size(); i++) {
			Object obj = _objects.get(i);

			if(obj instanceof TemplateInstance) {
				TemplateInstance ti2 = (TemplateInstance) obj;
				String n = ti.getTemplate().getName();
				String in = ti.getInstanceName();
				String n2 = ti2.getTemplate().getName();
				String in2 = ti2.getInstanceName();
				if(n.equals(n2) && in.equals(in2)) {
					c++;
				}

				if(ti2 == ti) {
					achou = true;
				}
			}
		}
		return c == 1;
	}
}
