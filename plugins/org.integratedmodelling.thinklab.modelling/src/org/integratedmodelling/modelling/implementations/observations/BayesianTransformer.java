package org.integratedmodelling.modelling.implementations.observations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.Obs;
import org.integratedmodelling.corescience.implementations.datasources.ClassData;
import org.integratedmodelling.corescience.implementations.datasources.IndexedContextualizedDatasourceInt;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.TransformingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.corescience.interfaces.data.IContextualizedState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Triple;

import smile.Network;

/**
 * Support for the modelling/bayesian form.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept="modeltypes:BayesianTransformer")
public class BayesianTransformer 
	extends Observation 
	implements IConceptualModel, TransformingConceptualModel {
	
	ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	HashSet<IConcept> outputStates = new HashSet<IConcept>();
	
	IConcept cSpace = null;
	private Network bn = null;
	
	@Override
	public IStateAccessor getStateAccessor(IConcept stateType,
			IObservationContext context) {
		throw new ThinklabRuntimeException("internal: bayesian transformer: get accessor called");
	}

	@Override
	public IConcept getStateType() {
		return cSpace;
	}

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
				
		IValue url = i.get("modeltypes:hasBayesianNetworkSource");
		IValue alg = i.get("modeltypes:hasBayesianAlgorithm");

		if (url != null) {
			
			/*
			 * read in the network
			 * TODO support URLs and relative file paths - see thinklab resolution
			 */
			this.bn = new Network();
			
			try {
				this.bn.readFile(url.toString());
				
				if (alg != null) {

					/*
					 * TODO if a specific algorithm is requested, set it
					 */
					
				}
				
			} catch (Exception e) {
				throw new ThinklabValidationException(
						"bayesian transformer: problem reading network from " + url);
			}
			
			for (IRelationship r : i.getRelationships("modeltypes:retainsState")) {
			}
			
		} else {
			
			/*
			 * TODO 
			 * convert observation specs into BN 
			 * TLC-43: Implement BN/CPT specs parsing and BN building in observation BayesianTransformer along with reading from URL.
			 * http://ecoinformatics.uvm.edu/jira/browse/TLC-43
			 * 
			 */
			throw new ThinklabUnimplementedFeatureException(
					"bayesian transformer: network URL not specified and inline specs unimplemented");
		}
		
		IValue def = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (def != null)
			cSpace = def.getConcept();

	}
	
	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
	}

	@Override
	public void validate(IObservation observation)
			throws ThinklabValidationException {
	}

	@Override
	public IContextualizedState createContextualizedStorage(int size)
			throws ThinklabException {
		return new CategoricalDistributionDatasource(cSpace, size);
	}

	@Override
	public Polylist getTransformedConceptualModel() {
		// this should be OK, we just transform to an identification.
		return null;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}

	@Override
	public IInstance transformObservation(IInstance inst)
			throws ThinklabException {
		
		IObservation orig = Obs.getObservation(inst);
		IObservationContext context = orig.getObservationContext();
		Map<IConcept, IContextualizedState> smap = Obs.getStateMap(orig);
		int size = context.getMultiplicity();

		/*
		 * see what states we want to retain as RandomClassifications. If no other
		 * specification has been made, use the set of our dependent evidence.
		 */
		if (outputStates.size() == 0) {
			throw new ThinklabModelException("bayesian transformer: no states are being retained, exiting");
		}
		
		/*
		 * prepare storage for each observable in all retained states, using the state ID for speed
		 */
		class PStorage { int field; IConcept observable; CategoricalDistributionDatasource data; };
		PStorage[] pstorage = new PStorage[outputStates.size()];
		int i = 0;
		for (IConcept var : outputStates) {
			
			PStorage st = new PStorage();
			st.field = bn.getNode(var.getLocalName());
			st.observable = var;
			st.data = new CategoricalDistributionDatasource(var, size);
			pstorage[i++] = st;
			
			/*
			 * TODO determine all possible states and their IDs in the net;
			 * map each to its concept (same concept space as var) and set
			 * legend in datasource.
			 */
			
		}
		
		/*
		 * enable fast access to evidence and ensure all evidence is classified
		 * appropriately; use evidence state ID for speed.
		 */
		class Evidence { 
			int field; ICategoryData data; 
			Evidence(int f, ICategoryData d) { field = f; data = d; }
		}
		i = 0;
		Evidence[] evidence = new Evidence[smap.size()];
		for (IConcept ec : smap.keySet()) {
			IContextualizedState cs = smap.get(ec);
			if (! (cs instanceof ICategoryData))
				throw new ThinklabModelException(
						"bayesian transformer: dependent for " +
						ec + 
						" is not a classification");
			evidence[i++] = new Evidence(bn.getNode(ec.getLocalName()), (ICategoryData)cs);
		}		
		
		/*
		 * you never know
		 */
		bn.clearAllEvidence();
		
		/*
		 * run network, setting state. 
		 * 
		 * TODO this would greatly benefit from parallelization.
		 * TLC-44: Parallelize BN calculations in BayesianTransformer. 
		 * Just needs to partition the points across available processors.
		 * http://ecoinformatics.uvm.edu/jira/browse/TLC-44
		 */
		for (int state = 0; state < size; state++) {
			
			/*
			 * submit evidence - we set the same values at each cycle, so we don't need to
			 * clear previous evidence unless we have a null.
			 */
			for (int e =0; e < evidence.length; e++) {
				IConcept ev = evidence[e].data.getCategory(state);
				if (ev == null)
					bn.clearEvidence(evidence[e].field);
				else 
					bn.setEvidence(
						evidence[e].field, 
						ev.getLocalName());
			}
			
			/*
			 * run inference
			 */
			bn.updateBeliefs();
			
			/*
			 * set states
			 * TODO must map the possible states for each var and set the legend in the
			 * storage beforehand
			 */
			for (int s = 0; s < pstorage.length; s++) {
				
			}
			
		}
		
		/*
		 * prepare new observation
		 */
		Polylist rdef = Polylist.list(
				CoreScience.OBSERVATION,
				Polylist.list(
						CoreScience.HAS_OBSERVABLE, getObservable().toList(null)));
		
		/*
		 * make new extents to match previous
		 */
		for (IConcept ext : context.getDimensions()) {
			rdef = ObservationFactory.addExtent(rdef, context.conceptualizeExtent(ext));
		}
		
		/*
		 * add states
		 */
		for (int s = 0; s < pstorage.length; s++) {
			
			Polylist ddef = Polylist.list(
					CoreScience.PROBABILISTIC_CLASSIFICATION,
					Polylist.list(
							CoreScience.HAS_OBSERVABLE, Polylist.list(pstorage[s].observable)),
					Polylist.list(CoreScience.HAS_DATASOURCE, Polylist.list("@", pstorage[s].data)));
			
			rdef = ObservationFactory.addDependency(rdef, ddef);
		}

		return null;
	}
}