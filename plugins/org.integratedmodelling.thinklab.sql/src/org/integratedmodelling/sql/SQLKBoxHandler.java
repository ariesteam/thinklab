package org.integratedmodelling.sql;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;

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
