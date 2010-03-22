package org.integratedmodelling.geospace.gazetteers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

import org.geotools.feature.FeatureIterator;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.sql.QueryResult;
import org.integratedmodelling.sql.postgres.PostgreSQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.MiscUtilities;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

public class PostgisGazetteer implements IGazetteer {

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
		if (shape.getCRS() != null && !shape.getCRS().equals(Geospace.get().getDefaultCRS())) {
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
		
		FeatureIterator<SimpleFeature> fi = null;
		try {
			fi = ((VectorCoverage)coverage).getFeatureIterator(null, (String[]) null);
			while (fi.hasNext()) {
				
				/*
				 * 
				 */
				SimpleFeature f = fi.next();
		        Geometry geometry = (Geometry) f.getDefaultGeometry();
		        ShapeValue shape = new ShapeValue(geometry, coverage.getCoordinateReferenceSystem());
		        
		        /*
		         * TODO add metadata from attributes
		         */
		        
		        addLocation(f.getID(), shape, null);    
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
		
		try {
			_server = new PostgreSQLServer(
					new URI(properties.getProperty("uri")), properties);
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
		}
	}
	
}
