package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class QuasiLinearHFunction implements IHFunction {

    public QuasiLinearHFunction(double indifferenceThreshold,
            double preferenceThreshold) {

        this.p = preferenceThreshold;
        this.q = indifferenceThreshold;
    }

    public double getHValue(double difference) {
        double absd = Math.abs(difference);
        if (absd >= p) return 1.0;
        else if (absd >= q) return ((absd - q) / (p - q));
        else return 0.0;
    }
    private double p;
    private double q;
}
