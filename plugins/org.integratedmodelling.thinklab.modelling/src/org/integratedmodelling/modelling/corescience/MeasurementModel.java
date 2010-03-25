package org.integratedmodelling.modelling.corescience;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.DefaultDynamicAbstractModel;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class MeasurementModel extends DefaultDynamicAbstractModel {

	String unitSpecs = null;
	
	@Override
	public String toString() {
		return ("measurement(" + getObservable() + "," + unitSpecs + ")");
	}
	
	public void setUnits(Object unitSpecs) {
		this.unitSpecs = unitSpecs.toString();
		this.metadata.put(Metadata.UNITS, this.unitSpecs);
		this.metadata.put(Metadata.CONTINUOUS, Boolean.TRUE);
	}
	
	@Override
	public Polylist buildDefinition(IKBox kbox, ISession session) throws ThinklabException {

		/*
		 * choose observation class according to derivative, probability etc.
		 */
		Polylist def = Polylist.listNotNull(
				(dynSpecs == null ? CoreScience.MEASUREMENT : "modeltypes:DynamicMeasurement"),
				(id != null ? 
					Polylist.list(CoreScience.HAS_FORMAL_NAME, id) :
					null),
				(dynSpecs != null?
					Polylist.list("modeltypes:hasStateFunction", dynSpecs) :
					null),
				(dynSpecs != null?
					Polylist.list("modeltypes:hasExpressionLanguage", 
							this.lang.equals(language.CLOJURE) ? "clojure" : "mvel") :
					null),
				unitSpecs.contains(" ") ?
						Polylist.list("measurement:value", unitSpecs) :
						Polylist.list("measurement:unit", unitSpecs),
				(isMediating() ? 
						null :
						Polylist.list(
								CoreScience.HAS_OBSERVABLE,
								Polylist.list(getObservable()))));
		
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

	@Override
	protected void validateSemantics(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


	
}
