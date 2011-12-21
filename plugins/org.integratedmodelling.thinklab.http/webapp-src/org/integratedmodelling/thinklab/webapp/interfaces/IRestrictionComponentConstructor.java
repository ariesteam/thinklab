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
package org.integratedmodelling.thinklab.webapp.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.zkoss.zk.ui.Component;

public interface IRestrictionComponentConstructor {

	/**
	 * Should return a Component that implement IRestrictionComponent and lets the user
	 * select an object of type range, possibly in the context of passed property. When
	 * ZK is fixed, we'll use an extended interface - not possible for now, due to
	 * duplication of the clone() method in Component.
	 * 
	 * @param property
	 * @param label
	 * @return
	 * @throws ThinklabException 
	 */
	public Component createRestrictionComponent(IProperty property, String label, int indentLevel)
		throws ThinklabException;

}
