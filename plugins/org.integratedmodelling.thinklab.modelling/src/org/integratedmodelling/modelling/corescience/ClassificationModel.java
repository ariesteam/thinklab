package org.integratedmodelling.modelling.corescience;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IPersistentSet;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.Keyword;

/**
 * Handles the classification form. 
 * 
 * @author Ferdinando
 *
 */
public class ClassificationModel extends DefaultDynamicAbstractModel {

	ArrayList<GeneralClassifier> classifiers = new ArrayList<GeneralClassifier>();	
	ArrayList<IConcept> concepts = new ArrayList<IConcept>();
	IConcept state = null;

	/* (non-Javadoc)
	 * @see org.integratedmodelling.modelling.DefaultStatefulAbstractModel#copy(org.integratedmodelling.modelling.DefaultStatefulAbstractModel)
	 */
	
	@Override
	public String toString() {
		return ("classification(" + getObservable() + ")");
	}

	@Override
	public void validateMediatedModel(IModel model) throws ThinklabValidationException {
		if (! (
				(model instanceof CountModel) ||
				(model instanceof RankingModel) ||
				(model instanceof CategorizationModel) ||
				(model instanceof ClassificationModel) ||
				(model instanceof MeasurementModel))) {
			throw new ThinklabValidationException(
					"classification models can only mediate classifications, counts, categorizations, rankings or measurements");
		}
	}

	public GeneralClassifier getClassifier(Object classifier) throws ThinklabException {

		GeneralClassifier ret = new GeneralClassifier();
		
		/*
		 * classifier can be:
		 * 
		 *  Number  (specific match)
		 *  String  (specific match)
		 *  Concept (concept to concept, using the reasoner)
		 * 	Vector  (numeric range, honoring :< :> :open :closed keywords)
		 *  List    (executable code, run after setting self to state)
		 *  Set     (set of values to choose from: final match is an OR on the contents)
		 */
		if (classifier == null) {
			
			ret.setNil();
			
		} else if (classifier instanceof Integer || classifier instanceof Double) {
			
			ret.setNumber(classifier);
			
		} else if (classifier instanceof IPersistentVector) {
			
			IPersistentVector vec = (IPersistentVector) classifier;
			int cnt = vec.count();
			Double b1 = null;
			Double b2 = null;
			boolean inclusiveLeft = true;
			boolean inclusiveRight = false;
			boolean gotKw = false;
			boolean gotOne = false;
		
			for (int i = 0; i < cnt; i++) {
				
				Object o = vec.nth(i);
				
				if (o instanceof Keyword) {
					
					if (o.toString().equals(":<")) {
						gotOne = true;
					} else if (o.toString().equals(":>")) {
						gotOne = true;						
					} else if (o.toString().equals(":exclusive")) {
						if (gotKw)
							inclusiveRight = false;
						else
							inclusiveLeft = false;
						gotKw = true;
					} else if (o.toString().equals(":inclusive")) {
						if (gotKw)
							inclusiveRight = true;
						else
							inclusiveLeft = true;
						gotKw = true;						
					} else {
						throw new ThinklabValidationException(
								"invalid key in interval classifier " + 
								classifier +
								": only :>, :<, :inclusive and :exclusive are admitted");					
					}
				} else if (o instanceof Integer) {
					if (!gotOne) b1 = (double)((Integer)o);
					else b2 = (double)((Integer)o);
					gotOne = true;
				}  else if (o instanceof Double) {
					if (!gotOne) b1 = (double)((Double)o);
					else b2 = (double)((Double)o);
					gotOne = true;
				}
			}
			
			ret.setInterval(new IntervalValue(b1, b2, !inclusiveLeft, !inclusiveRight));
			
		} else if (classifier instanceof IPersistentSet) {
			
			ISeq set = ((IPersistentSet) classifier).seq();
			while (set != null) {
				Object o = set.first();
				ret.addClassifier(getClassifier(o));
				set = set.rest();
			}

		} else if (classifier instanceof ISeq) {
			
			/*
			 * TODO must pass Clojure class proxy to be stored here - needs to be handled in 
			 * 	clj
			 * code to be execd, to be passed back to Clojure at runtime
			 */
			
		} else if (classifier.toString().equals(":otherwise")) {
	
			/*
			 * catch-all
			 */
			ret.setCatchAll();
			
		} else if (classifier instanceof String) {
			
			/*
			 * match value from classified numeric datasource, e.g. categorical raster
			 */
			ret.setString((String)classifier);
			
		} else {
			
			/*
			 * convert to string and see if it's a concept
			 */
			IConcept c = KnowledgeManager.get().retrieveConcept(classifier.toString());
			
			
			if (c == null) {
				throw new ThinklabValidationException(
						"invalid classifier " + 
						classifier +
						": should be a range vector, a number, matching closure, or a set of " +
						"valid classifiers");
			}	
			ret.setConcept(c);
		}
		
		return ret;
		
	}
	
