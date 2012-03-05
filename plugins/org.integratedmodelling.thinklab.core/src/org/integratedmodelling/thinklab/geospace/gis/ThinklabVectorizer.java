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
package org.integratedmodelling.thinklab.geospace.gis;

import java.util.ArrayList;

import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.Envelope2D;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.RasterCoverage;
import org.integratedmodelling.thinklab.geospace.coverage.VectorCoverage;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A Thinklab-aware vectorizer that produces a Thinklab coverage and optionally, an observation
 * structure with the recognizer features. Can eventually be used as a datasource or kbox.
 * 
 * @author Ferdinando Villa
 *
 */
public class ThinklabVectorizer  {
	
	public static VectorCoverage vectorize(AbstractRasterCoverage rCoverage, GridExtent extent)
			throws ThinklabException {

		ArrayList<Double> nans = new ArrayList<Double>();
		nans.add(Double.NaN);
		nans.add(0.0);
		
		// cross fingers
		Envelope2D bounds = new Envelope2D(extent.getEnvelope());
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
//		try {
////			 features = 
////				RasterToVectorProcess.process(rCoverage.getCoverage(), 0, bounds, nans, null);
//		} catch (ProcessException e) {
//			throw new ThinklabValidationException(e);
//		}
		
		return new VectorCoverage(features, extent.getCRS());
	}
	
	/*
	 * It's actually an "objectify" function. The features should have their state as an
	 * attribute.
	 * 
	 * expects fully categorized states.
	 * TODO different polygons for different states if necessary, according to metadata. Should
	 * refuse to vectorize anything continuous.
	 */
	public static VectorCoverage vectorize(IState state, GridExtent extent) throws ThinklabException {
		
		return vectorize(
				new RasterCoverage(
						state.getObservableClass().toString() + "_objects", 
						extent, 
						state.getDataAsDoubles()),
				extent);
	}

}
