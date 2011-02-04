package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.CoreScience.PhysicalNature;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@InstanceImplementation(concept="modeltypes:ProbabilisticMeasurement")
public class ProbabilisticMeasurement extends ModeledClassification {

	String unitSpecs = null;

	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {

		IConcept[] vmaps = new IConcept[classifiers.size()];
		for (int i = 0; i < classifiers.size(); i++)
			vmaps[i] = classifiers.get(i).getSecond();
		
		IState ret =
			new CategoricalDistributionDatasource(cSpace, size, vmaps, classifiers, (ObservationContext) context);

		Unit unit = new Unit(unitSpecs);
		ret.getMetadata().put(Metadata.UNIT, unit);
		ret.getMetadata().put(Metadata.UNIT_SPECS, unitSpecs);
		
		PhysicalNature physicalNature = 
			CoreScience.getPhysicalNature(getObservableClass());		
		ret.getMetadata().put(Metadata.PHYSICAL_NATURE, physicalNature);

		return ret;
	
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		if (unitSpecs == null) {
			IValue v = i.get(CoreScience.HAS_UNIT);
			if (v != null)
				unitSpecs = v.toString().trim();
		}

		/*
		 * validate observable. Must be physical property or a count with unitless units.
		 */
		if (!observable.is(CoreScience.PHYSICAL_PROPERTY)) {
				throw new ThinklabValidationException(
					"measurements can only be of physical properties: " + 
						observable.getDirectType());
		}

		
	}

}
