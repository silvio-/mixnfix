package mixnfix.gui;

import mixnfix.Model;
import mixnfix.modelo.CursoInstancia;

/**
 * ModelCursoInstancia
 */
public class ModelCursoInstancia extends Model {

    private CursoInstancia _cursoInstancia;

    public ModelCursoInstancia(ModelInstituicao modelInstituicao, CursoInstancia cursoInstancia) {
        this.setParent(modelInstituicao);
        _cursoInstancia = cursoInstancia;
    }

    public CursoInstancia getCursoInstancia() {
        return _cursoInstancia;
    }

}
