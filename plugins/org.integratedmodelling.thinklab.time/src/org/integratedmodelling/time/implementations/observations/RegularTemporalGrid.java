/**
 * RegularTemporalGrid.java
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
package org.integratedmodelling.time.implementations.observations;

import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.units.Unit;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.time.literals.DurationValue;
import org.integratedmodelling.time.literals.TimeValue;

/**
 * @author UVM Affiliate
 *
 */
@InstanceImplementation(concept="time:RegularTemporalGrid")
public class RegularTemporalGrid extends Observation implements Topology {

	TimeValue start = null;
	TimeValue end = null;
	DurationValue step = null;
	
	RegularTimeGridExtent extent = null;

	@Override
	public void initialize(IInstance i) throws ThinklabException {

		/* complete definition with observable. */
		i.addObjectRelationship(CoreScience.HAS_OBSERVABLE, TimePlugin.continuousTimeInstance());

		/* 
		 * recover values for three properties defining the grid. If one is not there, we assume none is
		 * there.
		 */
		if (i.get(TimePlugin.STARTS_AT_PROPERTY_ID) != null) {
			start = (TimeValue) i.get(TimePlugin.STARTS_AT_PROPERTY_ID);
			end   = (TimeValue) i.get(TimePlugin.ENDS_AT_PROPERTY_ID);
			step  = (DurationValue) i.get(TimePlugin.STEP_SIZE_PROPERTY_ID);
		}
		
		super.initialize(i);
		
		this.extent = new RegularTimeGridExtent(
				start.getTimeData(), end.getTimeData(), step.getMilliseconds());
	}

	@Override
	public Polylist conceptualize() throws ThinklabException {
		
		return Polylist.list(
				TimePlugin.TEMPORALGRID_TYPE_ID,
				Polylist.list(TimePlugin.STARTS_AT_PROPERTY_ID, start.toString()),
				Polylist.list(TimePlugin.ENDS_AT_PROPERTY_ID, end.toString()),
				Polylist.list(TimePlugin.STEP_SIZE_PROPERTY_ID, step));
	}

	@Override
	public IExtent getExtent() throws ThinklabException {
		return extent;
	}

	@Override
	public void checkUnitConformance(IConcept concept, Unit unit)
			throws ThinklabValidationException {
		
		if (!unit.isRate())
			throw new ThinklabValidationException(
					"concept " + 
					concept + 
					" is observed in time but unit " + 
					unit + 
					" does not specify a rate");
	}

}
