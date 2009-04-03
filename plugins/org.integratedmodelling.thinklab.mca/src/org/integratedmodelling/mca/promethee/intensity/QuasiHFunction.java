package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class QuasiHFunction implements IHFunction {

    public QuasiHFunction(double indifferenceThreshold) {
        this.q = indifferenceThreshold;
    }
    
    public double getHValue(double difference) {
        if (Math.abs(difference) > q) return 1.0;
        else return 0.0;
    }
    
    private double q;
    
}
