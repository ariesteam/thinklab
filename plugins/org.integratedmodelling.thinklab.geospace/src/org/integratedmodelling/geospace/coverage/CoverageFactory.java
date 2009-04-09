package org.integratedmodelling.geospace.coverage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.deegree.ogcwebservices.wcs.getcoverage.GetCoverage;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.factory.Hints;
import org.geotools.feature.FeatureCollection;
import org.geotools.gce.arcgrid.ArcGridReader;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.geospace.feature.AttributeTable;
import org.integratedmodelling.geospace.implementations.cmodels.RegularRasterModel;
import org.integratedmodelling.geospace.implementations.observations.RasterGrid;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.NameGenerator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;


/**
 * This one should wrap all the geotools methods that work on either raster or vector coverages. By vector coverage 
 * we mean a polygon map where different polygons represent different states of the same observation, and polygons do
 * not overlap. By reading a vector file through the coverage factory, we state that the file is a vector coverage, and
 * take responsibility for the consequences.
 * 
 * Coverages of different types should operate transparently in thinklab, although when that will be fully achieved is not
 * clear now.
 * 
 * TODO: this is basically a repository of big objects, and we should implement a strategy to release them when
 * not necessary anymore AND memory becomes critical. Just reference counting coverages that use them isn't enough
 * as we want to prevent continuous reading and releasing; we should use that and a check of the available memory.
 * 
 * @since Feb 22, 2008 Starting with only raster support.
 * 
 * @author Ferdinando Villa
 *
 */
public class CoverageFactory {

	// these are used as property keys to influence the way coverages are read from vector files.
	public static final String VALUE_ATTRIBUTE_PROPERTY = "geospace.internal.value-attribute";
	public static final String SOURCE_LINK_ATTRIBUTE_PROPERTY = "geospace.internal.source-link-attribute";
	public static final String TARGET_LINK_ATTRIBUTE_PROPERTY = "geospace.internal.target-link-attribute";
	public static final String ATTRIBUTE_URL_PROPERTY = "geospace.internal.attribute-url";
		
	static Hashtable<String, ICoverage> coverages = 
		new Hashtable<String, ICoverage>();
	
	/*
	 * we cache the featurecollections read from URLs and the attribute
	 * tables read from URLs (e.g. CSV) or from shapefiles. Our own featurecollection
	 * may or may not come from here.
	 */
	private static Hashtable<String, FeatureCollection<SimpleFeatureType, SimpleFeature>> featureCollections = 
		new Hashtable<String, FeatureCollection<SimpleFeatureType, SimpleFeature>>();

	private static Hashtable<String, AttributeTable> dataCollections = 
		new Hashtable<String, AttributeTable>();

	final static String[] supportedRasterExtensions = {
			"tif",
			"tiff"
	};
	
	final static String[] supportedVectorExtensions = {
			"shp"
	};

	private synchronized static AttributeTable readCSV(URL url, boolean hasHeaders, boolean isExcel) throws ThinklabIOException {
		
		
		AttributeTable atable = dataCollections.get(url.toString());
		
		if (atable == null) {
			atable = new AttributeTable(url, hasHeaders, isExcel);
			dataCollections.put(url.toString(), atable);
		}
		
		return atable;
	}
	
