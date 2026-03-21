package mixnfix.modelo;

public class ProvaKey implements Comparable {
	private int _id_prova;

	public ProvaKey(Prova obj) {
		_id_prova = obj.getId();
	}

	public ProvaKey(int id_prova) {
		_id_prova = id_prova;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		ProvaKey x = (ProvaKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_prova - x._id_prova;
		}
		return i;
	}
}
