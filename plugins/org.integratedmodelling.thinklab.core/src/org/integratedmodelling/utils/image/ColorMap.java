/**
 * ColorMap.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jun 5, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ImageMap.
 * 
 * ImageMap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ImageMap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Sergey Krivov
 * @date      Jun 5, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/

package org.integratedmodelling.utils.image;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

/**
 * this is a wrapper around IndexColorModel, just for convenience
 * @author Sergey Krivov
 *
 */
public class ColorMap {
	
	IndexColorModel model;
	int levels;

	private ColorMap(IndexColorModel cmodel, int levels) {
		this.model = cmodel;
		this.levels = levels;
	}
	
	private static class CmDesc {
		String id;
		int transp;
		boolean isZeroTransparent;
		int nlevels;
		int[] parameters = null;
	}
	
	static CmDesc parseMapDef(String s) {
		
		CmDesc ret = new CmDesc();
		
		// find part in parentheses
		
		
		return ret;
	}
	
	/**
	 * Parse a description string of the form mapname(parms) and return the corresponding map or 
	 * null.
	 * 
	 * Map definitions understood so far:
	 * 
	 * greyscale(n)      // n-level greyscale (black to white)
	 * greenscale(n)      // n-level greenscale (black to green)
	 * redscale(n)      // n-level greenscale (black to red)
	 * bluescale(n)      // n-level greenscale (black to blue)
	 * gradient(r1, g1, b1, r2, g2, b2, [r3, g3, b3, ....,] n)  // gradient from rgb to rgb color(s), n levels
	 * jet(n)  // the classic evil color ramp from blue to red, n levels
	 * BrBG(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels) 
	 * PiYG(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * PRGn(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * PuOr(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * RdBu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * RdGy(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * RdYlBu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * RdYlGn(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Spectral(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Accent(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Dark2(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Paired(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Pastel1(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Pastel2(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Set1(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Set2(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Set3(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Blues(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * BuGn(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * BuPu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * GnBu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Greens(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Greys(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Oranges(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * OrRd(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * PuBuPuBuGn(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * PuRd(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Purples(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * RdPu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * Reds(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * YlGn(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * YlGnBu(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * YlOrBr(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * YlOrRd(n) // corresponding ColorBrewer map (see colorbrewer2.org for admitted levels)
	 * 
	 * Appending a _z to the name (e.g. bluescale_z(12)) will force the zero color to be transparent.
	 * Appending a number (e.g. bluescale_50(12)) will force n% transparency to the whole map.
	 * 
	 * @param id
	 * @return
	 */
	public static ColorMap getColormap(String id) {

		ColorMap ret = null;
		CmDesc def = parseMapDef(id);
		
		
		
		return ret;
	}
	
	/**
     * Creates an array of Color objects for use as a gradient, using a linear 
     * interpolation between the two specified colors.
     * @param one Color used for the bottom of the gradient
     * @param two Color used for the top of the gradient
     * @param numSteps The number of steps in the gradient. 250 is a good number.
     */
    public static Color[] createGradient(final Color one, final Color two, final int numSteps)
    {
        int r1 = one.getRed();
        int g1 = one.getGreen();
        int b1 = one.getBlue();
        int a1 = one.getAlpha();

        int r2 = two.getRed();
        int g2 = two.getGreen();
        int b2 = two.getBlue();
        int a2 = two.getAlpha();

        int newR = 0;
        int newG = 0;
        int newB = 0;
        int newA = 0;

        Color[] gradient = new Color[numSteps];
        double iNorm;
        for (int i = 0; i < numSteps; i++)
        {
            iNorm = i / (double)numSteps; //a normalized [0:1] variable
            newR = (int) (r1 + iNorm * (r2 - r1));
            newG = (int) (g1 + iNorm * (g2 - g1));
            newB = (int) (b1 + iNorm * (b2 - b1));
            newA = (int) (a1 + iNorm * (a2 - a1));
            gradient[i] = new Color(newR, newG, newB, newA);
        }

        return gradient;
    }

