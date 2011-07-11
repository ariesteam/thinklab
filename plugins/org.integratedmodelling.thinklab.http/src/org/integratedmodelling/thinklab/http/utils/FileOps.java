package org.integratedmodelling.thinklab.http.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;

public class FileOps {

	public static int copyFiles(File src, File dest) throws IOException {
		return copyFiles(src, dest, null);
	}
	
	/**
	 * This function will copy files or directories from one location to another.
	 * note that the source and the destination must be mutually exclusive. This 
	 * function can not be used to copy a directory to a sub directory of itself.
	 * The function will also have problems if the destination files already exist.
	 * @param src -- A File object that represents the source for the copy
	 * @param dest -- A File object that represnts the destination for the copy.
	 * @throws IOException if unable to copy.
	 */
	public static int copyFiles(File src, File dest, String[] skipped) throws IOException {
		
		int ret = 0;
		
		//Check to ensure that the source is valid...
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath());
		} else if (!src.canRead()) { //check to ensure we have rights to the source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath());
		}
		
		if (skipped != null) {
			for (String sk : skipped)
				if (src.toString().endsWith(sk)) {
					return ret;
				}
		}
		
		//is this a directory copy?
		if (src.isDirectory()) 	{
			
			if (!dest.exists()) { //does the destination already exist?
				//if not we need to make it exist if possible (note this is mkdirs not mkdir)
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create directory: " + dest.getAbsolutePath());
				}
			}
			
			//get a listing of files...
			String list[] = src.list();
			//copy all the files in the list.
			for (int i = 0; i < list.length; i++)
			{				
				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				ret += copyFiles(src1 , dest1);
			}
		} else { 
			
			//This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
			int bytesRead;
			try {
				//open the files for input and output
				fin =  new FileInputStream(src);
				fout = new FileOutputStream (dest);
				//while bytesRead indicates a successful read, lets write...
				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer,0,bytesRead);
				}
			} catch (IOException e) { //Error copying file... 
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + 
							src.getAbsolutePath() + "to" + dest.getAbsolutePath()+".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { //Ensure that the files are closed (if they were open).
				if (fin != null) { fin.close(); }
				if (fout != null) { fin.close(); }
				ret ++;
			}
		}
		return ret;
	}
	
	public static void copyFilesCached(File src, File dest) throws IOException {
		copyFilesCached(src,dest,null);
	}
	
	/**
	 * This function will copy files or directories from one location to another.
	 * Destination files are only copied if they don't exist or the source is younger.
	 * 
	 * note that the source and the destination must be mutually exclusive. This 
	 * function can not be used to copy a directory to a sub directory of itself.
	 * The function will also have problems if the destination files already exist.
	 * @param src -- A File object that represents the source for the copy
	 * @param dest -- A File object that represents the destination for the copy.
	 * @throws IOException if unable to copy.
	 */
	public static int  copyFilesCached(File src, File dest, String[] skipped) throws IOException {

		int ret = 0;
		
		if (!src.exists()) {
			throw new IOException("copyFiles: Can not find source: " + src.getAbsolutePath());
		} else if (!src.canRead()) { //check to ensure we have rights to the source...
			throw new IOException("copyFiles: No right to source: " + src.getAbsolutePath());
		}
		
		if (src.isDirectory()) 	{
			
			if (!dest.exists()) { 
				
				if (!dest.mkdirs()) {
					throw new IOException("copyFiles: Could not create directory: " + dest.getAbsolutePath());
				}
			}

			String list[] = src.list();

			for (int i = 0; i < list.length; i++) {

				File dest1 = new File(dest, list[i]);
				File src1 = new File(src, list[i]);
				ret += copyFilesCached(src1 , dest1, skipped);
			}
		} else if (!dest.exists() || (src.lastModified() > dest.lastModified())) { 
			
			//This was not a directory, so lets just copy the file
			FileInputStream fin = null;
			FileOutputStream fout = null;
			byte[] buffer = new byte[4096]; //Buffer 4K at a time (you can change this).
			int bytesRead;
			try {

				fin =  new FileInputStream(src);
				fout = new FileOutputStream (dest);

				while ((bytesRead = fin.read(buffer)) >= 0) {
					fout.write(buffer,0,bytesRead);
				}
			} catch (IOException e) { 
				IOException wrapper = new IOException("copyFiles: Unable to copy file: " + 
							src.getAbsolutePath() + " to " + dest.getAbsolutePath()+".");
				wrapper.initCause(e);
				wrapper.setStackTrace(e.getStackTrace());
				throw wrapper;
			} finally { 
				if (fin != null) { fin.close(); }
				if (fout != null) { fin.close(); }
				ret++;
			}
		}
		
		return ret;
	}

	public static File getFileFromUrl(URL url) {
	
		String s = url.getFile();
		try {
			return new File(URLDecoder.decode(s, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
}
