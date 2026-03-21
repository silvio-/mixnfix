package mixnfix.modelo;

public class PersistentObject {
	// valor para os campos auto nos objetos que não estão no banco
	public static final int TRANSITORY_ID = -1;

	private boolean _persistent;

	public PersistentObject() {
		_persistent = false;
	}

	public void setPersistent(boolean p) {
		_persistent = p;
	}

	public boolean isPersistent() {
		return _persistent;
	}
}
