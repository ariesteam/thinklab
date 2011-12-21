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
package org.integratedmodelling.geospace.districting.algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.integratedmodelling.geospace.districting.interfaces.IDistrictingAlgorithm;
import org.integratedmodelling.geospace.districting.utils.DistrictingResults;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class KMeansAlgorithm implements IDistrictingAlgorithm
{
    public DistrictingResults createDistricts(double[][] dataset, int initialK) throws ThinklabException
    {
	int l = dataset.length;
	int n = dataset[0].length;
	int k = initialK;
	double stoppingThreshold = 0.2;

	DistrictingResults results = new DistrictingResults();

        int[] typeset = null;
	ArrayList<Integer> pointsPerCluster = null;
	ArrayList<Double>[] centroids = selectInitialCentroids(l, n, k, dataset);
	ArrayList<Double>[] prevCentroids = null;
	ArrayList<Double>[] variances = null;
	int iterations = 0;

	System.out.println("Beginning stabilization...");

	while (centroidsAreMoving(l, k, centroids, prevCentroids, stoppingThreshold)) {
	    prevCentroids = centroids;
	    typeset = collectPointsByCluster(l, n, k, centroids, dataset);
	    pointsPerCluster = countPointsPerCluster(n, k, typeset);
	    centroids = recomputeCentroids(l, n, k, typeset, pointsPerCluster, dataset);
	    pointsPerCluster = removeEmptyClusters(pointsPerCluster);
	    k = centroids[0].size();
	    iterations++;
	}
	variances = computeVariancesByCluster(l, n, k, centroids, typeset, pointsPerCluster, dataset);

	System.out.println("Completed after " + iterations + " total iterations.");

	results.setTypeset(typeset);
	results.setPointsPerCluster(pointsPerCluster);
	results.setCentroids(centroids);
	results.setStdevs(computeStdevs(l, k, variances));
	results.setIterations(iterations);
	results.setInitialK(initialK);
	results.setFinalK(k);
	results.setDatasetVariance(computeDatasetVariance(l, n, dataset));
	return results;
    }

    public DistrictingResults createDistricts(double[][] dataset, int initialK, double stoppingThreshold,
					      double varianceRatio, double membershipRatio,
					      double separationRatio) throws ThinklabException
    {
	// Just call the simpler createDistricts and ignore the extra parameters.
	return createDistricts(dataset, initialK);
    }

    private double[] computeDatasetVariance(int l, int n, double[][] dataset)
    {
	double[] datasetMean = new double[l];
	double[] datasetVariance = new double[l];
	for (int i=0; i<l; i++) {
	    for (int j=0; j<n; j++) {
		datasetMean[i] += dataset[i][j];
	    }
	    datasetMean[i] /= n;
	    for (int j=0; j<n; j++) {
		datasetVariance[i] += Math.pow(dataset[i][j] - datasetMean[i], 2);
	    }
	    datasetVariance[i] /= n;
	}
	return datasetVariance;
    }

    private ArrayList<Double>[] selectInitialCentroids(int l, int n, int k, double[][] dataset)
    {
	ArrayList<Double>[] centroids = new ArrayList[l];
	for (int i=0; i<l; i++) {
	    centroids[i] = new ArrayList<Double>();
	}

	Random randomNumberGenerator = new Random();
	int[] datasetIndeces = new int[k];

	for (int i=0; i<k; i++) {
	    datasetIndeces[i] = randomNumberGenerator.nextInt(n);
	    for (int j=0; j<i; j++) {
		if (datasetIndeces[j] == datasetIndeces[i]) {
		    i--;
		    break;
		}
	    }
	}

	for (int i=0; i<l; i++) {
	    for (int j=0; j<k; j++) {
		centroids[i].add(dataset[i][datasetIndeces[j]]);
	    }
	}

	return centroids;
    }

    private boolean centroidsAreMoving(int l, int k, ArrayList<Double>[] centroids,
				       ArrayList<Double>[] prevCentroids, double stoppingThreshold)
    {
    	if (centroids == null || prevCentroids == null || centroids[0].size() != prevCentroids[0].size()) {
	    return true;
	}
    	for (int i=0; i<k; i++) {
    		double centroidMovement = 0;
    		for (int j=0; j<l; j++) {
		    centroidMovement += Math.pow(centroids[j].get(i) - prevCentroids[j].get(i), 2);
    		}
    		if (centroidMovement > stoppingThreshold) {
    			return true;
    		}
    	}
    	return false;
    }

    private int[] collectPointsByCluster(int l, int n, int k, ArrayList<Double>[] centroids, double[][] dataset)
    {
	int[] typeset = new int[n];

	for (int a=0; a<n; a++) {
	    double minDistance = 1.0e9;
	    for (int b=0; b<k; b++) {
		double distance = 0;
		for (int c=0; c<l; c++) {
		    distance += Math.pow(dataset[c][a] - centroids[c].get(b), 2);
		}

		if (distance < minDistance) {
		    minDistance = distance;
		    typeset[a] = b;
		}
	    }
	}

	return typeset;
    }

    private ArrayList<Integer> countPointsPerCluster(int n, int k, int[] typeset)
    {
	ArrayList<Integer> pointsPerCluster = new ArrayList<Integer>();
	for (int i=0; i<k; i++) {
	    pointsPerCluster.add(0);
	}
	for (int i=0; i<n; i++) {
	    pointsPerCluster.set(typeset[i], 1 + pointsPerCluster.get(typeset[i]));
	}
	return pointsPerCluster;
    }

    private ArrayList<Double>[] recomputeCentroids(int l, int n, int k, int[] typeset,
						   ArrayList<Integer> pointsPerCluster, double[][] dataset)
    {
	ArrayList<Double>[] centroids = new ArrayList[l];
	for (int i=0; i<l; i++) {
	    centroids[i] = new ArrayList<Double>();
	}

	for (int i=0; i<l; i++) {
	    for (int j=0; j<k; j++) {
		centroids[i].add(0.0);
	    }
	    for (int j=0; j<n; j++) {
		centroids[i].set(typeset[j], centroids[i].get(typeset[j]) + dataset[i][j]);
	    }
	    for (int j=0; j<k; j++) {
		if (pointsPerCluster.get(j) == 0) {
		    centroids[i].set(j, null);
		} else {
		    centroids[i].set(j, centroids[i].get(j) / pointsPerCluster.get(j));
		}
	    }
	    Iterator iter = centroids[i].iterator();
	    while (iter.hasNext()) {
		if (iter.next() == null) {
		    iter.remove();
		}
	    }
	}

	return centroids;
    }

    private ArrayList<Integer> removeEmptyClusters(ArrayList<Integer> pointsPerCluster)
    {
	ArrayList<Integer> newPointsPerCluster = (ArrayList<Integer>)pointsPerCluster.clone();
	Iterator iter = newPointsPerCluster.iterator();
	while (iter.hasNext()) {
	    if ((Integer)iter.next() == 0) {
		iter.remove();
	    }
	}
	return newPointsPerCluster;
    }

    private ArrayList<Double>[] computeVariancesByCluster(int l, int n, int k, ArrayList<Double>[] centroids,
							  int[] typeset, ArrayList<Integer> pointsPerCluster,
							  double[][] dataset)
    {
	ArrayList<Double>[] variances = new ArrayList[l];
	for (int i=0; i<l; i++) {
	    variances[i] = new ArrayList<Double>();
	}
	for (int i=0; i<l; i++) {
	    for (int j=0; j<k; j++) {
		variances[i].add(0.0);
	    }
	    for (int j=0; j<n; j++) {
		variances[i].set(typeset[j],
				 variances[i].get(typeset[j])
				 + Math.pow(dataset[i][j] - centroids[i].get(typeset[j]), 2));
	    }
	    for (int j=0; j<k; j++) {
		variances[i].set(j, variances[i].get(j) / pointsPerCluster.get(j));
	    }
	}
	return variances;
    }

    private ArrayList<Double>[] computeStdevs(int l, int k, ArrayList<Double>[] variances)
    {
	ArrayList<Double>[] stdevs = new ArrayList[l];
	for (int i=0; i<l; i++) {
	    stdevs[i] = new ArrayList<Double>();
	}
	for (int i=0; i<l; i++) {
	    for (int j=0; j<k; j++) {
		stdevs[i].add(Math.sqrt(variances[i].get(j)));
	    }
	}
	return stdevs;
    }

}
