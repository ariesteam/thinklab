package org.integratedmodelling.mca.promethee.ranking;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public interface IRankingStrategy {

    public int[] getRanks(double[] leavingFlows, double[] enteringFlows);
    
}