    /**
     * Creates an array of Color objects for use as a gradient, using an array of Color objects. It uses a linear interpolation between each pair of points. The parameter numSteps defines the total number of colors in the returned array, not the number of colors per segment.
     * @param colors An array of Color objects used for the gradient. The Color at index 0 will be the lowest color.
     * @param numSteps The number of steps in the gradient. 250 is a good number.
     */
    public static Color[] createMultiGradient(Color[] colors, int numSteps)
    {
        //we assume a linear gradient, with equal spacing between colors
        //The final gradient will be made up of n 'sections', where n = colors.length - 1
        int numSections = colors.length - 1;
        int gradientIndex = 0; //points to the next open spot in the final gradient
        Color[] gradient = new Color[numSteps];
        Color[] temp;

        for (int section = 0; section < numSections; section++)
        {
            //we divide the gradient into (n - 1) sections, and do a regular gradient for each
            temp = createGradient(colors[section], colors[section+1], numSteps / numSections);
            for (int i = 0; i < temp.length; i++)
            {
                //copy the sub-gradient into the overall gradient
                gradient[gradientIndex++] = temp[i];
            }
        }

        if (gradientIndex < numSteps)
        {
            //The rounding didn't work out in our favor, and there is at least
            // one unfilled slot in the gradient[] array.
            //We can just copy the final color there
            for (/* nothing to initialize */; gradientIndex < numSteps; gradientIndex++)
            {
                gradient[gradientIndex] = colors[colors.length - 1];
            }
        }

        return gradient;
    }

	
	/**
	 * 
	 */
	public ColorMap(int bits, int[] indexes, Color[] colors, Color missingIndexColor) {
		
		int maxIndex=0;
		levels = colors.length;
		for (int i = 0; i < indexes.length; i++) {
			if(indexes[i]>maxIndex){
				maxIndex=indexes[i];
			}
		}

		Color[] colorArray = new Color[maxIndex+1];
		for (int i = 0; i < colorArray.length; i++) {
			colorArray[i]= missingIndexColor;
		}
		
		for (int i = 0; i < indexes.length; i++) {
			colorArray[indexes[i]]=colors[i];
		}
		
		createColorModel(bits, colorArray);
	}
	
	public ColorMap(int bits, Color[] colors) {
		levels = colors.length;
		createColorModel(bits, colors);
	}
	
	
	/**
	 * Return a black mask where the index determines transparency, from opaque (0)
	 * to fully transparent (max levels).
	 * 
	 * @param levels
	 * @return
	 */
	public static ColorMap alphamask(int levels) {
		
		byte[] r = new byte[levels];
		byte[] g = new byte[levels];
		byte[] b = new byte[levels];
		byte[] a = new byte[levels];
		
		for (int i = 0; i < levels; i++) {
			// ugly fuchsia
			r[i] = (byte) 244; g[i] = 0; b[i] = (byte)161;
			a[i] = (byte)((256/levels)*i);
		}
		
			 
		return new ColorMap(new IndexColorModel(8,levels,r,g,b,a), levels);
	}
	
	public static ColorMap jet(int n) {

		byte r[] = new byte[n];
		byte g[] = new byte[n];
		byte b[] = new byte[n];

		int maxval = 255;
		Arrays.fill(g, 0, n / 8, (byte) 0);
		for (int x = 0; x < n / 4; x++)
			g[x + n / 8] = (byte) (maxval * x * 4 / n);
		Arrays.fill(g, n * 3 / 8, n * 5 / 8, (byte) maxval);
		for (int x = 0; x < n / 4; x++)
			g[x + n * 5 / 8] = (byte) (maxval - (maxval * x * 4 / n));
		Arrays.fill(g, n * 7 / 8, n, (byte) 0);

		for (int x = 0; x < g.length; x++)
			b[x] = g[(x + n / 4) % g.length];
		Arrays.fill(b, n * 7 / 8, n, (byte) 0);
		Arrays.fill(g, 0, n / 8, (byte) 0);
		for (int x = n / 8; x < g.length; x++)
			r[x] = g[(x + n * 6 / 8) % g.length];

		Color[] table = new Color[n];
		for (int x = 0; x < n; x++)
			table[x] = new Color(getColor(x, r, g, b));

		return new ColorMap(8, table);
	}

