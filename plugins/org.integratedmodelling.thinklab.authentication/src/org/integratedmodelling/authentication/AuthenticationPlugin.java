/**
 * AuthenticationPlugin.java
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
package org.integratedmodelling.authentication;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.w3c.dom.Node;

/**
 * Authentication system simply uses an embedded database to store user info and 
 * encrypted passwords. We will want to extend to use common authentication protocols such as 
 * LDAP or the new IBM one, to be selected through properties.
 * 
 * The plugin object implements the authentication manager interface and redirects
 * authentication calls to whatever manager has been specified through its properties.
 * If no manager is specified (the default), calls to authentication return success and
 * calls to set or save properties raise an exception.
 * 
 * @author Ferdinando Villa
 *
 */
public class AuthenticationPlugin extends ThinklabPlugin implements IThinklabAuthenticationManager {

	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(AuthenticationPlugin.class);

	private final static String PLUGIN_ID = "org.integratedmodelling.thinklab.authentication";

	/**
	 * Provided as a convenience for wherever a username property is needed to 
	 * reflect authentication.
	 */
	public static String USERID_PROPERTY = "authentication.user.id";
	public static String USER_PROPERTIES = "authentication.user.properties";
	
	
	IThinklabAuthenticationManager authManager = null;
	
	
	public static AuthenticationPlugin get() {
		return (AuthenticationPlugin) getPlugin(PLUGIN_ID );
	}

	public static Logger logger() {
		return log;
	}
	
	@Override
	public void load(KnowledgeManager km) throws ThinklabException {
		
//			new Login().install(km);
//			new AddUser().install(km);
		
		/* create the auth manager specified in plugin properties, if any */
		String authClass = getProperties().getProperty("authentication.manager.class");
		
		if (authClass != null && !authClass.equals("")) {

			try {
				Class cl = Class.forName(authClass);
				if (cl != null) {
					authManager = (IThinklabAuthenticationManager) cl.newInstance();
				}
			} catch (Exception e) {
				throw new ThinklabPluginException(e);
			}
			authManager.initialize();
		}
	}

	public boolean haveAuthentication() {
		return authManager != null;
	}


	public boolean authenticateUser(String username, String password,
			Properties userProperties) throws ThinklabException {
		
		boolean ret = false;
		
		if (authManager != null) {
			try {
				ret = 
					authManager.authenticateUser(username, password, userProperties);
			} catch (ThinklabException e) {
				/* do nothing, just return false. */
			}
		}
		
		return ret;
	}


	public Properties getUserProperties(String username) throws ThinklabException {

		return authManager == null ? 
				new Properties() :
				authManager.getUserProperties(username);
	}


	public String getUserProperty(String user, String property,
			String defaultValue) throws ThinklabException {

		return authManager == null ?
				null :
				authManager.getUserProperty(user, property, defaultValue);
	}


	public void saveUserProperties(String user) throws ThinklabException {
		
		if (authManager == null) {
			throw new ThinklabPluginException("no authentication scheme selected: can't save user properties");
		}
		
		authManager.saveUserProperties(user);
		
	}


	public void setUserProperty(String user, String property, String value) throws ThinklabException {
		
		if (authManager == null) {
			throw new ThinklabPluginException("no authentication scheme selected: can't save user properties");
		}
		
		authManager.setUserProperty(user, property, value);
	}


	public void createUser(String user, String password) throws ThinklabException {
		
		if (authManager == null) {
			throw new ThinklabPluginException("no authentication scheme selected: can't add user");
		}
		
		authManager.createUser(user, password);
		
	}


	public boolean haveUser(String user) {

		if (authManager == null) {
			return false;
		}
		
		return authManager.haveUser(user);
	}


	public void setUserPassword(String user, String password)
			throws ThinklabException {
		
		if (authManager == null) {
			throw new ThinklabPluginException("no authentication scheme selected: can't set user password");
		}
		
		authManager.setUserPassword(user, password);
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialize() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
