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
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;

import org.integratedmodelling.exceptions.ThinklabIOException;

/**
 * 
 * @author Ferdinando Villa
 *
 */
public class HttpDocumentCache extends FilesystemCache<URL> {

	public HttpDocumentCache(String workDir) throws ThinklabIOException {
		super(workDir);
		// TODO Auto-generated constructor stub
	}

	/**
	 * TEMPORARY: I'll generalize later. 
	 * @throws ThinklabIOException 
	 */
	public URL cache(URL doc) throws ThinklabIOException {
		
		URL ret = doc;
		boolean needRefresh = true;
		
		if (doc.getProtocol().equals("http")) {
			
			File fpath = new File(doc.getPath());
			try {
				fpath = new File(getWorkDirectory() + "/" + fpath.getName());

				// TODO check stored dates through URLconnection and cache repository
				needRefresh = !fpath.exists();
				
				ret = fpath.toURL();
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}

			if (needRefresh)	
				CopyURL.copy(doc, fpath);
			
		} else if (!doc.getProtocol().equals("file"))
			throw new ThinklabIOException("HTTP cache: protocol " + doc.getProtocol() + " not supported" );
		
		return ret;
	}
	
	@Override
	Time checkDate(URL thing) {

		// uc = httpurlconnection; uc.getLastModified()
		
		return null;
	}

	@Override
	URL checkout(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	String getKey(URL thing) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void insert(URL thing) {
		// TODO Auto-generated method stub
		
	}

}
