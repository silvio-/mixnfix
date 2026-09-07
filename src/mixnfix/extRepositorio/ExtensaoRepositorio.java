package mixnfix.extRepositorio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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
            AlunoProvaCorrecao apc = new AlunoProvaCorrecao(0,a,provaCorrecao);
            apc.setPersistent(true);
            result.add(apc);
        }
        st.executeUpdate(s.toString());
        st.close();

        // written directly on the database: keep the cache in sync (see the
        // comment on inserirAlunos below)
        App.getRepositorioCache().adicionarAlunoProvaCorrecaoNaCache(new Vector<AlunoProvaCorrecao>(result));

        System.out.println("tempo para adicionar alunos (mseg): "+(System.currentTimeMillis()-t0));
        return result;
    }

    /** SQL quoting of a string literal (a single quote is doubled). */
    private static String quote(String s) {
        if (s == null)
            return "";
        return s.replace("'", "''");
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
                _buffer.append(quote(aluno.getNome()));
                _buffer.append("','");
                _buffer.append(quote(aluno.getMatricula()));
                _buffer.append("',");
                _buffer.append(instituicao.getId());

                _buffer.append(")");
                first = false;
            }

            System.out.println("Sentença: " + _buffer.toString());

            Statement s = App.getConnection().createStatement();

            // adding all students
            s.executeUpdate(_buffer.toString(),Statement.RETURN_GENERATED_KEYS);

            // A multi row insert only reports ONE generated key (the last one),
            // so the identifiers of the students just inserted are read back
            // from the database by their matricula. They used to be left at 0,
            // which made every imported student share the same (invalid)
            // identifier.
            HashMap<String,Aluno> porMatricula = new HashMap<String,Aluno>();
            for (Aluno aluno : alunos) {
                aluno.setId(0);
                if (aluno.getMatricula() != null)
                    porMatricula.put(aluno.getMatricula(), aluno);
            }
            ResultSet rs = s.executeQuery(
                "select id_aluno, matricula from Aluno where id_instituicao = " + instituicao.getId());
            while (rs.next()) {
                Aluno aluno = porMatricula.get(rs.getString(2));
                if (aluno != null)
                    aluno.setId(rs.getInt(1));
            }
            rs.close();

            // The rows above were written straight into the database, bypassing the
            // Repositorio (and therefore its cache). Complete the objects (the
            // institution they were just inserted with, and their persistent state)
            // and register them on the cache, otherwise every later query answered
            // from the cache (e.g. consultarAlunoPorInstituicao(), used by the
            // "Adicionar Alunos" dialog of the grading module) would not see the
            // students just imported until the application was restarted.
            Vector<Aluno> vector = new Vector<Aluno>(alunos.size());
            for (Aluno aluno : alunos) {
                if (aluno.getInstituicao_Aluno() == null)
                    aluno.setInstituicao_Aluno(instituicao); // still transitory: no update issued
                aluno.setPersistent(true);
                vector.add(aluno);
            }
            App.getRepositorioCache().adicionarAlunoNaCache(vector);
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

            // same as above: keep the cache in sync with what was written directly
            Vector<AlunoTurma> vector = new Vector<AlunoTurma>(alunosTurmas.size());
            for (AlunoTurma alunoTurma : alunosTurmas) {
                alunoTurma.setPersistent(true);
                vector.add(alunoTurma);
            }
            App.getRepositorioCache().adicionarAlunoTurmaNaCache(vector);
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
