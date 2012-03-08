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
package org.integratedmodelling.thinklab.geospace;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.ReferencingFactoryFinder;
import org.geotools.referencing.factory.PropertyAuthorityFactory;
import org.geotools.referencing.factory.ReferencingFactoryContainer;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.geospace.interfaces.IGazetteer;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.java.plugin.PluginLifecycleException;
import org.java.plugin.registry.Extension;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;
import org.w3c.dom.Node;

public class Geospace  {
	
	private IConcept shapeType;
	private IConcept pointType;
	private IConcept lineStringType;
	private IConcept polygonType;
	private IConcept multiPointType;
	private IConcept multiLineStringType;
	private IConcept multiPolygonType;
	private ISemanticObject areaLocationInstance;
	private ISemanticObject rasterGridInstance;
	private ISemanticObject spatialCoverageInstance;
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
	public static final String HAS_TRANSFORMATION_EXPRESSION = "geospace:hasTransformation";
	public static final String HAS_ATTRIBUTE_URL = "geospace:hasAttributeUrl";
	public static final String RASTER_GRID = "geospace:RasterGrid";
	public static final String AREAL_LOCATION = "geospace:RasterGrid";
	public static final String GRID_CLASSIFIER = "geospace:GridClassifier";
	public static final String CLASSIFIED_GRID = "geospace:ClassifiedGrid";
	public static final String GRID_CLASSIFICATION_MODEL = "geospace:GridClassification";
	public static final String HAS_FILTER_PROPERTY = "geospace:hasFilter";	

		
	// the projection to use if we need meters
	public static final String EPSG_PROJECTION_METERS  = "EPSG:3005";
	public static final String EPSG_PROJECTION_DEFAULT = "EPSG:4326";
	public static final String EPSG_PROJECTION_GOOGLE  = "EPSG:3857";
	
	// property to add new CRS by hand in property file 
	public static final String CUSTOM_CRS_PROPERTY = "geospace.crs.";

	// projections not in the main repository inserted through properties
	static HashMap<String, CoordinateReferenceSystem> localCRS = 
		new HashMap<String, CoordinateReferenceSystem>();
	
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
	private Boolean _useSquareCellsM;
	
	static private Geospace _this;
	
