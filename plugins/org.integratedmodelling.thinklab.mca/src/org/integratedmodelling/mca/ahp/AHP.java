/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.mca.ahp;

import org.integratedmodelling.mca.evamix.Evamix;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

/**
 * Helper class to define and obtain absolute quantitative criteria weights from a matrix of
 * pairwise comparison values.
 * 
 * If a ranking matrix is available, the static getRankings methods can be used directly without
 * instantiating an object. Otherwise, a PairwiseComparator object can be instantiated and used
 * to facilitate creating the matrix by calling rankPair on each couple. The other half of the
 * matrix is filled in automatically, and the matrix is initialized to neutral (all 1s).
 * 
 * TODO calculate and handle inconsistency (requires boring table from Saaty 1980)
 * TODO implement other weight extraction methods (eigenvalue method is problematic at times)
 * 
 * @author Ferdinando Villa
 *
 */
public class AHP {

	double[][] rankings = null;
	int size = 0;
	
	public AHP(int nCriteria) {

		size = nCriteria;
		rankings = new double[nCriteria][nCriteria];

		/* initialize to neutral */
		for (int i = 0; i < nCriteria; i++) {
			for (int j = 0; j < nCriteria; j++) {
				rankings[i][j] = 1.0;
			}
		}
	}
	
	public void rankPair(int critA, int critB, double ranking) {
		
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
	
	public double[][] getPairwiseMatrix() {
		return rankings;
	}
	
	public static double[] getRankings(double[][] ranks) throws ThinklabException {

		double[] ret = new double[ranks.length];
				
		/*
		 * compute eigenvalues and eigenvectors
		 */
		EigenvalueDecomposition eig = 
			new EigenvalueDecomposition(new DenseDoubleMatrix2D(ranks));
		
		/*
		 * find eigenvector corresponding to dominant real eigenvalue
		 */
		int m = 0;
		double[] vv = eig.getRealEigenvalues().toArray();

		for (int i = 1; i < vv.length; i++) {
			if (vv[i] > vv[m])
				m = i;
		}
		
		for (int i = 0; i < ranks.length; i++) {
			ret[i] = eig.getV().getQuick(m, i);
		}
		
		/* 
		 * normalize dominant eigenvector to sum up to 1.0
		 */
		double min = Evamix.min(ret);
		double max = Evamix.max(ret);		
		double sum = 0.0;
		
		for (int i = 0; i < ranks.length; i++) {
			ret[i] = (ret[i] - max + min);
			sum += ret[i];
		}
		for (int i = 0; i < ranks.length; i++) {
			ret[i] /= sum;
		}
		
		return ret;
	}
	
	public int size() {
		return size;
	}
	
	public static void main(String[] args) {
		
		AHP ahp = new AHP(3);
		
		// expert choice gives .067, .344, .589 for 0,1,2
		ahp.rankPair(0, 1, 9);
		ahp.rankPair(0, 2, 5);
		ahp.rankPair(1, 2, 3);
		
		double[] r = ahp.getRankings();
		double[][] m = ahp.getPairwiseMatrix();
		
		for (int i = 0; i < ahp.size(); i++) {
			
			for (int j = 0; j < ahp.size(); j++) {				
				System.out.print(m[i][j] + " ");
			}
			System.out.println("\t" + r[i]);	
		}
		
	}
	
}
