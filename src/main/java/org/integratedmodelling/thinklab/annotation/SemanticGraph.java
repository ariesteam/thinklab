package org.integratedmodelling.thinklab.annotation;

import java.util.HashMap;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticGraph.PropertyEdge;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IReferenceList;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

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
			root = new SemanticObject(semantics, o);
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
	
	
	
}
