package org.integratedmodelling.thinklab.modelling.compiler;

import java.util.HashMap;

import org.integratedmodelling.common.HashableObject;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.annotation.SemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IContextTransformingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IMediatingAccessor;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IObserver;
import org.integratedmodelling.thinklab.api.modelling.IParallelAccessor;
import org.integratedmodelling.thinklab.modelling.compiler.ModelResolver.DependencyEdge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Handles the contextualization of a model tree using a graph of connections between observations and
 * accessors.
 * 
 * @author Ferd
 */
public class Contextualizer  {

	private DirectedGraph<IModel, DependencyEdge> _structure;
	private int lastRegister = 0;

	public Contextualizer(DirectedGraph<IModel, DependencyEdge> structure) {
		_structure = structure;
	}
	
	public class Dependency extends DefaultEdge {
		
		public Dependency(boolean b, String formalName) {
			isMediation = b;
			this.formalName = formalName;
		}
		public Dependency(boolean b, String formalName,
				ISemanticObject<?> observable) {
			this(b, formalName);
			this.observable = observable;
		}
		
		private static final long serialVersionUID = 2366743581134478147L;
		public boolean isMediation = false;
		public String formalName = null;
		public ISemanticObject<?> observable = null;

		/*
		 * each dependency gets its own register automatically
		 */
		public int register = -1;

		@Override
		public boolean equals(Object edge) {
			return 
				edge instanceof Dependency &&
				this.getSource().equals(((Dependency)edge).getSource()) &&
				this.getTarget().equals(((Dependency)edge).getTarget()) &&
				isMediation == ((Dependency)edge).isMediation;
		}
	}

	
	/*
	 * compilation element - the accessor graph is made of these.
	 * TODO we need a strategy for multiple observables - must notify
	 * them all to the accessor, then have a way to obtain them - a 
	 * getValue(observable, index)?
	 */
	public class CElem extends HashableObject {
		
		public CElem(IAccessor accessor, IContext context, IModel model) {
			this.accessor = accessor;
			this.model = model;
			this.context = context;
		}

		// may be null. For semantics and logging only.
		public IModel model;
		public IAccessor accessor;
		/*
		 * May be different from node to node, either because only
		 * initializers are needed, or because a context transformer has been 
		 * processed.
		 */
		public IContext context;
		
		// topological order, only for debugging
		public int order = -1;
	}
	
	/**
	 * basically it's one accessor per model following the dependency
	 * structure; observer mediation will chain
	 * other accessors without states.
	 * 
	 * All accessors get notified of the dependencies and their formal
	 * names.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public DirectedGraph<CElem, Dependency> buildAccessorGraph(IModel root, 
			DirectedGraph<IModel, DependencyEdge> modelGraph, 
			IContext context) 
		throws ThinklabException {

		DirectedGraph<CElem, Dependency> graph =
				new DefaultDirectedGraph<CElem, Dependency>(Dependency.class);

		/*
		 * build the accessor graph. It may contain more than one disconnected graphs if the top model
		 * is an identification with dependencies but no observer, so we don't store the top node.
		 */
		buildAccessorGraphInternal(root, graph, modelGraph, context);
		
		/*
		 * assign registers to move values around, ensuring same observable uses same register. 
		 * Mediation chains for now happens across different registers for simplicity, we can reuse
		 * if necessary.
		 */
		lastRegister = 0;
		for (CElem cc : graph.vertexSet()) {
			
			HashMap<String, Integer> hs = new HashMap<String, Integer>();
			for (Dependency d : graph.outgoingEdgesOf(cc)) {
				/*
				 * observables are repeated from the source model along the chain 
				 * of mediation so that we can assign the same register.
				 */
				String sig = ((SemanticObject<?>)(d.observable)).getSignature();
				if (hs.get(sig) != null) {
					d.register = hs.get(sig);
				} else {
					d.register = lastRegister++;
					hs.put(sig, d.register);
				}
			}
		}
				
