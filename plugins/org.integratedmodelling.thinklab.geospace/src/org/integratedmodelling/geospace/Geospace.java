/**
 * GeospacePlugin.java
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
package org.integratedmodelling.geospace;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.referencing.CRS;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Node;

public class Geospace extends ThinklabPlugin  {

	private IConcept shapeType;
	private IConcept pointType;
	private IConcept lineStringType;
	private IConcept polygonType;
	private IConcept multiPointType;
	private IConcept multiLineStringType;
	private IConcept multiPolygonType;
	private IInstance areaLocationInstance;
	private IInstance rasterGridInstance;
	private IInstance spatialCoverageInstance;
	private IConcept arealLocationType;
	private IConcept rasterGridObservable;
	private IConcept subdividedSpaceObservable;
	private IConcept spaceObservable;
	private static String hasBoundingBoxPropertyID;
	private static String hasCentroidPropertyID;

	private static IConcept rasterSpaceType;
	static final public String PLUGIN_ID = "org.integratedmodelling.thinklab.geospace";
	
	public static final String X_RANGE_OFFSET = "geospace:hasXRangeOffset";
	public static final String X_RANGE_MAX = "geospace:hasXRangeMax";
	public static final String Y_RANGE_OFFSET = "geospace:hasYRangeOffset";
	public static final String Y_RANGE_MAX = "geospace:hasYRangeMax";
	public static final String LAT_LOWER_BOUND = "geospace:hasLatLowerBound";
	public static final String LON_LOWER_BOUND = "geospace:hasLonLowerBound";
	public static final String LAT_UPPER_BOUND = "geospace:hasLatUpperBound";
	public static final String LON_UPPER_BOUND = "geospace:hasLonUpperBound";
	public static final String CRS_CODE = "geospace:hasCoordinateReferenceSystem";
	public static final String COVERAGE_SOURCE_URL = "geospace:hasSourceURL";
	public static final String RASTER_CONCEPTUAL_MODEL = "geospace:RasterSpatialCoverage";
	public static final String POLYGON_COVERAGE_CONCEPTUAL_MODEL = "geospace:PolygonSpatialCoverage";
	public static final String RASTER_GRID_OBSERVABLE = "geospace:ContinuousRegularSpatialGrid";
	public static final String PREFERRED_CRS_PROPERTY = "geospace.preferred.crs";
	public static final String HAS_VALUE_ATTRIBUTE = "geospace:hasValueAttribute";
	public static final String HAS_SOURCE_LINK_ATTRIBUTE = "geospace:hasSourceLinkAttribute";
	public static final String HAS_TARGET_LINK_ATTRIBUTE = "geospace:hasTargetLinkAttribute";
	public static final String HAS_ATTRIBUTE_URL = "geospace:hasAttributeUrl";
	/*
	 * if not null, we have a preferred crs in the properties, and we solve
	 * all conflicts by translating to it. 
	 */
	CoordinateReferenceSystem preferredCRS = null;
	
	public static Geospace get() {
		return (Geospace) getPlugin(PLUGIN_ID);
	}
	
	@Override
	public void load(KnowledgeManager km) throws ThinklabPluginException {
		
		try {
			
			/*
			 * TODO put all these class names into global strings
			 */
			pointType = km.requireConcept("geospace:Point");
			lineStringType = km.requireConcept("geospace:LineString");
			polygonType = km.requireConcept("geospace:Polygon");
			multiPointType = km.requireConcept("geospace:MultiPoint");
			multiLineStringType = km.requireConcept("geospace:MultiLineString");
			multiPolygonType = km.requireConcept("geospace:MultiPolygon");
			areaLocationInstance = km.requireInstance("geospace:ArealLocationInstance");
			rasterGridInstance = km.requireInstance("geospace:RegularGridInstance");
			spatialCoverageInstance = km.requireInstance("geospace:SpatialCoverageInstance");
			arealLocationType = km.requireConcept("geospace:ArealLocation");
			rasterSpaceType = km.requireConcept(RASTER_CONCEPTUAL_MODEL);
			rasterGridObservable = km.requireConcept(RASTER_GRID_OBSERVABLE);
			subdividedSpaceObservable = km.requireConcept("geospace:SubdividedSpace");
			spaceObservable = km.requireConcept("geospace:SubdividedSpace");
			
			shapeType = km.requireConcept("geospace:SpatialRecord");
			
			hasBoundingBoxPropertyID = "geospace:hasBoundingBox";
			hasCentroidPropertyID = "geospace:hasCentroid";
						
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}
		
		
		/*
		 * create preferred CRS if one is specified. Highly adviceable to set one if hybrid data
		 * are used.
		 */
		if (getProperties().containsKey(PREFERRED_CRS_PROPERTY)) {
			try {
				preferredCRS = CRS.decode(getProperties().getProperty(PREFERRED_CRS_PROPERTY));
			} catch (Exception e) {
				throw new ThinklabPluginException(e);
			}
		}
	}

	public CoordinateReferenceSystem getPreferredCRS() {
		return preferredCRS;
	}
	
	/**
	 * The geotools implementation is unclear and doesn't seem to work, so 
	 * I put this function here and we'll only have to fix it in one place.
	 * 
	 * @param crs
	 * @return
	 * @throws ThinklabPluginException 
	 */
	public static String getCRSIdentifier(CoordinateReferenceSystem crs, boolean useDefault) throws ThinklabPluginException {
		
		if (crs != null) {
			try {
				return CRS.lookupIdentifier(crs, true);
			} catch (FactoryException e) {
				// FIXME when this thing works, just throw the exception
				return crs.getIdentifiers().iterator().next().toString();
				// throw new ThinklabValidationException(e);
			}
		}
		
		return useDefault ? get().getProperties().getProperty(PREFERRED_CRS_PROPERTY) : null;

	}
	

	public IConcept Point() {
		return pointType;
	}

	public IConcept LineString() {
		return lineStringType;
	}

	public IConcept Polygon() {
		return polygonType;
	}

	public IConcept MultiPoint() {
		return multiPointType;
	}

	public IConcept MultiLineString() {
		return multiLineStringType;
	}

	public IConcept MultiPolygon() {
		return multiPolygonType;
	}
	
	public IConcept Shape() {
		return shapeType;
	}

	public IInstance absoluteArealLocationInstance() {
		return areaLocationInstance;
	}
	
	public IInstance absoluteRasterGridInstance() {
		return rasterGridInstance;
	}

	public IConcept ArealLocation() {
		return arealLocationType;
	}

	public static String hasBoundingBox() {
		return hasBoundingBoxPropertyID;
	}

	public static String hasCentroid() {
		return hasCentroidPropertyID;
	}


	public boolean handlesFormat(String format) {
		// TODO add remaining support formats as necessary
		return 
			format.equals("shp") || 
			format.equals("tif") ||
			format.equals("tiff");
	}



	public Hints getGeotoolsHints() {
		// TODO we need to create appropriate hints at initialization, using the plugin's 
		// properties.
		return GeoTools.getDefaultHints();
	}

	public void notifyConfigurationNode(Node n) {
		// TODO Auto-generated method stub
		
	}

	public static IConcept RasterObservationSpace() {
		return rasterSpaceType;
	}

	public IConcept RasterGridObservable() {
		return rasterGridObservable;
	}

	public IConcept SubdividedSpaceObservable() {
		return subdividedSpaceObservable;
	}
	
	public IConcept SpaceObservable() {
		return spaceObservable;
	}


	public void setPreferredCRS(CoordinateReferenceSystem crs) {
		preferredCRS = crs;
	}

	public IInstance absoluteSpatialCoverageInstance() {
		return spatialCoverageInstance;
	}


	@Override
	protected void unload() throws ThinklabException {
		// TODO Auto-generated method stub
		
	}

}
