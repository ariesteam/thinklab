package org.integratedmodelling.modelling.implementations.observations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import smile.Network;

/**
 * Support for the modelling/bayesian form. 
 * TODO this only works with SMILE/Genie. Must be riskwiz-compatible, too.
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept="modeltypes:BayesianTransformer")
public class BayesianTransformer 
	extends Observation 
	implements  TransformingObservation {
	
	// relevant properties from ontology
	public static final String RETAINS_STATES = "modeltypes:retainsState";
	public static final String HAS_NETWORK_SOURCE = "modeltypes:hasBayesianNetworkSource";
	public static final String HAS_BAYESIAN_ALGORITHM = "modeltypes:hasBayesianAlgorithm";
	public static final String HAS_PROTOTYPE_MODEL = "modeltypes:hasPrototypeModel";
	
	// if these were passed, we use them to build the dependent states that
	// we compute with the BN. Otherwise they're just "stock" probabilistic 
	// classifications.
	public HashMap<IConcept, IObservation> modelPrototypes = 
		new HashMap<IConcept, IObservation>();
	
	// save metadata from prototypes
	public HashMap<IConcept, HashMap<String,Object>> modelMetadata = 
		new HashMap<IConcept, HashMap<String,Object>>();
	
	ArrayList<Pair<GeneralClassifier, IConcept>> classifiers = 
		new ArrayList<Pair<GeneralClassifier,IConcept>>();
	
	HashSet<IConcept> outputStates = new HashSet<IConcept>();
	
	IConcept cSpace = null;
	private Network bn = null;

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
				
		IValue url = i.get(HAS_NETWORK_SOURCE);
		IValue alg = i.get(HAS_BAYESIAN_ALGORITHM);

		if (url != null) {
			
			/*
			 * read in the network
			 */
			this.bn = new Network();
			
			try {
				this.bn.readFile(
						MiscUtilities.resolveUrlToFile(url.toString()).toString());
				
				if (alg != null) {

					/*
					 * TODO if a specific algorithm is requested, set it
					 */
					
				}
				
			} catch (Exception e) {
				throw new ThinklabValidationException(
						"bayesian transformer: reading " + url +
						": " + e.getMessage());
			}
			
			/*
			 * read the states we want
			 */
			for (IRelationship r : i.getRelationships(RETAINS_STATES)) {
				outputStates.add(KnowledgeManager.get().requireConcept(r.getValue().toString()));
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
		
		/*
		 * store any prototypes
		 */
		for (IRelationship r : i.getRelationships(HAS_PROTOTYPE_MODEL)) {
			IObservation prot = ObservationFactory.getObservation(r.getValue().asObjectReference().getObject());
			modelPrototypes.put(prot.getObservableClass(), prot);
			modelMetadata.put(prot.getObservableClass(), ((Observation)prot).metadata);
		}
		
		IValue def = i.get(CoreScience.HAS_CONCEPTUAL_SPACE);
		if (def != null)
			cSpace = def.getConcept();

	}
	
	@Override
	public Polylist transform(IInstance sourceObs, ISession session, IObservationContext context) 
		throws ThinklabException {

		// set to false unless you really want it
		boolean debug = false;
		
		HashMap<String, Integer> keyset = debug ? new HashMap<String, Integer>() : null;
		HashMap<String, String> resset = debug ? new HashMap<String, String>() : null;
		PrintWriter out = null;
		if (debug) {
			try {
				out =  new PrintWriter(new FileOutputStream("debug.txt", true));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			out.println(" >>>>>> " + getObservableClass() + "<<<<<<<\n");
		}
		
		IObservation orig = ObservationFactory.getObservation(sourceObs);
		
		Map<IConcept, IState> smap = ObservationFactory.getStateMap(orig);
		int size = context.getMultiplicity();

		/*
		 * get an index of all node names from the network, to be used later
		 */
		HashSet<String> nodeIDs = new HashSet<String>();
		for (String s : bn.getAllNodeIds()) {
			nodeIDs.add(s);
		}
		
		/*
		 * see what states we want to retain as RandomClassifications. If no 
		 * specification has been made, log a warning.
		 */
		if (outputStates.size() == 0) {
			ModellingPlugin.get().logger().warn(
					"bayesian transformer: " + 
					getObservableClass() + 
					": no states are being retained, bayesian network will be " +
					"computed without producing useful results");
		}
		
		/*
		 * prepare storage for each observable in all retained states, using the state ID for speed
		 */
		class PStorage { int field; IConcept observable; 
						CategoricalDistributionDatasource data; };
		PStorage[] pstorage = new PStorage[outputStates.size()];
		int i = 0;
		for (IConcept var : outputStates) {
			
			PStorage st = new PStorage();
			st.field = bn.getNode(var.getLocalName());
			st.observable = var;
			
			/*
			 * determine all possible states and their IDs in the net;
			 * map each to its concept (same concept space as var) and set
			 * value key in datasource.
			 */
			String[] pstates    = bn.getOutcomeIds(st.field);
			IConcept[] pcstates = new IConcept[pstates.length];
 			for (int j = 0; j < pstates.length; j++) {
				if (! (pstates[j].contains(":")))
					pstates[j] = var.getConceptSpace() + ":" + pstates[j];
				pcstates[j] = KnowledgeManager.get().requireConcept(pstates[j]);
			}
 			
 			/*
 			 * TODO add metadata to ds. These come from the classifications: must know if 
 			 * we're discretizing a continuous distribution or not. 
 			 */
			st.data = new CategoricalDistributionDatasource(var, size, pcstates, classifiers, 
					(ObservationContext) context);
			st.data.addAllMetadata(modelMetadata.get(st.observable));
			pstorage[i++] = st;
		}
		
		/*
		 * enable fast access to evidence and ensure all evidence is classified
		 * appropriately; use evidence state ID for speed.
		 */
		class Evidence { 
			int field; ICategoryData data; String nodename;
			Evidence(int f, ICategoryData d, String n) { field = f; data = d; nodename = n; }
		}
		i = 0;
		
		/*
		 * not all states retained are going to be part of the network. smap will contain
		 * more states than necessary, and they must not be added to the evidence array.
		 */
		ArrayList<Evidence> evdnc = new ArrayList<Evidence>();
		
		for (IConcept ec : smap.keySet()) {
			if (!nodeIDs.contains(ec.getLocalName()))
				continue;
			IState cs = smap.get(ec);
			if (! (cs instanceof ICategoryData))
				throw new ThinklabModelException(
						"bayesian(" + getObservableClass() + "): dependent for " +
						ec + 
						" is not a classification");
			evdnc.add(new Evidence(bn.getNode(ec.getLocalName()), (ICategoryData)cs, ec.getLocalName()));
		}		

		Evidence[] evidence = evdnc.toArray(new Evidence[evdnc.size()]);

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
			
			String ekey = debug ? "" : null;
			
			/*
			 * FIXME or better FIXIT - removing node evidence when there is a null 
			 * causes an exception (SMILE error -2), so we must do this OR understand why.
			 */ 
			bn.clearAllEvidence();
			
			/*
			 * submit evidence - we set the same values at each cycle, so we don't need to
			 * clear all previous evidence unless we have a null/nodata.
			 * 
			 * TODO this should be memoized. Still, doing so may require quite a bit of memory and
			 * setup time, so we should compare results before adopting it as default.
			 * 
			 */
			for (int e = 0; e < evidence.length; e++) {
				try {
					IConcept ev = evidence[e].data.getCategory(state);
					if (ev == null) {
						// FIXME
						// TODO see comment above - this causes a SMILE error -2 when called
						// with an existing, valid node name or id. 
						//bn.clearEvidence(evidence[e].field);
					} else {
						bn.setEvidence(
								evidence[e].field, 
								ev.getLocalName());
						if (ekey != null) {
							ekey += evidence[e].nodename + "=" + ev.getLocalName() + ", ";
						}
					}
				} catch (Exception ex) {
					ModellingPlugin.get().logger().error("exception " + ex + " while setting " + evidence[e].nodename);
					// throw new ThinklabValidationException(ex);
				}
			}
			
			/*
			 * run inference
			 */
			bn.updateBeliefs();
						
			if (ekey != null) {
				if (keyset.containsKey(ekey)) {
					keyset.put(ekey, keyset.get(ekey) + 1);
					ekey = null;
				} else {
					keyset.put(ekey, 1);
				}
			}
			
			/*
			 * set states of all desired outcomes
			 */
			String rrs = "";
			for (int s = 0; s < pstorage.length; s++) {
				pstorage[s].data.addValue(bn.getNodeValue(pstorage[s].field));
				if (ekey != null) {
					rrs += 
						pstorage[s].observable.getLocalName() + "=" + Arrays.toString(bn.getNodeValue(pstorage[s].field)) + 
						(s == (pstorage.length -1) ? "" : ", ");
				}
			}
			
			if (ekey != null) {
				resset.put(ekey, rrs);
			}
			
		}
		
		// debug output
		if (out != null) {
			for (String k: keyset.keySet())
				out.println("[" + keyset.get(k) + "] " + k + " -> " + resset.get(k));
			out.close();
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
			rdef = ObservationFactory.addExtent(rdef, context.getExtent(ext).conceptualize());
		}
		
		/*
		 * add states
		 */
		for (int s = 0; s < pstorage.length; s++) {
			
			IConcept obsv = pstorage[s].observable;
			IObservation proto = modelPrototypes.get(obsv);
			Polylist ddef = null;
			
			if (proto == null) {
				ddef = Polylist.list(
					CoreScience.PROBABILISTIC_CLASSIFICATION,
					Polylist.list(
							CoreScience.HAS_OBSERVABLE, Polylist.list(obsv)),
					Polylist.list(
							CoreScience.HAS_DATASOURCE, 
							pstorage[s].data.conceptualize()));
			} else {
		
				/*
				 * TODO ensure that metadata are communicated to the DS when created even if
				 * the DS is already set as we do here.
				 */
				ddef = proto.getObservationInstance().toList(null);
				ddef = 
					ObservationFactory.addDatasource(
							ddef, pstorage[s].data.conceptualize());
			}
			
			rdef = ObservationFactory.addDependency(rdef, ddef);
		}

		/*
		 * all evidence has the same context so keep it as provenance info. That
		 * will bring in a lot of stuff. Should be linked to a context parameter in the session?
		 */
//		for (IConcept ec : smap.keySet()) {
//			IObservation oo = ObservationFactory.findObservation(orig, ec);
//			rdef = ObservationFactory.addSameContextObservation(rdef, oo.getObservationInstance());
//		}
		
//		// TODO remove
//		System.out.println(
//				"\n >>>>>>>>>>>>>>>>>>>>>>>>> \n" + 
//				Polylist.prettyPrint(rdef) + 
//				"\n <<<<<<<<<<<<<<<<<<<<<<<<< \n");
		
		/*
		 * go for it
		 */
		return rdef;
	}
	
	@Override
	public String toString() {
		return ("bayesian(" + getObservableClass() + "): " + bn.getName());
	}


	@Override
	public IObservationContext getTransformedContext(IObservationContext context)
			throws ThinklabException {
		// we don't change the context at all, fortunately
		return context;
	}

	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}



}