package org.integratedmodelling.utils;

import java.io.File;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;

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
