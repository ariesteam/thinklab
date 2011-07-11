/**
 * ObjectSelector.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view.components;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponent;
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
public class ObjectSelector extends DynamicToggleComponent implements IRestrictionComponent {

	private static final long serialVersionUID = -8136433515441907285L;
	
	String conceptID = null;
	Component objComponent = null;
	VisualProperty property = null;

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
	
	public ObjectSelector(VisualProperty property, String conceptID, int indentLevel) throws ThinklabException {
		
		super(property.getLabel());
		this.property = property;	
		this.conceptID = conceptID;
		this.indentLevel = indentLevel;
	}

	public Restriction getRestriction() throws ThinklabException {
		
		Restriction ret = null;
		
		if (objComponent != null) {
			if (objComponent instanceof InstanceSelector) {
				ret = new Restriction(property.getProperty(), ((InstanceSelector)objComponent).getConstraint());
			} else if (objComponent instanceof IRestrictionComponent) {
				ret =((IRestrictionComponent)objComponent).getRestriction();
			}
		}		
		
		return ret;
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

}
