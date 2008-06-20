/**
 * CopyURL.java
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.channels.FileChannel;

import org.integratedmodelling.thinklab.exception.ThinklabIOException;



/**
 * An utility class to write a URL's content into a local file.
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 *
 */
public class CopyURL {
	
	/**
	 * Copy the given URL to the given local file, return number of bytes copied.
	 * @param url the URL
	 * @param file the File
	 * @return the number of bytes copied.
	 * @throws ThinklabIOException if URL can't be read or file can't be written.
	 */
	public static long copy(URL url, File file) throws ThinklabIOException
	{
		long count=0;
		
		try	{
			InputStream is = url.openStream();
			FileOutputStream fos = new FileOutputStream(file);

			int oneChar;
			while ((oneChar=is.read()) != -1) {
				fos.write(oneChar);
				count++;
			}
			
			is.close();			
			fos.close();
		} catch (Exception e) { 
			throw new ThinklabIOException(e.getMessage()); 
		}
		
		return count;
	}
	
	/**
	 * Copy the given File to the given local file, return number of bytes copied.
	 * @param url the URL
	 * @param file the File
	 * @return the number of bytes copied.
	 * @throws ThinklabIOException if URL can't be read or file can't be written.
	 */
	public static long copy(File url, File file) throws ThinklabIOException
	{
		long count=0;
		
		try	{
			InputStream is = new FileInputStream(url);
			FileOutputStream fos = new FileOutputStream(file);

			int oneChar;
			while ((oneChar=is.read()) != -1) {
				fos.write(oneChar);
				count++;
			}
			
			is.close();			
			fos.close();
		} catch (Exception e) { 
			throw new ThinklabIOException(e.getMessage()); 
		}
		
		return count;
	}
	
    public static void copyBuffered(File src, File dst) throws ThinklabIOException {

		try {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			throw new ThinklabIOException(e.getMessage());
		}

	}

    public static File getFileForURL(URL url) throws ThinklabIOException {
    	if (url.toString().startsWith("file:")) {
    		return new File(url.getFile());
    	} else {
    		File temp;
			try {
				temp = File.createTempFile("url", "url");
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
    		copy(url, temp);
    		return temp;
    	}
    }
    
    public static void copyChanneled(File src, File dst) throws ThinklabIOException {

		try {
			// Create channel on the source
			FileChannel srcChannel = new FileInputStream(src)
					.getChannel();

			// Create channel on the destination
			FileChannel dstChannel = new FileOutputStream(dst)
					.getChannel();

			// Copy file contents from source to destination
			dstChannel.transferFrom(srcChannel, 0, srcChannel.size());

			// Close the channels
			srcChannel.close();
			dstChannel.close();

		} catch (IOException e) {
			throw new ThinklabIOException(e.getMessage());
		}
	}
}
