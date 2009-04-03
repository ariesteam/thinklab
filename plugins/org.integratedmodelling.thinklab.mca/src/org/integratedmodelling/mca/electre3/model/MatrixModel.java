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
