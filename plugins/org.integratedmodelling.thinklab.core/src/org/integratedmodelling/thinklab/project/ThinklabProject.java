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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.api.project.IProject;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;
import org.java.plugin.JpfException;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;

public class ThinklabProject implements IProject {
	
	/*
	 * CAUTION: this may stay null if the plugin was found before activation.
	 */
	Plugin _plugin = null;
	File   _location;
	Properties _properties = null;
	String _id;
	private ArrayList<INamespace> _namespaces;
	
	public ThinklabProject(Plugin plugin) throws ThinklabException {
		this._plugin = plugin;
		this._location = Thinklab.getPluginLoadDirectory(plugin);
		this._id = plugin.getDescriptor().getId();
		_properties = getThinklabPluginProperties(_location);
		load();
	}

	public ThinklabProject(File location) throws ThinklabException {
		this._location = location;
		this._id = MiscUtilities.getFileName(location.toString());
		_properties = getThinklabPluginProperties(_location);
	}

	public String getId() {
		return _id;
	}
	
	public Plugin getPlugin() {
		return _plugin;
	}
	
	public static Properties getThinklabPluginProperties(File location) throws ThinklabException {
		Properties ret = null;
		File pfile = 
			new File(
				location + 
				File.separator + 
				"META-INF" +
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

	@Override
	public Properties getProperties() {
		return _properties;
	}
		
	/**
	 * Get the content of THINKLAB-INF/thinklab.properties if the plugin contains that
	 * directory, or null if it doesn't. Can be used to check if a plugin is a 
	 * thinklab plugin based on the null return value.
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
			ProjectFactory.get().registerProject(Thinklab.get().getManager().getPlugin(pluginId));
			
		} catch (JpfException e) {
			throw new ThinklabInternalErrorException(e);
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


	@Override
	public Collection<INamespace> getNamespaces() {
		return _namespaces;
	}

	@Override
	public Collection<File> getSourceFolders() {
		String[] folders = getProperties().getProperty(IProject.SOURCE_FOLDER_PROPERTY, "src").split(",");
		ArrayList<File> ret = new ArrayList<File>();
		for (String f : folders) {
			ret.add(new File(getPath() + File.separator + f));
		} 
		return ret;
	}
	
	public Collection<String> getSourceFolderNames() {
		String[] folders = getProperties().getProperty(IProject.SOURCE_FOLDER_PROPERTY, "src").split(",");
		ArrayList<String> ret = new ArrayList<String>();
		for (String f : folders) {
			ret.add(f);
		} 
		return ret;
	}

	@Override
	public String getOntologyNamespacePrefix() {
		return getProperties().getProperty(
				IProject.ONTOLOGY_NAMESPACE_PREFIX_PROPERTY, "http://www.integratedmodelling.org/ns");
	}
	

	public void load() throws ThinklabException {
	
		_namespaces = new ArrayList<INamespace>();
		HashSet<File> read = new HashSet<File>();
		
		for (File dir : this.getSourceFolders()) {
		
			if (!dir.isDirectory() || !dir.canRead()) {
				throw new ThinklabIOException("source directory " + dir + " is unreadable");
			}	 
		
			loadInternal(dir, read, _namespaces, "", this);
		}
		
	}

	private void loadInternal(File f, HashSet<File> read, ArrayList<INamespace> ret, String path,
			IProject project) throws ThinklabException {

		if (f. isDirectory()) {

			String pth = path.isEmpty() ? "" : (path + "." + MiscUtilities.getFileBaseName(f.toString()));

			for (File fl : f.listFiles()) {
				loadInternal(fl, read, ret, pth, project);
			}
			
		} else if (f.toString().endsWith(".owl")) {

			INamespace ns = ModelManager.get().loadFile(f.toString(), path + "." + MiscUtilities.getFileBaseName(f.toString()), this);
			ret.add(ns);
			
		} else if (f.toString().endsWith(".tql") || f.toString().endsWith(".clj")) {

			INamespace ns = ModelManager.get().loadFile(f.toString(), path + "." + MiscUtilities.getFileBaseName(f.toString()), this);
			ret.add(ns);
		}
		
	}

	@Override
	public void addDependency(String plugin, boolean reload)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Collection<IProject> getPrerequisiteProjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File findResource(String resource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File findResourceForNamespace(String namespace, String extension) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
