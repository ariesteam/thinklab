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
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.geospace.coverage.CoverageFactory;
import org.integratedmodelling.thinklab.interpreter.mvel.MVELExpression;

@Concept("geospace:WFSDataSource")
public class WFSCoverageDataSource extends VectorCoverageDataSource {

	public void initialize(ISemanticObject i) throws ThinklabException {

		Properties p = new Properties();
		p.putAll(Thinklab.get().getProperties());
		ISemanticObject server = i.get(Thinklab.p(NS.GEOSPACE_HAS_SERVICE_URL));
		String covId = i.get(Thinklab.p(NS.GEOSPACE_HAS_COVERAGE_ID)).toString();
		if (server != null)
			p.put(CoverageFactory.WFS_SERVICE_PROPERTY, server.toString());
		p.put(CoverageFactory.COVERAGE_ID_PROPERTY, covId);
		ISemanticObject attr = i.get(Thinklab.p(NS.GEOSPACE_HAS_VALUE_ATTRIBUTE));
		if (attr != null)
			p.put(CoverageFactory.VALUE_ATTRIBUTE_PROPERTY, attr.toString());
		attr = i.get(Thinklab.p(NS.GEOSPACE_HAS_VALUE_TYPE));
		if (attr != null)
			p.put(CoverageFactory.VALUE_TYPE_PROPERTY, attr.toString());
		attr = i.get(Thinklab.p(NS.GEOSPACE_HAS_VALUE_EXPRESSION));
		if (attr != null)
			p.put(CoverageFactory.VALUE_EXPRESSION_PROPERTY, attr.toString());
		attr = i.get(Thinklab.p(NS.GEOSPACE_HAS_VALUE_DEFAULT));
		if (attr != null)
			p.put(CoverageFactory.VALUE_DEFAULT_PROPERTY, attr.toString());
		attr = i.get(Thinklab.p(Geospace.HAS_FILTER_PROPERTY));
		if (attr != null)
			p.put(CoverageFactory.CQL_FILTER_PROPERTY, attr.toString());
		attr = i.get(Thinklab.p(Geospace.HAS_TRANSFORMATION_EXPRESSION));
		if (attr != null)
			this.transformation = new MVELExpression(attr.toString());
	
		URL url;
		try {
			url = new URL(
					(server == null ?  
							"http://127.0.0.1:8080/geoserver/wfs" :  //$NON-NLS-1$
							server.toString()) + 
					"?coverage="  + covId +  //$NON-NLS-1$
					"?VERSION=1.1.0" + //$NON-NLS-1$
					"?attribute=" + (attr == null ? "NONE" : attr.toString())); //$NON-NLS-1$ //$NON-NLS-2$
			
		} catch (MalformedURLException e) {
			throw new ThinklabIOException(e);
		}
		
		this.coverage = CoverageFactory.requireCoverage(url, p);
		this.coverage.setName(covId);
		
	}
}