	public void addClassifier(Object classifier, Object concept) throws ThinklabException {

		GeneralClassifier cl = getClassifier(classifier);
		IConcept c = 
			concept instanceof IConcept ? 
					(IConcept)concept : 
						KnowledgeManager.get().requireConcept(concept.toString());
					
		classifiers.add(cl);
		concepts.add(c);
	}

	/**
	 * This one checks if all classifiers are the discretization of a continuous distribution. 
	 * If so, it ranks them in order and returns an array of breakpoints that define the 
	 * continuous distribution they represent. If the classifiers are not like that, it 
	 * returns null.
	 *  
	 *  This does not touch or rank the concepts. If the concepts have a ranking (such as the
	 *  lexicographic ranking found in Metadata.rankConcepts() it is the user's responsibility
	 *  that the concepts and the ranges make sense together.
	 *  
	 * @return
	 */
	public static double[] computeDistributionBreakpoints(Collection<GeneralClassifier> cls) {
	
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
			if (Double.compare(pd.getFirst(), last) != 0) {
				// FIXME this should be debug output at most, it could be perfectly OK, but it's an easy
				// error to make.
				ModellingPlugin.get().logger().warn("disjoint intervals on " + pd.getFirst() + " and " + last);
				return null;
			}
			ret[i++] = pd.getFirst();
			last = pd.getSecond();
			if (n == ranges.size() -1)
				ret[i++] = last;
		}
				
		return ret;
	}
	
	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		
		try {
			return KnowledgeManager.get().requireConcept("modeltypes:ModeledClassification");
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state;
	}

	@Override
	public IModel getConfigurableClone() {
		ClassificationModel ret = new ClassificationModel();
		ret.copy(this);
		// we can share these
		ret.classifiers = this.classifiers;
		ret.concepts = this.concepts;
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException {

		if (state == null)
			state = KnowledgeManager.get().getLeastGeneralCommonConcept(concepts);

		if (state /* still */ == null)
			state = observable;
				
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add(dynSpecs == null ? "modeltypes:ModeledClassification" : "modeltypes:DynamicClassification");
		arr.add(Polylist.list(CoreScience.HAS_CONCEPTUAL_SPACE, Polylist.list(state)));
		
		if (id != null) {
			arr.add(Polylist.list(CoreScience.HAS_FORMAL_NAME, id));			
		}
		
		if (dynSpecs != null) {
			arr.add(Polylist.list("modeltypes:hasStateFunction", dynSpecs));
			arr.add(Polylist.list("modeltypes:hasExpressionLanguage", 
					this.lang.equals(language.CLOJURE) ? "clojure" : "mvel"));
		}

		double[] breakpoints = computeDistributionBreakpoints(classifiers);		
		if (breakpoints != null) {
			arr.add(Polylist.list(
					"modelTypes:encodesContinuousDistribution",
					MiscUtilities.printVector(breakpoints)));
		}
		
		if (!isMediating())
			arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, this.observableSpecs));
		
		for (int i = 0; i < classifiers.size(); i++) {
			arr.add(Polylist.list(
						"modeltypes:hasClassifier", 
						concepts.get(i) + "->" + classifiers.get(i)));
		}
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
