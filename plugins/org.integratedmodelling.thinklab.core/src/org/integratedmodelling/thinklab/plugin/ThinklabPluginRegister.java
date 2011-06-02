package org.integratedmodelling.thinklab.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.IResourceLoader;
import org.integratedmodelling.thinklab.project.ThinklabProject;
import org.java.plugin.Plugin;
import org.java.plugin.PluginManager;
import org.java.plugin.registry.PluginDescriptor;

/**
 * One of these is installed by thinklab to allow resource notification by
 * plugins that have no java methods, therefore do not implement ThinklabPlugin.
 * 
 * Such plugins may have a THINKLAB-INF/thinklab.properties dir that lists
 * loader classes to use on the plugin. These loaders will be generated at
 * plugin activations and stored here. Their load() and unload() method 
 * will be called appropriately.
 * 
 * @author ferdinando.villa
 *
 */
public class ThinklabPluginRegister implements PluginManager.EventListener {

	HashMap<String, List<IResourceLoader>> _loaders = 
		new HashMap<String, List<IResourceLoader>>();
	
	@Override
	public void pluginActivated(Plugin plugin) {
		
		try {
			
			Properties prop = ThinklabProject.getThinklabPluginProperties(plugin);
			if (prop != null) {

				List<IResourceLoader> loaders = 
					new ArrayList<IResourceLoader>();	
				/*
				 * find loader classes and instantiate them from installed
				 * instances (don't use classloader).
				 */
				
				
				for (IResourceLoader rl : loaders) {
					/*
					 * load content from each plugin
					 */
					rl.load(prop, Thinklab.getPluginLoadDirectory(plugin));
				}
				
				/*
				 * store loader for the plugin so we can call unload()
				 * at shutdown.
				 */
				_loaders.put(plugin.getDescriptor().getId(), loaders);
			}
			
			
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		}
		
	}

	@Override
	public void pluginDeactivated(Plugin plugin) {
	
		List<IResourceLoader> ls = _loaders.get(plugin.getDescriptor().getId());
		if (ls != null) {
			
			for (IResourceLoader rl : ls) {
				try {
					rl.unload(ThinklabProject.getThinklabPluginProperties(plugin),
							  Thinklab.getPluginLoadDirectory(plugin));
				} catch (Exception e) {
					throw new ThinklabRuntimeException(e);
				}
			}
		}
		
	}

	@Override
	public void pluginDisabled(PluginDescriptor arg0) {
	}

	@Override
	public void pluginEnabled(PluginDescriptor arg0) {
	}

}
