package org.integratedmodelling.thinklab.interfaces.applications;

import org.integratedmodelling.thinklab.exception.ThinklabException;

public interface ITask {

	public abstract void run(ISession session) throws ThinklabException;
	
}
