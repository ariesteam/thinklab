package org.integratedmodelling.utils.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * 
 * Usage:
 * 
 * 	ContourPlot plot = new ContourPlot(width, height, cmap, gridX, gridY);
	plot.setData(data);
	plot.paint();
	plot.save("file.png");
 *
 */
public class ContourPlot extends BufferedImage {

	private static final long serialVersionUID = -4321635336670844054L;

	final static boolean	SHOW_NUMBERS	= true;
	final static int	BLANK		= 32,
				OPEN_SUITE	= (int)'{',
				CLOSE_SUITE	= (int)'}',
				BETWEEN_ARGS	= (int)',',
				N_CONTOURS	= 10,
				PLOT_MARGIN	= 0,
				WEE_BIT		=  3,
				NUMBER_LENGTH	=  3;
	final static double	Z_MAX_MAX	= 1.0E+10,
				Z_MIN_MIN	= -Z_MAX_MAX;
	final static String EOL	=
		System.getProperty("line.separator");

	// Below, data members which store the grid steps,
	// the z values, the interpolation flag, the dimensions
	// of the contour plot and the increments in the grid:
	int		xSteps, ySteps;
	double		z[][];
	boolean		logInterpolation = false;
	Dimension	d = new Dimension();
	double		deltaX, deltaY;

	// Below, data members, most of which are adapted from
	// Fortran variables in Snyder's code:
	int	ncv = N_CONTOURS;
	int	l1[] = new int[4];
	int	l2[] = new int[4];
	int	ij[] = new int[2];
	int	i1[] = new int[2];
	int	i2[] = new int[2];
	int	i3[] = new int[6];
	int	ibkey,icur,jcur,ii,jj,elle,ix,iedge,iflag,ni,ks;
	int	cntrIndex,prevIndex;
	int	idir,nxidir,k;
	double	z1,z2,cval,zMax,zMin;
	double	intersect[]	= new double[4];
	double	xy[]		= new double[2];
	double	prevXY[]	= new double[2];
	float	cv[]		= new float[ncv];
	boolean	jump;
	ColorMap cmap; 
	
	public static ContourPlot getPlot(int x, int y) {
		ColorMap cmap = ColorMap.getColormap("rainbow", N_CONTOURS+1, true);
		ContourPlot ret = new ContourPlot(x, y, cmap, x, y);
		return ret;
	}
	
	//-------------------------------------------------------
	// A constructor method.
	//-------------------------------------------------------
	private ContourPlot(int width, int height, ColorMap cmap, int x, int y) {
		super(width, height, TYPE_BYTE_INDEXED, cmap.getColorModel());
		this.cmap = cmap;
		d.width = width;
		d.height = height;
		xSteps = x;
		ySteps = y;
	}

	//-------------------------------------------------------
	private int sign(int a, int b) {
		a = Math.abs(a);
		if (b < 0)	return -a;
		else		return  a;
	}

	//-------------------------------------------------------
	// "GetExtremes" scans the data in "z" in order
	// to assign values to "zMin" and "zMax".
	//-------------------------------------------------------
	private void getExtremes() throws ThinklabValidationException {
		int	i,j;
		double	here;

		zMin = z[0][0];
		zMax = zMin;
		for (i = 0; i < xSteps; i++) {
			for (j = 0; j < ySteps; j++) {
				here = z[i][j];
				if (zMin > here) zMin = here;
				if (zMax < here) zMax = here;
			}
		}
		if (zMin == zMax) {
			throw new ThinklabValidationException("invalid data in contour plot");
		}
		return;
	}

	//-------------------------------------------------------
	// "AssignContourValues" interpolates between "zMin" and
	// "zMax", either logarithmically or linearly, in order
	// to assign contour values to the array "cv".
	//-------------------------------------------------------
	private void assignContourValues() throws ThinklabValidationException {
		int	i;
		double	delta;

		if ((logInterpolation) && (zMin <= 0.0)) {
			throw new ThinklabValidationException("error assignign contour values");
		}
		if (logInterpolation) {
			double	temp = Math.log(zMin);

			delta = (Math.log(zMax)-temp) / ncv;
			for (i = 0; i < ncv; i++) cv[i] = (float)Math.exp(temp + (i+1)*delta);
		}
		else {
			delta = (zMax-zMin) / ncv;
			for (i = 0; i < ncv; i++) cv[i] = (float)(zMin + (i+1)*delta);
		}
	}

	//-------------------------------------------------------
	// "SetMeasurements" determines the dimensions of
	// the contour plot and the increments in the grid.
	//-------------------------------------------------------
	private void setMeasurements() {
		d.width  = d.width  - 2*PLOT_MARGIN;
		d.height = d.height - 2*PLOT_MARGIN;
		deltaX = d.width  / (xSteps - 1.0);
		deltaY = d.height / (ySteps - 1.0);
	}

