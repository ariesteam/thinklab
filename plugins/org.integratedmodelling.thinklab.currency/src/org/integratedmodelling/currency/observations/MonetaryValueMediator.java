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
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.values.TimeValue;
import org.integratedmodelling.utils.Pair;

/**
 * Mediates the purchasing power of one currency at a time towards another at another
 * time. May throw an exception 
 * @author Ferdinando Villa
 *
 */
public class MonetaryValueMediator implements IValueMediator {

	double conversionFactor = 1.0;
	
	public MonetaryValueMediator(String currencyFrom, TimeValue dateFrom, String currencyTo, TimeValue dateTo) throws ThinklabInexactConversionException {
		
		/* just store the conversion factor */
		conversionFactor = 
			CurrencyPlugin.get().getConverter().
				getConversionFactor(currencyFrom, dateFrom, currencyTo, dateTo);
	}
	
	public IValue getMediatedValue(IValue value,
			IObservationContextState context)
			throws ThinklabInexactConversionException {
		
		double val = 0.0;
		try {
			val = value.asNumber().asDouble() * conversionFactor;
		} catch (ThinklabValueConversionException e1) {
			/* won't happen */
		}
		IValue ret = value.clone();
		try {
			ret.asNumber().assign(val);
		} catch (ThinklabValidationException e) {
			/* won't happen */
		}
		return ret;
	}

	public Pair<IValue, IUncertainty> getMediatedValue(IValue value,
			IUncertainty uncertainty, IObservationContextState context) {

		IValue v = null;
		try {
			v = getMediatedValue(value, context);
		} catch (ThinklabInexactConversionException e) {
			/* won't happen */
		}
		IUncertainty u = null;
		
		// TODO handle uncertainties. This should be fairly easy given a regular
		// fractional uncertainty implementation.
		
		return new Pair<IValue, IUncertainty>(v, u);
	}

	public void initialize(IObservationContext overallContext) {
		// nothing
	}

	public boolean isExact() {
		return false;
	}

}
