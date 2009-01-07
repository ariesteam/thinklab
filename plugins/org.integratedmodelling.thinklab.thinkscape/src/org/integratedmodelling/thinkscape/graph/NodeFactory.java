package org.integratedmodelling.thinkscape.graph;

import java.util.Collection;

import org.integratedmodelling.ograph.OEdge;
import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IKnowledge;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.knowledge.IResource;
import org.integratedmodelling.utils.Pair;

public class NodeFactory {
		
	public   String lang;
	public OGraph ograph;
	
	public NodeFactory(OGraph g, String lang){
		ograph=g;
		this.lang=lang;
	}
	
	public String getPrefix(String conceptSpace){
		return "";
	}
	
	private void setResourceDescription(IResource rs, ONode node){
		if (rs.getDescription(lang) != null)
			node.setHint(rs.getDescription(lang));
		String ns =rs.getConceptSpace();
		String nsprefix =getPrefix(ns);
		node.setNamespace(ns);
		String label = rs.getLabel(lang);
		if (label != null)
			node.setRDFLabel(nsprefix + label.trim());
	}
	
	public Pair<ONode,Boolean> getNode(IKnowledge obj, int type) {
		
		String ns = obj.getConceptSpace();
		String ID = ns + ":" + obj.getURI();
		ONode node = ograph.findNode( ID );
		
		if (node != null)
			return new Pair<ONode,Boolean>(node, false);	
		 
		node = ograph.addONode();
		String label = obj.getLabel();
		
		node.init(ID, ((label == null || label.equals("")) ? obj.getLocalName() : label), type);
		
		setResourceDescription(obj,node );	
		
		return new Pair<ONode,Boolean>(node, true);
	}
	
	
	public ONode getConceptNode(IConcept obj) { 		
		return getNode(obj, ONode.OBJ_CLASS).getFirst();
	}
	
	
	public ONode getInstanceNode(IInstance obj) {
		
		Pair<ONode,Boolean> ret = getNode(obj, ONode.OBJ_INDIVIDUAL);

		if (!ret.getSecond())
			return ret.getFirst();
		
		// build instance -of relations
		IConcept directType= obj.getDirectType();
		ONode clNode= getConceptNode(directType);
		
		if(clNode!=null) {
			
			ograph.createEdge(ret.getFirst(), clNode, OEdge.INSTANCE_OF_EDGE);
			//indNode.set("directType",clNode);
		}
		
		
		// build relationships
		try {
			Collection<IRelationship> relations= obj.getRelationships();
			for (IRelationship relationship : relations) {		
				
				ONode relNode = getPropertyValueNode(obj, relationship);
				ONode valNode = getValueNode(relationship);
				
				if (relNode != null && valNode != null) {
					
					ograph.createEdge(ret.getFirst(), relNode, OEdge.START_EDGE);
					ograph.createEdge(relNode, valNode, OEdge.END_EDGE);
				}
			}
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return ret.getFirst();
	}
	

	public ONode getPropertyNode(IProperty prop  ) {

		ONode propNode = (ONode) ograph.findNode(prop.getURI());
		if (propNode != null)
			return propNode;
		String ns = prop.getConceptSpace(); 
		String nsprefix = getPrefix(ns);
		
		propNode =ograph.addONode();
		if(prop.isObjectProperty())
			propNode.init(prop.getURI(), nsprefix + prop.getLocalName(),ONode.REL_OBJECT_PROPERTY);
		else
			propNode.init(prop.getURI(), nsprefix + prop.getLocalName(),ONode.REL_DATA_PROPERTY);
			
		 
		setResourceDescription(prop,propNode );

		return propNode;

	}
	
	public ONode getRestrictionNode(Constraint prop,  int type){
		return null;
	}

	public ONode getPropertyValueNode(IInstance instance, IRelationship r) {
		
		String ID = null;
		int type = ONode.REL_OBJECT_PROPERTY_VALUE;
		
		if(r.isObject()){
			 ID = "OProp" + instance.getURI() + r.getProperty();
			 type = ONode.REL_OBJECT_PROPERTY_VALUE;
		} else if(r.isLiteral()){
			ID = "LProp" +instance.getURI() + r.getProperty();
			 type = ONode.REL_DATA_PROPERTY_VALUE;
		} else if(r.isClassification()){
			type = ONode.REL_OBJECT_PROPERTY_VALUE; 
			ID = "CProp" +instance.getURI() + r.getProperty();
		} 
		
		ONode newRNode = (ONode) ograph.findNode(ID);
		
		if(newRNode != null)
			return newRNode;
		
		String ns = r.getProperty().getConceptSpace();
		String nsprefix = getPrefix(ns);
		 
		newRNode  = ograph.addONode();
		newRNode.init(ID, nsprefix + r.getProperty().getLocalName(), type);
		
		setResourceDescription(r.getProperty(), newRNode );	
		 
		return newRNode;
	}

	public ONode getValueNode(IRelationship r) {

		ONode valNode=null;

		if(r.isObject()) {

			IInstance obj = null;
			
			try {
				
				obj = r.getValue().asObjectReference().getObject();
				valNode = getInstanceNode(obj);
				
			} catch (ThinklabValueConversionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		} else if(r.isLiteral()){

			valNode = ograph.addONode();
			String sval = r.getValue().toString();
			valNode.init(sval, sval, ONode.OBJ_DATATYPE);	

		} else if(r.isClassification()) {

			valNode= getConceptNode(r.getValue().getConcept());

		} 

		return valNode;
	}

}
