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

import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.implementations.cmodels.InstantConceptualModel;
import org.integratedmodelling.time.literals.TimeValue;

/**
 * Extent class for a single temporal location.
 * @author Ferdinando Villa
 *
 */
public class TemporalLocationExtent implements IExtent {

	InstantConceptualModel cModel = null;
	TimeValue value;
	
	public TemporalLocationExtent(InstantConceptualModel cm, TimeValue value) {
		cModel = cm;
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

	public ExtentConceptualModel getConceptualModel() {
		return cModel;
	}

	public TimeValue getTimeValue() {
		return value;
	}

	public String toString() {
		return value.toString();
	}

	@Override
	public IExtent getExtent(int granule) {
		return this;
	}
}
