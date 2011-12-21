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
package org.integratedmodelling.geospace;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.feature.ShapefileKBox;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.extensions.KBoxHandler;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.MiscUtilities;

public class GeospaceKboxHandler implements KBoxHandler {

	@Override
	public IKBox createKBox(String uri, String protocol, String dataUri, Properties properties) throws ThinklabException {

		IKBox ret = null;
		
		if (protocol.equals("shapefile")) {
			try {
				ret = new ShapefileKBox(uri, new URL(dataUri), properties);
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		return ret;
	}

	@Override
	public IKBox createKBoxFromURL(URI url) throws ThinklabStorageException {
		
		if (url.toString().startsWith("shapefile:")) {
			try {
				return new ShapefileKBox(url.toString(), MiscUtilities.getURLForResource(url.toString()), null);
			} catch (ThinklabException e) {
				throw new ThinklabStorageException(e);
			}
		}
		
		return null;
	}

}
