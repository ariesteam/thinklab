package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.modelling.DefaultStatefulAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

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

	public void addClassifier(Object classifier, Object concept) {

		System.out.println("got classifier " + classifier.getClass() + ": " + classifier + " for " + concept);
	}
	
	
	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
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

}
