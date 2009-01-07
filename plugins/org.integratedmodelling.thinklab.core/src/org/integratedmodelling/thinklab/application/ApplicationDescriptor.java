package org.integratedmodelling.thinklab.application;


import java.net.URL;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
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

	public ApplicationDescriptor(Plugin plugin, Extension ext) throws ThinklabIOException {

		this.registeringPlugin = plugin;	
	
		this.id = ext.getParameter("name").valueAsString();
		this.description = ext.getParameter("description").valueAsString();
		this.taskClass = JPFUtils.getParameter(ext, "main-task-class");
		
		Extension.Parameter aext = ext.getParameter("declaration");
		
		if (aext != null) {
			this.code = JPFUtils.getParameter(aext, "code");
			String s = JPFUtils.getParameter(aext, "script");
			if (s != null)
				this.script = Thinklab.get().getResourceURL(s);
			
			this.language = JPFUtils.getParameter(aext, "language");
			
		}
		
	}
}
