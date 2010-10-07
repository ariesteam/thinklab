package org.integratedmodelling.thinklab.rest;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.KeyValueMap;

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

	/**
	 * If query contains the ID of a valid user session, return the ISession 
	 * allocated to it, otherwise return null. 
	 * 
	 * @param query
	 * @return
	 */
	public ISession getSessionForCommand(KeyValueMap query) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public ISession initiateSession() {
		return null;
	}

}
