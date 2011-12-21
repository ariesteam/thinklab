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
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * These are simple aggregators of a state over its whole extent. In order to be used, they need
 * to be tagged with the @Aggregator annotation and explicitly called wy ID in a model command. They will
 * be applied only to the concepts  subsumed by the target concepts specified in the
 * annotation.
 * 
 * @author Ferd
 *
 */
public interface IStateAggregator {

	public double aggregate(IState state, IContext context) throws ThinklabException;
	
}
