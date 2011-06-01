/**
 * LocalAuthenticationManager.java
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.sql.QueryResult;
import org.integratedmodelling.sql.SQLPlugin;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.authentication.AuthenticationManager;
import org.integratedmodelling.thinklab.authentication.EncryptionManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabAuthenticationException;
import org.integratedmodelling.thinklab.exception.ThinklabDuplicateUserException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInvalidUserException;
import org.integratedmodelling.thinklab.interfaces.IThinklabAuthenticationProvider;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.owlapi.Session;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

/**
 * Simple database-backed authentication manager. It's entirely unsafe as it does not require
 * successful authentication in order to set or retrieve properties, and has no notion
 * of admin accounts, so any API user can manipulate the database. A "safe" subclass should be 
 * derived to do such things.
 * 
 * @author Ferdinando Villa
 *
 */
public class LocalAuthenticationManager implements IThinklabAuthenticationProvider {

	private SQLServer database = null;
	private EncryptionManager encryptionManager = null;
	private Session session;
	
	static Hashtable<String, Properties> userProperties = 
		new Hashtable<String, Properties>();
	
	// fairly obvious indeed, with the dreaded key-value pairs for properties.
	static final String dbInit = 
		"CREATE TABLE tluser (" +
		"	username	varchar(256) PRIMARY KEY," +
		"	userpass	varchar(256)" +
		");" +
		"CREATE TABLE tlprops (" + 
		"		username varchar(256)," + 
		"		propname varchar(256)," + 
		"		propvalue varchar(1024)" + 
		");" + 
		"CREATE INDEX idx_tlprops ON tlprops(username)";
	

	public boolean authenticateUser(String username, String password,
			Properties userProperties)  throws ThinklabException {
		
		QueryResult qr  = database.query(
				"SELECT username, userpass FROM tluser WHERE username = '" +
				username + 
				"'");

		if (qr.nRows() != 1) {
			throw new ThinklabInvalidUserException(username);
		}
		
		String ew = encryptionManager == null ?
				password : 
				encryptionManager.encrypt(password);
		
		return ew.equals(qr.get(0,1));
	}

	public Properties getUserProperties(String username)  throws ThinklabException{
		
		Properties obj = userProperties.get(username);
		
		if (obj == null) {
			if (!haveUser(username))
				throw new ThinklabInvalidUserException(username);
			
			obj = new Properties();
			
			QueryResult qr = 
				database.query(
						"SELECT propname, propvalue FROM tlprops WHERE username = '" + 
						username + 
						"'");
			
			for (int i = 0; i < qr.nRows(); i++)
				obj.setProperty(qr.get(i,0), qr.get(i,1));
			
			userProperties.put(username, obj);
		}
		
		return obj;
	}


	public String getUserProperty(String user, String property,
			String defaultValue)  throws ThinklabException {
		
		return getUserProperties(user).getProperty(property, defaultValue);
	}

	public synchronized void saveUserProperties(String user)  throws ThinklabException {
		
		Properties op = getUserProperties(user);
		
		/* delete any properties for user */
		database.execute("DELETE FROM tlprops WHERE username = '" + user + "'");
		
		for (Object p : op.keySet()) {
			database.execute(
					"INSERT INTO tlprops VALUES ('" +
					user + 
					"', '" +
					p.toString() + 
					"', '" +
					op.getProperty(p.toString()) +
					"')");
		}
		
	}


	public void setUserProperty(String user, String property, String value)  throws ThinklabException {

		Properties upr = getUserProperties(user);
		upr.setProperty(property, value);
	}


