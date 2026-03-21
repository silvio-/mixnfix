package mixnfix;

import java.util.Vector;

class Representantes {
    double _raio;
    int _maximumIntensity;
    private Vector _pontos;
    public Representantes(double raio) {
        _raio = raio;
        _maximumIntensity = 200;
        _pontos = new Vector();
    }
    public boolean addPontoSeForBom(int x, int y, int intensity) {
        if (intensity > _maximumIntensity)
            return false;

        // check if hits a region
        // swaps with last elements
        // to keep the hitting points
        // at the end.
        int smallestHitIntensity = Integer.MAX_VALUE;
        int n = _pontos.size();
        int hits = 0;
        for (int i=0;i<n-hits;i++) {
            Ponto pi = (Ponto)_pontos.get(i);
            if (pi.getDistance(x,y) < _raio) {
                Object aux = _pontos.get(n-1-hits);
                _pontos.set(i,aux);
                _pontos.set(n-1-hits,pi);
                if (pi.getIntensity() < smallestHitIntensity)
                    smallestHitIntensity = pi.getIntensity();
                hits++;
            }
        }

        //
        if (hits > 0 && smallestHitIntensity > intensity) {
            // remove hits with larger intensity
            for (int i=n-1;i>=n-hits;i--) {
                Ponto pi = (Ponto) _pontos.get(i);
                if (intensity < pi.getIntensity()) {
                    _pontos.remove(i);
                }
            }
            _pontos.add(new Ponto(x,y,intensity));
            return true;
        }
        else if (hits == 0) {
            _pontos.add(new Ponto(x,y,intensity));
            return true;
        }
        return false;
    }
    public double getRaio() {
        return _raio;
    }
    public int getSize() {
        return _pontos.size();
    }
    public Ponto getPonto(int index) {
        return (Ponto) _pontos.get(index);
    }
    public Ponto findClosestPoint(int x, int y) {
        double minDist = Double.MAX_VALUE;
        Ponto result = null;
        for (int i=0;i<_pontos.size();i++) {
            Ponto p = this.getPonto(i);
            double dist = p.getDistance(x,y);
            if (dist < minDist) {
                result = p;
                minDist = dist;
            }
        }
        return result;
    }
}
