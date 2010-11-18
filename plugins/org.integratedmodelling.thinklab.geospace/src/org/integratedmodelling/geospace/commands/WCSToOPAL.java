/**
 * GISToOPAL.java
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
package org.integratedmodelling.geospace.commands;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.coverage.WCSCoverage;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Turn a WCS capabilities document into an OPAL file for editing.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
public class WCSToOPAL implements ICommandHandler {

	public IValue execute(Command command, ISession session) throws ThinklabException {

		String server = command.getArgumentAsString("server");
		String output = command.getArgumentAsString("output");
		String match  = command.getArgumentAsString("match");
		
		if (match.equals("_NONE_"))
			match = null;
		
		int nCovs = 0;
		
		XMLDocument cap = null;
		XMLDocument out = new XMLDocument("kbox");
		
		out.addNamespace("observation", "http://www.integratedmodelling.org/ks/science/observation.owl");
		out.addNamespace("geospace", "http://www.integratedmodelling.org/ks/geospace/geospace.owl");
		
		
		try {
			 cap = 
				 new XMLDocument(
					new URL(server + "?service=WCS&version=1.0.0&request=getCapabilities"));
		} catch (MalformedURLException e) {
			throw new ThinklabValidationException(e);
		}
		
		Node n = cap.findNode("ContentMetadata");
		nCovs = parseMetadata(n, out, server, match);
		out.writeToFile(new File(output));
		
		session.getOutputStream().println(
				nCovs + 
				" coverages written to "
				+ output);
		
		return null;
	}

	private int parseMetadata(Node n, XMLDocument out, String server, String match) throws ThinklabException {

		Properties p = new Properties();
		
		p.put(WCSCoverage.WCS_SERVICE_PROPERTY, server);
		  int i = 0; Node child; Node next = (Node)n.getFirstChild();
		  while ((child = next) != null) {
				 
			  next = child.getNextSibling(); 
			  if (child.getNodeName().equals("CoverageOfferingBrief")) {
				  
				  String covId = XMLDocument.getTextValue((Element) child, "name");
				  if (match != null && !covId.startsWith(match))
					  continue;
				  
				  WCSCoverage coverage = new WCSCoverage(covId, p);
				  coverage.addOpalDescriptor(out, out.root());
				  i++;
			  }
		  }
		  
		  return i;
	}
}
