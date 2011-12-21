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
package org.integratedmodelling.mca.promethee;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class PrometheeConstants {

    /* Types of criteria */
    public static int USUAL = 101;
    public static int QUASI = 102;
    public static int LINEAR = 103;
    public static int LEVEL = 104;
    public static int QUASILINEAR = 105;
    public static int GAUSSIAN = 106;
    
    /* Action to do on a criterion */
    public static int MAXIMIZE = 201;
    public static int MINIMIZE = 202;
    
    /* Ranking method */
    public static int PROMETHEE_I = 301;
    public static int PROMETHEE_II = 302;
}