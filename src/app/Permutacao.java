package app;

import java.math.BigInteger;

public class Permutacao implements Comparable {
    public static final int MAX_NUM_ELEMENTS = 2000; // 1999 é primo!
    public static BigInteger[] _fatoriais;
    public static int[] _proximoPrimo;
    public static BigInteger[] _produtosDosPrimoMenoresOuIgual;
    static {
        init();
    }

    private static void init() {
        _fatoriais = new BigInteger[MAX_NUM_ELEMENTS + 1];
        _fatoriais[0] = BigInteger.ONE;
        _fatoriais[1] = BigInteger.ONE;
        for(int i = 1; i < MAX_NUM_ELEMENTS; i++) {
            _fatoriais[i] = _fatoriais[i - 1].multiply(BigInteger.valueOf(i));
        }

        // calcular o proximo primo
        _proximoPrimo = new int[MAX_NUM_ELEMENTS + 1];
        _proximoPrimo[0] = 2; _proximoPrimo[1] = 2;
        int ultimoPrimo = 2;
        int i = 3;
        while (true) {
            boolean isPrime = (BigInteger.valueOf(i)).isProbablePrime(1000);
            if (isPrime) {
                for (int j=ultimoPrimo;j<i;j++) {
                    // System.out.println(String.format("%d -> %d",j,i));
                    _proximoPrimo[j] = i;
                    if (j==MAX_NUM_ELEMENTS)
                        break;
                }
                if (i > MAX_NUM_ELEMENTS)
                    break;
                ultimoPrimo = i;
            }
            i++;
        }

        // produto dos primos
        _produtosDosPrimoMenoresOuIgual = new BigInteger[MAX_NUM_ELEMENTS + 1];
        _produtosDosPrimoMenoresOuIgual[0] = BigInteger.ONE;
        _produtosDosPrimoMenoresOuIgual[1] = BigInteger.ONE;
        i=2;
        BigInteger produtoCorrente = BigInteger.ONE;
        while(i<=MAX_NUM_ELEMENTS) {
            boolean isPrime = (BigInteger.valueOf(i)).isProbablePrime(1000);
            if (isPrime) {
                produtoCorrente = produtoCorrente.multiply(BigInteger.valueOf(i));
            }
            _produtosDosPrimoMenoresOuIgual[i] = produtoCorrente;
            i++;
        }

        // for (int kk = 0; kk < _produtosDosPrimoMenoresOuIgual.length; kk++)
        //     System.out.println(""+kk+" "+_produtosDosPrimoMenoresOuIgual[kk].toString());

    }

    public static BigInteger getSerial(BigInteger seed, int i, int n) {
        if (i == 0)
            return BigInteger.ZERO;

        BigInteger N = _fatoriais[n];
        // System.out.println("N: "+N.toString());
        BigInteger r = _produtosDosPrimoMenoresOuIgual[n];
        // System.out.println("r: "+r.toString());
        BigInteger t = BigInteger.valueOf(_proximoPrimo[n]);
        // System.out.println("t: "+t.toString());

        // System.out.println(""+i);
        BigInteger result = seed.add(BigInteger.ZERO);
        for (int k=1;k<=i;k++) {
            result =result.multiply(r.multiply(BigInteger.valueOf(2)).add(BigInteger.ONE)).add(t);
            result = result.mod(N);
            // System.out.println(""+k+" -> "+result.toString());
        }
        return result;
    }

    private int[] _v;
	private BigInteger _serial;

	public Permutacao(int d, BigInteger serial) {
		_serial = serial;

        if (d > MAX_NUM_ELEMENTS)
            throw new RuntimeException("Too much elements");

        // caso base em que a rotina a seguir nao funciona
        if (d == 1) {
            _v = new int[] {1};
            return;
        }

		int[] u = new int[d];
		BigInteger s = serial;
		for(int i = 0; i < d; i++) {
			BigInteger[] dm = s.divideAndRemainder(_fatoriais[d - (i + 1)]);
			u[i] = dm[0].intValue();
			s = dm[1];
			//System.out.println(s + " " + fatoriais[i] + " " + dm[0] + " " + dm[1] + " " + u[i]);
		}

		int[] id = new int[d];
		for(int i = 0; i < d; i++) {
			id[i] = i + 1;
		}

		//System.out.println();
		_v = new int[d];
		for(int i = 0; i < d; i++) {
			int x = 0;

			int j = 0;
			int k = 0;
			while(k < u[i] + 1) {
				if(id[j] > 0) {
					x = id[j];
					k++;
				}
				j++;
			}

			_v[i] = x;
			id[j - 1] = -1;

			//System.out.print(_v[i] + " ");
		}
		//System.out.println();
	}

	public int[] getV() {
		return _v;
	}

	public static void main(String[] args) throws Exception {
        BigInteger x = new BigInteger("8717");
        long t0 = System.currentTimeMillis();
		//for(int i = 0; i <= 10; i++) {
			Permutacao p = new Permutacao(10, x);
			System.out.println(p);
		//}
        long t1 = System.currentTimeMillis();
        System.out.println("Tempo: "+(t1-t0)+"mseg.");

	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		String v = "";
		for(int j = 0; j < _v.length; j++) {
			b.append(v + _v[j]);
			v = ", ";
		}
		b.append(": " + _serial);
		return b.toString();
	}

	public boolean equals(Object obj) {
		boolean eq = false;
		if(obj instanceof Permutacao) {
			eq = (compareTo(obj) == 0);
		}
		return eq;
	}

	public int compareTo(Object obj) {
		int c = 0;
		if(obj instanceof Permutacao) {
			Permutacao p = (Permutacao) obj;
			int[] v = p.getV();

			if(v.length == _v.length) {
				for(int i = 0; c == 0 && i < v.length; i++) {
					c = (_v[i] - v[i]);
				}
			}
		}
		else {
			throw new RuntimeException();
		}
		return c;
	}
}
