/**
 * RandomDataSource.java
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
package org.integratedmodelling.corescience.datasources.random;

import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.utils.Pair;

import cern.jet.random.Uniform;

/**
 * Abstract generic class to implement a random number generator, initialized with the distribution
 * and relevant parameters. Could actually be concrete and be the implementation of all the 
 * distributions. Will return as many numbers as requested.
 * 
 * TODO add a seed property
 * TODO derive all distributions
 * 
 * @author Ferdinando Villa
 *
 */
public class RandomDataSource implements IDataSource, IInstanceImplementation {

	private static final String MINVALUE_PROPERTY = "source:minValue";
	private static final String MAXVALUE_PROPERTY = "source:maxValue";
	private static final String ISINTEGER_PROPERTY = "source:isInteger";
	
	double min = 0.0;
	double max = 1.0;
	boolean integer = false;
	
	/**
	 * The easiest thing in the world: we just spit out a number every time we are
	 * asked, ignoring all the parameters. We just ensure the conceptual manager
	 * wants numbers.
	 */
	public boolean handshake(IConceptualModel cm,
			IObservationContext observationContext,
			IObservationContext overallContext)
			throws ThinklabValidationException {
				
		// FIXME use class tree
		if (!cm.getStateType().is(KnowledgeManager.Number()))
			
			throw new ThinklabValidationException(
					"randomizer data source can only provide numbers: " +
					cm.getStateType() + 
					" requested");
		
		return true;
	}

	public Pair<IValue, IUncertainty> getInitialValue() {
		return null;
	}

	public Pair<IValue, IUncertainty> getValue(IObservationContextState context, IConcept concept, boolean useExtentIndex) throws ThinklabValidationException {

		NumberValue ret = null;
		
		if (integer) {
			long rv = Uniform.staticNextLongFromTo((long)min, (long)max);
			ret = new NumberValue(rv);
		} else {
			double rv = Uniform.staticNextDoubleFromTo(min, max);
			ret = new NumberValue(rv);
		}
		return new Pair<IValue, IUncertainty>(ret, null);

	}

	public void initialize(IInstance i) throws ThinklabException {
		
		/* read parameters, if any */
		IValue min = i.get(MINVALUE_PROPERTY);
		IValue max = i.get(MAXVALUE_PROPERTY);
		IValue isi = i.get(ISINTEGER_PROPERTY);
		
		if (min != null)
			this.min = min.asNumber().asDouble();
		if (max != null)
			this.max = max.asNumber().asDouble();
		if (isi != null)
			this.integer = BooleanValue.parseBoolean(isi.toString());
	}

	public void validate(IInstance i) throws ThinklabValidationException {
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
