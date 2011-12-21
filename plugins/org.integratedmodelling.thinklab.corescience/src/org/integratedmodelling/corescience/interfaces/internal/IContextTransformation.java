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
package org.integratedmodelling.corescience.interfaces.internal;

import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * These can be inserted in a context to modify a state during contextualization.
 * 
 * @author ferdinando.villa
 *
 */
public interface IContextTransformation {

	public abstract Object transform(
			Object original, 
			IContext context, 
			int stateIndex, 
			Map<?,?> parameters);
	
	/**
	 * The transformation must be able to return a fresh
	 * copy of itself, serving as a factory, so that implementations
	 * can rely on state encountered for caching context information.
	 * 
	 * @return
	 */
	public abstract IContextTransformation newInstance();
	
	/**
	 * Return the observable class this applies to.
	 * @return
	 */
	public abstract IConcept getObservableClass();

	/**
	 * We allow null or invalid transformations (e.g. when referring to gazetteer
	 * shapes that are not found) to allow loading contexts without errors when
	 * those are referenced.
	 * 
	 * @return
	 */
	public abstract boolean isNull();
}
