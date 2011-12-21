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
package org.integratedmodelling.corescience.interfaces.data;

import java.util.Collection;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Anything that implements this is capable of returning the concept corresponding to the i-th 
 * data point. 
 * 
 * @author Ferdinando
 *
 */
public interface ICategoryData {

	/**
	 * Return the concept that most directly subsumes all the categories in the data source.
	 * 
	 * @return
	 */
	public abstract IConcept getConceptSpace();
	
	/**
	 * Return all the categories represented in the data source.
	 * 
	 * @return
	 */
	public abstract Collection<IConcept> getAllCategories();
	
	/**
	 * Return the category of data point n.
	 * 
	 * @param n
	 * @return
	 */
	public abstract IConcept getCategory(int n);
	
	/**
	 * Return an array of all data. Use only if strictly necessary.
	 * 
	 * @return
	 */
	public abstract IConcept[] getData();
	
}
