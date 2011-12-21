/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.dynamicmodelling.utils;

import java.awt.Color;

import org.integratedmodelling.utils.image.Picture;

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
