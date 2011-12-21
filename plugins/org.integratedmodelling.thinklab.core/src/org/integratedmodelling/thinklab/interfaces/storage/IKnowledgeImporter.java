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
package org.integratedmodelling.thinklab.interfaces.storage;

import java.util.Properties;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.utils.Polylist;

/**
 * Recognize a source of objects from a URL and allows retrieval of object definitions
 * contained in it. Use together with the KnowledgeLoader annotation if necessary to 
 * link to a specific format tag or extension. 
 * 
 * @author Ferdinando Villa
 *
 */
public interface IKnowledgeImporter {

	public void initialize(String url, Properties properties) throws ThinklabException;
	
	/**
	 * Number of objects in the source.
	 * 
	 * @return
	 */
	public int getObjectCount();
	
	/**
	 * ID of object #n
	 * @param i
	 * @return
	 */
	public String getObjectId(int i) throws ThinklabException;
	
	/**
	 * Create the n-th object, use the passed properties (and the global ones if
	 * necessary) to define the semantics if required.
	 * 
	 * @param i
	 * @param properties
	 * @return
	 */
	public Polylist getObjectDefinition(int i, Properties properties) throws ThinklabException ;
}
