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
package org.integratedmodelling.thinklab.extensions;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;

public interface KBoxHandler {

	/**
	 * Create the kbox with the passed URI, which should be enough to define it entirely. The
	 * initialize() function is not called by the knowledge manager. Called when the kbox is
	 * referenced directly by a URI using a recognized kbox protocol. Define this one to raise
	 * an exception if you don't want the kbox to be initialized this way.
	 * @param uri
	 * @return
	 * @throws ThinklabStorageException
	 */
	public abstract IKBox createKBoxFromURL(URI uri) throws ThinklabStorageException;
	
	/**
	 * Create the kbox for the passed protocol with no parameters, ready for initialization 
	 * through initialize(). This is called when the kbox is referenced through a metadata document
	 * identified by the kbox protocol. Define this one to raise an exception if you don't want the
	 * kbox to be initialized this way.
	 * @param properties 
	 * @param dataUri 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract IKBox createKBox(String originalURI, String protocol, String dataUri, Properties properties) throws ThinklabException;

}
