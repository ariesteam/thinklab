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
package org.integratedmodelling.modelling.model;

import org.integratedmodelling.corescience.literals.DistributionValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;

import umontreal.iro.lecuyer.probdist.Distribution;

public abstract class DefaultStatefulAbstractModel extends DefaultAbstractModel {

	public DefaultStatefulAbstractModel(String namespace) {
		super(namespace);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void applyClause(String keyword, Object argument) throws ThinklabException {
		
		if (keyword.equals(":value")) {
			if (argument instanceof Distribution) {
				distribution = new DistributionValue((Distribution)argument);
			} else {
				state = validateState(argument);			
			}
		} else super.applyClause(keyword, argument);
	}
		
	
	protected abstract Object validateState(Object state) throws ThinklabValidationException;
		
	/*
	 * Copy the relevant fields when a clone is created before configuration
	 */
	protected void copy(DefaultStatefulAbstractModel model) {
		super.copy(model);
		state = model.state;		
	}

	public Object getState() {
		return this.state;
	}
	
	@Override
	public boolean isStateful() {
		return true;
	}
}
