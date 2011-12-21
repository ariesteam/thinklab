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
package org.integratedmodelling.corescience.interfaces;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * Implemented by anything which is a physical entity or can produce one or more physical entities.
 * Used to extract identifications from observations of inherent properties. The current use is 
 * for derived representations such as grid extents, which may implement IEntifiable or have an IEntifiable in their
 * lineage so that the objects they were constructed from, if any (e.g. from rasterizing identifications of
 * spatial entities) can be reconstructed.
 *  
 * @author Ferdinando
 *
 */
public interface IEntifiable {

	/**
	 * Produce the entity or entities that this object represents. 
	 * 
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract Collection<IValue> getEntities() throws ThinklabException;
}
