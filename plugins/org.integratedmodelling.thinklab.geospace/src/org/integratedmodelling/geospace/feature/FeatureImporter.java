/**
 * FeatureImporter.java
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
package org.integratedmodelling.geospace.feature;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureCollection;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabStorageException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.ISpatialDataImporter;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.list.Polylist;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.literals.NumberValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.thinklab.literals.TextValue;
import org.integratedmodelling.thinklab.literals.Value;
import org.integratedmodelling.utils.LookupTable;
import org.integratedmodelling.utils.MiscUtilities;
import org.mvel2.MVEL;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.filter.identity.Identifier;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Base class that reads a shapefile from a URL and calls a virtual function that processes each
 * shape and its attributes. Attributes are passed to the function as IValues, and are preprocessed to
 * implement transformations that can be passed in a set of Java properties. Transformations include:
 * 
 * 1. Specify the type that the IValue that translates the attribute should have (validation is 
 * 	  done by the KM);
 * 2. Specify a formula to translate the attribute before an IValue is created from it;
 * 3. Specify a lookup table to translate or discretize the attribute before an IValue is created from it.
 * 
 * If a value type is specified for an attribute and it cannot be validated as a literal in the knowledge manager 
 * (no ILiteralValidator is installed), it is assumed to be a valid concept, and a IValue containing the class
 * is generated, or an exception is raised if the value does not resolve to a known concept.
 * 
 * In the good tradition of shapefiles, if a filename.kbox file is present where the .shp etc are, it is read
 * to provide translation and typing information for the shapefile.
 * 
 * by 
 */
public abstract class FeatureImporter implements ISpatialDataImporter {

	public URL shapeURL = null;
	String layerName = null;
	Properties properties = null;
	
	/*
	 * these tables store any attribute transformations from the properties, in their "compiled" form.
	 */
	Hashtable<String, LookupTable> lookupTables = new Hashtable<String, LookupTable>();
	Hashtable<String, Serializable> expressions = new Hashtable<String, Serializable>();
	Hashtable<String, IConcept> concepts = new Hashtable<String, IConcept>();
	
	private FeatureSource<SimpleFeatureType, SimpleFeature> source;
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.feature.ISpatialDataImporter#initialize(java.net.URL, java.util.Properties)
	 */
	public void initialize(URL url, Properties properties) throws ThinklabException {
		
		shapeURL = url;
		Properties p = getDefaultProperties();
		
		if (properties == null)
			properties = p;
		else
			properties.putAll(p);
		
		this.properties = properties;

		readSource(url, properties);
	}
	
	protected abstract DataStore getDataStore();
	
	protected FeatureSource<SimpleFeatureType, SimpleFeature> getFeatureSource() {
		return source;
	}
	
