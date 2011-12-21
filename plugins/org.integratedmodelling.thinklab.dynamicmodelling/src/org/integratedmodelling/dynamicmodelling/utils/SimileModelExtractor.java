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
package org.integratedmodelling.dynamicmodelling.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.Base64;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Decode the MIME Base64 portion of a .sml file that contains the model and return the relevant
 * input stream.
 * 
 * @author Ferdinando Villa
 *
 */
public class SimileModelExtractor {

	/**
	 * Extract prolog stream from named URL. Optionally save to passed file. 
	 * @param mime the MIME base64 file containing a model.pl section.
	 * @param saveTo file to save the prolog stream to. Will be overwritten if existing. Pass
	 * null if you don't need to keep it, and time will be saved.
	 * @return an InputStream that will stream the pl specs.
	 * @throws ThinklabException
	 */
	public static InputStream extractPrologModel(URL mime, File saveTo) throws ThinklabException {
		
		File inter = null;
		BufferedReader reader = null;
		FileOutputStream out = null;
		
		/* the boring and inefficient way: read from the first non-empty line after an empty
		 * one that's after the model.pl identifier to another empty one and write on a 
		 * temp file.
		 */
		try {
			
			inter = File.createTempFile("sml_", "mime");
			out = new FileOutputStream(inter);
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
				
		try {
			reader = new BufferedReader(new InputStreamReader(mime.openStream()));
		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
		boolean done = false;
		boolean hasm = false;
		for ( ;!done ;) {
			
			String s;
			try {
				s = reader.readLine();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}

			if (!hasm && s.contains("/model.pl")) {				
				hasm = true;
				
				/* skip to separator, after which model starts */
				do {
					try {
						s = reader.readLine();
					} catch (IOException e) {
						throw new ThinklabIOException(e);
					}
				} while (!s.trim().equals(""));
				
			} else if (hasm && s.trim().equals("")) {
				/* finished */
				done = true;
			} else {
				/* save if reading model, skip if not */
				if (hasm) {
					
					try {
						out.write(s.getBytes());
					} catch (IOException e) {
						throw new ThinklabIOException(e);
					}
				}
			}
		}
		
		try {
			out.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		/* decode temp file */
        FileInputStream fis;
		try {
			fis = new FileInputStream(inter);
		} catch (FileNotFoundException e) {
			throw new ThinklabIOException(e);
		}
        BufferedInputStream bis = new BufferedInputStream( fis );
        Base64.InputStream b64is = new Base64.InputStream( bis, Base64.DECODE & Base64.DONT_BREAK_LINES);
		
        InputStream ret = b64is;
        
        if (saveTo != null) {
        	try {
        		MiscUtilities.writeToFile(saveTo.toString(), b64is, true);
        		b64is.close();
        		ret = new FileInputStream(saveTo);
        	} catch (Exception e) {
        		throw new ThinklabIOException(e);
        	}
        }
        
		return ret;
		
	}
}
