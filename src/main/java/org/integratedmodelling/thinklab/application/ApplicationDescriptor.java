///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.application;
//
//
//import java.net.URL;
//import java.util.ArrayList;
//
//import org.integratedmodelling.exceptions.ThinklabIOException;
//import org.integratedmodelling.thinklab.owlapi.Session;
//import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
//import org.integratedmodelling.utils.JPFUtils;
//import org.java.plugin.Plugin;
//import org.java.plugin.registry.Extension;
//
//public class ApplicationDescriptor {
//
//	Plugin registeringPlugin;
//	String id;
//	String description;
//	String taskClass;
//	String code;
//	ArrayList<URL> scripts = new ArrayList<URL>();
//	String language;
//	String sessionClass;
//
//	public ApplicationDescriptor(Plugin plugin, Extension ext) throws ThinklabIOException {
//
//		this.registeringPlugin = plugin;	
//	
//		this.id = ext.getParameter("id").valueAsString();
//		this.description = JPFUtils.getParameter(ext, "description");
//		this.taskClass = JPFUtils.getParameter(ext, "main-task-class");
//		this.sessionClass = JPFUtils.getParameter(ext, 
//					"session-class", Session.class.getCanonicalName());
//		
//		Extension.Parameter aext = ext.getParameter("declaration");
//		
//		if (aext != null) {
//			
//			this.code = JPFUtils.getParameter(aext, "code");
//			String[] ss = JPFUtils.getParameters(aext, "script");
//			
//			if (ss != null) {
//				
//				for (String s: ss) {
//					
//					URL url = ((ThinklabPlugin)plugin).getResourceURL(s);
//					if (url == null) {
//						throw new ThinklabIOException("application script " + s + " not found in classpath");
//					}
//					this.scripts.add(url);
//				}
//			}
//			
//			this.language = JPFUtils.getParameter(aext, "language");
//		}
//		
//	}
//}
