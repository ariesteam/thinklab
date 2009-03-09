/**
 * LocalConfiguration.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.configuration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

/**
 * 
 * @author Ferdinando Villa
 *
 */
public class LocalConfiguration {

	static Properties properties = new Properties();
	
    static File userConfigPath = 
    	new File(
    			System.getProperty("user.home") + 
    			File.separator + 
    			".thinklab");
    
    static File systemPath = 
    	new File(
    			System.getProperty("user.home") + 
    			File.separator + 
    			".thinklab" + 
    			File.separator + 
    			"system");

    static File dataPath = 
    	new File(
    			System.getProperty("user.home") + 
    			File.separator + 
    			".thinklab" + 
    			File.separator + 
    			"data");
    
    static public File getSystemPath() {
    	return systemPath;
    }

    static public File getDataPath() {
    	return dataPath;
    }
    
    private static void setup() {
    	
		if (!userConfigPath.exists())
			userConfigPath.mkdir();
    	
    	if (properties.containsKey("thinklab.system.prefix"))
    		systemPath = new File(getProperties().getProperty("thinklab.system.prefix"));
    	if (properties.containsKey("thinklab.data.prefix"))
    		dataPath = new File(getProperties().getProperty("thinklab.data.prefix"));    	
    	
    	systemPath.mkdirs();
    	dataPath.mkdirs();
    }
    
    static OS os = null;
    
    static public OS getOS() {
    	
    	if (os == null) {

    		String osd = System.getProperty("os.name").toLowerCase();
            
            // TODO ALL these checks need careful checking
            if (osd.contains("windows")) {
               os = OS.WIN;
            } else if (osd.contains("mac")) {
            	os = OS.MACOS;
            } else if (osd.contains("linux") || osd.contains("unix")) {
            	os = OS.UNIX;
            } 
    		
    	}
    	
    	return os;
    }
    
    public static void setProperties(Properties p) {
    	
    	properties.putAll(p);
    	setup();
    }
    
    public static void loadProperties(URL pfile) throws ThinklabIOException {

    	try {
			properties.load(pfile.openStream());
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		setup();
    }
    
	/**
	 * Get the global system preferences. These can be overridden using files.
	 * @return the global system preferences.
	 * 
	 * FV properties must now be loaded explicitly - done in the core plugin
	 */
	static public Properties getProperties() {
        return properties;
	}
	
	
	/**
	 * Obtain the full system directory identified by the relative path passed. Directory may not exist: needs
     * to be checked. Users should not expect to be able to create this.
     * 
	 * @param path the path to the directory desired relative to the main system path
	 * @return a directory name, which may or may not exist
	 */
	static public File getSystemDirectory(String ID) {		
		
        return new File(systemPath + File.separator + ID);
    }        
	
	/**
	 * Obtain a writable directory name from preferences and return it after making sure it exists and is writable.
	 * @param pref a Preferences object to use
	 * @param ID ID of the property in Preferences
	 * @return a usable directory.
	 * @throws ThinklabIOException if IO errors occur.
	 */
	static public File getDataDirectory(String ID) throws ThinklabIOException {

        File ret = new File(dataPath + File.separator + ID);
        
		/* create if absent */
		if (!ret.exists() || !ret.isDirectory())
			ret.mkdirs();
		
		/* complain if still not there or not writable*/
		if (!ret.exists() || !ret.isDirectory() || !ret.canWrite())
			throw new ThinklabIOException("can't find or create directory " + ret);
		
		return ret;

		
	}
	
	public static File getUserConfigDirectory()  {
		
		if (!userConfigPath.exists())
			userConfigPath.mkdir();

		return userConfigPath;
	}
	
	public static URL getResource(String ID) throws ThinklabIOException{
		URL url;
		try {
			url = new URL(getProperties().getProperty(ID));
		} catch (MalformedURLException e) {
			throw new ThinklabIOException("can't locate resource " + ID);
		}
		return url;
	}
	
	/**
	 * An exception-silent version of the {@getResource} method. If the resource is missing
	 * returns the default one. If this one is malformed as well, then simply
	 * returns null. 
	 * @param ID the resource id (i.e. "reasoner.url")
	 * @param alternative the default value (i.e. "http://localhost:8080")
	 * @return
	 */
	public static URL getResource(String ID, String alternative){
		URL url;
		try {
			url = new URL(getProperties().getProperty(ID));
		} catch (MalformedURLException e1) {
			try {
				url = new URL(alternative);
			} catch (Exception e2) {
				return null;
			}
		}
		return url;
	}
	
	public static boolean hasResource(String ID) {
		URL url = null; 
		try {
			url = new URL(getProperties().getProperty(ID));
		} catch (MalformedURLException e) {
			// The exception is not thrown - simply return false!
			return false;
		}
		return url != null;
	}

	public static boolean strictValidation() {
		String validation = getProperties().getProperty("thinklab.validation", "strict");
		return validation.equals("strict");
	}
	
	
}
