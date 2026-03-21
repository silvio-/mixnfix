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

import quicktime.QTSession;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.std.StdQTConstants;
import quicktime.std.image.GraphicsExporter;
import quicktime.std.movies.Movie;
import quicktime.util.RawEncodedImage;

public class Library {

    public static void main(String[] args) {
        MFI2Java.setConstraints(
            new int[] {50, 40, 60, 70, 80}, //thresholds
            3,       // minPixelWidth
            12,      // maxPixelWidth
            3,       // minPixelHeight
            12,      // maxPixelHeight
            14,      // minNumPixels
            120,     // maxNumPixels
            0.4f,    // pixelDensity
            2,       // candidates closest points
            150,     // minSide
            4,       // difAngle
            30,      // targetRadius
            1.222f,    // correctSideRatio
            0.2f,       // sideRatioTolerance
            3,       // phase
            15,       // controlPointsRadius
            0,1,0,1
        );

        // set cellmap
        // MFI2Java.setCellMap("c:/workspace/mnfimg/c/x.txt");

        // makeFrames("c:/pub/videos/movie.avi",1,"x","c:/temp/avi");
        makeFrames("c:/pub/mixnfix/covest.mpg",100,"x","c:/temp/avi");
    }

    static byte _data[] = new byte[10000000];
    static float _cells[] = new float[5000];
    static float _controlPoints[] = new float[500];

    public static ArrayList makeFrames(String movieFilename, int numFrames, String prepend, String directory) {

        ArrayList resultFiles = new ArrayList();

        try {

            QTSession.open();

            QTFile qtFile = new QTFile(movieFilename);

            OpenMovieFile movieFile = OpenMovieFile.asRead(qtFile);

            Movie movie = Movie.fromFile(movieFile);

            int duration = movie.getDuration() - movie.getTime();

            int j = 0;

            int step = (duration + numFrames * 2) / numFrames;

            step = (int) (step * .95);

            Pict pict = null;

            QTFile f = null;

            for (int i = movie.getTime() + (int) (duration * .05); i < movie.getDuration(); i += step) {

                j++;

                pict = movie.getPict(i); // frame number


                // if (j == 1) {
                    // set up graphicsimporter

                    //
                    int width = pict.getPictFrame().getWidth();
                    int height = pict.getPictFrame().getHeight();
                    QDGraphics gw = new QDGraphics(pict.getPictFrame());
                    pict.draw(gw, pict.getPictFrame());
                    PixMap pixMap = gw.getPixMap();
                    RawEncodedImage rawImage = pixMap.getPixelData();
                    int rowInts = pixMap.getRowBytes()/4;
                    int[] pixels = new int[rowInts*height];
                    rawImage.copyToArray(0,pixels,0,pixels.length);


                    // save to _data array
                    int kk = 0;
                    for (int ii=0;ii<height;ii++) {
                        for (int jj=0;jj<width;jj++) {
                            int rgb = pixels[kk];
                            int gray = (30*(rgb & 0xFF) + 59*((rgb >> 8) & 0xFF) + 11*((rgb >> 16) & 0xFF)) / 100;
                            _data[kk] = (byte)gray;
                            kk++;
                        }
                    }

                    // boolean ok = MFI2Java.process(_data, width, height, _cells, _controlPoints);
                    boolean ok = false;

                // save file
                if (ok) {

                    System.out.println("size: " + pict.getSize() + ", i: " + i);

                    System.out.println(pict.getPictFrame());

                    String fullFileName = directory + File.separator + prepend + j + ".jpeg";

                    f = new QTFile(fullFileName);

                    resultFiles.add(f.getName());

                    GraphicsExporter graphicsExporter = new GraphicsExporter(StdQTConstants.kQTFileTypeJPEG);

                    graphicsExporter.setCompressionQuality(StdQTConstants.codecNormalQuality);

                    graphicsExporter.setOutputFile(f);

                    graphicsExporter.setInputPicture(pict);

                    graphicsExporter.doExport();
                }
            }

            QTSession.close();

        }
        catch (Exception e) {

            throw (new RuntimeException(e));

        }
        finally {

            QTSession.close();

        }

        return resultFiles;

    }

    public static ArrayList extractFrames(String movieFilename, int numFrames, String prepend, String directory) {

        ArrayList resultFiles = new ArrayList();

        long t0 = System.currentTimeMillis();

        try {

            QTSession.open();

            QTFile qtFile = new QTFile(movieFilename);

            OpenMovieFile movieFile = OpenMovieFile.asRead(qtFile);

            Movie movie = Movie.fromFile(movieFile);

            int duration = movie.getDuration() - movie.getTime();

            Pict pict = null;

            QTFile f = null;

            for (int i = 0; i < numFrames; i++) {

                int frameIndex = movie.getTime() + (int) Math.round((i  * (double)duration) / (double)(numFrames - 1));

                System.out.println(i+"-th frame is at "+frameIndex);

                // System.out.println("frame: " + i);

                pict = movie.getPict(frameIndex); // frame number

                // System.out.println(pict.getPictFrame());

                String fullFileName = directory + File.separator + prepend + i + ".jpg";

                f = new QTFile(fullFileName);

                resultFiles.add(f.getName());

                GraphicsExporter graphicsExporter = new GraphicsExporter(StdQTConstants.kQTFileTypeJPEG);

                graphicsExporter.setCompressionQuality(StdQTConstants.codecNormalQuality);

                graphicsExporter.setOutputFile(f);

                graphicsExporter.setInputPicture(pict);

                graphicsExporter.doExport();
            }


            System.out.println("movie.getTime(): "+movie.getTime());
            System.out.println("movie.getDuration(): "+movie.getDuration());
            System.out.print("Extracted "+numFrames+" in "+ (System.currentTimeMillis()-t0));

            QTSession.close();


        }
        catch (Exception e) {

            throw (new RuntimeException(e));

        }
        finally {

            QTSession.close();

        }


        return resultFiles;

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
