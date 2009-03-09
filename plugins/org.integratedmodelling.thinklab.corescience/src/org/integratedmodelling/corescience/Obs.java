package org.integratedmodelling.corescience;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.implementations.datasources.MemValueContextualizedDatasource;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.utils.Polylist;

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
		for (IObservation o : observation.getDependencies()) {
			if (!isContextualized(o))
				return false;
		}
		for (IObservation o : observation.getContingencies()) {
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
		
		for (IObservation o : obs.getContingencies()) {
			ret = findObservation(o, co);
			if (ret != null)
				return ret;
		}
		return null;
	}

	/**
	 * 
	 * @param observationType
	 * @param observable
	 * @param conceptualModel 
	 * @param ds
	 * @return
	 * @throws ThinklabException
	 */
	public static Polylist makeObservation(IConcept observationType,
			IInstance observable, IConceptualModel conceptualModel,
			MemValueContextualizedDatasource ds) throws ThinklabException {
		
		Polylist c = null;
		
		if (conceptualModel != null) {
			if (! (conceptualModel instanceof IConceptualizable))
				throw new ThinklabValidationException(
					"makeObservation: internal: cannot obtain " + 
					"representation of conceptual model " + 
					conceptualModel);
		
			c = ((IConceptualizable)conceptualModel).conceptualize();
		}
		
		Polylist o = 
			observable instanceof IConceptualizable ?
					((IConceptualizable)observable).conceptualize() :
					observable.toList(null);
		
		return c == null ?
				Polylist.list(
						observationType,
						Polylist.list(CoreScience.HAS_OBSERVABLE, o),
						Polylist.list(CoreScience.HAS_DATASOURCE,
								Polylist.list("@", ds))) :
				Polylist.list(
						observationType,
						Polylist.list(CoreScience.HAS_OBSERVABLE, o),
						Polylist.list(CoreScience.HAS_CONCEPTUAL_MODEL, c),
						Polylist.list(CoreScience.HAS_DATASOURCE,
						Polylist.list("@", ds)));			
	}
	
}
