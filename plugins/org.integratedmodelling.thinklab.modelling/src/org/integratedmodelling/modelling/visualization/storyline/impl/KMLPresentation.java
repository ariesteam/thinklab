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
package org.integratedmodelling.modelling.visualization.storyline.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate.Page;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.Escape;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.xml.XML;
import org.integratedmodelling.utils.xml.XML.XmlNode;

public class KMLPresentation implements IPresentation {
	
	private Storyline storyline;
	private File kmzFile;
	
	public KMLPresentation(File kmzFile) {
		this.kmzFile = kmzFile;
	}
	
	public KMLPresentation() {
	}

	@Override
	public void render() throws ThinklabException {
		
		File file = null;
		File tempdir = MiscUtilities.createTempDir();
		
		String xmlFile = 
			tempdir + 
			File.separator + 
			MiscUtilities.getFileBaseName(file.toString()) + ".kml";
		
		XML.document("xmlns=http://earth.google.com/kml/2.0",
				XML.node("kml", 
						XML.node("Document", 
								makeFolder(this.storyline, tempdir)))).
			writeToFile(new File(xmlFile));
	
		// zip the whole thing into the passed file, check extension is .kmz
		file = new File(MiscUtilities.changeExtension(file.toString(), "kmz"));
		FolderZiper.zipSubFolders(tempdir.toString(), file.toString());
		MiscUtilities.deleteDirectory(tempdir);
	}

	private XML.XmlNode makeFolder(Storyline storyline, File tempdir) throws ThinklabException {
		
		FileVisualization vis = null;
		
		if (storyline.getVisualization() instanceof FileVisualization)
			vis = (FileVisualization) storyline.getVisualization();
		
		ReferencedEnvelope env = null;
		if (storyline.getContext() != null) {
			ArealExtent extent = 
				(ArealExtent) storyline.getContext().getSpace();
			try {
				env = extent.getNormalizedEnvelope().
					transform(Geospace.get().getStraightGeoCRS(), true);
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
		}
		
		ArrayList<XML.XmlNode> nodes = new ArrayList<XML.XmlNode>();
		
		StorylineTemplate.Page info = storyline.getTemplate().getPage("info");
		if (info != null) {
			nodes.add(XML.node("name", info.getName()));
			nodes.add(XML.node("description", 
					Escape.forHTML(info.getHtml("description"))));
		}
		
		for (Storyline s : storyline.getChildren()) {
			nodes.add(makeFolder(s, tempdir));
		}
		
		for (StorylineTemplate.Page p : storyline.getTemplate().getPages()) {
			
			if (p.getBoolean("disabled")) {
				continue;
			}

			if (vis != null && env != null) {
				
				Collection<File> files = vis.getStateImages(p.getConcept());

				for (File f : files)
					nodes.add(makeOverlay(env, f, p, tempdir));
			}
			
		}
		
		return XML.node("Folder", nodes);
	
	}

	private XmlNode makeOverlay(ReferencedEnvelope env, File f, Page p, File tempdir) throws ThinklabException {
				
		/* extract these from grid in WSG84 normalized */
		double south = env.getMinY();
		double north = env.getMaxY();
		double east  = env.getMaxX();
		double west  = env.getMinX();

		/*
		 * copy image file in temp directory
		 */
		
		String fname = MiscUtilities.getFileName(f.toString());
		File fd = new File(tempdir + File.separator + fname);
		CopyURL.copy(f, fd);

		return 	
			XML.node("GroundOverlay", 
				XML.node("name", p.getName()),
				XML.node("description", 
						Escape.forHTML(p.getHtml("description"))),
				XML.node("Icon",
					XML.node("href", fname)),
				XML.node("LatLonBox",
					XML.node("north", north+""),
					XML.node("south", south+""),
					XML.node("east",  east+""),
					XML.node("west",  west+""))).
						attr("id", p.getName());

	}

	@Override
	public void initialize(Storyline storyline, Properties properties) {
		this.storyline = storyline;
		if (properties != null && kmzFile == null) {
			String p = properties.getProperty("file");
			if (p != null)
				kmzFile = new File(p);
		}
		
		if (kmzFile == null)
			throw new ThinklabRuntimeException("cannot create KML: no output file specified");
	}

	@Override
	public void render(IConcept concept) throws ThinklabException {

		
	}
	

}
