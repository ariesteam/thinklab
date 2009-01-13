package org.integratedmodelling.scheduler;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class Scheduler extends ThinklabPlugin {

	public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.scheduler";
	
	public static Scheduler get() {
		return (Scheduler)getPlugin(PLUGIN_ID);
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
