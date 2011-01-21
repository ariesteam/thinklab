package org.integratedmodelling.corescience.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.ComputedDataSource;
import org.integratedmodelling.corescience.interfaces.internal.IContextTransformation;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

/**
 * Builds a compiled contextualizer class specifically for the given observation structure,
 * ready to be instantiated and run.
 * 
 * @author Ferdinando
 *
 */
public class Compiler {

	/*
	 * optimization - if this is false, validators are not compiled in. Not linked to any
	 * interface yet, so useless until we figure out general parameters.
	 */
	boolean _validate = true;
	HashMap<IConcept,IInstance> transformedObservations = new HashMap<IConcept, IInstance>();
	
	class ObsDesc {
		int accessorId = -1;
		private IStateAccessor accessor = null;
		boolean needed = false;
		int register = -1;
		boolean stateStored = false;
		int stateId = -1;
		int validatorId = -1;
		int initialValueId = -1;
		boolean needsContextStates = false;
		IDataSource<?> datasource = null;
		boolean contextualized = false;
		public IState predefined;
		public ArrayList<IContextTransformation> transformations = null;
	}

	private HashMap<IConcept, IObservationContext> contexts = 
		new HashMap<IConcept, IObservationContext>();

	HashMap<IConcept,IState> tstates = new HashMap<IConcept, IState>();

	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = 
		new DefaultDirectedGraph<IObservation, MediatedDependencyEdge>(MediatedDependencyEdge.class);

	/*
	 * the dependency edge holds all details of the necessary mediation or aggregation.
	 */
	public static class MediatedDependencyEdge extends DefaultEdge {

		private static final long serialVersionUID = 5926757404834780955L;

		// if true, this edge does not express a dependency but a provenance relationship. The target 
		// observation is guaranteed to share the same context (i.e. must come from a observation.getSameContextAntecedent() call).
		boolean isAntecedent = false;
		
		public IObservation getSourceObservation() {
			return (IObservation)getSource();
		}
		
		public void setAntecedent(boolean b) {
			isAntecedent = b;
		}

		public IObservation getTargetObservation() {
			return (IObservation)getTarget();
		}
	}
	
	private void checkTopology() throws ThinklabCircularDependencyException {

		CycleDetector<IObservation, MediatedDependencyEdge> cycleDetector = 
			new CycleDetector<IObservation, MediatedDependencyEdge>(dependencies);
		
		if (cycleDetector.detectCycles()) {

			Set<IObservation> problemObs = cycleDetector.findCycles();
			throw new ThinklabCircularDependencyException(
					"observation has circular dependencies in " + problemObs);
		}
	}

	public Contextualizer compile(ObservationContext context, ISession session) throws ThinklabException {
		
		context.validate();
		
		/*
		 * figure out all dependencies from the context. Context may already contain states for
		 * given observables.
		 */
		learnStructure(context);
		
		/*
		 * check the topology of the observation tree. Must be cycle-free.
		 */
		checkTopology();
		
		TopologicalOrderIterator<IObservation, MediatedDependencyEdge> ord =
			new TopologicalOrderIterator<IObservation, MediatedDependencyEdge>(dependencies);
		
		/*
		 * TODO this is a good point to establish which states are computed more than
		 * once, and ensure that we only compile their accessors in the first time.
		 */
				
		/*
		 * compile initialization sequence
		 */
		IObservation obs; 
		ArrayList<IObservation> order = new ArrayList<IObservation>();

		IConcept stackType = null;
		
		Contextualizer ret = new Contextualizer(context);

		while (ord.hasNext()) {
			
			/*
			 * add to order of compilation for later, so we don't repeat the sorting.
			 */
			order.add(obs = ord.next());
			
			/*
			 * callback to expose obs to the context before anything happens.
			 */
			((Observation)obs).preContextualization(context, session);
			
			/*
			 * determine common stack type
			 */
			if (obs instanceof IndirectObservation &&
				((IndirectObservation)obs).getStateType() != null) {
				stackType = 
					stackType == null ? 
						((IndirectObservation)obs).getStateType() :
						stackType.getLeastGeneralCommonConcept(((IndirectObservation)obs).getStateType());
			}
			
			/*
			 * if we have no outgoing edges, we can work on the next dependencies independently;
			 * make a contextualizer and reset the order and the stack type.
			 * 
			 * FIXME this should be no direct dependencies, excluding contingencies. Must avoid
			 * compiling in "void" observations.
			 */
			if (dependencies.outgoingEdgesOf(obs).size()
					/*((Observation)obs).getNonExtentDependencies().length */ == 0) {
				
				VMContextualizer<?> ctxer = 
					createThreadContextualizer(order, stackType, context);
				
				if (ctxer != null) {
					ctxer.addTransformedStates(tstates);
					ret.addContextualizer(ctxer);
				}
				
				order.clear();
				stackType = null;
			}
		}
				
		return ret;
	}

