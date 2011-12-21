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
package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.JpfException;
import org.java.plugin.Plugin;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;

public class ThinklabProject {
	
	static HashMap<String, ThinklabProject> _projects = 
		new HashMap<String, ThinklabProject>();
	
	/*
	 * CAUTION: this may stay null if the plugin was found before activation.
	 */
	Plugin _plugin = null;
	File   _location;
	Properties _properties = null;
	String _id;
	
	public ThinklabProject(Plugin plugin) throws ThinklabException {
		this._plugin = plugin;
		this._location = Thinklab.getPluginLoadDirectory(plugin);
		this._id = plugin.getDescriptor().getId();
		_properties = getThinklabPluginProperties(_location);
	}

	public ThinklabProject(File location) throws ThinklabException {
		this._location = location;
		this._id = MiscUtilities.getFileName(location.toString());
		_properties = getThinklabPluginProperties(_location);
	}

	public String getId() {
		return _id;
	}
	
	public static Properties getThinklabPluginProperties(File location) throws ThinklabException {
		Properties ret = null;
		File pfile = 
			new File(
				location + 
				File.separator + 
				"THINKLAB-INF" +
				File.separator + 
				"thinklab.properties");
		
		if (pfile.exists()) {
			try {
				ret = new Properties();
				ret.load(new FileInputStream(pfile));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return ret;

	}

	public Properties getProperties() {
		return _properties;
	}
	
	/**
	 * Get the content of THINKLAB-INF/thinklab.properties if the plugin contains that
	 * directory, or null if it doesn't. Can be used to check if a plugin is a 
	 * thinklab plugin based on the null return value.
	 * 
	 * TODO move to client library and load the library in the server package
	 * 
	 * @param plugin
	 * @return
	 * @throws ThinklabIOException
	 */
	public static Properties getThinklabPluginProperties(Plugin plugin) throws ThinklabException {
		return getThinklabPluginProperties(Thinklab.getPluginLoadDirectory(plugin));
	}

	/**
	 * If plugin exists, stop it if active, undeploy and delete its contents. Then
	 * redeploy the plugin from given archive, deploy, activate if requested, register
	 * the correspondent ThinklabProject, and return it.
	 * 
	 * @param archive
	 * @param pluginId
	 * @param activate
	 * @return
	 * @throws ThinklabException
	 */
	public static ThinklabProject deploy(File archive, final String pluginId, boolean activate)
		throws ThinklabException {
		
		String instDir = System.getProperty("thinklab.inst");

		/*
		 * undeploy first
		 */
		undeploy(pluginId);
		
		
		/*
		 * do it
		 */
		final File deployDir = new File(instDir + File.separator + "plugins");		
		Thinklab.get().logger().info("deploying " + pluginId + " in " + deployDir);

		FolderZiper.unzip(archive, deployDir);
		
		try {
			Thinklab.get().getManager().publishPlugins(
					new PluginManager.PluginLocation[]{
							new PluginManager.PluginLocation() {
								
								@Override
								public URL getManifestLocation() {
									File f = 
										new File(
											deployDir + File.separator + 
											pluginId + File.separator +
											"plugin.xml");
									try {
										return f.toURI().toURL();
									} catch (MalformedURLException e) {
										throw new ThinklabRuntimeException(e);
									}
								}
								
								@Override
								public URL getContextLocation() {
									File f = new File(deployDir + File.separator + pluginId);
									try {
										return f.toURI().toURL();
									} catch (MalformedURLException e) {
										throw new ThinklabRuntimeException(e);
									}
								}
							}
					});
			
			PluginDescriptor pd = Thinklab.get().getManager().getRegistry().getPluginDescriptor(pluginId);
			Thinklab.get().getManager().enablePlugin(pd, true);			

			// create or refresh existing descriptor
			ThinklabProject.addProject(Thinklab.get().getManager().getPlugin(pluginId));
			
		} catch (JpfException e) {
			throw new ThinklabPluginException(e);
		}
		
		return null;
	}

	/**
	 * Stop if active, disable and delete files for plugin. Do nothing if not there.
	 * 
	 * @param id
	 * @throws ThinklabException
	 */
	public static void undeploy(String id)  throws ThinklabException  {
		
		if (!Thinklab.get().getManager().getRegistry().isPluginDescriptorAvailable(id))
			return;
			
		PluginDescriptor pd = Thinklab.get().getManager().getRegistry().getPluginDescriptor(id);
		
		File pdir = new File(
				System.getProperty("thinklab.inst") + File.separator + "plugins" + File.separator +
				id);

		Thinklab.get().logger().info("undeploying " + id + " from " + pdir);
			
		if (Thinklab.get().getManager().isPluginActivated(pd))
			Thinklab.get().getManager().deactivatePlugin(id);
			
		if (Thinklab.get().getManager().isPluginEnabled(pd))
			Thinklab.get().getManager().disablePlugin(pd);
						
		Thinklab.get().getManager().getRegistry().unregister(new String[]{id});
			
		if (pdir.exists())
			MiscUtilities.deleteDirectory(pdir);

	}
	
	public File getPath() {		
		return _location;
	}
	
	public static ThinklabProject addProject(Plugin plugin) throws ThinklabException {
		ThinklabProject ret = new ThinklabProject(plugin);
		_projects.put(plugin.getDescriptor().getId(), ret);
		return ret;
	}
	
	public static ThinklabProject addProject(File plugin) throws ThinklabException {
		ThinklabProject ret = new ThinklabProject(plugin);
		_projects.put(MiscUtilities.getFileName(plugin.toString()), ret);
		return ret;
	}
	
	public static ThinklabProject getProject(String id) {
		return _projects.get(id);
	}
	
	public static void removeProject(String id) {
		_projects.remove(id);
	}

	public static Collection<ThinklabProject> getProjects() {
		return _projects.values();
	}
	
}
