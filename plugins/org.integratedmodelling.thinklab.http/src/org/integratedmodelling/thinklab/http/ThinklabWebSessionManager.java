package org.integratedmodelling.thinklab.http;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.ISessionManager;

public class ThinklabWebSessionManager implements ISessionManager {

	@Override
	public ISession createNewSession() throws ThinklabException {
		return new ThinklabWebSession();
	}

	@Override
	public ISession getCurrentSession() {
		throw new ThinklabRuntimeException("internal error: no current session is available without servlet context");
	}

	@Override
	public void notifySessionDeletion(ISession session) {
	}

}
