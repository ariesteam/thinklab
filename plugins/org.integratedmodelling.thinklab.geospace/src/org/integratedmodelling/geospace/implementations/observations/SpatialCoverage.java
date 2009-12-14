/**
 * RasterGrid.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.observations;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ShapeExtent;
import org.integratedmodelling.thinklab.constraint.Restriction;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IRelationship;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An observation class that represents a view of space subdivided into irregular,
 * discontinuous areal extents. 
 * @author Ferdinando Villa
 */
@InstanceImplementation(concept="geospace:ArealFeatureSet")
public class SpatialCoverage extends Observation implements Topology {

	double latLB, lonLB, latUB, lonUB;
	CoordinateReferenceSystem crs;
	private ShapeExtent extent;
	
	public void initialize(IInstance i) throws ThinklabException {

		/*
		 * link the obvious observable - do it now, so that super.initialize() finds it.
		 */
		i.addObjectRelationship(
					CoreScience.HAS_OBSERVABLE, 
					Geospace.get().absoluteSpatialCoverageInstance());
		
		String crsId = null;
				
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			/* for speed */
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(Geospace.LAT_LOWER_BOUND)) {
					latLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_LOWER_BOUND)) {
					lonLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LAT_UPPER_BOUND)) {
					latUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.LON_UPPER_BOUND)) {
					lonUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(Geospace.CRS_CODE)) {
					crsId = r.getValue().toString();
				} 			
			}
		}

		if (crsId != null)
			crs = Geospace.getCRSFromID(crsId);
		
		super.initialize(i);

		this.extent = new ShapeExtent(this.getBoundary(), crs);
	}
	
	ReferencedEnvelope getBoundary() {
		return new ReferencedEnvelope(lonLB, lonUB, latLB, latUB, crs);
	}

	@Override
	public Restriction getConstraint(String operator) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IExtent getExtent() throws ThinklabException {
		return extent;
	}
}
