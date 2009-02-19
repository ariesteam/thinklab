/**
 * TemporalGridMediator.java
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

import org.integratedmodelling.corescience.exceptions.ThinklabMediationException;
import org.integratedmodelling.corescience.interfaces.cmodel.IExtentMediator;
import org.integratedmodelling.corescience.interfaces.cmodel.ExtentConceptualModel.Coverage;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.time.extents.RegularTimeGridExtent;
import org.integratedmodelling.time.literals.PeriodValue;
import org.jscience.mathematics.number.Rational;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

/**
 * Accumulates temporal extents to fit another. 
 * 
 * @author Ferdinando Villa
 *
 */
public class TemporalGridMediator implements IExtentMediator {

	Geometry current = null;
	LineString currentToCover = null;
	long currentlength = 0l;
	Coordinate[] c = new Coordinate[2];
	static private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(0.0));
	long stepsize = 0l;
	
	public TemporalGridMediator  (
			TemporalGridConceptualModel temporalGridConceptualModel,
			RegularTimeGridExtent ownExtent, RegularTimeGridExtent targetExtent) throws ThinklabException {

		// check that extents are compatible
		
		stepsize = ownExtent.getGranuleSize();
		
		// TODO Auto-generated constructor stub
		
	}

	public void addForeignExtent(IValue extent) throws ThinklabMediationException {
		
		PeriodValue pext = (PeriodValue)extent;
		c[0] = new Coordinate(pext.getStart(), 0);
		c[1] = new Coordinate(pext.getEnd(), 0);
		LineString crt = geometryFactory.createLineString(c);
		
		if (currentToCover == null) {
			/* 
			 * TODO 
			 * define the subextent of the original extent that is to be covered by this 
			 * foreign extent. Throw an exception if the extent can
			 */
		}
		
		if (!currentToCover.overlaps(crt))
			throw new ThinklabMediationException("extent mediation ");
		
		if (current == null) {
			current = crt;
		} else {
			current = current.union(crt);
		}
		
		currentlength = (long) crt.getLength();
	}

	public Coverage checkCoverage() {
		
		// TODO Auto-generated method stub
		// if (currentlength >= )
		
		return null;
	}

	public Rational getCurrentCoverage() {
		
		// TODO Auto-generated method stub
		return null;
	}

	public void resetForeignExtents() {
		
		Geometry diff = current.difference(currentToCover);
		
		if (diff.isEmpty()) {
			currentToCover = null;
			current = null;			
		} else {
			/*
			 * TODO 
			 * if the difference between the current and the one covered is not empty, use that to
			 * start the next coverage.
			 */
		}

	}

}