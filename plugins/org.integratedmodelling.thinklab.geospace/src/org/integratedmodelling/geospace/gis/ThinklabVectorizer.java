package org.integratedmodelling.geospace.gis;

import java.util.ArrayList;

import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.Envelope2D;
import org.geotools.process.ProcessException;
import org.geotools.process.raster.RasterToVectorProcess;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
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
		Envelope2D bounds = new Envelope2D(extent.getDefaultEnvelope());
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		try {
			 features = 
				RasterToVectorProcess.process(rCoverage.getCoverage(), 0, bounds, nans, null);
		} catch (ProcessException e) {
			throw new ThinklabValidationException(e);
		}
		
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
