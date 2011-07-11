package org.integratedmodelling.thinklab.commandline;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class CommandLine extends ThinklabPlugin {

    public static final String PLUGIN_ID = "org.integratedmodelling.thinklab.commandline";
	
    public static CommandLine get() {
    	return (CommandLine) getPlugin(PLUGIN_ID);
    }
    
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		
	}

	@Override
	protected void unload() throws ThinklabException {
	}

}
