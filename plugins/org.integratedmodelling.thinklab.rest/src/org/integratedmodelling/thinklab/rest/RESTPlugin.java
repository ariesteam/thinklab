package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class RESTPlugin extends ThinklabPlugin {

	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.rest";
	
	public static RESTPlugin get() {
		return (RESTPlugin)getPlugin(PLUGIN_ID);
	}
	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
