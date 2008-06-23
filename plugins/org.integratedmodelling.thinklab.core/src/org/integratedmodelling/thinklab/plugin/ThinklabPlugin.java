/**
 * Plugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.plugin;

/*
 * Plugin.java - Abstract class all plugins must implement
 *
 * Copyright (C) 1999, 2003 Slava Pestov
 * Adapted 2006 Ferdinando Villa
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Copyright (C) 2006, 2007 The Ecoinformatics Collaboratory, UVM
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.utils.CopyURL;
import org.java.plugin.Plugin;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.ExtensionPoint;


/**
 * A specialized JPF plugin to support extension of the knowledge manager.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class ThinklabPlugin extends Plugin
{
	HashMap<String, URL> resources = new HashMap<String, URL>();
	Properties properties = new Properties();
	
	private File dataFolder;
	private File confFolder;
	private File plugFolder;
	

	/**
	 * Demand plugin-specific initialization to this callback; 
	 * we intercept doStart
	 */
	abstract protected void load();
	
	abstract protected void unload();
	
	/**
	 * Any extensions other than the ones handled by default should be handled here.
	 */
	protected void loadExtensions() {
		
	}
	
	protected String getPluginBaseName() {
		String[] sp = getDescriptor().getId().split("\\.");
		return sp[sp.length - 1];
	}
	
	@Override
	protected final void doStart() throws Exception {
		
		loadConfiguration();
		
		/*
		 * Check if we have a KM and if not, put out a good explanation of why we should
		 * read the manual, if there was one.
		 */
		
		loadOntologies();
		loadLiteralValidators();
		loadKBoxHandlers();
		loadKnowledgeImporters();
		loadKnowledgeImporters();
		loadLanguageInterpreters();
		loadCommands();
		loadInstanceImplementationConstructors();
		
		loadExtensions();
		
		load();
	}

	private void loadConfiguration() throws ThinklabIOException {
	
       plugFolder = LocalConfiguration.getDataDirectory(getDescriptor().getId());
       confFolder = new File(plugFolder + File.separator + "config");
       dataFolder = new File(plugFolder + File.separator + "data");
	
       /*
        * make sure we have all paths
        */
       if (
    		   (!plugFolder.isDirectory() && !plugFolder.mkdirs()) || 
    		   (!confFolder.isDirectory() && !confFolder.mkdirs()) || 
    		   (!dataFolder.isDirectory() && !dataFolder.mkdirs()))
    	   throw new ThinklabIOException("problem writing to plugin directory: " + plugFolder);
       
		/*
		 * check if plugin contains a <pluginid.properties> file
		 */
       String configFile = getPluginBaseName() + ".properties";
       File pfile = new File(confFolder + File.separator + configFile);
       
       if (!pfile.exists()) {
    	   
    	   /*
    	    * copy stock properties if existing
    	    */
    	   URL sprop = getResourceURL("config/" + configFile);
    	   if (sprop != null)
    		   CopyURL.copy(sprop, pfile);
    	   
       } 
       
       
       if (pfile.exists()) {
    	   try {
			properties.load(new FileInputStream(pfile));
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
       }
		
	}

	private Iterator<Extension> getExtensions(String extensionPoint) {
		
		ExtensionPoint toolExtPoint = 
			getManager().getRegistry().getExtensionPoint(getDescriptor().getId(), extensionPoint);

		return toolExtPoint.getConnectedExtensions().iterator();
	}
	
	private void loadInstanceImplementationConstructors() {
		
		for (Iterator<Extension> it = getExtensions("instance-constructor"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
	}

	/**
	 * Retrieve an URL for the named resource: if the resource string represents a URL, return the
	 * url constructed from it; otherwise, check if the resource string represents an existing
	 * file path. If so, create a file url from it and return it. Otherwise, construct a URL from
	 * the plugin path and the resource name and return that.
	 * 
	 * @param resource
	 * @return
	 * @throws ThinklabIOException 
	 */
	protected URL getResourceURL(String resource) throws ThinklabIOException 	{

		URL ret = null;
		
		try {
			
			File f = new File(resource);
			
			if (f.exists()) {
				ret = f .toURI().toURL();
			} else if (resource.contains("://")) {
				ret = new URL(resource);
			} else {			
				ret =  getManager().getPluginClassLoader(getDescriptor()).getResource(resource);
			}
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}
	
	private void loadOntologies() throws ThinklabException {
	
		for (Iterator<Extension> it = getExtensions("ontology"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();

			KnowledgeManager.get().getKnowledgeRepository().refreshOntology(getResourceURL(url), csp);
		}
		
	}

	private void loadLiteralValidators() {
		
		for (Iterator<Extension> it = getExtensions("literal-validator"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}

	}

	private void loadLanguageInterpreters() {
		
		for (Iterator<Extension> it = getExtensions("language-interpreter"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
		
	}

	private void loadKnowledgeImporters() {
	
		for (Iterator<Extension> it = getExtensions("knowledge-importer"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
		
	}

	private void loadKBoxHandlers() {
		
		for (Iterator<Extension> it = getExtensions("kbox-handler"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
		
	}

	@Override
	protected final void doStop() throws Exception {
		
		unload();
	}
	
	private void loadCommands() {

		for (Iterator<Extension> it = getExtensions("command-handler"); it.hasNext();) {

			Extension ext = it.next();
			String url = ext.getParameter("url").valueAsString();
			String csp = ext.getParameter("concept-space").valueAsString();
			
			// TODO
		}
	}
	
	/**
	 * Use to check if a specific resource has been found in the JAR
	 * @param name
	 * @return
	 */
	public boolean hasResource(String name) {
		return resources.get(name) != null;
	} 
	
	/**
	 * Return the plugin properties, read from any .properties file in distribution.
	 * @return the plugin Properties. It's never null.
	 */
	public Properties getProperties() {
		return properties;
	}
	
	
	public URL exportResourceCached(String name) throws ThinklabException {
		
		URL ret = resources.get(name);
		
		if (ret == null)
			throw  new ThinklabPluginException("plugin " + getDescriptor().getId() + " does not provide resource " + name);
	
		// TODO see what the Jar thing 
		//ret = jar.saveResourceCached(name, KnowledgeManager.get().getPluginRegistry().getCacheDir());
		resources.put(name, ret);
		
		return ret;
	}
	
	public File getScratchPath() throws ThinklabException  {
		
		return PluginRegistry.get().getScratchDir(this.getDescriptor().getId());
		
	}
	
	public File getLoadPath() throws ThinklabException  {
		
		return new File (PluginRegistry.get().getLoadDir() + "/" + getDescriptor().getId());
		
	}
}
