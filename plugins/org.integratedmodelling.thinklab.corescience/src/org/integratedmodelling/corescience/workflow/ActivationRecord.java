package org.integratedmodelling.corescience.workflow;

import java.util.ArrayList;

import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.workflow.StackWorkflowCompiler.MediatedDependencyEdge;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Describes an observation's status within a contextualization workflow
 * @author Ferdinando
 *
 */
public class ActivationRecord {

	 ActivationRecord(
			IObservation obs,  
			DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies) {
		this.obs = obs;
		this.dependencies = dependencies;
	}
	IObservation obs = null;
	DefaultDirectedGraph<IObservation, MediatedDependencyEdge> dependencies = null;
	
	
}
