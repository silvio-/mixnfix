package mixnfix.folharesposta;

import mixnfix.prova.ProvaStructure;

/**
 * Fabrica de Folha de Resposta.
 */
public class FabricaDeFolhaDeResposta {

    public FabricaDeFolhaDeResposta() {
    }

    public static IFolhaResposta newFolhaResposta(ProvaStructure ps, int id) {
        if (id == GeradorFolhaRespostas.ID) {
            return new GeradorFolhaRespostas(ps);
        }
        else if (id == FolhaRespostaQuestionarioBasico.ID) {
            return new FolhaRespostaQuestionarioBasico(ps);
        }
        throw new RuntimeException("Folha de Resposta not Available!");
    }

}
