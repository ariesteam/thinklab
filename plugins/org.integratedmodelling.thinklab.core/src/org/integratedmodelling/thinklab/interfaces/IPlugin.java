/**
 * IPlugin.java
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
package org.integratedmodelling.thinklab.interfaces;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.PluginRegistry;
import org.w3c.dom.Node;

/**
 * Any plug-in class must implement this simple interface and have a name that reflects the name of the jar file it's
 * contained into, so the KM can find the class and load it. The KM is passed to load and unload as a convenience.
 * @author Ferdinando Villa
 * @see PluginRegistry
 */
public interface IPlugin {
	
	/**
	 * load the plugin into the KM. It will typically install commands and stuff.
	 * @param km the Knowledge Manager. It's a singleton so it's not really necessary, but it's passed as
	 *           a convenience.
     * @param baseReadPath a directory where the KM stores configuration informations etc. getPluginPath should return
     * the subdirectory if baseReadPath where it will install its own resources, or null if no resources are needed.
     * @param baseWritePath is a directory where the KM allows the plugin to write into for temporary storage, in the subdir correspondend
     * to the plugin's ID. No guarantees can be made on the persistence of this dir's content across invocations.
	 * @exception ThinklabPluginException thrown as appropriate. Convert all exceptions to this one.
	 */
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath) throws ThinklabPluginException;
	
	/** 
	 * Unload the plug-in. Not expected to be useful, as the lifetime of plug-ins is typically the same as
	 * the knowledge manager's. So don't worry. Anyway, it will be dutifully called by KM.shutdown(), so if
	 * you really have something to do here, do it.
	 * @param km the Knowledge Manager. It's a singleton so it's not really necessary, but it's passed as
	 *           a convenience.
	 * @exception ThinklabPluginException thrown as appropriate. Convert all exceptions to this one.
	 */
	public void unload(KnowledgeManager km) throws ThinklabPluginException;
	
	/**
	 * Because all plug-ins must have a unique ID (defined in plugin.xml) it's only fair to
	 * be able to ask for it.
	 * 
	 * @return the plugin's ID.
	 */
	public abstract String getId();

	/**
	 * Use to check if a specific resource has been found in the JAR
	 * @param name
	 * @return
	 */
	public boolean hasResource(String name);
		
	/**
	 * Callback called for each resource which is not a class file, a plugin description file, or an OWL ontology
	 * included in the jar. Applications can decide what to do with the resource.
	 * @param name name of resource
	 * @param time time of creation of resource
	 * @param size size of resource
	 */
	public void notifyResource(String name, long time, long size) throws ThinklabException;

	/**
	 * This callback is called after all plugins have been loaded and their load() has been 
	 * called in order of dependency. 
	 *
	 */
	public void initialize() throws ThinklabException;
	
	/**
	 * Pass resource name, retrieve InputStream for it. Resource must be among those notified to plugin.
	 * @param name
	 * @return
	 * @throws IOException
	 */
	public InputStream retrieveResource(String name) throws ThinklabException;
	
	/**
	 * 
	 * @param name
	 * @return
	 * @throws ThinklabException
	 */
	public URL exportResourceCached(String name) throws ThinklabException;

	/**
	 * All plugins have a Properties object, that is filled with properties found in any
	 * .properties file found in the jar. Once the plugin has been loaded once, the plugin's
	 * user properties (with defaults provided by the <PLUGIN-ID>.properties in the jar) 
	 * are stored in the file path returned by getPropertiesFilePath().
	 * @return
	 */
	public Properties getProperties();
	
	/**
	 * Return the file where the user properties will be stored and loaded from. This can
	 * be redefined by specific plug-ins; e.g. ThinkcapPlugin will store properties in
	 * WEB-INF while "regular" ones will use user directories.
	 * @return the full path to the plugins' user properties file
	 * @throws ThinklabException if the path cannot be defined or created
	 */
	public abstract File getPropertiesFilePath() throws ThinklabException;

    /**
     * Return the classpath portion of jar files embedded in plugin. 
     * @return
     */
	public String getJarClasspath();
	
	/**
	 * Return the jarfile we come from 
	 */
	public File getJarFile();
	
	/**
	 * Return the plugin-specific "scratch" directory, which is guaranteed to exist. Plugins can write in
	 * it liberally. Content is not guaranteed to remain there between invocations.
	 * @return
	 * @throws ThinklabException
	 */
	public File getScratchPath() throws ThinklabException;
	
	/**
	 * Return the plugin-specific load directory where plugin resources are unpacked, which is guaranteed 
	 * to exist. Plugins typically can not write in it.
	 * @return
	 * @throws ThinklabException
	 */
	public File getLoadPath() throws ThinklabException;

	/**
	 * Called when a node in plugin.xml is not understood by the main plugin 
	 * code. This only happens with top-level nodes.
	 * 
	 * @param n
	 */
	public void notifyConfigurationNode(Node n);
}
