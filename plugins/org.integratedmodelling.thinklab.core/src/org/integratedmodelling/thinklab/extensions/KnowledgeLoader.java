package org.integratedmodelling.thinklab.extensions;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.ISession;

public interface KnowledgeLoader {
	/**
	 * Define this one to load knowledge from the URL into the session passed. The loader in
	 * Session will do the rest.
	 * 
	 * @param url
	 * @param session
	 * @throws ThinklabException
	 */
	public abstract Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException;

	/**
	 * Write the passed instances to the specified outfile using the plugin's own conventions.
	 * @param outfile
	 * @param format a specific format specified by the user (between the ones handled) or null
	 * @param instances
	 * @throws ThinklabException
	 */
	public abstract void writeKnowledge(File outfile, String format, IInstance ... instances) throws ThinklabException;

}
