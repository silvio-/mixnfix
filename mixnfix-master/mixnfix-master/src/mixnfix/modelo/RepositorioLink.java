package mixnfix.modelo;

public class RepositorioLink {
	private static RepositorioLink _singleton;
	private Repositorio _repositorio;

	private RepositorioLink(Repositorio r) {
		_repositorio = r;
	}

	public static void init(Repositorio r) {
		_singleton = new RepositorioLink(r);
	}

	public static RepositorioLink getInstance() {
		return _singleton;
	}

	public Repositorio getRepositorio() {
		return _repositorio;
	}
}
