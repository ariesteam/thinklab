package org.integratedmodelling.corescience.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

/**
 * Helper methods to access and study observations
 * 
 * @author Ferdinando
 *
 */
public class Obs {

	public static IObservation getObservation(IInstance inst) throws ThinklabException {
		
		IInstanceImplementation ret = inst.getImplementation();
		
		if (ret == null || !(ret instanceof IObservation))
			throw new ThinklabValidationException("instance " + inst + " is not an observation");
		
		return (IObservation) ret;
		
	}
	
	/**
	 * Return true if all observations that have a datasource have one that
	 * implements IContextualizedState
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException 
	 */
	public static boolean isContextualized(IObservation observation) throws ThinklabException {
		
		if (observation.getDataSource() != null &&
				!(observation.getDataSource() instanceof IContextualizedState)) {
			return false;
		}
		for (IObservation o : observation.getContingencies()) {
			if (!isContextualized(o))
				return false;
		}
		for (IObservation o : observation.getDependencies()) {
			if (!isContextualized(o))
				return false;
		}
		return true;
	}
	
	/**
	 * Return all the observable concepts along the observation structure that have
	 * a state. Throws an exception if that state is not contextualized.
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException 
	 */
	public static Collection<IConcept> getStatefulObservables(IObservation observation) throws ThinklabException {
		return getStateMap(observation).keySet();
	}
	
	public static Collection<IContextualizedState> getStates(IObservation observation) throws ThinklabException {
		return getStateMap(observation).values();
	}
	
	public static Map<IConcept, IContextualizedState> getStateMap(IObservation observation) throws ThinklabException {
		HashMap<IConcept, IContextualizedState> ret = new HashMap<IConcept, IContextualizedState>();
		collectStates(observation, ret);
		return ret;
	}

	private static void collectStates(IObservation observation,
			HashMap<IConcept, IContextualizedState> ret) throws ThinklabException {
		
		if (observation.getDataSource() instanceof IContextualizedState) {
			ret.put(observation.getObservableClass(), (IContextualizedState) observation.getDataSource());
		}
		for (IObservation o : observation.getContingencies()) {
			collectStates(o, ret);
		}
		for (IObservation o : observation.getDependencies()) {
			collectStates(o, ret);
		}
	}
	
}
