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
package org.integratedmodelling.sql.hsql;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.sql.SQLPlugin;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.KnowledgeManager;

/**
 * An HSQLDB server that operates on a local filesystem directory.
 * 
 * @author Ferdinando Villa
 *
 */
public class HSQLFileServer extends SQLServer {
	
	File databaseFile = null;
	Thread SERVER = null;
	
	/**
	 * Pass a database full directory path if you want to control where the database goes in the filesystem,
	 * or a database name if you're fine with it residing in the plugin scratch directory.
	 * The directory path does not need to exist but you do need to have permission to create
	 * it.
	 * 
	 * @param databaseName a database name or full directory path for the dir holding the
	 * data.
	 * 
	 * @throws ThinklabException
	 */
	public HSQLFileServer(String databaseName) throws ThinklabException {

		if (!(databaseName.contains(File.separator) || databaseName.contains("/")) && 
				KnowledgeManager.KM != null) {
			
			String path = 
				SQLPlugin.get().getProperties().
					getProperty("hsql.datadir", SQLPlugin.get().getScratchPath().toString());
			
			databaseName = path + "/" + databaseName;
		}
		databaseFile = new File(databaseName);
		setUser("sa");
		setPassword("");
		initialize();
	}
	
	public HSQLFileServer(URI uri, Properties properties) throws ThinklabStorageException {
		initialize(uri, properties);
	}

	@Override
	public int getDefaultPort() {
		return 9001;
	}

	@Override
	public String getDriverClass() {
		return "org.hsqldb.jdbcDriver";
	}

	@Override
	public String getURI() {
	   	return "jdbc:hsqldb:file:" + databaseFile + ";sql.enforce_strict_size=true";
	}

	@Override
	protected void startServer(Properties properties)
			throws ThinklabStorageException {
		
		databaseFile.mkdirs();		
		if (!databaseFile.exists() || !databaseFile.isDirectory()) {
			throw new ThinklabStorageException("hsqldb: can't create data directory " + databaseFile);
		}
	
//		isNetwork = BooleanValue.parseBoolean(properties.getProperty("useNetwork", "no"));
//		
//        if (isNetwork) {
//        	
//            urlFragment = "jdbc:hsqldb:file:" + dbDirectory;
//            server = new Server();
//            server.setDatabaseName(0,dbDirectory);
//            server.setDatabasePath(0,"file:" + dbDirectory + ";sql.enforce_strict_size=true");
//            server.setLogWriter(null);
//            server.setErrWriter(null);
//         
//            server.start();
//            // server.checkRunning(true);
//            
//        } else {
//
//        	urlFragment = "jdbc:hsqldb:file:" + dbDirectory + ";sql.enforce_strict_size=true";
//
//        }

		
	}

	@Override
	protected void stopServer() throws ThinklabStorageException {
	   	execute("SHUTDOWN");
	}

	@Override
	public void createDatabase() throws ThinklabStorageException {
		// TODO add character set and anything needed to fit loaded schemata
		this.execute("CREATE DATABASE " + getDatabase() + ";");
	}

	@Override
	public void dropDatabase() throws ThinklabStorageException {
		this.execute("DROP DATABASE " + getDatabase() + ";");		
	}
}
