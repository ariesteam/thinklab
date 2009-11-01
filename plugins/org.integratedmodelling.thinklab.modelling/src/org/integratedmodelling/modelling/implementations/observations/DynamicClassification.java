package org.integratedmodelling.modelling.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Classification;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.modelling.data.adapters.ClojureAccessor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

@InstanceImplementation(concept="modeltypes:DynamicClassification")
public class DynamicClassification extends Classification {

	String code = null;
	
	public class DynamicClassificationModel extends ClassificationModel {

		/* (non-Javadoc)
		 * @see org.integratedmodelling.corescience.implementations.cmodels.MeasurementModel#getStateAccessor(org.integratedmodelling.thinklab.interfaces.knowledge.IConcept, org.integratedmodelling.corescience.interfaces.context.IObservationContext)
		 */
		@Override
		public IStateAccessor getStateAccessor(IConcept stateType,
				IObservationContext context) {
			return new ClojureAccessor(code, false);
		}
	}
		
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Observation#getConceptualModel()
	 */
	@Override
	public IConceptualModel getConceptualModel() throws ThinklabException {
		return new DynamicClassificationModel();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.implementations.observations.Measurement#initialize(org.integratedmodelling.thinklab.interfaces.knowledge.IInstance)
	 */
	@Override
	public void initialize(IInstance i) throws ThinklabException {
		super.initialize(i);
		this.code = i.get("modeltypes:hasStateFunction").toString();
	}

}
