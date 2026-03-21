package mixnfix.extRepositorio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import linsoft.log.Log;
import mixnfix.gui.App;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoProvaCorrecao;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.ProvaCorrecao;
import mixnfix.modelo.Turma;

public class ExtensaoRepositorio {

    ///////////////////////////////////
    // Log
    private Log _log = new Log();
    public void setLogLocation(String fileName) throws java.io.IOException {
        _log = new Log("", fileName, false);
    }

    public void setLog(Log log) {
        _log = log;
    }

    // Log
    ///////////////////////////////////

    /**
     * Singleton.
     */
    private static ExtensaoRepositorio _instance;

    private ExtensaoRepositorio() {
    }

    public static ExtensaoRepositorio getInstance() {
        if (_instance == null)
            _instance = new ExtensaoRepositorio();
        return _instance;
    }

    private Connection getConnection() throws SQLException {
        return App.getConnection();
    }

    private ResultSet executeQuery(Statement st, String sql) throws SQLException {
        ///////////////////////////////////
        // Log
        if (_log.isEnabled()) {
            _log.writeWithTimestamp(sql);
        }
        // Log
        ///////////////////////////////////

        return st.executeQuery(sql);
    }

    private ResultSet executeQuery(CallableStatement st, String sql) throws SQLException {
        ///////////////////////////////////
        // Log
        if (_log.isEnabled()) {
            _log.writeWithTimestamp(sql);
        }
        // Log
        ///////////////////////////////////

        return st.executeQuery();
    }

    private int executeUpdate(Statement st, String sql) throws SQLException {
        ///////////////////////////////////
        // Log
        if (_log.isEnabled()) {
            _log.writeWithTimestamp(sql);
        }
        // Log
        ///////////////////////////////////

        return st.executeUpdate(sql);
    }

    public List<AlunoProvaCorrecao> inserirAlunosEmProvaCorrecao(ProvaCorrecao provaCorrecao, List<Aluno> alunos) throws SQLException {
        ArrayList<AlunoProvaCorrecao> result = new ArrayList<AlunoProvaCorrecao>();

        if (alunos.size() == 0)
            return result;

        long t0 = System.currentTimeMillis();
        Connection connection = this.getConnection();
        Statement st = connection.createStatement();

        boolean first = true;
        StringBuffer s = new StringBuffer("insert into alunoprovacorrecao (id_provacorrecao, id_aluno, numentradas) values ");
        for (Aluno a : alunos) {
            if (!first) {
                s.append(',');
            }
            s.append("(" + provaCorrecao.getId() + "," + a.getId() + ",0)");
            first = false;
            result.add(new AlunoProvaCorrecao(0,a,provaCorrecao));
        }
        st.executeUpdate(s.toString());
        st.close();

        System.out.println("tempo para adicionar alunos (mseg): "+(System.currentTimeMillis()-t0));
        return result;
    }

    static StringBuffer _buffer = new StringBuffer();
    public static  void inserirAlunos(ArrayList<Aluno> alunos, ArrayList<AlunoTurma> alunosTurmas , Instituicao instituicao) throws SQLException {


        if (alunos.size() > 0) {
            boolean first = true;
            _buffer.setLength(0);
            _buffer.append("insert into Aluno (nome, matricula, id_instituicao) values ");
            for (Aluno aluno : alunos) {
                if (!first)
                    _buffer.append(",");
                _buffer.append("('");
                _buffer.append(aluno.getNome());
                _buffer.append("','");
                _buffer.append(aluno.getMatricula()); // foto
                _buffer.append("',");
                _buffer.append(instituicao.getId());

                _buffer.append(")");
                first = false;
            }

            System.out.println("Sentença: " + _buffer.toString());

            Statement s = App.getConnection().createStatement();

            // adding all controlpoints
            s.executeUpdate(_buffer.toString(),Statement.RETURN_GENERATED_KEYS);

            // generated keys
            ResultSet rs = s.getGeneratedKeys();
            int i = 0;
            while (rs.next()) {
                Aluno aluno = alunos.get(i);
                int id = rs.getInt(1);
                aluno.setId(id);
                i++;
            }
        }

        if (alunosTurmas.size() > 0) {
            boolean first = true;
            _buffer.setLength(0);
            _buffer.append("insert into AlunoTurma (id_aluno, id_turma) values ");
            for (AlunoTurma alunoTurma : alunosTurmas) {
                Aluno aluno = alunoTurma.getAluno_AlunoTurma();
                Turma turma = alunoTurma.getTurma_AlunoTurma();
                if (!first)
                    _buffer.append(",");
                _buffer.append("(");
                _buffer.append(aluno.getId());
                _buffer.append(",");
                _buffer.append(turma.getId());
                _buffer.append(")");
                first = false;
            }
            System.out.println("Sentença: " + _buffer.toString());

            Statement s = App.getConnection().createStatement();

            // adding all controlpoints
            s.executeUpdate(_buffer.toString());
        }
    }



    private int executeUpdate(PreparedStatement st, String sql) throws SQLException {
        ///////////////////////////////////
        // Log
        if (_log.isEnabled()) {
            _log.writeWithTimestamp(sql);
        }
        // Log
        ///////////////////////////////////

        return st.executeUpdate();
    }

    public static void main(String[] args) throws Exception {

        Connection connection = App.getConnection();

        String sql = "insert into Instituicao (Nome) values (?), (?), (?)";

        // java.sql.PreparedStatement


        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1,"'Teste5'");
        statement.setString(2,"'Teste6'");
        statement.setString(3,"'Teste7'");




        // Blob b ;
        // statement.setBlob();

        statement.execute();

        // statement.executeUpdate(sql);

        ResultSet rs = statement.getGeneratedKeys();
        int i = 0;
        while (rs.next())
            System.out.println(rs.getInt(1));



        System.exit(0);
    }



}
