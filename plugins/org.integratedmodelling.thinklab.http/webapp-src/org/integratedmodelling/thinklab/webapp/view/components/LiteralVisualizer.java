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

import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.webapp.interfaces.IVisualizationComponent;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualProperty;
import org.zkoss.zhtml.Text;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;

/**
 * A specialized grid row that contains a literal field search form, created on the 
 * basis of a property. It is capable of returning the restriction that is defined
 * through the form.
 * 
 * The form has a maximum of five fields: 
 * 
 * 1. the property descriptor (right-aligned with tooltip),
 * 2. a field for controls that specify how the value is matched
 * 3. A lookup value field
 * 4. an option field for things like case sensitivity, etc.
 * 5. an "help" field for little icons for further help
 * 
 * Leave empty as desired, but don't mix meanings or the final form will be 
 * quite messy. For special needs spans should be employed as appropriate.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class LiteralVisualizer extends Row implements IVisualizationComponent {

	VisualProperty property = null;
	Restriction restriction = null;
	IValue value;

	protected VisualProperty getProperty() {
		return property;
	}
	
	protected void setup() {
		
		addPropertyField();
		addValueField();
		addOptions();
	}
		
	public LiteralVisualizer(IProperty p) {
		super();
		property = TypeManager.get().getVisualProperty(p);
		this.value = null;
	}
	
	public void setValue(IValue value) {
		this.value = value;
		setup();
	}
	
	public LiteralVisualizer() {
		// TODO Auto-generated constructor stub
	}
	
	public void addPropertyField() {
		
		Label label = new Label(property.getLabel());
		appendChild(label);
	}
	
	public void addValueField() {
		
		Div div = new Div();
		
		div.setWidth("100%");
		div.appendChild(new Text(value.toString()));
		
		appendChild(div);
	}
	
	private void addOptions() {		
	}

	@Override
	public String getSpans() {
		return ",2";
	}

}
