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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveSubcommandInterface;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.XMLDocument;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.w3c.dom.Node;

/**
 * Turn a WCS capabilities document into an OPAL file for editing.
 * 
 * @author Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 */
@ThinklabCommand(
	name="wfs2opal",
	argumentNames="server",
	argumentDescriptions="WFS server URL",
	argumentTypes="thinklab-core:Text")
public class WFSToOPAL extends InteractiveSubcommandInterface {

	int nCovs = 0;
	ArrayList<String> coverages = new ArrayList<String>();
	DataStore data = null;
	private String service;
	XMLDocument doc = null;
	private ISession session;
	
	public IValue execute(Command command, ISession session) throws ThinklabException {

		this.service = command.getArgumentAsString("server");
		this.doc = new XMLDocument("kbox");
		this.coverages.clear();
		this.session = session;
		
		doc.addNamespace("observation", "http://www.integratedmodelling.org/ks/science/observation.owl");
		doc.addNamespace("measurement", "http://www.integratedmodelling.org/ks/science/measurement.owl");
		doc.addNamespace("geospace", "http://www.integratedmodelling.org/ks/geospace/geospace.owl");

		String covid  = null;
		if (command.hasArgument("coverage"))
			covid = command.getArgumentAsString("coverage");
		
		Map<Object,Object> connectionParameters = new HashMap<Object,Object>();
		connectionParameters.put(
				WFSDataStoreFactory.URL.key, 
				this.service + "?request=getCapabilities" );
		connectionParameters.put(
			WFSDataStoreFactory.TIMEOUT.key, 
			10000);
		connectionParameters.put(
			WFSDataStoreFactory.BUFFER_SIZE.key, 
			512);
	
		try {
			this.data = DataStoreFinder.getDataStore(connectionParameters);
			for (Name s : this.data.getNames()) {
				coverages.add(MiscUtilities.getNameFromURL(s.toString()));
			}
			session.getOutputStream().println(
					"Read " + coverages.size() + " feature collections");
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return super.execute(command, session);
	}



	@Override
	protected IValue cmd(String cmd, String[] arguments)
			throws ThinklabException {
		
		if (cmd.equals("list")) {
			for (int i = 0; i < coverages.size(); i++) {
				say(i+1 + ". " + coverages.get(i));
			}
		} else if (cmd.equals("info")) {
			info(Integer.parseInt(arguments[1])-1);
		} else if (cmd.equals("annotate")) {
			annotate(Integer.parseInt(arguments[1])-1);
		} else if (cmd.equals("write")) {
			write(arguments.length < 2 ? null : arguments[1]);
		}
 		
		return null;
	}

	private void annotate(int cov) throws ThinklabException {

		try {
			FeatureSource<SimpleFeatureType, SimpleFeature> source = data
				.getFeatureSource(coverages.get(cov));
			
			CoordinateReferenceSystem crs = source.getInfo().getCRS();
			ReferencedEnvelope envelope = source.getInfo().getBounds();
			String crsID = Geospace.getCRSIdentifier(crs, false);

			String ans = null;
			ans = ask("observation type [measurement:Ranking]? ");
			String otype = ans == null ? "measurement:Ranking" : ans;
			ans = ask("observable type [observation:GenericObservable]? ");
			String btype = ans == null ? "observation:GenericObservable" : ans;
			
			ArrayList<String> anames = new ArrayList<String>();
			say("Attributes:");
			say("  0. no attribute (ranking only: present = 1, absent = 0)");

			int i = 1;
			for (AttributeDescriptor ad : source.getSchema().getAttributeDescriptors()) {
				say ("  " + i++ + ". " + ad.getLocalName() + ": " + 
						ad.getType().getBinding().getSimpleName());
				
				anames.add(ad.getLocalName());
			}
			
			int n = Integer.parseInt(ask("Attribute to use? "));
			String aname = n == 0 ? null : anames.get(n-1);

			/*
			 * normalize envelope for OPAL output
			 */
			envelope = Geospace.normalizeEnvelope(envelope, crs);
			
			/*
			 * build up observation in XML, add to list
			 */
			Node obs = doc.appendTextNode(otype, null, doc.root());
			doc.addAttribute(obs, "id", coverages.get(cov).replace(':', '_'));
			Node oop = doc.appendTextNode("observation:hasObservable", null, obs);
			doc.appendTextNode(btype, null, oop);
			
			Node dsp = doc.appendTextNode("observation:hasDataSource", null, obs);
			Node dsc = doc.appendTextNode("geospace:WFSDataSource", null, dsp);
			doc.appendTextNode("geospace:hasServiceUrl", service, dsc);
			doc.appendTextNode("geospace:hasCoverageId", coverages.get(cov), dsc);
			if (aname != null) {
				doc.appendTextNode("geospace:hasValueAttribute", aname, dsc);
			}
			Node esp = doc.appendTextNode("observation:hasObservationExtent", null, obs);
			Node esc = doc.appendTextNode("geospace:ArealFeatureSet", null, esp);
			doc.appendTextNode("geospace:hasLatLowerBound", ""+envelope.getMinimum(1), esc);
			doc.appendTextNode("geospace:hasLonLowerBound", ""+envelope.getMinimum(0), esc);
			doc.appendTextNode("geospace:hasLatUpperBound", ""+envelope.getMaximum(1), esc);
			doc.appendTextNode("geospace:hasLonUpperBound", ""+envelope.getMaximum(0), esc);
			doc.appendTextNode("geospace:hasCoordinateReferenceSystem", crsID, esc);			

		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	private void write(String string) throws ThinklabIOException {
		if (string == null) 
			doc.dump(session.getOutputStream());
		else
			doc.writeToFile(new File(string));
	}

	private void info(int cov) throws ThinklabException {
		
		say("ID: " + coverages.get(cov));
		
		try {
			FeatureSource<SimpleFeatureType, SimpleFeature> source = data
				.getFeatureSource(coverages.get(cov));
			
			say("Boundaries: " + source.getInfo().getBounds());
			say("CRS: " + Geospace.getCRSIdentifier(source.getInfo().getCRS(), false));
			say("Description: " + source.getInfo().getDescription());
			say("Keywords: " + source.getInfo().getKeywords());
			;
			say("Attributes:");
			int i = 1;
			for (AttributeDescriptor ad : source.getSchema().getAttributeDescriptors()) {
				say ("  " + i++ + ". " + ad.getLocalName() + ": " + 
						ad.getType().getBinding().getSimpleName());
				
			}
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}
}