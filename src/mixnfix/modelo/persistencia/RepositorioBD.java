package mixnfix.modelo.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import mixnfix.gui.App;
import mixnfix.gui.ConfiguracaoMIXnFIX;
import mixnfix.modelo.Aluno;
import mixnfix.modelo.AlunoCursoInstancia;
import mixnfix.modelo.AlunoProvaCorrecao;
import mixnfix.modelo.AlunoTurma;
import mixnfix.modelo.Avaliacao;
import mixnfix.modelo.AvaliacaoAluno;
import mixnfix.modelo.ColetaQuestionario;
import mixnfix.modelo.Correcao;
import mixnfix.modelo.Curso;
import mixnfix.modelo.CursoInstancia;
import mixnfix.modelo.EntradaColetaQuestionario;
import mixnfix.modelo.EntradaProvaCorrecao;
import mixnfix.modelo.Instituicao;
import mixnfix.modelo.Periodo;
import mixnfix.modelo.Prova;
import mixnfix.modelo.ProvaCorrecao;
import mixnfix.modelo.Repositorio;
import mixnfix.modelo.Turma;

public class RepositorioBD implements Repositorio {
	private Connection _connection;
	private linsoft.log.Log _log;
	private Repositorio _repositorio;

	public RepositorioBD() {
		_repositorio = null;
		ConnectionVerifier verifier = new ConnectionVerifier();
		verifier.start();
		try {
			_log = new linsoft.log.Log("", "logs/bd.log", false);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setRepositorio(Repositorio r) {
		_repositorio = r;
	}

	public Connection getConnection() throws SQLException {
		if (_connection == null) {
			try {
				Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String dbName = App.getConfiguracao().getProperty(ConfiguracaoMIXnFIX.dbname);
			_connection = DriverManager.getConnection("jdbc:derby:"+dbName,"","");
		}
		return _connection;
		
//		if (_connection == null) {
//			try {
//				Class.forName("com.mysql.jdbc.Driver").newInstance();
//			} catch (InstantiationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			_connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mixnfix","root","");
//		}
//		return _connection;
	}

	private void testConnection() {
		try {
			_connection.getMetaData();
		}
		catch(Exception e){
			_connection = null;
		}
	}

	class ConnectionVerifier extends Thread {
		private final int delay = 60000;

		public void run() {
			while(true) {
				try {
					sleep(delay);
				}
				catch(InterruptedException e) {}
				testConnection();
			}
		}
	}

	public void enableLog(boolean e) {
		_log.setEnabled(e);
	}

	public void setLog(linsoft.log.Log l) {
		_log = l;
	}

	public linsoft.log.Log getLog() {
		return _log;
	}

	public int autoColumn(String table, String column) throws SQLException {
		int v = 0;
		String sql = "select max(" + column + ") from " + table;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		if(res.next()) {
			v = res.getInt("max(" + column + ")");
		}
		return v;
	}

	//////////////// entidade Instituicao ////////////////

	private Instituicao construirInstituicao(ResultSet res) throws SQLException {
		int id_instituicao = res.getInt("id_instituicao");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		Instituicao instituicao = new Instituicao(id_instituicao, nome);
		instituicao.setPersistent(true);
		return instituicao;
	}

	public Instituicao consultarInstituicao(int id_instituicao) throws SQLException {
		String sql = "select id_instituicao, nome from Instituicao where id_instituicao = " + id_instituicao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Instituicao instituicao = null;
		if(res.next()) {
			instituicao = construirInstituicao(res);
		}
		st.close();
		return instituicao;
	}

	public Vector consultarInstituicao() throws SQLException {
		String sql = "select id_instituicao, nome from Instituicao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Instituicao instituicao = construirInstituicao(res);
			vec.add(instituicao);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmInstituicao(Instituicao instituicao, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Instituicao set nome " + nomeStr + " where id_instituicao = " + instituicao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Instituicao inserirInstituicao(Instituicao obj) throws SQLException {
		Instituicao obj2 = inserirInstituicao(obj.getNome());
		return obj2;
	}

	public Instituicao inserirInstituicao(String nome) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "insert into Instituicao(nome) values (" + nomeStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		// Instituicao instituicao = new Instituicao(autoColumn("Instituicao", "id_instituicao"), nome);
        Instituicao instituicao = new Instituicao(id, nome);
		instituicao.setPersistent(true);
		return instituicao;
	}

	public void removerInstituicao(Instituicao obj) throws SQLException {
		removerInstituicao(obj.getId());
	}

	public void removerInstituicao(int id_instituicao) throws SQLException {
		String sql = "delete from Instituicao where id_instituicao = " + id_instituicao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Periodo ////////////////

	private Periodo construirPeriodo(ResultSet res) throws SQLException {
		String id_periodo = res.getString("id_periodo");
		if(id_periodo != null) {
			id_periodo = id_periodo.trim();
		}
		Periodo periodo = new Periodo(id_periodo);
		periodo.setPersistent(true);
		return periodo;
	}

	public Periodo consultarPeriodo(String id_periodo) throws SQLException {
		String id_periodoStr = "= '" + id_periodo + "'";
		String sql = "select id_periodo from Periodo where id_periodo " + id_periodoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Periodo periodo = null;
		if(res.next()) {
			periodo = construirPeriodo(res);
		}
		st.close();
		return periodo;
	}

	public Vector consultarPeriodo() throws SQLException {
		String sql = "select id_periodo from Periodo";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Periodo periodo = construirPeriodo(res);
			vec.add(periodo);
		}
		st.close();
		return vec;
	}

	public void atualizarId_periodoEmPeriodo(Periodo periodo, String id_periodo) throws SQLException {
		String id_periodoStr = "= null";
		if(id_periodo != null) {
			id_periodoStr = "= '" + (id_periodo.length() > 255? id_periodo.substring(0, 255): id_periodo) + "'";
		}
		String sql = "update Periodo set id_periodo " + id_periodoStr + " where id_periodo = '" + periodo.getId_periodo() + "'";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Periodo inserirPeriodo(Periodo obj) throws SQLException {
		Periodo obj2 = inserirPeriodo(obj.getId_periodo());
		return obj2;
	}

	public Periodo inserirPeriodo(String id_periodo) throws SQLException {
		String id_periodoStr = "null";
		if(id_periodo != null) {
			id_periodoStr = "'" + (id_periodo.length() > 255? id_periodo.substring(0, 255): id_periodo) + "'";
		}
		String sql = "insert into Periodo(id_periodo) values (" + id_periodoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Periodo periodo = new Periodo(id_periodo);
		periodo.setPersistent(true);
		return periodo;
	}

	public void removerPeriodo(Periodo obj) throws SQLException {
		removerPeriodo(obj.getId_periodo());
	}

	public void removerPeriodo(String id_periodo) throws SQLException {
		String id_periodoStr = "= '" + id_periodo + "'";
		String sql = "delete from Periodo where id_periodo " + id_periodoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Turma ////////////////

	private Turma construirTurma(ResultSet res) throws SQLException {
		int id_turma = res.getInt("id_turma");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		int id_instituicao = res.getInt("id_instituicao");
		Instituicao instituicao_Turma = null;
		if(id_instituicao != 0) {
			instituicao_Turma = _repositorio.consultarInstituicao(id_instituicao);
		}
		Turma turma = new Turma(id_turma, nome, instituicao_Turma);
		turma.setPersistent(true);
		return turma;
	}

	public Turma consultarTurma(int id_turma) throws SQLException {
		String sql = "select id_turma, nome, id_instituicao from Turma where id_turma = " + id_turma;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Turma turma = null;
		if(res.next()) {
			turma = construirTurma(res);
		}
		st.close();
		return turma;
	}

	public Vector consultarTurma() throws SQLException {
		String sql = "select id_turma, nome, id_instituicao from Turma";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Turma turma = construirTurma(res);
			vec.add(turma);
		}
		st.close();
		return vec;
	}

	public Vector consultarTurmaPorInstituicao(Instituicao instituicao_Turma) throws SQLException {
		String id_instituicaoStr = "is null";
		if(instituicao_Turma != null) {
			id_instituicaoStr = "= " + instituicao_Turma.getId();
		}
		String sql = "select id_turma, nome, id_instituicao from Turma where id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Turma turma = construirTurma(res);
			vec.add(turma);
		}
		st.close();
		return vec;
	}

	public Vector consultarTurmaPorInstituicaoNome(String nome, Instituicao instituicao_Turma) throws SQLException {
		String nomeStr = "is null";
		if(nome != null) {
			nomeStr = "= '" + nome + "'";
		}
		String id_instituicaoStr = "is null";
		if(instituicao_Turma != null) {
			id_instituicaoStr = "= " + instituicao_Turma.getId();
		}
		String sql = "select id_turma, nome, id_instituicao from Turma where nome " + nomeStr + " and id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Turma turma = construirTurma(res);
			vec.add(turma);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmTurma(Turma turma, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Turma set nome " + nomeStr + " where id_turma = " + turma.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarInstituicao_TurmaEmTurma(Turma turma, Instituicao instituicao_Turma) throws SQLException {
		String id_instituicaoStr = "= null";
		if(instituicao_Turma != null) {
			id_instituicaoStr = "= " + instituicao_Turma.getId();
		}
		String sql = "update Turma set id_instituicao " + id_instituicaoStr + " where id_turma = " + turma.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Turma inserirTurma(Turma obj) throws SQLException {
		Turma obj2 = inserirTurma(obj.getNome(), obj.getInstituicao_Turma());
		return obj2;
	}

	public Turma inserirTurma(String nome, Instituicao instituicao_Turma) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String id_instituicaoStr = "null";
		if(instituicao_Turma != null) {
			id_instituicaoStr = "" + instituicao_Turma.getId();
		}
		String sql = "insert into Turma(nome, id_instituicao) values (" + nomeStr + ", " + id_instituicaoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
        Turma turma = new Turma(id, nome, instituicao_Turma);
		//Turma turma = new Turma(autoColumn("Turma", "id_turma"), nome, instituicao_Turma);
		turma.setPersistent(true);
		return turma;
	}

	public void removerTurma(Turma obj) throws SQLException {
		removerTurma(obj.getId());
	}

	public void removerTurma(int id_turma) throws SQLException {
		String sql = "delete from Turma where id_turma = " + id_turma;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Aluno ////////////////

	private Aluno construirAluno(ResultSet res) throws SQLException {
		int id_aluno = res.getInt("id_aluno");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		String matricula = res.getString("matricula");
		if(matricula != null) {
			matricula = matricula.trim();
		}
		int id_instituicao = res.getInt("id_instituicao");
		Instituicao instituicao_Aluno = null;
		if(id_instituicao != 0) {
			instituicao_Aluno = _repositorio.consultarInstituicao(id_instituicao);
		}
		Aluno aluno = new Aluno(id_aluno, nome, matricula, instituicao_Aluno);
		aluno.setPersistent(true);
		return aluno;
	}

	public Aluno consultarAluno(int id_aluno) throws SQLException {
		String sql = "select id_aluno, nome, matricula, id_instituicao from Aluno where id_aluno = " + id_aluno;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Aluno aluno = null;
		if(res.next()) {
			aluno = construirAluno(res);
		}
		st.close();
		return aluno;
	}

	public Vector consultarAluno() throws SQLException {
		String sql = "select id_aluno, nome, matricula, id_instituicao from Aluno";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Aluno aluno = construirAluno(res);
			vec.add(aluno);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoPorMatricula(String matricula) throws SQLException {
		String matriculaStr = "is null";
		if(matricula != null) {
			matriculaStr = "= '" + matricula + "'";
		}
		String sql = "select id_aluno, nome, matricula, id_instituicao from Aluno where matricula " + matriculaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Aluno aluno = construirAluno(res);
			vec.add(aluno);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoPorInstituicao(Instituicao instituicao_Aluno) throws SQLException {
		String id_instituicaoStr = "is null";
		if(instituicao_Aluno != null) {
			id_instituicaoStr = "= " + instituicao_Aluno.getId();
		}
		String sql = "select id_aluno, nome, matricula, id_instituicao from Aluno where id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Aluno aluno = construirAluno(res);
			vec.add(aluno);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmAluno(Aluno aluno, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Aluno set nome " + nomeStr + " where id_aluno = " + aluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarMatriculaEmAluno(Aluno aluno, String matricula) throws SQLException {
		String matriculaStr = "= null";
		if(matricula != null) {
			matriculaStr = "= '" + (matricula.length() > 64? matricula.substring(0, 64): matricula) + "'";
		}
		String sql = "update Aluno set matricula " + matriculaStr + " where id_aluno = " + aluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarInstituicao_AlunoEmAluno(Aluno aluno, Instituicao instituicao_Aluno) throws SQLException {
		String id_instituicaoStr = "= null";
		if(instituicao_Aluno != null) {
			id_instituicaoStr = "= " + instituicao_Aluno.getId();
		}
		String sql = "update Aluno set id_instituicao " + id_instituicaoStr + " where id_aluno = " + aluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Aluno inserirAluno(Aluno obj) throws SQLException {
		Aluno obj2 = inserirAluno(obj.getNome(), obj.getMatricula(), obj.getInstituicao_Aluno());
		return obj2;
	}

	public Aluno inserirAluno(String nome, String matricula, Instituicao instituicao_Aluno) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String matriculaStr = "null";
		if(matricula != null) {
			matriculaStr = "'" + (matricula.length() > 64? matricula.substring(0, 64): matricula) + "'";
		}
		String id_instituicaoStr = "null";
		if(instituicao_Aluno != null) {
			id_instituicaoStr = "" + instituicao_Aluno.getId();
		}
		String sql = "insert into Aluno(nome, matricula, id_instituicao) values (" + nomeStr + ", " + matriculaStr + ", " + id_instituicaoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();

        Aluno aluno = new Aluno(id, nome, matricula, instituicao_Aluno);
        //Aluno aluno = new Aluno(autoColumn("Aluno", "id_aluno"), nome, matricula, instituicao_Aluno);
		aluno.setPersistent(true);
		return aluno;
	}

	public void removerAluno(Aluno obj) throws SQLException {
		removerAluno(obj.getId());
	}

	public void removerAluno(int id_aluno) throws SQLException {
		String sql = "delete from Aluno where id_aluno = " + id_aluno;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Curso ////////////////

	private Curso construirCurso(ResultSet res) throws SQLException {
		int id_curso = res.getInt("id_curso");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		int id_instituicao = res.getInt("id_instituicao");
		Instituicao instituicao_Curso = null;
		if(id_instituicao != 0) {
			instituicao_Curso = _repositorio.consultarInstituicao(id_instituicao);
		}
		Curso curso = new Curso(id_curso, nome, instituicao_Curso);
		curso.setPersistent(true);
		return curso;
	}

	public Curso consultarCurso(int id_curso) throws SQLException {
		String sql = "select id_curso, nome, id_instituicao from Curso where id_curso = " + id_curso;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Curso curso = null;
		if(res.next()) {
			curso = construirCurso(res);
		}
		st.close();
		return curso;
	}

	public Vector consultarCurso() throws SQLException {
		String sql = "select id_curso, nome, id_instituicao from Curso";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Curso curso = construirCurso(res);
			vec.add(curso);
		}
		st.close();
		return vec;
	}

	public Vector consultarCursoPorInstituicao(Instituicao instituicao_Curso) throws SQLException {
		String id_instituicaoStr = "is null";
		if(instituicao_Curso != null) {
			id_instituicaoStr = "= " + instituicao_Curso.getId();
		}
		String sql = "select id_curso, nome, id_instituicao from Curso where id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Curso curso = construirCurso(res);
			vec.add(curso);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmCurso(Curso curso, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Curso set nome " + nomeStr + " where id_curso = " + curso.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarInstituicao_CursoEmCurso(Curso curso, Instituicao instituicao_Curso) throws SQLException {
		String id_instituicaoStr = "= null";
		if(instituicao_Curso != null) {
			id_instituicaoStr = "= " + instituicao_Curso.getId();
		}
		String sql = "update Curso set id_instituicao " + id_instituicaoStr + " where id_curso = " + curso.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Curso inserirCurso(Curso obj) throws SQLException {
		Curso obj2 = inserirCurso(obj.getNome(), obj.getInstituicao_Curso());
		return obj2;
	}

	public Curso inserirCurso(String nome, Instituicao instituicao_Curso) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String id_instituicaoStr = "null";
		if(instituicao_Curso != null) {
			id_instituicaoStr = "" + instituicao_Curso.getId();
		}
		String sql = "insert into Curso(nome, id_instituicao) values (" + nomeStr + ", " + id_instituicaoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		Curso curso = new Curso(id, nome, instituicao_Curso);
		// Curso curso = new Curso(autoColumn("Curso", "id_curso"), nome, instituicao_Curso);
		curso.setPersistent(true);
		return curso;
	}

	public void removerCurso(Curso obj) throws SQLException {
		removerCurso(obj.getId());
	}

	public void removerCurso(int id_curso) throws SQLException {
		String sql = "delete from Curso where id_curso = " + id_curso;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade AlunoTurma ////////////////

	private AlunoTurma construirAlunoTurma(ResultSet res) throws SQLException {
		int id_aluno = res.getInt("id_aluno");
		Aluno aluno_AlunoTurma = null;
		if(id_aluno != 0) {
			aluno_AlunoTurma = _repositorio.consultarAluno(id_aluno);
		}
		int id_turma = res.getInt("id_turma");
		Turma turma_AlunoTurma = null;
		if(id_turma != 0) {
			turma_AlunoTurma = _repositorio.consultarTurma(id_turma);
		}
		AlunoTurma alunoTurma = new AlunoTurma(aluno_AlunoTurma, turma_AlunoTurma);
		alunoTurma.setPersistent(true);
		return alunoTurma;
	}

	public AlunoTurma consultarAlunoTurma(int id_aluno, int id_turma) throws SQLException {
		String sql = "select id_aluno, id_turma from AlunoTurma where id_aluno = " + id_aluno + " and id_turma = " + id_turma;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AlunoTurma alunoTurma = null;
		if(res.next()) {
			alunoTurma = construirAlunoTurma(res);
		}
		st.close();
		return alunoTurma;
	}

	public Vector consultarAlunoTurma() throws SQLException {
		String sql = "select id_aluno, id_turma from AlunoTurma";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoTurma alunoTurma = construirAlunoTurma(res);
			vec.add(alunoTurma);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoTurmaPorTurma(Turma turma_AlunoTurma) throws SQLException {
		String id_turmaStr = "is null";
		if(turma_AlunoTurma != null) {
			id_turmaStr = "= " + turma_AlunoTurma.getId();
		}
		String sql = "select id_aluno, id_turma from AlunoTurma where id_turma " + id_turmaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoTurma alunoTurma = construirAlunoTurma(res);
			vec.add(alunoTurma);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoTurmaPorAluno(Aluno aluno_AlunoTurma) throws SQLException {
		String id_alunoStr = "is null";
		if(aluno_AlunoTurma != null) {
			id_alunoStr = "= " + aluno_AlunoTurma.getId();
		}
		String sql = "select id_aluno, id_turma from AlunoTurma where id_aluno " + id_alunoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoTurma alunoTurma = construirAlunoTurma(res);
			vec.add(alunoTurma);
		}
		st.close();
		return vec;
	}

	public void atualizarAluno_AlunoTurmaEmAlunoTurma(AlunoTurma alunoTurma, Aluno aluno_AlunoTurma) throws SQLException {
		String id_alunoStr = "= null";
		if(aluno_AlunoTurma != null) {
			id_alunoStr = "= " + aluno_AlunoTurma.getId();
		}
		String sql = "update AlunoTurma set id_aluno " + id_alunoStr + " where id_aluno = " + alunoTurma.getId_aluno() + " and id_turma = " + alunoTurma.getId_turma();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarTurma_AlunoTurmaEmAlunoTurma(AlunoTurma alunoTurma, Turma turma_AlunoTurma) throws SQLException {
		String id_turmaStr = "= null";
		if(turma_AlunoTurma != null) {
			id_turmaStr = "= " + turma_AlunoTurma.getId();
		}
		String sql = "update AlunoTurma set id_turma " + id_turmaStr + " where id_aluno = " + alunoTurma.getId_aluno() + " and id_turma = " + alunoTurma.getId_turma();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public AlunoTurma inserirAlunoTurma(AlunoTurma obj) throws SQLException {
		AlunoTurma obj2 = inserirAlunoTurma(obj.getAluno_AlunoTurma(), obj.getTurma_AlunoTurma());
		return obj2;
	}

	public AlunoTurma inserirAlunoTurma(Aluno aluno_AlunoTurma, Turma turma_AlunoTurma) throws SQLException {
		String id_alunoStr = "null";
		if(aluno_AlunoTurma != null) {
			id_alunoStr = "" + aluno_AlunoTurma.getId();
		}
		String id_turmaStr = "null";
		if(turma_AlunoTurma != null) {
			id_turmaStr = "" + turma_AlunoTurma.getId();
		}
		String sql = "insert into AlunoTurma(id_aluno, id_turma) values (" + id_alunoStr + ", " + id_turmaStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AlunoTurma alunoTurma = new AlunoTurma(aluno_AlunoTurma, turma_AlunoTurma);
		alunoTurma.setPersistent(true);
		return alunoTurma;
	}

	public void removerAlunoTurma(AlunoTurma obj) throws SQLException {
		removerAlunoTurma(obj.getId_aluno(), obj.getId_turma());
	}

	public void removerAlunoTurma(int id_aluno, int id_turma) throws SQLException {
		String sql = "delete from AlunoTurma where id_aluno = " + id_aluno + " and id_turma = " + id_turma;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade CursoInstancia ////////////////

	private CursoInstancia construirCursoInstancia(ResultSet res) throws SQLException {
		int id_cursoinstancia = res.getInt("id_cursoinstancia");
		int id_instituicao = res.getInt("id_instituicao");
		Instituicao instituicao_CursoInstancia = null;
		if(id_instituicao != 0) {
			instituicao_CursoInstancia = _repositorio.consultarInstituicao(id_instituicao);
		}
		int id_curso = res.getInt("id_curso");
		Curso curso_CursoInstancia = null;
		if(id_curso != 0) {
			curso_CursoInstancia = _repositorio.consultarCurso(id_curso);
		}
		String id_periodo = res.getString("id_periodo");
		if(id_periodo != null) {
			id_periodo = id_periodo.trim();
		}
		Periodo periodo_CursoInstancia = null;
		if(id_periodo != null) {
			periodo_CursoInstancia = _repositorio.consultarPeriodo(id_periodo);
		}
		CursoInstancia cursoInstancia = new CursoInstancia(id_cursoinstancia, instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia);
		cursoInstancia.setPersistent(true);
		return cursoInstancia;
	}

	public CursoInstancia consultarCursoInstancia(int id_cursoinstancia) throws SQLException {
		String sql = "select id_cursoinstancia, id_instituicao, id_curso, id_periodo from CursoInstancia where id_cursoinstancia = " + id_cursoinstancia;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		CursoInstancia cursoInstancia = null;
		if(res.next()) {
			cursoInstancia = construirCursoInstancia(res);
		}
		st.close();
		return cursoInstancia;
	}

	public Vector consultarCursoInstancia() throws SQLException {
		String sql = "select id_cursoinstancia, id_instituicao, id_curso, id_periodo from CursoInstancia";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			CursoInstancia cursoInstancia = construirCursoInstancia(res);
			vec.add(cursoInstancia);
		}
		st.close();
		return vec;
	}

	public Vector consultarCursoInstanciaPorInstituicao(Instituicao instituicao_CursoInstancia) throws SQLException {
		String id_instituicaoStr = "is null";
		if(instituicao_CursoInstancia != null) {
			id_instituicaoStr = "= " + instituicao_CursoInstancia.getId();
		}
		String sql = "select id_cursoinstancia, id_instituicao, id_curso, id_periodo from CursoInstancia where id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			CursoInstancia cursoInstancia = construirCursoInstancia(res);
			vec.add(cursoInstancia);
		}
		st.close();
		return vec;
	}

	public Vector consultarCursoInstanciaPorCurso(Curso curso_CursoInstancia) throws SQLException {
		String id_cursoStr = "is null";
		if(curso_CursoInstancia != null) {
			id_cursoStr = "= " + curso_CursoInstancia.getId();
		}
		String sql = "select id_cursoinstancia, id_instituicao, id_curso, id_periodo from CursoInstancia where id_curso " + id_cursoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			CursoInstancia cursoInstancia = construirCursoInstancia(res);
			vec.add(cursoInstancia);
		}
		st.close();
		return vec;
	}

	public Vector consultarCursoInstanciaPorPeriodo(Periodo periodo_CursoInstancia) throws SQLException {
		String id_periodoStr = "is null";
		if(periodo_CursoInstancia != null) {
			id_periodoStr = "= '" + periodo_CursoInstancia.getId_periodo() + "'";
		}
		String sql = "select id_cursoinstancia, id_instituicao, id_curso, id_periodo from CursoInstancia where id_periodo " + id_periodoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			CursoInstancia cursoInstancia = construirCursoInstancia(res);
			vec.add(cursoInstancia);
		}
		st.close();
		return vec;
	}

	public void atualizarInstituicao_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Instituicao instituicao_CursoInstancia) throws SQLException {
		String id_instituicaoStr = "= null";
		if(instituicao_CursoInstancia != null) {
			id_instituicaoStr = "= " + instituicao_CursoInstancia.getId();
		}
		String sql = "update CursoInstancia set id_instituicao " + id_instituicaoStr + " where id_cursoinstancia = " + cursoInstancia.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarCurso_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Curso curso_CursoInstancia) throws SQLException {
		String id_cursoStr = "= null";
		if(curso_CursoInstancia != null) {
			id_cursoStr = "= " + curso_CursoInstancia.getId();
		}
		String sql = "update CursoInstancia set id_curso " + id_cursoStr + " where id_cursoinstancia = " + cursoInstancia.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarPeriodo_CursoInstanciaEmCursoInstancia(CursoInstancia cursoInstancia, Periodo periodo_CursoInstancia) throws SQLException {
		String id_periodoStr = "= null";
		if(periodo_CursoInstancia != null) {
			id_periodoStr = "= '" + (periodo_CursoInstancia.getId_periodo().length() > 255? periodo_CursoInstancia.getId_periodo().substring(0, 255): periodo_CursoInstancia.getId_periodo()) + "'";
		}
		String sql = "update CursoInstancia set id_periodo " + id_periodoStr + " where id_cursoinstancia = " + cursoInstancia.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public CursoInstancia inserirCursoInstancia(CursoInstancia obj) throws SQLException {
		CursoInstancia obj2 = inserirCursoInstancia(obj.getInstituicao_CursoInstancia(), obj.getCurso_CursoInstancia(), obj.getPeriodo_CursoInstancia());
		return obj2;
	}

	public CursoInstancia inserirCursoInstancia(Instituicao instituicao_CursoInstancia, Curso curso_CursoInstancia, Periodo periodo_CursoInstancia) throws SQLException {
		String id_instituicaoStr = "null";
		if(instituicao_CursoInstancia != null) {
			id_instituicaoStr = "" + instituicao_CursoInstancia.getId();
		}
		String id_cursoStr = "null";
		if(curso_CursoInstancia != null) {
			id_cursoStr = "" + curso_CursoInstancia.getId();
		}
		String id_periodoStr = "null";
		if(periodo_CursoInstancia != null) {
			id_periodoStr = "'" + (periodo_CursoInstancia.getId_periodo().length() > 255? periodo_CursoInstancia.getId_periodo().substring(0, 255): periodo_CursoInstancia.getId_periodo()) + "'";
		}
		String sql = "insert into CursoInstancia(id_instituicao, id_curso, id_periodo) values (" + id_instituicaoStr + ", " + id_cursoStr + ", " + id_periodoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
        CursoInstancia cursoInstancia = new CursoInstancia(id, instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia);
        // CursoInstancia cursoInstancia = new CursoInstancia(autoColumn("CursoInstancia", "id_cursoinstancia"), instituicao_CursoInstancia, curso_CursoInstancia, periodo_CursoInstancia);
		cursoInstancia.setPersistent(true);
		return cursoInstancia;
	}

	public void removerCursoInstancia(CursoInstancia obj) throws SQLException {
		removerCursoInstancia(obj.getId());
	}

	public void removerCursoInstancia(int id_cursoinstancia) throws SQLException {
		String sql = "delete from CursoInstancia where id_cursoinstancia = " + id_cursoinstancia;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade AlunoCursoInstancia ////////////////

	private AlunoCursoInstancia construirAlunoCursoInstancia(ResultSet res) throws SQLException {
		int id_alunocursoinstancia = res.getInt("id_alunocursoinstancia");
		int id_cursoinstancia = res.getInt("id_cursoinstancia");
		CursoInstancia cursoInstancia_AlunoCursoInstancia = null;
		if(id_cursoinstancia != 0) {
			cursoInstancia_AlunoCursoInstancia = _repositorio.consultarCursoInstancia(id_cursoinstancia);
		}
		int id_aluno = res.getInt("id_aluno");
		Aluno aluno_AlunoCursoInstancia = null;
		if(id_aluno != 0) {
			aluno_AlunoCursoInstancia = _repositorio.consultarAluno(id_aluno);
		}
		AlunoCursoInstancia alunoCursoInstancia = new AlunoCursoInstancia(id_alunocursoinstancia, cursoInstancia_AlunoCursoInstancia, aluno_AlunoCursoInstancia);
		alunoCursoInstancia.setPersistent(true);
		return alunoCursoInstancia;
	}

	public AlunoCursoInstancia consultarAlunoCursoInstancia(int id_alunocursoinstancia) throws SQLException {
		String sql = "select id_alunocursoinstancia, id_cursoinstancia, id_aluno from AlunoCursoInstancia where id_alunocursoinstancia = " + id_alunocursoinstancia;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AlunoCursoInstancia alunoCursoInstancia = null;
		if(res.next()) {
			alunoCursoInstancia = construirAlunoCursoInstancia(res);
		}
		st.close();
		return alunoCursoInstancia;
	}

	public Vector consultarAlunoCursoInstancia() throws SQLException {
		String sql = "select id_alunocursoinstancia, id_cursoinstancia, id_aluno from AlunoCursoInstancia";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoCursoInstancia alunoCursoInstancia = construirAlunoCursoInstancia(res);
			vec.add(alunoCursoInstancia);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoCursoInstanciaPorCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia) throws SQLException {
		String id_cursoinstanciaStr = "is null";
		if(cursoInstancia_AlunoCursoInstancia != null) {
			id_cursoinstanciaStr = "= " + cursoInstancia_AlunoCursoInstancia.getId();
		}
		String sql = "select id_alunocursoinstancia, id_cursoinstancia, id_aluno from AlunoCursoInstancia where id_cursoinstancia " + id_cursoinstanciaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoCursoInstancia alunoCursoInstancia = construirAlunoCursoInstancia(res);
			vec.add(alunoCursoInstancia);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoCursoInstanciaPorAluno(Aluno aluno_AlunoCursoInstancia) throws SQLException {
		String id_alunoStr = "is null";
		if(aluno_AlunoCursoInstancia != null) {
			id_alunoStr = "= " + aluno_AlunoCursoInstancia.getId();
		}
		String sql = "select id_alunocursoinstancia, id_cursoinstancia, id_aluno from AlunoCursoInstancia where id_aluno " + id_alunoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoCursoInstancia alunoCursoInstancia = construirAlunoCursoInstancia(res);
			vec.add(alunoCursoInstancia);
		}
		st.close();
		return vec;
	}

	public void atualizarCursoInstancia_AlunoCursoInstanciaEmAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia, CursoInstancia cursoInstancia_AlunoCursoInstancia) throws SQLException {
		String id_cursoinstanciaStr = "= null";
		if(cursoInstancia_AlunoCursoInstancia != null) {
			id_cursoinstanciaStr = "= " + cursoInstancia_AlunoCursoInstancia.getId();
		}
		String sql = "update AlunoCursoInstancia set id_cursoinstancia " + id_cursoinstanciaStr + " where id_alunocursoinstancia = " + alunoCursoInstancia.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAluno_AlunoCursoInstanciaEmAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia, Aluno aluno_AlunoCursoInstancia) throws SQLException {
		String id_alunoStr = "= null";
		if(aluno_AlunoCursoInstancia != null) {
			id_alunoStr = "= " + aluno_AlunoCursoInstancia.getId();
		}
		String sql = "update AlunoCursoInstancia set id_aluno " + id_alunoStr + " where id_alunocursoinstancia = " + alunoCursoInstancia.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public AlunoCursoInstancia inserirAlunoCursoInstancia(AlunoCursoInstancia obj) throws SQLException {
		AlunoCursoInstancia obj2 = inserirAlunoCursoInstancia(obj.getCursoInstancia_AlunoCursoInstancia(), obj.getAluno_AlunoCursoInstancia());
		return obj2;
	}

	public AlunoCursoInstancia inserirAlunoCursoInstancia(CursoInstancia cursoInstancia_AlunoCursoInstancia, Aluno aluno_AlunoCursoInstancia) throws SQLException {
		String id_cursoinstanciaStr = "null";
		if(cursoInstancia_AlunoCursoInstancia != null) {
			id_cursoinstanciaStr = "" + cursoInstancia_AlunoCursoInstancia.getId();
		}
		String id_alunoStr = "null";
		if(aluno_AlunoCursoInstancia != null) {
			id_alunoStr = "" + aluno_AlunoCursoInstancia.getId();
		}
		String sql = "insert into AlunoCursoInstancia(id_cursoinstancia, id_aluno) values (" + id_cursoinstanciaStr + ", " + id_alunoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		AlunoCursoInstancia alunoCursoInstancia = new AlunoCursoInstancia(id, cursoInstancia_AlunoCursoInstancia, aluno_AlunoCursoInstancia);
		alunoCursoInstancia.setPersistent(true);
		return alunoCursoInstancia;
	}

	public void removerAlunoCursoInstancia(AlunoCursoInstancia obj) throws SQLException {
		removerAlunoCursoInstancia(obj.getId());
	}

	public void removerAlunoCursoInstancia(int id_alunocursoinstancia) throws SQLException {
		String sql = "delete from AlunoCursoInstancia where id_alunocursoinstancia = " + id_alunocursoinstancia;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Avaliacao ////////////////

	private Avaliacao construirAvaliacao(ResultSet res) throws SQLException {
		int id_avaliacao = res.getInt("id_avaliacao");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		String tipo = res.getString("tipo");
		if(tipo != null) {
			tipo = tipo.trim();
		}
		int id_cursoinstancia = res.getInt("id_cursoinstancia");
		CursoInstancia cursoInstancia_Avaliacao = null;
		if(id_cursoinstancia != 0) {
			cursoInstancia_Avaliacao = _repositorio.consultarCursoInstancia(id_cursoinstancia);
		}
		Avaliacao avaliacao = new Avaliacao(id_avaliacao, nome, tipo, cursoInstancia_Avaliacao);
		avaliacao.setPersistent(true);
		return avaliacao;
	}

	public Avaliacao consultarAvaliacao(int id_avaliacao) throws SQLException {
		String sql = "select id_avaliacao, nome, tipo, id_cursoinstancia from Avaliacao where id_avaliacao = " + id_avaliacao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Avaliacao avaliacao = null;
		if(res.next()) {
			avaliacao = construirAvaliacao(res);
		}
		st.close();
		return avaliacao;
	}

	public Vector consultarAvaliacao() throws SQLException {
		String sql = "select id_avaliacao, nome, tipo, id_cursoinstancia from Avaliacao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Avaliacao avaliacao = construirAvaliacao(res);
			vec.add(avaliacao);
		}
		st.close();
		return vec;
	}

	public Vector consultarAvaliacaoPorTipo(String tipo) throws SQLException {
		String tipoStr = "is null";
		if(tipo != null) {
			tipoStr = "= '" + tipo + "'";
		}
		String sql = "select id_avaliacao, nome, tipo, id_cursoinstancia from Avaliacao where tipo " + tipoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Avaliacao avaliacao = construirAvaliacao(res);
			vec.add(avaliacao);
		}
		st.close();
		return vec;
	}

	public Vector consultarAvaliacaoPorCursoInstancia(CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		String id_cursoinstanciaStr = "is null";
		if(cursoInstancia_Avaliacao != null) {
			id_cursoinstanciaStr = "= " + cursoInstancia_Avaliacao.getId();
		}
		String sql = "select id_avaliacao, nome, tipo, id_cursoinstancia from Avaliacao where id_cursoinstancia " + id_cursoinstanciaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Avaliacao avaliacao = construirAvaliacao(res);
			vec.add(avaliacao);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmAvaliacao(Avaliacao avaliacao, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Avaliacao set nome " + nomeStr + " where id_avaliacao = " + avaliacao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarTipoEmAvaliacao(Avaliacao avaliacao, String tipo) throws SQLException {
		String tipoStr = "= null";
		if(tipo != null) {
			tipoStr = "= '" + (tipo.length() > 32? tipo.substring(0, 32): tipo) + "'";
		}
		String sql = "update Avaliacao set tipo " + tipoStr + " where id_avaliacao = " + avaliacao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarCursoInstancia_AvaliacaoEmAvaliacao(Avaliacao avaliacao, CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		String id_cursoinstanciaStr = "= null";
		if(cursoInstancia_Avaliacao != null) {
			id_cursoinstanciaStr = "= " + cursoInstancia_Avaliacao.getId();
		}
		String sql = "update Avaliacao set id_cursoinstancia " + id_cursoinstanciaStr + " where id_avaliacao = " + avaliacao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Avaliacao inserirAvaliacao(Avaliacao obj) throws SQLException {
		Avaliacao obj2 = inserirAvaliacao(obj.getNome(), obj.getTipo(), obj.getCursoInstancia_Avaliacao());
		return obj2;
	}

	public Avaliacao inserirAvaliacao(String nome, String tipo, CursoInstancia cursoInstancia_Avaliacao) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String tipoStr = "null";
		if(tipo != null) {
			tipoStr = "'" + (tipo.length() > 32? tipo.substring(0, 32): tipo) + "'";
		}
		String id_cursoinstanciaStr = "null";
		if(cursoInstancia_Avaliacao != null) {
			id_cursoinstanciaStr = "" + cursoInstancia_Avaliacao.getId();
		}
		String sql = "insert into Avaliacao(nome, tipo, id_cursoinstancia) values (" + nomeStr + ", " + tipoStr + ", " + id_cursoinstanciaStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		Avaliacao avaliacao = new Avaliacao(id, nome, tipo, cursoInstancia_Avaliacao);
		avaliacao.setPersistent(true);
		return avaliacao;
	}

	public void removerAvaliacao(Avaliacao obj) throws SQLException {
		removerAvaliacao(obj.getId());
	}

	public void removerAvaliacao(int id_avaliacao) throws SQLException {
		String sql = "delete from Avaliacao where id_avaliacao = " + id_avaliacao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade AvaliacaoAluno ////////////////

	private AvaliacaoAluno construirAvaliacaoAluno(ResultSet res) throws SQLException {
		int id_avaliacaoaluno = res.getInt("id_avaliacaoaluno");
		float nota = res.getFloat("nota");
		String noTotalizado = res.getString("noTotalizado");
		if(noTotalizado != null) {
			noTotalizado = noTotalizado.trim();
		}
		int id_avaliacao = res.getInt("id_avaliacao");
		Avaliacao avaliacao_AvaliacaoAluno = null;
		if(id_avaliacao != 0) {
			avaliacao_AvaliacaoAluno = _repositorio.consultarAvaliacao(id_avaliacao);
		}
		int id_correcao = res.getInt("id_correcao");
		Correcao correcao_AvaliacaoAluno = null;
		if(id_correcao != 0) {
			correcao_AvaliacaoAluno = _repositorio.consultarCorrecao(id_correcao);
		}
		int id_alunocursoinstancia = res.getInt("id_alunocursoinstancia");
		AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno = null;
		if(id_alunocursoinstancia != 0) {
			alunoCursoInstancia_AvaliacaoAluno = _repositorio.consultarAlunoCursoInstancia(id_alunocursoinstancia);
		}
		AvaliacaoAluno avaliacaoAluno = new AvaliacaoAluno(id_avaliacaoaluno, nota, noTotalizado, avaliacao_AvaliacaoAluno, correcao_AvaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno);
		avaliacaoAluno.setPersistent(true);
		return avaliacaoAluno;
	}

	public AvaliacaoAluno consultarAvaliacaoAluno(int id_avaliacaoaluno) throws SQLException {
		String sql = "select id_avaliacaoaluno, nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia from AvaliacaoAluno where id_avaliacaoaluno = " + id_avaliacaoaluno;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AvaliacaoAluno avaliacaoAluno = null;
		if(res.next()) {
			avaliacaoAluno = construirAvaliacaoAluno(res);
		}
		st.close();
		return avaliacaoAluno;
	}

	public Vector consultarAvaliacaoAluno() throws SQLException {
		String sql = "select id_avaliacaoaluno, nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia from AvaliacaoAluno";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AvaliacaoAluno avaliacaoAluno = construirAvaliacaoAluno(res);
			vec.add(avaliacaoAluno);
		}
		st.close();
		return vec;
	}

	public Vector consultarAvaliacaoAlunoPorAvaliacao(Avaliacao avaliacao_AvaliacaoAluno) throws SQLException {
		String id_avaliacaoStr = "is null";
		if(avaliacao_AvaliacaoAluno != null) {
			id_avaliacaoStr = "= " + avaliacao_AvaliacaoAluno.getId();
		}
		String sql = "select id_avaliacaoaluno, nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia from AvaliacaoAluno where id_avaliacao " + id_avaliacaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AvaliacaoAluno avaliacaoAluno = construirAvaliacaoAluno(res);
			vec.add(avaliacaoAluno);
		}
		st.close();
		return vec;
	}

	public Vector consultarAvaliacaoAlunoPorCorrecao(Correcao correcao_AvaliacaoAluno) throws SQLException {
		String id_correcaoStr = "is null";
		if(correcao_AvaliacaoAluno != null) {
			id_correcaoStr = "= " + correcao_AvaliacaoAluno.getId();
		}
		String sql = "select id_avaliacaoaluno, nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia from AvaliacaoAluno where id_correcao " + id_correcaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AvaliacaoAluno avaliacaoAluno = construirAvaliacaoAluno(res);
			vec.add(avaliacaoAluno);
		}
		st.close();
		return vec;
	}

	public Vector consultarAvaliacaoAlunoPorAlunoCursoInstancia(AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		String id_alunocursoinstanciaStr = "is null";
		if(alunoCursoInstancia_AvaliacaoAluno != null) {
			id_alunocursoinstanciaStr = "= " + alunoCursoInstancia_AvaliacaoAluno.getId();
		}
		String sql = "select id_avaliacaoaluno, nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia from AvaliacaoAluno where id_alunocursoinstancia " + id_alunocursoinstanciaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AvaliacaoAluno avaliacaoAluno = construirAvaliacaoAluno(res);
			vec.add(avaliacaoAluno);
		}
		st.close();
		return vec;
	}

	public void atualizarNotaEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, float nota) throws SQLException {
		String sql = "update AvaliacaoAluno set nota = " + nota + " where id_avaliacaoaluno = " + avaliacaoAluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarNoTotalizadoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, String noTotalizado) throws SQLException {
		String noTotalizadoStr = "= null";
		if(noTotalizado != null) {
			noTotalizadoStr = "= '" + (noTotalizado.length() > 128? noTotalizado.substring(0, 128): noTotalizado) + "'";
		}
		String sql = "update AvaliacaoAluno set noTotalizado " + noTotalizadoStr + " where id_avaliacaoaluno = " + avaliacaoAluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAvaliacao_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, Avaliacao avaliacao_AvaliacaoAluno) throws SQLException {
		String id_avaliacaoStr = "= null";
		if(avaliacao_AvaliacaoAluno != null) {
			id_avaliacaoStr = "= " + avaliacao_AvaliacaoAluno.getId();
		}
		String sql = "update AvaliacaoAluno set id_avaliacao " + id_avaliacaoStr + " where id_avaliacaoaluno = " + avaliacaoAluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarCorrecao_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, Correcao correcao_AvaliacaoAluno) throws SQLException {
		String id_correcaoStr = "= null";
		if(correcao_AvaliacaoAluno != null) {
			id_correcaoStr = "= " + correcao_AvaliacaoAluno.getId();
		}
		String sql = "update AvaliacaoAluno set id_correcao " + id_correcaoStr + " where id_avaliacaoaluno = " + avaliacaoAluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAlunoCursoInstancia_AvaliacaoAlunoEmAvaliacaoAluno(AvaliacaoAluno avaliacaoAluno, AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		String id_alunocursoinstanciaStr = "= null";
		if(alunoCursoInstancia_AvaliacaoAluno != null) {
			id_alunocursoinstanciaStr = "= " + alunoCursoInstancia_AvaliacaoAluno.getId();
		}
		String sql = "update AvaliacaoAluno set id_alunocursoinstancia " + id_alunocursoinstanciaStr + " where id_avaliacaoaluno = " + avaliacaoAluno.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public AvaliacaoAluno inserirAvaliacaoAluno(AvaliacaoAluno obj) throws SQLException {
		AvaliacaoAluno obj2 = inserirAvaliacaoAluno(obj.getNota(), obj.getNoTotalizado(), obj.getAvaliacao_AvaliacaoAluno(), obj.getCorrecao_AvaliacaoAluno(), obj.getAlunoCursoInstancia_AvaliacaoAluno());
		return obj2;
	}

	public AvaliacaoAluno inserirAvaliacaoAluno(float nota, String noTotalizado, Avaliacao avaliacao_AvaliacaoAluno, Correcao correcao_AvaliacaoAluno, AlunoCursoInstancia alunoCursoInstancia_AvaliacaoAluno) throws SQLException {
		String noTotalizadoStr = "null";
		if(noTotalizado != null) {
			noTotalizadoStr = "'" + (noTotalizado.length() > 128? noTotalizado.substring(0, 128): noTotalizado) + "'";
		}
		String id_avaliacaoStr = "null";
		if(avaliacao_AvaliacaoAluno != null) {
			id_avaliacaoStr = "" + avaliacao_AvaliacaoAluno.getId();
		}
		String id_correcaoStr = "null";
		if(correcao_AvaliacaoAluno != null) {
			id_correcaoStr = "" + correcao_AvaliacaoAluno.getId();
		}
		String id_alunocursoinstanciaStr = "null";
		if(alunoCursoInstancia_AvaliacaoAluno != null) {
			id_alunocursoinstanciaStr = "" + alunoCursoInstancia_AvaliacaoAluno.getId();
		}
		String sql = "insert into AvaliacaoAluno(nota, noTotalizado, id_avaliacao, id_correcao, id_alunocursoinstancia) values (" + nota + ", " + noTotalizadoStr + ", " + id_avaliacaoStr + ", " + id_correcaoStr + ", " + id_alunocursoinstanciaStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		AvaliacaoAluno avaliacaoAluno = new AvaliacaoAluno(id, nota, noTotalizado, avaliacao_AvaliacaoAluno, correcao_AvaliacaoAluno, alunoCursoInstancia_AvaliacaoAluno);
		avaliacaoAluno.setPersistent(true);
		return avaliacaoAluno;
	}

	public void removerAvaliacaoAluno(AvaliacaoAluno obj) throws SQLException {
		removerAvaliacaoAluno(obj.getId());
	}

	public void removerAvaliacaoAluno(int id_avaliacaoaluno) throws SQLException {
		String sql = "delete from AvaliacaoAluno where id_avaliacaoaluno = " + id_avaliacaoaluno;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Prova ////////////////

	private Prova construirProva(ResultSet res) throws SQLException {
		int id_prova = res.getInt("id_prova");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		String fonte = res.getString("fonte");
		if(fonte != null) {
			fonte = fonte.trim();
		}
		int indice = res.getInt("indice");
		int id_instituicao = res.getInt("id_instituicao");
		Instituicao instituicao_Prova = null;
		if(id_instituicao != 0) {
			instituicao_Prova = _repositorio.consultarInstituicao(id_instituicao);
		}
		Prova prova = new Prova(id_prova, nome, fonte, indice, instituicao_Prova);
		prova.setPersistent(true);
		return prova;
	}

	public Prova consultarProva(int id_prova) throws SQLException {
		String sql = "select id_prova, nome, fonte, indice, id_instituicao from Prova where id_prova = " + id_prova;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Prova prova = null;
		if(res.next()) {
			prova = construirProva(res);
		}
		st.close();
		return prova;
	}

	public Vector consultarProva() throws SQLException {
		String sql = "select id_prova, nome, fonte, indice, id_instituicao from Prova";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Prova prova = construirProva(res);
			vec.add(prova);
		}
		st.close();
		return vec;
	}

	public Vector consultarProvaPorInstituicao(Instituicao instituicao_Prova) throws SQLException {
		String id_instituicaoStr = "is null";
		if(instituicao_Prova != null) {
			id_instituicaoStr = "= " + instituicao_Prova.getId();
		}
		String sql = "select id_prova, nome, fonte, indice, id_instituicao from Prova where id_instituicao " + id_instituicaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Prova prova = construirProva(res);
			vec.add(prova);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmProva(Prova prova, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update Prova set nome " + nomeStr + " where id_prova = " + prova.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarFonteEmProva(Prova prova, String fonte) throws SQLException {
		String fonteStr = "= null";
		if(fonte != null) {
			fonteStr = "= '" + (fonte.length() > 128? fonte.substring(0, 128): fonte) + "'";
		}
		String sql = "update Prova set fonte " + fonteStr + " where id_prova = " + prova.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarIndiceEmProva(Prova prova, int indice) throws SQLException {
		String sql = "update Prova set indice = " + indice + " where id_prova = " + prova.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarInstituicao_ProvaEmProva(Prova prova, Instituicao instituicao_Prova) throws SQLException {
		String id_instituicaoStr = "= null";
		if(instituicao_Prova != null) {
			id_instituicaoStr = "= " + instituicao_Prova.getId();
		}
		String sql = "update Prova set id_instituicao " + id_instituicaoStr + " where id_prova = " + prova.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Prova inserirProva(Prova obj) throws SQLException {
		Prova obj2 = inserirProva(obj.getNome(), obj.getFonte(), obj.getIndice(), obj.getInstituicao_Prova());
		return obj2;
	}

	public Prova inserirProva(String nome, String fonte, int indice, Instituicao instituicao_Prova) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String fonteStr = "null";
		if(fonte != null) {
			fonteStr = "'" + (fonte.length() > 128? fonte.substring(0, 128): fonte) + "'";
		}
		String id_instituicaoStr = "null";
		if(instituicao_Prova != null) {
			id_instituicaoStr = "" + instituicao_Prova.getId();
		}
		String sql = "insert into Prova(nome, fonte, indice, id_instituicao) values (" + nomeStr + ", " + fonteStr + ", " + indice + ", " + id_instituicaoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		//Prova prova = new Prova(autoColumn("Prova", "id_prova"), nome, fonte, indice, instituicao_Prova);
        Prova prova = new Prova(id, nome, fonte, indice, instituicao_Prova);
		prova.setPersistent(true);
		return prova;
	}

	public void removerProva(Prova obj) throws SQLException {
		removerProva(obj.getId());
	}

	public void removerProva(int id_prova) throws SQLException {
		String sql = "delete from Prova where id_prova = " + id_prova;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade Correcao ////////////////

	private Correcao construirCorrecao(ResultSet res) throws SQLException {
		int id_correcao = res.getInt("id_correcao");
		String matriz = res.getString("matriz");
		if(matriz != null) {
			matriz = matriz.trim();
		}
		String imagem = res.getString("imagem");
		if(imagem != null) {
			imagem = imagem.trim();
		}
		float nota = res.getFloat("nota");
		int id_prova = res.getInt("id_prova");
		Prova prova_Correcao = null;
		if(id_prova != 0) {
			prova_Correcao = _repositorio.consultarProva(id_prova);
		}
		int id_aluno = res.getInt("id_aluno");
		Aluno aluno_Correcao = null;
		if(id_aluno != 0) {
			aluno_Correcao = _repositorio.consultarAluno(id_aluno);
		}
		Correcao correcao = new Correcao(id_correcao, matriz, imagem, nota, prova_Correcao, aluno_Correcao);
		correcao.setPersistent(true);
		return correcao;
	}

	public Correcao consultarCorrecao(int id_correcao) throws SQLException {
		String sql = "select id_correcao, matriz, imagem, nota, id_prova, id_aluno from Correcao where id_correcao = " + id_correcao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Correcao correcao = null;
		if(res.next()) {
			correcao = construirCorrecao(res);
		}
		st.close();
		return correcao;
	}

	public Vector consultarCorrecao() throws SQLException {
		String sql = "select id_correcao, matriz, imagem, nota, id_prova, id_aluno from Correcao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Correcao correcao = construirCorrecao(res);
			vec.add(correcao);
		}
		st.close();
		return vec;
	}

	public Vector consultarCorrecaoPorAluno(Aluno aluno_Correcao) throws SQLException {
		String id_alunoStr = "is null";
		if(aluno_Correcao != null) {
			id_alunoStr = "= " + aluno_Correcao.getId();
		}
		String sql = "select id_correcao, matriz, imagem, nota, id_prova, id_aluno from Correcao where id_aluno " + id_alunoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Correcao correcao = construirCorrecao(res);
			vec.add(correcao);
		}
		st.close();
		return vec;
	}

	public Vector consultarCorrecaoPorProva(Prova prova_Correcao) throws SQLException {
		String id_provaStr = "is null";
		if(prova_Correcao != null) {
			id_provaStr = "= " + prova_Correcao.getId();
		}
		String sql = "select id_correcao, matriz, imagem, nota, id_prova, id_aluno from Correcao where id_prova " + id_provaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			Correcao correcao = construirCorrecao(res);
			vec.add(correcao);
		}
		st.close();
		return vec;
	}

	public void atualizarMatrizEmCorrecao(Correcao correcao, String matriz) throws SQLException {
		String matrizStr = "= null";
		if(matriz != null) {
			matrizStr = "= '" + (matriz.length() > 128? matriz.substring(0, 128): matriz) + "'";
		}
		String sql = "update Correcao set matriz " + matrizStr + " where id_correcao = " + correcao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarImagemEmCorrecao(Correcao correcao, String imagem) throws SQLException {
		String imagemStr = "= null";
		if(imagem != null) {
			imagemStr = "= '" + (imagem.length() > 128? imagem.substring(0, 128): imagem) + "'";
		}
		String sql = "update Correcao set imagem " + imagemStr + " where id_correcao = " + correcao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarNotaEmCorrecao(Correcao correcao, float nota) throws SQLException {
		String sql = "update Correcao set nota = " + nota + " where id_correcao = " + correcao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarProva_CorrecaoEmCorrecao(Correcao correcao, Prova prova_Correcao) throws SQLException {
		String id_provaStr = "= null";
		if(prova_Correcao != null) {
			id_provaStr = "= " + prova_Correcao.getId();
		}
		String sql = "update Correcao set id_prova " + id_provaStr + " where id_correcao = " + correcao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAluno_CorrecaoEmCorrecao(Correcao correcao, Aluno aluno_Correcao) throws SQLException {
		String id_alunoStr = "= null";
		if(aluno_Correcao != null) {
			id_alunoStr = "= " + aluno_Correcao.getId();
		}
		String sql = "update Correcao set id_aluno " + id_alunoStr + " where id_correcao = " + correcao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public Correcao inserirCorrecao(Correcao obj) throws SQLException {
		Correcao obj2 = inserirCorrecao(obj.getMatriz(), obj.getImagem(), obj.getNota(), obj.getProva_Correcao(), obj.getAluno_Correcao());
		return obj2;
	}

	public Correcao inserirCorrecao(String matriz, String imagem, float nota, Prova prova_Correcao, Aluno aluno_Correcao) throws SQLException {
		String matrizStr = "null";
		if(matriz != null) {
			matrizStr = "'" + (matriz.length() > 128? matriz.substring(0, 128): matriz) + "'";
		}
		String imagemStr = "null";
		if(imagem != null) {
			imagemStr = "'" + (imagem.length() > 128? imagem.substring(0, 128): imagem) + "'";
		}
		String id_provaStr = "null";
		if(prova_Correcao != null) {
			id_provaStr = "" + prova_Correcao.getId();
		}
		String id_alunoStr = "null";
		if(aluno_Correcao != null) {
			id_alunoStr = "" + aluno_Correcao.getId();
		}
		String sql = "insert into Correcao(matriz, imagem, nota, id_prova, id_aluno) values (" + matrizStr + ", " + imagemStr + ", " + nota + ", " + id_provaStr + ", " + id_alunoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		Correcao correcao = new Correcao(id, matriz, imagem, nota, prova_Correcao, aluno_Correcao);
		correcao.setPersistent(true);
		return correcao;
	}

	public void removerCorrecao(Correcao obj) throws SQLException {
		removerCorrecao(obj.getId());
	}

	public void removerCorrecao(int id_correcao) throws SQLException {
		String sql = "delete from Correcao where id_correcao = " + id_correcao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade ProvaCorrecao ////////////////

	private ProvaCorrecao construirProvaCorrecao(ResultSet res) throws SQLException {
		int id_provacorrecao = res.getInt("id_provacorrecao");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		int id_prova = res.getInt("id_prova");
		Prova prova_ProvaCorrecao = null;
		if(id_prova != 0) {
			prova_ProvaCorrecao = _repositorio.consultarProva(id_prova);
		}
		ProvaCorrecao provaCorrecao = new ProvaCorrecao(id_provacorrecao, nome, prova_ProvaCorrecao);
		provaCorrecao.setPersistent(true);
		return provaCorrecao;
	}

	public ProvaCorrecao consultarProvaCorrecao(int id_provacorrecao) throws SQLException {
		String sql = "select id_provacorrecao, nome, id_prova from ProvaCorrecao where id_provacorrecao = " + id_provacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		ProvaCorrecao provaCorrecao = null;
		if(res.next()) {
			provaCorrecao = construirProvaCorrecao(res);
		}
		st.close();
		return provaCorrecao;
	}

	public Vector consultarProvaCorrecao() throws SQLException {
		String sql = "select id_provacorrecao, nome, id_prova from ProvaCorrecao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			ProvaCorrecao provaCorrecao = construirProvaCorrecao(res);
			vec.add(provaCorrecao);
		}
		st.close();
		return vec;
	}

	public Vector consultarProvaCorrecaoPorProva(Prova prova_ProvaCorrecao) throws SQLException {
		String id_provaStr = "is null";
		if(prova_ProvaCorrecao != null) {
			id_provaStr = "= " + prova_ProvaCorrecao.getId();
		}
		String sql = "select id_provacorrecao, nome, id_prova from ProvaCorrecao where id_prova " + id_provaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			ProvaCorrecao provaCorrecao = construirProvaCorrecao(res);
			vec.add(provaCorrecao);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmProvaCorrecao(ProvaCorrecao provaCorrecao, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update ProvaCorrecao set nome " + nomeStr + " where id_provacorrecao = " + provaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarProva_ProvaCorrecaoEmProvaCorrecao(ProvaCorrecao provaCorrecao, Prova prova_ProvaCorrecao) throws SQLException {
		String id_provaStr = "= null";
		if(prova_ProvaCorrecao != null) {
			id_provaStr = "= " + prova_ProvaCorrecao.getId();
		}
		String sql = "update ProvaCorrecao set id_prova " + id_provaStr + " where id_provacorrecao = " + provaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public ProvaCorrecao inserirProvaCorrecao(ProvaCorrecao obj) throws SQLException {
		ProvaCorrecao obj2 = inserirProvaCorrecao(obj.getNome(), obj.getProva_ProvaCorrecao());
		return obj2;
	}

	public ProvaCorrecao inserirProvaCorrecao(String nome, Prova prova_ProvaCorrecao) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String id_provaStr = "null";
		if(prova_ProvaCorrecao != null) {
			id_provaStr = "" + prova_ProvaCorrecao.getId();
		}
		String sql = "insert into ProvaCorrecao(nome, id_prova) values (" + nomeStr + ", " + id_provaStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		ProvaCorrecao provaCorrecao = new ProvaCorrecao(id, nome, prova_ProvaCorrecao);
		provaCorrecao.setPersistent(true);
		return provaCorrecao;
	}

	public void removerProvaCorrecao(ProvaCorrecao obj) throws SQLException {
		removerProvaCorrecao(obj.getId());
	}

	public void removerProvaCorrecao(int id_provacorrecao) throws SQLException {
		String sql = "delete from ProvaCorrecao where id_provacorrecao = " + id_provacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade AlunoProvaCorrecao ////////////////

	private AlunoProvaCorrecao construirAlunoProvaCorrecao(ResultSet res) throws SQLException {
		int numEntradas = res.getInt("numEntradas");
		int id_aluno = res.getInt("id_aluno");
		Aluno aluno_AlunoProvaCorrecao = null;
		if(id_aluno != 0) {
			aluno_AlunoProvaCorrecao = _repositorio.consultarAluno(id_aluno);
		}
		int id_provacorrecao = res.getInt("id_provacorrecao");
		ProvaCorrecao provaCorrecao_AlunoProvaCorrecao = null;
		if(id_provacorrecao != 0) {
			provaCorrecao_AlunoProvaCorrecao = _repositorio.consultarProvaCorrecao(id_provacorrecao);
		}
		AlunoProvaCorrecao alunoProvaCorrecao = new AlunoProvaCorrecao(numEntradas, aluno_AlunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao);
		alunoProvaCorrecao.setPersistent(true);
		return alunoProvaCorrecao;
	}

	public AlunoProvaCorrecao consultarAlunoProvaCorrecao(int id_aluno, int id_provacorrecao) throws SQLException {
		String sql = "select numEntradas, id_aluno, id_provacorrecao from AlunoProvaCorrecao where id_aluno = " + id_aluno + " and id_provacorrecao = " + id_provacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AlunoProvaCorrecao alunoProvaCorrecao = null;
		if(res.next()) {
			alunoProvaCorrecao = construirAlunoProvaCorrecao(res);
		}
		st.close();
		return alunoProvaCorrecao;
	}

	public Vector consultarAlunoProvaCorrecao() throws SQLException {
		String sql = "select numEntradas, id_aluno, id_provacorrecao from AlunoProvaCorrecao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoProvaCorrecao alunoProvaCorrecao = construirAlunoProvaCorrecao(res);
			vec.add(alunoProvaCorrecao);
		}
		st.close();
		return vec;
	}

	public Vector consultarAlunoProvaCorrecaoPorProvaCorrecao(ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		String id_provacorrecaoStr = "is null";
		if(provaCorrecao_AlunoProvaCorrecao != null) {
			id_provacorrecaoStr = "= " + provaCorrecao_AlunoProvaCorrecao.getId();
		}
		String sql = "select numEntradas, id_aluno, id_provacorrecao from AlunoProvaCorrecao where id_provacorrecao " + id_provacorrecaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			AlunoProvaCorrecao alunoProvaCorrecao = construirAlunoProvaCorrecao(res);
			vec.add(alunoProvaCorrecao);
		}
		st.close();
		return vec;
	}

	public void atualizarNumEntradasEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, int numEntradas) throws SQLException {
		String sql = "update AlunoProvaCorrecao set numEntradas = " + numEntradas + " where id_aluno = " + alunoProvaCorrecao.getId_aluno() + " and id_provacorrecao = " + alunoProvaCorrecao.getId_provacorrecao();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAluno_AlunoProvaCorrecaoEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, Aluno aluno_AlunoProvaCorrecao) throws SQLException {
		String id_alunoStr = "= null";
		if(aluno_AlunoProvaCorrecao != null) {
			id_alunoStr = "= " + aluno_AlunoProvaCorrecao.getId();
		}
		String sql = "update AlunoProvaCorrecao set id_aluno " + id_alunoStr + " where id_aluno = " + alunoProvaCorrecao.getId_aluno() + " and id_provacorrecao = " + alunoProvaCorrecao.getId_provacorrecao();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarProvaCorrecao_AlunoProvaCorrecaoEmAlunoProvaCorrecao(AlunoProvaCorrecao alunoProvaCorrecao, ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		String id_provacorrecaoStr = "= null";
		if(provaCorrecao_AlunoProvaCorrecao != null) {
			id_provacorrecaoStr = "= " + provaCorrecao_AlunoProvaCorrecao.getId();
		}
		String sql = "update AlunoProvaCorrecao set id_provacorrecao " + id_provacorrecaoStr + " where id_aluno = " + alunoProvaCorrecao.getId_aluno() + " and id_provacorrecao = " + alunoProvaCorrecao.getId_provacorrecao();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public AlunoProvaCorrecao inserirAlunoProvaCorrecao(AlunoProvaCorrecao obj) throws SQLException {
		AlunoProvaCorrecao obj2 = inserirAlunoProvaCorrecao(obj.getNumEntradas(), obj.getAluno_AlunoProvaCorrecao(), obj.getProvaCorrecao_AlunoProvaCorrecao());
		return obj2;
	}

	public AlunoProvaCorrecao inserirAlunoProvaCorrecao(int numEntradas, Aluno aluno_AlunoProvaCorrecao, ProvaCorrecao provaCorrecao_AlunoProvaCorrecao) throws SQLException {
		String id_alunoStr = "null";
		if(aluno_AlunoProvaCorrecao != null) {
			id_alunoStr = "" + aluno_AlunoProvaCorrecao.getId();
		}
		String id_provacorrecaoStr = "null";
		if(provaCorrecao_AlunoProvaCorrecao != null) {
			id_provacorrecaoStr = "" + provaCorrecao_AlunoProvaCorrecao.getId();
		}
		String sql = "insert into AlunoProvaCorrecao(numEntradas, id_aluno, id_provacorrecao) values (" + numEntradas + ", " + id_alunoStr + ", " + id_provacorrecaoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		AlunoProvaCorrecao alunoProvaCorrecao = new AlunoProvaCorrecao(numEntradas, aluno_AlunoProvaCorrecao, provaCorrecao_AlunoProvaCorrecao);
		alunoProvaCorrecao.setPersistent(true);
		return alunoProvaCorrecao;
	}

	public void removerAlunoProvaCorrecao(AlunoProvaCorrecao obj) throws SQLException {
		removerAlunoProvaCorrecao(obj.getId_aluno(), obj.getId_provacorrecao());
	}

	public void removerAlunoProvaCorrecao(int id_aluno, int id_provacorrecao) throws SQLException {
		String sql = "delete from AlunoProvaCorrecao where id_aluno = " + id_aluno + " and id_provacorrecao = " + id_provacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade EntradaProvaCorrecao ////////////////

	private EntradaProvaCorrecao construirEntradaProvaCorrecao(ResultSet res) throws SQLException {
		int id_entradaprovacorrecao = res.getInt("id_entradaprovacorrecao");
		byte status = res.getByte("status");
		String foto = res.getString("foto");
		if(foto != null) {
			foto = foto.trim();
		}
		int threshold = res.getInt("threshold");
		int phase = res.getInt("phase");
		int idImageData = res.getInt("idImageData");
		int tipo = res.getInt("tipo");
		int idAnswersData = res.getInt("idAnswersData");
		int id_provacorrecao = res.getInt("id_provacorrecao");
		ProvaCorrecao provaCorrecao_EntradaProvaCorrecao = null;
		if(id_provacorrecao != 0) {
			provaCorrecao_EntradaProvaCorrecao = _repositorio.consultarProvaCorrecao(id_provacorrecao);
		}
		int id_aluno = res.getInt("id_aluno");
		Aluno aluno_EntradaProvaCorrecao = null;
		if(id_aluno != 0) {
			aluno_EntradaProvaCorrecao = _repositorio.consultarAluno(id_aluno);
		}
		EntradaProvaCorrecao entradaProvaCorrecao = new EntradaProvaCorrecao(id_entradaprovacorrecao, status, foto, threshold, phase, idImageData, tipo, idAnswersData, provaCorrecao_EntradaProvaCorrecao, aluno_EntradaProvaCorrecao);
		entradaProvaCorrecao.setPersistent(true);
		return entradaProvaCorrecao;
	}

	public EntradaProvaCorrecao consultarEntradaProvaCorrecao(int id_entradaprovacorrecao) throws SQLException {
		String sql = "select id_entradaprovacorrecao, status, foto, threshold, phase, idImageData, tipo, idAnswersData, id_provacorrecao, id_aluno from EntradaProvaCorrecao where id_entradaprovacorrecao = " + id_entradaprovacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		EntradaProvaCorrecao entradaProvaCorrecao = null;
		if(res.next()) {
			entradaProvaCorrecao = construirEntradaProvaCorrecao(res);
		}
		st.close();
		return entradaProvaCorrecao;
	}

	public Vector consultarEntradaProvaCorrecao() throws SQLException {
		String sql = "select id_entradaprovacorrecao, status, foto, threshold, phase, idImageData, tipo, idAnswersData, id_provacorrecao, id_aluno from EntradaProvaCorrecao";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			EntradaProvaCorrecao entradaProvaCorrecao = construirEntradaProvaCorrecao(res);
			vec.add(entradaProvaCorrecao);
		}
		st.close();
		return vec;
	}

	public Vector consultarEntradaProvaCorrecaoporProvaCorrecao(ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) throws SQLException {
		String id_provacorrecaoStr = "is null";
		if(provaCorrecao_EntradaProvaCorrecao != null) {
			id_provacorrecaoStr = "= " + provaCorrecao_EntradaProvaCorrecao.getId();
		}
		String sql = "select id_entradaprovacorrecao, status, foto, threshold, phase, idImageData, tipo, idAnswersData, id_provacorrecao, id_aluno from EntradaProvaCorrecao where id_provacorrecao " + id_provacorrecaoStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			EntradaProvaCorrecao entradaProvaCorrecao = construirEntradaProvaCorrecao(res);
			vec.add(entradaProvaCorrecao);
		}
		st.close();
		return vec;
	}

	public void atualizarStatusEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, byte status) throws SQLException {
		String sql = "update EntradaProvaCorrecao set status = " + status + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarFotoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, String foto) throws SQLException {
		String fotoStr = "= null";
		if(foto != null) {
			fotoStr = "= '" + (foto.length() > 128? foto.substring(0, 128): foto) + "'";
		}
		String sql = "update EntradaProvaCorrecao set foto " + fotoStr + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarThresholdEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int threshold) throws SQLException {
		String sql = "update EntradaProvaCorrecao set threshold = " + threshold + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarPhaseEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int phase) throws SQLException {
		String sql = "update EntradaProvaCorrecao set phase = " + phase + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarIdImageDataEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int idImageData) throws SQLException {
		String sql = "update EntradaProvaCorrecao set idImageData = " + idImageData + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarTipoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int tipo) throws SQLException {
		String sql = "update EntradaProvaCorrecao set tipo = " + tipo + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarIdAnswersDataEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, int idAnswersData) throws SQLException {
		String sql = "update EntradaProvaCorrecao set idAnswersData = " + idAnswersData + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarProvaCorrecao_EntradaProvaCorrecaoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, ProvaCorrecao provaCorrecao_EntradaProvaCorrecao) throws SQLException {
		String id_provacorrecaoStr = "= null";
		if(provaCorrecao_EntradaProvaCorrecao != null) {
			id_provacorrecaoStr = "= " + provaCorrecao_EntradaProvaCorrecao.getId();
		}
		String sql = "update EntradaProvaCorrecao set id_provacorrecao " + id_provacorrecaoStr + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarAluno_EntradaProvaCorrecaoEmEntradaProvaCorrecao(EntradaProvaCorrecao entradaProvaCorrecao, Aluno aluno_EntradaProvaCorrecao) throws SQLException {
		String id_alunoStr = "= null";
		if(aluno_EntradaProvaCorrecao != null) {
			id_alunoStr = "= " + aluno_EntradaProvaCorrecao.getId();
		}
		String sql = "update EntradaProvaCorrecao set id_aluno " + id_alunoStr + " where id_entradaprovacorrecao = " + entradaProvaCorrecao.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public EntradaProvaCorrecao inserirEntradaProvaCorrecao(EntradaProvaCorrecao obj) throws SQLException {
		EntradaProvaCorrecao obj2 = inserirEntradaProvaCorrecao(obj.getStatus(), obj.getFoto(), obj.getThreshold(), obj.getPhase(), obj.getIdImageData(), obj.getTipo(), obj.getIdAnswersData(), obj.getProvaCorrecao_EntradaProvaCorrecao(), obj.getAluno_EntradaProvaCorrecao());
		return obj2;
	}

	public EntradaProvaCorrecao inserirEntradaProvaCorrecao(byte status, String foto, int threshold, int phase, int idImageData, int tipo, int idAnswersData, ProvaCorrecao provaCorrecao_EntradaProvaCorrecao, Aluno aluno_EntradaProvaCorrecao) throws SQLException {
		String fotoStr = "null";
		if(foto != null) {
			fotoStr = "'" + (foto.length() > 128? foto.substring(0, 128): foto) + "'";
		}
		String id_provacorrecaoStr = "null";
		if(provaCorrecao_EntradaProvaCorrecao != null) {
			id_provacorrecaoStr = "" + provaCorrecao_EntradaProvaCorrecao.getId();
		}
		String id_alunoStr = "null";
		if(aluno_EntradaProvaCorrecao != null) {
			id_alunoStr = "" + aluno_EntradaProvaCorrecao.getId();
		}
		String sql = "insert into EntradaProvaCorrecao(status, foto, threshold, phase, idImageData, tipo, idAnswersData, id_provacorrecao, id_aluno) values (" + status + ", " + fotoStr + ", " + threshold + ", " + phase + ", " + idImageData + ", " + tipo + ", " + idAnswersData + ", " + id_provacorrecaoStr + ", " + id_alunoStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		EntradaProvaCorrecao entradaProvaCorrecao = new EntradaProvaCorrecao(id, status, foto, threshold, phase, idImageData, tipo, idAnswersData, provaCorrecao_EntradaProvaCorrecao, aluno_EntradaProvaCorrecao);
		entradaProvaCorrecao.setPersistent(true);
		return entradaProvaCorrecao;
	}

	public void removerEntradaProvaCorrecao(EntradaProvaCorrecao obj) throws SQLException {
		removerEntradaProvaCorrecao(obj.getId());
	}

	public void removerEntradaProvaCorrecao(int id_entradaprovacorrecao) throws SQLException {
		String sql = "delete from EntradaProvaCorrecao where id_entradaprovacorrecao = " + id_entradaprovacorrecao;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade ColetaQuestionario ////////////////

	private ColetaQuestionario construirColetaQuestionario(ResultSet res) throws SQLException {
		int id_coletaquestionario = res.getInt("id_coletaquestionario");
		String nome = res.getString("nome");
		if(nome != null) {
			nome = nome.trim();
		}
		int id_prova = res.getInt("id_prova");
		Prova prova_ColetaQuestionario = null;
		if(id_prova != 0) {
			prova_ColetaQuestionario = _repositorio.consultarProva(id_prova);
		}
		ColetaQuestionario coletaQuestionario = new ColetaQuestionario(id_coletaquestionario, nome, prova_ColetaQuestionario);
		coletaQuestionario.setPersistent(true);
		return coletaQuestionario;
	}

	public ColetaQuestionario consultarColetaQuestionario(int id_coletaquestionario) throws SQLException {
		String sql = "select id_coletaquestionario, nome, id_prova from ColetaQuestionario where id_coletaquestionario = " + id_coletaquestionario;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		ColetaQuestionario coletaQuestionario = null;
		if(res.next()) {
			coletaQuestionario = construirColetaQuestionario(res);
		}
		st.close();
		return coletaQuestionario;
	}

	public Vector consultarColetaQuestionario() throws SQLException {
		String sql = "select id_coletaquestionario, nome, id_prova from ColetaQuestionario";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			ColetaQuestionario coletaQuestionario = construirColetaQuestionario(res);
			vec.add(coletaQuestionario);
		}
		st.close();
		return vec;
	}

	public Vector consultarColetaQuestionarioPorProva(Prova prova_ColetaQuestionario) throws SQLException {
		String id_provaStr = "is null";
		if(prova_ColetaQuestionario != null) {
			id_provaStr = "= " + prova_ColetaQuestionario.getId();
		}
		String sql = "select id_coletaquestionario, nome, id_prova from ColetaQuestionario where id_prova " + id_provaStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			ColetaQuestionario coletaQuestionario = construirColetaQuestionario(res);
			vec.add(coletaQuestionario);
		}
		st.close();
		return vec;
	}

	public void atualizarNomeEmColetaQuestionario(ColetaQuestionario coletaQuestionario, String nome) throws SQLException {
		String nomeStr = "= null";
		if(nome != null) {
			nomeStr = "= '" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String sql = "update ColetaQuestionario set nome " + nomeStr + " where id_coletaquestionario = " + coletaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarProva_ColetaQuestionarioEmColetaQuestionario(ColetaQuestionario coletaQuestionario, Prova prova_ColetaQuestionario) throws SQLException {
		String id_provaStr = "= null";
		if(prova_ColetaQuestionario != null) {
			id_provaStr = "= " + prova_ColetaQuestionario.getId();
		}
		String sql = "update ColetaQuestionario set id_prova " + id_provaStr + " where id_coletaquestionario = " + coletaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public ColetaQuestionario inserirColetaQuestionario(ColetaQuestionario obj) throws SQLException {
		ColetaQuestionario obj2 = inserirColetaQuestionario(obj.getNome(), obj.getProva_ColetaQuestionario());
		return obj2;
	}

	public ColetaQuestionario inserirColetaQuestionario(String nome, Prova prova_ColetaQuestionario) throws SQLException {
		String nomeStr = "null";
		if(nome != null) {
			nomeStr = "'" + (nome.length() > 128? nome.substring(0, 128): nome) + "'";
		}
		String id_provaStr = "null";
		if(prova_ColetaQuestionario != null) {
			id_provaStr = "" + prova_ColetaQuestionario.getId();
		}
		String sql = "insert into ColetaQuestionario(nome, id_prova) values (" + nomeStr + ", " + id_provaStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
        ColetaQuestionario coletaQuestionario = new ColetaQuestionario(id, nome, prova_ColetaQuestionario);
		coletaQuestionario.setPersistent(true);
		return coletaQuestionario;
	}

	public void removerColetaQuestionario(ColetaQuestionario obj) throws SQLException {
		removerColetaQuestionario(obj.getId());
	}

	public void removerColetaQuestionario(int id_coletaquestionario) throws SQLException {
		String sql = "delete from ColetaQuestionario where id_coletaquestionario = " + id_coletaquestionario;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}

	//////////////// entidade EntradaColetaQuestionario ////////////////

	private EntradaColetaQuestionario construirEntradaColetaQuestionario(ResultSet res) throws SQLException {
		int id_entradacoletaquestionario = res.getInt("id_entradacoletaquestionario");
		byte status = res.getByte("status");
		String foto = res.getString("foto");
		if(foto != null) {
			foto = foto.trim();
		}
		int threshold = res.getInt("threshold");
		int phase = res.getInt("phase");
		int idImageData = res.getInt("idImageData");
		int tipoFolhaResposta = res.getInt("tipoFolhaResposta");
		int idAnswersData = res.getInt("idAnswersData");
		int id_coletaquestionario = res.getInt("id_coletaquestionario");
		ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario = null;
		if(id_coletaquestionario != 0) {
			coletaQuestionario_EntradaColetaQuestionario = _repositorio.consultarColetaQuestionario(id_coletaquestionario);
		}
		EntradaColetaQuestionario entradaColetaQuestionario = new EntradaColetaQuestionario(id_entradacoletaquestionario, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, coletaQuestionario_EntradaColetaQuestionario);
		entradaColetaQuestionario.setPersistent(true);
		return entradaColetaQuestionario;
	}

	public EntradaColetaQuestionario consultarEntradaColetaQuestionario(int id_entradacoletaquestionario) throws SQLException {
		String sql = "select id_entradacoletaquestionario, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, id_coletaquestionario from EntradaColetaQuestionario where id_entradacoletaquestionario = " + id_entradacoletaquestionario;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		EntradaColetaQuestionario entradaColetaQuestionario = null;
		if(res.next()) {
			entradaColetaQuestionario = construirEntradaColetaQuestionario(res);
		}
		st.close();
		return entradaColetaQuestionario;
	}

	public Vector consultarEntradaColetaQuestionario() throws SQLException {
		String sql = "select id_entradacoletaquestionario, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, id_coletaquestionario from EntradaColetaQuestionario";
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			EntradaColetaQuestionario entradaColetaQuestionario = construirEntradaColetaQuestionario(res);
			vec.add(entradaColetaQuestionario);
		}
		st.close();
		return vec;
	}

	public Vector consultarEntradaColetaQuestionarioPorColetaQuestionario(ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		String id_coletaquestionarioStr = "is null";
		if(coletaQuestionario_EntradaColetaQuestionario != null) {
			id_coletaquestionarioStr = "= " + coletaQuestionario_EntradaColetaQuestionario.getId();
		}
		String sql = "select id_entradacoletaquestionario, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, id_coletaquestionario from EntradaColetaQuestionario where id_coletaquestionario " + id_coletaquestionarioStr;
		Connection c = getConnection();
		Statement st = c.createStatement();
		ResultSet res = st.executeQuery(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		Vector vec = new Vector();
		while(res.next()) {
			EntradaColetaQuestionario entradaColetaQuestionario = construirEntradaColetaQuestionario(res);
			vec.add(entradaColetaQuestionario);
		}
		st.close();
		return vec;
	}

	public void atualizarStatusEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, byte status) throws SQLException {
		String sql = "update EntradaColetaQuestionario set status = " + status + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarFotoEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, String foto) throws SQLException {
		String fotoStr = "= null";
		if(foto != null) {
			fotoStr = "= '" + (foto.length() > 128? foto.substring(0, 128): foto) + "'";
		}
		String sql = "update EntradaColetaQuestionario set foto " + fotoStr + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarThresholdEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int threshold) throws SQLException {
		String sql = "update EntradaColetaQuestionario set threshold = " + threshold + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarPhaseEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int phase) throws SQLException {
		String sql = "update EntradaColetaQuestionario set phase = " + phase + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarIdImageDataEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int idImageData) throws SQLException {
		String sql = "update EntradaColetaQuestionario set idImageData = " + idImageData + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarTipoFolhaRespostaEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int tipoFolhaResposta) throws SQLException {
		String sql = "update EntradaColetaQuestionario set tipoFolhaResposta = " + tipoFolhaResposta + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarIdAnswersDataEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, int idAnswersData) throws SQLException {
		String sql = "update EntradaColetaQuestionario set idAnswersData = " + idAnswersData + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public void atualizarColetaQuestionario_EntradaColetaQuestionarioEmEntradaColetaQuestionario(EntradaColetaQuestionario entradaColetaQuestionario, ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		String id_coletaquestionarioStr = "= null";
		if(coletaQuestionario_EntradaColetaQuestionario != null) {
			id_coletaquestionarioStr = "= " + coletaQuestionario_EntradaColetaQuestionario.getId();
		}
		String sql = "update EntradaColetaQuestionario set id_coletaquestionario " + id_coletaquestionarioStr + " where id_entradacoletaquestionario = " + entradaColetaQuestionario.getId();
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.close();
	}

	public EntradaColetaQuestionario inserirEntradaColetaQuestionario(EntradaColetaQuestionario obj) throws SQLException {
		EntradaColetaQuestionario obj2 = inserirEntradaColetaQuestionario(obj.getStatus(), obj.getFoto(), obj.getThreshold(), obj.getPhase(), obj.getIdImageData(), obj.getTipoFolhaResposta(), obj.getIdAnswersData(), obj.getColetaQuestionario_EntradaColetaQuestionario());
		return obj2;
	}

	public EntradaColetaQuestionario inserirEntradaColetaQuestionario(byte status, String foto, int threshold, int phase, int idImageData, int tipoFolhaResposta, int idAnswersData, ColetaQuestionario coletaQuestionario_EntradaColetaQuestionario) throws SQLException {
		String fotoStr = "null";
		if(foto != null) {
			fotoStr = "'" + (foto.length() > 128? foto.substring(0, 128): foto) + "'";
		}
		String id_coletaquestionarioStr = "null";
		if(coletaQuestionario_EntradaColetaQuestionario != null) {
			id_coletaquestionarioStr = "" + coletaQuestionario_EntradaColetaQuestionario.getId();
		}
		String sql = "insert into EntradaColetaQuestionario(status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, id_coletaquestionario) values (" + status + ", " + fotoStr + ", " + threshold + ", " + phase + ", " + idImageData + ", " + tipoFolhaResposta + ", " + idAnswersData + ", " + id_coletaquestionarioStr + ")";
		Connection c = getConnection();
		Statement st = c.createStatement();
		st.executeUpdate(sql,Statement.RETURN_GENERATED_KEYS);
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		
		// get just created id
		ResultSet rs = st.getGeneratedKeys();
        rs.next();
        int id = rs.getInt(1);
        rs.close();
		
		EntradaColetaQuestionario entradaColetaQuestionario = new EntradaColetaQuestionario(id, status, foto, threshold, phase, idImageData, tipoFolhaResposta, idAnswersData, coletaQuestionario_EntradaColetaQuestionario);
		entradaColetaQuestionario.setPersistent(true);
		return entradaColetaQuestionario;
	}

	public void removerEntradaColetaQuestionario(EntradaColetaQuestionario obj) throws SQLException {
		removerEntradaColetaQuestionario(obj.getId());
	}

	public void removerEntradaColetaQuestionario(int id_entradacoletaquestionario) throws SQLException {
		String sql = "delete from EntradaColetaQuestionario where id_entradacoletaquestionario = " + id_entradacoletaquestionario;
		Connection c = getConnection();
		Statement st = c.createStatement();
		if(_log.isEnabled()) {
			_log.writeWithTimestamp(sql);
		}
		st.executeUpdate(sql);
		st.close();
	}
}
