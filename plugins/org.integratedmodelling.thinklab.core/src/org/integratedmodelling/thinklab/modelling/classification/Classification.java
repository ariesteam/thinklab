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
package org.integratedmodelling.thinklab.modelling.classification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.collections.Triple;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticAnnotation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassification;
import org.integratedmodelling.thinklab.api.modelling.classification.IClassifier;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.Value;

/**
 * Reference implementation for IClassification. Also holds the global catalog of user-defined
 * orderings taken from project properties, which needs to be initialized on a project-to-project
 * basis by passing the properties to loadPredefinedOrdering() using a project action.
 * 
 * @author Ferd
 *
 */
public class Classification implements IClassification, IConceptualizable {

	private IConcept _cSpace = null;
	ArrayList<Pair<IClassifier, IConcept>> _classifiers = 
		new ArrayList<Pair<IClassifier,IConcept>>();
	private boolean _initialized = false;
	private boolean _hasNilClassifier = false;
	private boolean _hasZeroCategory = false;
	private boolean _isBoolean;
	private IConcept _trueCategory;
	private Type    _typeHint = null;
	private HashMap<IConcept, Integer> _ranks;
	private Type    _type = null;
	
	private static HashMap<IConcept,String> orderMap =
		new HashMap<IConcept, String>();

	// --- data for the lexicographic sort algorithm -----------------------------
	
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

	
	// --- public API below -----------------------------------------
	
	@Override
	public SemanticAnnotation conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize(IConcept cSpace, Type typeHint)
			throws ThinklabValidationException {
		this._cSpace = cSpace;
		this._typeHint = typeHint;
	}
	
	public void addClassifier(IClassifier classifier, IConcept concept) {
		_classifiers.add(new Pair<IClassifier,IConcept>(classifier, concept));
	}

	@Override
	public IConcept classify(Object o) {
		
		if (!_initialized) 
			analyzeClassification();
		
		if (o instanceof Number && Double.isNaN(((Number)o).doubleValue()))
			o = null;
		
		if (o == null && !_hasNilClassifier)
			return null;

		for (Pair<IClassifier, IConcept> p : _classifiers) {
			if (p.getFirst().classify(o))
				return p.getSecond();
		}
				
		return null;
	}
	
