package org.integratedmodelling.thinklab.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.xeoh.plugins.base.PluginInformation;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.AddPluginsFromOption;
import net.xeoh.plugins.base.options.GetPluginOption;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.factories.IPluginManager;
import org.integratedmodelling.thinklab.api.plugin.IPluginLifecycleListener;
import org.integratedmodelling.thinklab.api.plugin.IThinklabPlugin;

/**
 * Simple plugin manager using JSPF. Thinklab proxies to one instance of this.
 * 
 * @author Ferd
 *
 */
public class PluginManager implements IPluginManager {

	
	ArrayList<IThinklabPlugin> _plugins =
			new ArrayList<IThinklabPlugin>();
	ArrayList<IPluginLifecycleListener> _listeners =
			new ArrayList<IPluginLifecycleListener>();
	
	private net.xeoh.plugins.base.PluginManager _manager;
	private PluginInformation _info;
	
	public void boot() {

		this._manager = PluginManagerFactory.createPluginManager();
		this._manager.addPluginsFrom(
				Thinklab.get().getLoadPath(Thinklab.SUBSPACE_PLUGINS).toURI(), 
				(AddPluginsFromOption[])null);
			
		this._info = this._manager.getPlugin(PluginInformation.class, (GetPluginOption[])null);
	}
	
	@Override
	public void registerPluginPath(File path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPluginLifecycleListener(IPluginLifecycleListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<IThinklabPlugin> getPlugins() {
		return _plugins;
	}

	public void shutdown() {
		
		for (IThinklabPlugin plugin : _plugins) {
			try {
				for (IPluginLifecycleListener listener : _listeners) {
					listener.onPluginUnloaded(plugin);
				}
				plugin.unload();
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		_manager.shutdown();
	}

}