		return graph;
	}
	
	/**
	 * TODO to deal with parallel and transforming accessors: 
	 * 
	 * 	keep a hash of observable sig -> state
	 *  when parallel accessor encountered, run it and put its process() result in hash
	 *  else at each dependency, look first if it's in the hash and use that state as accessor if so.
	 *  IN order to do so, the observable of the target must be in the edge
	 *  
	 *  If context is transformed, get the context from the accessor and this must become the new
	 *  context - to be floated up to return value. CElem must also contain the context for the final 
	 *  node.
	 *  
	 * @param model
	 * @param graph
	 * @param modelGraph
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	private CElem buildAccessorGraphInternal(
			IModel model, 
			DirectedGraph<CElem, Dependency> graph, 
			DirectedGraph<IModel, DependencyEdge> modelGraph, 
			IContext context) throws ThinklabException {
		
		CElem node = null;
		
		if (model.getDatasource() != null) {
			
			/*
			 * get the accessor from the DS and chain it to ours
			 */
			node = new CElem(model.getObserver().getAccessor(context), context, null);

			if ( !(node.accessor instanceof IMediatingAccessor))
				throw new ThinklabValidationException("trying to mediate to a non-mediating observer");
			
			CElem target = new CElem(model.getDatasource().getAccessor(context), context, model);
			graph.addVertex(node);
			graph.addVertex(target);
			graph.addEdge(target, node, new Dependency(true, null, model.getObservables().get(0)));
			
		} else if (model.getObserver() != null) {
			
			IAccessor accessor = model.getObserver().getAccessor(context);
			
			if (accessor instanceof IParallelAccessor) {
				/*
				 * TODO
				 * 
				 * First time this is found, contextualize it as an identification and run it. 
				 * From the second time onwards, just pick its result context from previous
				 * storage.
				 * 
				 * Find the the dependency we're looking at and retrieve the corresponding
				 * the state computed. If also a context transformer, merge the new context in.
				 */
			}
			
			if (accessor instanceof IContextTransformingAccessor) {
				/*
				 * TODO transform the context, which becomes the new 
				 * current one, and attach the former one to its 
				 * provenance records.
				 */
			}
			
			if (accessor != null) {
				node = new CElem(accessor, context, model);
				graph.addVertex(node);
			}
		}
		
		for (DependencyEdge edge : modelGraph.outgoingEdgesOf(model)) {
			
			if (edge.isMediation) {
				
				/*
				 * if we get here, the node must have had an observer, so node can't be null.
				 */
				
				/*
				 * NOTE: there may be more dep edges to the same model - which must
				 * result in ONE accessor being created but repeated in each CElem,
				 * and the edge must carry the observable - to later be called as
				 * getValue(observable, index). Mediated nodes should be invoked as
				 * getMediatedValue(index).
				 */
				
				/*
				 * get the accessor chain for the final observable
				 */
				CElem target = 
						buildAccessorGraphInternal(
								modelGraph.getEdgeTarget(edge), graph, modelGraph, context);
				
				/*
				 * loop along the chain of mediation until we find the final
				 * observable; then get the model for it, get its CElem and
				 * tie the last mediator to it.
				 */				
				CElem start = node;
				IObserver obs = model.getObserver();
				
				while (obs.getMediatedObserver() != null) {
					CElem targ = new CElem(obs.getMediatedObserver().getAccessor(context), context, null);
					graph.addVertex(targ);
					graph.addEdge(targ, start, 
							new Dependency(true, null, model.getObservables().get(0)));
					obs = obs.getMediatedObserver();
					start = targ;
				}
				
				graph.addEdge(target, start, new Dependency(true, null, model.getObservables().get(0)));
				
			} else {

				/*
				 * create accessor and notify dependency
				 */
				CElem target = buildAccessorGraphInternal(modelGraph.getEdgeTarget(edge), graph, modelGraph, context);
				if (node != null) {
					graph.addEdge(target, node, new Dependency(false, edge.formalName, edge.observable));
				}
			}
		}
				
		return node;
	}

	public IObservation run(IModel model, IContext context) throws ThinklabException {

		DirectedGraph<CElem, Dependency> accessorGraph = 
				buildAccessorGraph(model, _structure, context);
				
		CompiledModel compiled = new CompiledModel(model, accessorGraph, context, lastRegister);

		/*
		 * find state to return as root, or build a new observation in this context if observables don't have
		 * states associated
		 */
		
		return compiled.run();
	}
	

	
}
