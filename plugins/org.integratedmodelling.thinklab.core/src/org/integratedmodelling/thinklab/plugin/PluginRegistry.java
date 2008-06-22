/**
 * PluginRegistry.java
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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.IPlugin;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.TopologicalSorter;

/**
 * A registry for plugins. Each plugin is loaded as a jar file and its
 * dependencies with others are loaded from the jar manifest. The registry
 * manages the set of plugins and loads them in order of dependency. Circular
 * dependencies generate exceptions.
 * 
 * 
 * 
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 * @see IPlugin
 * @see ThinklabPlugin
 */
public class PluginRegistry {

	Logger log = Logger.getLogger(PluginRegistry.class);

	HashMap<String, IPlugin> plugins = new HashMap<String, IPlugin>();
	ArrayList<File> pluginDirs = new ArrayList<File>();
	ArrayList<PluginJAR> jarFiles = new ArrayList<PluginJAR>();
	ArrayList<IPlugin> loadOrder = new ArrayList<IPlugin>();

	File cacheDir;
	File scratchDir;
	File loadDir;
	File libDir;
	PluginClassLoader loader;

	public PluginClassLoader getClassLoader() throws ThinklabIOException {

		if (loader == null) {

			URL[] urls = new URL[1];
			urls[0] = MiscUtilities.getURLForResource(loadDir.toString());

			loader = 
				new PluginClassLoader(
					urls,
					KnowledgeManager.class.getClassLoader());
		}
		return loader;
	}

	/**
	 * Return the directory seen by the class loader. This should be directly in
	 * the classpath. To be used (with caution) to make non-class resources
	 * embedded in plugins available in classpath.
	 * 
	 * @return
	 */
	public File getLoadDir() {
		return loadDir;
	}


	/**
	 * Add a resource to the classpath. Only the base name of a passed URL is used - no directory structure
	 * is created.
	 * 
	 * @param url
	 * @throws ThinklabIOException
	 */
	public void addToClasspath(URL url) throws ThinklabIOException {
		
		CopyURL.copy(url, new File(loadDir + "/" + MiscUtilities.getFileName(url.toString())));
	}
	
	public PluginRegistry() throws ThinklabIOException {

		cacheDir = LocalConfiguration.getDataDirectory("plugins/cache");
		scratchDir = LocalConfiguration.getDataDirectory("plugins/scratch");
	}

	/**
	 * Get the specific scratch directory for given plugin. Plugins can write in
	 * it to their heart's content. While it is not deleted upon finalization,
	 * plugins should not count on the contents being preserved between
	 * sessions.
	 * 
	 * @param plugID
	 *            ID of the plugin
	 * @return a valid, existing, writable directory
	 * @throws ThinklabPluginException
	 *             if the plugin is not installed.
	 */
	public File getScratchDir(String plugID) throws ThinklabPluginException {

		if (plugins.get(plugID) == null) {
			throw new ThinklabPluginException(
					"can't retrieve plugin directory for unknown plugin "
							+ plugID);
		}
		return new File(scratchDir + "/" + plugID);
	}

	/**
	 * Get the only instance of the plugin registry.
	 * 
	 * @return the plugin registry
	 * @throws ThinklabNoKMException
	 *             if no knowledge manager was initialized.
	 */
	static public PluginRegistry get() throws ThinklabNoKMException {
		return KnowledgeManager.get().getPluginRegistry();
	}

	/**
	 * Add a directory to the plugin search path.
	 * 
	 * @param pluginDir
	 */
	public void addPluginDirectory(File pluginDir) {
		pluginDirs.add(pluginDir);
	}

	/**
	 * 
	 * @param jarClassPath a 
	 * @param classPath
	 * @throws ThinklabException
	 */
	public void initialize(File jarClassPath, File classPath) throws ThinklabException {

		loadDir = classPath;
		libDir = jarClassPath;
		
		int jarcount = 0;
		HashMap<String, Integer> mapper = new HashMap<String, Integer>();

		/* scan plugin dirs */
		for (File dir : pluginDirs) {

			/* load all Jars in plugin dir */
			if (!dir.isDirectory())
				continue;

			log.info("Plugins are read from " + dir);

			for (File f : dir.listFiles()) {
				if (f.canRead() && f.toString().endsWith(".jar")) {

					log.debug("found plugin jar " + f);

					PluginJAR pjar = new PluginJAR(f, loadDir, libDir);
					jarFiles.add(jarcount++, pjar);
					mapper.put(pjar.getID(), new Integer(jarcount - 1));
				}
			}
		}

		log.info("found " + jarFiles.size() + " plugin jar files");

		/* topologically sort plugins */
		TopologicalSorter sorter = new TopologicalSorter(jarFiles.toArray());

		for (int ip = 0; ip < jarFiles.size(); ip++) {

			PluginJAR jar = jarFiles.get(ip);

			for (String ds : jar.getDependencies()) {
				Integer idx = mapper.get(ds);
				if (idx == null)
					throw new ThinklabPluginException("plugin " + jar.getID()
							+ " requires unknown plugin " + ds);

				log.debug("Plugin " + jar.getID() + " wants " + ds);

				sorter.addDependency(idx, ip);
			}
		}

		Object[] psort = sorter.sort();

		log.info("sorted " + psort.length + " plugins");

		/* activate all plugins in order of dependency */
		for (Object oo : psort) {
			
			PluginJAR plug = (PluginJAR) oo;

			log.info("Processing plugin = " + plug.getFile());

			Plugin plugin = null;
			try {
				String classname = plug.getMainClassName();
				Class<?> clazz = getClassLoader().loadClass(classname);
				plugin = (Plugin)clazz.newInstance();
			} catch (Exception e) {
				throw new ThinklabPluginException(e);
			}

			if (plugin != null) {
				plugin.setJar(plug);
			}

			// we store even if it's null
			plugins.put(plug.getID(), plugin);

			File cd = new File(cacheDir + "/" + plug.getID());
			File sd = new File(scratchDir + "/" + plug.getID());
			File od = new File(cacheDir + "/" + plug.getID() + "/ontologies");
			
			cd.mkdir();
			sd.mkdir();
			od.mkdir();

			plug.loadProperties(plugin);

			/* make sure plugin ontologies are there first */
			plug.loadOntologies(plugin,
					KnowledgeManager.get().getKnowledgeRepository(),
					od);
			
			/* load plugin-specific code */
			if (plugin != null) {

				/*
				 * take all metadata declarations from the plugins' properties
				 */
				KBoxManager.get().defineMetadataTypes(plugin.getProperties());

				plugin.load(KnowledgeManager.get(), cd, sd);

				/* have JAR tell us what else is there */
				plug.notifyResources((Plugin) plugin);
				
				/* and notify any mysterious spec in plugin.xml */
				plug.notifyConfigurationNodes(plugin);
			
			}

			/* preserve the load order, you know, for the children */
			loadOrder.add(plugin);
		}

		/*
		 * call initialize() on all plugins in load order after they've all been
		 * loaded
		 */
		for (IPlugin plu : loadOrder) {
			if (plu != null) {
				plu.initialize();
				log.info(plu + " initialized successfully");
			}
		}
	}

	public File getCacheDir() {
		return cacheDir;
	}

	/**
	 * Return an array of all plugins, in loading order.
	 * 
	 * @return the plugin array.
	 */
	public ArrayList<IPlugin> getPlugins() {
		return loadOrder;
	}

	/**
	 * Retrieve the named plugin
	 * 
	 * @param name
	 * @return
	 */
	public IPlugin retrievePlugin(String name) {
		return plugins.get(name);
	}
}
