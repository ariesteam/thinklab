/**
 * 
 */
package org.integratedmodelling.sql.mysql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.sql.SQLServerConstructor;
import org.integratedmodelling.sql.mysql.MySQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

public class MySQLServerConstructor implements SQLServerConstructor {
	public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
		return new MySQLServer(uri, properties);
	}
}