package org.integratedmodelling.corescience.metadata;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.InputSerializer;
import org.integratedmodelling.utils.OutputSerializer;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Triple;

/**
 * Just a holder of metadata ID strings.
 * 
 * @author Ferdinando Villa
 * 
 */
public class Metadata extends HashMap<String, Object> {

	private static final long serialVersionUID = 1265732119608093598L;

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
	public static final String CONTINUOS_DISTRIBUTION_BREAKPOINTS = "continuous_dist_breakpoints";
	public static final String ACTUAL_DATA_RANGE = "actual_data_range";
	public static final String ACTUAL_IMAGE_RANGE = "actual_image_range";
	public static final String HAS_NODATA_VALUES = "has_nodata_values";
	public static final String HAS_DATA_VALUES = "has_data_values";
	public static final String ZERO_IS_NODATA = "zero_is_nodata";

	/**
	 * The theoretical (allowed) data range, either from the distribution
	 * breakpoints, from original metadata (not implemented yet) or from the
	 * actual data range if none is specified. Should be used to define the
	 * legend.
	 */
	public static final String THEORETICAL_DATA_RANGE = "theoretical_data_range";
	public static final String THEORETICAL_IMAGE_RANGE = "theoretical_image_range";
	public static final String IMAGE_LEVELS = "image_levels";
	public static final String DATA_TYPE = "data_type";

	private static final int ORDINAL_RANKING = 1;
	private static final int CONTINUOUS_RANGE_MAPPING = 2;
	private static final int BOOLEAN_RANKING = 3;
	private static final int UNORDERED_CLASSIFICATION = 4;

	/**
	 * array of string names of all concepts the data MAY represent. If it's
	 * there, the categorical information does not encode rankings of any kind -
	 * boolean, range or or ordinal.
	 */
	public static final String CATEGORIES = "categories";
	public static final String COLORMAP = "colormap";

	// double[] arrays containing the extremes of the likely range for the data,
	// if applicable.
	public static final String RANGE_MIN = "range-min";
	public static final String RANGE_MAX = "range-max";
	public static final String AGGREGATED_MIN = "aggregated-min";
	public static final String AGGREGATED_MAX = "aggregated-max";
	public static final String AGGREGATED_TOTAL = "aggregated-total";
	public static final String IMAGE_TO_CLASS_OFFSET = "image-to-class-offset";
	public static final String DEFINING_MODEL = "defining-model"; // the model
																	// that
																	// generated
																	// this obs

	// see Measurement for the meaning, only set in corescience if the state
	// comes from a measurement
	public static final String PHYSICAL_NATURE = "physical-nature";

	public static final String DEFINING_QUERY = "defining-query";

	// classifiers are a list of pair<iconcept, genericclassifier>
	public static final String CLASSIFIERS = "classifiers";

	public Metadata(Metadata metadata) {
		try {
			merge(metadata);
		} catch (ThinklabValidationException e) {
			// will not happens
		}
	}

	public Metadata() {
		// TODO Auto-generated constructor stub
	}

	public static boolean isOrdinalRanking(Metadata state) {
		Integer dataType = (Integer) state.get(DATA_TYPE);
		return dataType == null ? false : dataType == ORDINAL_RANKING;
	}

	public static boolean isContinuousRangeMapping(Metadata state) {
		Integer dataType = (Integer) state.get(DATA_TYPE);
		return dataType == null ? false : dataType == CONTINUOUS_RANGE_MAPPING;
	}

	public static boolean isBooleanRanking(Metadata state) {
		Integer dataType = (Integer) state.get(DATA_TYPE);
		return dataType == null ? false : dataType == BOOLEAN_RANKING;
	}

	public static boolean isUnorderedClassification(Metadata state) {
		Integer dataType = (Integer) state.get(DATA_TYPE);
		return dataType == null ? false : dataType == UNORDERED_CLASSIFICATION;
	}

