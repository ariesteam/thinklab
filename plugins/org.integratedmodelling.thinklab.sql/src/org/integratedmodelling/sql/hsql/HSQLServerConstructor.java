/**
 * 
 */
package org.integratedmodelling.sql.hsql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.sql.SQLServer;
import org.integratedmodelling.sql.SQLServerConstructor;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;

public class HSQLServerConstructor implements SQLServerConstructor {
	
	public SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException {
		if (uri.toString().startsWith("hsqlmem:"))
			return new HSQLMemServer(uri, properties);
		else if (uri.toString().startsWith("hsqlfile:"))
			return new HSQLFileServer(uri, properties);
		else
			return new HSQLServer(uri, properties);
	}
}