package org.integratedmodelling.corescience.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

/**
 * Just a holder of metadata ID strings.
 * @author Ferdinando Villa
 *
 */
public class Metadata {

	public static final String UNCERTAINTY = "uncertainty";
	public static final String UNITS = "units";
	public static final String LEGEND = "legend";
	public static final String RANGES = "ranges";
	
	/*
	 * these are recognized as ordinal prefixes. In order for an order
	 * to be recognized, all concepts must be children of 
	 * thinklab-core:OrdinalRanking
	 * and their name must start with one of these prefixes.
	 */
	static String[] orderNarrative = {
			"^No[A-Z].*",
			"^Not[A-Z].*",
			"^ExtremelyLow[A-Z].*",
			"^ExtremelySmall[A-Z].*",
			"^VeryLow[A-Z].*",
			"^VerySmall[A-Z].*",
			"^Low[A-Z].*",
			"^Small[A-Z].*",
			"^Medium[A-Z].*",
			"^Moderate[A-Z].*",
			"^Partial[A-Z].*",
			"^ModeratelyHigh[A-Z].*",
			"^MediumHigh[A-Z].*",
			"^ModeratelyLarge[A-Z].*",
			"^MediumLarge[A-Z].*",
			"^High[A-Z].*",
			"^Large[A-Z].*",
			"^Full[A-Z].*",
			"^VeryHigh[A-Z].*",
			"^VeryLarge[A-Z].*",
			"^ExtremelyHigh[A-Z].*",
			"^ExtremelyLarge[A-Z].*"
	};
	
	/*
	 * only recognize the "no" case, the rest is a yes case
	 */
	static String[] booleanNarrative = {
		"^No[A-Z].*",
		"^Not[A-Z].*",
		".*Absent.*",      
		".*NotPresent.*"
	};

	/**
	 * Produce the lexical ranking of the immediate children of the
	 * passed concept, which must be a ranking according to
	 * the core ontology.
	 * 
	 * @param type
	 * @return
	 */
	static public HashMap<IConcept, Integer> rankConcepts(IConcept type) {

		ArrayList<Pair<IConcept, Integer>> lexicalRank =
			new ArrayList<Pair<IConcept,Integer>>();
		boolean gotNo = false;
		
		/*
		 * if presence-absence, map the "No*" or "notpresent" to 0 and 
		 * the other to 1, then return. Must be two concepts at most.
		 */
		if (type.is(KnowledgeManager.BooleanRanking())) {

			for (IConcept c : type.getChildren()) {
				int i = 0;
				for (String rx : booleanNarrative) {
					if (c.getLocalName().matches(rx)) {
						lexicalRank.add(new Pair<IConcept,Integer>(c,i));
						gotNo = true;
						break;
					}
					i++;
				}
				// wasn't a no, insert as a higher value.
				if (i == booleanNarrative.length)
					lexicalRank.add(new Pair<IConcept,Integer>(c,i+1));
			}
		} else if (
				type.is(KnowledgeManager.OrdinalRanking()) ||
				type.is(KnowledgeManager.OrderedRangeMapping())
				) {
			for (IConcept c : type.getChildren()) {
				int i = 0;
				for (String rx : orderNarrative) {
					if (c.getLocalName().matches(rx)) {
						lexicalRank.add(new Pair<IConcept,Integer>(c,i));
						break;
					}
					i++;
				}
			}
		}
		
		if (lexicalRank.size() == 0)
			return null;
		
		/*
		 * sort concepts according to rank in lexical array to
		 * linearize rank
		 */
		Collections.sort(lexicalRank, 
				new Comparator<Pair<IConcept, Integer>>() {

					@Override
					public int compare(Pair<IConcept, Integer> o1,
							Pair<IConcept, Integer> o2) {
						return o1.getSecond().compareTo(o2.getSecond());
					}
				}
		);
		
		/*
		 * make final map using linear index for sorted categories. We start at 0 only if the
		 * ranking is a "no" ranking, otherwise at 1.
		 */
		HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
		int i = 
			(gotNo ||
			 lexicalRank.get(0).getFirst().getLocalName().startsWith("No")) ? 
					0 : 1;
		for (Pair<IConcept, Integer> p : lexicalRank) {
			ret.put(p.getFirst(), i++);
		}
		
		// TODO remove
		System.out.println("ranked concepts: " + ret);
		
		return ret;
	}
	
	static public HashMap<Integer, Double> createOrderMapping(
			IConcept type,
			HashMap<IConcept, Integer> map) {
		// TODO analyze concepts, if any are found create the
		// order mapping.
		
		return null;
	}


}
