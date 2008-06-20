package org.integratedmodelling.thinklab.graph;

import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.jgrapht.graph.DefaultEdge;

public class PropertyEdge extends DefaultEdge {

	private static final long serialVersionUID = 4886595450750805232L;
	
	IProperty property = null;
	
	/** 
	 * If the property is null, we have a subclass or direct type
	 */
	public IProperty getProperty() {
		return property;
	}

	public void setProperty(IProperty property) {
		this.property = property;
	}

	public PropertyEdge() {
		// TODO Auto-generated constructor stub
	}

}
