package org.integratedmodelling.thinklab.interfaces.commands;

import java.io.InputStream;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;

/**
 * @deprecated use inputstream in user model
 */
public interface ICommandInputProvider {

	public abstract String readLine() throws ThinklabIOException;
	
	public abstract InputStream getInputStream(); 
}