	private void learnStructure(ObservationContext context) {
		
		if (context != null)
			insertDependencies(context);
	}

	private Observation insertDependencies(ObservationContext context) {
		
		Observation ret = (Observation) context.getObservation();

		contexts.put(ret.getObservableClass(), context);
		addObservation(ret);

		if (context.getState(ret.getObservableClass()) != null) {
			ret.setPredefinedState(context.getState(ret.getObservableClass()));
		} else {
			for (ObservationContext dep : context.getDependentContexts()) {
				addObservationDependency(ret, insertDependencies(dep));
			}
		}
		return ret;
	}

	private VMContextualizer<?> createThreadContextualizer(
			ArrayList<IObservation> order, 
			IConcept stackType, 
			IObservationContext context)
		throws ThinklabException {
		
		VMContextualizer<?> ret = null;
		IConcept stateType = null;
		
		/*
		 * create a contextualizer appropriately for the stack type
		 */
		if (stackType == null) {
			ret = createNoOpContextualizer();
		} else if (stackType.is(KnowledgeManager.Number())) {
			ret = new VMContextualizer<Float>(stackType, context);
			stateType = KnowledgeManager.Float();
		} else if (KnowledgeManager.Thing().equals(stackType)
				|| stackType.is(KnowledgeManager.LiteralValue())) {
			ret = new VMContextualizer<IValue>(stackType, context);
			stateType = KnowledgeManager.LiteralValue();
		} else {

			/*
			 * we should be using a mapping to abstract classifications
			 * 
			 * TODO analyze the stack type; make it Object if not any of the
			 * above, and ensure that observations build their own
			 * datasources.
			 */
			ret = new VMContextualizer<IConcept>(stackType, context);
			stateType = stackType;
		}
		
		/*
		 * First pass - retrieve all accessors, expose them to dependencies and determine for 
		 * which variables we need to communicate state 
		 */
		HashMap<IObservation, ObsDesc> accessors = new HashMap<IObservation, ObsDesc>();
		HashSet<IObservation> deactivatable = new HashSet<IObservation>();
		
		/*
		 * build descriptors with all the needed info for each obs
		 */
		boolean needsContextStates = false;
		for (IObservation o : order) {		
			
			if (buildObsDesc((Observation)o, accessors, deactivatable, context, ret, stateType).needsContextStates)
				needsContextStates = true;
		}
		
		boolean anythingNeeded = false;
		
		/*
		 * create context register: states for all dimensions, either ints or values.
		 */
		ret.initializeContextRegister(context, needsContextStates);
		
		/*
		 * push constants and initial values into registers
	     *
		 * TODO this is a good time to ensure only the first view of the same concept
		 * gets compiled.
		 */
		for (int i = 0; i < order.size(); i++) {

			IObservation o = order.get(i);
			ObsDesc odesc = accessors.get(o);
			
			if (odesc.contextualized) {
				ret.addTransformedState(o.getObservableClass(), (IState) odesc.datasource);
				continue;
			}
			
			if (odesc.initialValueId != -1 && odesc.register != -1) {				
				ret.encodeRegImmediate(odesc.register, odesc.initialValueId);
			}
		}
		
		int start = ret.getPC();
		
		if (deactivatable.size() > 0)
			ret.encodeActivateAll();
		
		/*
		 * Second pass: compile contextualization sequence. No register- function 
		 * or allocations should be called from now on: the rest of the bytecode is
		 * looped for each context state.
		 */	
		for (int i = 0; i < order.size(); i++) {

			IObservation o = order.get(i);
			ObsDesc odesc = accessors.get(o);
						
			if (odesc.predefined != null) {
				ret.encodePushPredefined(odesc.predefined);
				continue;
			}
			
			if (!odesc.needed || odesc.accessorId < 0 || odesc.contextualized)
				continue;
			
			anythingNeeded = true;
			
			/*
			 * if it can be deactivated, store the address of the activation check to encode a jump
			 * to next variable if the activation register is off
			 */
			/*
			 * push result
			 */
			ret.encodePushState(odesc.accessorId);

			/* validate if required */
			if (odesc.validatorId >= 0)
				ret.encodeValidation(odesc.validatorId);
			
			/* transform if requested */
			if (odesc.transformations != null) {
				for (IContextTransformation t : odesc.transformations) {
					ret.encodeTransform(t);
				}
			}
			
			if (odesc.needed) {

				ret.encodePopToRegister(odesc.register);
				if (odesc.stateStored) {
					ret.encodeStoreFromRegister(odesc.stateId, odesc.register);
				}
				
			} else if (odesc.stateStored) {

				ret.encodeStoreFromStack(odesc.stateId);
			}
		}
		
		/*
		 * encode increment context and jump to label if not finished. If context is just indexed,
		 * just encode a numeric increment and check.
		 */
		if (context.getMultiplicity() > 1 && anythingNeeded)
			ret.encodeIncrementContext(start);
		
		ret.encodeReturn();
		
		return ret;
	}