	public void initialize(Properties properties) throws ThinklabException {
		
		this.session = new Session();
		
		String db = 
			properties.getProperty(
					"authentication.local.database", 
					"hsqlfile://sa@localhost/auth");
		
		/*
		 * An encryption key should REALLY be set into properties.
		 */
		String ek = 
			properties.getProperty(
					"authentication.local.encryption.key");
		
		if (ek == null || ek.trim().equals("")) {
			throw new ThinklabAuthenticationException(
					"unsafe encrypion: please provide a valid encryption key in the Authentication plugin properties");
		}
		
		/**
		 * Create a user database with the specs given in properties, default to
		 * a HSQL file-based one. Pass the plugin's properties so we can configure
		 * the db using sql properties.
		 */
		database = 
			SQLPlugin.get().createSQLServer(db, properties);

		if (BooleanValue.parseBoolean(properties.getProperty(AuthenticationManager.USE_ENCRYPTION_PROPERTY, "false"))) {
			encryptionManager = new EncryptionManager(
					EncryptionManager.AES_ENCRYPTION_SCHEME,
					ek);			
		}

		/* create db if necessary */
		if (!database.haveTable("tluser")) {
			database.submit(dbInit);
		}
		
		/*
		 * look for a users.xml file in the config directory and load it if 
		 * there.
		 */
		File userfile = new File(
				LocalConfiguration.getUserConfigDirectory(Thinklab.PLUGIN_ID) +
				File.separator + 
				"users.xml");
		
		if (userfile.exists()) {
			loadUsers(userfile);
		}	
	}

	private void loadUsers(File userfile) throws ThinklabException {

		XMLDocument xml = new XMLDocument(userfile);
		
		for (XMLDocument.NodeIterator it = xml.iterator(); it.hasNext(); ) {
			Node node = it.next();
			if (node.getNodeName().equals("user")) {
				String uname = XMLDocument.getAttributeValue(node, "name");
				
				if (!haveUser(uname)) {
					
					String upass = XMLDocument.getAttributeValue(node, "password");
					createUser(uname,upass);
					setUserProperty(uname, "creation-date", new Date().toString());

					for (XMLDocument.NodeIterator ct = xml.iterator(node); ct.hasNext(); ) {
						Node at = ct.next();
						
						if (at.getNodeType() != Node.ELEMENT_NODE) 
							continue;
						
						String atid = at.getNodeName();
						String atva = XMLDocument.getNodeValue(at);
						setUserProperty(uname, atid, atva);
					}	
					
					saveUserProperties(uname);
				}
			}
		}
	}


	public synchronized void createUser(String user, String password) throws ThinklabException {
		
		if (haveUser(user)) {
			throw new ThinklabDuplicateUserException(user);
		}
		
		String ew = encryptionManager == null ? password : encryptionManager.encrypt(password);
		
		database.execute("INSERT INTO tluser VALUES ('" +
				user + 
				"', '" +
				ew +
				"')");
		
	}


	public boolean haveUser(String user)  {

		boolean ret = false;
		
		try {
			ret = 
				(userProperties.get(user) != null) || 
				(database.query(
					"SELECT username FROM tluser WHERE username = '" + 
					user + 
					"'").nRows() == 1);
		} catch (ThinklabException e) {
		}
		
		return ret;
	}


	public synchronized void setUserPassword(String user, String password)
			throws ThinklabException {
		
		if (!haveUser(user)) {
			throw new ThinklabInvalidUserException(user);
		}
		
		String ew = encryptionManager == null ? password : encryptionManager.encrypt(password);
		
		database.execute("UPDATE tluser SET userpass = '" +
				ew + 
				"' WHERE username = '" +
				user +
				"'");
		
	}


	@Override
	public void deleteUser(String user) throws ThinklabException {

		/* delete any properties for user */
		database.execute("DELETE FROM tlprops WHERE username = '" + user + "'");
		/* delete  user */
		database.execute("DELETE FROM tluser WHERE username = '" + user + "'");

		userProperties.remove(user);
	}


	@Override
	public Collection<String> listUsers() throws ThinklabException {

		ArrayList<String> ret = new ArrayList<String>();
		QueryResult qr  = database.query("SELECT username, userpass FROM tluser;");
		for (int i = 0; i < qr.nRows(); i++)
			ret.add(qr.get(i,0));
		return ret;
	}

	@Override
	public IInstance getUserInstance(String user) throws ThinklabException {
		
		IInstance ret = session.retrieveObject(user);
		
		if (ret == null) {
			String role = getUserProperty(user, "role", "user:UnprivilegedUser");
			ret = session.createObject(user, KnowledgeManager.getConcept(role));
		}
		
		return ret;
	}

}
