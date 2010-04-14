package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.List;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

import clojure.lang.IFn;

/**
 * This one is used mostly by the modelling system. It is expected to have contingencies and
 * a "switch layer" to tell us which context state corresponds to which contingency. Support
 * to compute the switch layer and skip the states that don't belong to a contingency is built
 * in the compiler and observation context. It works as a transformer, removing the original
 * dependencies and substituting them with merged dependencies whose datasources proxy to 
 * contingencies according to the results of a context model.
 * 
 * The use of this class would be complex with direct API or observation specification, but it
 * is a breeze with contingent models as specified in the modeling plugin. It is the class that
 * allows structurally variable models.
 * 
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept=CoreScience.STATELESS_MERGER_OBSERVATION)
public class ContingencyMerger extends Observation {

	// reflected 
	public SwitchLayer<IState> switchLayer = null;
	public ArrayList<Topology> contextExt = null;
	public IObservation        contextObs = null;
	public ArrayList<IFn>      conditionals = null;

	/**
	 * Called after all the contingencies have been independently contextualized. Will find
	 * observations of the same observables and build new switching datasources for them.
	 * 
	 * TODO this one is tough.
	 * 
	 * @param cResults
	 */
	public IObservation mergeResults(List<IObservation> cResults) {
		return null;
	}

	@Override
	public void initialize(IInstance inst) throws ThinklabException {

		super.initialize(inst);
		
	}
	
	
	
}
