package org.integratedmodelling.corescience.contextualization;

import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/*
 * Testing - not used at all for now.
 * Contextualizer class for simple workflows.
 */
public class SimpleContextualizer implements IContextualizer {

	static final byte F_ACTIVE = 0x01;
	static final byte F_MEDIATED = 0x01;
	
	static int n_obs = 0;
	
	byte[] oflags = null;

	@Override
	public IInstance run(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
