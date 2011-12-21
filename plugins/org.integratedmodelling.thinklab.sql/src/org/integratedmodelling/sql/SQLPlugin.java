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
package org.integratedmodelling.sql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import org.integratedmodelling.sql.hsql.HSQLServerConstructor;
import org.integratedmodelling.sql.mysql.MySQLServerConstructor;
import org.integratedmodelling.sql.postgres.PostgresSQLServerConstructor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

/**
 * TODO: create extension point for server constructors so that plugins can add servers
 * 
 * @author Ferdinando
 *
 */
public class SQLPlugin extends ThinklabPlugin {

	public File coreSchema = null;
	private HashMap<String, SQLServerConstructor> serverConstructors =
		new HashMap<String, SQLServerConstructor>();

	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.sql";

	static final public String DEFAULT_USER_PROPERTY = "thinklab.sql.user";
	static final public String DEFAULT_PASSWORD_PROPERTY = "thinklab.sql.password";
	static final public String DEFAULT_HOST_PROPERTY = "thinklab.sql.host";

	public static SQLPlugin get() {
		return (SQLPlugin) getPlugin(PLUGIN_ID);
	}

	public URL getSchema(String schemaID) throws ThinklabException {
		
		URL r = getResourceURL(schemaID + ".sqx");

		if (r == null) {
			throw new ThinklabIOException("schema " + schemaID + " referenced in kbox is not installed");
		}	
		
		return r;
	}

	
	@Override
	public void load(KnowledgeManager km) throws ThinklabPluginException {
				
		/* register server types to be returned by createSQLServer() */
		registerServerConstructor("hsql", new HSQLServerConstructor());
		registerServerConstructor("postgres", new PostgresSQLServerConstructor());
		registerServerConstructor("mysql", new MySQLServerConstructor());				
	}

	public void registerServerConstructor(String string, SQLServerConstructor serverConstructor) {
		serverConstructors.put(string, serverConstructor);	
	}

	@Override
	public void unload() throws ThinklabPluginException {
		// TODO Auto-generated method stub
	}

	public SQLServer createSQLServer(String uri, Properties properties) throws ThinklabStorageException {

		SQLServer ret = null;
		URI u;
		try {
			u = new URI(uri);
		} catch (URISyntaxException e) {
			throw new ThinklabStorageException(e);
		}
		
		for (String s : serverConstructors.keySet()) {
			if (uri.startsWith(s)) {
				ret = serverConstructors.get(s).createServer(u, properties);
				break;
			}
		}
		
		if (ret == null)
			throw new ThinklabStorageException("SQL plugin: cannot create SQL server for URI " +
					uri);

		return ret;
	}
	
	public String getDefaultUser() {
		return getProperties().getProperty(DEFAULT_USER_PROPERTY);
	}
	
	
	public String getDefaultPassword() {
		return getProperties().getProperty(DEFAULT_PASSWORD_PROPERTY);
	}

	public String getDefaultHost() {
		return getProperties().getProperty(DEFAULT_HOST_PROPERTY, "localhost");
	}
}
