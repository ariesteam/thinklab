/**
 * ShapeValue.java
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
package org.integratedmodelling.geospace.values;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.corescience.interfaces.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.IDataSource;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IObservationContextState;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IUncertainty;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.value.ParsedLiteralValue;
import org.integratedmodelling.thinklab.value.TextValue;
import org.integratedmodelling.utils.Pair;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

/**
 * TODO make geometry model configurable, static pointer.
 * @author Ferdinando Villa
 *
 */
public class ShapeValue extends ParsedLiteralValue implements IDataSource {

	Geometry shape = null;
	PrecisionModel precisionModel = null;
	CoordinateReferenceSystem crs = null;
	
    public ShapeValue(String s) throws ThinklabValidationException {
    	parseLiteral(s);
    	setConceptWithoutValidation(null);
    }

	public ShapeValue(String s, IConcept c) throws ThinklabValidationException {
    	parseLiteral(s);
    	setConceptWithValidation(c);
    }

    protected ShapeValue(Geometry shape, IConcept c)  {
    	this.shape = shape;
    	setConceptWithoutValidation(c);
    }
    
    public ShapeValue(Geometry geometry) {
    	this.shape = geometry;
    	setConceptWithoutValidation(null);
	}

    /**
     * Construct a rectangular "cell" from two points.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public ShapeValue(double x1, double y1, double x2, double y2) {
    	this.shape = makeCell(x1,y1,x2,y2);
    	setConceptWithoutValidation(null);
    }
    
    public static Geometry makeCell(double x1, double y1, double x2, double y2) {

    	/**
    	 * FIXME
    	 * We should probably have a static one (checking thread safety) or store it somewhere; see how it works
    	 * for now. 
    	 */
    	GeometryFactory gFactory = new GeometryFactory();
		
		Coordinate[] pts = { 						
				new Coordinate(x1,y1), 
				new Coordinate(x2,y1),
				new Coordinate(x2,y2),
				new Coordinate(x1,y2),
				new Coordinate(x1,y1)
				};
		
