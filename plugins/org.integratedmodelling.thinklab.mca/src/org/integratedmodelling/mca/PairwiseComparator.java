package org.integratedmodelling.mca;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

/**
 * Helper class to define and obtain absolute quantitative criteria weights from a matrix of
 * pairwise comparison values.
 * 
 * If a ranking matrix is available, the static getRankings methods can be used directly without
 * instantiating an object. Otherwise, a PairwiseComparator object can be instantiated and used
 * to facilitate creating the matrix by calling rankPair on each couple. The other half of the
 * matrix is filled in automatically, and the matrix is initialized to neutral (all 1s).
 * 
 * @author Ferdinando Villa
 *
 */
public class PairwiseComparator {

	double[][] rankings = null;
	
	
	public PairwiseComparator(int nCriteria) {

		rankings = new double[nCriteria][nCriteria];

		/* initialize to neutral */
		for (int i = 0; i < nCriteria; i++) {
			for (int j = 0; j < nCriteria; j++) {
				rankings[i][j] = 1.0;
			}
		}
	}
	
	void rankPair(int critA, int critB, double ranking) {
		
		if (rankings == null)
			throw new ThinklabRuntimeException("PairwiseComparison: no rankings defined, please use static methods.");			
		
		if (critA != critB) {
			rankings[critA][critB] = ranking;
			rankings[critB][critA] = 1.0/ranking;
		}
	}

	/**
	 * Use this one if you initialized the object with the constructor and used rankPair to
	 * set the rankings.
	 * 
	 * @return
	 */
	public double[] getRankings() {

		double[] ret = null;
		
		if (rankings == null)
			throw new ThinklabRuntimeException("PairwiseComparison: no rankings defined, please use static methods.");
		
		try {
			ret = getRankings(rankings);
		} catch (ThinklabException e) {
			// won't happen if we built it using rankPair
		}

		return ret;
	}
	
	public static double[] getRankings(double[][] ranks) throws ThinklabException {

		double[] ret = new double[ranks.length];
		
		/*
		 * check for antisymmetric matrix
		 */
		for (int i = 0; i < ranks.length; i++) {
			
			if (ranks[i][i] != 1.0)
				throw new ThinklabValidationException("PairwiseComparator: input matrix is not antisymmetric");
			
			for (int j = 0; j < i; j++) {
				if ((ranks[i][j] + ranks[j][i]) != 0.0) {
					throw new ThinklabValidationException("PairwiseComparator: input matrix is not antisymmetric");
				}
			}
		}
		
		/*
		 * compute eigenvalues and eigenvectors
		 */
		
		/*
		 * find dominant eigenvector
		 */
		
		/* 
		 * normalize dominant eigenvector into final ranking
		 */
		
		return ret;
	}
	
}
