/**
 * IKBoxPlugin.java
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
package org.integratedmodelling.thinklab.interfaces;

import java.net.URI;
import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.impl.Session;

/**
 * A plugin specialized to handle a KBox protocol. Only difference with a standard Plugin is that
 * it must provide a methods to create the KBox from its URL. The Knowledge manager locates the
 * plugin through the registry and defers kbox creation to it.
 * 
 * @author Ferdinando Villa
 * @see Session
 * @deprecated use extension points
 */
public interface IKBoxPlugin extends IPlugin {

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
	public abstract IKBox createKBox(String protocol, String dataUri, Properties properties) throws ThinklabException;

}