	public static boolean isContinuous(Metadata state) {
		Boolean ret = (Boolean) state.get(CONTINUOUS);
		return (ret != null) && ret;
	}

	public static boolean hasZeroCategory(Metadata state) {
		Boolean ret = (Boolean) state.get(HASZERO);
		return (ret != null) && ret;
	}

	public static boolean hasNoDataValues(IState state) {
		if (state.getMetadata().get(HAS_NODATA_VALUES) == null)
			analyzeData(state);
		return (Boolean) state.getMetadata().get(HAS_NODATA_VALUES);
	}

	public static boolean hasDataValues(IState state) {
		if (state.getMetadata().get(HAS_NODATA_VALUES) == null)
			analyzeData(state);
		return (Boolean) state.getMetadata().get(HAS_NODATA_VALUES);
	}

	public static double[] getDataRange(IState state) {
		if (state.getMetadata().get(HAS_NODATA_VALUES) == null)
			analyzeData(state);
		return (double[]) state.getMetadata().get(ACTUAL_DATA_RANGE);
	}

	public static HashMap<IConcept, Integer> getClassMappings(Metadata state) {
		return (HashMap<IConcept, Integer>) state.get(RANKING);
	}

	public static void analyzeData(IState state) {

		double[] data = null;
		boolean nodata = true;

		try {
			data = state.getDataAsDoubles();
		} catch (ThinklabValueConversionException e) {
		}

		boolean hasNaNs = false;
		boolean isReal = false;

		if (data != null) {

			int len = data.length;

			/*
			 * compute actual min/max
			 */
			Double min = null;
			Double max = null;

			for (int i = 0; i < len; i++) {
				if (!Double.isNaN(data[i])) {

					if (min == null) {
						min = data[i];
					} else {
						if (data[i] < min)
							min = data[i];
					}
					if (max == null) {
						max = data[i];
					} else {
						if (data[i] > max)
							max = data[i];
					}

					if (!isReal && (data[i] - Math.rint(data[i]) != 0))
						isReal = true;

				} else {
					hasNaNs = true;
				}
			}

			if (min != null && max != null) {
				state.getMetadata().put(ACTUAL_DATA_RANGE, new double[] { min, max });
				state.getMetadata().put(BOOLEAN, new Boolean(
						(min == 0 && max == 1 && isReal)));
			}
			nodata = (min != null || max != null);
		}

		state.getMetadata().put(HAS_NODATA_VALUES, new Boolean(hasNaNs));
		state.getMetadata().put(HAS_DATA_VALUES, new Boolean(nodata));

	}

	public static class MetadataSerializer extends OutputSerializer {

		class RankingWriter implements ObjectWriter {

			@Override
			public void writeObject(Object o) throws ThinklabIOException {
				if (o == null) {
					writeInteger(0);
					return;
				}
				HashMap<IConcept, Integer> r = (HashMap<IConcept, Integer>) o;
				writeInteger(r.size());
				for (Entry<IConcept, Integer> ek : r.entrySet()) {
					writeString(ek.getKey() == null ? null : ek.getKey()
							.toString());
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
			public HashMap<IConcept, Integer> readObject()
					throws ThinklabException {

				int size = readInteger();
				if (size == 0)
					return null;

				HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();

				for (int i = 0; i < size; i++) {
					String conc = readString();
					int val = readInteger();
					ret.put(conc == null ? null : KnowledgeManager.get()
							.requireConcept(conc), val);
				}

				return ret;
			}
		}

		public MetadataDeserializer(InputStream in) {
			super(in);
		}

		public HashMap<IConcept, Integer> readRankings()
				throws ThinklabException {
			return (HashMap<IConcept, Integer>) readObject(new RankingReader());
		}
	}

