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
package org.integratedmodelling.utils;

import java.io.File;
import java.sql.Time;
import java.util.Hashtable;

import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.utils.xml.XMLDocument;

/**
 * Base generic class for a cache of "stuff" based on the filesystem, which can save the status of
 * the stuff cached, compare with the saved status, and trigger something when stuff has
 * changed. The status is kept in an XML file stored in a file named "cache.status" in a 
 * specified directory. The directory can be used to write other things into.
 * 
 * @author Ferdinando Villa
 *
 */
public abstract class FilesystemCache<T> {
	
	Hashtable<String, Time> cache = new Hashtable<String, Time>();

	protected File dir = null;
	File bak = null;
	XMLDocument xml = null;
	
	private void loadCache() throws ThinklabIOException {
		
		xml = new XMLDocument(bak);
	}
	
	public File getWorkDirectory() {
		return dir;
	}
	
	/**
	 * Must return date of object being added to cache 
	 * @param thing
	 * @return
	 */
	 abstract Time checkDate(T thing);
	
	 /**
	  * Must return current version of object from cache 
	  * @param thing
	  * @return
	  */
	 abstract T checkout(String key);
	
	 /**
	  * Must insert T object into cache. Only called if T is not already in there.
	  */
	 abstract void insert(T thing);
	 
	 /**
	  * Return a key that can 
	  */
	 abstract String getKey(T thing);
	 
	 /**
	  * Override as necessary
	  */
	 boolean notifyInsert(String key, T object, boolean wasNew) {
		 return true;
	 }
	 	 
	/**
	 * Pass a relative path to the data directory.
	 * @param workDir
	 * @throws ThinklabIOException
	 */
	public FilesystemCache(String workDir) throws ThinklabIOException {

		dir = LocalConfiguration.getDataDirectory(workDir);
		bak = new File(dir + "/cache.status");
		
		loadCache();
	}

	/**
	 * Pass an absolute path to a valid, writable directory.
	 * @param workDir
	 * @throws ThinklabIOException
	 */
	public FilesystemCache(File workDir) throws ThinklabIOException {

		dir = workDir;
		bak = new File(dir + "/cache.status");
		
		loadCache();
	}

}
