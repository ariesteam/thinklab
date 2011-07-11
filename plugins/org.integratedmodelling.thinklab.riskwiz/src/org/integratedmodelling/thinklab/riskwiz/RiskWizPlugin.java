package org.integratedmodelling.thinklab.riskwiz;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class RiskWizPlugin extends ThinklabPlugin {

	public final static String PLUGIN_ID = "org.integratedmodelling.thinklab.riskwiz";
	public static final String BAYESIAN_ENGINE_PROPERTY = "thinklab.bayesian.engine";
	
	public static RiskWizPlugin get() {
		return (RiskWizPlugin)getPlugin(PLUGIN_ID);
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