	/*
	 * these are recognized as ordinal prefixes. In order for an order to be
	 * recognized, all concepts must be children of thinklab-core:OrdinalRanking
	 * and their name must start with one of these prefixes.
	 */
	static String[] orderNarrative = { "^No[A-Z].*", "^Not[A-Z].*",
			"^Minimal[A-Z].*", "^ExtremelyLow[A-Z].*",
			"^ExtremelySmall[A-Z].*", "^VeryLow[A-Z].*", "^VerySmall[A-Z].*",
			"^Low[A-Z].*", "^Small[A-Z].*", "^Medium[A-Z].*",
			"^Moderate[A-Z].*", "^Partial[A-Z].*", "^ModeratelyHigh[A-Z].*",
			"^MediumHigh[A-Z].*", "^ModeratelyLarge[A-Z].*",
			"^MediumLarge[A-Z].*", "^High[A-Z].*", "^Large[A-Z].*",
			"^Full[A-Z].*", "^VeryHigh[A-Z].*", "^VeryLarge[A-Z].*",
			"^Extreme[A-Z].*", "^ExtremelyHigh[A-Z].*",
			"^ExtremelyLarge[A-Z].*" };

	/*
	 * only recognize the "no" case, the rest is a yes case
	 */
	static String[] booleanNarrative = { "^No[A-Z].*", "^Not[A-Z].*",
			".*Absent.*", ".*NotPresent.*" };

	/**
	 * Produce the lexical ranking of the immediate children of the passed
	 * concept, which must be a ranking according to the core ontology.
	 * 
	 * @param type
	 * @return
	 */
	static public HashMap<IConcept, Integer> rankConcepts(IConcept type) {
		return rankConcepts(type, null);
	}

	/**
	 * Produce the lexical ranking of the concept passed and add metadata to the
	 * datasource
	 * 
	 * @param type
	 * @param datasource
	 * @return
	 */
	public static HashMap<IConcept, Integer> rankConcepts(IConcept type,
			Metadata metadata) {
		ArrayList<Pair<IConcept, Integer>> lexicalRank = new ArrayList<Pair<IConcept, Integer>>();

		boolean gotNo = false;
		boolean isBoolean = false;
		boolean isInterval = false;
		boolean isRanking = false;
		IConcept truecase = null;

		if (Value.isPOD(type))
			return null;

		/*
		 * if presence-absence, map the "No*" or "notpresent" to 0 and the other
		 * to 1, then return. Must be two concepts at most.
		 */
		if (type.is(KnowledgeManager.BooleanRanking())) {

			if (metadata != null) {
				metadata.put(DATA_TYPE, new Integer(BOOLEAN_RANKING));
			}

			for (IConcept c : type.getChildren()) {

				if (c.isAbstract())
					continue;

				int i = 0;
				for (String rx : booleanNarrative) {
					if (c.getLocalName().matches(rx)) {
						lexicalRank.add(new Pair<IConcept, Integer>(c, i));
						gotNo = true;
						break;
					}
					i++;
				}

				// wasn't a no, insert as a higher value.
				if (i == booleanNarrative.length) {
					lexicalRank.add(new Pair<IConcept, Integer>(c, i + 1));
					truecase = c;
				}

				isBoolean = true;

			}
		} else if (type.is(KnowledgeManager.OrdinalRanking())
				|| type.is(KnowledgeManager.OrderedRangeMapping())) {

			if (metadata != null) {
				metadata.put(DATA_TYPE, type.is(KnowledgeManager
						.OrdinalRanking()) ? new Integer(ORDINAL_RANKING)
						: new Integer(CONTINUOUS_RANGE_MAPPING));
			}

			isRanking = true;

			for (IConcept c : type.getChildren()) {
				
				if (c.isAbstract())
					continue;
				
				int i = 0;
				for (String rx : orderNarrative) {
					if (c.getLocalName().matches(rx)) {
						lexicalRank.add(new Pair<IConcept, Integer>(c, i));
						break;
					}
					i++;
				}
			}
		} else {

			/*
			 * no ranking, we still have subclasses to remember and assign
			 * numbers to.
			 */
			Collection<IConcept> ch = new ArrayList<IConcept>();
			for (IConcept cc : type.getChildren())
				if (!cc.isAbstract())
					ch.add(cc);
			
			String[] cnames = new String[ch.size()];
			int i = 0;
			HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
			for (IConcept c : ch) {
				cnames[i++] = c.toString();
				ret.put(c, i);
			}
			if (metadata != null) {
				metadata.put(RANKING, ret);
				metadata.put(CATEGORIES, cnames);
				metadata.put(DATA_TYPE, new Integer(
						UNORDERED_CLASSIFICATION));
			}
			return ret;
		}

		if (lexicalRank.size() == 0)
			return null;

		/*
		 * sort concepts according to rank in lexical array to linearize rank
		 */
		Collections.sort(lexicalRank,
				new Comparator<Pair<IConcept, Integer>>() {

					@Override
					public int compare(Pair<IConcept, Integer> o1,
							Pair<IConcept, Integer> o2) {
						return o1.getSecond().compareTo(o2.getSecond());
					}
				});

		/*
		 * make final map using linear index for sorted categories. We start at
		 * 0 only if the ranking is a "no" ranking, otherwise at 1.
		 */
		HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
		int i = (gotNo || lexicalRank.get(0).getFirst().getLocalName()
				.startsWith("No")) ? 0 : 1;
		for (Pair<IConcept, Integer> p : lexicalRank) {
			ret.put(p.getFirst(), i++);
		}

		if (metadata != null) {

			metadata.put(RANKING, ret);
			metadata.put(HASZERO, new Boolean(gotNo
					|| lexicalRank.get(0).getFirst().getLocalName().startsWith(
							"No")));
			metadata.put(BOOLEAN, new Boolean(isBoolean));
			if (truecase != null) {
				metadata.put(TRUECASE, truecase);
			}
		}

		return ret;
	}

