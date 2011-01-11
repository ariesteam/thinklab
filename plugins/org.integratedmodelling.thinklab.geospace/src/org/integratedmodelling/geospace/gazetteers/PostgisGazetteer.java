package org.integratedmodelling.geospace.gazetteers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.searchengine.QueryString;
import org.integratedmodelling.searchengine.ResultContainer;
import org.integratedmodelling.searchengine.SearchEngine;
import org.integratedmodelling.searchengine.SearchEnginePlugin;
import org.integratedmodelling.sql.QueryResult;
import org.integratedmodelling.sql.postgres.PostgreSQLServer;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Path;
import org.integratedmodelling.utils.Polylist;
import org.mvel2.templates.TemplateRuntime;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

public class PostgisGazetteer implements IGazetteer {

	public static final String SHAPE_ID_TEMPLATE = "thinklab.gazetteer.template.id";
	public static final String CRS_PROPERTY = "thinklab.gazetteer.projection";
	public static final String SIMPLIFY_PROPERTY = "thinklab.gazetteer.simplify";
	public static final String FIELD_PROPERTY_PREFIX = "thinklab.gazetteer.field.";
	public static final String INDEXED_FIELDS_PROPERTY = "thinklab.gazetteer.indexedfields";

	SearchEngine searchEngine = null;

	PostgreSQLServer _server = null;
	Properties _properties = null;
	Properties _additionalProperties = new Properties();
	File additionalPropertiesFile = null;

	String[] createStatements = {
	// TODO add metadata table as ugly id/key/value container
	};
	private int priority = 128;

	class GazetteerResult implements IQueryResult {

		ResultContainer res = null;

		public GazetteerResult(ResultContainer res) {
			this.res = res;
		}

		@Override
		public IValue getBestResult(ISession session) throws ThinklabException {
			return res.getBestResult(session);
		}

		@Override
		public IQueriable getQueriable() {
			return PostgisGazetteer.this;
		}

		@Override
		public IQuery getQuery() {
			return res.getQuery();
		}

		@Override
		public IValue getResult(int n, ISession session)
				throws ThinklabException {
			return res.getResult(n, session);
		}

		@Override
		public Polylist getResultAsList(int n,
				HashMap<String, String> references) throws ThinklabException {
			return res.getResultAsList(n, references);
		}

		@Override
		public int getResultCount() {
			return res.getResultCount();
		}

		@Override
		public IValue getResultField(int n, String schemaField)
				throws ThinklabException {

			if (schemaField.equals(IGazetteer.SHAPE_FIELD)) {
				IValue id = res.getResultField(n, "id");
				return resolve(res.getResultField(n, "id").toString(), null,
						null).iterator().next();
			}
			return res.getResultField(n, schemaField);
		}

		@Override
		public int getResultOffset() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float getResultScore(int n) {
			return res.getResultScore(n);
		}

		@Override
		public int getTotalResultCount() {
			return res.getTotalResultCount();
		}

		@Override
		public void moveTo(int currentItem, int itemsPerPage)
				throws ThinklabException {
			res.moveTo(currentItem, itemsPerPage);
		}

		@Override
		public float setResultScore(int n, float score) {
			return res.setResultScore(n, score);
		}
	}

	public PostgisGazetteer() {
		// do nothing, expect initialize()
	}

	private void init(URI uri, Properties properties) throws ThinklabException {

		String id = MiscUtilities.getNameFromURL(uri.toString());

		_server = new PostgreSQLServer(uri, properties);
		_properties = properties;

		additionalPropertiesFile = new File(Geospace.get().getScratchPath()
				+ File.separator + "gprops" + File.separator + id);
		additionalPropertiesFile.mkdirs();
		additionalPropertiesFile = new File(additionalPropertiesFile
				+ File.separator + id + ".properties");

		if (additionalPropertiesFile.exists())
			try {
				_additionalProperties.load(new FileInputStream(
						additionalPropertiesFile));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}

		if (!_server.haveTable("locations")) {

			_server.execute("CREATE TABLE locations (id varchar(128) PRIMARY KEY);");
			_server.query("SELECT AddGeometryColumn('', 'locations', 'shape', 4326, 'MULTIPOLYGON', 2);");

			for (String s : createStatements) {
				_server.execute(s);
			}
		}

		searchEngine = SearchEnginePlugin.get().createSearchEngine(id.replaceAll("\\.", "_"),
				properties);
		searchEngine.addIndexField("id", "text", 1.0);

		/*
		 * find all indexed field names in properties and add to the engine.
		 */
		String ifl = _additionalProperties.getProperty(INDEXED_FIELDS_PROPERTY);

		this.priority = Integer.parseInt(properties.getProperty(
				PRIORITY_PROPERTY, "128").trim());

		if (ifl != null) {
			String[] ps = ifl.split(",");
			for (String f : ps) {
				searchEngine.addIndexField(f, "text", 1.0);
			}
		}
	}

