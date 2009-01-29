package org.integratedmodelling.corescience.workflow;

import java.util.ArrayList;
import java.util.Set;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IStackCompiler;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.exception.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
public abstract class StackWorkflowCompiler implements IStackCompiler {

	
	/*
	 * the dependency edge holds all details of the necessary mediation or aggregation.
	 */
	public class MediatedDependencyEdge extends DefaultEdge {

		private static final long serialVersionUID = 5926757404834780955L;
		private boolean mediated = false;
		
		public IObservation getSourceObservation() {
			return (IObservation)getSource();
		}
		
		public IObservation getTargetObservation() {
			return (IObservation)getTarget();
		}
		
		public void setMediated(boolean mediated) {
			this.mediated  = mediated;
		}
		
		public boolean isMediated() {
			return mediated;
		}
		
	}
	
	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = 
		new DefaultDirectedGraph<IObservation, MediatedDependencyEdge>(MediatedDependencyEdge.class);
	
	@Override
	public void addMediatedDependency(IObservation destination, IObservation source) {
		dependencies.addEdge(source, destination).setMediated(true);
	}

	@Override
	public void addObservation(IObservation observation) {
		dependencies.addVertex(observation);
	}

	@Override
	public void addObservationDependency(IObservation destination, IObservation source) {
		dependencies.addEdge(source, destination);
	}
	
	private void checkTopology() throws ThinklabCircularDependencyException {

		CycleDetector<IObservation, MediatedDependencyEdge> cycleDetector = 
			new CycleDetector<IObservation, MediatedDependencyEdge>(dependencies);
		
		if (cycleDetector.detectCycles()) {
			
			/*
			 * TODO (minor): list involved observations in message.
			 */
			Set<IObservation> problemObs = cycleDetector.findCycles();
			throw new ThinklabCircularDependencyException(
					"observation has circular dependencies");
		}

	}

	@Override
	public IContextualizer compile(IObservation observation,
			IObservationContext context) throws ThinklabException {
		
		/*
		 * check the topology of the observation tree; if applicable, build
		 * separate sorting orders to be compiled into different threads.
		 * 
		 * TODO parallelization analysis is not here yet.
		 * 
		 */
		checkTopology();
		
		TopologicalOrderIterator<IObservation, MediatedDependencyEdge> ord =
			new TopologicalOrderIterator<IObservation, MediatedDependencyEdge>(dependencies);
		
		/*
		 * check whether we can compile a single cycle and repeat it for all
		 * context states.
		 * 
		 * TODO just assume it's repeatable for the time being. We have no case studies of
		 * non-repeatable behavior yet.
		 * 
		 */
		boolean isRepeatable = true;
		
		/*
		 * use a virtual to create an appropriate contextualizer
		 */
		IContextualizer ret = createStackBasedContextualizer();
		
		
		/*
		 * compile initialization sequence
		 */
		ActivationRecord obs; 
		ArrayList<ActivationRecord> order = new ArrayList<ActivationRecord>();

		boolean hasState = false;
		boolean isStateSaved = true;
		boolean isStateUsed = false;

		while (ord.hasNext()) {
			
			/*
			 * build an activation record, which studies the dependencies and
			 * contexts for the obs.
			 */
			order.add(obs = new ActivationRecord(ord.next(), dependencies));
			
			/*
			 * determine the chain of CMs, mediators and aggregators to extract this state
			 */
			
			/*
			 * determine if we have an initial state that is either saved or used by others
			 */
			hasState = false;
			isStateSaved = true;
			isStateUsed = false;
			
			/*
			 * if it has an initial state, compile in the accessors (or the
			 * inline value)
			 */
			
			
		}
		
		/*
		 * compile contextualization sequence
		 */
		for (ActivationRecord o : order) {

			/*
			 * determine the chain of CMs, mediators and aggregators to extract this state
			 */
			IConceptualModel cm = o.obs.getConceptualModel();
			/*
			 * determine if we have an initial state that is either saved or used by others
			 */
			hasState = false;
			isStateSaved = true;
			isStateUsed = false;
			
			/*
			 * if we need the state, compile in the accessors (or the
			 * inline value)
			 */
			
			/*
			 * if we store the state, compile in the storage call; dup if we need to 
			 * pass it along to the next function.
			 */
			
			
		}
		
		return ret;
	}

	/**
	 * Provide a contextualizer that works with the stack. All the stack functions in the
	 * implementing class will need to be connected to it.
	 * 
	 * @return
	 */
	protected abstract IContextualizer createStackBasedContextualizer();

	@Override
	public boolean canCompile(IObservation observation) {
		// TODO Auto-generated method stub
		return false;
	}


}
