package mixnfix.gui;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

import mixnfix.Model;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.EntradaProvaCorrecao;
import mixnfix.prova.ItemQuesito;
import mixnfix.prova.Quesito;

/**
 * EntradaCorrecao
 */
public class ModelEntradaProvaCorrecao extends Model {
    private ModelProvaCorrecao _correcaoProva;
    private EntradaProvaCorrecao _entradaProvaCorrecao;

    public static final byte PROVA_NAO_IDENTIFICADA = (byte) 1;
    public static final byte MATRICULA_NAO_IDENTIFICADA = (byte) 2;
    public static final byte NORMAL = (byte) 3;

    // image information
    private double[] _controlPointsImagePosition;
    /** @todo ligar com a folha resposta personalizada ou automática. por enquanto apenas a automática */

    // prova information
    private HashMap<String,Integer> _gabarito;

    public ModelEntradaProvaCorrecao(ModelProvaCorrecao correcaoProva, EntradaProvaCorrecao entradaProvaCorrecao) {
        _correcaoProva = correcaoProva;
        _entradaProvaCorrecao = entradaProvaCorrecao;
    }

    public ModelProvaCorrecao getCorrecaoProva() { return _correcaoProva; }
    public EntradaProvaCorrecao getEntradaProvaCorrecao() { return _entradaProvaCorrecao; }
    public Aluno getAluno() { return _entradaProvaCorrecao.getAluno_EntradaProvaCorrecao(); }
    public byte getStatus() { return _entradaProvaCorrecao.getStatus(); }

    public double[] getControlPointsImagePosition() throws IOException, SQLException {
        if (_controlPointsImagePosition == null && _entradaProvaCorrecao.getIdImageData() >= 0)
            _controlPointsImagePosition = EC.getControlPoints(_entradaProvaCorrecao.getIdImageData());
        return _controlPointsImagePosition;
    }

    public HashMap<String,Integer> getGabarito() throws IOException, SQLException {
        if (_gabarito == null && _entradaProvaCorrecao.getIdAnswersData() >= 0)
            _gabarito = EC.getGabarito(_entradaProvaCorrecao.getIdAnswersData());
        return _gabarito;
    }

    public void preencherProvaComGabaritoCorrente() throws IOException, SQLException {
        // obter a prova associada
        mixnfix.prova.ProvaStructure p = this.getProvaStructure();

        // permutá-la
        mixnfix.prova.ProvaStructure.permuteProva(p,this.getEntradaProvaCorrecao().getTipo());

        // gabarito
        HashMap<String,Integer> gabarito = this.getGabarito();
        if (gabarito != null) {
            java.util.List<Quesito> quesitos =  p.getQuesitosPermutados();
            int i=1;
            for (Quesito q: quesitos) {
                q.clearRespostaAluno();
                if (q.getTipo() == Quesito.TIPO_ALTERNATIVAS || q.getTipo() == Quesito.TIPO_SUBJETIVA_5 || q.getTipo() == Quesito.TIPO_SUBJETIVA_9) {
                    Integer value = gabarito.get(""+i);
                    if (value != null && value >= 0)
                        q.addRespostaAluno(value);
                }
                if (q.getTipo() == Quesito.TIPO_NUMERICO_99) {
                    Integer tens = gabarito.get(""+i+".0");
                    Integer units = gabarito.get(""+i+".1");
                    if (tens != null && units != null && tens >= 0 && units >= 0) {
                        int value = tens * 10 + units;
                        q.addRespostaAluno(value);
                    }
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
