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

/**
 * Not intended as merely an averager, but as the handler of an intensive physical
 * property (meaning the aggregation will be an average, but the distribution will 
 * leave the value unchanged).
 * 
 * @author Ferdinando
 *
 */
public class Average extends Sum {
	int n = 0;

	@Override
	public void add(Object value) {
		super.add(value);
		n++;
	}

	@Override
	public Object getAggregatedValue() {
		return (Double)(super.getAggregatedValue())/(double)n;
	}

	@Override
	public void reset() {
		super.reset();
		n = 0;
	}
	
	@Override
	public Object getDistributedValue(Object value, float ratio) {
		return ((Number)value).doubleValue();
	}
}