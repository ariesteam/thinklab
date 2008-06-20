/**
 * TileImage.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.dynamicmodelling.utils;

import java.awt.Color;

/**
 * Simple code from the web - may be used eventually to tile large graphs and show them with
 * GSV.js.
 * @author Ferdinando Villa
 *
 */
public class TileImage {

    public static void main(String[] args) {
    	
        Picture input = new Picture(args[0]);
        int height = input.height();
        int width  = input.width();
        
        int M = Integer.parseInt(args[1]);
        int N = Integer.parseInt(args[2]);

        Picture output = new Picture(N*input.width(), M*input.height());

        // convert to black and white
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = input.get(x, y);
                for (int i = 0; i < M; i++) {
                    for (int j = 0; j < N; j++) {
                        output.set(width*j + x, height*i + y, c);
                    }
                }
            }
        }
        output.show();
        output.save("temp.png");
    }
 

   
}
