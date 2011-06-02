package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.utils.FolderZiper;
import org.java.plugin.JpfException;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;

public class ThinklabProject {
	
	static HashMap<String, ThinklabProject> _projects = 
		new HashMap<String, ThinklabProject>();
	
	Plugin _plugin;
	
	public ThinklabProject(Plugin plugin) {
		this._plugin = plugin;
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
	public static Properties getThinklabPluginProperties(Plugin plugin) throws ThinklabIOException {
		
			Properties ret = null;
			File pfile = 
				new File(
					Thinklab.getPluginLoadDirectory(plugin) + 
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
		final File pluginDir = 
			new File(instDir + File.separator + "plugins" + 
				File.separator + pluginId);
		pluginDir.mkdirs();
		
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
			
			Thinklab.get().getManager().activatePlugin(pluginId);
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
		
	}
	
	public static ThinklabProject addProject(Plugin plugin) {
		ThinklabProject ret = new ThinklabProject(plugin);
		_projects.put(plugin.getDescriptor().getId(), ret);
		return ret;
	}
	
	public static ThinklabProject getProject(String id) {
		return _projects.get(id);
	}
	
	public static void removeProject(String id) {
		_projects.remove(id);
	}
	
}
