package org.integratedmodelling.modelling.implementations.observations;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.datasources.InlineAccessor;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.data.ICategoryData;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.TransformingObservation;
import org.integratedmodelling.corescience.literals.GeneralClassifier;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.ObservationFactory;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
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
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.riskwiz.bn.BayesianFactory;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianInference;
import org.integratedmodelling.thinklab.riskwiz.interfaces.IBayesianNetwork;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

/**
 * Support for the modelling/bayesian form. 
 * 
 * @author Ferdinando
 */
@InstanceImplementation(concept="modeltypes:BayesianTransformer")
public class BayesianTransformer 
	extends Observation 
	implements IndirectObservation, TransformingObservation {
	
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
	HashSet<IConcept> requiredStates = new HashSet<IConcept>();
	
	public ArrayList<IModel> observed = new ArrayList<IModel>();
	
	// these two should be phased out...
	public IndirectObservation outputObservation = null;
	private IState outputState = null;
	private IConcept outputObservable = null;
	private IModel outputModel = null;
	
	IConcept cSpace = null;
	private IBayesianNetwork bn = null;

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		super.initialize(i);
		
		IValue url = i.get(HAS_NETWORK_SOURCE);
		IValue alg = i.get(HAS_BAYESIAN_ALGORITHM);

		if (url != null) {
			
			/*
			 * read in the network
			 */
			this.bn = BayesianFactory.get().createBayesianNetwork(url.toString());
			
			if (alg != null) {
				/*
				 * TODO if a specific algorithm is requested, set it
				 */
			}
				
			/*
			 * look for a node that has the same type as the
			 * model observable. If found, find a model in the prototypes with the same
			 * observable. If found, create an observation from it and use that to define
			 * the state as it is now.
			 */
			
			/*
			 * get an index of all node names from the network, to be used later
			 */
//			HashSet<String> nodeIDs = new HashSet<String>();
			for (String s : bn.getAllNodeIds()) {
				String cid = getObservableClass().getConceptSpace() + ":" + s;
				if (getObservableClass().toString().equals(cid)) {
					
					outputObservable = KnowledgeManager.getConcept(cid);
					
					for (IModel m : observed) {
						if (((Model)m).getDefinition().getObservableClass().equals(outputObservable)) {
							outputModel = m;
						}
					}
					
					break;
				}
			}
					
			
			/*
			 * read the states we want
			 */
			for (IRelationship r : i.getRelationships(ModelFactory.RETAINS_STATES)) {
				outputStates.add(KnowledgeManager.get().requireConcept(r.getValue().toString()));
			}
			
			/*
			 * and the states we won't do without
			 */
			for (IRelationship r : i.getRelationships(ModelFactory.REQUIRES_STATES)) {
				requiredStates.add(KnowledgeManager.get().requireConcept(r.getValue().toString()));
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
		 * store any prototypes. This must change, oh yes.
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
	public IContext transform(IObservationContext sourceCtx, ISession session, IContext context) 
		throws ThinklabException {

		// set to false unless you really want it
		boolean debug = false;
		// only log errors once
		boolean logged = false;
		
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
		
		int size = ((IObservationContext)context).getMultiplicity();

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
		
		IBayesianInference inference = bn.getInference();
		
		/*
		 * prepare storage for each observable in all retained states, using the state ID for speed
		 */
		class PStorage { String field; IConcept observable; IState data; };
		PStorage[] pstorage = new PStorage[outputStates.size()];
		int i = 0;
		for (IConcept var : outputStates) {
			
			PStorage st = new PStorage();
			st.field = var.getLocalName();
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
 			
 			ArrayList<Pair<GeneralClassifier, IConcept>> classf = new ArrayList<Pair<GeneralClassifier, IConcept>>();
 			IObservation gmodel = modelPrototypes.get(var);
 			if (gmodel instanceof ModeledClassification) 
 				classf = ((ModeledClassification)gmodel).classifiers;
 			
 			/*
 			 * add metadata to ds. These come from the classifications: must know if 
 			 * we're discretizing a continuous distribution or not. 
 			 */
 			if (gmodel != null) {
 				st.data =  
 					((IndirectObservation)gmodel).
 						createState(context.getMultiplicity(), (IObservationContext) context);
 				
 				if ( !(st.data instanceof CategoricalDistributionDatasource)) {
 					throw new ThinklabValidationException(
 							"model " +
 							((IModel)(gmodel.getMetadata().get(Metadata.DEFINING_MODEL))).getName() +
 							" in :observed clause for " +
 							st.observable +
 							"is not probabilistic");
 				}
 				
 			} else {
 				st.data = new CategoricalDistributionDatasource(var, size, pcstates, classf, 
 						(ObservationContext) context);
 				((CategoricalDistributionDatasource)(st.data)).
 					addAllMetadata(modelMetadata.get(st.observable));
 			}
			
			pstorage[i++] = st;
		}
		
		/*
		 * enable fast access to evidence and ensure all evidence is classified
		 * appropriately; use evidence state ID for speed.
		 */
		class Evidence { 
			// TODO check - at this point f == nodename unless nodeIDs does not contain it
			String field; IState data; String nodename;
			Evidence(String f, IState d, String n) { field = f; data = d; nodename = n; }

			public IConcept getStateConcept(int state) throws ThinklabValidationException {
			
				if (data instanceof ICategoryData)
					return ((ICategoryData)data).getCategory(state);
				
				Object ret = data.getValue(state);
				
				if (ret != null && !(ret instanceof IConcept)) {
//					throw new ThinklabValidationException(
//							"data used to set bayesian evidence for " + 
//							nodename + 
//							" is not a classification");
					return null;
				}
								
				return (IConcept)ret;
			}
		}
		i = 0;
		
		/*
		 * not all states retained are going to be part of the network. smap will contain
		 * more states than necessary, and they must not be added to the evidence array.
		 */
		ArrayList<Evidence> evdnc = new ArrayList<Evidence>();
		
		for (IConcept ec : sourceCtx.getStateObservables()) {
			
			// allowing unneeded evidence to get there for other purposes, checking at setEvidence time.
			IState cs = sourceCtx.getState(ec);
			evdnc.add(new Evidence(
						nodeIDs.contains(ec.getLocalName()) ? ec.getLocalName() : null, 
						cs, ec.getLocalName()));
		}		

		Evidence[] evidence = evdnc.toArray(new Evidence[evdnc.size()]);

		/*
		 * you never know
		 */
		inference.clearEvidence();
		
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
			
			inference.clearEvidence();
			
			/*
			 * submit evidence - we set the same values at each cycle, so we don't need to
			 * clear all previous evidence unless we have a null/nodata.
			 * 
			 * TODO this should be memoized. Still, doing so may require quite a bit of memory and
			 * setup time, so we should compare results before adopting it as default.
			 * 
			 */
			boolean skip = false;
			
			for (int e = 0; e < evidence.length; e++) {
					IConcept ev = evidence[e].getStateConcept(state);
					try {
						if (ev != null) {
							if (evidence[e].field != null) {
								inference.setEvidence(
										evidence[e].field, 
										ev.getLocalName());
								if (ekey != null) {
									ekey += evidence[e].nodename + "=" + ev.getLocalName() + ", ";
								}
							}
						} else {
							/*
							 * if we have nodata in one of the required states, we want
							 * nodata as a result instead of using the priors.
							 */
							if (requiredStates.contains(evidence[e].data.getObservableClass())) {
								skip = true;
								break;
							}
						}
					} catch (Exception ex) {
						
						/*
						 * only once, which enables further error to be hidden, but allows to avoi
						 * one hundred thousand error messages. This should be put in the session instead
						 * of printed if the session requires storage of errors.
						 */
						if (!logged) {
							ModellingPlugin.get().logger().error("exception " + ex + " while setting " + evidence[e].nodename + " to " + ev);
							// throw new ThinklabValidationException(ex);
							logged = true;
						}
					}
			}
			
			/*
			 * run inference unless crucial data not available
			 */
			if (!skip)
				inference.run();
						
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
			 * TODO check whether this should use a context mapper 
			 */
			String rrs = "";
			for (int s = 0; s < pstorage.length; s++) {
				
				pstorage[s].data.setValue(
						state, 
						skip ? null : inference.getMarginalValues(pstorage[s].field));
				
				if (ekey != null) {
					rrs += 
						pstorage[s].observable.getLocalName() + 
						"=" + 
						Arrays.toString(inference.getMarginalValues(pstorage[s].field)) + 
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
		
		ObservationContext ret = (ObservationContext)context.cloneExtents();;
		ret.setObservation(this);

		/*
		 * add states
		 */
		for (int s = 0; s < pstorage.length; s++) {
			
			if (
				outputState != null &&
				pstorage[s].data.getObservableClass().
					equals(outputState.getObservableClass())) {
				
				for (int is = 0; is < pstorage[s].data.getValueCount(); is++)
					outputState.setValue(is, pstorage[s].data.getValue(is));
			} else {
				ret.addState(pstorage[s].data);
			}
		}

		return ret;
	}
	
	@Override
	public String toString() {
		return ("bayesian(" + getObservableClass() + "): " + bn.getName());
	}


	@Override
	public IConcept getTransformedObservationClass() {
		return CoreScience.Observation();
	}

	public void checkOutputState() {
		if (outputObservation != null && outputState == null) {
//			outputState = outputObservation.createState(size, context);
		}
	}

	@Override
	public IConcept getStateType() {
		return 
			outputObservation == null ? 
				cSpace : // or null?
				outputObservation.getStateType();
	}

	@Override
	public void preContextualization(ObservationContext context,
			ISession session) throws ThinklabException {
		
		if (outputModel != null && outputObservation == null) {
			Polylist ls = 
				((Model)outputModel).getDefinition().buildDefinition(KBoxManager.get(), session, context, 0);
			outputObservation = 
				(IndirectObservation) ObservationFactory.getObservation(session.createObject(ls));	
		}
		if (outputObservation != null && outputState == null) {
			outputState = outputObservation.createState(context.getMultiplicity(), context);
		} else if (outputModel == null && outputObservation == null && outputObservable != null) {
			// TODO must float the state of the given concept but it's work to get the
			// parameters at this point
		}
	}

	@Override
	public IState createState(int size, IObservationContext context) throws ThinklabException {
		return outputState;
	}

	@Override
	public IStateAccessor getAccessor(IObservationContext context) {
		return outputState == null ? null : new InlineAccessor(outputState);
	}
	


}