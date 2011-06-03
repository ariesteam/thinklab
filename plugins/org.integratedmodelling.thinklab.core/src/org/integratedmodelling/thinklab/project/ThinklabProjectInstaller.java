package org.integratedmodelling.thinklab.project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
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
public class ThinklabProjectInstaller implements PluginManager.EventListener {

	HashMap<String, List<IProjectLoader>> _loaders = 
		new HashMap<String, List<IProjectLoader>>();
	
	@Override
	public void pluginActivated(Plugin plugin) {
		
		try {
			
			Properties prop = ThinklabProject.getThinklabPluginProperties(plugin);
			if (prop != null) {

				ArrayList<IProjectLoader> loaders = 
					new ArrayList<IProjectLoader>();	
				
				for (File f : Thinklab.getPluginLoadDirectory(plugin).listFiles()) {
					
					if (!f.isDirectory())
						continue;
							
					String folder = MiscUtilities.getFileName(f.toString());
					Class<?> plc = Thinklab.get().getProjectLoader(folder);
					
					if (plc != null) {
						IProjectLoader pl = (IProjectLoader) plc.newInstance();
						loaders.add(pl);
						pl.load(f);
					}	
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
	
		List<IProjectLoader> ls = _loaders.get(plugin.getDescriptor().getId());
		if (ls != null) {
			
			for (IProjectLoader rl : ls) {
				try {
					rl.unload(Thinklab.getPluginLoadDirectory(plugin));
					
				} catch (Exception e) {
					throw new ThinklabRuntimeException(e);
				}
			}
			
			/*
			 * clean up
			 */
			_loaders.remove(plugin.getDescriptor().getId());
		}
		
	}

	@Override
	public void pluginDisabled(PluginDescriptor arg0) {
		ThinklabProject.removeProject(arg0.getId());
	}

	@Override
	public void pluginEnabled(PluginDescriptor arg0) {
	}
}
