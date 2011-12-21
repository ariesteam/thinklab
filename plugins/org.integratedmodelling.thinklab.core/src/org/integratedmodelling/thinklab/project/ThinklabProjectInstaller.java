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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.project.interfaces.IProjectLoader;
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
