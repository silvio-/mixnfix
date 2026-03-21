/*
 * QuickTime for Java SDK Sample Code

   Usage subject to restrictions in SDK License Agreement
 * Copyright: © 1996-1999 Apple Computer, Inc.

 */
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import quicktime.Errors;
import quicktime.QTException;
import quicktime.QTRuntimeException;
import quicktime.QTRuntimeHandler;
import quicktime.QTSession;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.StdQTConstants;
import quicktime.std.movies.ActionFilter;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;
import quicktime.std.movies.MovieDrawingComplete;
import quicktime.std.movies.Track;
import quicktime.util.StringHandle;
import quicktime.vr.QTVRConstants;
import quicktime.vr.QTVREnteringNode;
import quicktime.vr.QTVRInstance;
import quicktime.vr.QTVRInterceptRecord;
import quicktime.vr.QTVRInterceptor;
import quicktime.vr.QTVRLeavingNode;
import quicktime.vr.QTVRMouseOverHotSpot;

public class MovieCallbacks extends Frame implements Errors {

	public static void main (String args[]) {
		try {
			System.out.println ("Open a Multi-node VR Movie...");
			QTSession.open (QTSession.kInitVR);
				//register handler for QTRuntimeExceptions
			QTRuntimeException.registerHandler (new Handler());

			MovieCallbacks pm = new MovieCallbacks("QT in Java");
			pm.pack();
			pm.setVisible(true);
			pm.toFront();
		} catch (QTException e) {
			if (e.errorCode() == userCanceledErr) {
				QTSession.close();
				System.exit(0);
			}
			e.printStackTrace();
			QTSession.close();
		}
	}

	QTVRInstance myQTVRInstance;
	Movie myMovie;
	MovieController myMovieController;

	MovieCallbacks (String title) throws QTException {
		super (title);

		QTFile qtFile = QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);

		OpenMovieFile movieFile = OpenMovieFile.asRead(qtFile);
		myMovie = Movie.fromFile (movieFile);
		myMovieController = new MovieController (myMovie);
		myMovieController.setKeysEnabled (true);

		Track track = myMovie.getQTVRTrack (1);
		if (track != null) {	//setup VR callbacks
			myQTVRInstance = new QTVRInstance (track, myMovieController);
			myQTVRInstance.setEnteringNodeProc (new EnteringNode(), 0);
			myQTVRInstance.setLeavingNodeProc (new LeavingNode(), 0);
			myQTVRInstance.setMouseOverHotSpotProc (new HotSpot(), 0);
			Interceptor ip = new Interceptor();
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRSetPanAngleSelector, ip, 0);
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRSetTiltAngleSelector, ip, 0);
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRSetFieldOfViewSelector, ip, 0);
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRSetViewCenterSelector, ip, 0);
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRTriggerHotSpotSelector, ip, 0);
			myQTVRInstance.installInterceptProc (QTVRConstants.kQTVRGetHotSpotTypeSelector, ip, 0);
		}

		// set up movie drawing callback
		myMovie.setDrawingCompleteProc (StdQTConstants.movieDrawingCallWhenChanged, new MovieDrawing());
		// set up action filter
		myMovieController.setActionFilter (new PMFilter(), false);	//don't do idle events

                QTComponent mComponent = QTFactory.makeQTComponent(myMovieController);
                add((Component) mComponent);

		addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {
				try {	// remove callbacks we instantiated
					if (myQTVRInstance != null) {
						myQTVRInstance.removeEnteringNodeProc();
						myQTVRInstance.removeLeavingNodeProc();
						myQTVRInstance.removeMouseOverHotSpotProc();

						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRSetPanAngleSelector);
						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRSetTiltAngleSelector);
						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRSetFieldOfViewSelector);
						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRSetViewCenterSelector);
						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRTriggerHotSpotSelector);
						myQTVRInstance.removeInterceptProc (QTVRConstants.kQTVRGetHotSpotTypeSelector);
					}
					myMovie.removeDrawingCompleteProc();
					myMovieController.removeActionFilter();
				} catch (QTException ex) {}

				QTSession.close();
				dispose();
			}

			public void windowClosed (WindowEvent e) {
				System.exit(0);
			}
		});
	}

	static class MovieDrawing implements MovieDrawingComplete {
		public int execute (Movie m) {
			System.out.println ("drawing:" + m);
			return 0;
		}

		public int execute (Movie m, int action, StringHandle sh) {
			System.out.println (m + ",action=" + action);
			return 0;
		}
	}

	static class EnteringNode implements QTVREnteringNode {
		public int execute (QTVRInstance vr, int nodeID) {
			System.out.println (vr + ",entering:" + nodeID);
			return 0;
		}
	}

	static class LeavingNode implements QTVRLeavingNode {
		public int execute (QTVRInstance vr, int fromNodeID, int toNodeID, boolean[] cancel) {
			System.out.println (vr + ",leaving:" + fromNodeID + ",entering:" + toNodeID);
			// no error and Don't cancel leaving node
				// cancel[0] = true; -> this would cancel leaving the fromNode
			return 0;
		}
	}

	static class HotSpot implements QTVRMouseOverHotSpot {
		public int execute (QTVRInstance vr, int hotSpotID, int flags) {
			System.out.println (vr + ",hotSpot:" + hotSpotID + ",flags=" + flags);
			return 0;
		}
	}

	static class Interceptor implements QTVRInterceptor {
		public boolean execute (QTVRInstance vr, QTVRInterceptRecord qtvrMsg) {
			System.out.println (vr + "," + qtvrMsg);
			return false;	//dont cancel default execution
		}
	}

	static class PMFilter extends ActionFilter {
		public boolean execute (MovieController mc, int action) {
			System.out.println (mc + "," + "action:" + action);
			return false;
		}

		public boolean execute (MovieController mc, int action, float value) {
			System.out.println (mc + "," + "action:" + action + ",value=" + value);
			return false;
		}
	}

	//_________________________ Runtime Error Handling
	static class Handler implements QTRuntimeHandler {
		public void exceptionOccurred (QTRuntimeException e, Object eGenerator, String methodNameIfKnown, boolean unrecoverableFlag) {
			System.out.println (eGenerator + "," + methodNameIfKnown + ",unrecoverable=" + unrecoverableFlag);
			e.printStackTrace();
			throw e;	// we don't handle this exception - just print stack trace and throw it
		}
	}
}


