/**
 * 
 */
package org.integratedmodelling.sql.postgres;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.sql.SQLServerConstructor;

public class PostgresSQLServerConstructor implements SQLServerConstructor {
	public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
		return new PostgreSQLServer(uri, properties);	
	}
}