package mixnfix.gui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import mixnfix.modelo.ColetaQuestionario;
import mixnfix.modelo.EntradaColetaQuestionario;

/**
 * Class that is used to save a lot of objects on
 * memory for posterior database batch saving.
 */
public class EQ {

    public static final byte PROCESSAMENTO_LEITURA_IMAGEM_FALHOU = (byte) 0;
    public static final byte PROCESSAMENTO_IMAGEM_FALHOU = (byte) 1;
    public static final byte PROCESSAMENTO_SUCESSO = (byte) 2;

    private int _idEntradaColetaQuestionario;
    private byte _status;
    private File _foto;
    private String _fotoname;
    private int _threshold;
    private int _phase;
    private int _tipoFolhaResposta;
    private int _idControlPoints;
    private Serializable _controlPoints;
    private int _idGabarito;
    private Serializable _gabarito;

    private ModelColetaQuestionario _mcq;

    public EQ(byte status, File foto, int threshold, int phase,
              int tipoFolhaResposta, Serializable controlPoints,
              Serializable gabarito, ModelColetaQuestionario mcq) {
        _status = status;
        _foto = foto;
        _threshold = threshold;
        _phase = phase;
        _tipoFolhaResposta = tipoFolhaResposta;
        _controlPoints = controlPoints;
        _gabarito = gabarito;
        _mcq = mcq;
    }

    public ColetaQuestionario getColetaQuestionario() {
        return _mcq.getColetaQuestionario();
    }

    public byte getStatus() {
        return _status;
    }

    public File getFoto() {
        return _foto;
    }

    public int getThreshold() {
        return _threshold;
    }

    public int getPhase() {
        return _phase;
    }

    public int getTipoFolhaResposta() {
        return _tipoFolhaResposta;
    }

    public Serializable getControlPoints() {
        return _controlPoints;
    }

    public Serializable getGabarito() {
        return _gabarito;
    }

    public void setIdControlPoints(int id) {
        _idControlPoints = id;
    }

    public void setIdGabarito(int id) {
        _idGabarito = id;
    }

    public int getIdControlPoints() {
        return _idControlPoints;
    }

    public int getIdGabarito() {
        return _idGabarito;
    }

    public void setIdEntradaColetaQuestionario(int id) {
        _idEntradaColetaQuestionario = id;
    }

    public String getFotoName() {
        return _fotoname;
    }

    public void setFotoName(String fotoName) {
        _fotoname = fotoName;
    }

    public int getIdEntradaColetaQuestionario() {
        return _idEntradaColetaQuestionario;
    }

    public EntradaColetaQuestionario getEntradaColetaQuestionario() {
        EntradaColetaQuestionario epc =
            new EntradaColetaQuestionario(
               _idEntradaColetaQuestionario,
               _status,
               this.getFotoName(),
               _threshold,
               _phase,
               _idControlPoints,
               _tipoFolhaResposta,
               _idGabarito,
               this._mcq.getColetaQuestionario());
        epc.setPersistent(true);
        return epc;
    }

