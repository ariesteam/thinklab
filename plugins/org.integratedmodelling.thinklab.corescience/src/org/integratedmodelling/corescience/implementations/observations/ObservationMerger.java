package org.integratedmodelling.corescience.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.datasources.SwitchableState;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * An observation capable of building a merged datasource to properly handle contingencies.
 * Currently the only type of observation that is given contingencies; this may change, but
 * for now there's no guarantee of contingencies working with any other kind of observation.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept=CoreScience.MERGER_OBSERVATION)
public class ObservationMerger extends Observation implements IndirectObservation {

	public SwitchLayer<IState> switchLayer = null;
	IState[] datasources = null;
	
	@Override
	public IState createState(int size, IObservationContext context)
			throws ThinklabException {
		return new SwitchableState(switchLayer);
	}

	@Override
	public IStateAccessor getAccessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		return null;
	}

}
