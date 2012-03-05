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
package org.integratedmodelling.thinklab.geospace.districting.utils;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.geospace.exceptions.ThinklabDistrictingException;

public class DistrictingResults {

	private int[] typeset;
	private ArrayList<Integer> pointsPerCluster;
	private ArrayList<Double>[] centroids;
	private ArrayList<Double>[] stdevs;
	private int iterations;
	private int initialK;
	private int finalK;
	private double[] datasetVariance;
	private String[] variableNames;

	public void setTypeset(int[] t) {
		typeset = t;
	}

	public void setPointsPerCluster(ArrayList<Integer> p) {
		pointsPerCluster = p;
	}

	public void setCentroids(ArrayList<Double>[] c) {
		centroids = c;
	}

	public void setStdevs(ArrayList<Double>[] s) {
		stdevs = s;
	}

	public void setIterations(int i) {
		iterations = i;
	}

	public void setInitialK(int k) {
		initialK = k;
	}

	public void setFinalK(int k) {
		finalK = k;
	}

	public void setDatasetVariance(double[] d) {
		datasetVariance = d;
	}

	public void setVariableNames(String[] v)
			throws ThinklabDistrictingException {
		if (v.length != centroids.length) {
			throw new ThinklabDistrictingException(
					"Invalid variable names list: must have same length "
							+ "as number of variables in districting results.");
		}
		variableNames = v;
	}

	public int[] getTypeset() {
		return typeset;
	}

	public ArrayList<Integer> getPointsPerCluster() {
		return pointsPerCluster;
	}

	public ArrayList<Double>[] getCentroids() {
		return centroids;
	}

	public double[] getCentroids(int districtIndex) {
		
		double[] ret = new double[centroids.length];
		
		for (int i = 0; i < centroids.length; i++)
			ret[i] = getCentroids()[i].get(districtIndex);
		
		return ret;
	}
	
	public ArrayList<Double>[] getStdevs() {
		return stdevs;
	}

	public int getIterations() {
		return iterations;
	}

	public int getInitialK() {
		return initialK;
	}

	public int getFinalK() {
		return finalK;
	}

	public double[] getDatasetVariance() {
		return datasetVariance;
	}

	public String[] getVariableNames() {
		return variableNames;
	}

}
