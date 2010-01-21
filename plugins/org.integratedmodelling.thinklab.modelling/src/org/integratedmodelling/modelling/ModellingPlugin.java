package org.integratedmodelling.modelling;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class ModellingPlugin extends ThinklabPlugin {

	private static final String USE_CACHE_PROPERTY = "modelling.use.cache";

	public static String PLUGIN_ID = "org.integratedmodelling.thinklab.modelling";
	
	private ModelFactory manager = null;
	private ObservationCache cache = null;
	
	public static ModellingPlugin get() {
		return (ModellingPlugin) getPlugin(PLUGIN_ID);
	}
	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {

		boolean persistent = false;
		manager = new ModelFactory();
		if (getProperties().containsKey(USE_CACHE_PROPERTY) &&
			Boolean.parseBoolean(getProperties().getProperty(USE_CACHE_PROPERTY))) {
			persistent = true;
		}
		cache = new ObservationCache(getScratchPath(), persistent);
	}

	@Override
	protected void unload() throws ThinklabException {
	}

	public ModelFactory getModelManager() {
		return manager;
	}
	
	public ObservationCache getCache() {
		return cache;
	}

}
