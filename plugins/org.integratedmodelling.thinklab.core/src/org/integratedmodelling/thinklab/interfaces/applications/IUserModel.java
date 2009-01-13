package org.integratedmodelling.thinklab.interfaces.applications;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Implements the model of the user during a Thinklab session. Only one user model should exist per
 * session, and it can be retrieved from it; it may also be null for non-interactive sessions.
 * 
 * Application tasks and interface components will check the lineage of the user model in order
 * to enable or disable operations.
 * 
 * @author Ferdinando
 *
 */
public interface IUserModel {

	/**
	 * Sessions may have an input and an output stream associated in case they can interact
	 * with the user through them.
	 * 
	 * @return
	 */
	public abstract InputStream getInputStream();
	
	/**
	 * Sessions may have an input and an output stream associated in case they can interact
	 * with the user through them.
	 * 
	 * @return
	 */
	public abstract PrintStream getOutputStream();
}
