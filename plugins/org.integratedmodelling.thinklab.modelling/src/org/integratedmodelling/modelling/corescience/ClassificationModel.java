package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IPersistentSet;
import clojure.lang.IPersistentVector;
import clojure.lang.ISeq;

public class ClassificationModel extends DefaultStatefulAbstractModel {

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

	public GeneralClassifier getClassifier(Object classifier) {

		GeneralClassifier ret = null;
		
		/*
		 * classifier can be:
		 * 
		 *  Number  (specific match)
		 *  Concept (concept to concept, using the reasoner)
		 * 	Vector  (numeric range, honoring :< :> :open :closed keywords)
		 *  List    (executable code, run after setting self to state)
		 *  Set     (set of values to choose from: final match is an OR on the contents)
		 */
		if (classifier instanceof Integer || classifier instanceof Integer) {
			
			/*
			 * match values
			 */
			
		} else if (classifier instanceof IPersistentVector) {
			
			/*
			 * match ranges
			 */
			
		} else if (classifier instanceof IPersistentSet) {
			
			/*
			 * make one classifier per element
			 */
			
		} else if (classifier instanceof ISeq) {
			
			/*
			 * code to be execd
			 */
			
		} else {
			
			/*
			 * convert to string and see if it's a concept
			 */
		}
		
		return ret;

		
	}
	
	public void addClassifier(Object classifier, Object concept) throws ThinklabException {

		System.out.println("got classifier " + classifier.getClass() + ": " + classifier + " for " + concept);
		
		GeneralClassifier cl = getClassifier(classifier);
		IConcept c = 
			concept instanceof IConcept ? 
					(IConcept)concept : 
						KnowledgeManager.get().requireConcept(concept.toString());
					
		cl.setClass(c);
		
	}
	
	

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IModel getConfigurableClone() {

		ClassificationModel ret = new ClassificationModel();
		ret.copy(this);
		// TODO add class specs
		return ret;
	}

	@Override
	public Polylist buildDefinition() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
