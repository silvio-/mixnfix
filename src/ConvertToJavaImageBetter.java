import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.GraphicsImporterDrawer;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.app.view.QTImageProducer;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.image.GraphicsImporter;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.media.DataRef;

public class ConvertToJavaImageBetter
    extends Frame implements ActionListener {

    Movie movie;
    MovieController controller;
    QTComponent qtc;
    GraphicsImporter gi;
    GraphicsImporterDrawer gid;
    static int nextFrameX, nextFrameY;

    public static void main(String[] arrImAPirate) {
        ConvertToJavaImageBetter ctji =
            new ConvertToJavaImageBetter();
        ctji.pack();
        ctji.setVisible(true);
        Rectangle ctjiBounds = ctji.getBounds();
        nextFrameX = ctjiBounds.x + ctjiBounds.width;
        nextFrameY = ctjiBounds.y + ctjiBounds.height;
    }

    public ConvertToJavaImageBetter() {
        super("QuickTime Movie");
        try {
// get movie
            QTSession.open();
            QTFile file =
                QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);
            OpenMovieFile omFile = OpenMovieFile.asRead(file);
            movie = Movie.fromFile(omFile);
            controller = new MovieController(movie);
// build gui
            qtc = QTFactory.makeQTComponent(controller);
            Component c = qtc.asComponent();
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
            Button imageButton = new Button("Make Java Image");
            add(imageButton, BorderLayout.SOUTH);
            imageButton.addActionListener(this);
// set up graphicsimporter
            gi = new GraphicsImporter(StdQTConstants.kQTFileTypePicture);
            gid = new GraphicsImporterDrawer(gi);
// set up close-to-quit
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    System.exit(0);
                }
            });
        }
        catch (QTException qte) {
            qte.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent e) {
        grabMovieImage();
    }

    public void grabMovieImage() {
        try {
// stop movie to take picture
            boolean wasPlaying = false;
            if (movie.getRate() > 0) {
                movie.stop();
                wasPlaying = true;
            }

// take a pict
            Pict pict = movie.getPict(movie.getTime());

// add 512-byte header that pict would have as file
            byte[] newPictBytes =
                new byte[pict.getSize() + 512];
            pict.copyToArray(0,
                             newPictBytes,
                             512,
                             newPictBytes.length - 512);
            pict = new Pict(newPictBytes);

// export it
            DataRef ref = new DataRef(pict,
                                      StdQTConstants.kDataRefQTFileTypeTag,
                                      "PICT");
            gi.setDataReference(ref);
            QDRect rect = gi.getSourceRect();
            Dimension dim = new Dimension(rect.getWidth(),
                                          rect.getHeight());
            QTImageProducer ip = new QTImageProducer(gid, dim);

// convert to java.awt.Image
            Image image = Toolkit.getDefaultToolkit().createImage(ip);

// make a swing icon out of it and show it in a frame
            ImageIcon icon = new ImageIcon(image);
            JLabel label = new JLabel(icon);
            JFrame frame = new JFrame("Java image");
            frame.getContentPane().add(label);
            frame.pack();
            frame.setLocation(nextFrameX += 10,
                              nextFrameY += 10);
            frame.setVisible(true);

// restart movie
            if (wasPlaying)
                movie.start();
        }
        catch (QTException qte) {
            qte.printStackTrace();
        }
    }

}
