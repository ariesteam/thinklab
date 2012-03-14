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

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;

/**
 * The only purpose of this is to add existing files to a zip archive easily.
 * Not efficient, not right.
 * 
 * @author Ferdinando Villa
 *
 */
public class Zipper {

	String zipfile = null;
	File tempdir = null;
	
	public Zipper(String zipfile) throws ThinklabIOException {
		this.zipfile = zipfile;
		tempdir = MiscUtilities.createTempDir();
	}
	
	public void addFile(String file) throws ThinklabIOException {
		
		/*
		 * copy file to temp directory
		 */			
		String fname = MiscUtilities.getFileName(file);
		File f = new File(tempdir + File.separator + fname);
		CopyURL.copy(new File(file), f);
	}
	
	/**
	 * Copy given file with given basename (extension not modified)
	 * @param file
	 * @param basename
	 * @throws ThinklabIOException
	 */
	public void addFile(String file, String basename) throws ThinklabIOException {
		
		/*
		 * copy file to temp directory
		 */			
		String ext = MiscUtilities.getFileExtension(file);
		File f = new File(tempdir + File.separator + basename + "." + ext);
		CopyURL.copy(new File(file), f);
	}
	
	public File getFile(String basename) {
		return new File(tempdir + File.separator + basename);
	}
	
	public void write() throws ThinklabException {
		FolderZiper.zipSubFolders(tempdir.toString(), zipfile);
		MiscUtilities.deleteDirectory(tempdir);
	}
}
