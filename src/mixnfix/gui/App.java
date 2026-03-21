package mixnfix.gui;

import java.sql.Connection;
import java.sql.SQLException;

import mixnfix.extRepositorio.ExtensaoRepositorio;
import mixnfix.modelo.Repositorio;
import mixnfix.modelo.RepositorioLink;
import mixnfix.modelo.persistencia.RepositorioBD;
import mixnfix.modelo.persistencia.RepositorioCache;
//import linsoft.gui.table.*;

public class App  {

    ///////////////////////////////////
    // INICIO: Configuracao
    private static final ConfiguracaoMIXnFIX _config = new ConfiguracaoMIXnFIX("mixnfix");

    public static ConfiguracaoMIXnFIX getConfiguracao() {
        return App._config;
    }

    public static String getProperty(String name) {
        return _config.getProperty(name);
    }

    public static void setProperty(String name, String value) {
        _config.setProperty(name, value);
    }

    public static String getProperty(String nodeName, String name) {
        return _config.getProperty(nodeName, name);
    }

    public static void setProperty(String nodeName, String name, String value) {
        _config.setProperty(nodeName, name, value);
    }
    // FIM: Configuracao
    ///////////////////////////////////

    //////////////////////////////////////
    // INICIO: Program name and version
    public static final String NAME = "MIXnFIX";
    public static final String VERSION = "v1b";
    public static final String DATE = "02/01/2008";

    public static String getVersion() { return VERSION; }
    public static String getName() { return NAME; }
    public static String getDate() { return DATE; }

    // FIM: Program name and version
    //////////////////////////////////////

    //////////////////////////////////////
    // INICIO: Repositorio
    private static Repositorio      _repositorio;
    private static RepositorioBD    _repositorioBD;
    private static RepositorioCache _repositorioCache;

    public static Repositorio getRepositorio() {
        if (_repositorio == null) {
            try {
                // inicialização do repositório
                _repositorioCache  = new RepositorioCache();
                _repositorioBD = new RepositorioBD();
                _repositorioCache.setRepositorio(_repositorioBD);
                _repositorioBD.setRepositorio(_repositorioCache);
                RepositorioLink.init(_repositorioCache);
                _repositorio = _repositorioCache;
            }
            catch (Exception ex) {
                return null;
            }
        }
        return _repositorio;
    }

    public static RepositorioBD getRepositorioBD() {
        if (_repositorioBD == null)
            App.getRepositorio();
        return _repositorioBD;
    }

    public static void createFreshCacheRepository() {
        setCacheRepository(new RepositorioCache());
    }

    public static void setCacheRepository(RepositorioCache c) {
        _repositorioCache = c;
        _repositorioCache.setRepositorio(_repositorioBD);
        _repositorioBD.setRepositorio(_repositorioCache);
        RepositorioLink.init(_repositorioCache);
        _repositorio = _repositorioCache;
    }

    public static Connection getConnection() throws SQLException {
        return _repositorioBD.getConnection();
    }

    /**
     * Obter a extensão de repositorio persistente
     */
    public static ExtensaoRepositorio getExtensaoRepositorio() {
        return ExtensaoRepositorio.getInstance();
    }

    // FIM: Repositorio
    //////////////////////////////////////



    static {

        /*
        // instalação do formatador para as grades
        linsoft.gui.table.CentralDeFormatadorDeTexto.setFormatadorDeTexto(new FormatText());

        FornecedorCTCR f = new FornecedorCTCRPadrao() {
            public ConfigurableCellRendererDefault getCTCR(Class objectClass,
                String rendererKey) {
                if (rendererKey != null && rendererKey.equals("resumoPaj")) {
                    return new CRCTResumoPajs();
                }
                else if (rendererKey != null && rendererKey.equals("pedidosEm2Linhas")) {
                    return new CTCRPedidos();
                }
                else if (rendererKey != null && rendererKey.equals("multilines")) {
                    return new CTCRMultiLines();
                }
                else
                    return super.getCTCR(objectClass, rendererKey);
            }
        };
        CentralFCTCR.setFornecedorCTCR(f);
        */

        ///////////////////////////////////////////////////////
        // inicializaçao do driver
//        try {
//            System.out.println("Registering database connection");
//            new JDCConnectionDriver(
//                "com.mysql.jdbc.Driver",
//                "jdbc:mysql://localhost/"+DB_NAME,
//                "root",
//                ""
//                );
//        }
//        catch (SQLException ex) {
//            ex.printStackTrace();
//        }
//        catch (IllegalAccessException ex) {
//            ex.printStackTrace();
//        }
//        catch (InstantiationException ex) {
//            ex.printStackTrace();
//        }
//        catch (ClassNotFoundException ex) {
//            ex.printStackTrace();
//        }

        // log on default output
        linsoft.log.Log log = new linsoft.log.Log("Log de Tudo",System.out);
        App.getRepositorioBD().setLog(log);
        App.getExtensaoRepositorio().setLog(log);

    }

    /*
    private static OpManager _opManager = new OpManager();

    public static OpManager getOpManager() {
        return _opManager;
    }
    */

}
