/*
 * quicktime.app: Sample Code for Initial Seeding
 *
 * © 1996, 97 Copyright, Apple Computer
 * All rights reserved
 */

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import quicktime.Errors;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.GraphicsImporterDrawer;
import quicktime.app.view.QTImageProducer;
import quicktime.io.QTFile;
import quicktime.qd.QDRect;
import quicktime.std.image.GraphicsImporter;

/*
	This sample code shows how to create a java.awt.Image out
	of some image that QuickTime produces.

	The source of the QuickTime image could come from any one of:
		(1) An image file in a format that Java doesn't directly support but QT does
		(2) Recording the drawing actions of a QDGraphics into a Pict -> this can be
		written out to a file or presented by an ImagePresenter class to the QTIMageProducer directly
		(3) Using the services of QuickTime's SequenceGrabbing component. A SequenceGrabber
		can be used to capture just an individual frame from a video source (the SGCapture shows basic
		usage of the SequenceGrabber and the QT documentation has more details on these components)

	In this code the user is prompted to open an image file (one of 20+ formats that QuickTime's
	GraphicsImporter can import)

	The program then uses the QTImageProducer to create a java.awt.Image which is then drawn
	in the paint method of the Frame
*/
public class QTtoJavaImage extends Frame {

	public static void main (String args[]) {
		try {
			QTSession.open ();
			QTtoJavaImage window = new QTtoJavaImage("QT in Java");
				// this will lay out and resize the Frame to the size of the selected movie
			window.pack();
			window.setVisible(true);
			window.toFront();
		} catch (QTException e) {
				// catch a userCanceledErr and just exit the program
			if (e.errorCode() == Errors.userCanceledErr) {
				QTSession.close();
				System.exit(0);
			}
				// some other error occured - print out a stack trace
				// and close the QTSession
			e.printStackTrace();
			QTSession.close();
		}
	}

	QTtoJavaImage (String title) throws QTException {
		super (title);

			// prompt the user to select an image file
		QTFile imageFile = QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);

			// import the image into QuickTime
		GraphicsImporter myGraphicsImporter = new GraphicsImporter (imageFile);

			//Create a GraphicsImporterDrawer which uses the GraphicsImporter to draw
			//this object produces pixels for the QTImageProducer
		GraphicsImporterDrawer myDrawer = new GraphicsImporterDrawer (myGraphicsImporter);

			//Create a java.awt.Image from the pixels supplied to it by the QTImageProducer
		QDRect r = myDrawer.getDisplayBounds();
			// this is the size of the image - this will become the size of the frame
		imageSize = new Dimension (r.getWidth(), r.getHeight());
		QTImageProducer qtProducer = new QTImageProducer (myDrawer, imageSize);
		javaImage = Toolkit.getDefaultToolkit().createImage(qtProducer);

			// add a Window Listener to this frame
			// that will close down the QTSession, dispose of the Frame
			// which will close the window - where we exit
		addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {
				QTSession.close();
				dispose();
			}

			public void windowClosed (WindowEvent e) {
				System.exit(0);
			}
		});
	}

	Image javaImage = null;
	Dimension imageSize;

	public void paint (Graphics g) {
		Insets i = getInsets();
		Dimension d = getSize();
		int width = d.width - i.left - i.right;
		int height = d.height - i.top - i.bottom;
			//make sure image is scaled correctly to fill the entire visible area of the frame
		g.drawImage (javaImage, i.left, i.top, width, height, this);
	}

		//this returns the size of the image - so the pack will correctly resize the frame
	public Dimension getPreferredSize () {
		return imageSize;
	}
}





