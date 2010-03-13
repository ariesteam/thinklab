/**
 * WCSCoverage.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2009 www.integratedmodelling.org
 * Created: Apr 9, 2009
 *
 * ----------------------------------------------------------------------------------
 * This file is part of thinklab.
 * 
 * thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @copyright 2009 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Apr 9, 2009
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.coverage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.media.jai.iterator.RandomIterFactory;

import org.geotools.coverage.grid.GeneralGridEnvelope;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.corescience.CoreScience;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabInternalErrorException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

public class WCSCoverage extends AbstractRasterCoverage {

	public static final String WCS_SERVICE_PROPERTY = "wcs.service.url";
	public static final String WCS_FORMAT_PROPERTY = "wcs.service.format";
	public static final String WCS_NODATA_PROPERTY = "wcs.service.nodata";
	
	String wcsService = "http://127.0.0.1:8080/geoserver/wcs";
	String wcsFormat = "geotiff";
	
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

		if (properties != null) {
			wcsService = 
				properties.getProperty(WCS_SERVICE_PROPERTY, "http://127.0.0.1:8080/geoserver/wcs");
			wcsFormat =
				properties.getProperty(WCS_FORMAT_PROPERTY, wcsFormat);
			if (properties.containsKey(WCS_NODATA_PROPERTY)) {
				this.noData = new double[1];
				this.noData[0] = Double.parseDouble(properties.getProperty(WCS_NODATA_PROPERTY));
			}
		}
				
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

		  // desc.dump(System.out);

		  Node n = desc.findNode("gml:Envelope");
		  
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
				dimSpecs[0] = XMLDocument.getNodeValue(child).trim();
			  else if (child.getNodeName().equals("gml:high"))
				dimSpecs[1] = XMLDocument.getNodeValue(child).trim();
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
		  
		  /*
		   * TODO process available formats and extract default or validate given
		   */
		  try {
			  this.crs = Geospace.getCRSFromID(srs);
			} catch (Exception e) {
				Geospace.get().logger().error(layerName + ": " + e.getMessage());
			};


		  /* read no data values. FIXME: limited to one SingleValue spec */
		  n = desc.findNode("nullValues");
		  if (n != null)  {
			  
			  next = (Node)n.getFirstChild();
			  while ((child = next) != null) {
				  next = child.getNextSibling(); 
				  if (child.getNodeName().equals("singleValue")) {
					  this.noData = new double[1];
					  this.noData[0] = 
						  Double.parseDouble(XMLDocument.getNodeValue(child).toString());
				  }
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
				String kw = XMLDocument.getNodeValue(child).trim();
				String[] zoz = kw.split("\\ ");
				for (String kz : zoz) {
					if (kz.contains("=")) {
						String[] kvp = kz.split("=");
						if (kvp.length == 2)
							properties.put(kvp[0], kvp[1]);
					}
				}
			  }
		  }
		  
		  this.xCellSize = (x2 - x1)/(sx2 - sx1);
		  this.yCellSize = (y2 - y1)/(sy2 - sy1);
			
		  this.boundingBox = new ReferencedEnvelope(x1, x2, y1, y2, crs);

		  this.gridGeometry = 
				new GridGeometry2D(
					new GeneralGridEnvelope( new int[] {sx1, sy1}, new int[] {sx2, sy2}, false), 
					boundingBox);

	}

	private URL buildDescribeUrl(String coverageId) throws ThinklabInternalErrorException {

		URL url = null;
		try {
			url = new URL(wcsService +
					"?service=WCS&version=1.0.0&request=DescribeCoverage&coverage=" +
					coverageId);
		} catch (MalformedURLException e) {
			throw new ThinklabInternalErrorException(e);
		}
				
		return url;
	}
	
	private URL buildRetrieveUrl(GridExtent extent) throws ThinklabException {
		
		URL url = null;
		String rcrs = Geospace.getCRSIdentifier(extent.getCRS(), false);
		
		int xc = extent.getXCells();
		int yc = extent.getYCells();
		
		if (extent.getXCells() == 0 && extent.getYCells() == 0) {
			xc = (int) ((extent.getEast() - extent.getWest())/this.xCellSize);
			yc = (int) ((extent.getNorth() - extent.getSouth())/this.yCellSize);
			
			System.out.println("computed raster size is " + xc + " x " + yc);
			extent.setResolution(xc, yc);
		}
		
		String s = 
			wcsService + 
			"?service=WCS&version=1.0.0&request=GetCoverage&coverage=" +
			layerName +
			"&bbox=" + 
			extent.getWest() +
			"," +
			extent.getSouth() +
			"," +
			extent.getEast() +
			"," +
			extent.getNorth() +
			"&crs=" + 
			rcrs +
			"&responseCRS=" +
			rcrs +
			"&width=" +
			xc +
			"&height=" +
			yc +
			"&format=" +
			wcsFormat;
		
		try {
			url = new URL(s);
		} catch (MalformedURLException e) {
			throw new ThinklabInternalErrorException(e);
		}

		// System.out.println("WCS URL: " + url);

		return url;
	}

	@Override
	public void loadData() throws ThinklabException {
		
		checkCoverage();

		/*
		 * get rid of old image if we had one
		 */
		if (image != null) {
			image = null;
		}
		
		image = coverage.getRenderedImage();
		itera = RandomIterFactory.create(image, null);
		

	}

	private void checkCoverage() throws ThinklabException {
		
		if (coverage == null) {
			/*
			 * TODO we should read it as is, but typically we don't care for downloading a gigabyte
			 * from the net, so the case with coverage == null is normally an error - no specification
			 * of a subrange has been asked. For now let's just throw an error.
			 */
			throw new ThinklabInappropriateOperationException(
					"WCS coverage " + layerName + " being read in its entirety without subsetting: " +
					"this is most likely an error causing extremely long download times.");
		}
	}
	
	@Override
	public ICoverage requireMatch(ArealExtent arealExtent, boolean allowClassChange) throws ThinklabException {
		
		if (! (arealExtent instanceof GridExtent))
			throw new ThinklabValidationException("coverage can only be reprojected on a grid extent for now");
		
		URL getCov = buildRetrieveUrl((GridExtent) arealExtent);

		ClassLoader clsl = null;
		
		try {

			clsl = Geospace.get().swapClassloader();
			
			File f = File.createTempFile("geo", ".tiff");
			CopyURL.copy(getCov, f);
			getCov = f.toURI().toURL();
			GeoTiffReader reader = new GeoTiffReader(getCov, 
					Geospace.get().getGeotoolsHints());	
			this.coverage = (GridCoverage2D)reader.read(null);	
			
		} catch (IOException e) {
			throw new ThinklabIOException(layerName + ": " + e.getMessage());
		} finally {
			Geospace.get().resetClassLoader(clsl);
		}

		setExtent((GridExtent) arealExtent);
		
		return this;
	}

	private void setExtent(GridExtent e) {

		  this.xCellSize = e.getEWResolution();
		  this.xCellSize = e.getNSResolution();
			
		  this.boundingBox = e.getNormalizedEnvelope();

		  this.gridGeometry = 
				new GridGeometry2D(
					new GeneralGridEnvelope( 
							new int[] {e.getXMinCell(), e.getYMinCell()}, 
							new int[] {e.getXMaxCell(), e.getYMaxCell()}, false), 
					boundingBox);
	}

	/**
	 * Utility method to create an observation descriptor for this coverage as an observation,
	 * using metadata and geographical descriptors.
	 * @param out
	 * @throws ThinklabPluginException 
	 */
	public void addOpalDescriptor(XMLDocument doc, Node root) throws ThinklabException {
		
		String obsClass =
			properties.getProperty("observation.type", "measurement:Ranking");
		String observable =  
			properties.getProperty("observable.type", "observation:GenericQuantifiable");
		
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

	public Polylist asObservation(Properties prop) throws ThinklabException {

		// need these in properties
		String otype = prop.getProperty("observation.type", "measurement:Ranking");
		String btype = prop.getProperty("observable.type", "observation:GenericObservable");
		
		return 
			Polylist.list(
				otype,
				Polylist.list(CoreScience.HAS_OBSERVABLE,
					Polylist.list(btype)),
				Polylist.list(CoreScience.HAS_DATASOURCE,
					Polylist.list("geospace:WCSDataSource",
						Polylist.list("geospace:hasServiceUrl", wcsService),
						Polylist.list("geospace:hasCoverageId", layerName))),
				Polylist.list("observation:hasObservationExtent",
					Polylist.list("geospace:RasterGrid",
						Polylist.list("geospace:hasXRangeMax", ""+gridGeometry.getGridRange2D().getHigh(0)),
						Polylist.list("geospace:hasYRangeMax", ""+gridGeometry.getGridRange2D().getHigh(1)),
						Polylist.list("geospace:hasLatLowerBound", ""+boundingBox.getMinimum(1)),
						Polylist.list("geospace:hasLonLowerBound", ""+boundingBox.getMinimum(0)),
						Polylist.list("geospace:hasLatUpperBound", ""+boundingBox.getMaximum(1)),
						Polylist.list("geospace:hasLonUpperBound", ""+boundingBox.getMaximum(0)),
						Polylist.list("geospace:hasCoordinateReferenceSystem", 
								Geospace.getCRSIdentifier(crs, false)))));
	}
}
