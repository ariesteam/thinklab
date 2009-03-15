/**
 * SubdividedCoverageConceptualModel.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 19, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @date      Feb 19, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.cmodels;

import java.util.Properties;

import org.geotools.referencing.CRS;
import org.integratedmodelling.corescience.interfaces.context.IObservationContextState;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.jscience.mathematics.number.Rational;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * The spatial conceptual model that handles both raster and vector coverages of
 * an observation extent. It represents an observation domain that recognizes
 * one or more portions of non-overlapping space.
 * 
 * @author Ferdinando
 * 
 */
public abstract class SubdividedCoverageConceptualModel extends SpatialConceptualModel {

	private String name;

	public IConcept getStateType() {
		return Geospace.get().Polygon();
	}

	public IValue partition(IValue originalValue, Rational ratio) {
		// TODO Auto-generated method stub
		return null;
	}

	public void validate(IObservation observation)
			throws ThinklabValidationException {
		
		/* store observation and its coverage */

	}

	public IValue validateLiteral(String value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public IValue validateValue(IValue value,
			IObservationContextState contextState)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getObjectName() {
		return name;
	}

	public void setObjectName(String name) {
		this.name = name;
	}

	public void initialize(IInstance i) throws ThinklabException {

		String crsID = null;
		
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			/* for speed */
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.LAT_LOWER_BOUND)) {
					latLowerBound = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_LOWER_BOUND)) {
					lonLowerBound = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LAT_UPPER_BOUND)) {
					latUpperBound = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_UPPER_BOUND)) {
					lonUpperBound = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.CRS_CODE)) {
					crsID = r.getValue().toString();
				}		
			}
		}
		
		setBoundary(latUpperBound, latLowerBound, lonUpperBound, lonLowerBound);
		setCRS(getCRSFromID(crsID));
		
		/*
		 * we have no datasource, but we need to have the coverage we're working with. 
		 */
	}

	public static CoordinateReferenceSystem getCRSFromID(String crsId) throws ThinklabValidationException {
		
		CoordinateReferenceSystem ret = null;
		
		try {
			ret = CRS.decode(crsId);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return ret;
		
	}

	public void validate(IInstance i) throws ThinklabValidationException {
		// TODO Auto-generated method stub

	}

}
