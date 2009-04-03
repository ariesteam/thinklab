package org.integratedmodelling.mca.electre3.model;

import java.io.Serializable;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class Threshold implements Serializable {
    
    public Threshold(double alpha, double beta) {
        setAlpha(alpha);
        setBeta(beta);
    }
    
    public Threshold() {
        setAlpha(0.0);
        setBeta(0.0);
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }
    
    private double alpha;
    private double beta;
    
}
