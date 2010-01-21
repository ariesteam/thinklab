package org.integratedmodelling.thinklab;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.storage.IPersistentObject;
import org.integratedmodelling.utils.MiscUtilities;

public class PersistenceManager {

	HashMap<String, Class<?>> extensions = new HashMap<String, Class<?>>();
	HashMap<String, Class<?>> classIndex = new HashMap<String, Class<?>>();
	
	static PersistenceManager _this = null;
	
	public static PersistenceManager get() {
		if (_this == null)
			_this = new PersistenceManager();
		return _this;
	}
	
	public void registerSerializableClass(Class<?> clazz, String extension) {
		classIndex.put(clazz.getCanonicalName(), clazz);
		if (extension != null)
			extensions.put(extension, clazz);
	}
	
	/**
	 * Create an object for a given class. Normally you would just use Class.forName and
	 * call a newInstance on it, but this will find classes declared in plugins that may
	 * not be visible everywhere.
	 * 
	 * @param clazz
	 * @return
	 * @throws ThinklabResourceNotFoundException
	 */
	public IPersistentObject createPersistentObject(String clazz) throws ThinklabResourceNotFoundException {
		
		Class<?> cls = classIndex.get(clazz);
		if (cls == null)
			throw new ThinklabResourceNotFoundException(
					"PersistenceManager: class " + clazz + " was not registered for persistence" );

		IPersistentObject ret = null;
		
		try {
			ret = (IPersistentObject) cls.newInstance();
		} catch (Exception e) {
			throw new ThinklabResourceNotFoundException(e);
		}
		
		return ret;
	}
	
	/**
	 * If the extension of a file or URL has been linked to a persistent object class
	 * through a PersistentObject annotation, this one will find the class and create
	 * the object from the corresponding serialized inputstream. Files, URLs and plugin
	 * path :: resource syntax should all be good input.
	 * 
	 * @param file
	 * @return
	 * @throws ThinklabException
	 */
	public IPersistentObject loadPersistentObject(String file) throws ThinklabException {
		
		Class<?> cls = extensions.get(MiscUtilities.getFileExtension(file));
		if (cls == null)
			throw new ThinklabResourceNotFoundException(
					"PersistenceManager: extension in " + file + " is not linked to any persistent class");
		IPersistentObject ret = null;
		
		try {
			ret = (IPersistentObject) cls.newInstance();
		} catch (Exception e) {
			throw new ThinklabResourceNotFoundException(e);
		}
		
		InputStream inp = MiscUtilities.getInputStreamForResource(file);
		ret.deserialize(inp);
		
		return ret;
	}
}