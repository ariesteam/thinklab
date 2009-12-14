package org.integratedmodelling.corescience;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

public class ObservationFactory {

	/**
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public IInstance contextualize(IInstance observation, ISession session) throws ThinklabException {
		ObservationContext ctx = new ObservationContext(getObservation(observation), null);
		return ctx.run(session, null);
	}
	

	public IInstance contextualize(IInstance observation, ISession session,
			ArrayList<IContextualizationListener> lis) throws ThinklabException {
		ObservationContext ctx = new ObservationContext(getObservation(observation), null);
		return ctx.run(session, lis);
	}
	
	public static IObservation getObservation(IInstance inst) throws ThinklabException {
		
		IInstanceImplementation ret = inst.getImplementation();
		
		if (ret == null || !(ret instanceof IObservation))
			throw new ThinklabValidationException("instance " + inst + " is not an observation");
		
		return (IObservation) ret;
	}
	
	/**
	 * Return all the observable concepts along the observation structure that have
	 * a state. Throws an exception if that state is not contextualized. Will also return any observation
	 * in the provenance chain that shares the same context with the others.
	 * 
	 * @param observation
	 * @return
	 * @throws ThinklabException 
	 */
	public static Collection<IConcept> getStatefulObservables(IObservation observation) throws ThinklabException {
		return getStateMap(observation).keySet();
	}
	
	public static Collection<IState> getStates(IObservation observation) throws ThinklabException {
		return getStateMap(observation).values();
	}
	
	public static Map<IConcept, IState> getStateMap(IObservation observation) throws ThinklabException {
		HashMap<IConcept, IState> ret = new HashMap<IConcept, IState>();
		collectStates(observation, ret);
		return ret;
	}

	private static void collectStates(IObservation observation,
			HashMap<IConcept, IState> ret) throws ThinklabException {
		
		if (observation.getDataSource() instanceof IState) {
			ret.put(observation.getObservableClass(), (IState) observation.getDataSource());
		}
		for (IObservation o : observation.getDependencies()) {
			collectStates(o, ret);
		}
		for (IObservation o : observation.getAntecedents()) {
			collectStates(o, ret);
		}
	}

	/**
	 * Find the observation in the structure starting at obs that observes the 
	 * given observable class. Looks in the dependencies first, then in the
	 * contingencies. 
	 * 
	 * TODO check if we want to change the logics by limiting to the 
	 * dependencies.
	 * 
	 * @param obs
	 * @param co
	 * @return
	 */
	public static IObservation findObservation(IObservation obs, IConcept co) {
		
		IObservation ret = null;
		
		if (obs.getObservable().is(co)) {
			return obs;
		}

		for (IObservation o : obs.getDependencies()) {
			ret = findObservation(o, co);
			if (ret != null)
				return ret;
		}
		
		return null;
	}

}