	//-------------------------------------------------------
	// "DrawGrid" draws the rectangular grid of gray lines
	// on top of which the contours will later be drawn.
	//-------------------------------------------------------
	private void drawGrid(Graphics g) {
		int i,j,kx,ky;

		// Interchange horizontal & vertical
		g.clearRect(0, 0, d.height+2*PLOT_MARGIN, d.width +2*PLOT_MARGIN);
		g.setColor(Color.gray);
		for (i = 0; i < xSteps; i++) {
			kx = (int)((float)i * deltaX);
			g.drawLine( PLOT_MARGIN,
			PLOT_MARGIN+kx,
			PLOT_MARGIN+d.height,
			PLOT_MARGIN+kx);
		}
		for (j = 0; j < ySteps; j++) {
			ky = (int)((float)j * deltaY);
			g.drawLine( PLOT_MARGIN+ky,
			PLOT_MARGIN,
			PLOT_MARGIN+ky,
			PLOT_MARGIN+d.width);
		}
		g.setColor(Color.black);
	}

	//-------------------------------------------------------
	// "SetColour" sets the colour of the graphics object,
	// given the contour index, by interpolating linearly
	// between "Color.blue" & "Color.red".
	//-------------------------------------------------------
	private void setColour(Graphics g) {
		
		Color c = cmap.getColor(ncv-cntrIndex+1);
//		
//		Color c = new Color(
//			((ncv-cntrIndex) * Color.blue.getRed()   +
//				cntrIndex * Color.red.getRed())/ncv,
//			((ncv-cntrIndex) * Color.blue.getGreen() +
//				cntrIndex * Color.red.getGreen())/ncv,
//			((ncv-cntrIndex) * Color.blue.getBlue()  +
//				cntrIndex * Color.red.getBlue())/ncv);

		g.setColor(c);
	}

	//-------------------------------------------------------
	// "DrawKernel" is the guts of drawing and is called
	// directly or indirectly by "ContourPlotKernel" in order
	// to draw a segment of a contour or to set the pen
	// position "prevXY". Its action depends on "iflag":
	//
	// iflag == 1 means Continue a contour
	// iflag == 2 means Start a contour at a boundary
	// iflag == 3 means Start a contour not at a boundary
	// iflag == 4 means Finish contour at a boundary
	// iflag == 5 means Finish closed contour not at boundary
	// iflag == 6 means Set pen position
	//
	// If the constant "SHOW_NUMBERS" is true then when
	// completing a contour ("iflag" == 4 or 5) the contour
	// index is drawn adjacent to where the contour ends.
	//-------------------------------------------------------
	private void drawKernel(Graphics g) {
		int	prevU,prevV,u,v;

		if ((iflag == 1) || (iflag == 4) || (iflag == 5)) {
			if (cntrIndex != prevIndex) { // Must change colour
				setColour(g);
				prevIndex = cntrIndex;
			}
			prevU = (int)((prevXY[0] - 1.0) * deltaX);
			prevV = (int)((prevXY[1] - 1.0) * deltaY);
			u = (int)((xy[0] - 1.0) * deltaX);
			v = (int)((xy[1] - 1.0) * deltaY);

			// Interchange horizontal & vertical
			g.drawLine(PLOT_MARGIN+prevV,PLOT_MARGIN+prevU,
				   PLOT_MARGIN+v, PLOT_MARGIN+u);
			if ((SHOW_NUMBERS) && ((iflag==4) || (iflag==5))) {
				if      (u == 0)	u = u - WEE_BIT;
				else if	(u == d.width)  u = u + PLOT_MARGIN/2;
				else if	(v == 0)	v = v - PLOT_MARGIN/2;
				else if	(v == d.height) v = v + WEE_BIT;
				g.drawString(Integer.toString(cntrIndex),
					PLOT_MARGIN+v, PLOT_MARGIN+u);
			}
		}
		prevXY[0] = xy[0];
		prevXY[1] = xy[1];
	}

	private void detectBoundary() {
		ix = 1;
		if (ij[1-elle] != 1) {
			ii = ij[0] - i1[1-elle];
			jj = ij[1] - i1[elle];
			if (z[ii-1][jj-1] <= Z_MAX_MAX) {
				ii = ij[0] + i2[elle];
				jj = ij[1] + i2[1-elle];
				if (z[ii-1][jj-1] < Z_MAX_MAX) ix = 0;
			}
			if (ij[1-elle] >= l1[1-elle]) {
				ix = ix + 2;
				return;
			}
		}
		ii = ij[0] + i1[1-elle];
		jj = ij[1] + i1[elle];
		if (z[ii-1][jj-1] > Z_MAX_MAX) {
			ix = ix + 2;
			return;
		}
		if (z[ij[0]][ij[1]] >= Z_MAX_MAX) ix = ix + 2;
	}