	public static void main(String args[]) {
		
		String req = 
			"http://127.0.0.1:8080/geoserver/wcs?service=wcs&version=1.0.0&request=GetCoverage&coverage=puget:NCLD_King&bbox=1088921.93,-96339.66,1111495.9,-76887.06&crs=EPSG:2285&width=512&height=298&format=geotiff";
		
		try {
			ArrayList<ICoverage> zio = readRaster(new URL(req), null);
			System.out.println(zio);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the source and set properties, but do not render any image or waste any more memory
	 * than necessary at this stage. Load the data using loadImage, possibly after setting different
	 * crop, projection and no-data values.
	 * 
	 * @param url
	 * @param properties
	 * @throws ThinklabException
	 */
	public synchronized static ArrayList<ICoverage> readRaster(URL url, Properties properties) throws ThinklabException {
		
		ArrayList<ICoverage> ret = new ArrayList<ICoverage>();
		
		if (url.toString().startsWith("http:")) {
			
			try {
				File f = File.createTempFile("geo", ".tiff");
				CopyURL.copy(url, f);
				url = f.toURI().toURL();
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		/*
		 * TODO we will need to connect the hints to the plugin's properties, and have our own
		 * hints object in the plugin, assuming the stupid hints interface stays this way.
		 */
		GridCoverage2D coverage = null;
		
		if (url.toString().endsWith(".tif") || url.toString().endsWith(".tiff")) {
			
			try {

				System.out.println("reading TIFF " + url);
				
				GeoTiffReader reader = 
					new GeoTiffReader(url, 
							new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

				coverage = (GridCoverage2D)reader.read(null);	
								
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
			
		} else 	if (url.toString().endsWith(".adf")) {
				
				try {

					System.out.println("reading ArcGrid " + url);
					
					ArcGridReader reader = 
						new ArcGridReader(url, 
								new Hints(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE));

					coverage = (GridCoverage2D)reader.read(null);
					
					
				} catch (Exception e) {
					throw new ThinklabValidationException(e);
				}
				
			}
		
		/* tsk tsk */
		if (coverage == null) {
			throw new ThinklabIOException("read error loading coverage from " + url);
		}

		/* analyze data content. This will give us an object per band. For this purpose, we consider
		 * each band a separate observation. */
		GridSampleDimension[] sdims = coverage.getSampleDimensions();

		for (GridSampleDimension dim : sdims) {
			ret.add(new RasterCoverage(url.toString(), coverage, dim, sdims.length == 1));
		}
		
		return ret;
	}
	
	public synchronized static ArrayList<ICoverage> readVector(URL url, Properties properties) throws ThinklabException {

		ArrayList<ICoverage> ret = new ArrayList<ICoverage>();
		ICoverage coverage = null;
		
		/*
		 * must define the link field in the properties
		 */
		
		FeatureCollection<SimpleFeatureType, SimpleFeature> fc = featureCollections.get(url.toString());
		
		if (fc == null) {
		
		    Map<String, Object> connect = new HashMap<String,Object>();
		    connect.put( "url", url );
		    
			try {
				DataStore dataStore = DataStoreFinder.getDataStore(connect);
				String name = dataStore.getTypeNames()[0];
				FeatureSource<SimpleFeatureType, SimpleFeature> fc1 = dataStore.getFeatureSource(name);
				fc = fc1.getFeatures();
				
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
			
		}

		String valAttr = properties.getProperty(VALUE_ATTRIBUTE_PROPERTY);
		
		String srcAttr = properties.getProperty(SOURCE_LINK_ATTRIBUTE_PROPERTY);
		String lnkAttr = properties.getProperty(TARGET_LINK_ATTRIBUTE_PROPERTY);
		String dataURL = properties.getProperty(ATTRIBUTE_URL_PROPERTY);
		
		if (dataURL != null) {

			try {
				
				/* TODO this may not be excel. Titles should definitely be there. */
				AttributeTable atable = readCSV(new URL(dataURL), true, true);
				
				coverage = 
					new VectorCoverage(
							fc, 
							fc.getSchema().getCoordinateReferenceSystem(), 
							atable,
							srcAttr,
							lnkAttr, 
							valAttr, 
							false);
				
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		} else {

			coverage = new VectorCoverage(
					fc, 
					fc.getSchema().getCoordinateReferenceSystem(), 
					valAttr, 
					false);
		}
		
		if (coverage != null)
			ret.add(coverage);
		
		return ret;
	}
	
	/****
	 * TODO
	 * 
	 * 1. readResource should remove any # from the resource url, read the url and reconstruct coverage names
	 * by appending band names after the anchor; then they should be cached in the coverages array.
	 * 
	 * 2. readResource should be private, and only getCoverage and requireCoverage should be called. They return
	 * ONE coverage: if we have raster bands, the # must be used to identify them. Technically the coverage without
	 * the # should be a merged one, but that's for later. 
	 * 
	 * @param url
	 * @param properties can be null, or use to pass further info to inform how to read the coverage (see 
	 * 		  readXXXX functions).
	 * @return
	 * @throws ThinklabException
	 */
	private static ICoverage getCoverage(URL url, Properties properties) throws ThinklabException {
		
		ICoverage ret = null;
		
		if (url.toString().startsWith("http:")) {
		
			/* 
			 * we don't read coverages from web-based files; interpret as
			 * a WCS call
			 */
//            GetCoverage req = null;
//			try {
//				req = GetCoverage.create(
//						NameGenerator.newName("WCS"), 
//						url.toString());
//			} catch (Exception e) {
//				throw new ThinklabIOException("can't access WCS service for " + url);		
//			}
//			
//			ret = new WCSCoverage(req.getOutput());
//            if ( !req.getOutput().getFormat().getCode().equals("image/tiff") ) {
//            }
//            if ( !req.getDomainSubset().getRequestSRS().getCode().equals("EPSG:4326") ) {
//
//            }
//            Envelope env = GeometryFactory.createEnvelope(-1.5,-1.5,1.5,1.5, CRSFactory.create( "EPSG:4326" ));
//            if ( !req.getDomainSubset().getSpatialSubset().getEnvelope().equals(env) ) {
//
//            }
//            env = GeometryFactory.createEnvelope(0,0,600-1,500-1, null);
//            if ( !req.getDomainSubset().getSpatialSubset().getGrid().equals(env) ) {
//
//            }
//            if ( !req.getSourceCoverage().equals("MapNeatline") ) {
//
//            }
			
		} else {
		
			ret = coverages.get(url.toString());
		
			if (ret == null) {
				
				ArrayList<ICoverage> cret = readResource(url, null);
				for (ICoverage c : cret) {	
					if (c.getSourceUrl().equals(url.toString())) {
						ret = c;
					}
				}
			}
		}
		return ret;
	}
	
	
	/**
	 * Read the given URL and return the coverage(s) defined in it. The URL can be either polygon or vector.
	 * 
	 * @param url
	 * @param properties
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public static ArrayList<ICoverage> readResource(URL url, Properties properties) throws ThinklabException {
		
		ArrayList<ICoverage> cret = null;
		URL resUrl = url;
		
		/* 
		 * if there's an anchor part, remove it - we may add it later if we get more than a coverage
		 * back.
		 */
		if (url.getRef() != null) {
			String[] urlpc = url.toString().split("#");
			try {
				resUrl = new URL(urlpc[0]);
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		}
		
		String ext = MiscUtilities.getFileExtension(resUrl.toString());
		
		if (Arrays.binarySearch(supportedRasterExtensions, ext) >= 0) {
			
			cret = readRaster(resUrl, properties);

			/* store all bands as separate coverages so we don't read them again. */
			for (ICoverage c : cret) {			
				coverages.put(c.getSourceUrl(), c);
			}

		} else if (Arrays.binarySearch(supportedVectorExtensions, ext) >= 0) {
			
			cret = readVector(resUrl, properties);
			
			// we don't cache the coverages with vector: we cache the feature collections 
			// instead. So new coverages are always created anew.
		
		} 
		
		if (cret == null)
			throw new ThinklabValidationException(
					"geospace: can't import a coverage from url: " +
					url + 
					": format unrecognized");
		
		
		return cret;
	}



	/**
	 * Determine if the URL has an associated semantic annotation (.kbox) file, and return it. If there is
	 * no kbox file, return null.
	 * 
	 * @param url
	 * @return
	 */
	public static File getSemanticAnnotation(URL url) {
		return null;
	}
	
	
	public static boolean supportsFormat(String ext) {
		
		return
			Arrays.binarySearch(supportedRasterExtensions, ext) >= 0 || 
			Arrays.binarySearch(supportedVectorExtensions, ext) >= 0;
	}


	public static ICoverage requireCoverage(URL url, Properties properties) throws ThinklabException {
	
		ICoverage ret = getCoverage(url, properties);
		
		if (ret == null)
			throw new ThinklabIOException("CoverageFactory: coverage " + url + " cannot be read");
		
		return ret;
	}
	
	public static ICoverage makeCoverage(RasterGrid extent, Map<Collection<Integer>,Double> data) throws ThinklabException {
		
		GridExtent ext =
			(GridExtent) ((RegularRasterModel)(extent.getConceptualModel())).getExtent();
		
		double[] dataset = new double[ext.getYCells() * ext.getXCells()];
		
		for (Collection<Integer> o : data.keySet()) {
			
			Iterator<Integer> it = o.iterator();
			
			int y = it.next();
			int x = it.next();
			double d = data.get(o);
//			System.out.println(x + "," + y + " = " + d);
			
			dataset [(y * ext.getXCells()) + x] = d;
		}
		
		RasterCoverage ret = new RasterCoverage("", ext, dataset);
		
		ret.write(new File("D:\\A\\results\\temp.tif"));
		
		return ret;
	}
}
