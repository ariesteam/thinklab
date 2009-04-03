package org.integratedmodelling.mca.promethee.ranking;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class RankingContext {
    
    public RankingContext(IRankingStrategy rankingStrategy) {
        this.strategy = rankingStrategy;
    }
    
    public int[] getRanks(double[] leavingFlows, double[] enteringFlows) {
        return strategy.getRanks(leavingFlows, enteringFlows);
    }
    
    private IRankingStrategy strategy;

}
