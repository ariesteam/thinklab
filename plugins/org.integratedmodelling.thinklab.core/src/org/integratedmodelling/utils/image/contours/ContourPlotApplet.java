package org.integratedmodelling.utils.image.contours;

import java.awt.*;
import java.io.*;

//----------------------------------------------------------
// "ContourPlotApplet" is the main class, i.e. the applet
// which is a container for all the user-interface elements.
//----------------------------------------------------------
public class ContourPlotApplet extends java.applet.Applet {

	private static final long serialVersionUID = 1338114997763055973L;
	// Below, constants, i.e. "final static" data members:
	final static int NUMBER_COMPONENTS	= 6;
	final static int MIN_X_STEPS =   2,
			 MIN_Y_STEPS =   2,
			 MAX_X_STEPS = 1000,
			 MAX_Y_STEPS = 1000;
	final static String EOL	=
		System.getProperty("line.separator");
	final static String DEFAULT_Z	=
		"{{0.5,1.1,1.5,1,2.0,3,3,2,1,0.1}," + EOL +
		" {1.0,1.5,3.0,5,6.0,2,1,1.2,1,4}," + EOL +
		" {0.9,2.0,2.1,3,6.0,7,3,2,1,1.4}," + EOL +
		" {1.0,1.5,3.0,4,6.0,5,2,1.5,1,2}," + EOL +
		" {0.8,2.0,3.0,3,4.0,4,3,2.4,2,3}," + EOL +
		" {0.6,1.1,1.5,1,4.0,3.5,3,2,3,4}," + EOL +
		" {1.0,1.5,3.0,5,6.0,2,1,1.2,2.7,4}," + EOL +
		" {0.8,2.0,3.0,3,5.5,6,3,2,1,1.4}," + EOL +
		" {1.0,1.5,3.0,4,6.0,5,2,1,0.5,0.2}}";

	// Below, the six user-interface components:
	ContourPlot thePlot	 =	new ContourPlot(MIN_X_STEPS,
							MIN_Y_STEPS);
	Label		zPrompt  =	new Label("", Label.LEFT);
	TextArea	zField   =	new TextArea(DEFAULT_Z,30,6);
	Checkbox	interBox =	new Checkbox();
	Button		drawBtn  =	new Button();
	TextArea	results  =	new TextArea();

	// Below, class data members read from the <APPLET> tag:
	static String	contourValuesTitle,infoStrX,infoStrY,
			errParse,errLog,errComp,errEqual,
			errExpect,errEOF,errBounds;

	//-------------------------------------------------------
	// "init" overrides "super.init()" and initializes the
	// applet by:
	//	1. getting parameters from the <APPLET> tag;
	// 2. setting layout to instance of "ContourPlotLayout";
	// 3. initializing and adding the six user-interface
	//		components, using the method "add()" which will
	//		also call "ContourPlotLayout.addLayoutComponent()".
	//-------------------------------------------------------
	public void init() {
		infoStrX = getParameter("stringX");
		infoStrY = getParameter("stringY");

		setLayout(new ContourPlotLayout());
		add("thePlot", thePlot);
		zPrompt.setText(getParameter("stringZ"));
		add("zPrompt", zPrompt);
		zField.setBackground(Color.white);
		add("zField", zField);
		interBox.setLabel(getParameter("stringBox"));
		interBox.setState(false);
		add("interBox", interBox);
		drawBtn.setLabel(getParameter("stringButton"));
		drawBtn.setFont(new Font("Helvetica", Font.BOLD, 10));
		drawBtn.setBackground(Color.white);
		add("drawBtn", drawBtn);
		results.setEditable(false);
		results.setFont(new Font("Courier", Font.PLAIN, 9));
		results.setBackground(Color.white);
		add("results", results);
		contourValuesTitle =	getParameter("stringResults");
		errParse	=	getParameter("stringErrParse");
		errLog		=	getParameter("stringErrLog1") + EOL +
					getParameter("stringErrLog2") + EOL +
					getParameter("stringErrLog3");
		errComp		=	getParameter("stringErrComp");
		errEqual	=	getParameter("stringErrEqual");
		errExpect	=	getParameter("stringErrExpect");
		errEOF		=	getParameter("stringErrEOF");
		errBounds	=	getParameter("stringErrBounds");
	}

	//-------------------------------------------------------
	// Handle events. The only event not handled by the
	// superclass is a mouse hit (i.e. "Event.ACTION_EVENT")
	// in the "Draw" button.
	//-------------------------------------------------------
	public boolean handleEvent(Event e) {
		if ((e != null) &&
			(e.id == Event.ACTION_EVENT) &&
			(e.target == drawBtn)) {
			DrawTheContourPlot();
			return true;
		}
		else return super.handleEvent(e);
	}

	//-------------------------------------------------------
	// "DrawTheContourPlot" does what its name says (in
	// reaction to a hit on the "Draw" button).
	// The guts of this method are in the "try" block which:
	// 1. gets the interpolation flag (for contour values);
	// 2. parses the data, i.e. the matrix of z values;
	// 3. draws the contour plot by calling the "paint()"
	//		method of the component "thePlot";
	//	4. displays the results, i.e. the number of rows and
	//		columns in the grid, an echo of the matrix of z
	//		values, and the list of contour values.
	// This method catches 2 exceptions, then finally (i.e.
	// regardless of exceptions) sends a completion message
	// to the Java console using "System.out.println()".
	//-------------------------------------------------------
	public void DrawTheContourPlot() {
		String		s;

		try {
			s = zField.getText();
			thePlot.logInterpolation = interBox.getState();
			thePlot.ParseZedMatrix(s);
			thePlot.paint(thePlot.getGraphics());
			s = thePlot.ReturnZedMatrix() +
				contourValuesTitle + EOL +
				thePlot.GetContourValuesString();
			results.setText(s);
		}
		catch(ParseMatrixException e) {
			thePlot.repaint();
			results.setText(e.getMessage());
		}
		catch(IOException e) {
			thePlot.repaint();
			results.setText(e.getMessage());
		}
		finally {
			System.out.println("Exiting DrawTheContourPlot");
		}
	}
}

