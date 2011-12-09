package org.integratedmodelling.geospace.commands;

import java.io.File;
import java.net.URL;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.DelegateFeatureIterator;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;

@ThinklabCommand(
		name="shapeops",
		argumentNames="source",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="source of shapes (file)",
		optionNames="op,s",
		optionLongNames="operation,simplify",
		optionTypes="thinklab-core:Text,owl:Nothing",
		optionArgumentLabels="operation,simplify flag",
		optionDescriptions="operation to perform,simplify result")
public class ShapeOps implements ICommandHandler {

	CoordinateReferenceSystem crs = null;
	
	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		
		ShapefileDataStore sds;
		try {
			URL sourceUrl  = new File(command.getArgumentAsString("source")).toURI().toURL();
			sds = new ShapefileDataStore(sourceUrl);
//			String layerName = MiscUtilities.getNameFromURL(sourceUrl.toString());
			FeatureCollection<SimpleFeatureType, SimpleFeature> features = sds.getFeatureSource(sds.getTypeNames()[0]).getFeatures();
			ReferencedEnvelope boundingBox = sds.getFeatureSource(sds.getTypeNames()[0]).getBounds();
			this.crs = features.getSchema().getCoordinateReferenceSystem();
			
			session.print("CRS is " + Geospace.getCRSIdentifier(crs, true));
			session.print("Bounding box: \n" + JTS.toGeometry(boundingBox.toBounds(boundingBox.getCoordinateReferenceSystem())));
			
			String op = command.getOptionAsString("operation");
			
			if (op == null) {
				throw new ThinklabRuntimeException("no operation given: use -op switch");
			} else if (op.equals("union")) {
				
				ShapeValue union = performUnion(features);
				if (command.hasOption("simplify"))
					union.simplify(0.1);
				
				session.print("Union:\n" + union);
				
			}

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}	
		
		return null;
	}

	private ShapeValue performUnion(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features) throws ThinklabException {
		
		FeatureIterator<SimpleFeature> it = 
				new DelegateFeatureIterator<SimpleFeature>(features, features.iterator());		
		
		ShapeValue v = null;
		
        while (it.hasNext()) {
        	
            SimpleFeature shape = it.next();
            ShapeValue sh = new ShapeValue((Geometry)(shape.getDefaultGeometry()), crs);
            if (v == null) 
            	v = sh;
            else
            	v = v.union(sh);
        }
        
		return v;
		
	}

}
