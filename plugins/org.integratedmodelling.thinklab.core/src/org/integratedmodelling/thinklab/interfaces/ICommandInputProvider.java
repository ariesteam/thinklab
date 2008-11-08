package org.integratedmodelling.thinklab.interfaces;

import java.io.InputStream;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

public interface ICommandInputProvider {

	public abstract String readLine() throws ThinklabIOException;
	
	public abstract InputStream getInputStream(); 
}
