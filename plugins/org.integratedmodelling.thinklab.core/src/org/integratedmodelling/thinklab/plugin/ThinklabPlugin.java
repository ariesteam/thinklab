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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IPlugin;

/**
 * The abstract base class that every plugin must implement.
 *	
 * @author Ferdinando Villa (rewrite for JIMT)
 * @author Ioannis N. Athanasiadis (contributions to ThinkLab version) 
 */
public abstract class ThinklabPlugin implements IPlugin
{
	PluginJAR jar;
	HashMap<String, URL> resources = new HashMap<String, URL>();
	static URL uninitializedResource;
	
	private static final  Logger log = Logger.getLogger(ThinklabPlugin.class);
	
	Properties properties = new Properties();
	
	public ThinklabPlugin() {
		if (uninitializedResource == null)
			try {
				uninitializedResource = new URL("http://uninitialized/resource");
			} catch (MalformedURLException e) {
				// can't happen
			}
	}

	
	public void setJar(PluginJAR jar) {
		this.jar = jar;
	}
	
	/** 
	 * Retrieve named plugin, for convenience. Normally used inside plugins, to retrieve 
	 * themselves or their dependents.
	 * @param ID the plugin name
	 * @return the plugin, or NULL if not there. 
	 */
	protected static IPlugin getPlugin(String ID) {
		IPlugin ret = null;
		try {
			ret =KnowledgeManager.get().getPluginRegistry().retrievePlugin(ID);
		} catch (ThinklabNoKMException e) {
			// should not happen
		}
		return ret;
	}
	
	public abstract void load(KnowledgeManager km, File baseReadPath, File baseWritePath) 
		throws ThinklabPluginException;
	
	
	public abstract void unload(KnowledgeManager km) throws ThinklabPluginException;

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
	
	/**
	 * Returns the plugin's class name. This might not be the same as
	 * the class of the actual <code>EditPlugin</code> instance, for
	 * example if the plugin is not loaded yet.
	 */
	public String getClassName()
	{
		return getClass().getName();
	}

	/**
	 * Returns the JAR file containing this plugin.
	 */
	public PluginJAR getPluginJAR()
	{
		return jar;
	} 
	
	/**
	 * by default, plugin properties reside in the user configuration directory.
	 */
	public File getPropertiesFilePath()  throws ThinklabException  {

		return 
			new File(LocalConfiguration.getUserConfigDirectory() + "/" + getId() + ".properties");
	}

	/* called by PluginJar when resources are found that we don't know what to do with. Merely stores the
	 * resource name and calls notifier. 
	 */
	public void addResource(String name, long time, long size) throws ThinklabException {
		resources.put(name, uninitializedResource);
		notifyResource(name, time, size);
	}

	/**
	 * Callback called for each resource which is not a class file, a plugin description file, or an OWL ontology
	 * included in the jar. Applications can decide what to do with the resource.
	 * @param name name of resource
	 * @param time time of creation of resource
	 * @param size size of resource
	 */
	public abstract void notifyResource(String name, long time, long size) throws ThinklabException;

	/**
	 * This callback is called after all plugins have been loaded and their load() has been 
	 * called in order of dependency. 
	 *
	 */
	public abstract void initialize() throws ThinklabException;
	
	/**
	 * Pass resource name, retrieve InputStream for it. Resource must be among those notified to plugin.
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public InputStream retrieveResource(String name) throws ThinklabException {

		if (resources.get(name) == null)
			throw  new ThinklabPluginException("plugin " + jar.getID() + " does not provide resource " + name);
		
		return jar.retrieveResource(name);
	}
	
	public String getId()  {
		return jar.getID();
	}
	
	public Collection<String> getAuthors() {
		return jar.getAuthors();
	}
	
	public String getDate() {
		return jar.getDate();
	}
	
	public String getDescription() {
		return jar.getDescription();
	}
	
	public void dump() {
		log.info("Plugin: "   + getId() 
				+"\tAuthor: " + getAuthors()
		        +"\tDate: "   + getDate());
		log.debug("Provides:");
		for (String s : resources.keySet()) {
			log.debug("\t" + s);
		}
	}

	public String toString(){
		return "Plugin : " + getId(); 
	}
	
	public long getResourceTime(String name) {
		return jar.getResourceTime(name);
	}
	
	public URL exportResourceCached(String name) throws ThinklabException {
		
		URL ret = resources.get(name);
		
		if (ret == null)
			throw  new ThinklabPluginException("plugin " + jar.getID() + " does not provide resource " + name);
	
		if (resources.get(name).equals(uninitializedResource))
			{
				ret = jar.saveResourceCached(name, KnowledgeManager.get().getPluginRegistry().getCacheDir());
				resources.put(name, ret);
			}
		
		return ret;
	}
	
	public String getJarClasspath() {
		
		String ret = ""; int nf = 0;
		
		for (File f : jar.getEmbeddedJarFiles()) {
			if (nf++ > 0) {
				ret += ";";
			}
			ret += f;
		}
		
		return ret;
	}
	
	public void addToClasspath(URL url) throws ThinklabException {
		PluginRegistry.get().addToClasspath(url);
	}
	
	public File getJarFile() {
		return jar.getFile();
	}
	
	public File getScratchPath() throws ThinklabException  {
		
		return PluginRegistry.get().getScratchDir(this.getId());
		
	}
	
	public File getLoadPath() throws ThinklabException  {
		
		return new File (PluginRegistry.get().getLoadDir() + "/" + getId());
		
	}
}
