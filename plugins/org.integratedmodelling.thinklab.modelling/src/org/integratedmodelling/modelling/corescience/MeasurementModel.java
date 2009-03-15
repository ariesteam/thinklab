package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class MeasurementModel implements IModel {

	IModel mediated = null;
	String unitSpecs = null;
	IConcept observable = null;
	
	public MeasurementModel(Object observableOrModel, String unitSpecs) {
		
		if (observableOrModel instanceof IModel) {
			/*
			 * TODO check that it's a Measurement too
			 */
			this.mediated = (IModel) observableOrModel;
			this.observable = ((IModel)observableOrModel).getObservable();
		} else if (observableOrModel instanceof IConcept) {
			this.observable = (IConcept) observableOrModel;
		} else {
			
			/*
			 * TODO complain - internal error
			 */
		}
		this.unitSpecs = unitSpecs;
	}
	
	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {

		Polylist def = Polylist.list(
				CoreScience.MEASUREMENT,
				unitSpecs.contains(" ") ?
						Polylist.list("measurement:value", unitSpecs) :
						Polylist.list("measurement:unit", unitSpecs),
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(getObservable())));
		
		return session.createObject(def);
	}

	@Override
	public IConcept getObservable() {
		return observable;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Measurement();
	}

}
