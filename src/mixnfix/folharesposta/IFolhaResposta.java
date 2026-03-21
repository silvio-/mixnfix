package mixnfix.folharesposta;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public interface IFolhaResposta {

    /**
     * Ceulas que são fixas.
     */
    public int getId();

    /**
     * Ceulas que são fixas.
     */
    public String getName();

    /**
     * Ceulas que são fixas.
     */
    public String getDescription();

    /**
     * Ceulas que são fixas.
     */
    public CellMap getCellMapFixo();

    /**
     * Ceulas que são variáveis.
     */
    public CellMap getCellMapVariavel();

    /**
     * Ceulas que são variáveis.
     */
    public HashMap<String, Integer> getGabaritoCorrente();

    /**
     * Inicializar quesitos
     */
    public void inicializarQuesitos(int tipo);

    /**
     * Write EPS File
     */
    public void writeEPS(PrintWriter pw) throws IOException;


}
