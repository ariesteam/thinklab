package org.integratedmodelling.mca.electre3.view;

import java.text.DecimalFormat;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class ViewSettings {

    public static DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }
    
    public static void setDecimalFormat(String pattern) {
        decimalFormat = new DecimalFormat(pattern);
    }
    
    private static DecimalFormat decimalFormat = new DecimalFormat("#.###");
}
