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
package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * A dataset is a persistent ObservationContext. Should be persistent and be able to
 * reconstruct the context it came from.
 * 
 * @author Ferdinando Villa
 */
public interface IDataset {

	/**
	 * Set the context for the dataset. If there is one, ensure compatibility of extents
	 * and merge states from it.
	 * 
	 * @param context
	 * @throws ThinklabException
	 */
	public abstract void setContext(IContext context) throws ThinklabException;
	
	/**
	 * Return the context we represent, creating it if we were loaded from persistent
	 * storage.
	 * 
	 * @param context
	 * @return
	 * @throws ThinklabException
	 */
	public abstract IContext getContext() 
		throws ThinklabException;

	/**
	 * Ensure we can get GC's without losing data. We should know where. Return a 
	 * location we can be restored from.
	 * 
	 * @throws ThinklabException
	 */
	public abstract String persist() throws ThinklabException;
	
	/**
	 * Read the dataset from assigned storage.
	 * @throws ThinklabException
	 */
	public abstract void restore(String location) throws ThinklabException;
	
}
