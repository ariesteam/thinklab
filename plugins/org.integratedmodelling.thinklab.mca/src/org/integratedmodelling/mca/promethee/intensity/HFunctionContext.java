package org.integratedmodelling.mca.promethee.intensity;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class HFunctionContext {

    public HFunctionContext(IHFunction strategy) {
        this.strategy = strategy;
    }
    
    public double getValue(double difference) {
        return strategy.getHValue(difference);
    }
    
    IHFunction strategy;
}
