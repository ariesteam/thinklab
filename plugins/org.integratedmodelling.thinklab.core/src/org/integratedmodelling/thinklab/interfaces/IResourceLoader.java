package org.integratedmodelling.thinklab.interfaces;

import java.io.File;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Plugins can install resource loaders, which will operate on subsequently loaded 
 * plugins. 
 * 
 * @author Ferdinando
 *
 */
public interface IResourceLoader {
	
	public void load(Properties properties, File loadDir) throws ThinklabException;

	public void unload(Properties properties, File loadDir);
	
}
