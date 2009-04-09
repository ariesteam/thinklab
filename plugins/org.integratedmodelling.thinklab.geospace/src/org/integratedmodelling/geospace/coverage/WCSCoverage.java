package org.integratedmodelling.geospace.coverage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.utils.XMLDocument;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WCSCoverage extends AbstractRasterCoverage {

	public static final String WCS_SERVICE_PROPERTY = "wcs.service.url";
	
	String wcsService = null;
	
	/**
	 * This constructor reads the WCS coverage descriptor and initializes all fields from it. Data are
	 * not loaded until loadData(), so the coverage is null.
	 * 
	 * @param url
	 * @param properties should contain the URL of the WCS service; if null, geoserver on
	 * localhost:8080 is used (not elegant, but OK for now).
	 */
	public WCSCoverage(String coverageID, Properties properties) throws ThinklabException {

		wcsService = properties.getProperty(WCS_SERVICE_PROPERTY, "http://127.0.0.1:8080/geoserver/wcs");
		
		XMLDocument desc = new XMLDocument(buildDescribeUrl(coverageID));
		parseDescriptor(desc);
		
	}
	
	/**
	 * This constructor creates the coverage by reading the WCS coverage passed from the
	 * associated WCS service, reading data only for the specified extent.
	 * 
	 * @param coverage
	 * @param arealExtent
	 */
	public WCSCoverage(WCSCoverage coverage, ArealExtent extent) {
		
	}
	
	private void parseDescriptor(XMLDocument desc) {
		
		// TODO Auto-generated method stub
		// we need at least: CRS, pixel size and bounding box
		
	}

	private URL buildDescribeUrl(String coverageID) throws ThinklabInternalErrorException {

		URL url = null;
		try {
			url = new URL(wcsService +
					"?service=WCS&version=1.0.0&request=DescribeCoverage&identifiers=" +
					coverageID);
		} catch (MalformedURLException e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		return url;
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public VectorCoverage requireCRS(CoordinateReferenceSystem crs)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICoverage requireMatch(ArealExtent arealExtent,
			boolean allowClassChange) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}


}