	public PostgisGazetteer(URI uri, Properties properties)
			throws ThinklabException {
		init(uri, properties);
	}

	@Override
	public Collection<String> getKnownNames(Collection<String> container) {
		// we could retrieve these, but they may be millions
		return container == null ? new ArrayList<String>() : container;
	}

	@Override
	public Collection<ShapeValue> resolve(String id,
			Collection<ShapeValue> container, Properties options)
			throws ThinklabException {

		if (container == null)
			container = new ArrayList<ShapeValue>();

		String sql = "SELECT id, ST_AsText(shape) AS shape FROM locations WHERE id = '"
				+ id + "';";

		QueryResult res = _server.query(sql);
		for (int i = 0; i < res.nRows(); i++) {
			ShapeValue shape = new ShapeValue("EPSG:4326 "
					+ res.getString(i, 1));
			container.add(shape);
		}
		return container;
	}

	@Override
	public void addLocation(String id, ShapeValue shape,
			Map<String, Object> metadata) throws ThinklabException {

		
		
		/*
		 * the gazetteer will contain WGS84 no matter what we feed it. If no
		 * CRS, we assume it's that but who knows.
		 */
		shape = shape.transform(DefaultGeographicCRS.WGS84);

		// generate and run appropriate insert statements
		String sql = "INSERT INTO locations (id, shape) VALUES ('"
				+ Escape.forSQL(id) + "', ST_GeomFromText('"
				+ shape.getGeometry().toText() + "',4326));";

		_server.execute(sql);

		Geospace.get().logger().info("added shape " + id + " to gazetteer");

	}

