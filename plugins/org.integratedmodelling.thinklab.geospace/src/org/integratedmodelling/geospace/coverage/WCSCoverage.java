package org.integratedmodelling.geospace.coverage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.XMLDocument;
import org.w3c.dom.Node;

public class WCSCoverage extends AbstractRasterCoverage {

	public static final String WCS_SERVICE_PROPERTY = "wcs.service.url";
	
	String wcsService = "http://127.0.0.1:8080/geoserver/wcs";
	String description = null;
	// read from kvp in coverage keywords
	Properties properties = new Properties();
	
	/**
	 * This constructor reads the WCS coverage descriptor and initializes all fields from it. Data are
	 * not loaded until loadData(), so the coverage is null.
	 * 
	 * @param url
	 * @param properties should contain the URL of the WCS service; if null, geoserver on
	 * localhost:8080 is used (not elegant, but OK for now).
	 */
	public WCSCoverage(String coverageID, Properties properties) throws ThinklabException {

		if (properties != null)
			wcsService = 
				properties.getProperty(WCS_SERVICE_PROPERTY, "http://127.0.0.1:8080/geoserver/wcs");

		layerName = coverageID;
		
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
		layerName = coverage.layerName;
	}
	
	private void parseDescriptor(XMLDocument desc) throws ThinklabException {

		  String[] dimSpecs = new String[2];

		  // TODO the only check we make is that the first <name> node (depth-first) 
		  // is the layer name we requested.
		  Node n = desc.findNode("name");

		  if (n == null || !XMLDocument.getNodeValue(n).trim().equals(layerName))
			  throw new ThinklabResourceNotFoundException(
					  "coverage " + layerName + " not found on WCS service");

		  n = desc.findNode("gml:Envelope");
		  
		  String srs = XMLDocument.getAttributeValue(n, "srsName").trim();
		  
		  // TODO read metadata: description, label, keywords
		  
		  int i = 0; Node child; Node next = (Node)n.getFirstChild();
		  while ((child = next) != null) {
				 
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("gml:pos"))
				dimSpecs[i++] = XMLDocument.getNodeValue(child);
		  }
		  
		  
		  /*
		   * process dims
		   */
		  double x1, y1, x2, y2;
		  String[] z = dimSpecs[0].split("\\ ");  
		  x1 = Double.parseDouble(z[0]);
		  y1 = Double.parseDouble(z[1]);
		  z = dimSpecs[1].split("\\ ");  
		  x2 = Double.parseDouble(z[0]);
		  y2 = Double.parseDouble(z[1]);
		  
