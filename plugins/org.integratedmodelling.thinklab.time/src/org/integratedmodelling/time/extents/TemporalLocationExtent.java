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

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.internal.IDatasourceTransformation;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.Polylist;

/**
 * Extent class for a single temporal location.
 * @author Ferdinando Villa
 *
 */
public class TemporalLocationExtent implements IExtent {

	TimeValue value;
	
	public TemporalLocationExtent(TimeValue value) {
		this.value = value;
	}
	
	public IValue getFullExtentValue() {
		return value;
	}

	public IValue getState(int granule) {
		return value;
	}

	public int getTotalGranularity() {
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
	public IExtent merge(IExtent extent) throws ThinklabException {
		
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
		return merge(extent);
	}

}