	@Override
	public int getRank(IConcept concept) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double[] getNumericRange(IConcept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDistributionBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IClassifier> getClassifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IConcept> getConceptOrder() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// --- tough stuff below ----------------------------------------------------
	
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
	public Pair<double[], IConcept[]> computeDistributionBreakpoints(
			IConcept observable, Collection<Classifier> cls,
			List<IConcept> classes) throws ThinklabValidationException {

		if (cls.size() < 1)
			return null;

		double[] ret = null;

		ArrayList<Triple<Double, Double, IConcept>> ranges = new ArrayList<Triple<Double, Double, IConcept>>();

		int i = 0;
		for (Classifier c : cls) {
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
			if (typeIs(Type.ORDERED_RANGE_MAPPING) || observable.is(KnowledgeManager.OrderedRangeMapping())
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


	private boolean typeIs(Type type) {
		return _typeHint != null && _typeHint.equals(type);
	}

	private void analyzeClassification()  {
		
		/*
		 * we have no guarantee that the universal classifier, if there,
		 * will be last, given that it may come from an OWL multiproperty where
		 * the orderding isn't guaranteed.
		 * 
		 * scan the classifiers and if we have a universal classifier make sure
		 * it's the last one, to avoid problems.
		 */
		int unidx = -1; int iz = 0;
		for (Pair<IClassifier, IConcept> cls : _classifiers) {
			if (cls.getFirst().isUniversal()) {
				unidx = iz;
			}
			iz++;
		}
		
		if (unidx >= 0 && unidx < _classifiers.size() -1) { 
			ArrayList<Pair<IClassifier, IConcept>> nc =
				new ArrayList<Pair<IClassifier,IConcept>>();
			for (iz = 0; iz < _classifiers.size(); iz++) {
				if (iz != unidx)
					nc.add(_classifiers.get(iz));
			}
			nc.add(_classifiers.get(unidx));
			_classifiers = nc;
		}
		
		/*
		 * check if we have a nil classifier; if we don't we don't bother classifying
		 * nulls and save some work.
		 */
		for (Pair<IClassifier, IConcept> cl : _classifiers) {
			if (cl.getFirst().isNil()) {
				this._hasNilClassifier = true;
				break;
			}
		}
			
		IConcept[] rnk = null;
		
		/*
		 * remap the values to ranks and determine how to rewire the input
		 * if necessary, use classifiers instead of lexicographic order to
		 * infer the appropriate concept order
		 */
		ArrayList<Classifier> cla = new ArrayList<Classifier>();
		ArrayList<IConcept> con = new ArrayList<IConcept>();
		for (Pair<IClassifier, IConcept> op : _classifiers) {
			cla.add((Classifier) op.getFirst());
			con.add(op.getSecond());
		}

		Pair<double[], IConcept[]> pd;
		try {
			pd = computeDistributionBreakpoints(_cSpace, cla, con);
		} catch (ThinklabValidationException e) {
			throw new ThinklabRuntimeException(e);
		}
		if (pd != null) {
			if (pd.getSecond()[0] != null) {
				rnk = pd.getSecond();
			}
		}
		
		this._ranks = rankConcepts(_cSpace);
		
		if (rnk != null) {	
			
			/*
			 * recompute ranks as requested and substitute
			 */
			int start = _hasZeroCategory ? 0 : 1;
			HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
			for (IConcept r : rnk)
				ret.put(r, new Integer(start++));
			this._ranks = ret;
		}
	}


	/**
	 * Produce the lexical ranking of the concept passed, using a ranking method
	 * that depends on the concept (or the type hint passed if any). If we are told
	 * to order the concepts and we have no help from the classifiers, try using
	 * lexicographic ranking. Honor any configuration for specific concepts that
	 * may specify or override the "natural" ordering.
	 * 
	 * @param type
	 * @param datasource
	 * @return
	 */
	public HashMap<IConcept, Integer> rankConcepts(IConcept type) {

		ArrayList<Pair<IConcept, Integer>> lexicalRank = new ArrayList<Pair<IConcept, Integer>>();

		boolean gotNo = false;
		boolean isBoolean = false;
		boolean isInterval = false;
		boolean isRanking = false;

		this._trueCategory = null;

		if (Value.isPOD(type))
			return null;

		/*
		 * check if we have any predefined ordering; if so, use that and return
		 */
		if (orderMap.containsKey(type)) {
		
			String[] order = orderMap.get(type).split(",");
			int start = 
					(order[0].startsWith("No") || order[0].endsWith("Absent")) ?
							0 : 1;
		
			HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
			int n = start;
			for (String s : order) {
				String c = type.getConceptSpace() + ":" + s;
				ret.put(KnowledgeManager.getConcept(c), n++);
			}
		
			this._hasZeroCategory = start == 0;	
			return ret;
		}
	
		/*
		 * if presence-absence, map the "No*" or "notpresent" to 0 and the other
		 * to 1, then return. Must be two concepts at most.
		 */
		if (typeIs(Type.BOOLEAN_RANKING) || type.is(KnowledgeManager.BooleanRanking())) {
			
			_type = Type.BOOLEAN_RANKING;
			
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
				this._trueCategory = c;
			}

			isBoolean = true;
		}
			
		} else if ( typeIs(Type.ORDERED_RANKING) ||
				typeIs(Type.ORDERED_RANGE_MAPPING) ||
				type.is(KnowledgeManager.OrdinalRanking()) ||
				type.is(KnowledgeManager.OrderedRangeMapping())) {

			if (typeIs(Type.ORDERED_RANKING) || type.is(KnowledgeManager.OrdinalRanking()))
				_type = Type.ORDERED_RANKING;
			else
				_type = Type.ORDERED_RANGE_MAPPING;

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
			for (IConcept cc : type.getChildren()) {
				if (!cc.isAbstract())
					ch.add(cc);
			}
			String[] cnames = new String[ch.size()];
			int i = 0;
			HashMap<IConcept, Integer> ret = new HashMap<IConcept, Integer>();
			for (IConcept c : ch) {
				cnames[i++] = c.toString();
				ret.put(c, i);
			}
		
			_type = Type.UNORDERED;
			
			// nothing left to do
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

		this._hasZeroCategory = gotNo || lexicalRank.get(0).getFirst().getLocalName().startsWith("No");
		if (isBoolean) {
			_type = Type.BOOLEAN_RANKING;
		}		

		return ret;
}


	public static void loadPredefinedOrderings(Properties properties) throws ThinklabException {
		
		String propertyPrefix = "thinklab.ordering.";
		int ln = propertyPrefix.length();
		for (Object k : properties.keySet()) {
			if (k.toString().startsWith(propertyPrefix)) {
				String cString = k.toString().substring(ln);
				cString = cString.replaceAll("-",":");
				IConcept kconc = KnowledgeManager.get().requireConcept(cString);
				orderMap.put(kconc, properties.getProperty(k.toString()));
			}
		}
	}

	@Override
	public void define(SemanticAnnotation conceptualization) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


}
