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
package org.integratedmodelling.thinklab.modelling.internal;

import org.integratedmodelling.thinklab.api.knowledge.IConcept;

/**
 * This one identifies a model capable of describing a state. Such models are
 * conceptualizable and must identify a compatible observation type(s) for query 
 * and conceptualization. They also must be capable of producing a query for
 * the observations they describe.
 * 
 * @author Ferd
 *
 */
public interface StateModel {

	/**
	 * Return the type of observation that we can deal with if we need to
	 * be paired to data from a kbox. If we're compatible with more than
	 * one type, build a union of types in the model manager session and 
	 * return that.
	 * 
	 * @return
	 */
	public abstract IConcept getCompatibleObservationType();
}
