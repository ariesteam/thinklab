package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class LinearHFunction implements IHFunction {

    public LinearHFunction(double preferenceThreshold) {
        this.p = preferenceThreshold;
    }
    
    public double getHValue(double difference) {
        double absd = Math.abs(difference);
        if (absd >= p) return 1.0;
        else return (absd / p);
    }

    private double p;
    
}
