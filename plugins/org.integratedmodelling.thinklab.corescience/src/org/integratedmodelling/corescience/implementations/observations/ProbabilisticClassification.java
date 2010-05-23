package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Polylist;

/**
 * TODO this is a stub, used by BN transformers; only works if built manually.
 * 
 * @author Ferdinando Villa
 *
 */
@InstanceImplementation(concept="observation:ProbabilisticClassification")
public class ProbabilisticClassification extends Observation implements IndirectObservation {

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		ArrayList<Object> arr = new ArrayList<Object>();
		
		arr.add("observation:ProbabilisticClassification");
		arr.add(Polylist.list(CoreScience.HAS_OBSERVABLE, Polylist.list(getObservableClass())));

		if (getFormalName() != null) {
			arr.add(Polylist.list(CoreScience.HAS_FORMAL_NAME, getFormalName()));			
		}
		return Polylist.PolylistFromArrayList(arr);
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getStateType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
