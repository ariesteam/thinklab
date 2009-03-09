/**
 * CoverageHandler.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
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
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.coverage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import org.integratedmodelling.geospace.ISpatialDataImporter;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.PropertiesTemplateAdapter;

public abstract class CoverageHandler  implements ISpatialDataImporter {

	URL sourceURL = null;
	Properties properties;
	ArrayList<ICoverage> coverages = null;
	private String layerName;
	private PropertiesTemplateAdapter tengine = null;
	ArrayList<Polylist> olist = null;


	private Properties getDefaultProperties() throws ThinklabIOException {

		/* easy: lookup a kbox file in the same directory as the shapefile */
		String urlkb = MiscUtilities.changeExtension(sourceURL.toString(), "kbox");
		InputStream input = MiscUtilities.getInputStreamForResource(urlkb);
		
		Properties ret = new Properties();
		
		if (input != null)
			try {
				ret.load(input);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		
		return ret;
	}
	
	public CoverageHandler(URL url, Properties properties) throws ThinklabException {
		
		sourceURL = url;
		
		Properties p = getDefaultProperties();
		
		if (properties == null)
			properties = p;
		else
			properties.putAll(p);
		
		this.properties = properties;	
		layerName = MiscUtilities.getURLBaseName(url.toString()).toLowerCase();
		
		tengine  = new PropertiesTemplateAdapter(getProperties(), "geospace." + layerName + ".variable");

		coverages = CoverageFactory.readResource(url, properties);

	}

	public String getLayerName() {
		return layerName;
	}

	public Properties getProperties() {
		return properties;
	}
	
	public ArrayList<Polylist> getInstanceLists() {
		return olist;
	}

	public int process() throws ThinklabException {

		if (coverages != null) {
			
			for (ICoverage coverage : coverages) {
				notifyCoverage(coverage, properties);
			}
		}
		return coverages == null ? 0 : coverages.size();
	}
	
	public void notifyCoverage(ICoverage coverage, Properties properties)
			throws ThinklabException {
		
		olist = new ArrayList<Polylist>();
		
		/* produce all necessary variables for macro substitution */
		Hashtable<String, Object> vmap = new Hashtable<String,Object>();
		
		vmap.put("latLowerBound", coverage.getLatLowerBound());
		vmap.put("latUpperBound", coverage.getLatUpperBound());
		vmap.put("lonLowerBound", coverage.getLonLowerBound());
		vmap.put("lonUpperBound", coverage.getLonUpperBound());
		vmap.put("crsCode",       coverage.getCoordinateReferenceSystemCode());
		vmap.put("coverageUrl",   coverage.getSourceUrl());
		vmap.put("layerName",     coverage.getLayerName());
		
		if (coverage instanceof RasterCoverage) {
			vmap.put("xRangeOffset",  ((RasterCoverage)coverage).getXRangeOffset());
			vmap.put("xRangeMax",     ((RasterCoverage)coverage).getXRangeMax());
			vmap.put("yRangeOffset",  ((RasterCoverage)coverage).getYRangeOffset());
			vmap.put("yRangeMax",     ((RasterCoverage)coverage).getYRangeMax());
			vmap.put("minDataValue",  ((RasterCoverage)coverage).getMinDataValue());
			vmap.put("maxDataValue",  ((RasterCoverage)coverage).getMaxDataValue());
			vmap.put("noDataValue",  ((RasterCoverage)coverage).getNoDataValue());
		}
		
		/* substitute into lists */
		ArrayList<Pair<String, Polylist>> ilists = 
			tengine.computeListsFromTemplates("geospace." + getLayerName() + ".instance.template", vmap);
		
		for (Pair<String, Polylist> t : ilists) {	
				olist.add(t.getSecond());
		}
	}

	public void initialize(URL url, Properties properties)
			throws ThinklabException {
		// TODO Auto-generated method stub
		
	}


	
}
