package org.integratedmodelling.thinklab.application;


import org.integratedmodelling.utils.JPFUtils;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;

public class ApplicationDescriptor {

	Plugin registeringPlugin;
	String id;
	String description;
	String taskClass;
	String code;
	String script;
	String language;

	public ApplicationDescriptor(Plugin plugin, Extension ext) {

		this.registeringPlugin = plugin;	
	
		this.id = ext.getParameter("name").valueAsString();
		this.description = ext.getParameter("description").valueAsString();
		this.taskClass = JPFUtils.getParameter(ext, "main-task-class");
		
		Extension.Parameter aext = ext.getParameter("declaration");
		
		if (aext != null) {
			this.code = JPFUtils.getParameter(aext, "code");
			this.script = JPFUtils.getParameter(aext, "script");
			this.language = JPFUtils.getParameter(aext, "language");
			
		}
		
	}
}
