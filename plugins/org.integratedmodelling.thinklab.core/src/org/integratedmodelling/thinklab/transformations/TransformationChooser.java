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
package org.integratedmodelling.thinklab.transformations;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Maps concepts to data transformations to apply when data in states are returned as doubles. 
 * Properties to map concepts to transformations should have the form
 * 
 * thinklab.transformation.mapping.representation-LogDistributedState = log10
 *
 * which assumes a log10 transformation was defined.
 *  
 * @author Ferdinando
 *
 */
public class TransformationChooser extends ConfigurableIntelligentMap<ITransformation> {

	public TransformationChooser() {
		super("thinklab.transformation.mapping.");
	}

	@Override
	protected ITransformation getObjectFromPropertyValue(String pvalue, Object[] parameters) 
		throws ThinklabException {
		return TransformationFactory.get().requireTransformation(pvalue, parameters);
	}
}
