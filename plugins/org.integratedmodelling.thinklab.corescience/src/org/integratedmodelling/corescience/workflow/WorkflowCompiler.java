package org.integratedmodelling.corescience.workflow;

import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationWorkflow;
import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.observation.IObservation;
import org.integratedmodelling.corescience.observation.IObservationState;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Builds a compiled contextualizer class specifically for the given observation structure,
 * ready to be instantiated and run.
 * 
 * @author Ferdinando
 *
 */
public class WorkflowCompiler implements IContextualizationCompiler {

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
		
	}
	
	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = 
		new DefaultDirectedGraph<IObservation, MediatedDependencyEdge>(MediatedDependencyEdge.class);
	
	@Override
	public void addMediatedDependency(IObservation destination, IObservation source) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addObservation(IObservation observation) {
		dependencies.addVertex(observation);
	}

	@Override
	public void addObservationDependency(IObservation destination, IObservation source) {
		dependencies.addEdge(source, destination).setMediated(true);
	}

	@Override
	public Class<?> compile(IObservation observation,
			IObservationContext context) throws ThinklabException {

		final float[] zio = new float[]{10.2f}; 
		
		class Contx extends SimpleContextualizer {

			float[] ff = zio;
		
		}
		
		return Contx.class;
	}


}
