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

import org.integratedmodelling.mca.promethee.PrometheeConstants;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class IntensityCalculator  {

    public double[][] getIntensities(double[] performanceValues,
            int operation, HFunctionContext hFunction) {

        int acnt = performanceValues.length; // No of alternatives
        double[][] intensities = new double[acnt][acnt];

        // Loop through pairs of alternatives
        for (int ia1 = 0; ia1 < acnt; ia1++) {
            double pa1 = performanceValues[ia1];
            for (int ia2 = 0; ia2 < acnt; ia2++) {
                double pa2 = performanceValues[ia2];
                double margin = calculateMargin(pa1, pa2, operation);
                if (margin >= 0.0)
                    intensities[ia1][ia2] = hFunction.getValue(margin);
                else
                    intensities[ia1][ia2] = 0.0;
            }
        }

        return intensities;
    }

    private double calculateMargin(double pa1, double pa2, int operation) {
        if (operation == PrometheeConstants.MAXIMIZE) {
            return (pa1 - pa2);
        } else if (operation == PrometheeConstants.MINIMIZE) {
            return (pa2 - pa1);
        } else { 
            throw new UnsupportedOperationException("Invalid operation code");
        }
    }
}
