package mixnfix.bin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

import mixnfix.modelo.Repositorio;
import mixnfix.modelo.TesteMixnfix;

public class Install {
    public static final String DATABASE_NAME = "mixnfix";
    public static final String MYSQL_PATH = "mysql -u root "; //"\"c:\\mysql\\bin\\mysql\"";

    public static void main(String argv[]) throws Exception {

        // add options to optionsSet
        HashSet optionsSet = new HashSet();
        for (int i=0;i<argv.length;i++) {
            optionsSet.add(argv[i]);
        }

        //
        System.out.println("MIXnFIX - Instalar Banco de Dados/Limpar Registros - (18/11/2004)");
        System.out.println("Opcoes:");
        System.out.println("   -d  remover e recriar banco de dados de nome \""+DATABASE_NAME+"\"");
        System.out.println("   -r  remover informacoes do registro");

        // limpar registro
        if (optionsSet.contains("-r") || optionsSet.contains("-R"))
            registro();

        // criar database
        if (optionsSet.contains("-d") || optionsSet.contains("-D"))
            database();


        // esperar 5 segundos
        System.in.read();


        //
        System.exit(0);
    }

    private static void database() throws Exception {

        // create temp file with install database script
        File deleteScriptFile = criarArquivoComText("deleteScript.sql", "drop database " + DATABASE_NAME + ";");
        File createScriptFile = criarArquivoComText("createScript.sql", "create database " + DATABASE_NAME + ";");
        File deleteBatchFile = criarArquivoComText("createScript.bat", MYSQL_PATH + " < " + deleteScriptFile.getName());
        File createBatchFile = criarArquivoComText("deleteScript.bat", MYSQL_PATH + " < " + createScriptFile.getName());

        // esperar 5 segundos
        System.out.println("removendo base antiga...");

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("sh "+deleteBatchFile.getAbsolutePath());
            process.waitFor();
        }
        catch (IOException ex) {
        	ex.printStackTrace();
            System.out.println("base ainda nao existe (ou problema na remocao)");
        }

        System.out.println("criando base nova...");

        process = Runtime.getRuntime().exec("sh "+createBatchFile.getAbsolutePath());
        process.waitFor();

        // erase file
        deleteScriptFile.delete();
        createScriptFile.delete();
        deleteBatchFile.delete();
        createBatchFile.delete();

        // init configuracao
        // App.getConfiguracao();

        // esperar
        System.out.println("criando tabelas...");

        // criar tabelas
        TesteMixnfix t = new TesteMixnfix();
        t.inicializar();
        t.criarTabelas();

        // esperar
        System.out.println("populando base...");

        // popular tabelas
        // popularTabelas(Modelo.getInstance().getRepositorio());

        // esperar 5 segundos
        System.out.println("banco de dados instalado com sucesso");

    }

    /**
     * Remover informacao nos registros para na prï¿½xima
     * inicializacao do programa os defaults voltarem.
     */
    private static void registro() throws Exception {
        System.out.println("limpando registros do MIXnFIX...");
        // Preferencias p = new Preferencias();
        // p.clearPreferences();
        System.out.println("registro limpo com sucesso");
    }

    public static File criarArquivoComText(String fileName, String fileText) throws IOException {
        File f = new File(fileName);
        f.createNewFile();
        FileWriter writer = new FileWriter(f, false);
        writer.write(fileText);
        writer.flush();
        writer.close();
        return f;
    }

    public static void popularTabelas(Repositorio r) throws SQLException {
    }

}
