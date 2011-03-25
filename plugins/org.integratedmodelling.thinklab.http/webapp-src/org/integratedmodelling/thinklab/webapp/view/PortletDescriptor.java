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
