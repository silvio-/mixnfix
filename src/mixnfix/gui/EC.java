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

import mixnfix.modelo.EntradaProvaCorrecao;

/**
 * Class that is used to save a lot of objects on
 * memory for posterior database batch saving.
 */
public class EC {

    public static final byte PROCESSAMENTO_IMAGEM_FALHOU = (byte) 0;
    public static final byte PROCESSAMENTO_TIPO_PROVA_FALHOU = (byte) 1;
    public static final byte PROCESSAMENTO_MATRICULA_FALHOU = (byte) 2;
    public static final byte PROCESSAMENTO_SUCESSO = (byte) 3;
    public static final byte PROCESSAMENTO_DUPLICATA = (byte) 4;

    private int _idEntradaProvaCorrecao;
    private byte _status;
    private File _foto;
    private String _fotoname;
    private int _threshold;
    private int _phase;
    private int _tipo;
    private ModelAlunoProvaCorrecao _mapc;
    private int _idControlPoints;
    private Serializable _controlPoints;
    private int _idGabarito;
    private Serializable _gabarito;
    public EC(byte status, File foto, int threshold, int phase,
              int tipo, Serializable controlPoints, Serializable gabarito,
              ModelAlunoProvaCorrecao mapc) {
        _status = status;
        _foto = foto;
        _threshold = threshold;
        _phase = phase;
        _tipo = tipo;
        _mapc = mapc;
        _controlPoints = controlPoints;
        _gabarito = gabarito;
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

    public int getTipo() {
        return _tipo;
    }

    public ModelAlunoProvaCorrecao getModelAlunoProvaCorrecao() {
        return _mapc;
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

    public void setIdEntradaProvaCorrecao(int id) {
        _idEntradaProvaCorrecao = id;
    }

    public String getFotoName() {
        return _fotoname;
    }

    public void setFotoName(String fotoName) {
        _fotoname = fotoName;
    }

    public int getIdEntradaProvaCorrecao() {
        return _idEntradaProvaCorrecao;
    }

    public EntradaProvaCorrecao getEntradaProvaCorrecao() {
        EntradaProvaCorrecao epc = new EntradaProvaCorrecao(_idEntradaProvaCorrecao,
            _status,this.getFotoName(),_threshold,_phase,_idControlPoints,_tipo,
            _idGabarito,this._mapc.getProvaCorrecao(),this._mapc.getAluno());
        epc.setPersistent(true);
        return epc;
    }

    static StringBuffer _buffer = new StringBuffer();
    static ArrayList<EC> _listWithGabarito = new ArrayList<EC> ();
    public static EntradaProvaCorrecao[] save(List<EC> list) throws IOException, SQLException {
        App.getRepositorioBD().getConnection();

        
        for (EC ec: list)
        { // criar blobs dos pontos de controle

            _buffer.setLength(0);
            _buffer.append("insert into blobdata (blobData) values (?)");
            PreparedStatement ps = App.getConnection().prepareStatement(_buffer.toString(),Statement.RETURN_GENERATED_KEYS);
            InputStream is = encode(ec.getControlPoints());
            ps.setBinaryStream(1, is, is.available());

            // adding all controlpoints
            ps.executeUpdate();

            // generated keys
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                ec.setIdControlPoints(id);
            	System.out.println("blobdata controlpoints ([D) "+id);
            }
            rs.close();

            _listWithGabarito.clear();

            if (ec.getStatus() == PROCESSAMENTO_SUCESSO) {

            	_listWithGabarito.add(ec);

                boolean first = true;

                _buffer.setLength(0);
                _buffer.append("insert into blobdata (blobData) values (?)");
                ps = App.getConnection().prepareStatement(_buffer.toString(),Statement.RETURN_GENERATED_KEYS);
                // System.out.println("" + _buffer.toString());
                is = encode(ec.getGabarito());
                ps.setBinaryStream(1, is, is.available());

                // adding all controlpoints
                ps.executeUpdate();

                // generated keys
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    ec.setIdGabarito(id);
                	System.out.println("blobdata gabarito (HashMap) "+id);
                }
                rs.close();
            }
        } // criar os blobs do gabarito}

        long baseTime = System.currentTimeMillis();

        { // adicionar entradaprovacorrecao
            boolean first = true;

            _buffer.setLength(0);
            _buffer.append("insert into entradaprovacorrecao (status, foto, threshold, phase, idImageData, tipo, idAnswersData, id_provacorrecao, id_aluno) values ");
            for (int i = 0; i < list.size(); i++) {
                EC ec = list.get(i);

                // move foto file to database directory
                String newName = "foto-" + baseTime + "-" + i + ".jpg"; 
                String newFilePath = App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.datadir) +"/"+ newName; 
                System.out.println(ec.getFoto().getAbsolutePath()+" "+newFilePath);
                if (!ec.getFoto().renameTo(new File(newFilePath)))
                    throw new RuntimeException("could not move file!!!");
                ec.setFotoName(newName);
                // move foto file to database directory

                if (!first)
                    _buffer.append(",");
                _buffer.append("(");
                _buffer.append(ec.getStatus());
                _buffer.append(",'");
                _buffer.append(newName); // foto
                _buffer.append("',");
                _buffer.append(ec.getThreshold());
                _buffer.append(",");
                _buffer.append(ec.getPhase());
                _buffer.append(",");
                _buffer.append(ec.getIdControlPoints());
                _buffer.append(",");
                _buffer.append(ec.getTipo());
                _buffer.append(",");
                _buffer.append(ec.getIdGabarito());
                _buffer.append(",");
                if (ec.getStatus() == PROCESSAMENTO_SUCESSO)
                    _buffer.append(ec.getModelAlunoProvaCorrecao().getProvaCorrecao().getId());
                else
                    _buffer.append("NULL");
                _buffer.append(",");
                if (ec.getStatus() == PROCESSAMENTO_SUCESSO)
                    _buffer.append(ec.getModelAlunoProvaCorrecao().getAluno().getId());
                else
                    _buffer.append("NULL");
                _buffer.append(")");
                first = false;
            }

            Statement s = App.getConnection().createStatement();

            // adding all controlpoints
            s.executeUpdate(_buffer.toString(),Statement.RETURN_GENERATED_KEYS);

            // A multi row insert only reports ONE generated key (the last one),
            // so the identifiers are read back from the database through the
            // name of the picture, which is unique. They used to be left at 0,
            // and the entries of the batch ended up sharing the same (invalid)
            // identifier - which, for instance, made "Remover Entradas" delete
            // the wrong rows.
            HashMap<String,EC> porFoto = new HashMap<String,EC>();
            StringBuffer fotos = new StringBuffer();
            for (EC ec: list) {
                ec.setIdEntradaProvaCorrecao(0);
                porFoto.put(ec.getFotoName(), ec);
                if (fotos.length() > 0)
                    fotos.append(",");
                fotos.append("'").append(ec.getFotoName()).append("'");
            }
            ResultSet rs = s.executeQuery(
                "select id_entradaprovacorrecao, foto from entradaprovacorrecao where foto in (" + fotos + ")");
            while (rs.next()) {
                EC ec = porFoto.get(rs.getString(2));
                if (ec != null)
                    ec.setIdEntradaProvaCorrecao(rs.getInt(1));
            }
            rs.close();
        } // adicionar entradaprovacorrecao

        // devolver resultado
        EntradaProvaCorrecao[] result = new EntradaProvaCorrecao[list.size()];
        for (int i=0;i<list.size();i++)
            result[i] = list.get(i).getEntradaProvaCorrecao();
        return result;
    }

    public static void deleteEntradas(EntradaProvaCorrecao[] entradas) throws IOException,SQLException {

        if (entradas.length == 0)
            return;

        int nroBlobs = 0;
        String idsEntradas = "";
        String idsBlobs = "";
        for (EntradaProvaCorrecao entrada: entradas) {
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
                st.executeUpdate("delete from blobdata where idblob in (" + idsBlobs + ")");
            st.executeUpdate("delete from EntradaProvaCorrecao where id_entradaProvaCorrecao in (" + idsEntradas + ")");

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

    public static double[] getControlPoints(int idBlob) throws IOException,SQLException {
        Statement st = App.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select blobdata from blobdata where idblob="+idBlob);
        double[] result = null;
        if (rs.next()) {
            try {
                result = (double[])decode(rs.getBinaryStream(1));
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
