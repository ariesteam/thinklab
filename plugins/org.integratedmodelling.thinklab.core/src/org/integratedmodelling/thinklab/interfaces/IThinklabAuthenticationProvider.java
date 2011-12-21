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
package org.integratedmodelling.thinklab.interfaces;

import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Simply manages identification, authentication and storage/retrieval of user properties.
 * 
 * TODO document
 * TODO add methods to remove users and properties
 * 
 * @author Ferdinando Villa
 *
 */
public interface IThinklabAuthenticationProvider {
	
	public abstract void initialize(Properties properties) throws ThinklabException;

	public abstract boolean authenticateUser(String username, String password, Properties userProperties)  throws ThinklabException;
	
	public abstract Properties getUserProperties(String username)  throws ThinklabException;
	
	public abstract String getUserProperty(String user, String property, String defaultValue) throws ThinklabException;

	public abstract void setUserProperty(String user, String property, String value) throws ThinklabException;
	
	public abstract void saveUserProperties(String user) throws ThinklabException;
	
	public abstract boolean haveUser(String user);
	
	public abstract void createUser(String user, String password) throws ThinklabException;

	public abstract void deleteUser(String user) throws ThinklabException;

	public abstract void setUserPassword(String user, String password) throws ThinklabException;
	
	public abstract Collection<String> listUsers() throws ThinklabException;

	public abstract IInstance getUserInstance(String user, ISession session) throws ThinklabException;
}