	private boolean routine_label_020() {
		l2[0] =  ij[0];
		l2[1] =  ij[1];
		l2[2] = -ij[0];
		l2[3] = -ij[1];
		idir = 0;
		nxidir = 1;
		k = 1;
		ij[0] = Math.abs(ij[0]);
		ij[1] = Math.abs(ij[1]);
		if (z[ij[0]-1][ij[1]-1] > Z_MAX_MAX) {
			elle = idir % 2;
			ij[elle] = sign(ij[elle],l1[k-1]);
			return true;
		}
		elle = 0;
		return false;
	}


	private boolean routine_label_050() {
		while (true) {
			if (ij[elle] >= l1[elle]) {
				if (++elle <= 1) continue;
				elle = idir % 2;
				ij[elle] = sign(ij[elle],l1[k-1]);
				if (routine_label_150()) return true;
				continue;
			}
			ii = ij[0] + i1[elle];
			jj = ij[1] + i1[1-elle];
			if (z[ii-1][jj-1] > Z_MAX_MAX) {
				if (++elle <= 1) continue;
				elle = idir % 2;
				ij[elle] = sign(ij[elle],l1[k-1]);
				if (routine_label_150()) return true;
				continue;
			}
			break;
		}
		jump = false;
		return false;
	}
	
	private boolean routine_label_150() {
		while (true) {
			//------------------------------------------------
			// Lines from z[ij[0]-1][ij[1]-1]
			//	   to z[ij[0]  ][ij[1]-1]
			//	  and z[ij[0]-1][ij[1]]
			// are not satisfactory. Continue the spiral.
			//------------------------------------------------
			if (ij[elle] < l1[k-1]) {
				ij[elle]++;
				if (ij[elle] > l2[k-1]) {
					l2[k-1] = ij[elle];
					idir = nxidir;
					nxidir = idir + 1;
					k = nxidir;
					if (nxidir > 3) nxidir = 0;
				}
				ij[0] = Math.abs(ij[0]);
				ij[1] = Math.abs(ij[1]);
				if (z[ij[0]-1][ij[1]-1] > Z_MAX_MAX) {
					elle = idir % 2;
					ij[elle] = sign(ij[elle],l1[k-1]);
					continue;
				}
				elle = 0;
				return false;
			}
			if (idir != nxidir) {
				nxidir++;
				ij[elle] = l1[k-1];
				k = nxidir;
				elle = 1 - elle;
				ij[elle] = l2[k-1];
				if (nxidir > 3) nxidir = 0;
				continue;
			}

			if (ibkey != 0) return true;
			ibkey = 1;
			ij[0] = icur;
			ij[1] = jcur;
			if (routine_label_020()) continue;
			return false;
		}
	}

	private short routine_label_200(Graphics g,  boolean workSpace[])
	{
		while (true) {
			xy[elle] = 1.0*ij[elle] + intersect[iedge-1];
			xy[1-elle] = 1.0*ij[1-elle];
			workSpace[2*(xSteps*(ySteps*cntrIndex+ij[1]-1)
				+ij[0]-1) + elle] = true;
			drawKernel(g);
			if (iflag >= 4) {
				icur = ij[0];
				jcur = ij[1];
				return 1;
			}
			continueContour();
			if (!workSpace[2*(xSteps*(ySteps*cntrIndex
				+ij[1]-1)+ij[0]-1)+elle]) return 2;
			iflag = 5;		// 5. Finish a closed contour
			iedge = ks + 2;
			if (iedge > 4) iedge = iedge - 4;
			intersect[iedge-1] = intersect[ks-1];
		}
	}

	private boolean crossedByContour(boolean workSpace[]) {
		ii = ij[0] + i1[elle];
		jj = ij[1] + i1[1-elle];
		z1 = z[ij[0]-1][ij[1]-1];
		z2 = z[ii-1][jj-1];
		for (cntrIndex = 0; cntrIndex < ncv; cntrIndex++) {
			int i = 2*(xSteps*(ySteps*cntrIndex+ij[1]-1) + ij[0]-1) + elle;

			if (!workSpace[i]) {
				float x = cv[cntrIndex];
				if ((x>Math.min(z1,z2)) && (x<=Math.max(z1,z2))) {
					workSpace[i] = true;
					return true;
				}
			}
		}
		return false;
	}

