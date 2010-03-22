package org.integratedmodelling.geospace.gazetteers;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.geotools.feature.FeatureIterator;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.sql.QueryResult;
import org.integratedmodelling.sql.postgres.PostgreSQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.MiscUtilities;
import org.mvel2.templates.TemplateRuntime;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class PostgisGazetteer implements IGazetteer {

	private static final String SHAPE_ID_TEMPLATE = "thinklab.gazetteer.template.id";
	private static final String CRS_PROPERTY = "thinklab.gazetteer.projection";
	private static final String SIMPLIFY_PROPERTY = "thinklab.gazetteer.simplify";

	SearchEngine searchEngine = null;
	
	PostgreSQLServer _server = null;
	Properties _properties = null;
	
	String[] createStatements = {
			// TODO add metadata table as ugly id/key/value container
	};
	
	public PostgisGazetteer() {
		// do nothing, expect initialize()
	}
	
	public PostgisGazetteer(URI uri, Properties properties) throws ThinklabStorageException {
		
		_server = new PostgreSQLServer(uri, properties);
		_properties = properties;
		
		if (!_server.haveTable("locations")) {
			
			_server.execute("CREATE TABLE locations (id varchar(128) PRIMARY KEY);");
			_server.query("SELECT AddGeometryColumn('', 'locations', 'shape', 4326, 'MULTIPOLYGON', 2);");

			for (String s : createStatements) {
				_server.execute(s);
			}
		}
	}
	
	@Override
	public Collection<String> getKnownNames(Collection<String> container) {
		// we could retrieve these, but they may be millions
		return container == null ? new ArrayList<String>() : container;
	}

	@Override
	public Collection<ShapeValue> resolve(String name,
			Collection<ShapeValue> container, Properties options)
			throws ThinklabException {
		
		if (container == null)
			container = new ArrayList<ShapeValue>();
		
		String sql =  
			"SELECT id, ST_AsText(shape) AS shape FROM locations WHERE id = '" + 
			name + 
			"';";
		
		QueryResult res = _server.query(sql);
		for (int i = 0; i < res.nRows(); i++) {
			ShapeValue shape = new ShapeValue("EPSG:4326 " + res.getString(i, 1));
			container.add(shape);
		}
		return container;
	}

	@Override
	public void addLocation(String id, ShapeValue shape,
			Map<String, Object> metadata) throws ThinklabException {

		/*
		 * the gazetteer will contain WGS84 no matter what we feed it. If no CRS, we assume it's that but who knows.
		 */
		if (!shape.getCRS().equals(Geospace.get().getDefaultCRS())) {
			shape = shape.transform(Geospace.get().getDefaultCRS());
		}
				
		// generate and run appropriate insert statements
		String sql =
			"INSERT INTO locations (id, shape) VALUES ('" +
			id +
			"', ST_GeomFromText('" +
			shape.getGeometry().toText() + 
			"',4326));";
		
		_server.execute(sql);
		
		Geospace.get().logger().info("added shape " + id + " to gazetteer");
			
	}

	@Override
	public void importLocations(String url) throws ThinklabException {
	
		// this should be a shapefile or a WFS data source
		ICoverage coverage =
			CoverageFactory.requireCoverage(MiscUtilities.getURLForResource(url), _properties);
		
		if (! (coverage instanceof VectorCoverage)) {
			throw new ThinklabInappropriateOperationException(
					url + " specifies a non-vector coverage: cannot use in gazetteer");
		}
		
		String fl = url;
		try {
			if (url.startsWith("file:"))
				fl = new URL(url).getFile();
		} catch (MalformedURLException e1) {
			throw new ThinklabValidationException(e1);
		}
		
		/*
		 * look for a properties file to drive the conversion into features
		 */
		Properties fprop = new Properties();
		File pfile = new File(MiscUtilities.changeExtension(fl, "properties"));
		if (pfile.exists())
			try {
				fprop.load(new FileInputStream(pfile));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
		
		FeatureIterator<SimpleFeature> fi = null;
		String[] attributes = ((VectorCoverage)coverage).getAttributeNames();
		
		String epsg = fprop.getProperty(CRS_PROPERTY);
		Double simplify = null;
		if (fprop.containsKey(SIMPLIFY_PROPERTY)) 
			simplify = Double.parseDouble(fprop.getProperty(SIMPLIFY_PROPERTY));
		
		CoordinateReferenceSystem crs = 
			epsg != null ?
				Geospace.getCRSFromID(epsg) :
				coverage.getCoordinateReferenceSystem();
				
		if (crs == null)
			throw new ThinklabValidationException("cannot establish projection for source " + url);
		
		try {
			fi = ((VectorCoverage)coverage).getFeatureIterator(null, attributes);
			while (fi.hasNext()) {
				
				SimpleFeature f = fi.next();
		        Geometry geometry = (Geometry) f.getDefaultGeometry();
		        ShapeValue shape = new ShapeValue(geometry, crs);
		        
		        /*
		         * simplify if so requested, using values in original units.
		         */
		        if (simplify != null)
		        	shape.simplify(simplify);
		        
		        /*
		         * substitute fields; the worst that can happen is to use the 
		         * native id of the shape, which usually is ugly and useless.s
		         */
		        String idTemplate = fprop.getProperty(SHAPE_ID_TEMPLATE, "@{id}");
		        HashMap<String,Object> fields = new HashMap<String, Object>();
				
		        // to be used if nothing else is available
		        fields.put("id", f.getID());
		        
				/*
				 * retrieve all attributes for the shape.
				 */
		        for (int i = 0; i < attributes.length; i++) {
		        	fields.put(attributes[i], f.getAttribute(attributes[i]));
		        }
		        
		        String id = (String) TemplateRuntime.eval(idTemplate, fields);
		        
		        /*
		         * TODO compute any other field defined in properties and do something with them. It
		         * should be a Lucene index obviously.
		         */
		        
		        addLocation(id, shape, null);    
			}
		} finally {
			fi.close();
		}
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] s) {
		
	}

	@Override
	public void initialize(Properties properties) throws ThinklabException {
		
		String uri = properties.getProperty("uri");
		String id = MiscUtilities.getNameFromURL(uri);
		
		try {
			_server = new PostgreSQLServer(new URI(uri), properties);
		} catch (URISyntaxException e) {
			throw new ThinklabValidationException(e);
		}
		_properties = properties;
		
		if (!_server.haveTable("locations")) {
			
			_server.execute("CREATE TABLE locations (id varchar(128) PRIMARY KEY);");
			_server.query("SELECT AddGeometryColumn('', 'locations', 'shape', 4326, 'MULTIPOLYGON', 2);");

			for (String s : createStatements) {
				_server.execute(s);
			}
			
			searchEngine = SearchEnginePlugin.get().createSearchEngine(id, properties);
		}
	}

	@Override
	public Collection<ShapeValue> findLocations(String name,
			Collection<ShapeValue> container) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
