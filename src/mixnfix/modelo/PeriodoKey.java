package mixnfix.modelo;

public class PeriodoKey implements Comparable {
	private String _id_periodo;

	public PeriodoKey(Periodo obj) {
		_id_periodo = obj.getId_periodo();
	}

	public PeriodoKey(String id_periodo) {
		_id_periodo = id_periodo;
	}

	public boolean equals(Object obj) {
		return compareTo(obj) == 0;
	}

	public int compareTo(Object obj) {
		PeriodoKey x = (PeriodoKey) obj;
		int i = 0;
		if(i == 0) {
			i = _id_periodo.compareTo(x._id_periodo);
		}
		return i;
	}
}
