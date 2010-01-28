package org.integratedmodelling.corescience.metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.utils.InputSerializer;
import org.integratedmodelling.utils.OutputSerializer;
import org.integratedmodelling.utils.Pair;

import au.id.jericho.lib.html.OutputSegment;

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
	public static final String BOOLEAN = "boolean";
	public static final String RANKING = "ranking";
	public static final String CONTINUOUS = "continuous";
	public static final String ORDINAL = "ordinal";
	public static final String HASZERO = "haszero";
	public static final String TRUECASE = "truecase";
	public static final String CONTINUOS_DISTRIBUTION_BREAKPOINTS = 
			"continuous_dist_breakpoints";
	
	public static class MetadataSerializer extends OutputSerializer {
			
			class RankingWriter implements ObjectWriter {

				@Override
				public void writeObject(Object o) throws ThinklabIOException {
					if (o == null) {
						writeInteger(0);
						return;
					}
					HashMap<IConcept,Integer> r = (HashMap<IConcept, Integer>) o;
					writeInteger(r.size());
					for (Entry<IConcept, Integer> ek : r.entrySet()) {
						writeString(ek.getKey() == null ? null : ek.getKey().toString());
						writeInteger(ek.getValue());
					}
				}
			}
			
			public MetadataSerializer(OutputStream out) {
				super(out);
			}
			
			public void writeRankings(Object o) throws ThinklabException {
				writeObject(o, new RankingWriter());
			}
		}
	
	public static class MetadataDeserializer extends InputSerializer {
		
		class RankingReader implements ObjectReader {

			@Override
			public HashMap<IConcept,Integer> readObject() throws ThinklabException {

				int size = readInteger();
				if (size == 0)
					return null;
				
				HashMap<IConcept,Integer> ret = new HashMap<IConcept, Integer>();

				for (int i = 0; i < size; i++) {
					String conc = readString();
					int val = readInteger();
					ret.put(conc == null ? null : KnowledgeManager.get().requireConcept(conc), val);
				}
				
				return ret;
			}
		}
		
		public MetadataDeserializer(InputStream in) {
			super(in);
		}
		
		public HashMap<IConcept,Integer> readRankings() throws ThinklabException {
			return (HashMap<IConcept, Integer>) readObject(new RankingReader());
		}
	}
	 
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
		return rankConcepts(type, null);
	}
	

	/**
	 * Produce the lexical ranking of the concept passed and add metadata to the datasource
	 *
	 * @param type
	 * @param datasource
	 * @return
	 */
	public static HashMap<IConcept, Integer> rankConcepts(IConcept type, IState datasource) {
		ArrayList<Pair<IConcept, Integer>> lexicalRank =
			new ArrayList<Pair<IConcept,Integer>>();
		
		boolean gotNo = false;
		boolean isBoolean = false;
		boolean isInterval = false;
		boolean isRanking = false; 
		IConcept truecase = null;
		
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
				if (i == booleanNarrative.length) {
					lexicalRank.add(new Pair<IConcept,Integer>(c,i+1));
					truecase = c;
				}
				
				isBoolean = true;
				
			}
		} else if (
				type.is(KnowledgeManager.OrdinalRanking()) ||
				type.is(KnowledgeManager.OrderedRangeMapping())
				) {
			
			isRanking = true;
			
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
		
		if (datasource != null) {

			datasource.setMetadata(RANKING, ret);
			datasource.setMetadata(HASZERO, 
					new Boolean(gotNo || lexicalRank.get(0).getFirst().getLocalName().startsWith("No")));
			datasource.setMetadata(BOOLEAN, new Boolean(isBoolean));
			if (truecase != null) {
				datasource.setMetadata(TRUECASE, truecase);
			}
		}
		
		return ret;
	}


	/**
	 * This one checks if all classifiers are the discretization of a continuous distribution. 
	 * If so, it ranks them in order and returns an array of breakpoints that define the 
	 * continuous distribution they represent. If the classifiers are not like that, it 
	 * returns null.
	 *  
	 * This does not touch or rank the concepts. If the concepts have a ranking (such as the
	 * lexicographic ranking found in Metadata.rankConcepts() it is the user's responsibility
	 * that the concepts and the ranges make sense together. We do, however, enforce that continuous
	 * ranges are propertly defined if the observable is the discretization of a continuous range.
	 *  
	 * @return
	 * @throws ThinklabValidationException if the observable is a continuous range mapping but
	 * 		   the classification has disjoint intervals.
	 */
	public static double[] computeDistributionBreakpoints(
			IConcept observable, Collection<GeneralClassifier> cls) throws ThinklabValidationException {
	
		double[] ret = null;
		
		ArrayList<Pair<Double, Double>> ranges = new ArrayList<Pair<Double,Double>>();
	
		for (GeneralClassifier c : cls) {
			if (!c.isInterval())
				return null;
			IntervalValue iv = c.getInterval();
			double d1 = iv.isLeftInfinite() ?  Double.NEGATIVE_INFINITY : iv.getMinimumValue();
			double d2 = iv.isRightInfinite() ? Double.POSITIVE_INFINITY : iv.getMaximumValue();
			ranges.add(new Pair<Double,Double>(d1, d2));
		}
		
		/*
		 * sort ranges so that they appear in ascending order
		 */
		Collections.sort(ranges, new Comparator <Pair<Double, Double>>() {
	
			@Override
			public int compare(Pair<Double, Double> o1, Pair<Double, Double> o2) {
		
				if (Double.compare(o1.getFirst(), o2.getFirst()) == 0 &&
					Double.compare(o1.getSecond(), o2.getSecond()) == 0)
					return 0;
				
				return o2.getFirst() >= o1.getSecond() ?  -1 : 1;
			}
		});
		
		/*
		 * build vector from sorted array
		 */
		ret = new double[ranges.size() + 1];
		int i = 0; double last = 0.0;
		ret[i++] = ranges.get(0).getFirst();
		last = ranges.get(0).getSecond();
		for (int n = 1; n < ranges.size(); n++) {
		
			Pair<Double,Double> pd = ranges.get(n);
			/*
			 * we don't allow ordered range mappings to have disjoint intervals
			 */
			if (observable.is(KnowledgeManager.OrderedRangeMapping()) && 
				Double.compare(pd.getFirst(), last) != 0) {
				throw new ThinklabValidationException(
						"disjoint intervals for ordered range mapping of " +
						observable + ": " + pd.getFirst() + " -- " + last);
			}
			ret[i++] = pd.getFirst();
			last = pd.getSecond();
			if (n == ranges.size() -1)
				ret[i++] = last;
		}
				
		return ret;
	}


	public static void serializeMetadata(Properties metadata, OutputStream fop) throws ThinklabException {


		
		MetadataSerializer out = new MetadataSerializer(fop);

		// UNCERTAINTY (double[])
		// UNITS (string)
		// BOOLEAN (boolean)
		// HASZERO (boolean)
		// TRUECASE (concept)
		// RANKING (hasmap<concept, integer>)

		out.writeDoubles((double[]) metadata.get(UNCERTAINTY));
		out.writeString(metadata.getProperty(UNITS));
		out.writeInteger(
				metadata.get(BOOLEAN) == null ? 
						-1 : 
						((Boolean)(metadata.get(BOOLEAN)) ? 1 : 0));
		out.writeInteger(
				metadata.get(HASZERO) == null ? 
						-1 : 
						((Boolean)(metadata.get(HASZERO)) ? 1 : 0));
		out.writeInteger(
				metadata.get(CONTINUOUS) == null ? 
						-1 : 
						((Boolean)(metadata.get(CONTINUOUS)) ? 1 : 0));
		out.writeString(metadata.getProperty(TRUECASE));
		out.writeRankings(metadata.get(RANKING));
	}


	public static Properties deserializeMetadata(InputStream fop) throws ThinklabException {

		
		Properties ret = new Properties();
		MetadataDeserializer in = new MetadataDeserializer(fop);
		
		// UNCERTAINTY (double[])
		// UNITS (string)
		// BOOLEAN (boolean)
		// HASZERO (boolean)
		// TRUECASE (concept)
		// RANKING (hasmap<concept, integer>)
		
		double[] unc = in.readDoubles();
		if (unc != null)
			ret.put(UNCERTAINTY, unc);
		String units = in.readString();
		if (units != null)
			ret.put(UNITS, units);
		
		int bool = in.readInteger();
		if (bool >= 0)
			ret.put(BOOLEAN, bool == 0 ? Boolean.FALSE : Boolean.TRUE);
		bool = in.readInteger();
		if (bool >= 0)
			ret.put(HASZERO, bool == 0 ? Boolean.FALSE : Boolean.TRUE);
		bool = in.readInteger();
		if (bool >= 0)
			ret.put(CONTINUOUS, bool == 0 ? Boolean.FALSE : Boolean.TRUE);
		units = in.readString();
		if (units != null)
			ret.put(TRUECASE, KnowledgeManager.get().requireConcept(units));
		HashMap<IConcept, Integer> rank = in.readRankings();
		if (rank != null)
			ret.put(RANKING, rank);
			
		return ret;
	}


}
