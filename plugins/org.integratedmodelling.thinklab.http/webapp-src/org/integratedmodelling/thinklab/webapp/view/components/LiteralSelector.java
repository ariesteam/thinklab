/**
 * LiteralSelector.java
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
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponent;
import org.integratedmodelling.thinklab.webapp.view.TypeManager;
import org.integratedmodelling.thinklab.webapp.view.VisualProperty;
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
public abstract class LiteralSelector extends Row implements IRestrictionComponent {

	VisualProperty property = null;
	Restriction restriction = null;

	protected VisualProperty getProperty() {
		return property;
	}
	
	protected abstract void setup();
	
	/**
	 * Take status of each component in array and 
	 * redefine the restriction accordingly
	 * @throws ThinklabException 
	 */
	protected abstract Restriction defineRestriction() throws ThinklabException;
	
	public LiteralSelector(IProperty p) {
		super();
		property = TypeManager.get().getVisualProperty(p);
		setup();
	}
	
	public LiteralSelector() {
		// TODO Auto-generated constructor stub
	}

	public Restriction getRestriction() throws ThinklabException {
		restriction = defineRestriction();
		return restriction;
	}
	
	public void addPropertyField() {
		
		Label label = new Label(property.getLabel());
		appendChild(label);
	}
}
