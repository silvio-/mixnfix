package mixnfix;

import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

public class I {
    // rotations
    public static final byte ROTATE_0 = (byte) 0;
    public static final byte ROTATE_90 = (byte) 1;
    public static final byte ROTATE_180 = (byte) 2;
    public static final byte ROTATE_270 = (byte) 3;

    // state
    private byte _rotation;
    private int _data[][];
    private int _w; // width
    private int _h; // height

    //
	public I(String imgFileName, byte rotation) throws IOException {
        _rotation = rotation;

		BufferedImage img = ImageIO.read(new File(imgFileName));

        int aw = img.getWidth(); // actual width
        int ah = img.getHeight(); // actual height

        // data width and height
        _w = ((_rotation == ROTATE_0 || _rotation == ROTATE_180) ? aw : ah); // rotated width
        _h = ((_rotation == ROTATE_0 || _rotation == ROTATE_180) ? ah : aw); // rotated height

        // [ a b x ]   [ j ]
        // [ c d y ] * [ i ]
        // [ 0 0 1 ]   [ 1 ]
        int a,b,c,d,x,y;
        switch (_rotation) {
            case ROTATE_0:
                a=1; b=0; c=0; d=1; x=0; y=0;
                break;
            case ROTATE_90:
                a=0; b=-1; c=1; d=0; x=ah-1; y=0;
                break;
            case ROTATE_180:
                a=-1; b=0; c=0; d=-1; x=aw-1; y=ah-1;
                break;
            case ROTATE_270:
                a=0; b=1; c=-1; d=0; x=0; y=aw-1;
                break;
            default:
                a=1; b=0; c=0; d=1; x=0; y=0;
                break;
        }

        // data vector
        _data = new int[_h][_w];

        // read pixels
        Raster raster = img.getRaster();
        int pixel[] = new int[4];
        int imgType = img.getType();

        for (int i=0;i<ah;i++)
            for (int j=0;j<aw;j++) {
                raster.getPixel(j, i, pixel);

                int jj = a * j + b * i + x;
                int ii = c * j + d * i + y;

                if (imgType == BufferedImage.TYPE_INT_RGB) {
                    _data[ii][jj] = (pixel[0] + pixel[1] + pixel[2]) / 3;
                }
                else if (imgType == BufferedImage.TYPE_BYTE_GRAY) {
                    _data[ii][jj] = pixel[0];
                }
            }
	}

    // save file
    public void saveFile(String fileName) throws Exception {
        this.saveImage(_data,_w,_h,fileName);
    }

    private void saveImage(int a[][],int w, int h, String fileName) throws Exception {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int number = cs.getNumComponents();
        // System.out.println("Number of Components "+number);
        int[] nBits = {8};
        ColorModel colorModel = new ComponentColorModel(cs, nBits,false,false,Transparency.OPAQUE,DataBuffer.TYPE_BYTE);
        SampleModel sampleModel = colorModel.createCompatibleSampleModel(_w,_h);
        DataBuffer dataBuffer = sampleModel.createDataBuffer();
        for (int i=0;i<_h;i++) {
            for (int j = 0; j < _w; j++) {
                dataBuffer.setElem(i*_w+j,a[i][j]);
            }
        }
        WritableRaster wr = Raster.createWritableRaster(sampleModel,dataBuffer, new Point(0,0));
        BufferedImage bf = new BufferedImage(colorModel, wr, true, null);


        Iterator writers = ImageIO.getImageWritersByFormatName("jpg");
        ImageWriter writer = (ImageWriter)writers.next();
        File f = new File(fileName);
        ImageOutputStream ios = ImageIO.createImageOutputStream(f);
        writer.setOutput(ios);

        // Set the compression quality
        ImageWriteParam iwparam = new MyImageWriteParam();
        iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwparam.setCompressionQuality(1);
        writer.write(null, new IIOImage(bf, null, null), iwparam);

        /*
        try {
            ImageIO.write(bf,"bmp",new File(fileName));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }

    public static void main(String argv[]) throws Exception {
        System.out.println("Gray and Rotate in Java");
        if (argv.length != 1) {
            System.out.println("No Arguments");
            return;
        }

        String fileName = argv[0];
        I i000 = new I(fileName,I.ROTATE_0);
        I i090 = new I(fileName,I.ROTATE_90);
        I i180 = new I(fileName,I.ROTATE_180);
        I i270 = new I(fileName,I.ROTATE_270);

        i000.saveFile(fileName+".000.jpg");
        i090.saveFile(fileName+".090.jpg");
        i180.saveFile(fileName+".180.jpg");
        i270.saveFile(fileName+".270.jpg");

    }

}


// This class overrides the setCompressionQuality() method to workaround
// a problem in compressing JPEG images using the javax.imageio package.
class MyImageWriteParam extends JPEGImageWriteParam {
    public MyImageWriteParam() {
        super(Locale.getDefault());
    }

    // This method accepts quality levels between 0 (lowest) and 1 (highest) and simply converts
    // it to a range between 0 and 256; this is not a correct conversion algorithm.
    // However, a proper alternative is a lot more complicated.
    // This should do until the bug is fixed.
    /*
    public void setCompressionQuality(float quality) {
        if (quality < 0.0F || quality > 1.0F) {
            throw new IllegalArgumentException("Quality out-of-bounds!");
        }
        this.compressionQuality = 256 - (quality * 256);
    } */
}