		return gFactory.createPolygon(gFactory.createLinearRing(pts), null);
    }

    
	@Override
    public void parseLiteral(String s) throws ThinklabValidationException {
		
		/*
		 * first see if we start with a token that matches "EPSG:[0-9]*". If so,
		 * set the CRS from it; otherwise it is null (not the plugin default).
		 */
		if (s.startsWith("EPSG:")) {
			int n = s.indexOf(' ');
			String escode = s.substring(0, n);
			s = s.substring(n+1);
			try {
				crs = CRS.decode(escode);
			} catch (Exception e) {
				throw new ThinklabValidationException(e);
			}
		}
		
    	try {
    		if (s.contains("(")) {
    			shape = new WKTReader().read(s);
    		} else {
    			shape = new WKBReader().read(WKBReader.hexToBytes(s));
    		}
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
    }

	public TextValue asText() throws ThinklabValueConversionException {
		return new TextValue(new WKTWriter().write(shape));
	}

	public String toString() {
		return new WKTWriter().write(shape);
	}

	public boolean isPODType() {
		// definitely not
		return false;
	}

	public IValue op(String op, IValue other) throws ThinklabInappropriateOperationException, ThinklabValueConversionException {

		/**
		 * Implements:
		 * 
		 * + -> union
		 * - -> difference
		 * * -> intersection
		 * overlaps
		 * contains
		 * ...
		 */
		
	   	IValue ret = null;
    	if (op.equals("=")) {
    		try {
        		ret = clone();
				ret.setToCommonConcept(other.getConcept(), KnowledgeManager.get().getNumberType());
			} catch (ThinklabException e) {
			}
    	} else {
    		
    		if (other != null && !(other instanceof ShapeValue) )
    			throw new ThinklabValueConversionException("shape value operator applied to non-shape " + other.getConcept());
    		
    		ShapeValue onum = (ShapeValue)other;
    		
    		if (op.equals("+")) {
    			
    			// union
    			Geometry newgeo = this.shape.union(onum.shape);
    			/* 
    			 * TODO can be any shape, so we should use something that finds out the
    			 * proper "minimum" concept for the new shape
    			 */
    			ret = new ShapeValue(newgeo, this.getConcept());

    		} else if (op.equals("*")) {
    			
    			// intersection
    			Geometry newgeo = this.shape.intersection(onum.shape);
    			ret = new ShapeValue(newgeo, this.getConcept());

    		} else if (op.equals("-")) {

    			// difference
    			Geometry newgeo = this.shape.difference(onum.shape);
    			ret = new ShapeValue(newgeo, this.getConcept());

    			// TODO all other special ops
    		
    		} else {
    			throw new ThinklabInappropriateOperationException("number values do not support operator " + op);
    		}
    		
    		if (other != null)
    			ret.setToCommonConcept(other.getConcept(), KnowledgeManager.Number());
    	}
    	
    	return ret;
	}

	public void setConceptWithValidation(IConcept concept) throws ThinklabValidationException {
		
		boolean ok = false;
		
		/* if that's all we ask for, let it have it */
		if (concept.equals(KnowledgeManager.Thing()) ||
			concept.equals(Geospace.get().Shape())) {
			setConceptWithoutValidation(Geospace.get().Shape());
			return;
		}
		
		// check that passed shape is consistent with passed concept
		if (shape instanceof Point)
			ok = concept.is(Geospace.get().Point());
		else if (shape instanceof LineString)
			ok = concept.is(Geospace.get().LineString());
		else if (shape instanceof Polygon)
			ok = concept.is(Geospace.get().Polygon());
		else if (shape instanceof MultiPoint)
			ok = concept.is(Geospace.get().MultiPoint());
		else if (shape instanceof MultiLineString)
			ok = concept.is(Geospace.get().MultiLineString());
		else if (shape instanceof MultiPolygon)
			ok = concept.is(Geospace.get().MultiPolygon());
		
		if (!ok)
			throw new ThinklabValidationException(
					"shapevalue: shape is not consistent with concept: " +
					concept + 
					" != " + 
					shape);
		
		setConceptWithoutValidation(concept);
	}

	public void setConceptWithoutValidation(IConcept concept) {
		
		IConcept c = concept;
		
		if (c == null) {
			
			if (shape instanceof Point)
				c = Geospace.get().Point();
			else if (shape instanceof LineString)
				c = Geospace.get().LineString();
			else if (shape instanceof Polygon)
				c = Geospace.get().Polygon();
			else if (shape instanceof MultiPoint)
				c = Geospace.get().MultiPoint();
			else if (shape instanceof MultiLineString)
				c = Geospace.get().MultiLineString();
			else if (shape instanceof MultiPolygon)
				c = Geospace.get().MultiPolygon();
		}
		
		if (c == null)
			System.out.println("stocazzo, un " + shape + " non si sa come cazzo appioppar");
		super.setConceptWithoutValidation(c);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.value.Value#clone()
	 */
	@Override
	public IValue clone() {
		return new ShapeValue((Geometry)(shape.clone()), concept);
	}

	public Geometry getGeometry() {
		return shape;
	}
	
	public int getSRID(int def) {
		int ret = shape.getSRID();
		if (ret <= 0)
			ret = def;
		return ret;
	}

	public Pair<IValue, IUncertainty> getInitialValue() {
		return null;
	}

	public Pair<IValue, IUncertainty> getValue(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		return new Pair<IValue, IUncertainty>(this, null);
	}

	public Pair<String, IUncertainty> getValueLiteral(
			IObservationContextState context, IConcept concept,
			boolean useExtentIndex) throws ThinklabValidationException {
		return new Pair<String, IUncertainty>(this.toString(), null);
	}

	public ValueType getValueType() {
		return ValueType.IVALUE;
	}

	public boolean handshake(IConceptualModel cm,
			IObservationContext observationContext,
			IObservationContext overallContext)
			throws ThinklabValidationException {
		return true;
	}

	/**
	 * Get the referenced bounding box of the shape.
	 * @return
	 */
	public ReferencedEnvelope getEnvelope() {
		return new ReferencedEnvelope(shape.getEnvelopeInternal(), crs);
	}

	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	@Override
	public Object demote() {
		return shape;
	}

}
