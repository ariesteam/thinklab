package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class GaussianHFunction implements IHFunction {

    public GaussianHFunction(double standardDeviation) {
        this.stdev = standardDeviation;
    }
    
    public double getHValue(double difference) {
        double absd = Math.abs(difference);
        return 1.0 - Math.exp(-Math.pow(absd, 2) / (2 * Math.pow(stdev, 2)));
    }

    private double stdev;
    
}
