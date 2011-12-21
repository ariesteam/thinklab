/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.graph;

import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
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
