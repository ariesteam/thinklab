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
		optionalArgumentNames="arg1",
		optionalArgumentDefaultValues="__NONE__",
		optionalArgumentTypes="thinklab-core:Text",
		optionalArgumentDescriptions="argument",
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
				
			} else if (op.equals("extract")) {

				ShapeValue union = extract(features, command.getArgumentAsString("arg1"));
				if (command.hasOption("simplify") && union != null)
					union.simplify(0.1);
				
				session.print("Feature:\n" + (union != null ? union.toString() : "NOT FOUND"));
				
			} else if (op.equals("stats")) {
				
				doStats(features, session);
			}

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}	
		
		return null;
	}

	private void doStats(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features,
			ISession session) throws ThinklabException {

		FeatureIterator<SimpleFeature> it = 
				new DelegateFeatureIterator<SimpleFeature>(features, features.iterator());		
		
        while (it.hasNext()) {
        	
            SimpleFeature shape = it.next();            
            ShapeValue v = new ShapeValue((Geometry)(shape.getDefaultGeometry()), crs);
            session.print(shape.getID() + ": area= " + v.getArea() + " m2");
        
        }
	}

	private ShapeValue extract(
			FeatureCollection<SimpleFeatureType, SimpleFeature> features, String id) {

		FeatureIterator<SimpleFeature> it = 
				new DelegateFeatureIterator<SimpleFeature>(features, features.iterator());		
		
        while (it.hasNext()) {
        	
            SimpleFeature shape = it.next();
            
            if (shape.getID().equals(id)) {
            	return new ShapeValue((Geometry)(shape.getDefaultGeometry()), crs);
            }
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
