package org.integratedmodelling.agriculture;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class AgriculturePlugin extends ThinklabPlugin {

	public final static String PLUGIN_ID = "org.integratemodelling.thinklab.agriculture";
	
	public static AgriculturePlugin get() {
		return (AgriculturePlugin) getPlugin(PLUGIN_ID );
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
