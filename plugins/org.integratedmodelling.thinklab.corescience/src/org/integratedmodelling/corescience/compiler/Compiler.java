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
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.internal.IndirectObservation;
import org.integratedmodelling.corescience.interfaces.internal.MediatingObservation;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
		IStateAccessor accessor = null;
		boolean needed = false;
		int aggregatorId = -1;
		int register = -1;
		int activationRegister = -1;
		boolean stateStored = false;
		int stateId = -1;
		int validatorId = -1;
		int initialValueId = -1;
		boolean needsContextStates = false;
		boolean[] activeDims;
		IDataSource<?> datasource = null;
		boolean contextualized = false;
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

	public Contextualizer compile(ObservationContext context) throws ThinklabException {
		
		/*
		 * figure out all dependencies from the context
		 */
		learnStructure(context);
		
		/*
		 * check the topology of the observation tree. Must be cycle-free.
		 */
		checkTopology();
		
		TopologicalOrderIterator<IObservation, MediatedDependencyEdge> ord =
			new TopologicalOrderIterator<IObservation, MediatedDependencyEdge>(dependencies);
				
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
		insertDependencies(context);
	}


	private Observation insertDependencies(ObservationContext context) {
		
		Observation ret = (Observation) context.getObservation();

		contexts.put(ret.getObservableClass(), context);
		addObservation(ret);

		for (ObservationContext dep : context.getDependentContexts()) {
			addObservationDependency(ret, insertDependencies(dep));
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
			ret = new VMContextualizer<Float>(stackType);
			stateType = KnowledgeManager.Float();
		} else if (KnowledgeManager.Thing().equals(stackType)
				|| stackType.is(KnowledgeManager.LiteralValue())) {
			ret = new VMContextualizer<IValue>(stackType);
			stateType = KnowledgeManager.LiteralValue();
		} else {

			/*
			 * we should be using a mapping to abstract classifications
			 * 
			 * TODO analyze the stack type; make it Object if not any of the
			 * above, and ensure that observations build their own
			 * datasources.
			 */
			ret = new VMContextualizer<IConcept>(stackType);
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
		boolean anyAccessors = false;
		for (IObservation o : order) {		
			
			if (buildObsDesc((Observation)o, accessors, deactivatable, context, ret, stateType).needsContextStates)
				needsContextStates = true;
			
			if (accessors.get(o).accessorId >= 0)
				anyAccessors = true;	
		}
		
		/*
		 * resolve the deactivations
		 */
		for (IObservation o : order)
			if (deactivatable.contains(o)) {
				accessors.get(o).activationRegister = ret.getNewActivationRegister();
			}
		
		boolean anythingNeeded = false;
		
		/*
		 * create context register: states for all dimensions, either ints or values.
		 */
		ret.initializeContextRegister(context, needsContextStates);
		
		/*
		 * push constants and initial values into registers
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
						
			if (!odesc.needed || odesc.accessorId < 0 || odesc.contextualized)
				continue;
			
			anythingNeeded = true;
			
			/*
			 * this is not null if not all context changes determine a change in our state. If so,
			 * compile in a jump over all our state setting unless those specific dimensions have
			 * changed.
			 */
			int[] ctxInact = null;
			if (odesc.activeDims != null) {
				ctxInact = new int[odesc.activeDims.length];
				for (int zi = 0; zi < odesc.activeDims.length; zi++) {
					ctxInact[zi] = ret.encodeContextJump(zi);
				}
			}
			
			/*
			 * if it can be deactivated, store the address of the activation check to encode a jump
			 * to next variable if the activation register is off
			 */
			int jumpAddr = ret.getPC();
			int activationJump = -1;
			if (odesc.activationRegister >= 0)
				activationJump = ret.encodeActivationCheck(odesc.activationRegister);
			
			/*
			 * push result
			 */
			ret.encodePushState(odesc.accessorId);

			/* validate if required */
			if (odesc.validatorId >= 0)
				ret.encodeValidation(odesc.validatorId);

			/*
			 * if we're aggregating, compile in aggregation and check of coverage
			 * with jump address. Also determine activation register for all dependents
			 * and we must ensure we jump after pop state if the dependent needs us.
			 */
			if (odesc.aggregatorId != -1) {
				
				/* encode coverage check of current extent; 
				 * if ok, jump to state storage, else encode deactivation of all
				 * dependents and jump to after pop state
				 */
				int jump = ret.encodeCoverageCheck(odesc.aggregatorId);
				
				ArrayList<Integer> deacRegs = new ArrayList<Integer>();
				for (MediatedDependencyEdge e : dependencies.outgoingEdgesOf(o)) {
					
					IObservation oo = e.getTargetObservation();
					ObsDesc obd = accessors.get(oo);
					deacRegs.add(obd.activationRegister);
				}
				
				// this part is skipped unless coverage test fails
				// for all dependents, ret.encodeDeactivation() of their activation registers
				for (int reg : deacRegs) {
					ret.encodeDeactivation(reg);
				}
								
				// resolve the check with jump to next PC so that deactivation is skipped if
				// check is successful
				ret.resolveJump(jump, ret.getNextPC());				
			}
			
			if (odesc.needed) {

				ret.encodePopToRegister(odesc.register);
				if (odesc.stateStored) {
					ret.encodeStoreFromRegister(odesc.stateId, odesc.register);
				}
				
			} else if (odesc.stateStored) {

				ret.encodeStoreFromStack(odesc.stateId);
			}

			/*
			 * if we had an activation check, compile in the jump address now.
			 */
			if (activationJump >= 0)
				ret.resolveJump(activationJump, jumpAddr);
			
			/*
			 * if we are skipping context states, resolve their instructions
			 */
			if (ctxInact != null) {
				for (int z : ctxInact) {
					ret.resolveJump(ctxInact[z], jumpAddr);
				}
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
		VMContextualizer<?> cc = new VMContextualizer<Float>(null);
		return cc;
	}

	private ObsDesc buildObsDesc(Observation o,
			HashMap<IObservation, ObsDesc> accessors, 
			HashSet<IObservation> deactivatable, 
			IObservationContext context, 
			VMContextualizer<?> contextualizer, 
			IConcept stateType) throws ThinklabException {
		
		if (accessors.containsKey(o))
			return accessors.get(o);
		
		ObsDesc odesc = new ObsDesc();

		IDataSource<?> ds = o.getDataSource();
		IObservationContext ownContext = contexts.get(o.getObservableClass());
		
		odesc.datasource = ds;
		if (/* o.isTransformed() && */odesc.datasource != null && odesc.datasource instanceof IState) {
			// FIXME we just assume this for the time being; the datasource will be 
			// automatically inserted and no code generated.
			odesc.contextualized = true;
			accessors.put(o, odesc);
			return odesc;
		}

		/*
		 * the accessor is a mediator if we are mediating
		 */
		IStateAccessor accessor = null;
		
		if (o.isMediator() && (o instanceof MediatingObservation)) {
			accessor = ((MediatingObservation)o).getMediator((IndirectObservation) o.getMediatedObservation());
		} else if (o instanceof IndirectObservation) {
			accessor = ((IndirectObservation)o).getAccessor();
		}
		
		if (accessor != null) {

			// TODO check if it's a parameter and if it has an initial value, set odesc
			// to behave accordingly (if parameter, notify it and build parm support;
			// if initial value, make it a parameter and load value into register			
			boolean constant = false;
			if ( (constant = accessor.isConstant())) {
				odesc.initialValueId = contextualizer.registerValue(accessor.getValue(null));
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
		
		/*
		 * if we depend on an aggregated state upstream, compile in a check for activation and
		 * insert a jump if deactivated, to be resolved later.
		 */
		boolean isExtent = o instanceof Topology;

		if ( (odesc.stateStored = (storeState && !isExtent && !o.isMediated()))) {

			int size = ownContext.getMultiplicity();
			odesc.stateId = 
				contextualizer.registerStateStorage((IndirectObservation) o, o.getObservableClass(), size);
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
				buildObsDesc((Observation)dependent, accessors, deactivatable, context, contextualizer, stateType);

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
			}

			if (odesc.aggregatorId != -1) {
				deactivatable.add(dependent);
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
			
			/*
			 * last, determine when our state should be updated in our register, which 
			 * is when any of the dimensions of the overall context that we also have
			 * in ours have changed. If this array is null, no conditional jump will
			 * be inserted.
			 */
			boolean[] activeDims = new boolean[context.getNumberOfDimensions()];
			int xind = 0;
			boolean hasAll = true;
			for (IConcept dc : context.getDimensions()) {
				if (ownContext != null && ownContext.getExtent(dc) != null) {
					activeDims[xind] = true;
				} else {
					hasAll = false;
				} 
				xind++;	
			}
			if (!hasAll) {
				
				// TLC-46: Wrong generation of context skipping in observation contextualization compiler
				// http://ecoinformatics.uvm.edu/jira/browse/TLC-46
				// FIXME will croak with an array out of bounds when interpreting bytecode
				odesc.activeDims = activeDims;
			}
		
		}
			
		accessors.put(o, odesc);
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
