/**
 * MonetaryValueConceptualModel.java
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

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.cmodel.IValueMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.MediatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.ValidatingConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.corescience.observation.ConceptualModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.time.values.TimeValue;
import org.jscience.mathematics.number.Rational;

public class MonetaryValueConceptualModel extends ConceptualModel 
	implements MediatingConceptualModel, ValidatingConceptualModel {

	IInstance currency = null;
	TimeValue time = null;
	
	public IConcept getStateType() {
		// FIXME we may want to use a higher precision value from a financial 
		// library.
		return KnowledgeManager.Double();
	}

	public void validate(IObservation observation)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}

	public IValueAggregator getAggregator(IObservationContext ownContext, IObservationContext overallContext) {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue partition(IValue originalValue, Rational ratio) {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateData(byte b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(int b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(long b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(float b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	public IValue validateData(double b) throws ThinklabValidationException {

		double dv = (double)b;
		// TODO more validation
		return new NumberValue(dv);
	}

	@Override
	public IConcept getUncertaintyType() {
		return KnowledgeManager.Nothing();
	}

	@Override
	public IValueMediator getMediator(IConceptualModel conceptualModel,
			IObservationContext ctx) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
}
