package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.Model;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class MeasurementModel extends DefaultDynamicAbstractModel {

	String unitSpecs = null;
	
	
	public void setUnits(Object unitSpecs) {
		this.unitSpecs = unitSpecs.toString();
	}
	
	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {

		IInstance med = null;
		
		if (!isResolved()) {
			med = 
				Model.resolveObservable(
						observable, 
						kbox, 
						getCompatibleObservationType(session),
						session);
			
			if (med == null) {
				throw new ThinklabValidationException(
						"cannot find unresolved observation of " + 
						observable + 
						" in " + kbox);
			}
		} else if (mediated != null) {
			med = mediated.buildObservation(kbox, session);
		}
		
		Polylist def = Polylist.listNotNull(
				CoreScience.MEASUREMENT,
				unitSpecs.contains(" ") ?
						Polylist.list("measurement:value", unitSpecs) :
						Polylist.list("measurement:unit", unitSpecs),
				med == null ?
					null :
					Polylist.list("measurement:mediates", med),
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(getObservable())));
		
		return session.createObject(def);
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


	
}
