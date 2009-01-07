package org.integratedmodelling.sql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public class SQLKBoxHandler implements KBoxHandler {


	public IKBox createKBox(String originalURI, String protocol, String dataUri, Properties properties) throws ThinklabException {
		
		if (protocol.equals("pg") || protocol.equals("hsqldb") || protocol.equals("mysql"))
			return new SQLKBox(originalURI, protocol, dataUri, properties);

		return null;
	}

	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		throw new ThinklabStorageException("sql kboxes must be created with the kbox protocol");
	}
	

}