	private void continueContour() {
		short local_k;

		ni = 1;
		if (iedge >= 3) {
			ij[0] = ij[0] - i3[iedge-1];
			ij[1] = ij[1] - i3[iedge+1];
		}
		for (local_k = 1; local_k < 5; local_k++)
			if (local_k != iedge) {
				ii = ij[0] + i3[local_k-1];
				jj = ij[1] + i3[local_k];
				z1 = z[ii-1][jj-1];
				ii = ij[0] + i3[local_k];
				jj = ij[1] + i3[local_k+1];
				z2 = z[ii-1][jj-1];
				if ((cval > Math.min(z1,z2) && (cval <= Math.max(z1,z2)))) {
					if ((local_k == 1) || (local_k == 4)) {
						double	zz = z2;

						z2 = z1;
						z1 = zz;
					}
					intersect[local_k-1] = (cval - z1)/(z2 - z1);
					ni++;
					ks = local_k;
				}
			}
		if (ni != 2) {
			//-------------------------------------------------
			// The contour crosses all 4 edges of cell being
			// examined. Choose lines top-to-left & bottom-to-
			// right if interpolation point on top edge is
			// less than interpolation point on bottom edge.
			// Otherwise, choose the other pair. This method
			// produces the same results if axes are reversed.
			// The contour may close at any edge, but must not
			// cross itself inside any cell.
			//-------------------------------------------------
			ks = 5 - iedge;
			if (intersect[2] >= intersect[0]) {
				ks = 3 - iedge;
				if (ks <= 0) ks = ks + 4;
			}
		}
		//----------------------------------------------------
		// Determine whether the contour will close or run
		// into a boundary at edge ks of the current cell.
		//----------------------------------------------------
		elle = ks - 1;
		iflag = 1;		// 1. Continue a contour
		jump = true;
		if (ks >= 3) {
			ij[0] = ij[0] + i3[ks-1];
			ij[1] = ij[1] + i3[ks+1];
			elle = ks - 3;
		}
	}

	private void contourPlotKernel(Graphics g,	boolean workSpace[])
	{
		short val_label_200;

		l1[0] = xSteps;	l1[1] = ySteps;
		l1[2] = -1;l1[3] = -1;
		i1[0] =	1; i1[1] =  0;
		i2[0] =	1; i2[1] = -1;
		i3[0] =	1; i3[1] =  0; i3[2] = 0;
		i3[3] =	1; i3[4] =  1; i3[5] = 0;
		prevXY[0] = 0.0; prevXY[1] = 0.0;
		xy[0] = 1.0; xy[1] = 1.0;
		cntrIndex = 0;
		prevIndex = -1;
		iflag = 6;
		drawKernel(g);
		icur = Math.max(1, Math.min((int)Math.floor(xy[0]), xSteps));
		jcur = Math.max(1, Math.min((int)Math.floor(xy[1]), ySteps));
		ibkey = 0;
		ij[0] = icur;
		ij[1] = jcur;
		if (routine_label_020() &&
			 routine_label_150()) return;
		if (routine_label_050()) return;
		while (true) {
			detectBoundary();
			if (jump) {
				if (ix != 0) iflag = 4; // Finish contour at boundary
				iedge = ks + 2;
				if (iedge > 4) iedge = iedge - 4;
				intersect[iedge-1] = intersect[ks-1];
				val_label_200 = routine_label_200(g,workSpace);
				if (val_label_200 == 1) {
					if (routine_label_020() && routine_label_150()) return;
					if (routine_label_050()) return;
					continue;
				}
				if (val_label_200 == 2) continue;
				return;
			}
			if ((ix != 3) && (ix+ibkey != 0) && crossedByContour(workSpace)) {
				//
				// An acceptable line segment has been found.
				// Follow contour until it hits a
				// boundary or closes.
				//
				iedge = elle + 1;
				cval = cv[cntrIndex];
				if (ix != 1) iedge = iedge + 2;
				iflag = 2 + ibkey;
				intersect[iedge-1] = (cval - z1) / (z2 - z1);
				val_label_200 = routine_label_200(g,workSpace);
				if (val_label_200 == 1) {
					if (routine_label_020() && routine_label_150()) return;
					if (routine_label_050()) return;
					continue;
				}
				if (val_label_200 == 2) continue;
				return;
			}
			if (++elle > 1) {
				elle = idir % 2;
				ij[elle] = sign(ij[elle],l1[k-1]);
				if (routine_label_150()) return;
			}
			if (routine_label_050()) return;
		}
	}

	public void paint()
	{
		Graphics g = getGraphics();
		int workLength = 2 * xSteps * ySteps * ncv;
		boolean	workSpace[]; // Allocate below if data valid

		setMeasurements();
		drawGrid(g);
		if (cv[0] != cv[1]) { // Valid data
			workSpace = new boolean[workLength];
			contourPlotKernel(g, workSpace);
		}
	}

	public void save(String file) {
		ImageUtil.saveImage(this, file);
	}
	
	public void setData(double[][] data) throws ThinklabValidationException
	{
		z = data;
		getExtremes();
		if (zMax > Z_MAX_MAX) zMax = Z_MAX_MAX;
		if (zMin < Z_MIN_MIN) zMin = Z_MIN_MIN;
		assignContourValues();
	}


}
