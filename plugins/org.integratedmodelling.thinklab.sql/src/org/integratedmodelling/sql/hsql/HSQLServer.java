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

import java.net.URI;
import java.util.Properties;

import org.hsqldb.Server;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

/**
 * An HSQLDB server that is started on a thread if local, or is supposed to be somewhere
 * else and not started at all otherwise.
 * 
 * @author Ferdinando Villa
 *
 */
public class HSQLServer extends SQLServer {
	
	static Thread SERVER = null;
	
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
	public HSQLServer(String databaseName) throws ThinklabException {

		setDatabase(databaseName);
		setUser("sa");
		setPassword("");
		setHost("localhost");
		initialize();
	}
	
	public HSQLServer(URI uri, Properties properties) throws ThinklabStorageException {
		setDatabase(getDatabaseName(uri));
		setUser(getUser(uri));
		setPassword(getPassword(uri));
		setHost(getHost(uri));
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
	   	return 
	   		"jdbc:hsqldb:hsql://" +
	   		getHost() + 
	   		"/" + 
	   		getDatabase();
	}

	@Override
	protected void startServer(Properties properties)
	throws ThinklabStorageException {

		if (getHost() == "localhost") {

			if (SERVER != null) {
				throw new ThinklabStorageException("another HSQL server is running: running multiple servers is not supported yet");
			}

			// starts a new thread to run the database server
			Thread server = new Thread() {
				public void run() {
					String[] args = { "-database", getDatabase(),
							"-port", String.valueOf(getPort()),
							"-no_system_exit", "true" };
					Server.main(args);
				}
			};
			SERVER = server;
			server.start();
		}
	}

	@Override
	protected void stopServer() throws ThinklabStorageException {
		
		if (getHost() == "localhost") {

			if (SERVER == null)
    		return;
			execute("SHUTDOWN");
			SERVER = null;
		}
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
