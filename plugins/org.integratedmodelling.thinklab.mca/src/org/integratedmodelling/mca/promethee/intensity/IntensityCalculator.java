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
