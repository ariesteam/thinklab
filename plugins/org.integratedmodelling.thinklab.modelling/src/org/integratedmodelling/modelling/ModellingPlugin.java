package org.integratedmodelling.modelling;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class ModellingPlugin extends ThinklabPlugin {

	public static String PLUGIN_ID = "org.integratedmodelling.thinklab.modelling";
	
	private ModelFactory manager = null;
	
	public static ModellingPlugin get() {
		return (ModellingPlugin) getPlugin(PLUGIN_ID);
	}
	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		manager = new ModelFactory();
	}

	@Override
	protected void unload() throws ThinklabException {
	}

	public ModelFactory getModelManager() {
		return manager;
	}

}
