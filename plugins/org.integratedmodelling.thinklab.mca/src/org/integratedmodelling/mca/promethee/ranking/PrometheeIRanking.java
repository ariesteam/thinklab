package org.integratedmodelling.mca.promethee.ranking;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class PrometheeIRanking implements IRankingStrategy {

    public int[] getRanks(double[] leavingFlows, double[] enteringFlows) {
        int cnt = leavingFlows.length;
        
        // Build the outranking relations
        int[][] relations = new int[cnt][cnt];
        int[] outranker = new int[cnt];
        for (int i = 0; i < cnt; i++) {
            for (int j = i; j < cnt; j++) {
                if ((leavingFlows[i] > leavingFlows [j]) && 
                        (enteringFlows[i] <= enteringFlows[j])) {
                    relations[i][j] = OUTRANKS;
                    relations[j][i] = OUTRANKED;
                } else if ((leavingFlows[j] > leavingFlows [i]) && 
                        (enteringFlows[j] <= enteringFlows[i])) {
                    relations[i][j] = OUTRANKED;
                    relations[j][i] = OUTRANKS;
                } else if ((leavingFlows[i] == leavingFlows[j]) && 
                        (enteringFlows[i] == enteringFlows[j])) {
                    relations[i][j] = relations[j][i] = INDIFFERENT;
                } else if ((leavingFlows[i] > leavingFlows [j]) &&
                        (enteringFlows[i] > enteringFlows[j])) {
                    relations[i][j] = relations[j][i] = INCOMPARABLE;
                }
            }
        }
        
        for (int i = 0; i < cnt; i++) {
            for (int j = 0; j < cnt; j++) {
                if (relations[i][j] == OUTRANKED)
                    outranker[i]++;
            }
        }
        
        // Calculate the ranks
        int[] ranks = new int[cnt];
        boolean finished = false;
        int curRank = 1;
        while (!finished) {
            finished = true;
            int[] newOutranker = new int[cnt];
            System.arraycopy(outranker, 0, newOutranker, 0, cnt);
            for (int i = 0; i < cnt; i++) {
                if (outranker[i] == 0) {
                    newOutranker[i] = -1;
                    finished = false;
                    ranks[i] = curRank;
                    for (int j = 0; j < cnt; j++) {
                        if (relations[i][j] == OUTRANKS)
                            newOutranker[j]--;
                    }
                }
            }
            outranker = newOutranker;
            curRank++;
        }
        
        return ranks;
    }

    private final int OUTRANKS = 1;
    private final int OUTRANKED = 2;
    private final int INDIFFERENT = 3;
    private final int INCOMPARABLE = 4;
    
}
