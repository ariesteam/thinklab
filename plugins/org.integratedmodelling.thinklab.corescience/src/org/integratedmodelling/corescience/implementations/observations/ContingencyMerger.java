package org.integratedmodelling.corescience.implementations.observations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.ObservationFactory;
import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.SwitchingState;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.constraint.DefaultConformance;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;
import clojure.lang.Keyword;
import clojure.lang.PersistentArrayMap;

/**
 * This one is used mostly by the modelling system. It is expected to have contingencies and
 * a "switch layer" to tell us which context state corresponds to which contingency. Support
 * to compute the switch layer and skip the states that don't belong to a contingency is built
 * in the compiler and observation context. It works as a transformer, removing the original
 * dependencies and substituting them with merged dependencies whose datasources proxy to 
 * contingencies according to the results of a context model.
 * 
 * The use of this class would be horribly complex with direct API or observation specification, but it
 * is a breeze with contingent models as specified in the modeling plugin. It is the class that
 * allows structurally variable models.
 * 
 * @author Ferdinando
 *
 */
@InstanceImplementation(concept=CoreScience.STATELESS_MERGER_OBSERVATION)
public class ContingencyMerger extends Observation {

	// reflected 
	public ArrayList<Topology> contextExt = null;
	public IObservation        contextObs = null;
	public ArrayList<IFn>      conditionals = null;

	class OSource {
		IInstance observable;
		ArrayList<IState> states = new ArrayList<IState>();
		ArrayList<Integer> contingencies = new ArrayList<Integer>();
	};
	
	/**
	 * Called after all the contingencies have been independently contextualized. Will find
	 * observations of the same observables and build new switching datasources for them.
	 * 
	 * @param cResults
	 * @throws ThinklabException 
	 */
	public Polylist mergeResults(IObservation[] cResults, IObservationContext context) throws ThinklabException {
		
		SwitchLayer<ContextMapper> switchLayer = null;
		if (contextObs != null) {

			switchLayer = new SwitchLayer<ContextMapper>(
					(ObservationContext) context);

			ArrayList<Pair<Keyword, ContextMapper>> cdata = 
				new ArrayList<Pair<Keyword, ContextMapper>>();
			for (Entry<IConcept, IState> ee : ObservationFactory.getStateMap(
					contextObs).entrySet()) {

				String id = ee.getKey().getLocalName();
				IModel mo = (IModel) ee.getValue().getMetadata().get(
						Metadata.DEFINING_MODEL);
				if (mo != null) {
					id = mo.getId();
				}

				cdata.add(new Pair<Keyword, ContextMapper>(
						Keyword.intern(null, id),
						new ContextMapper(ee.getValue(), context)));
			}

			/*
			 * create switch layer to select what obs to merge where, based
			 * on either results of conditionals or having state there. This is
			 * indexed by the main contingency, but will be used to merge their
			 * common dependencies across the whole hierarchy.
			 */
			for (int i = 0; i < context.getMultiplicity(); i++) {

				for (int st = 0; st < cResults.length; st++) {

					boolean ok = true; // if no conditional, it's true, order matters

					if (conditionals != null && conditionals.get(st) != null) {

						/*
						 * build parameter map
						 */
						PersistentArrayMap pmap = new PersistentArrayMap(new Object[]{});
						for (Pair<Keyword, ContextMapper> p : cdata) {
							pmap = (PersistentArrayMap) 
								pmap.assoc(
										p.getFirst(),
										p.getSecond().getValue(i, null));
						}
						
						/*
						 * eval conditionals or check for non-null val until one
						 * of the states matches
						 */
						try {
							ok = (Boolean)conditionals.get(st).invoke(pmap);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					/*
					 * set switch at location
					 */
					if (ok) {
						switchLayer.set(i, (byte) st);
						break;
					}
				}
			}
		}
		/*
		 * determine the appropriate observables for the merged states and establish a merge
		 * ordering. We use a list where each element is a pair, the first being the 
		 * (possibly common) observable, and the second a list of all the states it
		 * comes from, paired with the correspondent contingency order (it will be in 
		 * ascending order in the pairs).
		 */
		ArrayList<OSource> catalog = new ArrayList<OSource>();
		
		for (int cord = 0; cord < cResults.length; cord++) {
			/*
			 * fill in each slot for this observable, passing the correspondent
			 * contingency order
			 */
			scanContingency(cResults[cord], cord, catalog);
		}
		
		/*
		 * create merged states and observations, passing the switchlayer to resolve 
		 * indirections.
		 */
		Polylist ret = Polylist.list(
				CoreScience.OBSERVATION,
				Polylist.list(CoreScience.HAS_OBSERVABLE, getObservable().toList(null)));
		
		for (OSource os : catalog) {
			ContextMapper[] states = new ContextMapper[cResults.length];
			for (int i = 0; i < states.length; i++) {
				if (i == os.contingencies.get(i)) {
					states[i] = new ContextMapper(os.states.get(i), context);
				}
			}

			IState sws = new SwitchingState(states, os.observable, switchLayer, context);
			for (IState state : os.states) 
				sws.getMetadata().merge(state.getMetadata());
			
			// TODO what to use here? An observation is not its type for sure.
			Polylist dep = 
				Polylist.list(
					CoreScience.OBSERVATION,
					Polylist.list(CoreScience.HAS_OBSERVABLE, os.observable.toList(null)),
					Polylist.list(CoreScience.HAS_DATASOURCE, sws.conceptualize()));
			
			ret = ObservationFactory.addDependency(ret, dep);
						
		}
				
		return ret;
	}

	private void scanContingency(IObservation obs, int cord,
			ArrayList<OSource> catalog) throws ThinklabException {
		
		if (obs.getDataSource() instanceof IState) {
			Constraint c = new DefaultConformance().getConstraint(getObservable());
			int i = 0;
			for (; i < catalog.size(); i++) {
				if (c.match(catalog.get(i).observable)) {
					break;
				}
				if (i == catalog.size()) {
					catalog.add(new OSource());
				}
				OSource os = catalog.get(i);
				os.states.add((IState)obs.getDataSource());
				os.contingencies.add(cord);
			}
		}
		for (IObservation dep : obs.getDependencies()) {
			scanContingency(dep, cord, catalog);
		}
	}
}