	/**
	 * Use this ranking for the concepts, but ensure that the lexicographical
	 * info are in.
	 * 
	 * @param rnk
	 * @param datasource
	 * @return
	 */
	public static HashMap<IConcept, Integer> rankConcepts(IConcept type,
			IConcept[] rnk, Metadata metadata) {

		rankConcepts(type, metadata);

		/*
		 * recompute ranks as requested and substitute
		 */
		int start = Metadata.hasZeroCategory(metadata) ? 0 : 1;
		HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
		for (IConcept r : rnk)
			ret.put(r, new Integer(start++));
		metadata.put(RANKING, ret);
		return ret;
	}

	/**
	 * This one checks if all classifiers are the discretization of a continuous
	 * distribution. If so, it ranks them in order and returns an array of
	 * breakpoints that define the continuous distribution they represent. If
	 * the classifiers are not like that, it returns null.
	 * 
	 * This does not touch or rank the concepts. If the concepts have a ranking
	 * (such as the lexicographic ranking found in Metadata.rankConcepts() it is
	 * the user's responsibility that the concepts and the ranges make sense
	 * together. We do, however, enforce that continuous ranges are propertly
	 * defined if the observable is the discretization of a continuous range.
	 * 
	 * @return null if we don't encode a continuous discretization; otherwise a
	 *         pair containing the breakpoints as a double[] (n+1) and a vector
	 *         of concepts in the order defined by the intervals (size n). If
	 *         the concept list was not passed, the concept array will be filled
	 *         with nulls.
	 * 
	 * @throws ThinklabValidationException
	 *             if the observable is a continuous range mapping but the
	 *             classification has disjoint intervals.
	 */
	public static Pair<double[], IConcept[]> computeDistributionBreakpoints(
			IConcept observable, Collection<GeneralClassifier> cls,
			List<IConcept> classes) throws ThinklabValidationException {

		if (cls.size() < 1)
			return null;

		double[] ret = null;

		ArrayList<Triple<Double, Double, IConcept>> ranges = new ArrayList<Triple<Double, Double, IConcept>>();

		int i = 0;
		for (GeneralClassifier c : cls) {
			if (!c.isInterval())
				return null;
			IntervalValue iv = c.getInterval();
			IConcept concept = classes == null ? null : classes.get(i++);
			double d1 = iv.isLeftInfinite() ? Double.NEGATIVE_INFINITY : iv
					.getMinimumValue();
			double d2 = iv.isRightInfinite() ? Double.POSITIVE_INFINITY : iv
					.getMaximumValue();
			ranges.add(new Triple<Double, Double, IConcept>(d1, d2, concept));
		}

		/*
		 * sort ranges so that they appear in ascending order
		 */
		Collections.sort(ranges,
				new Comparator<Triple<Double, Double, IConcept>>() {

					@Override
					public int compare(Triple<Double, Double, IConcept> o1,
							Triple<Double, Double, IConcept> o2) {

						if (Double.compare(o1.getFirst(), o2.getFirst()) == 0
								&& Double.compare(o1.getSecond(), o2
										.getSecond()) == 0)
							return 0;

						return o2.getFirst() >= o1.getSecond() ? -1 : 1;
					}
				});

		/*
		 * sorted vector of concepts
		 */
		IConcept[] cret = new IConcept[ranges.size()];
		for (int jc = 0; jc < ranges.size(); jc++)
			cret[jc] = ranges.get(jc).getThird();

		/*
		 * build vector from sorted array
		 */
		ret = new double[ranges.size() + 1];
		i = 0;
		double last = 0.0;
		ret[i++] = ranges.get(0).getFirst();
		last = ranges.get(0).getSecond();
		for (int n = 1; n < ranges.size(); n++) {

			Triple<Double, Double, IConcept> pd = ranges.get(n);
			/*
			 * we don't allow ordered range mappings to have disjoint intervals
			 */
			if (observable.is(KnowledgeManager.OrderedRangeMapping())
					&& Double.compare(pd.getFirst(), last) != 0) {
				throw new ThinklabValidationException(
						"disjoint intervals for ordered range mapping of "
								+ observable + ": " + pd.getFirst() + " -- "
								+ last);
			}
			ret[i++] = pd.getFirst();
			last = pd.getSecond();
			if (n == ranges.size() - 1)
				ret[i++] = last;
		}

		return new Pair<double[], IConcept[]>(ret, cret);
	}

