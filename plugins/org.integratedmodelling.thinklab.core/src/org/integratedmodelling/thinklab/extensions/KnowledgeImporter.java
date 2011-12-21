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

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.runtime.ISession;

/**
 * Objects of this kind will import knowledge from a source to a destination. Both are identified
 * by their URL, and the destination is typically a kbox, but this is not mandatory; the implementations
 * should be able to tell whether the destination is appropriate.
 * 
 * @author Ferdinando
 *
 */
public interface KnowledgeImporter {
	
	public abstract IList exportKnowledge(String sourceURL, ISession session) throws ThinklabException;

	public abstract void importKnowledge(String targetURL, IList knowledge) throws ThinklabException;
	
//	public abstract void transferKnowledge(String sourceURL, String targetURL, ISession session) throws ThinklabException;

}
