/**
 * MonetaryValueMediator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCurrencyPlugin.
 * 
 * ThinklabCurrencyPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCurrencyPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.currency.observations;

import org.integratedmodelling.corescience.exceptions.ThinklabInexactConversionException;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.IStateAccessor;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.time.literals.TimeValue;

/**
 * Mediates the purchasing power of one currency at a time towards another at another
 * time. 
 * @author Ferdinando Villa
 *
 */
public class MonetaryValueMediator implements IStateAccessor {

	double conversionFactor = 1.0;
	
	public MonetaryValueMediator(String currencyFrom, TimeValue dateFrom, String currencyTo, TimeValue dateTo) throws ThinklabInexactConversionException {
		
		/* just store the conversion factor */
		conversionFactor = 
			CurrencyPlugin.get().getConverter().
				getConversionFactor(currencyFrom, dateFrom, currencyTo, dateTo);
	}

	@Override
	public Object getValue(int idx, Object[] registers) {
		return ((Double)registers[0]) * conversionFactor;
	}

	@Override
	public boolean isConstant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean notifyDependencyObservable(IObservation o,
			IConcept observable, String formalName) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void notifyDependencyRegister(IObservation observation,
			IConcept observable, int register, IConcept stateType)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyState(IState dds, IObservationContext overallContext,
			IObservationContext ownContext) throws ThinklabException  {
		// TODO Auto-generated method stub
		
	}


}