/**
 * DynamicToggleComponent.java
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

import java.util.ArrayList;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

/**
 * A component that shows a label with an arrow next to it. Clicking the label 
 * creates or destroys components below it, as specified in a virtual function.
 * @author Ferdinando Villa
 *
 */
public abstract class DynamicToggleComponent extends Vbox {

	private static final long serialVersionUID = 6577679803374746854L;
	private boolean isOpen = false;
	private ToggleLabel mainLabel = null;
	Component[] components = null;
	
	// TODO some sensible default here
	private String labelStyle = "font-size: 10pt; font-weight: bold;";
	private String labelValue = "";
	
	public class ToggleLabel extends Label {
		
		private static final long serialVersionUID = 1376800030951568184L;

		public ToggleLabel(String value) {
			super((isOpen ? "\u25BC " : "\u25BA ") + value);
			setWidth("100%");
			setStyle(labelStyle);
		}
		
		public void onClick() throws ThinklabException {
			isOpen = !isOpen;
			setValue((isOpen ? "\u25BC " : "\u25BA ") + getValue().substring(2));
			invalidate();
			setup();
		}	
	}
	
	public void addToggledComponent(Component c) throws ThinklabException {
		
		Component[] ncomp = new Component[components.length + 1];
		int n = 0;
		while (n < components.length) {
			ncomp[n] = components[n];
			n++;
		}
		ncomp[n] = c;
		components = ncomp;
		setup();
	}
	
	protected void reset() {
		
		ArrayList<Component> cp = new ArrayList<Component>();
		
		/* erase the whole form */
		for (Object c : getChildren()) {
			cp.add((Component)c);
		}
		
		for (Component c : cp)
			c.detach();
	}

	public DynamicToggleComponent(String label, Component ... children) throws ThinklabException {
		super();
		labelValue = label;
		components = children;
		setup();
	}
	
	public DynamicToggleComponent(String label) throws ThinklabException {
		super();
		labelValue = label;
		setup();
	}
	
	public void setLabel(String label) throws ThinklabException {
		labelValue = label;
		setup();
	}
	
	protected void setup() throws ThinklabException {
		
		reset();
		
		mainLabel = new ToggleLabel(labelValue);
		mainLabel.setStyle(labelStyle);
	
		// insert label as first child
		this.appendChild(mainLabel);
		
		/* small separator */
		Separator s = new Separator();
		s.setHeight("4px"); s.setBar(true);  s.setWidth("100%"); s.setVisible(isOpen);
		appendChild(s);
		
		if (isOpen) {
		
			if (components == null)
				components = createToggledComponents();
			
			for (Component c : components) {
				c.setVisible(true);
				appendChild(c);
			}
		} else {
			
			components = null;
			notifyComponentsDetached();
			
		}
		
		/* some more space please */
		s = new Separator();
		s.setHeight("4px"); s.setBar(true); s.setWidth("100%"); s.setVisible(isOpen);
		appendChild(s);
			
		invalidate();
	}
	
	/**
	 * Create the components we want to toggle. Called only when the arrow is clicked to 
	 * show the components.
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	protected abstract Component[] createToggledComponents() throws ThinklabException;
	
	protected abstract void notifyComponentsDetached();
	
	public void setLabelStyle(String style) throws ThinklabException {
		labelStyle = style;
		setup();
	}
	
}
