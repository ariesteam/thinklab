package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.utils.Polylist;

@InstanceImplementation(concept="observation:ProbabilisticClassification")
public class ProbabilisticClassification extends Observation implements IConceptualModel, IConceptualizable {

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContextualizedState createContextualizedStorage(int size)
			throws ThinklabException {
		// TODO make some nice probabilistic DS that encodes distributions.
		return null;
	}

}
