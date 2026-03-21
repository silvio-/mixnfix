/*
 * QuickTime for Java SDK Sample Code

   Usage subject to restrictions in SDK License Agreement
 * Copyright: © 1996-1999 Apple Computer, Inc.

 */
package duke;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import quicktime.Errors;
import quicktime.QTException;
import quicktime.io.QTFile;
import quicktime.util.QTUtils;


/**
 * A simple panel of AWT objects to demonstrate how AWT can be used to control a
 * QuickTime movie.
 */
public class ControlPanel extends Panel implements Errors {
//_________________________ CLASS METHODS
	public ControlPanel(Color bgColor, MovieScreen ms, Frame frame) {
		this.ms = ms;
                this.frame = frame;
        
		setBackground(bgColor);
		setLayout(new FlowLayout(FlowLayout.CENTER, 10, 3));
		setFont(new Font("Geneva", Font.BOLD, 10));
		
		Action actionListener = new Action();
		Item itemListener = new Item();
	
		// start, stop, loop and get a new movie
		movieButtons.setBackground(getBackground());
		movieButtons.setLayout(new GridLayout(4, 1, 3, 3));
		movieButtons.add(startButton);
		movieButtons.add(stopButton);
		loopButton.setState(false);
		movieButtons.add(loopButton);
		movieButtons.add(getMovieButton);
		add(movieButtons);
		
		startButton.addActionListener( actionListener );
		stopButton.addActionListener( actionListener );
		loopButton.addItemListener( itemListener );
		getMovieButton.addActionListener( actionListener );
		
		// choose a visibility option
		visiblity.setBackground(getBackground());
		visiblity.setLayout(new GridLayout(3, 1));
		visiblity.add(visibleButton);
		visiblity.add(invisibleButton);
		add(visiblity);
		
		visibleButton.addItemListener( itemListener );
		invisibleButton.addItemListener( itemListener );
	}

	public Dimension getPreferredSize () { return new Dimension (100, 130); }
//_________________________ INSTANCE VARIABLES
	private MovieScreen ms;
        private Frame frame;

	private Panel  movieButtons   = new Panel();
	private Button startButton    = new Button("Start");
	private Button stopButton     = new Button("Stop");
	private Button getMovieButton = new Button("Get QT Media");

	private Checkbox loopButton = new Checkbox("Looping");

	private Panel         visiblity		 = new Panel();
	private CheckboxGroup checkBoxGrp2           = new CheckboxGroup();
	private Checkbox      visibleButton  = new Checkbox("Visible", checkBoxGrp2, true);
	private Checkbox      invisibleButton= new Checkbox("Invisible", checkBoxGrp2, false);

//_________________________ INSTANCE METHODS
	public void doGetMovie () {
		try {
			ms.stopPlayer ();
			QTFile qtf = QTFile.standardGetFilePreview(QTFile.kStandardQTFileTypes);
// Below is code to use the Standard Java File Dialog, i.e no fancy Quicktime preview dialog.
//			FileDialog fd = new FileDialog ((Frame)getParent(), "Choose a QuickTime file to view:");
//			QTFileTypesFilter f = new QTFileTypesFilter();
//			f.set3DAccept(false);
//			fd.setFilenameFilter(f);
//			fd.show();
//			String fileDir = fd.getDirectory();
//			String fileName = fd.getFile();
//			if (fileDir != null && fileName != null) {
//				QTFile qtf = new QTFile (fileDir + fileName);
				ms.getNewMovie(qtf);
                                frame.pack();
//			}
		} catch (QTException e) {
		} catch (Exception e) {
		} finally { QTUtils.reclaimMemory(); }
	}
	
	/** Handle all the AWT checkbox item events here. */
	class Item implements ItemListener {
		public void itemStateChanged( ItemEvent event ) {
			if ( event.getSource() == visibleButton ) {
				((Component)ms.getMovieComponent()).setVisible(true);
			}
			else if (event.getSource() == invisibleButton) {
				ms.stopPlayer();
				((Component)ms.getMovieComponent()).setVisible(false);
			}
			else if (event.getSource() == loopButton) {
				if (ms.isLooping()) {
					loopButton.setState(false);
					ms.setLooping(false);
				}
				else {
					loopButton.setState(true);
					ms.setLooping(true);
				}
			}
		}
	}
	
	/** Handle all the AWT button events here. */
	class Action implements ActionListener {
		public void actionPerformed  (ActionEvent event) {
			if ( event.getSource() == getMovieButton ) {
				doGetMovie();
			}
			else if (event.getSource() == stopButton) {
				ms.stopPlayer();
			}
			else if (event.getSource() == startButton) {
				ms.startPlayer();
			}
                }
	}
}
