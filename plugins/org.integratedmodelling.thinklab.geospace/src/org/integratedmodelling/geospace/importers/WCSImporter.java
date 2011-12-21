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
package org.integratedmodelling.geospace.importers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.integratedmodelling.geospace.coverage.WCSCoverage;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.storage.IKnowledgeImporter;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@KnowledgeLoader(format="wcs")
public class WCSImporter implements IKnowledgeImporter {

	ArrayList<String> coverages = new ArrayList<String>();
	Properties properties = null;
	
	@Override
	public int getObjectCount() {
		return coverages.size();
	}

	@Override
	public Polylist getObjectDefinition(int i, Properties prop)
			throws ThinklabException {
		
		Properties pr = new Properties();
		pr.putAll(this.properties);
		pr.putAll(prop);
		WCSCoverage coverage = new WCSCoverage(coverages.get(i), pr);
		return coverage.asObservation(properties);
	}

	@Override
	public String getObjectId(int i) throws ThinklabException {
		return coverages.get(i);
	}

	@Override
	public void initialize(String url, Properties properties)
			throws ThinklabException {

		String server = url.toString();
		this.properties = properties;
		
		XMLDocument cap = null;
		
		try {
			 cap = 
				 new XMLDocument(
					new URL(server + "?service=WCS&version=1.0.0&request=getCapabilities"));
		} catch (MalformedURLException e) {
			throw new ThinklabValidationException(e);
		}
		
		Node n = cap.findNode("ContentMetadata");
		parseMetadata(n, server);
	}


	private void parseMetadata(Node n, String server) throws ThinklabException {

		Properties p = new Properties();
		
		p.put(WCSCoverage.WCS_SERVICE_PROPERTY, server);
		  Node child; Node next = (Node)n.getFirstChild();
		  while ((child = next) != null) {	 
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("CoverageOfferingBrief")) {
				  String covId = XMLDocument.getTextValue((Element) child, "name");
				  coverages.add(covId);
			  }
		  }
	}

}