    static StringBuffer _buffer = new StringBuffer();
    static ArrayList<EQ> _listWithGabarito = new ArrayList<EQ> ();
    public static EntradaColetaQuestionario[] save(List<EQ> list) throws IOException, SQLException {
        App.getRepositorioBD().getConnection();

        { // criar blobs dos pontos de controle

            _buffer.setLength(0);
            _buffer.append("insert into blobdata (blobData) values ");
            boolean first = true;
            for (int i = 0; i < list.size(); i++) {
                if (!first)
                    _buffer.append(",");
                _buffer.append("(?)");
                first = false;
            }
            _buffer.append(";");
            PreparedStatement ps = App.getConnection().prepareStatement(_buffer.toString());
            int i = 1;
            for (EQ eq : list) {
                InputStream is = encode(eq.getControlPoints());
                ps.setBinaryStream(i, is, is.available());
                i++;
            }

            // adding all controlpoints
            ps.executeUpdate();

            // generated keys
            ResultSet rs = ps.getGeneratedKeys();
            i = 0;
            while (rs.next()) {
                EQ eq = list.get(i);
                int id = rs.getInt(1);
                eq.setIdControlPoints(id);
                i++;
            }

        } // criar blobs dos pontos de controle

        { // criar os blobs do gabarito
            _listWithGabarito.clear();

            for (EQ eq : list) {
                if (eq.getStatus() == PROCESSAMENTO_SUCESSO) {
                    _listWithGabarito.add(eq);
                }
            }

            if (_listWithGabarito.size() > 0) {

                boolean first = true;

                _buffer.setLength(0);
                _buffer.append("insert into blobdata (blobData) values ");
                for (int i = 0; i < _listWithGabarito.size(); i++) {
                    if (!first)
                        _buffer.append(",");
                    _buffer.append("(?)");
                    first = false;
                }
                _buffer.append(";");

                PreparedStatement ps = App.getConnection().prepareStatement(_buffer.toString());
                // System.out.println("" + _buffer.toString());
                int i = 1;
                for (EQ eq : _listWithGabarito) {
                    InputStream is = encode(eq.getGabarito());
                    ps.setBinaryStream(i, is, is.available());
                    i++;
                }

                // adding all controlpoints
                ps.executeUpdate();

                // generated keys
                ResultSet rs = ps.getGeneratedKeys();
                i = 0;
                while (rs.next()) {
                    EQ eq = _listWithGabarito.get(i);
                    int id = rs.getInt(1);
                    eq.setIdGabarito(id);
                    i++;
                }
            }
        } // criar os blobs do gabarito}

        long baseTime = System.currentTimeMillis();

        { // adicionar entradaprovacorrecao
            boolean first = true;

            _buffer.setLength(0);
            _buffer.append("insert into entradacoletaquestionario (status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, id_coletaquestionario) values ");
            for (int i = 0; i < list.size(); i++) {
                EQ eq = list.get(i);

                // move foto file to database directory
                String newName = "foto-" + baseTime + "-" + i + ".jpg";
                if (!eq.getFoto().renameTo(new File(App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir) +"/" + newName)))
                    throw new RuntimeException("could not move file!!!");
                eq.setFotoName(newName);
                // move foto file to database directory

                if (!first)
                    _buffer.append(",");
                _buffer.append("(");
                _buffer.append(eq.getStatus());
                _buffer.append(",'");
                _buffer.append(newName); // foto
                _buffer.append("',");
                _buffer.append(eq.getThreshold());
                _buffer.append(",");
                _buffer.append(eq.getPhase());
                _buffer.append(",");
                _buffer.append(eq.getIdControlPoints());
                _buffer.append(",");
                _buffer.append(eq.getTipoFolhaResposta());
                _buffer.append(",");
                _buffer.append(eq.getIdGabarito());
                _buffer.append(",");
                if (eq.getStatus() == PROCESSAMENTO_SUCESSO)
                    _buffer.append(eq.getColetaQuestionario().getId());
                else
                    _buffer.append("NULL");
                _buffer.append(")");
                first = false;
            }
            _buffer.append(";");

            Statement s = App.getConnection().createStatement();

            // adding all controlpoints
            s.executeUpdate(_buffer.toString());

            // generated keys
            ResultSet rs = s.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                EQ eq = list.get(i);
                int id = rs.getInt(1);
                eq.setIdEntradaColetaQuestionario(id);
                i++;
            }
        } // adicionar entradaprovacorrecao

        // devolver resultado
        EntradaColetaQuestionario[] result = new EntradaColetaQuestionario[list.size()];
        for (int i=0;i<list.size();i++)
            result[i] = list.get(i).getEntradaColetaQuestionario();
        return result;
    }

    public static void deleteEntradas(EntradaColetaQuestionario[] entradas) throws IOException,SQLException {

        if (entradas.length == 0)
            return;

        int nroBlobs = 0;
        String idsEntradas = "";
        String idsBlobs = "";
        for (EntradaColetaQuestionario entrada: entradas) {
            if (!"".equals(idsEntradas))
                idsEntradas += ", ";
            idsEntradas += entrada.getId();
            if (entrada.getIdAnswersData() != -1) {
                if (!"".equals(idsBlobs))
                    idsBlobs += ", ";
                idsBlobs += entrada.getIdAnswersData();
                nroBlobs++;
            }
            if (entrada.getIdImageData() != -1) {
                if (!"".equals(idsBlobs))
                    idsBlobs += ", ";
                idsBlobs += entrada.getIdImageData();
                nroBlobs++;
            }
        }

        Connection connection = App.getConnection();
        Statement st = connection.createStatement();
        connection.setAutoCommit(false);
        try {
            if (nroBlobs != 0)
                st.executeUpdate("delete from blobdata where idblob in (" + idsBlobs + ");");
            st.executeUpdate("delete from EntradaColetaQuestionario where id_entradaColetaQuestionario in (" + idsEntradas + ");");

            connection.commit();
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            connection.rollback();
            throw exception;
        }
        finally {
            connection.setAutoCommit(true);
        }

    }

    public static float[] getControlPoints(int idBlob) throws IOException,SQLException {
        Statement st = App.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select blobdata from blobdata where idblob="+idBlob);
        float[] result = null;
        if (rs.next()) {
            try {
                result = (float[])decode(rs.getBinaryStream(1));
            }
            catch (ClassNotFoundException ex) {
            }
        }
        return result;
    }

    public static HashMap<String,Integer> getGabarito(int idBlob) throws IOException,SQLException {
        Statement st = App.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select blobdata from blobdata where idblob="+idBlob);
        HashMap<String,Integer> result = null;
        if (rs.next()) {
            try {
                result = (HashMap<String,Integer>)decode(rs.getBinaryStream(1));
            }
            catch (ClassNotFoundException ex) {
            }
        }
        return result;
    }

    private static InputStream encode(Serializable s) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzos = new GZIPOutputStream(bos);
        ObjectOutputStream oos = new ObjectOutputStream(gzos);
        oos.writeObject(s);
        oos.flush();
        oos.close();
        byte[] data = bos.toByteArray();
        return new ByteArrayInputStream(data);
    }

    private static Object decode(InputStream is) throws IOException, ClassNotFoundException {
        GZIPInputStream gzis = new GZIPInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(gzis);
        Object result = ois.readObject();
        ois.close();
        return result;
    }

}
