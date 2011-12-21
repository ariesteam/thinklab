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

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

/**
 * A component that shows a label with an arrow next to it. Clicking the label 
 * toggles visibility of the other components besides the label.
 * @author Ferdinando Villa
 *
 */
public class ToggleComponent extends Vbox {

	private static final long serialVersionUID = 6577679803374746854L;
	private boolean isOpen = false;
	private ToggleLabel mainLabel = null;
	private Component[] components = null;
	
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
		
		public void onClick() {
			isOpen = !isOpen;
			setValue((isOpen ? "\u25BC " : "\u25BA ") + getValue().substring(2));
			invalidate();
			setup();
		}	
	}
	
	public void addToggledComponent(Component c) {
		
		Component[] ncomp = new Component[components == null ? 1 : (components.length + 1)];
		int n = 0;
		if (components != null)
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

	public void setOpen(boolean open) {
		isOpen = open;
		setup();
	}
	
	public ToggleComponent(String label, Component ... children) {
		super();
		labelValue = label;
		components = children;
		setup();
	}
	
	public ToggleComponent(String label) {
		super();
		labelValue = label;
		setup();
	}
	
	public void setLabel(String label) {
		labelValue = label;
		setup();
	}
	
	private void setup() {
		
		reset();
		
		mainLabel = new ToggleLabel(labelValue);
		mainLabel.setStyle(labelStyle);
		mainLabel.setWidth("100%");
	
		// insert label as first child
		this.appendChild(mainLabel);
		
		/* small separator */
		Separator s = new Separator();
		s.setHeight("4px"); s.setBar(true);  s.setWidth("100%"); s.setVisible(isOpen);
		appendChild(s);
		
		if (components != null)
			for (Component c : components) {
				c.setVisible(isOpen);
				appendChild(c);
			}
		
		/* some more space please */
		s = new Separator();
		s.setHeight("4px"); s.setBar(true); s.setWidth("100%"); s.setVisible(isOpen);
		appendChild(s);
			
		invalidate();
	}
	
	public void setLabelStyle(String style) {
		labelStyle = style;
		setup();
	}
	
}
