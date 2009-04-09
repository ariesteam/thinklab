package org.integratedmodelling.geospace.gis;

import org.integratedmodelling.geospace.coverage.AbstractRasterCoverage;
import org.integratedmodelling.geospace.coverage.RasterCoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * A Thinklab-aware vectorizer that produces a Thinklab coverage and optionally, an observation
 * structure with the recognizer features. Can eventually be used as a datasource or kbox.
 * 
 * @author Ferdinando Villa
 *
 */
public class ThinklabVectorizer  {

	RasterCoverage coverage = null;
	
	public VectorCoverage vectorize(AbstractRasterCoverage rCoverage, GridExtent extent)
			throws ThinklabException {

//		FeatureCollection<SimpleFeatureType, SimpleFeature> ret = null;
//		
//		if (extent != null)
//			this.coverage = (RasterCoverage) rCoverage.requireMatch(extent, false);
//		else
//			this.coverage = rCoverage;
//		
//		int xc = coverage.getXCells();
//		int yc = coverage.getYCells();
//
//		double[][] dataMatrix = new double[xc][yc];
//		
//		/*
//		 * extract raster data into matrix
//		 */
//		for (int x = 0; x < xc; x++)
//			for (int y = 0; y < yc; y++)
//				dataMatrix[x][y] = coverage.getDataAsDouble(x, y);
//
//		Vectorizer vectorizer = 
//			new Vectorizer(dataMatrix, rCoverage.getBoundingBox(), rCoverage.getNoDataValue());
//
//		try {
//			ret = vectorizer.extractFeatures();
//		} catch (VectorizationException e) {
//			throw new ThinklabVectorizationException(e);
//		}
//		
//		return new VectorCoverage(
//				ret, 
//				rCoverage.getCoordinateReferenceSystem(), 
//				vectorizer.getValueAttributeName(), 
//				true);
		
		return null;
	}

}
