package org.integratedmodelling.mca;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;

/**
 * Driver class to run a whole MCA analysis from definition to results. It makes using 
 * Evamix and PairwiseComparator very simple, but it is not required to use it if you
 * know your way around the analysis.
 *
 * Steps to use:
 * 
 * 1. declare your criteria and alternatives;
 * 2. set alternative values for each criterion;
 * 3. set weights, either directly or using the pairwise method; don't mix calls to these.
 * 4. run analysis;
 * 5. use inquiry methods to obtain results.
 * 
 * Any operation in step 2 and 3 will freexe the declaration functions, which will throw
 * an exception if called after that. Operations 2 and 3 can be called again after running
 * the analysis, and will make incremental changes to the existing situation so that different
 * scenarios can be analyzed.
 * 
 * @author Ferdinando Villa
 *
 */
public class MCA {

	final public static String ORDINAL = "Ordinal";
	final public static String BINARY = "Binary";
	final public static String RATIO = "Ratio";
	
	public class Alternative {
		
		String name;
		double[] values = null;
	}
	
	public class Criterion {
	
		String name;
		String type;
		boolean benefit;
		double weight;
	}
	
	ArrayList<Criterion> criteria = new ArrayList<Criterion>();
	ArrayList<Alternative> alternatives = new ArrayList<Alternative>();
	PairwiseComparator pairwise = null;

	// we collect weights here, unless the pairwise comparator is used.
	double weights[] = null;

	// if true, we have started setting values, so we cannot declare anything new.
	boolean frozen = false;

	// store the index in the main array of both alternatives and criteria for speed
	HashMap<String, Integer> altIndex = new HashMap<String, Integer>();
	HashMap<String, Integer> critIndex = new HashMap<String, Integer>();
	
	// results are kept here after runEvamix() is called.
	Evamix.Results results = null;
	
	private int getAltIndex(String alternative) {
		return altIndex.get(alternative);
	}

	private int getCritIndex(String criterion) {
		return critIndex.get(criterion);
	}
	
	public void declareCriterion(String criterionName, String type, boolean isBenefit) {

		if (frozen)
			throw new ThinklabRuntimeException("MCA: cannot add criteria when data input has begun");

		if (!isBenefit && !type.equals(RATIO)) 
			throw new ThinklabRuntimeException("MCA: cost criteria can only be quantitative");
		
		Criterion c = new Criterion();
		c.name = criterionName;
		c.type = type;
		c.benefit = isBenefit;
		
		criteria.add(c);
		critIndex.put(criterionName, criteria.size() - 1);
	}
	
	public void declareAlternative(String alternativeName) {
	
		if (frozen)
			throw new ThinklabRuntimeException("MCA: cannot add alternatives when data input has begun");
		
		Alternative a = new Alternative();
		a.name = alternativeName;
		
		alternatives.add(a);
		altIndex.put(alternativeName, alternatives.size() - 1);
	}
	
	public void setCriterionWeight(String criterionName, double criterionWeight) {
	
		frozen = true;
		
		if (pairwise != null) {
			throw new ThinklabRuntimeException("MCA: cannot mix pairwise weighting with direct weighting");
		}
		
		if (weights == null) {
			weights = new double[criteria.size()];
		}
		
		weights[getCritIndex(criterionName)] = criterionWeight;
	}

	public void compareCriteria(String criterion1, String criterion2, double comparativeWeight) {

		frozen = true;

		if (weights != null) {
			throw new ThinklabRuntimeException("MCA: cannot mix pairwise weighting with direct weighting");
		}
	
		if (pairwise == null) {
			pairwise = new PairwiseComparator(criteria.size());
		}
		
		pairwise.rankPair(getCritIndex(criterion1), getCritIndex(criterion2), comparativeWeight);
	}
	
	public void setCriterionValue(String alternativeName, String criterionName, double value) {
		
		Alternative alt = alternatives.get(getAltIndex(alternativeName));
		int crit = getCritIndex(criterionName);
		
		if (alt.values == null)
			alt.values = new double[criteria.size()];
		
		alt.values[crit] = value;
	}
	
	public Evamix.Results runEvamix() throws ThinklabException {
			
		if (weights == null && pairwise != null) {
			weights = pairwise.getRankings();
		}
		
		double[][] data = new double[alternatives.size()][criteria.size()];
		boolean benefit[] = new boolean[criteria.size()];
		String types[] = new String[criteria.size()];
		String cnames[] = new String[criteria.size()];
		
		int i = 0;
		for (Criterion c : criteria) {
			benefit[i] = c.benefit;
			types[i] = c.type;
			cnames[i] = c.name; 
			i++;
		}
		
		String anames[] = new String[alternatives.size()];
		
		i = 0;
		for (Alternative a : alternatives) {
			anames[i] = a.name;
			for (int j = 0; j < criteria.size(); j++) {
				data[i][j] = a.values[j];
			}
			i++;
		}
		
		results = 
			Evamix.run(data, weights, types, benefit, anames, cnames); 
		
		return results;
	}
	
	public double getAlternativeRanking(String alternativeName) {
		
		if (results == null)
			throw new ThinklabRuntimeException("MCA: cannot report results before runEvamix() is called");
		
		double score = results.evamix_scores[getAltIndex(alternativeName)];
		
		/*
		 * TODO - what do we want to do here, just report the score or rank.
		 */
		return score;
	}
	
	public static void main(String[] args) {
		
		MCA mca = new MCA();
		
		mca.declareAlternative("Villa");
		mca.declareAlternative("Costanza");
		mca.declareAlternative("Boumans");
		
		mca.declareCriterion("Fama", ORDINAL, true);
		mca.declareCriterion("Cattiveria", RATIO, false);
		mca.declareCriterion("Simpatia", RATIO, true);
		
		mca.setCriterionValue("Villa", "Fama", 10);
		mca.setCriterionValue("Villa", "Cattiveria", 0);
		mca.setCriterionValue("Villa", "Simpatia", 7);

		mca.setCriterionValue("Costanza", "Fama", 8);
		mca.setCriterionValue("Costanza", "Cattiveria", 10);
		mca.setCriterionValue("Costanza", "Simpatia", 0);

		mca.setCriterionValue("Boumans", "Fama", 1);
		mca.setCriterionValue("Boumans", "Cattiveria", 2);
		mca.setCriterionValue("Boumans", "Simpatia", 7);

		// nice guy scenario
		mca.setCriterionWeight("Fama", 0);
		mca.setCriterionWeight("Cattiveria", 10);
		mca.setCriterionWeight("Simpatia", 8);
	
		System.out.println("*** Nice guy scenario ***\n");
		try {
			mca.runEvamix().dump();
		} catch (ThinklabException e) {
			e.printStackTrace();
		}
		
		// famous guy scenario
		mca.setCriterionWeight("Fama", 10);
		mca.setCriterionWeight("Cattiveria", 1);
		mca.setCriterionWeight("Simpatia", 4);
	
		System.out.println("\n*** Famous guy scenario ***\n");
		try {
			mca.runEvamix().dump();
		} catch (ThinklabException e) {
			e.printStackTrace();
		}
		
	}
}
