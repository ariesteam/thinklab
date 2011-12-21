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

import java.util.Set;

import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * Tag interface for any object that can be stored in the model map. Currently that
 * means agents, models, contexts and scenarios. Hopefully it will stop at this, meaning
 * the whole thing makes sense.
 * 
 * @author Ferdinando
 *
 */
public interface IModelForm {

	
	/**
	 * Models must have an ID
	 * @return
	 */
	public abstract String getId();
	
	
	/**
	 * Models must have a namespace
	 * @return
	 */
	public abstract String getNamespace();	
	
	/**
	 * This must return getNamespace() + "/" + getId()
	 * @return
	 */
	public abstract String getName();
	
	/**
	 * Return the set of all concepts observed in this model object
	 * @return
	 */
	public Set<IConcept> getObservables();
}
