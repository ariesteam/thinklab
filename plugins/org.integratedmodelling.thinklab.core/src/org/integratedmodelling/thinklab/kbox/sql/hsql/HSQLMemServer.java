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
package org.integratedmodelling.thinklab.kbox.sql.hsql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.thinklab.kbox.sql.SQLServer;

/**
 * An HSQLDB server that only operates in memory.
 * @author Ferdinando Villa
 *
 */
public class HSQLMemServer extends SQLServer {
	
	public HSQLMemServer(String databaseName) throws ThinklabStorageException {
		setDatabase(databaseName);
		setUser("sa");
		setPassword("");
		initialize();
	}
	
	public HSQLMemServer(URI uri, Properties properties) throws ThinklabStorageException {
		setDatabase(getDatabaseName(uri));
		setUser(getUser(uri));
		setPassword(getPassword(uri));
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
		return "jdbc:hsqldb:mem:" + getDatabase();
	}

	@Override
	protected void startServer(Properties properties)
			throws ThinklabStorageException {
	}

	@Override
	protected void stopServer() throws ThinklabStorageException {
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
