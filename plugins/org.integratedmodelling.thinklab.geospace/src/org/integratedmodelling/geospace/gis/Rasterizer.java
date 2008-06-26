package org.integratedmodelling.geospace.gis;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.feature.Feature;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;

public class Rasterizer {
	
	
//	   /**
//     * Fill polygon areas mapping on a raster
//     * 
//     * @param active the active region
//     * @param polygon the jts polygon geometry
//     * @param raster the empty raster data to be filled
//     * @param rasterToMap the map from which the values to fill raster are taken (if null, it is a
//     *        bitmap of 0 and 1)
//     * @param monitor
//     */
//    public static void rasterizePolygonGeometry( Window active, Geometry polygon,
//            RasterData raster, RasterData rasterToMap ) {
//        GeometryFactory gFactory = new GeometryFactory();
//        int rows = active.getRows();
//        int cols = active.getCols();
//
//        for( int i = 0; i < rows; i++ ) {
//
//        	// preset the matrix to novalue
//            for( int j = 0; j < cols; j++ ) {
//                if (rasterToMap != null) {
//                    raster.setValueAt(i, j, JGrassConstans.defaultNovalue);
//                } else {
//                    raster.setValueAt(i, j, 0.0);
//                }
//            }
//            // do scan line to fill the polygon
//            LineString line = gFactory.createLineString(new Coordinate[]{
//                    rowColToCenterCoordinates(active, i, 0),
//                    rowColToCenterCoordinates(active, i, cols - 1),});
//            if (polygon.intersects(line)) {
//                Geometry internalLines = polygon.intersection(line);
//                Coordinate[] coords = internalLines.getCoordinates();
//                for( int j = 0; j < coords.length; j = j + 2 ) {
//
//                    int[] startcol = coordinateToNearestRowCol(active, coords[j]);
//                    int[] endcol = coordinateToNearestRowCol(active, coords[j + 1]);
//
//                    if (startcol == null || endcol == null) {
//                        // vertex is outside of the region, ignore it
//                        continue;
//                    }
//                    /*
//                     * the part in between has to be filled
//                     */
//                    for( int k = startcol[0]; k <= endcol[0]; k++ ) {
//                        if (rasterToMap != null) {
//                            raster.setValueAt(i, k, rasterToMap.getValueAt(i, k));
//                        } else {
//                            raster.setValueAt(i, k, 1.0);
//                        }
//                    }
//                }
//            }
//        }
//    }
//    
//    /**
//     * Return the row and column of the active region matrix for a give coordinate
//     * 
//     * @param active the active region
//     * @param coord
//     * @return and int array containing row and col
//     */
//    public static int[] coordinateToNearestRowCol( Window active, double easting, double northing ) {
//
//        int[] rowcol = new int[2];
//        if (easting > active.getEast() || easting < active.getWest()
//                || northing > active.getNorth() || northing < active.getSouth()) {
//            return null;
//        }
//
//        double minx = active.getWest();
//        double ewres = active.getWEResolution();
//        for( int i = 0; i < active.getCols(); i++ ) {
//            minx = minx + ewres;
//            if (easting < minx) {
//                rowcol[0] = i;
//                break;
//            }
//        }
//
//        double miny = active.getSouth();
//        double nsres = active.getNSResolution();
//        for( int i = 0; i < active.getRows(); i++ ) {
//            miny = miny + nsres;
//            if (northing < miny) {
//                rowcol[1] = i;
//                break;
//            }
//        }
//
//        return rowcol;
//    }
//	
//	

//	/*
//	 * if there is anything in the passed row, bisecting horizontally and call self recursively until
//	 * the row is a cell or less; when that happens, do the actual raster conversion. If we end up 
//	 * overlapping one and one only polygon, just set all corresponding cells to its value.
//	 */
//	private void rasterizeRow(
//			Envelope box, FeatureCollection features, String valueId, float[][] data, 
//			float xSize, float ySize) {
//		
//		if (!checkContinueConditions(box, features, valueId, data, xSize, ySize))
//			return;
//		
//		/* 
//		 * if we get here, we must have a box which is larger than one cell horizontally:
//		 * halve envelope horizontally and call self recursively
//		 */
//		rasterizeRow(new Envelope(box.getMinX()+box.getWidth()/2, box.getMaxX(), box.getMinY(), box.getMaxY()), 
//				features, valueId, data, xSize, ySize);			
//		rasterizeRow(new Envelope(box.getMinX(), box.getMaxX()-box.getWidth()/2, box.getMinY(), box.getMaxY()), 
//				features, valueId, data, xSize, ySize);
//	}
//	
//	/*
//	 * if there is anything in the passed box, bisect vertically and call self recursively until
//	 * the box is a row or less; then return result of rasterizeRow. If we end up 
//	 * overlapping one and one only feature, just set all corresponding cells to its value.
//	 */
//	private void rasterizeBox(
//			Envelope box, FeatureCollection features, String valueId, float[][] data, 
//			float xSize, float ySize) {
//		
//		if (!checkContinueConditions(box, features, valueId, data, xSize, ySize))
//			return;
//		
//		/* if we get here, halve envelope horizontally and call self recursively if height is
//		 * > cell size, else 
//		 */
//		if (box.getHeight() > ySize) {
//			rasterizeBox(new Envelope(box.getMinX(), box.getMaxX(), box.getMinY()+box.getHeight()/2, box.getMaxY()), 
//					features, valueId, data, xSize, ySize);			
//			rasterizeBox(new Envelope(box.getMinX(), box.getMaxX(), box.getMinY(), box.getMaxY()-box.getHeight()/2), 
//					features, valueId, data, xSize, ySize);
//		} else {
//			rasterizeRow(box, features, valueId, data, xSize, ySize);
//		}
//		
//	}
//	
//	
//	private boolean checkContinueConditions(Envelope box, FeatureCollection features, 
//			String valueId, float[][] data, float xSize, float ySize) {
//		
//		/*
//		 * stop conditions in order of check (return false):
//		 * 
//		 * 0. box is smaller than one cell; recursion is over, no action;
//		 * 1. envelope does not intersect any feature - no action;
//		 * 2. we are entirely inside a feature - set all cells that overlap box;
//		 * 3. envelope is one cell in size - set the cell appropriately for the intersection;
//		 */
//		
//		/*
//		 * Stop condition 1: box is smaller than half one cell; recursion is over, no action;
//		 */
//		if (box.getWidth() < xSize/2 || box.getHeight() < ySize/2)
//			return false;
//		
//		/*
//	     * to assess the other conditions, we need to check the features that intersect the box
//		 * make a filter for the envelope, so we can use any index we may have
//		 */
//		FilterFactory2 bounds = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
////		String geometryPropertyName = "the_geom"; // schema.getDefaultProperty().getName().getLocalPart();
//// this snippet sucks
////		ReferencedEnvelope bbox = new ReferencedEnvelope( x1,y1, x2, y2, crs );
////		Filter filter =  bounds.bbox( geometryPropertyName ), ff.literal( bounds ) );
//		
//		/*
//		 * Stop condition 2: filter does not select any feature - nothing left to do;
//		 */
//		
//		/*
//		 * we do intercept at least one feature: check if we represent one cell only
//		 */
//		
//		/*
//		 * check if we lie entirely within the intersecting feature
//		 */
//
//		
//		
//		return false;
//	}

