/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab;

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
		return ret.deserialize(inp);
	}
}