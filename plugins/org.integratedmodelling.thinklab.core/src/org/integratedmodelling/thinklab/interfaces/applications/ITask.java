package org.integratedmodelling.thinklab.interfaces.applications;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public interface ITask {

	public abstract void run(ISession session) throws ThinklabException;
	
}
