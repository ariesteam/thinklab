package org.integratedmodelling.thinklab.project.interfaces;

import java.io.File;

import org.integratedmodelling.exceptions.ThinklabException;

/**
 * Each resource loader installed will be passed the parsed content of the
 * THINKLAB-INF/thinklab.properties file (empty if there is no such file) as well as 
 * the plugin load directory, to do what it needs to do with. Resource loaders are called after 
 * every standard resource (such as ontologies) has been installed, but just before doStart() is invoked.
 * 
 * @author ferdinando.villa
 *
 */
public interface IProjectLoader {

	/**
	 * Handle resources in the given directory
	 * 
	 * @param directory
	 * @throws ThinklabException
	 */
	void load(File directory) throws ThinklabException;

	/**
	 * Cleanup resources coming from given directory
	 * 
	 * @param directory
	 * @throws ThinklabException
	 */
	void unload(File directory) throws ThinklabException;

}
