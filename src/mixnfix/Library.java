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

    /**
     * Splits a command line into its arguments, honouring double quotes and
     * blanks escaped with a backslash (the way the command templates of the
     * settings panel, see ConfiguracaoMIXnFIX.getCommandCompileTEX2PDF(),
     * quote directory and file names).
     */
    public static String[] splitCommandLine(String cmd) {
        ArrayList<String> args = new ArrayList<String>();
        StringBuffer current = new StringBuffer();
        boolean quoted = false;
        boolean has = false;
        for (int i = 0; i < cmd.length(); i++) {
            char c = cmd.charAt(i);
            if (c == '\\' && i + 1 < cmd.length() && cmd.charAt(i + 1) == ' ') {
                current.append(' ');
                has = true;
                i++;
            }
            else if (c == '"') {
                quoted = !quoted;
                has = true;
            }
            else if ((c == ' ' || c == '\t' || c == '\n') && !quoted) {
                if (has) {
                    args.add(current.toString());
                    current.setLength(0);
                    has = false;
                }
            }
            else {
                current.append(c);
                has = true;
            }
        }
        if (has)
            args.add(current.toString());
        return args.toArray(new String[args.size()]);
    }

    /**
     * Executes a command given as an already split argument list (so arguments
     * containing blanks - directory names, file names - are passed untouched),
     * optionally inside a working directory and with extra environment
     * variables ("NAME=value").
     */
    public static int executeCommand(String[] cmd, String dir, String[] envp, boolean wait) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (String s: cmd) sb.append(s).append(' ');
        System.out.println((dir != null? "["+dir+"] ": "")+sb.toString());
        ProcessBuilder pb = new ProcessBuilder(cmd);
        if (envp != null) {
            Map<String, String> env = pb.environment();
            for (String pair: envp) {
                int eq = pair.indexOf('=');
                if (eq > 0)
                    env.put(pair.substring(0, eq), pair.substring(eq + 1));
            }
        }
        if (dir != null)
            pb.directory(new File(dir));
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
