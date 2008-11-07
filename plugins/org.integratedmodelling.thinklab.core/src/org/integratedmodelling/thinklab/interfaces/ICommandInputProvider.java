package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

public interface ICommandInputProvider {

	public abstract String readLine() throws ThinklabIOException;
}