	public static Geospace get() {
		if (_this == null) {
			try {
				_this = new Geospace();
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return _this;
	}
	
	void registerAdditionalCRS() throws ThinklabException {
		
		URL epsg = Thinklab.get().getResourceURL("proj/epsg.properties");
		
		if (epsg != null) {
			Hints hints = 
				new Hints(Hints.CRS_AUTHORITY_FACTORY, PropertyAuthorityFactory.class);
			ReferencingFactoryContainer referencingFactoryContainer = 
				ReferencingFactoryContainer.instance(hints);
			PropertyAuthorityFactory factory;
			try {
				factory = new PropertyAuthorityFactory(
				                referencingFactoryContainer,
				                Citations.fromName("EPSG"),
				                epsg);
				ReferencingFactoryFinder.addAuthorityFactory(factory);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}      
		}
	}
	
	
	private Geospace() throws ThinklabException {
				
			/*
			 * TODO put all these class names into global strings
			 */
			pointType = Thinklab.get().getConcept("geospace:Point");
			lineStringType = Thinklab.get().getConcept("geospace:LineString");
			polygonType = Thinklab.get().getConcept("geospace:Polygon");
			multiPointType = Thinklab.get().getConcept("geospace:MultiPoint");
			multiLineStringType = Thinklab.get().getConcept("geospace:MultiLineString");
			multiPolygonType = Thinklab.get().getConcept("geospace:MultiPolygon");
//			areaLocationInstance = Thinklab.get().getConcept("geospace:ArealLocationInstance");
//			rasterGridInstance = Thinklab.get().getConcept("geospace:RegularGridInstance");
//			spatialCoverageInstance = Thinklab.get().getConcept("geospace:SpatialCoverageInstance");
			arealLocationType = Thinklab.get().getConcept("geospace:ArealLocation");
			rasterSpaceType = Thinklab.get().getConcept(RASTER_CONCEPTUAL_MODEL);
			rasterGridObservable = Thinklab.get().getConcept(RASTER_GRID_OBSERVABLE);
			subdividedSpaceObservable = Thinklab.get().getConcept("geospace:SubdividedSpace");
			spaceObservable = Thinklab.get().getConcept("geospace:SpaceObservable");
			gridClassifierType = Thinklab.get().getConcept(GRID_CLASSIFIER);
			
			shapeType = Thinklab.get().getConcept("geospace:SpatialRecord");
			
			hasBoundingBoxPropertyID = "geospace:hasBoundingBox";
			hasCentroidPropertyID = "geospace:hasCentroid";
						
	

		try {
			registerAdditionalCRS();

			metersCRS = CRS.decode(EPSG_PROJECTION_METERS, true);
			defaultCRS = CRS.decode(EPSG_PROJECTION_DEFAULT, true);
			googleCRS = CRS.decode(EPSG_PROJECTION_GOOGLE, true);
			
			CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
			geoCRSstraight = factory.createCoordinateReferenceSystem("EPSG:4326");
			
		} catch (Exception e) {
			throw new ThinklabException(e);
		} 
		
		/*
		 * create preferred CRS if one is specified. Highly advisable to set one if hybrid data
		 * are used.
		 */
		if (Thinklab.get().getProperties().containsKey(PREFERRED_CRS_PROPERTY)) {	
			preferredCRS = getCRSFromID(Thinklab.get().getProperties().getProperty(PREFERRED_CRS_PROPERTY));
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
		
		return useDefault ? Thinklab.get().getProperties().getProperty(PREFERRED_CRS_PROPERTY) : null;

	}
	
	public CoordinateReferenceSystem getMetersCRS() {
		return metersCRS;
	}

	/**
	 * Get the appropriate (sort of) UTM projection for the passed WGS84 point.
	 * 
	 * @param x
	 * @param y
	 * @return
	 * @throws ThinklabValidationException
	 */
	public CoordinateReferenceSystem getMetersCRS(double x, double y) throws ThinklabValidationException {

		int base_srid = y < 0 ? 32700 : 32600;
		int out_srid = 
				x == 180.0 ? 
						base_srid + 60 :
						base_srid + (int)Math.floor((x + 186.0)/6.0);
		
		CoordinateReferenceSystem ret = getCRSFromID("EPSG:" + out_srid);
		
		return ret == null ? metersCRS : ret;
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

	public ISemanticObject absoluteArealLocationInstance(IOntology session) {
		return areaLocationInstance;
	}
	
	public ISemanticObject absoluteRasterGridInstance(IOntology session) {
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

	public ISemanticObject absoluteSpatialCoverageInstance(IOntology session) {
		return spatialCoverageInstance;
	}


	public IConcept GridClassifier() {
		return gridClassifierType;
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
//	public IQueryResult lookupFeature(String name) 
//		throws ThinklabException {
//		
//		MultipleQueryResult ret =
//			new MultipleQueryResult(new QueryString(name));
//		
//		// sort them every time, we're not going to have a million of these.
//		IGazetteer[] gazz = new IGazetteer[gazetteers.size()];
//		int i = 0;
//		for (IGazetteer g : gazetteers.values())
//			gazz[i++] = g;
//		
//		Arrays.sort(gazz, new Comparator<IGazetteer>() {
//			@Override
//			public int compare(IGazetteer o1, IGazetteer o2) {
//				return o1.getPriority() - o2.getPriority();
//			}
//		});
//		
//		for (IGazetteer g : gazz) {
//			ret.add(g.query(g.parseQuery(name)));
//		}
//		
//		return ret;
//	}

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
		
//		String id = getParameter(ext, "id");
//		
//		/*
//		 * find the declaring plugin so we can find data and files in its classpath
//		 */
//		ThinklabPlugin resourceFinder = null;
//		try {
//			resourceFinder =
//				(ThinklabPlugin)getManager().getPlugin(ext.getDeclaringPluginDescriptor().getId());
//		} catch (PluginLifecycleException e) {
//			throw new ThinklabValidationException("can't determine the plugin that created the gazetteer "+ id);
//		}
//		
//		Properties p = new Properties();
//		p.putAll(properties);
//		
//		/*
//		 * may point to a property file in the config dir
//		 */
//		String pfile = getParameter(ext, "property-file");
//		if (pfile != null) {
//			File f = new File(resourceFinder.getConfigPath() + "/" + pfile);
//			if (!f.exists()) {
//				
//				/*
//				 * exit silently, just print a warning
//				 */
//				logger().warn("gazetteer " + id + " cannot find the property file " +
//						pfile + "; initialization aborted");
//				return;
//			}
//			Properties pp = new Properties();
//			try {
//				pp.load(new FileInputStream(f));
//			} catch (Exception e) {
//				throw new ThinklabIOException(e);
//			}
//			p.putAll(pp);
//		}
//		
//		/*
//		 * can also specify properties inline
//		 */
//		for (Extension.Parameter aext : ext.getParameters("property")) {
//			String name = aext.getSubParameter("name").valueAsString();
//			String value = aext.getSubParameter("value").valueAsString();
//			p.setProperty(name, value);
//		}
//
//		log.info("creating gazetteer " + id);
//		IGazetteer ret = (IGazetteer) getHandlerInstance(ext, "class");
//		ret.initialize(p);
//		gazetteers.put(id, ret);
	}
	
	
	/**
	 * Load the gazetteers specified in the passed plugin and set them in the
	 * engine repository. In addition, look in plugin data dir for subdirectories
	 * of gazetters/. Any subdir in it is supposed to contain a gazetteer.properties
	 * file that will be used to initialize a PostgisGazetteer, and (optionally) 
	 * a set of shapefiles that will be loaded if their 
	 * creation date is more recent than the date of last access to the 
	 * gazetteer.
	 *
	 *  Must be called explicitly by plugins declaring gazetteers.
	 * 
	 * @param pluginId
	 * @throws ThinklabException
	 * @throws PluginLifecycleException 
	 */
	public void loadGazetteers(String pluginId) throws ThinklabException {	
//
//		for (Extension ext : getPluginExtensions(pluginId, PLUGIN_ID, "gazetteer")) {
//			createGazetteer(ext, getProperties());
//		}
	}

	
	public void loadGazetteersFromDirectory(File dir) throws ThinklabException {

		File pdir = new File(dir + File.separator + "gazetteers");
		
		if (!(pdir.exists() && pdir.isDirectory()))
			return;
		
		String[] files = pdir.list();
		
		for (String file : files) {
			File gdir = new File(pdir + File.separator + file);
			if (gdir.isDirectory()) {
				IGazetteer gaz = readGazetteerFromDirectory(gdir);
				if (gaz != null) {
					gazetteers.put(file, gaz);
				}
			}
		}
	}
	
	private IGazetteer readGazetteerFromDirectory(File gdir) throws ThinklabException {
		
//		File props = new File(gdir + File.separator + "gazetteer.properties");
		IGazetteer ret = null;
//		
//		if (props.exists()) {
//			
//			Properties pp = new Properties();
//			try {
//				pp.load(new FileInputStream(props));
//			} catch (Exception e) {
//				throw new ThinklabIOException(e);
//			}
//			
//			logger().info("loading gazetteer from " + gdir);
//			
//			ret = new PostgisGazetteer();
//			ret.initialize(pp);
//			
//			long mtime = new Date().getTime();
//			File lock = new File(gdir + File.separator + ".last_access");
//			
//			if (lock.exists()) {
//				mtime = lock.lastModified();
//			}
//				
//			for (String ff : gdir.list()) {
//				if (ff.endsWith(".shp")) {
//
//					File shp = new File(gdir + File.separator + ff);					
//					File lck = new File(MiscUtilities.changeExtension(shp.toString(), "lck"));
//
//					if (!lck.exists()) {
//
//						logger().info("adding gazetteer source " + ff);
//
//						try {
//							ret.importLocations(shp.toURI().toURL().toString(), null);
//						} catch (MalformedURLException e) {
//							throw new ThinklabValidationException(e);
//						}
//						
//						try {
//							FileUtils.touch(lck);
//						} catch (IOException e) {
//							throw new ThinklabIOException(e);
//						}
//					}
//				}
//			}
//			
//			try {
//				FileUtils.touch(lock);
//			} catch (IOException e) {
//				throw new ThinklabIOException(e);
//			}
//		}
//		
		return ret;
	}

	/**
	 * Find a specific gazetteer by name, complain if not found
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
	 * Find a specific gazetteer by name
	 * 
	 * @param id
	 * @return
	 * @throws ThinklabResourceNotFoundException
	 */
	public IGazetteer retrieveGazetteer(String id) {
		return gazetteers.get(id);
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
	 * Works around geotools bugs, supports the EPSG:STRAIGHT hack for when we need a code for
	 * lat/lon corrected WGS84, and gives us a nice capturable thinklab exception if things go wrong.
	 * 
	 * Should be used throughout instead of CRS.decode().
	 * 
	 * @param crsId
	 * @return
	 * @throws ThinklabValidationException
	 */
    public static CoordinateReferenceSystem getCRSFromID(String crsId) throws ThinklabValidationException {
        
    	if (crsId.equals("EPSG:STRAIGHT"))
    		return get().geoCRSstraight;
    	
        CoordinateReferenceSystem ret = localCRS.get(crsId);
        
        if (ret == null) {
        	try {
        		ret = CRS.decode(crsId, true);
        	} catch (Exception e) {
                throw new ThinklabValidationException(e);
        	}
        }
        
        return ret;
    }

    /**
     * Return a shape from the first gazetteer that knows its ID.
     * @param id
     * @return
     * @throws ThinklabException
     */
	public ShapeValue retrieveFeature(String id) throws ThinklabException {
		for (IGazetteer g : gazetteers.values()) {
			Collection<ShapeValue> shapes = g.resolve(id, null, null);
			if (shapes.size() > 0) {
				ShapeValue ret = shapes.iterator().next();
				return ret;
			}
		}
		return null;
	}

	public boolean squareCellsM() {
		if (_useSquareCellsM == null) {
			_useSquareCellsM = 
					BooleanValue.parseBoolean(Thinklab.get().getProperties().getProperty("square.cells.meters", "false"));
		}
		return _useSquareCellsM;
	}
	
}
