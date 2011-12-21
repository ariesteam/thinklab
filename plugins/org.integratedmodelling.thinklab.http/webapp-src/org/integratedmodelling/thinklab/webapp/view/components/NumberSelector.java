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
package org.integratedmodelling.thinklab.webapp.view.components;

import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IProperty;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponentConstructor;
import org.zkoss.zk.ui.Component;

public class NumberSelector extends LiteralSelector implements IRestrictionComponentConstructor {

	private static final long serialVersionUID = -7882278377506046859L;

	public NumberSelector(IProperty p, int indentLevel) {
		super(p);
	}

	@Override
	protected Restriction defineRestriction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setup() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component createRestrictionComponent(IProperty property, String label, int indentLevel) 
		throws ThinklabException {
		NumberSelector ret = new NumberSelector(property, indentLevel);
		ret.setWidth("100%");
		return ret;
	}

}
