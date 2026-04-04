package mixnfix;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Map;

public class Library {

    // QuickTime for Java was discontinued by Apple in 2009 and is not
    // available on Linux. The video-frame-extraction feature is stubbed out.
    // If you need it, use ffmpeg externally:
    //   ffmpeg -i input.mpg -vf fps=1 outdir/prefix%d.jpg

    public static ArrayList makeFrames(String movieFilename, int numFrames, String prepend, String directory) {
        throw new UnsupportedOperationException(
            "Video frame extraction requires QuickTime for Java, which is not available on this platform. " +
            "Use ffmpeg to extract frames manually, then import the jpg files.");
    }

    public static ArrayList extractFrames(String movieFilename, int numFrames, String prepend, String directory) {
        throw new UnsupportedOperationException(
            "Video frame extraction requires QuickTime for Java, which is not available on this platform. " +
            "Use ffmpeg to extract frames manually, then import the jpg files.");
    }

    /**
     * Copies src file to dst file.
     * If the dst file does not exist, it is created
     *
     * @param src File
     * @param dst File
     * @throws IOException
     */
    public static void copyFile(File src, File dst) throws IOException {
        dst.getParentFile().mkdirs(); // create directory if it doesn't exist
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[2048];
        int len;
        while ( (len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public static int executeCommand(String cmd,boolean wait) throws Exception {
        System.out.println(cmd);
        Process proc = Runtime.getRuntime().exec(cmd);
        SG out = new SG(proc.getInputStream(),"out> ");
        SG err = new SG(proc.getErrorStream(),"err> ");
        out.start();
        err.start();
        if (wait)
            return proc.waitFor();
        else
            return 0;
        // System.out.println("exitValue was " + r);
    }

    public static int executeCommand(String cmd, String dir, String[] envp, boolean wait) throws Exception {
        System.out.println(cmd);
        String tt[] = cmd.split("[ ]+");
        ProcessBuilder pb = new ProcessBuilder(tt);
        Map<String, String> env = pb.environment();
        if (envp != null) {
        	for (String pair: envp) {
        		String[] tokens = pair.split("=");
        		env.put(tokens[0], tokens[1]);
        	}
        }
        if (dir != null)
        	pb.directory(new File(dir.replace("\\","/").replace(" ","\\ ")));
        Process proc = pb.start();
        SG out = new SG(proc.getInputStream(),"out> ");
        SG err = new SG(proc.getErrorStream(),"err> ");
        out.start();
        err.start();
        if (wait)
            return proc.waitFor();
        else
            return 0;
    }

    public static String fillRight(String s, int size, char ch) {
       String result = s;
       for (int i=0;i<size-s.length();i++) {
           result+=ch;
       }
       if (result.length() > size)
           result = result.substring(0,size);
       return result;
   }

   public static String fillLeft(String s, int size, char ch) {
       String result = "";
       for (int i=0;i<size-s.length();i++) {
           result+=ch;
       }
       result += s;
       if (result.length() > size)
           result = result.substring(0,size);
       return result;
    }

    public static boolean isLocked(File file) {
        if (!file.exists())
            return false;

        try {
            // Get a file channel for the file
            FileChannel channel = new RandomAccessFile(file, "rw").getChannel();

            // Use the file channel to create a lock on the file.
            // This method blocks until it can retrieve the lock.
            FileLock lock = channel.lock();

            // Release the lock
            lock.release();

            // Close the file
            channel.close();

        } catch (Exception e) {
            return true;
        }
        return false;
    }


    /**
     * comma and point symbols for the fractional part are allowed
     */
    public static float parseFloat(String st) {
        try {
            return Float.parseFloat(st);
        }
        catch (Exception x) {
            try {
                return (float)linsoft.gui.util.Library.parseCommaSeparatedDecimal(st);
            }
            catch (Exception xx) {
                throw new RuntimeException("parseFloat problem");
            }
        }
    }

}
