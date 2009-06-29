/**
 * PostgreSQLServer.java
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
package org.integratedmodelling.sql.postgres;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

public class PostgreSQLServer extends SQLServer {

	public PostgreSQLServer(URI uri, Properties properties) throws ThinklabStorageException {
		initialize(uri, properties);
	}

	public PostgreSQLServer(String username, String password, String host) throws ThinklabStorageException {

		setUser(username);
		setPassword(password);
		setHost(host);
		initialize();
	}
	
	boolean useSSL = false;
	
	@Override
	public int getDefaultPort() {
		return 5432;
	}

	@Override
	public String getDriverClass() {
		return "org.postgresql.Driver";
	}

	@Override
	public String getURI() {

		String ret = 
			"jdbc:postgresql://" +
			getHost() + 
			"/" +
			getDatabase() + 
			"?user=" + 
			getUser() + 
			"&password=" +
			getPassword();
		
			/* 
			 * must do it this way: the JDBC driver has a bug that requires the server
			 * to allow SSL even if we say ssl=false.
			 */
			if (useSSL) 
				ret += "&ssl=true";
			
			return ret;
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
		this.execute("CLOSE DATABASE " + getDatabase() + ";");
		this.execute("DROP DATABASE " + getDatabase() + ";");		
	}

}
