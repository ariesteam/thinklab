package org.integratedmodelling.thinklab.geospace.feature;
///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.geospace.feature;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.Serializable;
//import java.net.URL;
//import java.util.HashMap;
//import java.util.Hashtable;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Properties;
//
//import org.geotools.data.FeatureSource;
//import org.geotools.data.shapefile.ShapefileDataStore;
//import org.geotools.data.shapefile.indexed.IndexedShapefileDataStore;
//import org.geotools.feature.FeatureCollection;
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.exceptions.ThinklabIOException;
//import org.integratedmodelling.exceptions.ThinklabValidationException;
//import org.integratedmodelling.geospace.Geospace;
//import org.integratedmodelling.geospace.literals.ShapeValue;
//import org.integratedmodelling.thinklab.KnowledgeManager;
//import org.integratedmodelling.thinklab.api.knowledge.IConcept;
//import org.integratedmodelling.thinklab.literals.BooleanValue;
//import org.integratedmodelling.thinklab.literals.NumberValue;
//import org.integratedmodelling.thinklab.literals.TextValue;
//import org.integratedmodelling.thinklab.literals.Value;
//import org.integratedmodelling.utils.LookupTable;
//import org.integratedmodelling.utils.MiscUtilities;
//import org.mvel2.MVEL;
//import org.opengis.feature.simple.SimpleFeature;
//import org.opengis.feature.simple.SimpleFeatureType;
//import org.opengis.feature.type.AttributeDescriptor;
//
//import com.vividsolutions.jts.geom.Geometry;
//
///**
// * Base class that reads a shapefile from a URL and calls a virtual function that processes each
// * shape and its attributes. Attributes are passed to the function as IValues, and are preprocessed to
// * implement transformations that can be passed in a set of Java properties. Transformations include:
// * 
// * 1. Specify the type that the IValue that translates the attribute should have (validation is 
// * 	  done by the KM);
// * 2. Specify a formula to translate the attribute before an IValue is created from it;
// * 3. Specify a lookup table to translate or discretize the attribute before an IValue is created from it.
// * 
// * If a value type is specified for an attribute and it cannot be validated as a literal in the knowledge manager 
// * (no ILiteralValidator is installed), it is assumed to be a valid concept, and a IValue containing the class
// * is generated, or an exception is raised if the value does not resolve to a known concept.
// * 
// * In the good tradition of shapefiles, if a filename.kbox file is present where the .shp etc are, it is read
// * to provide translation and typing information for the shapefile.
// * 
// * by 
// */
//public class ShapefileReader {
//
//	public URL shapeURL = null;
//	ShapefileDataStore dataStore = null;
//	String layerName = null;
//	Properties properties = null;
//	
//	/*
//	 * these tables store any attribute transformations from the properties, in their "compiled" form.
//	 */
//	Hashtable<String, LookupTable> lookupTables = new Hashtable<String, LookupTable>();
//	Hashtable<String, Serializable> expressions = new Hashtable<String, Serializable>();
//	Hashtable<String, IConcept> concepts = new Hashtable<String, IConcept>();
//	
//	private FeatureSource<SimpleFeatureType, SimpleFeature> source;
//	
//	protected void initialize(URL url, Properties properties) {
//		
//	}
//	
//	protected void cleanup() {
//		
//	}
//	
//	protected FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource() {
//		return source;
//	}
//	
//	private void readShapeFile(URL url, Properties properties) throws ThinklabException {
//		
//		try {
//			
//			dataStore = new IndexedShapefileDataStore(url);
//			layerName = MiscUtilities.getURLBaseName(url.toString()).toLowerCase();
//			// feature type name is defaulted to the name of shapefile (without extension)
//			String name = dataStore.getTypeNames()[0];
//			source = dataStore.getFeatureSource(name);
//			
//			/*
//			 * read in transformations for all attributes
// 			 */
//			SimpleFeatureType schema = dataStore.getSchema();
//			List<AttributeDescriptor> attrs = schema.getAttributeDescriptors();
//			
//			for (int i = 0; i < schema.getAttributeCount(); i++) {
//				
//				AttributeDescriptor atype = attrs.get(i);
//				
//				// lookup attribute transformations
//				String lut = 
//					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".lookup");
//				
//				if (lut != null) {
//					lookupTables.put(atype.getLocalName(), new LookupTable(lut));
//				}
//				
//				// lookup expression
//				String exp = 
//					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".value");
//				
//				if (exp != null) {
//					expressions.put(atype.getLocalName(), MVEL.compileExpression(exp));
//				}
//				
//				String con = 
//					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".type");
//				
//				if (con != null) {
//					concepts.put(atype.getLocalName(), KnowledgeManager.get().requireConcept(con));
//				}
//			}
//			
//
//		} catch (Exception e) {
//			throw new ThinklabIOException(e);
//		}
//		
//	}
//	
//	private Properties getDefaultProperties() throws ThinklabIOException {
//
//		/* easy: lookup a kbox file in the same directory as the shapefile */
//		String urlkb = MiscUtilities.changeExtension(shapeURL.toString(), "kbox");
//		InputStream input = MiscUtilities.getInputStreamForResource(urlkb);
//		
//		Properties ret = new Properties();
//		
//		if (input != null)
//			try {
//				ret.load(input);
//			} catch (IOException e) {
//				throw new ThinklabIOException(e);
//			}
//		
//		return ret;
//	}
//	
//	public Properties getProperties() {
//		return properties;
//	}
//	
//	public String getLayerName() {
//		return layerName;
//	}
//	
//	public ShapefileReader(URL url, Properties properties) throws ThinklabException {
//		
//		shapeURL = url;
//		Properties p = getDefaultProperties();
//		
//		if (properties == null)
//			properties = p;
//		else
//			properties.putAll(p);
//		
//		this.properties = properties;
//		
//		readShapeFile(url, properties);
//
//	}
//	
//	/**
//	 * Define this one to implement what you want to do with each feature in the shapefile. It will be called
//	 * at each process(String) if the feature exists, or as many times as there are features when process() is
//	 * called.
//	 * 
//	 * @param featureID Unique feature ID in the shapefile.
//	 * @param shape A ShapeValue containing the shape.
//	 * @param attributeNames
//	 * @param attributeValues
//	 */
//	public void notifyFeature(
//			String featureID, ShapeValue shape, 
//			String[] attributeNames, IValue[] attributeValues) {
//		
//		System.out.println("*** " + featureID);
//		System.out.println("shape = " + shape);
//		System.out.println("attrs=" + attributeNames + "; values=" + attributeValues);
//		
//		
//	}
//	
//	public IValue attributeToValue(AttributeDescriptor atype, Object avalue) throws ThinklabException {
//		
//		IValue ret = null;
//		String name = atype.getLocalName();
//		
//		Serializable expression = expressions.get(name);
//		LookupTable lTable= lookupTables.get(name);
//		IConcept type = concepts.get(name);
//		
//		String tValue = null;
//		
//		/* apply expression first */
//		if (expression != null) {
//			HashMap<String, Object> vars = new HashMap<String, Object>();
//			vars.put("value", avalue);
//			tValue = MVEL.executeExpression(expression, vars).toString();
//		}
//		
//		/* then lookup table */
//		if (lTable != null) {
//			tValue = lTable.lookup(tValue == null ? avalue.toString() : tValue);
//		}
//		
//		/* then try to validate as concept if any is requested */
//		if (type != null) {
//
//			if (tValue == null)
//				tValue = avalue.toString();
//			
//			/* if we have a validator, use it */
//			ParsedLiteralValue validator = KnowledgeManager.get().getRawLiteral(type);
//			
//			if (validator != null) {
//				validator.parseLiteral(tValue);
//				ret = validator;
//			} else {
//			
//				/* otherwise, must be a concept to classify with */
//				IConcept c = 
//					SemanticType.validate(tValue) ? 
//							KnowledgeManager.get().retrieveConcept(tValue) :
//							null;
//				
//				if (c != null) {
//					ret = new Value(c);
//				} else {
//					/* if neither, complain */
//					throw new ThinklabValidationException("cannot validate value " + tValue + " as a " + type);
//				}
//			}
//		} else {
//			
//			/* no concept override; proceed according to geotools type */
//			if (avalue instanceof Double) {
//				
//				ret = 
//					tValue == null ?
//						new NumberValue((Double)avalue) :
//						KnowledgeManager.get().validateLiteral(KnowledgeManager.Double(), tValue);
//						
//			} else if (avalue instanceof Boolean) {
//				
//				ret = 
//					tValue == null ?
//						new BooleanValue((Boolean)avalue) :
//						KnowledgeManager.get().validateLiteral(KnowledgeManager.Boolean(), tValue);
//						
//			} else if (avalue instanceof Integer) {
//
//				ret = 
//					tValue == null ?
//						new NumberValue((Integer)avalue) :
//						KnowledgeManager.get().validateLiteral(KnowledgeManager.Integer(), tValue);
//	
//			} else if (avalue instanceof Float) {
//				
//				ret = 
//					tValue == null ?
//						new NumberValue((Float)avalue) :
//						KnowledgeManager.get().validateLiteral(KnowledgeManager.Float(), tValue);
//					
//			} else if (avalue instanceof String) {
//
//				ret = 
//					tValue == null ?
//						new TextValue((String)avalue) :
//						KnowledgeManager.get().validateLiteral(KnowledgeManager.Text(), tValue);
//						
//			} else if (avalue instanceof Geometry) {
//
//				ret = 
//					tValue == null ?
//						new ShapeValue((Geometry)avalue):
//						KnowledgeManager.get().validateLiteral(Geospace.get().Shape(), tValue);
//			}
//		}
//		
//		if (ret == null) {
//
//			throw new ThinklabValidationException(
//					"shapefile: attribute " + 
//					name + 
//					" has null value for object " + 
//					avalue);
//		}
//		
//		/* this usually causes trouble, so check */
//		String rval = ret.toString();
//		if (rval == null || rval.trim().equals("")) {
//			Geospace.get().logger().
//				warn("shapefile: attribute " + name + " has empty string value for " + avalue);
//		}
//		
//		return ret;
//		
//	}
//
//
//	protected void processFeature(SimpleFeature simpleFeature) throws ThinklabException {
//		
//		
//		SimpleFeatureType ftype = simpleFeature.getFeatureType();
//		
//		int acount = ftype.getAttributeCount();
//		List<AttributeDescriptor> attrs = ftype.getAttributeDescriptors();
//		
//		String[] attNames  = new String[acount-1];
//		IValue[] attValues = new IValue[acount-1];
//
//		IValue shape = null;
//		int n = 0;
//		
//		for (int i = 0; i < acount; i++) {
//			
//			AttributeDescriptor atype = attrs.get(i);
//			
//			/* 
//			 * apply transformations, translations, etc. and return the processed attribute as
//			 * an IValue 
//			 */
//			IValue value = attributeToValue(atype, simpleFeature.getAttribute(i));
//			
//			/* put values away properly */
//			if (atype.getLocalName().equals("the_geom")) {
//				shape = value;
//			} else {
//				attNames[n] = atype.getLocalName();
//				attValues[n] = value;
//				n++;
//			}
//		}
//
//		/* call processing function */
//		notifyFeature(simpleFeature.getID(), (ShapeValue)shape, attNames, attValues);
//	}
//	
//	/**
//	 * Process all features sequentially
//	 */
//	public void process() throws ThinklabException {
//
//		try {
//			FeatureCollection<SimpleFeatureType, SimpleFeature> fcoll = source.getFeatures();
//			
//			for (Iterator<SimpleFeature> it = fcoll.iterator(); it.hasNext(); ) {
//				processFeature(it.next());
//			}
//			
//		} catch (IOException e) {
//			throw new ThinklabIOException(e);
//		}
//		
//	}
//	
//	/**
//	 * Process a specific feature by ID
//	 * @param id
//	 * @throws ThinklabException
//	 */
//	public void process(String id)	throws ThinklabException {
//
//		/* get feature from shapefile */
////		Feature f = null;
////		FidFilter filter = filterFactory.createFidFilter(id);
////		try {
////			FeatureCollection fcoll = source.getFeatures(filter);
////			if (fcoll.size() != 1) {
////				throw new ThinklabIOException("shapefile kbox: " +
////						shapeURL + 
////						": feature " + 
////						id + 
////						" absent or duplicated in shapefile");	
////			}
////			f = (Feature)fcoll.iterator().next();
////		} catch (IOException e) {
////			throw new ThinklabIOException(e);
////		}
//		
//		// processFeature(f);
//	}
//
//	public static void main(String[] args) {
//		
//		try {
//			ShapefileReader s = new ShapefileReader(new URL("file:../ThinklabGeospacePlugin/examples/world_adm0.shp"), null);
//			s.process();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//}
