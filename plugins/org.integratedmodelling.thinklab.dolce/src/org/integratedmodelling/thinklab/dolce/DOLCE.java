package org.integratedmodelling.thinklab.dolce;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

public class DOLCE extends ThinklabPlugin {

	static final String PLUGIN_ID = "org.integratedmodelling.thinklab.dolce";
	
	// to be completed
	static public final String PART_OF = "DOLCE-Lite:part-of";
	static public final String PARTICIPANT_IN = "DOLCE-Lite:participant-in";
	static public final String INHERENT_IN = "DOLCE-Lite:inherent-in";
	static public final String IMMEDIATE_RELATION = "DOLCE-Lite:immediate-relation";
	static public final String IMMEDIATE_RELATION_I = "DOLCE-Lite:immediate-relation-i";
	
	@Override
	protected void load(KnowledgeManager km) throws ThinklabException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
	}

}
