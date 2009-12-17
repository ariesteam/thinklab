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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;
import org.integratedmodelling.utils.image.ColorMap;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
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
	private IConcept gridClassifierType;
	
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
	public static final String RASTER_GRID = "geospace:RasterGrid";
	public static final String AREAL_LOCATION = "geospace:RasterGrid";
	public static final String GRID_CLASSIFIER = "geospace:GridClassifier";
	public static final String CLASSIFIED_GRID = "geospace:ClassifiedGrid";
	public static final String GRID_CLASSIFICATION_MODEL = "geospace:GridClassification";
		
	// the projection to use if we need meters
	public static final String EPSG_PROJECTION_METERS  = "EPSG:3005";
	public static final String EPSG_PROJECTION_DEFAULT = "EPSG:4326";
	public static final String EPSG_PROJECTION_GOOGLE  = "EPSG:3857";	

	/*
	 * if not null, we have a preferred crs in the properties, and we solve
	 * all conflicts by translating to it. 
	 */
	CoordinateReferenceSystem preferredCRS = null;
	CoordinateReferenceSystem metersCRS = null;
	CoordinateReferenceSystem googleCRS = null;	
	CoordinateReferenceSystem geoCRSstraight = null;
	
	/*
	 * we maintain a collection of gazetteers that plugins can install. The lookupFeature() function
	 * will search all of them.
	 */
	HashMap<String, IGazetteer> gazetteers = new HashMap<String, IGazetteer>();
	private CoordinateReferenceSystem defaultCRS = null;
	
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
			spaceObservable = km.requireConcept("geospace:SpaceObservable");
			gridClassifierType = km.requireConcept(GRID_CLASSIFIER);
			
			shapeType = km.requireConcept("geospace:SpatialRecord");
			
			hasBoundingBoxPropertyID = "geospace:hasBoundingBox";
			hasCentroidPropertyID = "geospace:hasCentroid";
						
		} catch (ThinklabException e) {
			throw new ThinklabPluginException(e);
		}

		try {
			
			// ColorMap.rainbow(256).getColorbar(16, new File("cbar.png"));
			
			metersCRS = CRS.decode(EPSG_PROJECTION_METERS);
			defaultCRS = CRS.decode(EPSG_PROJECTION_DEFAULT);
			googleCRS = CRS.decode(EPSG_PROJECTION_GOOGLE);
			
			CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
			geoCRSstraight = factory.createCoordinateReferenceSystem("EPSG:4326");
			
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		} 
		
		/*
		 * create preferred CRS if one is specified. Highly advisable to set one if hybrid data
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
	
	public CoordinateReferenceSystem getGoogleCRS() {
		return googleCRS;
	}
	
	/**
	 * Return a lat/lon CRS that is guaranteed to transform data to coordinates that have longitude on
	 * the X axis.
	 *  
	 * @return
	 */
	public CoordinateReferenceSystem getStraightGeoCRS() {
		return geoCRSstraight;
	}
	
	/**
	 * The geotools implementation is unclear and doesn't seem to work, so 
	 * I put this function here and we'll only have to fix it in one place.
	 * 
	 * @param crs
	 * @return
	 * @throws ThinklabPluginException 
	 */
	public static String getCRSIdentifier(CoordinateReferenceSystem crs, boolean useDefault) throws ThinklabException {
		
		if (crs != null) {
			try {
				return CRS.lookupIdentifier(crs, true);
			} catch (FactoryException e) {
				throw new ThinklabValidationException(e);
			}
		}
		
		return useDefault ? get().getProperties().getProperty(PREFERRED_CRS_PROPERTY) : null;

	}
	
	public CoordinateReferenceSystem getMetersCRS() {
		return metersCRS;
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

	public IConcept GridClassifier() {
		return gridClassifierType;
	}

	/**
	 * Ensure that the passed envelope is east-west on X axis, assuming it is representing
	 * the axis order defined by the passed crs.
	 * @param envelope
	 * @param ccr
	 * @return
	 */
	public static ReferencedEnvelope normalizeEnvelope(
			ReferencedEnvelope envelope, CoordinateReferenceSystem ccr) {

		ReferencedEnvelope ret = envelope;
		if (ccr != null && ccr.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
			/*
			 * swap x/y to obtain the right envelope according to the CRS
			 */
			ret = new ReferencedEnvelope(
					envelope.getMinY(), envelope.getMaxY(),
					envelope.getMinX(), envelope.getMaxX(), ccr);
		} 
		
		return ret;
	}

	/**
	 * Add a gazetteer to the collection.
	 * @param g
	 */
	public void addGazetteer(String id, IGazetteer g) {
		gazetteers.put(id,g);
	}
	
	public CoordinateReferenceSystem getDefaultCRS() {
		return defaultCRS ;
	}
	
	/**
	 * Lookup a feature name through all existing gazetteers. If stopWhenFound is true, return
	 * after the first lookup that succeeds.
	 * 
	 * @param name
	 * @return
	 * @throws ThinklabException
	 */
	public Collection<ShapeValue> lookupFeature(String name, boolean stopWhenFound) 
		throws ThinklabException {
		
		ArrayList<ShapeValue> ret = new ArrayList<ShapeValue>();
		
		for (IGazetteer g : gazetteers.values()) {
			g.resolve(name, ret, null);
			if (stopWhenFound && ret.size() > 0)
				break;
		}
		return ret;
	}

	public Collection<String> listKnownFeatures() {

		ArrayList<String> ret = new ArrayList<String>();
		
		for (IGazetteer g : gazetteers.values()) {
			g.getKnownNames(ret);
		}

		return ret;
	}
	
	/**
	 * Get your engine here, passing the necessary configuration properties. 
	 * 
	 * @param subject
	 * @param properties
	 * @return
	 * @throws ThinklabException
	 */
	public void createGazetteer(Extension ext, Properties properties) throws ThinklabException {
		
		String id = getParameter(ext, "id");
		
		/*
		 * find the declaring plugin so we can find data and files in its classpath
		 */
		ThinklabPlugin resourceFinder = null;
		try {
			resourceFinder =
				(ThinklabPlugin)getManager().getPlugin(ext.getDeclaringPluginDescriptor().getId());
		} catch (PluginLifecycleException e) {
			throw new ThinklabValidationException("can't determine the plugin that created the gazetteer "+ id);
		}
		
		Properties p = new Properties();
		p.putAll(properties);
		
		/*
		 * may point to a property file in the config dir
		 */
		String pfile = getParameter(ext, "property-file");
		if (pfile != null) {
			File f = new File(resourceFinder.getConfigPath() + "/" + pfile);
			if (!f.exists()) {
				
				/*
				 * exit silently, just print a warning
				 */
				logger().warn("gazetteer " + id + " cannot find the property file " +
						pfile + "; initialization aborted");
				return;
			}
			Properties pp = new Properties();
			try {
				pp.load(new FileInputStream(f));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
			p.putAll(pp);
		}
		
		/*
		 * can also specify properties inline
		 */
		for (Extension.Parameter aext : ext.getParameters("property")) {
			String name = aext.getSubParameter("name").valueAsString();
			String value = aext.getSubParameter("value").valueAsString();
			p.setProperty(name, value);
		}

		log.info("creating gazetteer " + id);
		IGazetteer ret = (IGazetteer) getHandlerInstance(ext, "class");
		ret.initialize(p);
		gazetteers.put(id, ret);
	}
	
	
	/**
	 * Load the gazetteers specified in the passed plugin and set them in the
	 * engine repository. Must be called explicitly by plugins declaring gazetteers.
	 * 
	 * @param pluginId
	 * @throws ThinklabException
	 */
	public void loadGazetteers(String pluginId) throws ThinklabException {	

		for (Extension ext : getPluginExtensions(pluginId, PLUGIN_ID, "gazetteer")) {
			createGazetteer(ext, getProperties());
		}
	}

	/**
	 * Find a specific gazetteer by name
	 * 
	 * @param id
	 * @return
	 * @throws ThinklabResourceNotFoundException
	 */
	public IGazetteer requireGazetteer(String id) throws ThinklabResourceNotFoundException {
		IGazetteer ret = gazetteers.get(id);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("gazetteer " + id + " is not registered");
		return ret;
	}

	/**
	 * Returns true if the passed reference system assumes the X axis to be longitude (east-west).
	 * @param ccr
	 * @return
	 */
	public static boolean isLongitudeX(CoordinateReferenceSystem ccr) {
		return !ccr.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH);
	}

	/**
	 * This method decides the CRS to use when they differ in merged extents.
	 * TODO at the moment it just returns the preferred CRS in the plugin, so
	 * even equal CRS get transformed.
	 * 
	 * @param crs1
	 * @param crs2
	 * @return
	 */
	public static CoordinateReferenceSystem chooseCRS(
			CoordinateReferenceSystem crs1, CoordinateReferenceSystem crs2) {

		CoordinateReferenceSystem ret = Geospace.get().getPreferredCRS();
		
		if (ret == null) {
			ret = crs1;
			Geospace.get().setPreferredCRS(ret);
		}
		
		return ret;

	}
	
	/**
	 * Works around geotools bugs and gives us a nice thinklab exception if necessary.
	 * 
	 * @param crsId
	 * @return
	 * @throws ThinklabValidationException
	 */
    public static CoordinateReferenceSystem getCRSFromID(String crsId) throws ThinklabValidationException {
        
        CoordinateReferenceSystem ret = null;
        
        try {
                ret = CRS.decode(crsId);
        } catch (Exception e) {
                throw new ThinklabValidationException(e);
        }
        
        return ret;
        
}
	
}
