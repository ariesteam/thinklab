/**
 * ExpressionDatasource.java
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
package org.integratedmodelling.corescience.datasources;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.observation.IDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.LanguageInterpreter;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.utils.Pair;

/**
 * Datasource returning the result of evaluating an expression, binding the dependent observation's IDs to 
 * their current state.
 * 
 * @author Ferdinando Villa
 *
 */
public class ExpressionDatasource implements IDataSource, IInstanceImplementation {

	String expressionSource = null;
	AlgorithmValue expression = null;
	LanguageInterpreter interpreter = null;
	LanguageInterpreter.IContext expContext = null;
	
	static final String HAS_ALGORITHM = "source:hasAlgorithm";
	
	public Pair<IValue, IUncertainty> getValue(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
		// bind variable values to context
		
		
		// exec expression, make IValue from it
		IValue ret = null;
		try {
			ret = interpreter.execute(expression, expContext);
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(e);
		}
		
		return new Pair<IValue, IUncertainty>(ret, null);
	}

	public boolean handshake(IConceptualModel cm,
			IObservationContext observationContext,
			IObservationContext overallContext)
			throws ThinklabValidationException {

		// TODO Auto-generated method stub
		
		// match variable names to dependency IDs
		
		// get compiled expression from factory
		
		return false;
	}

	public Pair<IValue, IUncertainty> getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize(IInstance i) throws ThinklabException {
		// TODO read expression
		
	}

	public void validate(IInstance i) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	public Pair<String, IUncertainty> getValueLiteral(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueType getValueType() {
		return ValueType.IVALUE;
	}

}