	private Envelope computeEnvelope(FeatureCollection features) {
		
		Envelope ret = null;
		
		// determine common envelope for all features.
		for (FeatureIterator f = features.features(); f.hasNext() ; ) {
			
			Feature ff = f.next();
			
			ReferencedEnvelope env = ff.getBounds();
			
			if (ret == null) {
				ret = env;
			} else {
				ret.expandToInclude(env);
			}
		}
		
		return ret;
			
	}
	
	/**
	 * Rasterize a feature collection.
	 * 
	 * @param features any feature collection we want to rasterize
	 * @param valueId the ID of a value attribute in the feature, which will become the raster data
	 * @param noDataValue what we want to fill the raster with where there are no features
	 * @param xCells the number of raster cells in a row of the desired raster
	 * @param yCells the number of raster cells in a column of the desired raster
	 * @param envelope the envelope we want to rasterize. Pass null to compute the envelope for
	 * 		  the whole feature collection.
	 * 
	 * @return A new grid coverage with the same CRS of the passed features and the passed parameters.
	 * 
	 */
	public GridCoverage2D rasterize(
			FeatureCollection features, 
			String valueId, 
			float noDataValue, 
			int xCells, int yCells,
			Envelope envelope) {
		
		float[][] data = new float[xCells][yCells];
		
		/* everything is nodata until proven otherwise */
		for (int i = 0; i < xCells; i++)
			for (int j = 0; j < yCells; j++)
				data[i][j] = noDataValue;
		
		if (envelope == null) {
			envelope = computeEnvelope(features);
		}
		
		float xSize = (float) (envelope.getWidth()/xCells);
		float ySize = (float) (envelope.getHeight()/yCells);
		
		
		CoordinateReferenceSystem crs = 
			features.getSchema().getDefaultGeometry().getCoordinateSystem();
		
		return new GridCoverageFactory().create("", data, new ReferencedEnvelope(envelope, crs));
	
	}

	private void rasterizeBox(Envelope envelope, FeatureCollection features,
			String valueId, float[][] data, float xCellSize, float yCellSize) {

		for (FeatureIterator it = features.features(); it.hasNext(); ) {
			
		}
		
	}
	

}
