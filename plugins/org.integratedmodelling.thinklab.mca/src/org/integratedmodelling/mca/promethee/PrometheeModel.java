package org.integratedmodelling.mca.promethee;

import org.integratedmodelling.mca.promethee.intensity.GaussianHFunction;
import org.integratedmodelling.mca.promethee.intensity.HFunctionContext;
import org.integratedmodelling.mca.promethee.intensity.IntensityCalculator;
import org.integratedmodelling.mca.promethee.intensity.LevelHFunction;
import org.integratedmodelling.mca.promethee.intensity.LinearHFunction;
import org.integratedmodelling.mca.promethee.intensity.QuasiHFunction;
import org.integratedmodelling.mca.promethee.intensity.QuasiLinearHFunction;
import org.integratedmodelling.mca.promethee.intensity.UsualHFunction;
import org.integratedmodelling.mca.promethee.ranking.PrometheeIIRanking;
import org.integratedmodelling.mca.promethee.ranking.PrometheeIRanking;
import org.integratedmodelling.mca.promethee.ranking.RankCalculator;
import org.integratedmodelling.mca.promethee.ranking.RankingContext;

/**
 *
 * @author Edwin Boaz Soenaryo
 */
public class PrometheeModel {

    public PrometheeModel(double[][] performances, double[] weights,
            int[] operations, int[] criterionTypes, int rankingMethod) {

        this.performances = performances;
        this.weights = weights;
        this.operations = operations;
        this.criterionTypes = criterionTypes;
        this.rankingMethod = rankingMethod;
        computed = false;
    }

    public void compute() {
        int ccnt = performances.length;
        int acnt = performances[0].length;
        criterionIntensities = new double[ccnt][acnt][acnt];
        globalIntensities = new double[acnt][acnt];
        ranks = new int[acnt];
        IntensityCalculator intCalc = new IntensityCalculator();

        for (int c = 0; c < ccnt; c++) {
            HFunctionContext hf = createHFunction(criterionTypes[c],
                    operations[c], performances[c]);
            criterionIntensities[c] = intCalc.getIntensities(performances[c],
                    operations[c], hf);
            offset(globalIntensities, criterionIntensities[c], weights[c]);
        }
        normalize(globalIntensities, getSum(weights));

        RankCalculator rankCalc = new RankCalculator();
        RankingContext rc = createRankingContext(rankingMethod);
        ranks = rankCalc.getRanks(globalIntensities, rc);
        enteringFlows = rankCalc.getEnteringFlows();
        leavingFlows = rankCalc.getLeavingFlows();

        computed = true;
    }

    public double[] getEnteringFlows() {
        return enteringFlows;
    }

    public double[] getLeavingFlows() {
        return leavingFlows;
    }

    public int[] getRanks() {
        if (computed) {
            return ranks;
        } else {
            throw new UnsupportedOperationException("Not yet computed.");
        }
    }

    public double[][] getGlobalIntensities() {
        if (computed) {
            return globalIntensities;
        } else {
            throw new UnsupportedOperationException("Not yet computed.");
        }
    }

    public double[][] getCriterionIntensities(int criterionIndex) {
        if (computed) {
            return criterionIntensities[criterionIndex];
        } else {
            throw new UnsupportedOperationException("Not yet computed.");
        }
    }

    private HFunctionContext createHFunction(int criterionType, int operation,
            double[] performances) {

        // Calculate the population standard deviation
        double stdev = getStDev(performances, getMean(performances));

        // Create a H-function for the criterion
        if (criterionType == PrometheeConstants.USUAL) {
            return new HFunctionContext(new UsualHFunction());
        } else if (criterionType == PrometheeConstants.QUASI) {
            return new HFunctionContext(new QuasiHFunction(stdev));
        } else if (criterionType == PrometheeConstants.LINEAR) {
            return new HFunctionContext(new LinearHFunction(stdev));
        } else if (criterionType == PrometheeConstants.LEVEL) {
            double q = stdev * 2.0d / 3.0d;
            double p = q * 2.0d;
            return new HFunctionContext(new LevelHFunction(q, p));
        } else if (criterionType == PrometheeConstants.QUASILINEAR) {
            double q = stdev * 2.0d / 3.0d;
            double p = q * 2.0d;
            return new HFunctionContext(new QuasiLinearHFunction(q, p));
        } else if (criterionType == PrometheeConstants.GAUSSIAN) {
            return new HFunctionContext(new GaussianHFunction(stdev));
        } else {
            throw new UnsupportedOperationException("Illegal criterion type");
        }
    }

    private double getSum(double[] values) {
        int count = values.length;
        double sum = 0.0;

        // Calculate the mean
        for (int i = 0; i < count; i++) {
            sum += values[i];
        }
        return sum;
    }

    private double getMean(double[] values) {
        return getSum(values) / values.length;
    }

    private double getStDev(double[] values, double mean) {
        int count = values.length;
        double sum = 0.0;

        // Calculate population standard deviation
        sum = 0.0;
        for (int i = 0; i < count; i++) {
            sum += Math.pow((values[i] - mean), 2);
        }
        return Math.sqrt(sum / count);
    }

    private void offset(double[][] dest, double[][] src, double mult) {
        int cnt1 = dest.length;
        int cnt2 = dest[0].length;

        for (int i = 0; i < cnt1; i++) {
            for (int j = 0; j < cnt2; j++) {
                dest[i][j] += src[i][j] * mult;
            }
        }
    }

    private void normalize(double[][] values, double totalWeight) {
        if (totalWeight == 1.0) {
            return;
        } else {
            int cnt1 = values.length;
            int cnt2 = values[0].length;
            for (int i = 0; i < cnt1; i++) {
                for (int j = 0; j < cnt2; j++) {
                    values[i][j] /= totalWeight;
                }
            }
        }
    }

    private RankingContext createRankingContext(int rankingMethod) {
        if (rankingMethod == PrometheeConstants.PROMETHEE_I) {
            return new RankingContext(new PrometheeIRanking());
        } else if (rankingMethod == PrometheeConstants.PROMETHEE_II) {
            return new RankingContext(new PrometheeIIRanking());
        } else {
            throw new UnsupportedOperationException("Illegal ranking method");
        }
    }
    private double[][] performances; // Performance matrix
    private int[] operations; // Operation codes of each criterion
    private int[] criterionTypes; // H-function to be used on each criterion
    private double[] weights; // Weights of each criterion
    private boolean computed;
    private int rankingMethod;
    private double[][][] criterionIntensities;
    private double[][] globalIntensities;
    private int[] ranks;
    private double[] enteringFlows;
    private double[] leavingFlows;
}
