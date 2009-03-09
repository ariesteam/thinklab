package org.integratedmodelling.thinklab.application;


import java.net.URL;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.JPFUtils;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;

public class ApplicationDescriptor {

	Plugin registeringPlugin;
	String id;
	String description;
	String taskClass;
	String code;
	URL script;
	String language;
	String sessionClass;

	public ApplicationDescriptor(Plugin plugin, Extension ext) throws ThinklabIOException {

		this.registeringPlugin = plugin;	
	
		this.id = ext.getParameter("id").valueAsString();
		this.description = JPFUtils.getParameter(ext, "description");
		this.taskClass = JPFUtils.getParameter(ext, "main-task-class");
		this.sessionClass = JPFUtils.getParameter(ext, 
					"session-class", Session.class.getCanonicalName());
		
		Extension.Parameter aext = ext.getParameter("declaration");
		
		if (aext != null) {
			
			this.code = JPFUtils.getParameter(aext, "code");
			String s = JPFUtils.getParameter(aext, "script");
			
			if (s != null) {
				this.script = ((ThinklabPlugin)plugin).getResourceURL(s);
			
				if (this.script == null) 
					throw new ThinklabIOException("application script " + s + " not found in classpath");
			}
			
			this.language = JPFUtils.getParameter(aext, "language");
		}
		
	}
}