	private VMContextualizer<?> createNoOpContextualizer() {
		// TODO check if we need more
		VMContextualizer<?> cc = new VMContextualizer<Float>(null, null);
		return cc;
	}

	private ObsDesc buildObsDesc(Observation o,
			HashMap<IObservation, ObsDesc> descriptors, 
			HashSet<IObservation> deactivatable, 
			IObservationContext context, 
			VMContextualizer<?> contextualizer, 
			IConcept stateType) throws ThinklabException {
		
		if (descriptors.containsKey(o))
			return descriptors.get(o);
		
		ObsDesc odesc = new ObsDesc();

		if (o.getPredefinedState() != null) {
			/*
			 * if context has a state for this observable, just use that state and compile
			 * no further.
			 * 
			 * FIXME disabling until the fucking "undiscretizers" are not needed any more. Those are
			 * predefined models that don't predefine anything, and confuse the hell out of it.
			 */
//			odesc.predefined = o.getPredefinedState();
//			descriptors.put(o, odesc);
//			return odesc;
		}
		
		if (((ObservationContext)context).getTransformation(o.getObservableClass()) != null) {
			odesc.transformations = new ArrayList<IContextTransformation>();
			odesc.transformations.add(
					((ObservationContext)context).getTransformation(o.getObservableClass()).newInstance());
		}
		
		IDataSource<?> ds = o.getDataSource();
		IObservationContext ownContext = contexts.get(o.getObservableClass());
		
		odesc.datasource = ds;
		if (/* o.isTransformed() && */odesc.datasource != null && odesc.datasource instanceof IState) {
			// FIXME we just assume this for the time being; the datasource will be 
			// automatically inserted and no code generated.
			odesc.contextualized = true;
			descriptors.put(o, odesc);
			return odesc;
		}
		
		/*
		 * the accessor is a mediator if we are mediating
		 */
		IStateAccessor accessor = null;
		
		if (o.isMediator() && (o instanceof MediatingObservation)) {
			accessor = ((MediatingObservation)o).getMediator((IndirectObservation) o.getMediatedObservation(), context);
		} else if (o instanceof IndirectObservation) {
			accessor = ((IndirectObservation)o).getAccessor(context);
		}
		
		if (accessor != null) {

			// TODO check if it's a parameter and if it has an initial value, set odesc
			// to behave accordingly (if parameter, notify it and build parm support;
			// if initial value, make it a parameter and load value into register			
			boolean constant = false;
			if ( (constant = accessor.isConstant())) {
				odesc.initialValueId = contextualizer.registerValue(accessor.getValue(0, null));
			} 
			
			if (!constant) {
				odesc.accessorId = contextualizer.registerStateAccessor(accessor);
				// store the accessor so dependencies can be notified
				odesc.accessor = accessor;
			} 
		}
		
		/* will need it anyway if someone is mediating it */
		if (o.isMediated()) {
			odesc.needed = true;
		}
		
		boolean storeState = 
			o instanceof IndirectObservation && 
			((IndirectObservation)o).getStateType() != null && 
			isStored(o.getObservableClass());

		boolean isExtent = o instanceof Topology;

		if ( (odesc.stateStored = (storeState && !isExtent && !o.isMediated()))) {

			int size = ownContext.getMultiplicity();
			odesc.stateId = 
				contextualizer.registerStateStorage(
						(IndirectObservation) o, 
						o.getObservableClass(), 
						size,
						ownContext,
						context,
						accessor);
		} else if (accessor != null) {
			/*
			 * give the accessor a chance to create private state storage if necessary in 
			 * case the compiler doesn't need to keep state but the accessor does, as in
			 * computing accessors.
			 */
			accessor.notifyState(null, context, ownContext);
		}
		
		/* store them all here, we notify our register to them at the end when we have one */
		ArrayList<IStateAccessor> accessorsThatWantUs = new ArrayList<IStateAccessor>();
		ArrayList<IDataSource<?>> datasourcesThatWantUs = new ArrayList<IDataSource<?>>();
		
		/* 
		 * notify our state to our dependents, and determine if they
		 * actually want us. 
		 */
		for (MediatedDependencyEdge e : dependencies.outgoingEdgesOf(o)) {
			
			IObservation dependent = e.getTargetObservation();
			ObsDesc odsc = 
				buildObsDesc((Observation)dependent, descriptors, deactivatable, context, contextualizer, stateType);

			/**
			 * TODO -- check logics:
			 * If the dependent is a mediator and we are an extent, the dependent only wants
			 * whatever is mediating, which is not going to be an extent. At this time it's hard
			 * to catch this condition from the mediator itself, so it's best to avoid exposing
			 * it altogether.
			 */
			if (isExtent && dependent.isMediator())
				continue;
			
			if (odsc.accessor != null &&
					odsc.accessor.notifyDependencyObservable(o, o.getObservableClass(), o.getFormalName())) {
				accessorsThatWantUs.add(odsc.accessor);
				datasourcesThatWantUs.add(odsc.datasource);
				odesc.needed = true;
				
//				/*
//				 * if we are stateless we have no state, so what the accessor means is that it wants all of
//				 * our direct dependencies. Of course this stateless bullshit should only exist at the top
//				 * of an observation tree, but for now we have the f'ing bayesian things to make sure the
//				 * kids can use Genie.
//				 */
//				for (MediatedDependencyEdge ed : dependencies.incomingEdgesOf(dependent)) {	
//					
//					IObservation adep = ed.getSourceObservation();
//					System.out.println("fcy " + adep.getObservableClass() + " is wanted by " + dependent.getObservableClass());
//					ObsDesc zio = descriptors.get(o);
//					System.out.println("its descriptor is " + zio + "\n");
//					
//					
//					//					ObsDesc aods = 
////						buildObsDesc((Observation)adep, accessors, deactivatable, context, contextualizer, stateType);
////					odsc.accessor.notifyDependencyObservable(adep, adep.getObservableClass(), ((Observation)adep).getFormalName());
//				}
			}

		}

		/* another good reason to keep it is that we have and want its state, even if nothing
		 * depends on us 
		 * 
		 * FIXME this should be different: needed = needed in a register; if stored, we can 
		 * pop directly to state unless ALSO needed (or aggregated).
		 * 
		 */
		if (odesc.stateStored && odesc.accessor != null)
			odesc.needed = true;

		
		/* if someone wants us, give us a register to pop our value into and
		 * let them know what it is */
		if (odesc.needed) {
		
			odesc.register = contextualizer.getNewRegister();
		
			for (int oo = 0; oo < accessorsThatWantUs.size(); oo++) { 
				
				IStateAccessor acc = accessorsThatWantUs.get(oo); 
				IDataSource<?> dsc = datasourcesThatWantUs.get(oo); 

				acc.notifyDependencyRegister(
							o,
							o.getObservableClass(),
							odesc.register, stateType);
				
				/* we also notify the register to the data sources
				 * of the target observation */
				if (dsc != null && dsc instanceof ComputedDataSource) 
					((ComputedDataSource)dsc).notifyDependency(
							o.getObservableClass(), stateType, odesc.register);
				
			}
		}
			
		descriptors.put(o, odesc);
		return odesc;
	}

	/**
	 * Override this one to customize what states get stored into datasources for the final observation. The
	 * default is to store all that get calculated except extents.
	 * 
	 * @param observableClass
	 * @return
	 */
	protected boolean isStored(IConcept observableClass) {
		return true;
	}

	public void notifyContext(IConcept observable, IObservationContext context) {
		contexts.put(observable, context);
	}
	
	public void addObservation(Observation observation) {			
		dependencies.addVertex(observation);
	}

	public MediatedDependencyEdge addObservationDependency(IObservation destination, IObservation source) {
		
		dependencies.addVertex(source);
		dependencies.addVertex(destination);
		return dependencies.addEdge(source, destination);
	}

}
