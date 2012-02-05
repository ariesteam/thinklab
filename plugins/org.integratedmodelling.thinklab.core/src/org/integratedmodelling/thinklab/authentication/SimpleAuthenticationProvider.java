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
package org.integratedmodelling.thinklab.authentication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabAuthenticationException;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.interfaces.IThinklabAuthenticationProvider;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.utils.xml.XML;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

/**
 * Simple authentication manager using only a user.xml file in the config area. Obvious shortcomings, obvious
 * advantages. Modifications through the API will only affect the running instance, no modified data are ever 
 * saved.
 * 
 * @author Ferdinando Villa
 *
 */
public class SimpleAuthenticationProvider implements IThinklabAuthenticationProvider {

	private EncryptionManager encryptionManager = null;
	
	private File userFile = null;
	
	Hashtable<String, Properties> userProperties = new Hashtable<String, Properties>();
	HashMap<String, String> passwords = new HashMap<String, String>();
		
	public boolean authenticateUser(String username, String password,
			Properties userProperties)  throws ThinklabException {
		
		String passwd = passwords.get(username);
		
		if (passwd == null) {
			throw new ThinklabValidationException(username);
		}
		
		String ew = encryptionManager == null ?
				password : 
				encryptionManager.encrypt(password);
		
		return ew.equals(passwd);
	}

	public Properties getUserProperties(String username)  throws ThinklabException{
		
		Properties obj = userProperties.get(username);
		
		if (obj == null) {
			if (!haveUser(username))
				throw new ThinklabValidationException(username);	
			obj = new Properties();
			userProperties.put(username, obj);
		}
		
		return obj;
	}


	public String getUserProperty(String user, String property,
			String defaultValue)  throws ThinklabException {
		
		return getUserProperties(user).getProperty(property, defaultValue);
	}

	public void setUserProperty(String user, String property, String value)  throws ThinklabException {

		Properties upr = getUserProperties(user);
		upr.setProperty(property, value);
		write();
	}

	public void initialize(Properties properties) throws ThinklabException {

		/*
		 * initialize encryption manager if requested. Otherwise we're flying unencrypted.
		 */
		if (BooleanValue.parseBoolean(properties.getProperty(AuthenticationManager.USE_ENCRYPTION_PROPERTY, "false"))) {

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

			encryptionManager = new EncryptionManager(
					EncryptionManager.AES_ENCRYPTION_SCHEME,
					ek);			
		}

		/*
		 * look for a users.xml file in the config directory and load it if 
		 * there.
		 */
		File userfile = new File(
				LocalConfiguration.getUserConfigDirectory() +
				File.separator + 
				"users.xml");
		
		if (userfile.exists()) {
			loadUsers(userfile);
			Thinklab.get().logger().info("authorization database read from " + userfile);
		} else {			
			/*
			 * ok to not have users, we just won't be able to authenticate anyone.
			 */
			Thinklab.get().logger().info("authorization database file " + userfile + " not present");
		}
	}

	private void loadUsers(File userfile) throws ThinklabException {

		this.userFile = userfile;
		
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
				}
			}
		}
	}

	/**
	 * Always pass the unencrypted password.
	 */
	public synchronized void createUser(String user, String password) throws ThinklabException {
		
		if (haveUser(user)) {
			throw new ThinklabValidationException(user);
		}
		String ew = encryptionManager == null ? password : encryptionManager.encrypt(password);
		passwords.put(user, ew);
		write();
	}

	public boolean haveUser(String user)  {
		return passwords.containsKey(user);
	}


	public synchronized void setUserPassword(String user, String password)
			throws ThinklabException {
		
		if (!haveUser(user)) {
			throw new ThinklabValidationException(user);
		}
		
		String ew = encryptionManager == null ? password : encryptionManager.encrypt(password);
		passwords.put(user, ew);
		write();
	}


	@Override
	public void deleteUser(String user) throws ThinklabException {

		userProperties.remove(user);
		passwords.remove(user);
		write();
	}


	@Override
	public Collection<String> listUsers() throws ThinklabException {
		return passwords.keySet();
	}

	@Override
	public void saveUserProperties(String user) throws ThinklabException {
		write();
	}

	@Override
	public IInstance getUserInstance(String user, ISession session) throws ThinklabException {
		
		IInstance ret = session.retrieveObject("user");
		
		if (ret == null) {
			String role = getUserProperty(user, "role", "user:UnprivilegedUser");
			ret = session.createObject("user", KnowledgeManager.getConcept(role));
		}
		
		return ret;
	}
	
	/*
	 * FIXME - buggy, no root node exception from writeToFile
	 */
	public void write() throws ThinklabException {
	
		// disable temporarily for debugging
		if (userFile == null) {
			userFile = 
				new File(LocalConfiguration.getUserConfigDirectory() + 
						File.separator + "users.xml");
		}
		
		ArrayList<XML.XmlNode> nodes = new ArrayList<XML.XmlNode>();
		for (String user : passwords.keySet()) {
			
			Properties prop = userProperties.get(user);
			ArrayList<XML.XmlNode> pnodes = null;
			
			if (prop != null && prop.size() > 0) {
				pnodes = new ArrayList<XML.XmlNode>();
				for (Object o : prop.keySet()) {
					pnodes.add(XML.node(o.toString(), prop.getProperty(o.toString())));
				}
			}
			
			nodes.add(XML.node("user", pnodes).
							attr("name", user).
							attr("password", passwords.get(user)));
		}
		
		XML.document(XML.node("users", nodes.toArray())).writeToFile(userFile);
	}
	
}
