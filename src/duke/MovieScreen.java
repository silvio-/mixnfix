/*
 * QuickTime for Java SDK Sample Code

   Usage subject to restrictions in SDK License Agreement
 * Copyright: © 1996-1999 Apple Computer, Inc.

 */
package duke;

import java.io.IOException;

import quicktime.QTException;
import quicktime.app.view.QTComponent;
import quicktime.app.view.QTFactory;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.std.movies.Movie;
import quicktime.std.movies.MovieController;

/**
 * This class is just used as a central location for the player, canvas and
 * controller, which are all set up via the getNewMovie method.
 */
public class MovieScreen {
//_________________________ CLASS METHODS
	public MovieScreen() throws QTException {
                try {
                        QTFile qtfile = new QTFile(QTFactory.findAbsolutePath("Sample.mov"));
                        OpenMovieFile openqtfile = OpenMovieFile.asRead(qtfile);
                        Movie movie = Movie.fromFile(openqtfile);
                        MovieController movController = new MovieController(movie);
                        movieComponent = QTFactory.makeQTComponent(movController);
                } catch (IOException e) {}
        }
	
//_________________________ INSTANCE VARIABLES
        private QTComponent movieComponent = null;
	
//_________________________ INSTANCE METHODS
	/** Stops the player and removes it from the task thread*/
	public void stopPlayer () {
		try {
			if (movieComponent != null && movieComponent instanceof QTComponent)
				movieComponent.getMovieController().play(0);
		} catch (QTException e) {}
	}

	/** Return the current QTComponent. */
	public QTComponent getMovieComponent() { return this.movieComponent; }
	
	public void startPlayer() {
		try {
			if (movieComponent != null && movieComponent instanceof QTComponent)
				movieComponent.getMovieController().play(1);
		} catch (QTException e) {}
	}
	
	public void setLooping (boolean flag) {
		try {
			if (movieComponent != null && movieComponent instanceof QTComponent)
				movieComponent.getMovieController().setLooping(flag);	
		} catch (QTException e) {}
	}
	
	public boolean isLooping () {
		try {
			if (movieComponent != null && movieComponent instanceof QTComponent)
				return movieComponent.getMovieController().getLooping();
		} catch (QTException e) {}
		return false;
	}

	/**
	 * Allow user to pick a new movie. Ordinarily, this method would return a
	 * boolean. But it returns the path string of the chosen movie so we know
	 * where to get the images for the tumbling duke animation.
	 */
	public void getNewMovie (QTFile qtf) throws QTException, IOException {
		stopPlayer();
		
                OpenMovieFile openqtfile = OpenMovieFile.asRead(qtf);
                Movie movie = Movie.fromFile(openqtfile);
                MovieController movController = new MovieController(movie);
                movieComponent.setMovieController(movController);
	}
}
