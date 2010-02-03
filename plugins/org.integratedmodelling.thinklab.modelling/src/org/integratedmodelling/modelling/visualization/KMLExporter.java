package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.FolderZiper;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.xml.XML;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

//<?xml version="1.0" encoding="UTF-8"?>
//<kml xmlns="http://earth.google.com/kml/2.0">
//<GroundOverlay>
//<name>viewsink</name>
//<Icon>
//<href>viewsink.png</href>
//</Icon><LatLonBox>
//<north>47.63389205932617</north>
//<south>46.678871154785156</south>
//<east>-120.82057189941406</east>
//<west>-122.0390396118164</west>
//</LatLonBox>
//</GroundOverlay>
//</kml>


public class KMLExporter  {
	
	private FileBasedDataset dset;

	public KMLExporter(FileBasedDataset dset) throws ThinklabException {
		this.dset = dset;
	}
		
	/**
	 * Export everything we have in the super-object
	 * 
	 * @param file
	 * @throws ThinklabException
	 */
	public void export(File file) throws ThinklabException {
		export(dset.getStatefulObservables(), file);
	}

	/**
	 * Export specified observables
	 * 
	 * @param observables
	 * @param file
	 */
	public File export(Collection<IConcept> observables, File file) 
		throws ThinklabException {

		File tempdir = MiscUtilities.createTempDir();
		
		String xmlFile = 
			tempdir + 
			File.separator + 
			MiscUtilities.getFileBaseName(file.toString()) + ".kml";

		GridExtent ext = (GridExtent) dset.getGrid().getExtent();
		ReferencedEnvelope env = null;
		try {
			env = ext.getNormalizedEnvelope().transform(Geospace.get().getStraightGeoCRS(), true);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		/* extract these from grid in WSG84 normalized */
		double south = env.getMinY();
		double north = env.getMaxY();
		double east  = env.getMaxX();
		double west  = env.getMinX();
		
		ArrayList<XML.XmlNode> nodes = new ArrayList<XML.XmlNode>();
		for (IConcept observable : observables) {
		
			String imagename = observable.toString().replace(":", "_") + ".png";
			String imagefile = tempdir + File.separator + imagename;
			
			/*
			 * make image in temp dir
			 * TODO at some point the res will become concept-dependent
			 */
			int x = dset.getGrid().getColumns();
			int y = dset.getGrid().getRows();
			dset.makeSurfacePlot(observable, imagefile, x, y, null);
			
			nodes.add(
				XML.node(
					"GroundOverlay", 
					XML.node("Name", observable.getLocalName()),
					XML.node("Icon",
						XML.node("href", imagename)),
					XML.node("LatLonBox",
						XML.node("north", north+""),
						XML.node("south", south+""),
						XML.node("east", east+""),
						XML.node("west", west+""))));
		}
		
		XML.document("http://earth.google.com/kml/2.0",
					XML.node("kml", nodes)).
				writeToFile(new File(xmlFile));
		
		// zip the whole thing into the passed file, check extension is .kmz
		file = new File(MiscUtilities.changeExtension(file.toString(), "kmz"));
		FolderZiper.zipFolder(tempdir.toString(), file.toString());
		MiscUtilities.deleteDirectory(tempdir);
		
		return file;
	}
}
