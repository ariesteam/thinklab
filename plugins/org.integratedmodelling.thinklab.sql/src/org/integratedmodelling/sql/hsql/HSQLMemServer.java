/**
 * HSQLMemServer.java
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
package org.integratedmodelling.sql.hsql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

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

}
