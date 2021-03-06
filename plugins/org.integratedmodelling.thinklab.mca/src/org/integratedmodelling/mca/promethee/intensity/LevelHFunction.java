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
package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class LevelHFunction implements IHFunction {

    public LevelHFunction(double indifferenceThreshold,
            double preferenceThreshold) {
        
        this.p = preferenceThreshold;
        this.q = indifferenceThreshold;
    }
    
    public double getHValue(double difference) {
        double absd = Math.abs(difference);
        if (absd >= p) return 1.0;
        else if (absd >= q) return 0.5;
        else return 0.0;
    }
    
    private double p;
    private double q;

}