	public static int getColor(int idx, byte[] r, byte[] g, byte[] b) {
		int pixel = ((r[idx] << 16) & 0xff0000) | ((g[idx] << 8) & 0xff00)
				| (b[idx] & 0xff);

		return pixel;
	}

	public static ColorMap gradient(Color from, Color to, int numSteps) {
		return new ColorMap(8, createGradient(from,to,numSteps));
	}

	public static ColorMap gradient(Color[] colors, int numSteps) {
		return new ColorMap(8, createMultiGradient(colors, numSteps));
	}

	
	/**
	 * Make N grey levels 
	 * @param levels
	 * @return
	 */
	public static ColorMap greyscale(int levels) {

		/*
		 * grey colormap, to be changed later
		 */
		Color[] greys = new Color[levels];
		int incr = 256/levels;		
		for (int i = 0; i < levels; i++) {			
			int level = i * incr;
			greys[i] = new Color(level, level, level);
		}
		
		return new ColorMap(8, greys);
	}

	public File getColorbar(int h, File fileOrNull) throws ThinklabIOException {
		
		if (fileOrNull == null)
			try {
				fileOrNull = File.createTempFile("cbar", ".png");
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		
		int[][] cdata = new int[h][256];
		int incr = 256/levels;
			for (int y = 0; y < h; y++) {
				for (int i = 0; i < 256; i += incr) {
					int col = i * incr;
					for (int x = i; x < i+incr; x++)
						cdata[y][x] = col;
				}
		}
			
		ImageUtil.createImageFile(cdata, 256, h, this, fileOrNull.toString(), false);
		
		return fileOrNull;
	}
	
	/**
	 * Make N green levels 
	 * @param levels
	 * @return
	 */
	public static ColorMap greenscale(int levels) {

		/*
		 * grey colormap, to be changed later
		 */
		Color[] greens = new Color[levels];
		
		int incr = 256/levels;		
		for (int i = 0; i < levels; i++) {			
			int level = i * incr;
			greens[i] = new Color(0, level, 0);
		}
		
		return new ColorMap(8, greens);
	}

	/**
	 * Make N red levels 
	 * @param levels
	 * @return
	 */
	public static ColorMap redscale(int levels) {

		/*
		 * grey colormap, to be changed later
		 */
		Color[] reds = new Color[levels];
		
		int incr = 256/levels;		
		for (int i = 0; i < levels; i++) {			
			int level = i * incr;
			reds[i] = new Color(level, 0, 0);
		}
		
		return new ColorMap(8, reds);
	}

	/**
	 * Make N yellow levels 
	 * @param levels
	 * @return
	 */
	public static ColorMap yellowscale(int levels) {

		/*
		 * grey colormap, to be changed later
		 */
		Color[] yellows = new Color[levels];
		
		int incr = 256/levels;		
		for (int i = 0; i < levels; i++) {			
			int level = i * incr;
			yellows[i] = new Color(level, level, 0);
		}
		
		return new ColorMap(8, yellows);
	}

	
	/**
	 * Make N blue levels 
	 * @param levels
	 * @return
	 */
	public static ColorMap bluescale(int levels) {

		/*
		 * grey colormap, to be changed later
		 */
		Color[] blues = new Color[levels];
		
		int incr = 256/levels;		
		for (int i = 0; i < levels; i++) {			
			int level = i * incr;
			blues[i] = new Color(0, 0, level);
		}
		
		return new ColorMap(8, blues);
	}

	
	public void createColorModel(int bits, Color[] colors) {
		int size = colors.length;
		 byte[] r= new byte[size];
		 byte[] g= new byte[size];
		 byte[] b= new byte[size];
		 for (int i = 0; i < colors.length; i++) {
			r[i]=(byte)colors[i].getRed();
			g[i]=(byte)colors[i].getGreen();
			b[i]=(byte)colors[i].getBlue();
		}
		 
		 model = new IndexColorModel(bits, size,r,g,b, 0);
	}
	
	public IndexColorModel getColorModel(){
		return model;
	}
    
    public static void main(String args[]) {
    	try {
			greyscale(256).getColorbar(16, new File("cbar.png"));
		} catch (ThinklabIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