	private void readSource(URL url, Properties properties) throws ThinklabException {
		
		try {
			
			DataStore dataStore = getDataStore();
			layerName = MiscUtilities.getURLBaseName(url.toString()).toLowerCase();
			// feature type name is defaulted to the datastore name 
			String name = dataStore.getTypeNames()[0];
			source = dataStore.getFeatureSource(name);
			
			/*
			 * read in transformations for all attributes
 			 */
			SimpleFeatureType schema = dataStore.getSchema(name);
			List<AttributeDescriptor> attrs = schema.getAttributeDescriptors();
			
			for (int i = 0; i < schema.getAttributeCount(); i++) {
				
				AttributeDescriptor atype = attrs.get(i);
				
				// lookup attribute transformations
				String lut = 
					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".lookup");
				
				if (lut != null) {
					lookupTables.put(atype.getLocalName(), new LookupTable(lut));
				}
				
				// lookup expression
				String exp = 
					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".value");
				
				if (exp != null) {
					expressions.put(atype.getLocalName(), MVEL.compileExpression(exp));
				}
				
				String con = 
					properties.getProperty("geospace." + layerName + "." + atype.getLocalName() + ".type");
				
				if (con != null) {
					concepts.put(atype.getLocalName(), KnowledgeManager.get().requireConcept(con));
				}
			}
			

		} catch (Exception e) {
			throw new ThinklabIOException(e);
		}
		
	}
	
	private Properties getDefaultProperties() throws ThinklabIOException {

		/* easy: lookup a kbox file in the same directory as the shapefile */
		String urlkb = MiscUtilities.changeExtension(shapeURL.toString(), "kbox");
		InputStream input = MiscUtilities.getInputStreamForResource(urlkb);
		
		Properties ret = new Properties();
		
		if (input != null)
			try {
				ret.load(input);
			} catch (IOException e) {
				throw new ThinklabIOException(e);
			}
		
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.feature.ISpatialDataImporter#getProperties()
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.feature.ISpatialDataImporter#getLayerName()
	 */
	public String getLayerName() {
		return layerName;
	}
	
	public FeatureImporter() {
		
		/* must call initialize explicitly if this is used */
	}
		
	public FeatureImporter(URL url, Properties properties) throws ThinklabException {
		initialize(url, properties);
	}
	
	/** 
	 * Return a list of lists containing the feature ID and the string value of any attribute requested
	 * for each existing feature.
	 * 
	 * @param schema
	 * @return
	 * @throws ThinklabIOException 
	 */
	public Polylist getFeatureIDs(Polylist schema) throws ThinklabException {
	
		ArrayList<Polylist> lists = new ArrayList<Polylist>();
		
		try {
			FeatureCollection<SimpleFeatureType, SimpleFeature> fcoll = source.getFeatures();
			
			for (Iterator<SimpleFeature> it = fcoll.iterator(); it.hasNext();) {

				SimpleFeature f = it.next();

				ArrayList<String> al = new ArrayList<String>();
				
				al.add(f.getID());
				
				/* add all attributes, complain if they're not there */
				Object[] c = schema.array();
				for (Object oo : c) {
					String aname = oo.toString();
					Object avalu = f.getAttribute(aname);
					if (avalu == null) {
						throw new ThinklabStorageException(
								"shapefile " + getLayerName() + " does not contain attribute " + aname);
					}
					al.add(avalu.toString());
				}
				
				lists.add(Polylist.PolylistFromArray(al.toArray()));	
			}
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}

		
		return Polylist.PolylistFromArray(lists.toArray());
	}

	
	/**
	 * Define this one to implement what you want to do with each feature in the shapefile. It will be called
	 * at each process(String) if the feature exists, or as many times as there are features when process() is
	 * called.
	 * 
	 * @param featureID Unique feature ID in the shapefile.
	 * @param shape A ShapeValue containing the shape.
	 * @param attributeNames
	 * @param attributeValues
	 * @throws ThinklabException 
	 */
	public abstract void notifyFeature(
			String featureID, ShapeValue shape, 
			String[] attributeNames, IValue[] attributeValues) throws ThinklabException;
	
	public IValue attributeToValue(AttributeDescriptor atype, Object avalue) throws ThinklabException {
		
		IValue ret = null;
		String name = atype.getLocalName();
		
		Serializable expression = expressions.get(name);
		LookupTable lTable= lookupTables.get(name);
		IConcept type = concepts.get(name);
		
		String tValue = null;
		
		/* apply expression first */
		if (expression != null) {
			HashMap<String, Object> vars = new HashMap<String, Object>();
			vars.put("value", avalue);
			tValue = MVEL.executeExpression(expression, vars).toString();
		}
		
		/* then lookup table */
		if (lTable != null) {
			tValue = lTable.lookup(tValue == null ? avalue.toString() : tValue);
		}
		
		/* then try to validate as concept if any is requested */
		if (type != null) {

			if (tValue == null)
				tValue = avalue.toString();
			
			/* if we have a validator, use it */
			ParsedLiteralValue validator = KnowledgeManager.get().getRawLiteral(type);
			
			if (validator != null) {
				validator.parseLiteral(tValue);
				ret = validator;
			} else {
			
				/* otherwise, must be a concept to classify with */
				IConcept c = 
					SemanticType.validate(tValue) ? 
							KnowledgeManager.get().retrieveConcept(tValue) :
							null;
				
				if (c != null) {
					ret = new Value(c);
				} else {
					/* if neither, complain */
					throw new ThinklabValidationException("cannot validate value " + tValue + " as a " + type);
				}
			}
		} else {
			
			/* no concept override; proceed according to geotools type */
			if (avalue instanceof Double) {
				
				ret = 
					tValue == null ?
						new NumberValue((Double)avalue) :
						KnowledgeManager.get().validateLiteral(KnowledgeManager.Double(), tValue);
						
			} else if (avalue instanceof Boolean) {
				
				ret = 
					tValue == null ?
						new BooleanValue((Boolean)avalue) :
						KnowledgeManager.get().validateLiteral(KnowledgeManager.Boolean(), tValue);
						
			} else if (avalue instanceof Integer) {

				ret = 
					tValue == null ?
						new NumberValue((Integer)avalue) :
						KnowledgeManager.get().validateLiteral(KnowledgeManager.Integer(), tValue);
	
			} else if (avalue instanceof Float) {
				
				ret = 
					tValue == null ?
						new NumberValue((Float)avalue) :
						KnowledgeManager.get().validateLiteral(KnowledgeManager.Float(), tValue);
					
			} else if (avalue instanceof String) {

				ret = 
					tValue == null ?
						new TextValue((String)avalue) :
						KnowledgeManager.get().validateLiteral(KnowledgeManager.Text(), tValue);
						
			} else if (avalue instanceof Geometry) {

				ret = 
					tValue == null ?
						new ShapeValue((Geometry)avalue):
						KnowledgeManager.get().validateLiteral(Geospace.get().Shape(), tValue);
			}
		}
		
		return ret;
		
	}


	protected void processFeature(SimpleFeature simpleFeature) throws ThinklabException {
		
		
		SimpleFeatureType ftype = simpleFeature.getFeatureType();
		int acount = ftype.getAttributeCount();
		
		String[] attNames  = new String[acount-1];
		IValue[] attValues = new IValue[acount-1];

		IValue shape = null;
		int n = 0;
		List<AttributeDescriptor> attrs = ftype.getAttributeDescriptors();
		
		for (int i = 0; i < acount; i++) {
			
			AttributeDescriptor atype = attrs.get(i);
			
			/* 
			 * apply transformations, translations, etc. and return the processed attribute as
			 * an IValue 
			 */
			IValue value = attributeToValue(atype, simpleFeature.getAttribute(i));
			
			/* put values away properly */
			if (atype.getLocalName().equals("the_geom")) {
				shape = value;
			} else {
				attNames[n] = atype.getLocalName();
				attValues[n] = value;
				n++;
			}
		}

		/* call processing function */
		notifyFeature(simpleFeature.getID(), (ShapeValue)shape, attNames, attValues);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.geospace.feature.ISpatialDataImporter#process()
	 */
	public int process() throws ThinklabException {

		int n = 0;
		
		try {
			FeatureCollection<SimpleFeatureType, SimpleFeature> fcoll = source.getFeatures();
			
			for (Iterator<SimpleFeature> it = fcoll.iterator(); it.hasNext(); ) {
				processFeature(it.next());
				n++;
			}
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		return n;
		
	}
	
	/**
	 * Process a specific feature by ID
	 * @param id
	 * @throws ThinklabException
	 */
	public void process(String id)	throws ThinklabException {

		/* get feature from shapefile */
		SimpleFeature f = null;
		FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
		FeatureId fid = ff.featureId(id);
		Set<Identifier> iids = new HashSet<Identifier>();
		iids.add(fid);
		
		try {
			FeatureCollection<SimpleFeatureType, SimpleFeature> fcoll = source.getFeatures(ff.id(iids));
			if (fcoll.size() != 1) {
				throw new ThinklabIOException("shapefile kbox: " +
						shapeURL + 
						": feature " + 
						id + 
						" absent or duplicated in shapefile");	
			}
			f = fcoll.iterator().next();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		if (f != null)
			processFeature(f);
	}


}
