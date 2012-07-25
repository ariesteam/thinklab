package org.integratedmodelling.thinklab.modelling.debug;

import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.api.modelling.IAccessor;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.ISerialAccessor;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.CElem;
import org.integratedmodelling.thinklab.modelling.compiler.Contextualizer.Dependency;
import org.integratedmodelling.thinklab.modelling.compiler.ModelResolver.DependencyEdge;
import org.integratedmodelling.utils.graph.GraphViz;
import org.integratedmodelling.utils.graph.GraphViz.NodePropertiesProvider;
import org.jgrapht.DirectedGraph;

/**
 * Make graphs from model structure. Just an aid in debugging, if prettified maybe even
 * a GUI contributor one day.
 * 
 * @author ferdinando.villa
 *
 */
public class ModelGraph {

	private DirectedGraph<?, ?> graph;

	public ModelGraph(DirectedGraph<?, ?> graph) {
		this.graph = graph;
	}
	
	public String dump(boolean reverse) throws ThinklabResourceNotFoundException {
		
		GraphViz ziz = new GraphViz();
		ziz.loadGraph(graph, new NodePropertiesProvider() {
			
			@Override
			public int getNodeWidth(Object o) {
				return 40;
			}
			
			@Override
			public String getNodeId(Object o) {
				
				String id = "?";
				
				if (o instanceof IModel) {
				
					id = ((IModel)o).getId();
					if (ModelManager.isGeneratedId(id))
						id = "[" + ((IModel)o).getObservables().get(0).getDirectType() + "]";
				} else if (o instanceof IAccessor) {
					id = "Accessor (?)";
				} else if (o instanceof CElem) {
					id = 
						((CElem)o).accessor.toString() +
						(((CElem)o).model == null ? "" : " " + (((CElem)o).model.getObservables().get(0).getDirectType())) +
						" #" + ((CElem)o).order + "";
				}
				
				return id;
			}
			
			@Override
			public int getNodeHeight(Object o) {
				return 20;
			}

			@Override
			public String getNodeShape(Object o) {

				if (o instanceof IAccessor) {
					return HEXAGON;
				} else if (o instanceof CElem) {
					return ((CElem)o).accessor instanceof ISerialAccessor ? ELLIPSE : HEXAGON;
				} else if (o instanceof IModel) {
					return ((IModel)o).getDatasource() == null ? BOX : BOX3D;
				} 
				return BOX;
			}

			@Override
			public String getEdgeColor() {
				return "black";
			}

			@Override
			public String getEdgeLabel(Object e) {
				if (e instanceof Dependency) {
					return (((Dependency)e).isMediation ?
							("(m) " + ((Dependency)e).formalName) : ((Dependency)e).formalName) + 
							" [reg #" + ((Dependency)e).register + "]";
				} else 	if (e instanceof DependencyEdge) {
					return ((DependencyEdge)e).isMediation ?
							(((DependencyEdge)e).isInitialization ? "(i)" : "(m)"): ((DependencyEdge)e).formalName;
				}
				return "";
			}
			
		}, reverse);
		
		return ziz.getDotSource();
	}

}
