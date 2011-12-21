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
package org.integratedmodelling.thinklab.webapp.view;

import java.util.ArrayList;

import org.integratedmodelling.thinklab.http.ThinklabWebPlugin;
import org.integratedmodelling.thinklab.http.utils.JPFUtils;
import org.java.plugin.registry.Extension;

public class PortletDescriptor {

	private String id;
	public ThinklabWebPlugin registeringPlugin;
	public String view;
	public String url;
	
	public ArrayList<StateD> states = new ArrayList<StateD>();
	
	public class StateD {
		public String width;
		public String height;
		public String id;
	}
	
	public PortletDescriptor(ThinklabWebPlugin plugin, Extension ext) {
		
		this.id = ext.getParameter("id").valueAsString();
		this.view = JPFUtils.getParameter(ext, "class");
		this.url = JPFUtils.getParameter(ext, "url");
		this.registeringPlugin = plugin;
		
		for (Extension.Parameter aext : ext.getParameters("state")) {
			
			StateD st = new StateD();
			st.width = aext.getSubParameter("width").valueAsString();
			st.height = aext.getSubParameter("height").valueAsString();
			st.id = aext.getSubParameter("id").valueAsString();
			
			states.add(st);
		}		
	}

	public String getId() {
		return id;
	}
}
