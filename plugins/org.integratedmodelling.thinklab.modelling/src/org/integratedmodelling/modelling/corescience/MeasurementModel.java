package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

public class MeasurementModel extends DefaultDynamicAbstractModel {

	String unitSpecs = null;
	
	
	public void setUnits(Object unitSpecs) {
		this.unitSpecs = unitSpecs.toString();
	}
	
	@Override
	public Polylist buildDefinition() throws ThinklabException {

		/*
		 * TODO choose observation class according to derivative, probability etc.
		 */
		Polylist def = Polylist.listNotNull(
				CoreScience.MEASUREMENT,
				unitSpecs.contains(" ") ?
						Polylist.list("measurement:value", unitSpecs) :
						Polylist.list("measurement:unit", unitSpecs),
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(getObservable())));
		
		return def;
	}


	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		return CoreScience.Measurement();
	}

	@Override
	public void validateMediatedModel(IModel model) throws ThinklabValidationException {
		if (! (model instanceof MeasurementModel)) {
			throw new ThinklabValidationException("measurement models can only mediate other measurements");
		}
	}

	@Override
	protected Object validateState(Object state)
			throws ThinklabValidationException {
		return state instanceof Double ? state : Double.parseDouble(state.toString());
	}

	@Override
	public IModel getConfigurableClone() {
		// TODO Auto-generated method stub
		MeasurementModel ret = new MeasurementModel();
		ret.copy(this);
		ret.unitSpecs = unitSpecs;
		return ret;
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


	
}
