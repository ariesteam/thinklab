package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.cmodels.MeasurementModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Polylist;

/**
 * Implementation for instances of measurements. Admits definition of simple "value unit" cases
 * through a single observation:value property.
 *  
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept="measurement:Measurement")
public class Measurement extends Observation implements IConceptualizable {
	
	String unitSpecs = null;
	String valueSpecs = null;
	
	@Override
	protected IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		
		/*
		 * if we get here, no unit model was given, so we must have passed unitSpecs; if not there,
		 * this will generate an exception
		 * 
		 * TODO catch the exception and generate an informative error message
		 */
		return new MeasurementModel(CoreScience.get().MeasurementModel(), unitSpecs);
	}

	@Override
	protected IDataSource<?> createMissingDatasource() throws ThinklabException {

		/*
		 * if we get here, no DS was given, so we must have value specs
		 */
// not really, ds may come later
//		if (valueSpecs == null && !isMediator()) {
//			throw new ThinklabValidationException(
//					"no value specifications given in measurement " +
//					" with no datasource associated");
//		}
		
		if (valueSpecs != null)
			((MeasurementModel)getConceptualModel()).setInlineValue(Double.parseDouble(valueSpecs));
		
		return null;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);

		// lookup defs - either unit and value or textual definition of both
		IValue v = i.get("observation:value");
		
		if (v != null) {
			
			String s = v.toString();
			int idx = s.indexOf(' ');
			
			if (idx >= 0) {
				valueSpecs = s.substring(0, idx).trim();
				unitSpecs = s.substring(idx+1).trim();
			} else {
				throw new ThinklabValidationException(
						"measurement value must contain numeric value and units: " + s);
			}
		} else {
			
			// may just have unit and link to datasource or mediated obs
			v = i.get("measurement:unit");
			if (v != null)
				unitSpecs = v.toString().trim();
			
		}
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		return Polylist.list(
				CoreScience.MEASUREMENT,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
						(getObservable() instanceof IConceptualizable) ? 
								((IConceptualizable)getObservable()).conceptualize() :
								getObservable().toList(null)),
				Polylist.list("measurement:unit", unitSpecs));
	}

}
