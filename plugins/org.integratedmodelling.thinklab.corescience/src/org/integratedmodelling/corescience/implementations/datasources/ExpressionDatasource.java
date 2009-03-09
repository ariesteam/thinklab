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
package org.integratedmodelling.corescience.implementations.datasources;

import java.util.Properties;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.ComputedDataSource;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;

/**
 * Datasource returning the result of evaluating an expression, binding the dependent observation's IDs to 
 * their current state.
 * 
 * @author Ferdinando Villa
 *
 */
public class ExpressionDatasource 
	implements IDataSource<Object>, IInstanceImplementation, ComputedDataSource {

	String expressionSource = null;

	@Override
	public void initialize(IInstance i, Properties properties) throws ThinklabException {
		// TODO read expression
		
	}

	@Override
	public void validate(IInstance i) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(int index, Object[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void notifyDependency(IConcept observable, IConcept type, int register)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IDataSource<?> validateDependencies()
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getInitialValue() {
		return null;
	}

	@Override
	public boolean handshake(IConceptualModel cm,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

}
