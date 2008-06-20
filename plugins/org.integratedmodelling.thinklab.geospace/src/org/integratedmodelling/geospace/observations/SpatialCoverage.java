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
package org.integratedmodelling.geospace.observations;

import org.integratedmodelling.corescience.CoreSciencePlugin;
import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.observation.Observation;
import org.integratedmodelling.geospace.GeospacePlugin;
import org.integratedmodelling.geospace.cmodel.FeatureCoverageModel;
import org.integratedmodelling.geospace.cmodel.RegularRasterModel;
import org.integratedmodelling.geospace.cmodel.SubdividedCoverageConceptualModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An observation class that represents a gridded view of space, perfect to serve
 * as the spatial extent observation of another observation. Will create all its
 * conceptual model etc. from the OWL specs, so it's typically all you need to
 * define to provide a raster spatial context to an observation.
 * 
 * @author Ferdinando Villa
 */
public class SpatialCoverage extends Observation {

	double latLB, lonLB, latUB, lonUB;
	CoordinateReferenceSystem crs;
	
	public void initialize(IInstance i) throws ThinklabException {

		/*
		 * link the obvious observable - do it now, so that super.initialize() finds it.
		 */
		i.addObjectRelationship(
					CoreSciencePlugin.HAS_OBSERVABLE, 
					GeospacePlugin.absoluteSpatialCoverageInstance());
		
		String crsId = null;
				
		// read requested parameters from properties
		for (IRelationship r : i.getRelationships()) {
			
			/* for speed */
			if (r.isLiteral()) {
				
				if (r.getProperty().equals(GeospacePlugin.LAT_LOWER_BOUND)) {
					latLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(GeospacePlugin.LON_LOWER_BOUND)) {
					lonLB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(GeospacePlugin.LAT_UPPER_BOUND)) {
					latUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(GeospacePlugin.LON_UPPER_BOUND)) {
					lonUB = r.getValue().asNumber().asDouble();
				} else if (r.getProperty().equals(GeospacePlugin.CRS_CODE)) {
					crsId = r.getValue().toString();
				} 			
			}
		}

		if (crsId != null)
			crs = SubdividedCoverageConceptualModel.getCRSFromID(crsId);
		
		super.initialize(i);

	}
	
	@Override
	public IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		
		return new FeatureCoverageModel(latLB, latUB, lonLB, lonUB, crs);
	}
}
