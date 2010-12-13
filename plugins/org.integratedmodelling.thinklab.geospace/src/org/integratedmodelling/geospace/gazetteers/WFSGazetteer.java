package org.integratedmodelling.geospace.gazetteers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Geometry;

public class WFSGazetteer implements IGazetteer {

	String _server;
	String _layer;
	private DataStore datastore;
	
	@Override
	public IQuery parseQuery(String toEval) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ShapeValue> resolve(String name,
			Collection<ShapeValue> container, Properties options)
			throws ThinklabException {

		if (container == null)
			container = new ArrayList<ShapeValue>();
		
		SimpleFeatureType schema = null;
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		Iterator<SimpleFeature> iterator = null;
		Geometry geom = null;
		
		try {
			
			schema = datastore.getSchema( this._layer );
			FeatureSource<SimpleFeatureType, SimpleFeature> source = datastore.getFeatureSource( this._layer );
			String geomName = schema.getGeometryDescriptor().getLocalName();

			/*
			 * TODO allow to use attribute
			 */
			FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2( GeoTools.getDefaultHints() );
			Set<FeatureId> fids = new HashSet<FeatureId>();
			fids.add(ff.featureId(name));
			Filter filter = ff.id( fids );

			Query query = new DefaultQuery( _layer, filter, new String[]{ geomName } );
		
			features = source.getFeatures( query );
			iterator = features.iterator();
			
			if( iterator.hasNext() ){
				SimpleFeature feature = (SimpleFeature) iterator.next();
				geom = (Geometry) feature.getDefaultGeometry();
			}
			
		} catch (IOException e) {

			throw new ThinklabIOException(e);
			
		} finally {
			if (features != null)
				features.close( iterator );
		}
		
		
		if (geom != null) {
			container.add(
				new ShapeValue(geom, schema.getCoordinateReferenceSystem()).
					transform(Geospace.get().getStraightGeoCRS()));
		}

		return container;
	}

	@Override
	public Collection<String> getKnownNames(Collection<String> container) {

		if (container == null)
			container = new ArrayList<String>();
		
		FeatureCollection<SimpleFeatureType, SimpleFeature> features = null;
		Iterator<SimpleFeature> iterator = null;
		
		try {
			
			FeatureSource<SimpleFeatureType, SimpleFeature> source = datastore.getFeatureSource( this._layer );
			Query query = new DefaultQuery( _layer);

			features = source.getFeatures( query );
			iterator = features.iterator();
			
			while ( iterator.hasNext() ) {
				
				/*
				 * TODO allow to use attribute
				 */
				SimpleFeature feature = (SimpleFeature) iterator.next();
				container.add(feature.getID());
				
			}
			
		} catch (IOException e) {
			// just ignore it
		} finally {
			if (features != null)
				features.close( iterator );
		}
		
		return container;

	
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void importLocations(String url) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addLocation(String id, ShapeValue shape,
			Map<String, Object> metadata) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize(Properties properties) throws ThinklabException {

		this._server = properties.getProperty("wfs.service");
		this._layer = properties.getProperty("wfs.layer");
		
		String getCapabilities = _server + "?REQUEST=GetCapabilities";
		HashMap<String, Object> connectionParameters = new HashMap<String, Object>();
		connectionParameters.put("WFSDataStoreFactory:GET_CAPABILITIES_URL", getCapabilities );

		// Step 2 - connection
		try {
			this.datastore = DataStoreFinder.getDataStore( connectionParameters );
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}

	}

	@Override
	public int getPriority() {
		// TODO Auto-generated method stub
		return 0;
	}

}
