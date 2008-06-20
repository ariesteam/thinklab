/**
 * FileTypeFilter.java
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
package org.integratedmodelling.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple filter for filetypes. It implements the {@code java.io.FilenameFilter} interface, so it can be used directly 
 * through the {@code java.io.File} methods, as {@code list()} and {@code listFile()}.
 * Furthermore, provides with implementations that search through subfolders.
 * Note that the matching is not case sensitive (i.e. xml and XML are the same endings).
 * 
 * Certain filetype endings used in Thinklab are added as public static attributes.
 * 
 * @author Ioannis N. Athanasiadis, Dalle Molle Institute for Artificial Intelligence, USI/SUPSI
 *
 * @since 19 Apr 2006
 */
public class FileTypeFilter implements FilenameFilter {
	public static String OWLFileType = ".owl";
	public static String RepositoryFileType = ".repository";
	public static String JavaFileType = ".java";
	public static String HBMFileType = ".hbm.xml";
	public static String HBCFileType = ".cfg.xml";
	public static String XMLFileType = ".xml";
	protected String pattern;
	private String fileseparator = System.getProperty("file.separator");

	/**
	 * The default contructor that creates the filter 
	 * 
	 * @param str file type ending
	 */
	public FileTypeFilter(String str) {
		pattern = str;
	}

	/* (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith(pattern.toLowerCase());
	}
	
	/**
	 * Returns all files matching the filter as a set of files
	 * @param folder to search in
	 * @return Set of Files
	 */
	public Set<File> listFilesSubFoldersIncluded(File folder){
		HashSet<File> set = new HashSet<File>();
		listFilesSubFoldersIncludedHelper(set, folder);
		return set;
	}
	
	private  void listFilesSubFoldersIncludedHelper(Set<File> set,File folder){
		for (File file: folder.listFiles()){
			if(file.isDirectory())
				listFilesSubFoldersIncludedHelper(set, file);
			else 
				if (file.getName().toLowerCase().endsWith(pattern.toLowerCase()))
					set.add(file);
		}
	}
	
	/**
	 * Returns all files matching the filter as a set of Strings relative to the root folder
	 * @param folder to search in
	 * @return Set of Strings
	 */
	public Set<String> listSubFoldersIncluded(File folder){
		HashSet<String> set = new HashSet<String>();
		listSubFoldersIncludedHelper(set, folder, "");
		return set;
	}
	
	private  void listSubFoldersIncludedHelper(Set<String> set,File folder, String path){
		for (File file: folder.listFiles()){
			if(file.isDirectory())
				listSubFoldersIncludedHelper(set, file, path+file.getName()+fileseparator );
			else 
				if (file.getName().toLowerCase().endsWith(pattern.toLowerCase()))
					set.add(path+file.getName());
		}
	}
	
}
