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
package org.integratedmodelling.multidimensional;

import org.integratedmodelling.exceptions.ThinklabRuntimeException;

public class Sum implements IAggregator {

	double acc = 0.0;
	
	@Override
	public void add(Object value) {
		if (value instanceof Double) {
			acc += (Double)value;
		} else if (value instanceof Float) {
			acc += (double)(Float)value;
		} else if (value instanceof Long) {
			acc += (double)(Long)value;
		} else if (value instanceof Integer) {
			acc += (double)(Integer)value;
		} else if (value instanceof Byte) {
			acc += (double)(Byte)value;
		} else {
			throw new ThinklabRuntimeException("cannot accumulate non-numeric values.");
		}
	}

	@Override
	public Object getAggregatedValue() {
		return new Double(acc);
	}

	@Override
	public void reset() {
		acc = 0.0;
	}

	@Override
	public Object getDistributedValue(Object value, float ratio) {
		return ((Number)value).doubleValue()/ratio;
	}
	
}