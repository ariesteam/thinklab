/**
 * WCSGridDataSource.java
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
package org.integratedmodelling.geospace.implementations.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

@InstanceImplementation(concept="geospace:WFSDataSource")
public class WFSCoverageDataSource extends VectorCoverageDataSource {

	public void initialize(IInstance i) throws ThinklabException {

		Properties p = new Properties();
		p.putAll(Geospace.get().getProperties());
		IValue server = i.get("geospace:hasServiceUrl");
		String covId = i.get("geospace:hasCoverageId").toString();
		if (server != null)
			p.put(CoverageFactory.WFS_SERVICE_PROPERTY, server.toString());
		p.put(CoverageFactory.COVERAGE_ID_PROPERTY, covId);
		IValue attr = i.get("geospace:hasValueAttribute");
		if (attr != null)
			p.put(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, attr.toString());
		attr = i.get("geospace:hasValueType");
		if (attr != null)
			p.put(CoverageFactory.VALUE_TYPE_PROPERTY, attr.toString());
		attr = i.get("geospace:hasValueDefault");
		if (attr != null)
			p.put(CoverageFactory.VALUE_DEFAULT_PROPERTY, attr.toString());
		attr = i.get(Geospace.HAS_FILTER_PROPERTY);
		if (attr != null)
			p.put(CoverageFactory.CQL_FILTER_PROPERTY, attr.toString());
		
		URL url;
		try {
			url = new URL(
					(server == null ?  
							"http://127.0.0.1:8080/geoserver/wfs" : 
							server.toString()) + 
					"?coverage="  + covId + 
					"?attribute=" + (attr == null ? "NONE" : attr.toString()));
			
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
		this.coverage = CoverageFactory.requireCoverage(url, p);
		this.coverage.setName(covId);
		
	}
}
