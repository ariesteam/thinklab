/**
 * InstantConceptualModel.java
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
package org.integratedmodelling.time.implementations.cmodels;

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtent;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.corescience.interfaces.data.IStateAccessor;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.extents.TemporalLocationExtent;
import org.integratedmodelling.time.literals.TimeValue;
import org.integratedmodelling.utils.LogicalConnector;


/**
 * Conceptual model for observations of a single, absolute location in time.
 * @author Ferdinando Villa
 *
 */
public class InstantConceptualModel implements ExtentConceptualModel {

	TimeValue time = null;
	
	public InstantConceptualModel(TimeValue value) {
		time = value;
	}

	public IConcept getStateType() {

		IConcept ret = null;
		try {
			ret=  KnowledgeManager.get().requireConcept(TimePlugin.DATETIME_TYPE_ID);
		} catch (ThinklabException e) {
		}
		return ret;
	}

	public IExtent getExtent() {
		return new TemporalLocationExtent(this, time);
	}
	
	
	/**
	 * Two locations in time can only be merged if they represent the same time. If one has
	 * been specified at a finer resolution than the other (e.g. seconds have been specified)
	 * the union operation should return the coarser and the intersection should return the
	 * finest. In all cases, the extent represents a single time "thing" and can't be further
	 * partitioned: the resolution is entirely related to the precision of representation.
	 * 
	 * TODO implement constrained logics
	 * 
	 * For now the only resolutions supported are year, month, day and millisecond.
	 */
	public IExtent mergeExtents(IExtent original, IExtent other, LogicalConnector connector, boolean isConstraint) throws ThinklabContextualizationException {
		
		if (!((original instanceof TemporalLocationExtent) && (other instanceof TemporalLocationExtent))) {
			throw new ThinklabContextualizationException("temporal extents are incompatible: heterogeneous time models used in observations");
		}
		
		TimeValue v1 = ((TemporalLocationExtent)original).getTimeValue();
		TimeValue v2 = ((TemporalLocationExtent)other).getTimeValue();
		TimeValue max = null;
		
		if (v1.isIdentical(v2)) {
			max = v1;
		} else if (v1.comparable(v2) || v2.comparable(v1)){
			max = connector.equals(LogicalConnector.UNION) ? v1.leastPrecise(v2) : v1.mostPrecise(v2);
		}
		
		if (max == null)
			throw new ThinklabContextualizationException("temporal extents " + v1 + " and " + v2 + " are incompatible");
	
		return new TemporalLocationExtent(this, max);
	}

	public void validate(IObservation observation) throws ThinklabValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IExtentMediator getExtentMediator(IExtent extent)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStateAccessor getStateAccessor(IConcept stateType, IObservationContext context) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handshake(IDataSource<?> dataSource,
			IObservationContext observationContext,
			IObservationContext overallContext) throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

	
}