		  n = desc.findNode("gml:GridEnvelope");
		  i = 0; next = (Node)n.getFirstChild();
		  while ((child = next) != null) {
				 
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("gml:low"))
				dimSpecs[0] = XMLDocument.getNodeValue(child);
			  else if (child.getNodeName().equals("gml:high"))
				dimSpecs[1] = XMLDocument.getNodeValue(child);
		  }
		  
		  /*
		   * process pixel size
		   */
		  int sx1, sy1, sx2, sy2;
		  z = dimSpecs[0].split("\\ ");  
		  sx1 = Integer.parseInt(z[0]);
		  sy1 = Integer.parseInt(z[1]);
		  z = dimSpecs[1].split("\\ ");  
		  sx2 = Integer.parseInt(z[0]);
		  sy2 = Integer.parseInt(z[1]);
		  
		  try {
			  this.crs = CRS.decode(srs);
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			};


		  /* read no data values. FIXME: limited to one SingleValue spec */
		  n = desc.findNode("nullValues");
		  next = (Node)n.getFirstChild();
		  while ((child = next) != null) {
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("SingleValue")) {
				  this.noData = new double[1];
				  this.noData[0] = 
					  Double.parseDouble(XMLDocument.getNodeValue(child).toString());
			  }
		  }

		  /*
		   * keywords: recognize KVP and instantiate properties from them
		   */
		  n = desc.findNode("keywords");
		  i = 0; next = (Node)n.getFirstChild();
		  while ((child = next) != null) {
				 
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("keyword")) {
				String kw = XMLDocument.getNodeValue(child);
				if (kw.contains("=")) {
					String[] kvp = kw.split("=");
					if (kvp.length == 2)
						properties.put(kvp[0], kvp[1]);
				}
			  }
		  }
		  
		  this.xCellSize = (x2 - x1)/(sx2 - sx1);
		  this.xCellSize = (y2 - y1)/(sy2 - sy1);
			
		  this.boundingBox = new ReferencedEnvelope(x1, x2, y1, y2, crs);

		  this.gridGeometry = 
				new GridGeometry2D(
					new GeneralGridEnvelope( new int[] {sx1,sy1}, new int[] {sx2, sy2}, false), 
					boundingBox);

	}

	private URL buildDescribeUrl(String coverageID) throws ThinklabInternalErrorException {

		URL url = null;
		try {
			url = new URL(wcsService +
					"?service=WCS&version=1.0.0&request=DescribeCoverage&coverage=" +
					coverageID);
		} catch (MalformedURLException e) {
			throw new ThinklabInternalErrorException(e);
		}
		
		return url;
	}

	@Override
	public void loadData() {

		if (coverage == null) {
			
			/*
			 * place call to read data from WCS and read coverage
			 */
		}
	}


	@Override
	public ICoverage requireMatch(ArealExtent arealExtent,
			boolean allowClassChange) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	public static void main(String args[]) {
		
		try {
			
			WCSCoverage cov = new WCSCoverage("puget:NCLD_King", null);
			
			
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * Utility method to create an observation descriptor for this coverage as an observation,
	 * using metadata and geographical descriptors.
	 * @param out
	 * @throws ThinklabPluginException 
	 */
	public void addOpalDescriptor(XMLDocument doc, Node root) throws ThinklabPluginException {
		
		String obsClass =
			properties.getProperty("observation-class", "observation:Ranking");
		String observable =  
			properties.getProperty("observable-class", "observation:GenericQuantifiable");
		
		Node obs = doc.appendTextNode(obsClass, null, root);
		doc.addAttribute(obs, "id", layerName);
		Node oop = doc.appendTextNode("observation:hasObservable", null, obs);
		doc.appendTextNode(observable, null, oop);
		
		Node dsp = doc.appendTextNode("observation:hasDataSource", null, obs);
		Node dsc = doc.appendTextNode("geospace:WCSDataSource", null, dsp);
		doc.appendTextNode("geospace:hasCoverageId", layerName, dsc);
		
		Node esp = doc.appendTextNode("observation:hasObservationExtent", null, obs);
		Node esc = doc.appendTextNode("geospace:RasterGrid", null, esp);
		doc.appendTextNode("geospace:hasXRangeMin", ""+gridGeometry.getGridRange2D().getLow(0), esc);
		doc.appendTextNode("geospace:hasYRangeMin", ""+gridGeometry.getGridRange2D().getLow(1), esc);
		doc.appendTextNode("geospace:hasXRangeMax", ""+gridGeometry.getGridRange2D().getHigh(0), esc);
		doc.appendTextNode("geospace:hasYRangeMax", ""+gridGeometry.getGridRange2D().getHigh(1), esc);
		doc.appendTextNode("geospace:hasLatLowerBound", ""+boundingBox.getMinimum(1), esc);
		doc.appendTextNode("geospace:hasLonLowerBound", ""+boundingBox.getMinimum(0), esc);
		doc.appendTextNode("geospace:hasLatUpperBound", ""+boundingBox.getMaximum(1), esc);
		doc.appendTextNode("geospace:hasLonUpperBound", ""+boundingBox.getMaximum(0), esc);
		doc.appendTextNode("geospace:hasCoordinateReferenceSystem", 
				Geospace.getCRSIdentifier(crs, false), esc);
		
	}
}
