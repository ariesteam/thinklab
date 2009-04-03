package org.integratedmodelling.mca.promethee.ranking;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class RankCalculator {

    public int[] getRanks(double[][] globalIntensities, RankingContext rc) {
        int cnt = globalIntensities.length;
        leavingFlows = new double[cnt];
        enteringFlows = new double[cnt];
        for (int i = 0; i < cnt; i++) {
            for (int j = 0; j < cnt; j++) {
                leavingFlows[i] += globalIntensities[i][j];
                enteringFlows[j] += globalIntensities[i][j];
            }
        }
        return rc.getRanks(leavingFlows, enteringFlows);
    }
    
    public double[] getEnteringFlows() {
        return enteringFlows;
    }
    
    public double[] getLeavingFlows() {
        return leavingFlows;
    }
    
    private double[] leavingFlows;
    private double[] enteringFlows;
    
}
