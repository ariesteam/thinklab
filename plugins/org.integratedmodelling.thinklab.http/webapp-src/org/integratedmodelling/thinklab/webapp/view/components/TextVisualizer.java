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
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.webapp.interfaces.IVisualizationComponentConstructor;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

public class TextVisualizer extends LiteralVisualizer implements IVisualizationComponentConstructor {

	private static final long serialVersionUID = 866669186594668554L;

	Listbox opts, cs, mode;
	Textbox field;
	
	public TextVisualizer() {
		super();
	}
	
	public TextVisualizer(IProperty p) {
		super(p);	
	}
	
	@Override
	public Component createVisualizationComponent(IProperty property, String label, int indentLevel) throws ThinklabException {
		TextVisualizer ret = new TextVisualizer(property);
		ret.setWidth("100%");
		return ret;
	}

}
