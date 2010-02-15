package org.integratedmodelling.geospace.importers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.storage.IKnowledgeImporter;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

@KnowledgeLoader(format="wfs")
public class WFSImporter implements IKnowledgeImporter {

	int nCovs = 0;
	ArrayList<String> coverages = new ArrayList<String>();
	DataStore data = null;
	private String service;
	private ISession session;
	Properties properties = null;
	
	@Override
	public int getObjectCount() {
		return coverages.size();
	}

	@Override
	public Polylist getObjectDefinition(int i, Properties prop) throws ThinklabException {
		
		Polylist ret = null;
		Properties pr = new Properties();
		pr.putAll(this.properties);
		pr.putAll(prop);
		
		try {
			FeatureSource<SimpleFeatureType, SimpleFeature> source = data
				.getFeatureSource(coverages.get(i));
			
			CoordinateReferenceSystem crs = source.getInfo().getCRS();
			ReferencedEnvelope envelope = source.getInfo().getBounds();
			String crsID = Geospace.getCRSIdentifier(crs, false);

			// need these in properties
			String otype = pr.getProperty("observation.type", "measurement:Ranking");
			String btype = pr.getProperty("observable.type", "observation:GenericObservable");
			String aname = pr.getProperty("observation.attribute");

			/*
			 * normalize envelope for OPAL output
			 */
			envelope = Geospace.normalizeEnvelope(envelope, crs);
			
			/*
			 * build up observation in XML, add to list
			 */
			ret = 
				Polylist.list(
					otype,
					Polylist.list(CoreScience.HAS_OBSERVABLE,
						Polylist.list(btype)),
					Polylist.list(CoreScience.HAS_DATASOURCE,
						Polylist.listNotNull("geospace:WFSDataSource",
							Polylist.list("geospace:hasServiceUrl", service),
							Polylist.list("geospace:hasCoverageId", coverages.get(i)),
							(aname == null ? 
								null :
								Polylist.list("geospace:hasValueAttribute", aname)))),
					Polylist.list("observation:hasObservationExtent",
						Polylist.list("geospace:ArealFeatureSet",
							Polylist.list("geospace:hasLatLowerBound", ""+envelope.getMinimum(1)),
							Polylist.list("geospace:hasLonLowerBound", ""+envelope.getMinimum(0)),
							Polylist.list("geospace:hasLatUpperBound", ""+envelope.getMaximum(1)),
							Polylist.list("geospace:hasLonUpperBound", ""+envelope.getMaximum(0)),
							Polylist.list("geospace:hasCoordinateReferenceSystem", crsID))));
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		return ret;
	}

	@Override
	public String getObjectId(int i) throws ThinklabException {
		return coverages.get(i);
	}

	@Override
	public void initialize(String url, Properties properties) throws ThinklabException {

		this.service = url.toString();
		this.coverages.clear();
		this.properties = properties;
		
		Map<Object,Object> connectionParameters = new HashMap<Object,Object>();
		connectionParameters.put(
				WFSDataStoreFactory.URL.key, 
				this.service + "?request=getCapabilities" );
		connectionParameters.put(
			WFSDataStoreFactory.TIMEOUT.key, 
			10000);
		connectionParameters.put(
			WFSDataStoreFactory.BUFFER_SIZE.key, 
			512);
	
		try {
			this.data = DataStoreFinder.getDataStore(connectionParameters);
			for (Name s : this.data.getNames()) {
				coverages.add(MiscUtilities.getNameFromURL(s.toString()));
			}
			session.getOutputStream().println(
					"Read " + coverages.size() + " feature collections");
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}

	}

}
