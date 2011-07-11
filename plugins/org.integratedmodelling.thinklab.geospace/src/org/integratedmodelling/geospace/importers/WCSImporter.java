package org.integratedmodelling.geospace.importers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.coverage.WCSCoverage;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.interfaces.annotations.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.storage.IKnowledgeImporter;
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
