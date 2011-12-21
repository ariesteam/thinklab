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
package org.integratedmodelling.thinklab.webapp.view.components;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.webapp.view.QueryFormStructure;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualInstance;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Vbox;

/**
 * Generates a vbox of fields from the definition of a concept that will 
 * create a constraint on the concept's properties when submitted.
 * 
 * @author Ferdinando Villa
 *
 */
public class InstanceVisualizer extends Vbox {

	private static final long serialVersionUID = 5817053042690295462L;
	VisualInstance concept = null;
	QueryFormStructure schema = null;
	Collection<Component> components = null;
	Component mainC = this;
	private int indentLevel;
	
	protected void reset() {
		
		ArrayList<Component> cp = new ArrayList<Component>();
		
		/* erase the whole form */
		for (Object c : getChildren()) {
			cp.add((Component)c);
		}
		
		for (Component c : cp)
			c.detach();
	}
	
	/* create the form */
	protected void setup() throws ThinklabException {
		
		reset();
	
		if (schema == null) {
			/* retrieve schema from type manager */
			schema = TypeManager.get().getVisualizationStructure(concept.getInstance());
			components = schema.getVisualizationComponents(indentLevel);
		}
		
		/* TODO see if a YUI grid is better */
		Grid cGrid = null; Rows rows = null;
		
		for (Component c : components) {
			
			if (c instanceof Row) {

				if (cGrid == null) {
					cGrid = new Grid();
					rows = new Rows();
					cGrid.setWidth("100%");
					cGrid.appendChild(rows);
				}
				
				rows.appendChild(c);
				
			} else {
				
				if (cGrid != null) {
					appendChild(cGrid);
					cGrid = null;
					rows = null;
				}
				appendChild(c);
			}
			
		}

		if (cGrid != null) {
			appendChild(cGrid);
		}

		
		invalidate();
	}
	
	public InstanceVisualizer(int indentLevel) {
		this.indentLevel = indentLevel;
	}
	
	public void setObject(IInstance c) throws ThinklabException {
		schema = null;
		concept = TypeManager.get().getVisualInstance(c);
		setup();
	}
	
}


