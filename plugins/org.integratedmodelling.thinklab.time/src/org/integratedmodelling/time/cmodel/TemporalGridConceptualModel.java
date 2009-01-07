/**
 * TemporalGridConceptualModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabTimePlugin.
 * 
 * ThinklabTimePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabTimePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.time.cmodel;

import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.IValueAggregator;
import org.integratedmodelling.corescience.interfaces.IValueMediator;
import org.integratedmodelling.corescience.observation.ConceptualModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.time.values.DurationValue;
import org.integratedmodelling.time.values.TimeValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.joda.time.DateTime;
import org.jscience.mathematics.number.Rational;

/**
 * 
 * 
 * @author Ferdinando Villa
 *
 */
public class TemporalGridConceptualModel extends ConceptualModel implements IExtentConceptualModel {

	DateTime start;
	DateTime end;
	long step;
	
	public TemporalGridConceptualModel(TimeValue start, TimeValue end, DurationValue step) {

		this.start = start.getTimeData();
		this.end = end.getTimeData();
		this.step = step.getMilliseconds();
	}

	public IExtent getExtent() throws ThinklabException {
		return new RegularTimeGridExtent(this, start,end, step);
	}

	
	/**
	 * TODO implement constrained logics
	 */
	public IExtent mergeExtents(IExtent original, IExtent other, LogicalConnector connector, boolean isConstraint)
			throws ThinklabException {

		RegularTimeGridExtent ori = (RegularTimeGridExtent) original;
		RegularTimeGridExtent oth = (RegularTimeGridExtent) other;
		
		return connector == 
			LogicalConnector.INTERSECTION ? 
					ori.intersection(oth) :
					ori.union(oth);
	}


	public IConcept getStateType() {
		return TimePlugin.DateTime();
	}

	public void validate(IObservation observation) throws ThinklabValidationException {

		long total = end.getMillis() - start.getMillis();

		if ((total % step) != 0) {
			throw new ThinklabValidationException(
					"invalid temporal grid: " +
					step + 
					" milliseconds interval does not divide the interval [" +
					start + 
					"," +
					end +
					") exactly");
		}
	
	}

	public IValueMediator getMediator(IConceptualModel conceptualModel, IObservationContext ctx) {
		// TODO do we need mediators? (take time from another model...)
		return null;
	}

	public IExtentMediator getExtentMediator(IExtent extent) throws ThinklabException {
		
		// TODO check that extent is proper type or catch/translate cast exception 
		
		RegularTimeGridExtent ori = (RegularTimeGridExtent) getExtent();
		RegularTimeGridExtent oth = (RegularTimeGridExtent) extent;
		
		if (ori.equals(oth)) {
			return null;
		}
		
		return new TemporalGridMediator(this, ori, oth);
	}

	public IValueAggregator getAggregator(IObservationContext ownContext, IObservationContext overallContext) {
		// extents should never aggregate along extents
		return null;
	}

	public IValue partition(IValue originalValue, Rational ratio) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// we just turn everything to null. We're not supposed to have a datasource.
		return null;
	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// we just turn everything to null. We're not supposed to have a datasource.
		return null;
	}

	public void setObjectName(String name) {
		// TODO Auto-generated method stub
		
	}
	public IValue validateData(byte b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid temporal grid state from a number");
	}

	public IValue validateData(int b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid temporal grid state from a number");
	}

	public IValue validateData(long b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid temporal grid state from a number");
	}

	public IValue validateData(float b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid temporal grid state from a number");
	}

	public IValue validateData(double b) throws ThinklabValidationException {
		throw new ThinklabValidationException("cannot create a valid temporal grid state from a number");
	}


}
