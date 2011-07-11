package org.integratedmodelling.thinklab.http.geospace;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class ThinklabGeospaceWebPlugin extends ThinklabPlugin {

	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
		requirePlugin("org.integratedmodelling.thinklab.core");
		requirePlugin("org.integratedmodelling.thinklab.geospace");
		requirePlugin("org.integratedmodelling.thinklab.http");
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
