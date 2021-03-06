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

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public interface KnowledgeLoader {
	
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

}
