package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;

public interface ITask {

	public abstract void run(ISession session) throws ThinklabException;
}
