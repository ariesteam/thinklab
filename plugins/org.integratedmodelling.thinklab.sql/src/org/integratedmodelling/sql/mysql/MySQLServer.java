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
package org.integratedmodelling.sql.mysql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

public class MySQLServer extends SQLServer {
	
	public MySQLServer(URI uri, Properties properties) throws ThinklabStorageException {
		initialize(uri, properties);
	}

	public MySQLServer(String username, String password, String host) throws ThinklabStorageException {

		setUser(username);
		setPassword(password);
		setHost(host);
		initialize();
	}
	
	@Override
	public int getDefaultPort() {
		return 3306;
	}

	@Override
	public String getDriverClass() {
		return "org.mysql.Driver";
	}

	@Override
	public String getURI() {
		return 
			"jdbc:mysql://" +
			getHost() + 
			"/" +
			getDatabase() + 
			"?user=" + 
			getUser() + 
			"&password=" +
			getPassword();
	}

	@Override
	protected void startServer(Properties properties)
			throws ThinklabStorageException {
		// do nothing
	}

	@Override
	protected void stopServer() throws ThinklabStorageException {
		// do nothing
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
