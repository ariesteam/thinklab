/**
 * 
 */
package org.integratedmodelling.sql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabStorageException;

public interface SQLServerConstructor {
	public abstract SQLServer createServer(URI uri, Properties properties) throws ThinklabStorageException;
}