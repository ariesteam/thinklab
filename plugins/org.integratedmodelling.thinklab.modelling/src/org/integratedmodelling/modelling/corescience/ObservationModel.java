package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.DefaultAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

/**
 * All that is required from an identification is to build a dependency structure. Its state
 * may be an instance (same as its observable).
 * 
 * @author Ferdinando Villa
 *
 */
public class ObservationModel extends DefaultAbstractModel {

	@Override
	protected void validateMediatedModel(IModel model)
			throws ThinklabValidationException {
		
		/*
		 * TODO a fairly sophisticated mediation may actually take place - a real semantic
		 * mediation of observables - but that's for another stage.
		 */
		throw new ThinklabValidationException("no mediation is allowed in identifications");
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Observation();
	}

	@Override
	public IModel getConfigurableClone() {
		ObservationModel ret = new ObservationModel();
		ret.copy(this);
		return ret;
	}

	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException {
		return Polylist.list(CoreScience.Observation());
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
