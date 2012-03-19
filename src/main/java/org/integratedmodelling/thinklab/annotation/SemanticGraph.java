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
		DefaultDirectedGraph<ISemanticObject<?>, PropertyEdge> {

	private static final long serialVersionUID = -3767397482776773404L;
	ISemanticObject<?> _root;
	
	public SemanticGraph(IReferenceList semantics, ISemanticObject<?> semanticObject) {
		super(PropertyEdge.class);
		_root = build(semanticObject, semantics, null);
	}

	private ISemanticObject<?> build(
			ISemanticObject<?> root, IReferenceList semantics, 
			HashMap<IReferenceList, ISemanticObject<?>> refs) {

		if (refs == null) {
			refs = new HashMap<IReferenceList, ISemanticObject<?>>();
		}
		
		if (root == null) {
			IConcept concept = Thinklab.c(semantics.first().toString());
			Object o = null;
			if (Thinklab.get().isLiteralConcept(concept))
				o = semantics.nth(1);
			root = (ISemanticObject<?>) Thinklab.get().getSemanticObject(semantics, o);
		}
		
		if (refs.containsKey(semantics)) {
			return refs.get(semantics);
		}

		addVertex(root);
		refs.put(semantics, root);
		
		for (Object o : semantics) {
			
			if (o instanceof IList) {
				IProperty p = Thinklab.p(((IList) o).first().toString());
				ISemanticObject<?> oo = null;
				if (((IList) o).nth(1) instanceof IList) {
					oo = build(null, (IReferenceList) ((IList) o).nth(1), refs);
				}
				if (oo.isLiteral()) {
					((SemanticObject<?>)root).setLiteralRelationship(p, oo);
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

		public ISemanticObject<?> getFrom() {
			return (ISemanticObject<?>)getSource();
		}	

		public ISemanticObject<?> getTo() {
			return (ISemanticObject<?>)getTarget();
		}
	}
	
	public boolean hasCycles() {
		
		CycleDetector<ISemanticObject<?>, PropertyEdge> cycleDetector = 
				new CycleDetector<ISemanticObject<?>, PropertyEdge>(this);
		return cycleDetector.detectCycles();
	}
	
	public List<ISemanticObject<?>> getTopologicalSorting() throws ThinklabCircularDependencyException {
		
		if (hasCycles())
			throw new ThinklabCircularDependencyException(
					"object semantics for " + _root + " has mutual dependencies: cannot sort");
		
		TopologicalOrderIterator<ISemanticObject<?>, PropertyEdge> iterator =
				new TopologicalOrderIterator<ISemanticObject<?>, SemanticGraph.PropertyEdge>(this);
		
		ArrayList<ISemanticObject<?>> ret = new ArrayList<ISemanticObject<?>>();
		
		while (iterator.hasNext())
			ret.add(iterator.next());
		
		return ret;
	}
	
}
