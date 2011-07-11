/**
 * WFSInstanceImporter.java
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
package org.integratedmodelling.geospace.feature;

import java.io.Serializable;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;

import org.geotools.data.DataStore;
import org.geotools.data.wfs.WFSDataStore;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.list.Polylist;

/**
 * Convert a shapefile into an ontology of observations of a given class, converting the
 * attributes to data properties and the spatial representation into the corresponding 
 * spatial context. Output the converted specification as basic OPAL.
 * 
 * @author UVM Affiliate
 *
 */
public class WFSInstanceImporter extends InstanceFeatureImporter {

	Hashtable<String, Serializable> variables = new Hashtable<String, Serializable>();
	WFSDataStore shapefile = null;
	
	public WFSInstanceImporter(URL url)
			throws ThinklabException {
		super(url, null);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		
	}


	@Override
	protected DataStore getDataStore() {
		return shapefile;
	}

	@Override
	public void initialize(URL url, Properties properties) throws ThinklabException {
		
//		try {
			shapefile = 
// TODO must use DataStore factory
				//				new WFSDataStore(
//						url, 
//						null, 
//						properties.getProperty("wfs.server.username"),
//						properties.getProperty("wfs.server.password"),
//						3000,
//						10,
//						true);
			null;
//		} catch (MalformedURLException e) {
//			throw new ThinklabIOException(e);
//		}
		super.initialize(url, properties);
	}


	@Override
	public void notifyInstance(Polylist list) {
		// TODO Auto-generated method stub
		
	}

}
