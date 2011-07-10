/**
 * IThinklabAuthenticationManager.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabAuthenticationPlugin.
 * 
 * ThinklabAuthenticationPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabAuthenticationPlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.interfaces;

import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.runtime.ISession;

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


