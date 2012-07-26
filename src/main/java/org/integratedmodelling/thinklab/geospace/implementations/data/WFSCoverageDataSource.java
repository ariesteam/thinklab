/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.geospace.implementations.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.geospace.coverage.CoverageFactory;

@Concept("geospace:WFSDataSource")
public class WFSCoverageDataSource extends VectorCoverageDataSource {

	/**
	 * WFS service URL
	 */
	String server;
	
	/**
	 * Coverage ID.
	 */
	String covId;
	
	/**
	 * attribute containing the data we want. If no attribute, it's 0/1 for
	 * presence/absence.
	 */
	String attr;
	
	/**
	 * CQL expression to filter features if requested
	 */
	String filter;
	
	Properties properties = new Properties();
	boolean initialized = false;
	
	/**
	 * 
	 * @param service the WFS service URL. Cannot be null.
	 * @param id the coverage ID. 
	 * @param attribute
	 * @param filter
	 * @param valueType
	 * @param valueDefault
	 */
	public WFSCoverageDataSource(
			String service, String id, 
			String attribute, 
			String filter,
			String valueType,
			String valueDefault) {

		this.server = service;
		this.covId = id;
		this.attr = attribute;
		this.filter = filter;

		
		properties.put(CoverageFactory.WFS_SERVICE_PROPERTY, server);
		properties.put(CoverageFactory.COVERAGE_ID_PROPERTY, covId);
		
		if (attr != null)
			properties.put(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, attr);
		if (valueType != null)
			properties.put(CoverageFactory.VALUE_TYPE_PROPERTY, valueType);
		if (valueDefault != null)
			properties.put(CoverageFactory.VALUE_DEFAULT_PROPERTY, valueDefault);
		if (filter != null)
			properties.put(CoverageFactory.CQL_FILTER_PROPERTY, filter);
	}
	
	@Override
	public void initialize() throws ThinklabException {
		
		if (!initialized) {
		
			/*
			 * TODO check if we still want that - seems ugly
			 */
			properties.putAll(Thinklab.get().getProperties());
	
			URL url;
			try {
				url = new URL(
					(server == null ?  
							"http://127.0.0.1:8080/geoserver/wfs" :  
							server) + 
					"?coverage="  + covId +  
					"?VERSION=1.1.0" + 
					"?attribute=" + (attr == null ? "NONE" : attr.toString()));
			
			} catch (MalformedURLException e) {
				throw new ThinklabIOException(e);
			}
		
			this.coverage = CoverageFactory.requireCoverage(url, properties);
			this.coverage.setName(covId);
		
			initialized = true;
		}
	}
}
