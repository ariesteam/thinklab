/**
 * TemporalLocationExtent.java
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
package org.integratedmodelling.time.extents;

import java.util.Collection;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.ITopologicallyComparable;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

/**
 * Extent class for a single temporal location.
 * TODO this should be defined from a period as well as a timevalue, because it is exactly
 * what it represents (according to resolution). In the present implementation resolutions
 * are fixed, but I need to make them arbitrary and connect TimeGrid with this using the
 * full extent value. 
 * 
 * @author Ferdinando Villa
 *
 */
public class TemporalLocationExtent implements IExtent {

	TimeValue value;
	
	public TemporalLocationExtent(TimeValue value) {
		this.value = value;
	}
	
	@Override
	public IValue getFullExtentValue() {
		return value;
	}
	
	@Override
	public Object getValue(int granule) {
		return value;
	}

	public int getValueCount() {
		return 1;
	}

	public TimeValue getTimeValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

	@Override
	public IDatasourceTransformation getDatasourceTransformation(
			IConcept mainObservable, IExtent extent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getExtent(int granule) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent and(IExtent extent) throws ThinklabException {
		
		if (! (extent instanceof TemporalLocationExtent)) {
			throw new ThinklabContextualizationException("temporal extents are incompatible: heterogeneous time models used in observations");
		}
		
		TimeValue v1 = getTimeValue();
		TimeValue v2 = ((TemporalLocationExtent)extent).getTimeValue();
		TimeValue max = null;
		
		if (v1.isIdentical(v2)) {
			max = v1;
		} else if (v1.comparable(v2) || v2.comparable(v1)){
			max = v1.mostPrecise(v2);
		}
		
		if (max == null)
			throw new ThinklabContextualizationException("temporal extents " + v1 + " and " + v2 + " are incompatible");
	
		return new TemporalLocationExtent(max);
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent constrain(IExtent extent) throws ThinklabException {
		// we don't have a grain, so this should be OK
		return and(extent);
	}

	@Override
	public String getSignature() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(ITopologicallyComparable o)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent or(IExtent myExtent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getAggregatedExtent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Pair<String, Integer>> getStateLocators(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkDomainDiscontinuity() throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IExtent intersection(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return extent;
	}

	@Override
	public IExtent force(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return extent;
	}

	@Override
	public IExtent union(IExtent extent) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(int index, Object o) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getRawData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double[] getDataAsDoubles() throws ThinklabValueConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDoubleValue(int index)
			throws ThinklabValueConversionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getObservableClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ObservationContext getObservationContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Restriction getConstraint(String operator) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

}
