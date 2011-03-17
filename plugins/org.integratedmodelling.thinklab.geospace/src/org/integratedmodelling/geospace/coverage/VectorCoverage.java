/**
 * VectorCoverage.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Feb 26, 2008
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
 * @date      Feb 26, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.coverage;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.DelegateFeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.extents.ShapeExtent;
import org.integratedmodelling.geospace.feature.AttributeTable;
import org.integratedmodelling.geospace.feature.LazyShapeCollection;
import org.integratedmodelling.geospace.gis.ThinklabRasterizer;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabUnimplementedFeatureException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class VectorCoverage implements ICoverage {

	private FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
	CoordinateReferenceSystem crs = null;
	// this is never null and may come from the source or be passed by the user
	private BoundingBox boundingBox = null;
	private String layerName = null;
	private String valueField = null;
	private String sourceUrl = null;
	private IConcept valueType = null;
	private String  filterExpression = null;
	
	/*
	 * if this is > -1, we have an independent attribute table associated with the feature collection. 
	 * If so, valueField is the feature attribute we use to link to it, and the handle points to a 
	 * translation table for the attribute.
	 */
	int attributeHandle = -1;
	private FeatureSource<SimpleFeatureType, SimpleFeature> source;
	private String valueDefault;
	
	/*
	 * fill value between shapes when rasterizing
	 * 
	 * ACHTUNG
	 * this one makes a big difference because if not filled with 0s, the areas not
	 * covered by shapes will be nodata, and whatever other layer set as 
	 * lower priority will show through. 0.0 would 
	 * be the right value for the areas inside the features' convex hull. 
	 * Unfortunately none of the methods to
	 * compute the convex hull of the shapes works acceptably unless each shape
	 * is clipped to the region, so for now NaN is the compromise that keeps
	 * stacking layers working, with the possibility of error.
	 * 
	 */
	private float fillValue = Float.NaN;

	
	public VectorCoverage(URL url, String valueField, boolean validate) throws ThinklabException {
		
		sourceUrl  = url.toString();
		
		ShapefileDataStore sds;
		try {
			sds = new ShapefileDataStore(url);
			layerName = MiscUtilities.getNameFromURL(url.toString());
			features = sds.getFeatureSource(sds.getTypeNames()[0]).getFeatures();
			boundingBox = sds.getFeatureSource(sds.getTypeNames()[0]).getBounds();
			
			// cross fingers
			crs = features.getSchema().getCoordinateReferenceSystem();
			
			if (validate)
				validateFeatures();

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
	}

	/**
	 * Check all features and make sure they're OK for a vector coverage. Means that they should
	 * be all polygons and have the identified value field.
	 */
	private void validateFeatures() {
		// TODO
	}

	public LazyShapeCollection getShapes() throws ThinklabException {
		return new LazyShapeCollection(getFeatureIterator(null, (String[]) null), crs);
	}
	
	public VectorCoverage(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features,
			CoordinateReferenceSystem crs, 
			String valueField, 
			String valueType, // concept - may be null
			String valueDefault, // if the attribute is empty or null, use this instead - may be null
			ReferencedEnvelope envelope,
			FeatureSource<SimpleFeatureType, SimpleFeature> source, 
			String filterExpression, // CQL rule or null
			boolean validate) throws ThinklabException {
		
		this.features = features;
		this.crs = crs;
		this.valueField = valueField;
		this.source = source;
		this.valueType = valueType == null ? null : KnowledgeManager.get().requireConcept(valueType);
		this.valueDefault = valueDefault;
		this.filterExpression = filterExpression;
		
		if (validate)
			validateFeatures();
		
		if (envelope == null)
			computeEnvelope();
		else {
			boundingBox = envelope;
		}
	}
	
	/**
	 * This one takes the features from one source and the values from another, typically a CVS file or
	 * database query, and links the features to the values through the value of an attribute of the feature.
	 * 
	 * E.g., connect a country shapefile with a spreadsheet of country population statistics through the country
	 * ISO code.
	 * 
	 * If created through the CoverageFactory, we ensure that there is no duplication of the coverages and tables
	 * as long as the corresponding URLs stay the same.
	 * 
	 * @param features
	 * @param crs
	 * @param valueField
	 * @param attributes
	 * @param validate
	 * @throws ThinklabException 
	 */
	public VectorCoverage(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features,
			CoordinateReferenceSystem crs, 
			AttributeTable attributes,
			String linkField,
			String linkTargetField,
			String valueField,
			String valueType, // concept - may be null
			String valueDefault, // if attribute is null or empty, use this - may be null
			String filterExpression, // CQL rule or null
			boolean validate) throws ThinklabException {
		
		this.features = features;
		this.crs = crs;
		this.valueField = linkField;
		this.valueType = valueType == null ? null : KnowledgeManager.get().requireConcept(valueType);
		this.valueDefault = valueDefault;
		this.attributeHandle = attributes == null ? -1 : attributes.index(linkTargetField, valueField);
		this.filterExpression = filterExpression;
		
		if (validate)
			validateFeatures();
		
		computeEnvelope();
	}
	
	public VectorCoverage(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features,
			CoordinateReferenceSystem crs) throws ThinklabException {
		this(features, crs, null, null, null, null, null, null, null, false);
	}

	/**
	 * 
	 * @param envelope
	 * @param attributes If we want the features to retain an attribute other than the
	 *        geometry, pass it here
	 * @return
	 * @throws ThinklabException
	 */
	public FeatureIterator<SimpleFeature> getFeatureIterator(ReferencedEnvelope envelope, String ... attributes) throws ThinklabException {

		ClassLoader clsl = null;
		FeatureIterator<SimpleFeature> ret = null;
		
		try {

			// SPI be damned
			clsl = Geospace.get().swapClassloader();			
			
			if (envelope == null) {
				ret = new DelegateFeatureIterator<SimpleFeature>(getFeatures(), getFeatures().iterator());
			} else {
				
				/*
				 * TODO/FIXME/CHECK
				 * if WGS84, WFS will want the bounding box in lat/lon order. How do we ensure which box we need to use?
				 * Datasource properties? For Geoserver, this is OK. For anything else, who knows. 
				 * OR we need to set hints in the filter - more likely? FIXME FIXME FIXME
				 */
				if (Geospace.getCRSIdentifier(envelope.getCoordinateReferenceSystem(), true).equals("EPSG:4326")) {
					envelope = 
						new ReferencedEnvelope(
								envelope.getMinY(), envelope.getMaxY(), 
								envelope.getMinX(), envelope.getMaxX(), 
								envelope.getCoordinateReferenceSystem());
				}

				/*
				 * query
				 */
				FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
				String geomName = source.getSchema().getGeometryDescriptor().getLocalName();
				String typeName = source.getSchema().getTypeName();
				Filter filter = ff.bbox(ff.property(geomName), envelope);
				
				if (filterExpression != null) {
					try {
						Filter cql = CQL.toFilter(filterExpression);
						filter = ff.and(filter, cql);
					} catch (CQLException e) {
						throw new ThinklabValidationException(e);
					}
				}
				
//		    	System.out.println("upper: " + envelope.getUpperCorner());
//		    	System.out.println("lower: " + envelope.getLowerCorner());
				
		    	/*
		    	 * attributes to put in the query
		    	 */
		    	String[] attnames = new String[] { geomName };
		    	int ii = 0;
		    	for (String ss : attributes) {
		    		if (ss != null)
		    			ii++;
		    	}
	    		attnames = new String[ii + 1];
	    		attnames[0] = geomName;
		    	if (attributes != null) {
		    		int i = 1;
		    		for (String a : attributes) {
		    			if (a != null)
		    				attnames[i++] = a;
		    		}
		    	}
		    	
				DefaultQuery query = new DefaultQuery(typeName, filter, attnames);
				// TODO check: we want the results to match the CRS that the query is expressed in
				query.setCoordinateSystem(envelope.getCoordinateReferenceSystem());
				query.setCoordinateSystemReproject(envelope.getCoordinateReferenceSystem());
				FeatureCollection<SimpleFeatureType, SimpleFeature> feat;
				
				try {
					feat = source.getFeatures(query);
				} catch (IOException e) {
					throw new ThinklabIOException(e);
				}
				
				ret = new DelegateFeatureIterator<SimpleFeature>(feat, feat.iterator());
			}

		} finally {
			Geospace.get().resetClassLoader(clsl);
		}
	
		return ret;
	}

	private void computeEnvelope() throws ThinklabException {
		
		FeatureIterator<SimpleFeature> f = null;
		try {
			
			for ( f = getFeatureIterator(null, (String[]) null); f.hasNext() ; ) {
			
				SimpleFeature ff = f.next();
				BoundingBox env = ff.getBounds();
			
			if (boundingBox == null) {
				boundingBox = env;
			} else {
				boundingBox.include(env);
			}
		  }
		} finally {
			f.close();
		}
	}

	public String getCoordinateReferenceSystemCode()
			throws ThinklabValidationException {
		try {
			return CRS.lookupIdentifier(crs, true);
		} catch (FactoryException e) {
			// FIXME when this thing works, just throw the exception
			return crs.getIdentifiers().iterator().next().toString();
			// throw new ThinklabValidationException(e);
		}
	}

	public BoundingBox getBoundingBox() {
		return boundingBox;
	}
	
	public double getLatLowerBound() {
		return boundingBox.getMinY();
	}

	public double getLatUpperBound() {
		return boundingBox.getMaxY();
	}

	public double getLonLowerBound() {
		return boundingBox.getMinX();
	}

	public double getLonUpperBound() {
		return boundingBox.getMaxX();
	}
	
	public String getLayerName() {
		return layerName == null ? "no name" : layerName;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public IValue getSubdivisionValue(int subdivisionOrder, ArealExtent extent)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadData() {
		// nothing to do
	}

	public VectorCoverage requireCRS(CoordinateReferenceSystem crs)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public ICoverage requireMatch(ArealExtent arealExtent,
			boolean allowClassChange) throws ThinklabException {

		ICoverage ret = null;
		
		if (arealExtent instanceof GridExtent && allowClassChange) {
			
				/* 
				 * WOW - we want a raster - rasterize it 
				 * TODO implement some sort of caching mechanism
				 */
				System.out.println("rasterizing vector coverage " + layerName + " over " + arealExtent + " ...");
				ret = convertToRaster((GridExtent) arealExtent);
				
				/*
				 * set the grid extent's lineage so that our rasterized objects can be reconstructed.
				 */
				((GridExtent)arealExtent).setAncestor(new ShapeExtent(arealExtent, this));

		}  else {
		
			/*
			 * TODO reproject and subset if the passed extent is different from ours
			 */
			ret = this;
		}
		
		return ret;
	}

	/**
	 * Create a new raster coverage for the passed extent and return it. It even sounds 
	 * easy.
	 * 
	 * TODO this sets the raster to 0 before burning the shapes in, except where
	 * the bounding box is not covered. It may need to be changed to reflect some
	 * parameters, specifically set to NaN if requested.
	 * 
	 * @param arealExtent
	 * @return
	 * @throws ThinklabException
	 */
	public ICoverage convertToRaster(GridExtent arealExtent) throws ThinklabException {
		return ThinklabRasterizer.rasterize(this, valueField, this.fillValue , arealExtent, valueType, valueDefault);
	}

	public void show() {

//		JFrame frame = new JFrame(getLayerName());
//		
//        // create the style
//        StyleBuilder sb = new StyleBuilder();
//        Style simple = sb.createStyle(sb.createPolygonSymbolizer(
//                    Color.LIGHT_GRAY, Color.BLACK, 1));
//        
//        JMapPane mapPane = new JMapPane();
//        mapPane.setRenderer(new ShapefileRenderer());
//        
//        MapLayer layer = new DefaultMapLayer(features, simple);
//        layer.setTitle(getLayerName());
// 
//        MapContext context = new DefaultMapContext(crs == null ? DefaultGeographicCRS.WGS84 : crs);
//        context.addLayer(layer);
//        
//        mapPane.setContext(context);
//        mapPane.setMapArea(layer.getBounds());
//        
//        frame.setContentPane(mapPane);
//        frame.setLocationRelativeTo(null);
//        frame.setSize(640,480);
//        frame.setVisible(true);
	}

	public void setSourceUrl(String s) {
		sourceUrl = s;
	}
	
	public void writeImage(File outfile, String format)
			throws ThinklabIOException {
		// TODO Auto-generated method stub
		
	}
	
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return crs;
	}

	public FeatureCollection<SimpleFeatureType, SimpleFeature> getFeatures() {
		return features;
	}

	public void write(File f) throws ThinklabException {
		
		if ( ! (f.toString().endsWith(".shp"))) {
			throw new ThinklabUnimplementedFeatureException(
					"vector coverage: writing: only shapefile format is supported for now");
		}
		
		
//		DefaultTransaction transaction = new DefaultTransaction("Example1");
//		FeatureStore<SimpleFeatureType, SimpleFeature> store = 
//			(FeatureStore<SimpleFeatureType, SimpleFeature>) dataStore.getFeatureSource( typeName );
//		store.setTransaction( transaction );
//
//		SimpleFeatureType featureType = store.getFeatureType();
//
//		try {
//		    store.addFeatures( features );
//		    transaction.commit(); // actually writes out the features in one go
//		}
//		catch( Exception eek){
//		    transaction.rollback();
//		}
		
	}

	public AttributeDescriptor getAttributeDescriptor(String valueId) {
		return source.getSchema().getDescriptor(valueId);
	}

	@Override
	public double[] getNodataValue() {
		// TODO see what applies here. For now no support for nodata values.
		return null;
	}

	@Override
	public void setName(String covId) {
		layerName = covId;
	}

	public String[] getAttributeNames() {

		String[] ret = new String[source.getSchema().getAttributeCount()];
		int i = 0;
		
		for (AttributeDescriptor ad : source.getSchema().getAttributeDescriptors()) {
			ret[i++] = ad.getLocalName();
		}
		
		return ret;
	}

}
