package org.integratedmodelling.thinklab.interfaces.storage;

import java.io.InputStream;
import java.io.OutputStream;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Objects implementing this one have a poor-man persistence mechanism that is recognized
 * through Thinklab. In particular, if instance implementations can be persisted then whole
 * Thinklab object can. The InputSerializer and OutputSerializer classes make the job
 * of serializing quite easy. Java serialization is quite limited and ends up becoming
 * messier than this, so here we go.
 * 
 * NOTE: classes that implement this should have an empty constructor and be annotated
 * as PersistentObject() for the whole mechanism to work. The annotation can 
 * optionally specify a file extension if we want the PersistenceManager to recognize
 * the class from the file extension.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IPersistentObject {
	
	/**
	 * Persistent objects must be capable of writing themselves to a stream. Use OutputSerializer to
	 * make that easy.
	 * 
	 * @param fop
	 */
	public void serialize(OutputStream fop) throws ThinklabException;
	
	/**
	 * Persistent objects must be capable of being created with an empty constructor and read 
	 * themselves from an open reader. Use InputSerializer to make that easy.
	 * 
	 * @param fop
	 */
	public void deserialize(InputStream fop) throws ThinklabException;
}
