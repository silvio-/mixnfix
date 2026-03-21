import java.io.File;
import java.util.ArrayList;

import mixnfix.MFI2Java;
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

public class VideoCapture {

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
            0,1,0,1   // search region margins
        );

        // set cellmap
        // MFI2Java.setCellMap("c:/workspace/mnfimg/c/x.txt");

        // makeFrames("c:/pub/videos/movie.avi",1,"x","c:/temp/avi");
        makeFrames("c:/pub/mixnfix/covest.mpg",100,"x","c:/temp/avi");
    }

    static byte _data[] = new byte[10000000];
    static float _cells[] = new float[5000];
    static float _controlPoints[] = new float[500];

    static public ArrayList makeFrames(String movieFilename, int numFrames, String prepend, String directory) {

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
}
