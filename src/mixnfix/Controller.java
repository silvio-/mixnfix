package mixnfix;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Controller {
    public Controller() {
    }

    // tmp directory: where intermediate files are written
    public static String TMP_DIR = System.getProperty("java.io.tmpdir");
    static {
    	if (TMP_DIR!=null && TMP_DIR.charAt(TMP_DIR.length()-1) != '/')
    		TMP_DIR += "/";
    }

    // é pra gerar o gabarito nota dez
    private static final boolean GABARITO_NOTA_DEZ = true;

    private static void executeCommand(String cmd) throws Exception {
        System.out.println(cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        SG out = new SG(proc.getInputStream(), "out> ");
        SG err = new SG(proc.getErrorStream(), "err> ");
        out.start();
        err.start();
        int r = proc.waitFor();
        System.out.println("exitValue was " + r);
    }

    /**
     * UNZIP Algorithm
     */
    private static final int BUFFER = 2048;
    public static void unzip(String fileName, String outDir) throws IOException {
        BufferedOutputStream dest = null;
        FileInputStream fis = new FileInputStream(fileName);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
        ZipEntry entry;

        // assure output directory
        File outDirFile = new File(outDir);
        if (!outDirFile.exists())
            outDirFile.mkdirs();

        while ( (entry = zis.getNextEntry()) != null) {
            System.out.println("Extracting: " + entry);
            int count;
            byte data[] = new byte[BUFFER];

            // write the files to the disk
            FileOutputStream fos = new FileOutputStream(outDirFile.getAbsolutePath() + "/" + entry.getName());
            dest = new BufferedOutputStream(fos, BUFFER);
            while ( (count = zis.read(data, 0, BUFFER)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        zis.close();
    }

    /**
     * UNZIP Algorithm
     */
    public static void zipDirectory(String dir, String outFile) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
        byte buf[] = new byte[BUFFER];
        File fdir = new File(dir);
        File[] files = fdir.listFiles();
        for (File f : files) {
            out.putNextEntry(new ZipEntry(f.getName()));
            FileInputStream in = new FileInputStream(f);
            // Transfer bytes from the file to the ZIP file
            int len;
            while ( (len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        out.close();
    }

    /**
     * UNZIP Algorithm
     */
    public static void zipFiles(File directory, java.util.List<String> names, String outFile) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFile));
        byte buf[] = new byte[BUFFER];
        for (String n : names) {
            File f = new File(directory.getAbsolutePath() + "/" + n);
            System.out.println("zipping: " + f.getName());
            out.putNextEntry(new ZipEntry(f.getName()));
            FileInputStream in = new FileInputStream(f);
            // Transfer bytes from the file to the ZIP file
            int len;
            while ( (len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Complete the entry
            out.closeEntry();
            in.close();
        }
        out.close();
    }
}
