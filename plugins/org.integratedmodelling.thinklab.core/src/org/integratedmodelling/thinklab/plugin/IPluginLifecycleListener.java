package org.integratedmodelling.thinklab.plugin;

public interface IPluginLifecycleListener {

	public abstract void onPluginLoaded(ThinklabPlugin plugin);

	public abstract void onPluginUnloaded(ThinklabPlugin plugin);

	public abstract void prePluginLoaded(ThinklabPlugin thinklabPlugin);

	public abstract void prePluginUnloaded(ThinklabPlugin thinklabPlugin);
}
