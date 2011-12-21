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
package org.integratedmodelling.mca.electre3.model;

import java.util.Hashtable;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class MatrixModel {

    public MatrixModel() {
        values = new Hashtable<AAPair, Double>();
    }

    public void setValue(Alternative alt1, Alternative alt2, double value) {
        AAPair aa = new AAPair(alt1, alt2);
        values.put(aa, value);
    }

    public double getValue(AAPair aa) {
        return values.get(aa);
    }
    
    private Hashtable<AAPair, Double> values;
}
