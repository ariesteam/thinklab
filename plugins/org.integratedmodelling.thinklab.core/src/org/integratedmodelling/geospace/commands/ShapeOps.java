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
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.collection.DelegateFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
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
		optionNames="op,s,o,b",
		optionLongNames="operation,simplify,output,wkb-output",
		optionTypes="thinklab-core:Text,owl:Nothing,thinklab-core:Text,owl:Nothing",
		optionArgumentLabels="operation,simplify flag,output,wkb output",
		optionDescriptions="operation to perform,simplify result,save result to shapefile,outputs results in WKB instead of WKT")
public class ShapeOps implements ICommandHandler {

	CoordinateReferenceSystem crs = null;
	
	@Override
	public ISemanticLiteral execute(Command command, ISession session)
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
			ShapeValue ret = null;
			boolean wkb = command.hasOption("wkb-output");
			
			if (op == null) {
				throw new ThinklabRuntimeException("no operation given: use -op switch");
			} else if (op.equals("union")) {
				
				ret = performUnion(features);
				if (command.hasOption("simplify"))
					ret.simplify(0.1);
				
				session.print("Union:\n" + (wkb ? ret.getWKB() : ret.getWKT()));
				
			} else if (op.equals("extract")) {

				ret = extract(features, command.getArgumentAsString("arg1"));
				if (command.hasOption("simplify") && ret != null)
					ret.simplify(0.1);
				
				session.print("Feature:\n" + (ret != null ? (wkb ? ret.getWKB() : ret.getWKT()) : "NOT FOUND"));
				
			} else if (op.equals("stats")) {
				
				doStats(features, session);
			}
			
			if (command.hasOption("output")) {
				
		        if (ret == null) {
		        	session.write("nothing to output: -o option ignored");
		        	return null;
		        }
		        
				
				ret = ret.transform(Geospace.get().getDefaultCRS());
				
		        FeatureCollection<SimpleFeatureType, SimpleFeature> collection = FeatureCollections.newCollection();
		        final SimpleFeatureType TYPE = DataUtilities.createType(
		                "Location",                   // <- the name for our feature type
		                "location:MultiPolygon:srid=4326," + // <- the geometry attribute: Point type
		                "name:String"         // <- a String attribute
		        );

		        /*
		         * GeometryFactory will be used to create the geometry attribute of each feature (a Point
		         * object for the location)
		         */
		        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		        
		        /* Longitude (= x coord) first ! */
		        featureBuilder.add(ret.getGeometry());
		        featureBuilder.add("location");
		        SimpleFeature feature = featureBuilder.buildFeature(null);
		        collection.add(feature);
		        
		        File newFile = new File(command.getOptionAsString("output"));

		        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

		        Map<String, Serializable> params = new HashMap<String, Serializable>();
		        params.put("url", newFile.toURI().toURL());
		        ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		        newDataStore.createSchema(TYPE);
		        newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);
		        
		        Transaction transaction = new DefaultTransaction("create");

		        String typeName = newDataStore.getTypeNames()[0];
		        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = newDataStore.getFeatureSource(typeName);
		        ((FeatureStore<SimpleFeatureType, SimpleFeature>)featureSource).setTransaction(transaction);
		        boolean fuck = false;
		        try {
		        	((FeatureStore<SimpleFeatureType, SimpleFeature>)featureSource).addFeatures(collection);
		        	transaction.commit();
		        	
		            } catch (Exception problem) {
		                transaction.rollback();
		                fuck = true;
		            } finally {
		                transaction.close();
		            }
		        
		        if (fuck) {
		        	throw new ThinklabIOException("problem writing output shapefile");
		        }
		        
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
