package org.integratedmodelling.thinklab.interfaces;

import java.io.File;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Plugins can install resource loaders, which will operate on subsequently loaded 
 * plugins. Each resource loader installed will be passed the parsed content of the
 * THINKLAB-INF/thinklab.properties file (empty if there is no such file) as well as 
 * the plugin load directory, to do what it needs to do with. Resource loaders are called after 
 * every standard resource (such as ontologies) has been installed, but just before doStart() is invoked.
 * 
 * @author Ferdinando
 *
 */
public interface IResourceLoader {
	public void load(Properties properties, File loadDir) throws ThinklabException;
}
