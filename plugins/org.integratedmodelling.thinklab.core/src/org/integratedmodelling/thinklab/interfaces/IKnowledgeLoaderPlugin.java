/**
 * IKnowledgeLoaderPlugin.java
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

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.session.Session;

/**
 * A plugin specialized to read knowledge from a given format. Adds one abstract method and must be named
 * <FORMAT>LoaderPlugin where FORMAT corresponds to the uppercase version of the URL extension corresponding
 * to the format supported.
 * 
 * @author Ferdinando Villa
 * @see Session
 * @deprecated use extension point
 */
public interface IKnowledgeLoaderPlugin extends IPlugin {
	
	/**
	 * Define this one to load knowledge from the URL into the session passed. The loader in
	 * Session will do the rest.
	 * 
	 * @param url
	 * @param session
	 * @throws ThinklabException
	 */
	public abstract Collection<IInstance> loadKnowledge(URL url, ISession session, IKBox kbox) throws ThinklabException;

	/**
	 * Write the passed instances to the specified outfile using the plugin's own conventions.
	 * @param outfile
	 * @param format a specific format specified by the user (between the ones handled) or null
	 * @param instances
	 * @throws ThinklabException
	 */
	public abstract void writeKnowledge(File outfile, String format, IInstance ... instances) throws ThinklabException;
	
	/**
	 * Define this to return true if the plugin knows how to handle a URL with this extension.
	 */
	public abstract boolean handlesFormat(String format);

}
