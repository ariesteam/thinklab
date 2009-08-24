package org.integratedmodelling.modelling.corescience;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IPersistentSet;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;
import clojure.lang.Keyword;

public class ClassificationModel extends DefaultStatefulAbstractModel {

	ArrayList<GeneralClassifier> classifiers = new ArrayList<GeneralClassifier>();	
	ArrayList<IConcept> concepts = new ArrayList<IConcept>();
	IConcept state = null;

	@Override
	public void validateMediatedModel(IModel model) throws ThinklabValidationException {
		if (! (
				(model instanceof CountModel) ||
				(model instanceof RankingModel) ||
				(model instanceof ClassificationModel) ||
				(model instanceof MeasurementModel))) {
			throw new ThinklabValidationException(
					"classification models can only mediate classifications, counts, rankings or measurements");
		}
	}

	public GeneralClassifier getClassifier(Object classifier) throws ThinklabException {

		GeneralClassifier ret = new GeneralClassifier();
		
		/*
		 * classifier can be:
		 * 
		 *  Number  (specific match)
		 *  Concept (concept to concept, using the reasoner)
		 * 	Vector  (numeric range, honoring :< :> :open :closed keywords)
		 *  List    (executable code, run after setting self to state)
		 *  Set     (set of values to choose from: final match is an OR on the contents)
		 */
		if (classifier instanceof Integer || classifier instanceof Double) {
			
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
			for (Object o = set.first(); o != null; ) {
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

		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add("modeltypes:ModeledClassification");
		arr.add(Polylist.list("observation:hasObservationClass", Polylist.list(state)));
		
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
