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
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;

public class LocalConfiguration {

	static Properties properties = null;
	
    static File systemPrefix = null;
    static File writableSpace = null;
    static File configSpace = null;
    static File userConfigPath = new File(System.getProperty("user.home") + "/.thinklab");
    
    static Logger log =  Logger.getLogger("org.integratedmodelling.utils.LocalConfiguration");
    static public File getSystemPath() {
    	return new File(getProperties().getProperty("thinklab.system.prefix"));
    }

    static public File getDataPath() {
    	return new File(getProperties().getProperty("thinklab.data.prefix"));
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
    
    
	/**
	 * Get the global system preferences. These can be overridden using files.
	 * @return the global system preferences.
	 */
	static public Properties getProperties() {

        if (properties == null) {

            properties = new Properties();
			
			/* look for properties file in typical places and load it if existing. */
			File sysconf = new File("/usr/share/thinklab");
            File wprefix = new File(System.getProperty("user.dir") + "/thinklab/data");

            switch (getOS()) {
            case MACOS: 
                // TODO where the heck should this be on a Mac?
                sysconf = new File("/usr/share/thinklab");
            	break;
            case WIN: 
                sysconf = new File("C:\\Program Files\\ThinkLab");
            	break;
            case UNIX:
                sysconf = new File("/usr/share/thinklab");
            	break;
            }
            
            File sysconfig = new File(getUserConfigDirectory() + "/thinklab.properties");
            
            if (!sysconfig.exists()) 
                sysconfig = new File(sysconf + "/config/thinklab.properties");

            if (sysconfig.exists())
                try {
                	log.info("ThinkLab configuration loaded from : " + sysconfig);
                    properties.load(new FileInputStream(sysconfig));
                } catch (Exception e) {
                    // just do nothing
                } 
                
            if (properties.getProperty("thinklab.system.prefix") == null)
                properties.setProperty("thinklab.system.prefix", sysconf.toString());
            if (properties.getProperty("thinklab.data.prefix") == null)
                properties.setProperty("thinklab.data.prefix", wprefix.toString());
		}
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
		
        return new File(getProperties().get("thinklab.system.prefix") + "/" + ID);
    }        
	
	/**
	 * Obtain a writable directory name from preferences and return it after making sure it exists and is writable.
	 * @param pref a Preferences object to use
	 * @param ID ID of the property in Preferences
	 * @return a usable directory.
	 * @throws ThinklabIOException if IO errors occur.
	 */
	static public File getDataDirectory(String ID) throws ThinklabIOException {

        File ret = new File(getProperties().getProperty("thinklab.data.prefix") + "/" + ID);
        
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
	
	
}
