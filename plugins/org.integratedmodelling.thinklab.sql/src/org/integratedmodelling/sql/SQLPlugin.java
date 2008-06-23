/**
 * SQLPlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabSQLPlugin.
 * 
 * ThinklabSQLPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabSQLPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.sql;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.sql.hsql.HSQLFileServer;
import org.integratedmodelling.sql.hsql.HSQLMemServer;
import org.integratedmodelling.sql.hsql.HSQLServer;
import org.integratedmodelling.sql.mysql.MySQLServer;
import org.integratedmodelling.sql.postgres.PostgreSQLServer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKBoxPlugin;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.plugin.Plugin;
import org.w3c.dom.Node;

public class SQLPlugin extends Plugin implements IKBoxPlugin {

	public interface SQLServerConstructor {
		public abstract SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException;
	}

	private class HSQLServerConstructor implements SQLServerConstructor {
		public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
			if (uri.toString().startsWith("hsqlmem:"))
				return new HSQLMemServer(uri, properties);
			else if (uri.toString().startsWith("hsqlfile:"))
				return new HSQLFileServer(uri, properties);
			else
				return new HSQLServer(uri, properties);
		}
	}

	private class MySQLServerConstructor implements SQLServerConstructor {
		public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
			return new MySQLServer(uri, properties);
		}
	}

	private class PostgresSQLServerConstructor implements SQLServerConstructor {
		public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
			return new PostgreSQLServer(uri, properties);	
		}
	}
	

		
	public File coreSchema = null;
	public ArrayList<File> schemata = new ArrayList<File>();
	private HashMap<String, SQLServerConstructor> serverConstructors =
		new HashMap<String, SQLServerConstructor>();

	/* log4j logger used for this class. Can be used by other classes through logger()  */
	private static  Logger log = Logger.getLogger(SQLPlugin.class);
	static final public String ID = "SQL";

	public SQLPlugin() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize() throws ThinklabException {
		
	}

	public static SQLPlugin get() {
		return (SQLPlugin) getPlugin(ID);
	}

	public static Logger logger() {
		return log;
	}
	
	public File getSchema(String schemaID) throws ThinklabException {
		
		
		File schema = null;
		
		for (File sch : schemata) {
			if (sch.toString().endsWith(schemaID + ".sqx")) { 
				schema = sch;
				break;
			}
		}

		if (schema == null) {
			throw new ThinklabIOException("schema " + schemaID + " referenced in kbox is not installed");
		}
		
		return schema;
	}

	
	@Override
	public void load(KnowledgeManager km, File baseReadPath, File baseWritePath)
			throws ThinklabPluginException {

		/* register server types to be returned by createSQLServer() */
		registerServerConstructor("hsql", new HSQLServerConstructor());
		registerServerConstructor("postgres", new PostgresSQLServerConstructor());
		registerServerConstructor("mysql", new MySQLServerConstructor());
		
		/* register plugin to handle our nice kboxes 
		 * TODO substitute with extension points
		 * */
		KBoxManager.get().registerKBoxProtocol("pg", this);
		KBoxManager.get().registerKBoxProtocol("hsqldb", this);
		KBoxManager.get().registerKBoxProtocol("mysql", this);
		
	}

	public void registerServerConstructor(String string, SQLServerConstructor serverConstructor) {
		serverConstructors .put(string, serverConstructor);	
	}

	@Override
	public void notifyResource(String name, long time, long size)
			throws ThinklabException {
		
		if (name.endsWith(".sqx")) {
			URL uu = this.exportResourceCached(name);
			
			if (name.contains("coresql.sqx")) {
				coreSchema = new File(uu.getFile());
			} else {
				schemata.add(new File(uu.getFile()));
			}
		}
	}

	@Override
	public void unload(KnowledgeManager km) throws ThinklabPluginException {
		// TODO Auto-generated method stub
	}

	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		throw new ThinklabStorageException("sql kboxes must be created with the kbox protocol");
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

	public IKBox createKBox(String protocol, String dataUri, Properties properties) throws ThinklabException {
		
		if (protocol.equals("pg") || protocol.equals("hsqldb") || protocol.equals("mysql"))
			return new SQLKBox(protocol, dataUri, properties);

		return null;
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

}
