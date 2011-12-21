/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.interfaces.applications;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

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
	
	/*
	 * initialize, passing the session that this user works in
	 */
	public abstract void initialize(ISession session);

	/*
	 * set properties for user
	 */
	public abstract void setProperties(Properties uprop);

	/*
	 * get properties for user. Should never return null.
	 */
	public abstract Properties getProperties();

	/**
	 * if a user is logged in, this method must return a valid instance that
	 * describes it. We use the instance for authentication (checking subsumption
	 * by roles).
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IInstance getUserInstance() throws ThinklabException;
}
