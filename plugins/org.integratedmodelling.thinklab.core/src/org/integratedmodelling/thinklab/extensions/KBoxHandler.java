package org.integratedmodelling.thinklab.extensions;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public interface KBoxHandler {

	/**
	 * Create the kbox with the passed URI, which should be enough to define it entirely. The
	 * initialize() function is not called by the knowledge manager. Called when the kbox is
	 * referenced directly by a URI using a recognized kbox protocol. Define this one to raise
	 * an exception if you don't want the kbox to be initialized this way.
	 * @param uri
	 * @return
	 * @throws ThinklabStorageException
	 */
	public abstract IKBox createKBoxFromURL(URI uri) throws ThinklabStorageException;
	
	/**
	 * Create the kbox for the passed protocol with no parameters, ready for initialization 
	 * through initialize(). This is called when the kbox is referenced through a metadata document
	 * identified by the kbox protocol. Define this one to raise an exception if you don't want the
	 * kbox to be initialized this way.
	 * @param properties 
	 * @param dataUri 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IKBox createKBox(String originalURI, String protocol, String dataUri, Properties properties) throws ThinklabException;

}