	@Override
	public void importLocations(String url, Properties properties)
			throws ThinklabException {

		if (properties == null)
			properties = new Properties();
		
		if (_properties != null)
			properties.putAll(_properties);
		
		// this should be a shapefile or a WFS data source
		ICoverage coverage = CoverageFactory.requireCoverage(
				MiscUtilities.getURLForResource(url), properties);

		if (!(coverage instanceof VectorCoverage)) {
			throw new ThinklabInappropriateOperationException(
					url
							+ " specifies a non-vector coverage: cannot use in gazetteer");
		}

		String fl = url;
		try {
			if (url.startsWith("file:"))
				fl = Escape.fromURL(new URL(url).getFile());
		} catch (MalformedURLException e1) {
			throw new ThinklabValidationException(e1);
		}

		/*
		 * look for a properties file to drive the conversion into features
		 */
		Properties fprop = new Properties(properties);
		File pfile = new File(MiscUtilities.changeExtension(fl, "properties"));
		if (pfile.exists())
			try {
				fprop.load(new FileInputStream(pfile));
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}

		FeatureIterator<SimpleFeature> fi = null;
		String[] attributes = ((VectorCoverage) coverage).getAttributeNames();

		String epsg = fprop.getProperty(CRS_PROPERTY);
		Double simplify = null;
		if (fprop.containsKey(SIMPLIFY_PROPERTY))
			simplify = Double.parseDouble(fprop.getProperty(SIMPLIFY_PROPERTY));

		CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem();
		if (crs == null && epsg != null) 
			crs = Geospace.getCRSFromID(epsg);
		
		if (crs == null)
			throw new ThinklabValidationException(
					"cannot establish projection for source " + url);

		try {
			fi = ((VectorCoverage) coverage).getFeatureIterator(null,
					attributes);
			boolean first = true;
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
				 * native id of the shape, which usually is ugly and useless.
				 */
				String idTemplate = fprop.getProperty(SHAPE_ID_TEMPLATE,
						"@{id}");
				HashMap<String, Object> fields = new HashMap<String, Object>();

				fields.put("id", f.getID());

				/*
				 * retrieve all attributes for the shape.
				 */
				for (int i = 0; i < attributes.length; i++) {
					/*
					 * skip the monster geometry. FIXME this should use the
					 * "endorsed" name from the schema, although it's always
					 * the_geom for now.
					 */
					if (attributes[i].equals("the_geom"))
						continue;
					fields.put(attributes[i], f.getAttribute(attributes[i]));
				}

				/*
				 * compute any other field defined in properties and pass to the
				 * Lucene index.
				 */
				ArrayList<String> sav = new ArrayList<String>();

				for (Object p : fprop.keySet()) {
					if (p.toString().startsWith(FIELD_PROPERTY_PREFIX)) {
						String tmpl = fprop.getProperty(p.toString());
						String fiel = Path.getLast(p.toString(), '.');
						fields.put(fiel,
								(String) TemplateRuntime.eval(tmpl, fields));
						sav.add(fiel);
					}
				}

				/*
				 * compute id and use in both lucene index and postgis
				 */
				String id = (String) TemplateRuntime.eval(idTemplate, fields);

				// do this again to update the ID in case it changed
				fields.put("id", id);
				// and this so that the ID gets indexed too
				fields.put("id_text", id);
				
				addLocation(id, shape, fields);

				sav.add("id");
				sav.add("id_text");
				
				String[] saved = (String[]) sav.toArray(new String[sav.size()]);

				/*
				 * if first time, communicate indexed fields to gazetteer
				 * and save them in properties for next time. Also add to
				 * search engine if not there already.
				 */
				if (first) {

					boolean changed = false;
					String pr = _additionalProperties.getProperty(
							INDEXED_FIELDS_PROPERTY, "");
					for (String ss : saved) {
						if (!searchEngine.haveIndexField(ss)) {
							pr += (pr.isEmpty() ? "" : ",") + ss;
							changed = true;
						}
						searchEngine.addIndexField(ss, "text", 1.0);
					}

					if (changed) {
						_additionalProperties.setProperty(
								INDEXED_FIELDS_PROPERTY, pr);
						try {
							_additionalProperties.store(
									new FileOutputStream(
									this.additionalPropertiesFile),
									null);
						} catch (Exception e) {
							throw new ThinklabIOException(e);
						}
					}
					
					first = false;
				}

				searchEngine.submitRecord(saved, fields, null);

			}
		} finally {
			fi.close();
			searchEngine.flush();
		}
	}

	@Override
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPropertyFile(File propfile) {

	}

	@Override
	public void initialize(Properties properties) throws ThinklabException {

		String u = properties.getProperty("uri");
		URI uri;
		try {
			uri = new URI(u);
		} catch (URISyntaxException e) {
			throw new ThinklabValidationException(e);
		}

		init(uri, properties);
	}

	@Override
	public IQuery parseQuery(String toEval) throws ThinklabException {
		return new QueryString(toEval);
	}

	@Override
	public IQueryResult query(IQuery q) throws ThinklabException {
		return new GazetteerResult((ResultContainer) searchEngine.query(q));
	}

	@Override
	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		return new GazetteerResult((ResultContainer) searchEngine.query(q,
				offset, maxResults));
	}

	@Override
	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		return new GazetteerResult((ResultContainer) searchEngine.query(q,
				metadata, offset, maxResults));
	}

	@Override
	public int getPriority() {
		return this.priority;
	}

	@Override
	public void resetToEmpty() throws ThinklabException {

		if (_server.haveTable("locations")) {

			_server.execute("DROP TABLE locations;");
			_server.execute("CREATE TABLE locations (id varchar(128) PRIMARY KEY);");
			_server.query("SELECT AddGeometryColumn('', 'locations', 'shape', 4326, 'MULTIPOLYGON', 2);");

			for (String s : createStatements) {
				_server.execute(s);
			}
		}
		searchEngine.clear();
	}

}
