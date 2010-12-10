package org.integratedmodelling.corescience;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.listeners.IContextualizationListener;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Polylist;

public class ObservationFactory {

	/**
	 * Run a contextualization in its "natural" context - i.e., the merge of the contexts of each observation in 
	 * the passed observation structure.
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance contextualize(IInstance observation, ISession session) throws ThinklabException {
		ObservationContext ctx = new ObservationContext(getObservation(observation), null);
		return ctx.run(session, null);
	}
	
	/**
	 * Run a contextualization in the natural context constrained by the passed topologies.
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance contextualize(IInstance observation, ISession session, Topology ... context) throws ThinklabException {
		
		ObservationContext constraint = new ObservationContext(context);
		ObservationContext ctx = new ObservationContext(getObservation(observation), constraint);
		return ctx.run(session, null);
	}
	
	/**
	 * Run a contextualization in the natural context constrained by the passed topologies.
	 * 
	 * @param observation
	 * @param session
	 * @return
	 * @throws ThinklabException
	 */
	public static IInstance contextualize(IInstance observation, ISession session, 
			Collection<IContextualizationListener> listeners, 
			Topology ... context) throws ThinklabException {
		
		ObservationContext constraint = new ObservationContext(context);
		ObservationContext ctx = new ObservationContext(getObservation(observation), constraint);

		if (Thinklab.debug(session)) {
			ctx.dump(session.getOutputStream());
		}
		
		return ctx.run(session, listeners);
	}

	public IInstance contextualize(IInstance observation, ISession session,
			Collection<IContextualizationListener> lis) throws ThinklabException {
		ObservationContext ctx = new ObservationContext(getObservation(observation), null);
		ctx.dump(session.getOutputStream());
		return ctx.run(session, lis);
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

	public static Map<IConcept, IState> getStateMap(IInstance observation) throws ThinklabException {
		return getStateMap(getObservation(observation));
	}

	private static void collectStates(IObservation observation,
			HashMap<IConcept, IState> ret) throws ThinklabException {
		
		if (observation.getDataSource() instanceof IState && 
				!ret.containsKey(observation.getObservableClass())) {
			ret.put(observation.getObservableClass(), (IState) observation.getDataSource());
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
		
		return null;
	}

	public static IObservation findTopology(IObservation obs, IConcept co) {

		IObservation ret = null;
		
			for (IObservation o : obs.getTopologies()) {
				if (o.getObservable().is(co)) {
					return o;
				}
		}

		for (IObservation o : obs.getDependencies()) {
			ret = findTopology(o, co);
			if (ret != null)
				return ret;
		}
		
		return null;
	}
	
	/**
	 * Add a new instance of an observable. If null is passed, see if we can
	 * define a meaningful observable automatically. 
	 * 
	 * @param observation
	 * @param observableClass
	 * @return
	 */
	public static Polylist setObservable(Polylist observation, String observableClass) {
		
		return observation.appendElement(
				Polylist.list(
						CoreScience.HAS_OBSERVABLE, 
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String idType, String observableClass) {
		
		return Polylist.list(idType, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return a ranking of the given observable with the given value
	 * @param observableClass
	 * @return
	 */
	public static Polylist createRanking(String observableClass, double value) {
		
		return Polylist.list(
					CoreScience.RANKING, 
					Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)),
					Polylist.list(CoreScience.HAS_VALUE,
						value+""));
	}
	
	/**
	 * Return a measurement of the given observable with the given value and units
	 * @param observableClass
	 * @return
	 */
	public static Polylist createMeasurement(String observableClass, double value, String unit) {
		
		return Polylist.list(
					CoreScience.MEASUREMENT, 
					Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)),
					Polylist.list(CoreScience.HAS_VALUE, value+""),
					Polylist.list(CoreScience.HAS_UNIT, unit));
	}
	
	
	/**
	 * Return an identification of the given type of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String idType, Polylist observable) {

		return Polylist.list(idType, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}

	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(String observableClass) {
		
		return Polylist.list(CoreScience.IDENTIFICATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						Polylist.list(observableClass)));
	}
	
	/**
	 * Return an identification of the given observable
	 * @param observableClass
	 * @return
	 */
	public static Polylist createIdentification(Polylist observable) {
		
		return Polylist.list(CoreScience.IDENTIFICATION, 
				Polylist.list(
						CoreScience.HAS_OBSERVABLE,
						observable));
	}

	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addDependency(Polylist observation, Polylist dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.DEPENDS_ON, dependent));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addDependency(Polylist observation, IInstance dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.DEPENDS_ON, dependent));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addReflectedField(Polylist observation, String field, Object value) {
		
		return observation.appendElement(
				Polylist.list(":" + field, value));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addContingency(Polylist observation, Polylist dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_CONTINGENCY, dependent));
	}
	
	/**
	 * 
	 * @param observation
	 * @param dependent
	 * @return
	 */
	public static Polylist addContingency(Polylist observation, IInstance dependent) {
		
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_CONTINGENCY, dependent));
	}
	
	/**
	 * 
	 * @return
	 */
	public static Polylist createIdentification() {
		
		return Polylist.list(CoreScience.IDENTIFICATION);		
	}

	/**
	 * Return the concept that this observation is observing.
	 * @param data
	 * @return
	 * @throws ThinklabException 
	 */
	public static IConcept getObservableClass(IInstance data) throws ThinklabException {
		return ((IObservation)(data.getImplementation())).getObservableClass();
	}

	/**
	 * Add the given observable definition to the given observation spec.
	 * 
	 * @param observation
	 * @param observableSpecs
	 * @return
	 */
	public static Polylist setObservable(Polylist observation, Polylist observableSpecs) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_OBSERVABLE, observableSpecs));
	}

	/**
	 * Add the given mediated definition
	 * 
	 * @param observation
	 * @param mediated
	 * @return
	 */
	public static Polylist addMediatedObservation(Polylist observation, Polylist mediated) {
		return observation.appendElement(
				Polylist.list(CoreScience.MEDIATES_OBSERVATION, mediated));
	}

	/**
	 * Return the associated IObservation from an instance, making sure it's actually an observation.
	 * 
	 * @param o
	 * @return
	 * @throws ThinklabException 
	 */
	public static IObservation getObservation(IInstance o) throws ThinklabException {
		
		Object iret = o.getImplementation();
		
		if (iret == null || !(iret instanceof IObservation))
			throw new ThinklabValidationException("object " + o.getLocalName() + " is not an observation");
		
		return (IObservation)iret;
	}

	/**
	 * Add an extent to the passed obs
	 * 
	 * @param observation
	 * @param extent
	 * @return
	 */
	public static Polylist addExtent(Polylist observation, Polylist extent) {
		return observation.appendElement(
				Polylist.list(CoreScience.HAS_EXTENT, extent));
	}
	
}
