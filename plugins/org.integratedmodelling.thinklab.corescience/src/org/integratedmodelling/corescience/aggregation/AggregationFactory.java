/**
 * AggregationFactory.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience.aggregation;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.interfaces.IConformance;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.utils.Pair;

/**
 * Separate a pool of objects into groups based on a passed conformance
 * policy.
 * 
 * @author Ferdinando Villa
 * @date August 4, 2007
 */
public class AggregationFactory {

	IConformance conformance = null;
	ArrayList<IInstance> objects = new ArrayList<IInstance>();
	
	public AggregationFactory(IConformance conformance) {
		this.conformance = conformance;
	}
	
	void addObject(IInstance instance) {
		objects.add(instance);
	}

	Collection<Pair<Collection<IInstance>, Constraint>> aggregate() {
		return null;
	}
	
}
