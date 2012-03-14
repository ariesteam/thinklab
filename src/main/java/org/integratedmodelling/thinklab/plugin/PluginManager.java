package org.integratedmodelling.thinklab.plugin;

import java.io.File;
import java.util.List;

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

	public void boot() {
		
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
		// TODO Auto-generated method stub
		return null;
	}

}
