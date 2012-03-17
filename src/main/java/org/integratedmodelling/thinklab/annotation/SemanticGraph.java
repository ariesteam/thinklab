package org.integratedmodelling.thinklab.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabCircularDependencyException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticGraph.PropertyEdge;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

public class SemanticGraph extends
		DefaultDirectedGraph<SemanticObject, PropertyEdge> {

	private static final long serialVersionUID = -3767397482776773404L;
	SemanticObject _root;
	
	public SemanticGraph(IReferenceList semantics, SemanticObject semanticObject) {
		super(PropertyEdge.class);
		_root = build(semanticObject, semantics, null);
	}

	private SemanticObject build(
			SemanticObject root, IReferenceList semantics, 
			HashMap<IReferenceList, SemanticObject> refs) {

		if (refs == null) {
			refs = new HashMap<IReferenceList, SemanticObject>();
		}
		
		if (root == null) {
			IConcept concept = Thinklab.c(semantics.first().toString());
			Object o = null;
			if (Thinklab.get().isLiteralConcept(concept))
				o = semantics.nth(1);
			root = (SemanticObject) Thinklab.get().getSemanticObject(semantics, o);
		}
		
		if (refs.containsKey(semantics)) {
			return refs.get(semantics);
		}

		addVertex(root);
		refs.put(semantics, root);
		
		for (Object o : semantics) {
			
			if (o instanceof IList) {
				IProperty p = Thinklab.p(((IList) o).first().toString());
				SemanticObject oo = null;
				if (((IList) o).nth(1) instanceof IList) {
					oo = build(null, (IReferenceList) ((IList) o).nth(1), refs);
				}
				if (oo.isLiteral()) {
					root.setLiteralRelationship(p, oo);
				} else {
					addEdge(root, oo, new PropertyEdge(p));
				}
			}
		}
				
		return root;
	}

	public static class PropertyEdge extends DefaultEdge {
		
		private static final long serialVersionUID = 5926757404834780955L;
		public IProperty property = null;
		
		public PropertyEdge(IProperty p) {
			property = p;
		}

		public SemanticObject getFrom() {
			return (SemanticObject)getSource();
		}	

		public SemanticObject getTo() {
			return (SemanticObject)getTarget();
		}
	}
	
	public boolean hasCycles() {
		
		CycleDetector<SemanticObject, PropertyEdge> cycleDetector = 
				new CycleDetector<SemanticObject, PropertyEdge>(this);
		return cycleDetector.detectCycles();
	}
	
	public List<ISemanticObject> getTopologicalSorting() throws ThinklabCircularDependencyException {
		
		if (hasCycles())
			throw new ThinklabCircularDependencyException(
					"object semantics for " + _root + " has mutual dependencies: cannot sort");
		
		TopologicalOrderIterator<SemanticObject, PropertyEdge> iterator =
				new TopologicalOrderIterator<SemanticObject, SemanticGraph.PropertyEdge>(this);
		
		ArrayList<ISemanticObject> ret = new ArrayList<ISemanticObject>();
		
		while (iterator.hasNext())
			ret.add(iterator.next());
		
		return ret;
	}
	
}
