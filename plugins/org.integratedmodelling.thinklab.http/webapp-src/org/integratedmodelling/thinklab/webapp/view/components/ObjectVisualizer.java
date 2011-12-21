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

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.webapp.interfaces.IVisualizationComponent;
import org.integratedmodelling.thinklab.webapp.view.VisualProperty;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * A collapsible label with a whole object form hidden under it; any action in 
 * the form below generates a restriction with the whole sub-constraint in it.
 * 
 * @author Ferdinando Villa
 */
public class ObjectVisualizer extends DynamicToggleComponent implements IVisualizationComponent {

	private static final long serialVersionUID = -8136433515441907285L;
	
	String conceptID = null;
	Component objComponent = null;
	VisualProperty property = null;
	IInstance object;

	private int indentLevel;
	
	private void os_setup() throws ThinklabException {
		setup();
	}
	
	public class ObjectClassSelector extends KnowledgeSelector {

		private static final long serialVersionUID = -1344892358289931282L;

		public ObjectClassSelector(String conceptID, int indentLevel) {
			super(conceptID);
		}


		@Override
		public void notifyKnowledgeSelected(String selected) throws ThinklabException {
			conceptID = selected;
			components = createToggledComponents();
			os_setup();
		}
	}
	
	public ObjectVisualizer(VisualProperty property, String conceptID, int indentLevel) throws ThinklabException {
		
		super(property.getLabel());
		this.property = property;	
		this.conceptID = conceptID;
		this.indentLevel = indentLevel;
	}

	@Override
	protected Component[] createToggledComponents() throws ThinklabException {
		
		/* 
		 * create a classification tree for all the subclasses of the 
		 * main type, and link the select event to the redefinition of the sub-form.
		 * 
		 * A bit messy, mostly for aesthetics, which is the whole point for usability here.
		 */
		Hbox classel = new Hbox();

		classel.setWidth("100%");
		
		Window wc = new Window();
		wc.setWidth("100%");
		wc.setBorder("normal");
		
		classel.appendChild(new Label("restrict type to "));
		classel.appendChild(new ObjectClassSelector(conceptID, indentLevel));
		
		/* TODO we must track the recursion level and insert an "indent" component if > 0 */
		
		InstanceSelector subc = new InstanceSelector(indentLevel);
		subc.setConcept(conceptID);
		subc.setWidth("100%");
		objComponent = subc;
		
		Vbox amo = new Vbox(); amo.setWidth("100%");
		Hbox uff = new Hbox(); uff.setWidth("100%");
	
		amo.appendChild(classel);
		amo.appendChild(subc);
		wc.appendChild(amo);
		
		if (indentLevel > 0) {
			Separator sep = new Separator("vertical");
			sep.setWidth((30*indentLevel) + "px");
			uff.appendChild(sep);
		}
		uff.appendChild(wc);
		
		Component[] c = new Component[1];
		c[0] = uff;
		
		return c;
		
	}

	@Override
	protected void notifyComponentsDetached() {
		objComponent = null;
	}

	@Override
	public void setValue(IValue value) throws ThinklabException {
		try {
			object = value.asObjectReference().getObject();
		} catch (ThinklabValueConversionException e) {
		}
		os_setup();
	}

}
