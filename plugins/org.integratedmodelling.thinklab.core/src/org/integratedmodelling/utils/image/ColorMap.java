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

/**
 * this is a wrapper around IndexColorModel, just for convenience
 * @author Sergey Krivov
 *
 */
public class ColorMap {
	
	IndexColorModel model;

	/**
	 * 
	 */
	public ColorMap(int bits, int[] indexes, Color[] colors, Color missingIndexColor) {
		
		int maxIndex=0;
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
		
		createColorModel(  bits,   colorArray);
	}
	
	public ColorMap(int bits, Color[] colors) {
		createColorModel(  bits,   colors);
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
		 
		 model = new IndexColorModel(bits, size,r,g,b);
	}
	
	
	public IndexColorModel getColorModel(){
		return model;
	}
	
	 
}
