package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import mixnfix.Model;
import mixnfix.modelo.EntradaColetaQuestionario;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.Quesito;

/**
 * EntradaCorrecao
 */
public class ModelEntradaColetaQuestionario extends Model {
    private ModelColetaQuestionario _correcaoProva;
    private EntradaColetaQuestionario _entradaColetaQuestionario;

    public static final byte PROVA_NAO_IDENTIFICADA = (byte) 1;
    public static final byte MATRICULA_NAO_IDENTIFICADA = (byte) 2;
    public static final byte NORMAL = (byte) 3;

    // image information
    private double[] _controlPointsImagePosition;
    /** @todo ligar com a folha resposta personalizada ou automática. por enquanto apenas a automática */

    // prova information
    private HashMap<String,Integer> _gabarito;

    public ModelEntradaColetaQuestionario(ModelColetaQuestionario correcaoProva, EntradaColetaQuestionario entradaColetaQuestionario) {
        _correcaoProva = correcaoProva;
        _entradaColetaQuestionario = entradaColetaQuestionario;
    }

    public ModelColetaQuestionario getCorrecaoProva() { return _correcaoProva; }
    public EntradaColetaQuestionario getEntradaColetaQuestionario() { return _entradaColetaQuestionario; }
    public byte getStatus() { return _entradaColetaQuestionario.getStatus(); }

    public double[] getControlPointsImagePosition() throws IOException, SQLException {
        if (_controlPointsImagePosition == null && _entradaColetaQuestionario.getIdImageData() >= 0)
            _controlPointsImagePosition = EC.getControlPoints(_entradaColetaQuestionario.getIdImageData());
        return _controlPointsImagePosition;
    }

    public HashMap<String,Integer> getGabarito() throws IOException, SQLException {
        if (_gabarito == null && _entradaColetaQuestionario.getIdAnswersData() >= 0)
            _gabarito = EC.getGabarito(_entradaColetaQuestionario.getIdAnswersData());
        return _gabarito;
    }

    public void preencherProvaComGabaritoCorrente() throws IOException, SQLException {
        // obter a prova associada
        mixnfix.prova.ProvaStructure p = this.getProvaStructure();

        // permutá-la (questionário não permuta!)
        mixnfix.prova.ProvaStructure.permuteProva(p,0);

        // gabarito
        HashMap<String,Integer> gabarito = this.getGabarito();
        if (gabarito != null) {
            java.util.List<Quesito> quesitos =  p.getQuesitosPermutados();
            int i=1;
            for (Quesito q: quesitos) {
                q.clearRespostaAluno();
                if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS || q.getTipo() == Quesito.TIPO_SUBJETIVA_5) {
                    Integer value = gabarito.get(""+i);
                    if (value != null && value >= 0)
                        q.addRespostaAluno(value);
                }
                if (q.getTipo() == Quesito.TIPO_NUMERICO_99) {

                    int d = q.getNumDigitosQuesitoNumerico();
                    int value = 0;
                    for (int j=0;j<d;j++) {
                        value = 10 * value + gabarito.get(""+i+"."+j);
                    }
                    q.addRespostaAluno(value);

                    /*
                    Integer units = gabarito.get(""+i+".1");
                    if (tens != null && units != null && tens >= 0 && units >= 0) {
                        int value = tens * 10 + units;
                        q.addRespostaAluno(value);
                    }*/
                }
                else if (q.getTipo() == Quesito.TIPO_FALSO_VERDADEIRO) {
                    java.util.List itens = q.getPermutacao();
                    int j=0;
                    for (Object obj: itens) {
                        ItemQuesito item = (ItemQuesito) obj;
                        item.clearRespostaAluno();
                        Integer value = gabarito.get(i+"."+j);
                        if (value != null && value >= 0)
                            item.addRespostaAluno(value);
                        j++;
                    }
                }
                i++;
            }
        }
    }

    public mixnfix.prova.ProvaStructure getProvaStructure() {
        return this.getCorrecaoProva().getModelProva().getProvaStructure();
    }

}