	public static void serializeMetadata(Metadata metadata, OutputStream fop)
			throws ThinklabException {

		MetadataSerializer out = new MetadataSerializer(fop);

		// UNCERTAINTY (double[])
		// UNITS (string)
		// BOOLEAN (boolean)
		// HASZERO (boolean)
		// TRUECASE (concept)
		// RANKING (hasmap<concept, integer>)

		out.writeDoubles((double[]) metadata.get(UNCERTAINTY));
		out.writeString((String) metadata.get(UNITS));
		out.writeInteger(metadata.get(BOOLEAN) == null ? -1
				: ((Boolean) (metadata.get(BOOLEAN)) ? 1 : 0));
		out.writeInteger(metadata.get(HASZERO) == null ? -1
				: ((Boolean) (metadata.get(HASZERO)) ? 1 : 0));
		out.writeInteger(metadata.get(CONTINUOUS) == null ? -1
				: ((Boolean) (metadata.get(CONTINUOUS)) ? 1 : 0));
		IConcept truecase = (IConcept) metadata.get(TRUECASE);
		out.writeString(truecase == null ? null : truecase.toString());
		out.writeRankings(metadata.get(RANKING));

		// TODO new stuff missing
	}

	public static Metadata deserializeMetadata(InputStream fop)
			throws ThinklabException {

		Metadata ret = new Metadata();
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

	/**
	 * Remap double data from a datasource into integers that can be used to
	 * create an image. Set metadata so that the image can be annotated
	 * appropriately. Int values will not be more than 255.
	 * 
	 * Metadata fields that may be set are:
	 * 
	 * DATA_TYPE -> {"DISCRETE_RANKS", "CONTINUOUS", "CATEGORIES",
	 * "BOOLEAN_RANKING", "BOOLEAN_PROBABILITY"} ACTUAL_DATA_RANGE -> [min, max]
	 * (doubles) ACTUAL_IMAGE_RANGE -> [min, max] (integers) DISPLAY_RANGE ->
	 * [min, max] DISPLAY_CATEGORIES -> {category strings} (not there if
	 * continuous or boolean prob) DISPLAY_LEVELS -> Integer (0 - 256)
	 * 
	 * @return integer data or null if there's no chance to remap.
	 * @throws ThinklabValueConversionException
	 */
	public static int[] getImageData(IState state)
			throws ThinklabValueConversionException {

		double[] data = state.getDataAsDoubles();
		
		if (data == null)
			return null;
		
		int len = data.length;
		int[] idata = new int[len];

		double[] dataRange = Metadata.getDataRange(state);

		/*
		 * see if we have categories and redefine from there.
		 */
		HashMap<IConcept, Integer> ranking = (HashMap<IConcept, Integer>) state
				.getMetadata().get(RANKING);
		Boolean hasZeroRanking = (Boolean) state.getMetadata().get(HASZERO);
		if (hasZeroRanking == null)
			hasZeroRanking = false;
		Boolean continuous = (Boolean) state.getMetadata().get(CONTINUOUS);
		if (continuous == null)
			continuous = false;

		boolean needZeroInColormap = false;

		int offset = 0;

		int nlevels = 255;
		if (ranking != null && !continuous) {
			nlevels = ranking.size();
			if ((Metadata.hasNoDataValues(state) && !hasZeroRanking))
				nlevels++;

			if (!hasZeroRanking && !Metadata.hasNoDataValues(state)) {
				// useful levels will start at 1, so subtract 1 to map to
				// colormap properly
				offset = 1;
			} else {
				needZeroInColormap = true;
			}
		}

		state.getMetadata().put(IMAGE_TO_CLASS_OFFSET, new Integer(offset));

		/*
		 * compute the display data range in actual values from the semantics
		 */
		double expmin =
		// TODO honor metadata-driven min
		dataRange == null ? 0.0 : dataRange[0];
		double expmax =
		// TODO honor metadata-driven max
		dataRange == null ? 0.0 : dataRange[1];

		double[] distribution = (double[]) state
				.getMetadata().get(Metadata.CONTINUOS_DISTRIBUTION_BREAKPOINTS);
		if (distribution != null) {
			expmin = 0;
			expmax = distribution.length - 1;
		}

		state.getMetadata().put(THEORETICAL_DATA_RANGE,
				new double[] { expmin, expmax });

		int imin = 0, imax = 0;
		for (int i = 0; i < len; i++) {

			if (Double.isNaN(data[i]))
				idata[i] = 0;
			else {
				idata[i] = (ranking != null && !continuous) ? ((int) data[i] - offset)
						: (int) (((data[i] - expmin) / (expmax - expmin)) * (nlevels - 1));
			}

			if (i == 0) {
				imin = idata[0];
				imax = idata[0];
			} else {
				if (idata[i] > imax)
					imax = idata[i];
				if (idata[i] < imin)
					imin = idata[i];
			}
		}

		state.getMetadata().put(IMAGE_LEVELS, nlevels);
		state.getMetadata().put(ZERO_IS_NODATA, new Boolean(needZeroInColormap));
		state.getMetadata().put(ACTUAL_IMAGE_RANGE, new int[] { imin, imax });

		return idata;
	}

	public void merge(Metadata metadata) throws ThinklabValidationException {
		// TODO must merge same fields intelligently and solve or reject
		// discrepancies
		for (String key : metadata.keySet())
			if (!containsKey(key))
				this.put(key, metadata.get(key));
	}

}
