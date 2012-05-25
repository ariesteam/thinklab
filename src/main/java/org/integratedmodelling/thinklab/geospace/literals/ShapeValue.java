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
package org.integratedmodelling.thinklab.geospace.literals;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.list.ReferenceList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.annotation.SemanticLiteral;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IConceptualizable;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.AxisDirection;

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
import com.vividsolutions.jts.io.WKBWriter;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * TODO make geometry model configurable, static pointer.
 * @author Ferdinando Villa
 *
 */
@Literal(concept="geospace:SpatialRecord", datatype="http://www.integratedmodelling.org/ks/geospace/geospace.owl#geometry", javaClass=Geometry.class)
public class ShapeValue extends SemanticLiteral<Geometry> 
	implements IParseable, IConceptualizable, ITopologicallyComparable<ShapeValue> {

	PrecisionModel precisionModel = null;
	CoordinateReferenceSystem crs = null;
	
	public ShapeValue(IConcept c, Geometry value) {
		super(c, value);
	}
	
	public ShapeValue() {
    	setConceptWithoutValidation(Geospace.get().Shape());
	}
	
    public ShapeValue(String s) throws ThinklabValidationException {
    	parse(s);
    	setConceptWithoutValidation(Geospace.get().Shape());
    }

	public ShapeValue(String s, IConcept c) throws ThinklabValidationException {
    	parse(s);
    	setConceptWithValidation(c);
    }

    protected ShapeValue(Geometry shape, IConcept c)  {
    	this.value = shape;
    	setConceptWithoutValidation(c);
    }
    
    public ShapeValue(Geometry geometry) {
    	this.value = geometry;
    	setConceptWithoutValidation(null);
	}
    
    public ShapeValue(Geometry geometry, CoordinateReferenceSystem crs) {
    	this.value = geometry;
    	this.crs = crs;
    	setConceptWithoutValidation(null);
	}

	@Override
	public IList getSemantics() {
		return ReferenceList.list(concept, demote());
	}
    
    /**
     * Construct a rectangular "cell" from two points.
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public ShapeValue(double x1, double y1, double x2, double y2) {
    	this.value = makeCell(x1,y1,x2,y2);
    	setConceptWithoutValidation(null);
    }
    
    public ShapeValue(String s, CoordinateReferenceSystem crs) throws ThinklabValidationException {
    	parse(s);
    	this.crs = crs;
    	setConceptWithoutValidation(Geospace.get().Shape());
	}

    /* create a polygon from the passed envelope */ 
	public ShapeValue(ReferencedEnvelope e) {
		
    	GeometryFactory gFactory = new GeometryFactory();
		Coordinate[] pts = { 						
				new Coordinate(e.getMinX(),e.getMinY()),
				new Coordinate(e.getMaxX(),e.getMinY()),
				new Coordinate(e.getMaxX(),e.getMaxY()),
				new Coordinate(e.getMinX(),e.getMaxY()),
				new Coordinate(e.getMinX(),e.getMinY())
				};
		
		value = gFactory.createPolygon(gFactory.createLinearRing(pts), null);
		crs = e.getCoordinateReferenceSystem();
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
    public void parse(String s) throws ThinklabValidationException {
		
		/*
		 * first see if we start with a token that matches "EPSG:[0-9]*". If so,
		 * set the CRS from it; otherwise it is null (not the plugin default).
		 */
		if (s.startsWith("EPSG:")) {
			int n = s.indexOf(' ');
			String escode = s.substring(0, n);
			s = s.substring(n+1);
			crs = Geospace.getCRSFromID(escode);
		}
		
    	try {
    		if (s.contains("(")) {
    			value = new WKTReader().read(s);
    		} else {
    			value = new WKBReader().read(WKBReader.hexToBytes(s));
    		}
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
    }

	@Override
	public String asText() {
		return new WKTWriter().write(value);
	}

	@Override
	public String toString() {
		return new WKTWriter().write(value);
	}

//	public ISemanticLiteral op(String op, ISemanticLiteral other) throws ThinklabInappropriateOperationException, ThinklabValueConversionException {
//
//		/**
//		 * Implements:
//		 * 
//		 * + -> union
//		 * - -> difference
//		 * * -> intersection
//		 * overlaps
//		 * contains
//		 * ...
//		 */
//		
//	   	ISemanticLiteral ret = null;
//    	if (op.equals("=")) {
//    		try {
//        		ret = clone();
//				ret.setToCommonConcept(other.getConcept(), KnowledgeManager.get().getNumberType());
//			} catch (ThinklabException e) {
//			}
//    	} else {
//    		
//    		if (other != null && !(other instanceof ShapeValue) )
//    			throw new ThinklabValueConversionException("shape value operator applied to non-shape " + other.getConcept());
//    		
//    		ShapeValue onum = (ShapeValue)other;
//    		
//    		if (op.equals("+")) {
//    			
//    			// union
//    			Geometry newgeo = this.shape.union(onum.shape);
//    			/* 
//    			 * TODO can be any shape, so we should use something that finds out the
//    			 * proper "minimum" concept for the new shape
//    			 */
//    			ret = new ShapeValue(newgeo, this.getConcept());
//
//    		} else if (op.equals("*")) {
//    			
//    			// intersection
//    			Geometry newgeo = this.shape.intersection(onum.shape);
//    			ret = new ShapeValue(newgeo, this.getConcept());
//
//    		} else if (op.equals("-")) {
//
//    			// difference
//    			Geometry newgeo = this.shape.difference(onum.shape);
//    			ret = new ShapeValue(newgeo, this.getConcept());
//
//    			// TODO all other special ops
//    		
//    		} else {
//    			throw new ThinklabValidationException("number values do not support operator " + op);
//    		}
//    		
////    		if (other != null)
////    			((Value)ret).setToCommonConcept(other.getConcept(), KnowledgeManager.Number());
//    	}
//    	
//    	return ret;
//	}

//	@Override
	public void setConceptWithValidation(IConcept concept) throws ThinklabValidationException {
		
		boolean ok = false;
		
		/* if that's all we ask for, let it have it */
		if (concept.equals(Thinklab.THING) ||
			concept.equals(Geospace.get().Shape())) {
			setConceptWithoutValidation(Geospace.get().Shape());
			return;
		}
		
		// check that passed shape is consistent with passed concept
		if (value instanceof Point)
			ok = concept.is(Geospace.get().Point());
		else if (value instanceof LineString)
			ok = concept.is(Geospace.get().LineString());
		else if (value instanceof Polygon)
			ok = concept.is(Geospace.get().Polygon());
		else if (value instanceof MultiPoint)
			ok = concept.is(Geospace.get().MultiPoint());
		else if (value instanceof MultiLineString)
			ok = concept.is(Geospace.get().MultiLineString());
		else if (value instanceof MultiPolygon)
			ok = concept.is(Geospace.get().MultiPolygon());
		
		if (!ok)
			throw new ThinklabValidationException(
					"shapevalue: shape is not consistent with concept: " +
					concept + 
					" != " + 
					value);
		
		setConceptWithoutValidation(concept);
	}

//	@Override
	public void setConceptWithoutValidation(IConcept concept) {
		
		IConcept c = concept;
		
		if (c == null) {
			
			if (value instanceof Point)
				c = Geospace.get().Point();
			else if (value instanceof LineString)
				c = Geospace.get().LineString();
			else if (value instanceof Polygon)
				c = Geospace.get().Polygon();
			else if (value instanceof MultiPoint)
				c = Geospace.get().MultiPoint();
			else if (value instanceof MultiLineString)
				c = Geospace.get().MultiLineString();
			else if (value instanceof MultiPolygon)
				c = Geospace.get().MultiPolygon();
		}
		
		/*
		 * happens with empty collection shapes
		 */
		if (c == null)
			c = Geospace.get().Shape();
			
		super.setConcept(c);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.value.Value#clone()
	 */
	@Override
	public Object clone() {
		return new ShapeValue((Geometry)(value.clone()), concept);
	}
	
	public ShapeValue getBoundingBox() {
		return new ShapeValue(value.getEnvelope(), crs);
	}

	public ShapeValue getCentroid() {
		return new ShapeValue(value.getCentroid(), crs);
	}

	public Geometry getGeometry() {
		return value;
	}
	
	public void simplify(double tolerance) {
		value = TopologyPreservingSimplifier.simplify(value, tolerance);
	}
	
	public int getSRID(int def) {
		int ret = value.getSRID();
		if (ret <= 0)
			ret = def;
		return ret;
	}


	/**
	 * Get the referenced bounding box of the shape using the normalized axis order.
	 * @return
	 */
	public ReferencedEnvelope getEnvelope() {
		return new ReferencedEnvelope(value.getEnvelopeInternal(), crs);
	}

	
	/**
	 * Get the referenced bounding box of the shape using the axis order requested by the crs.
	 * @return
	 */
	public ReferencedEnvelope getDefaultEnvelope() {
		ReferencedEnvelope ret = new ReferencedEnvelope(value.getEnvelopeInternal(), crs);
		if (crs.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
			ret = new ReferencedEnvelope(ret.getMinY(),ret.getMaxY(), ret.getMinX(), ret.getMaxX(), getCRS());
		}
		return ret;
	}

	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	public ShapeValue union(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(value.union(region.value));
		ret.crs = crs;
		return ret;
	}
	
	public ShapeValue intersection(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(value.intersection(region.value));
		ret.crs = crs;
		return ret;
	}
	
	public ShapeValue difference(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(value.difference(region.value));
		ret.crs = crs;
		return ret;
	}

	public String getWKT() {
		return new WKTWriter().write(value);
	}

	public boolean isValid() {
		return value == null ? true : value.isValid();
	}

	/**
	 * Return the area of the shape in square meters. Transforms the shape if 
	 * necessary and computes the area, so it may be expensive. The shape must
	 * have a CRS.
	 * 
	 * @return the area in square meters, using projection EPSG:3005
	 * @throws ThinklabException if the shape has no CRS or a transformation cannot be found.
	 */
	public double getArea() throws ThinklabException {
		
		if (crs == null)
			throw new ThinklabValidationException("shape: cannot compute area of shape without CRS");
		
		double ret = 0.0;
		
		try {
			ret = 
				JTS.transform(
						value, 
						CRS.findMathTransform(crs, Geospace.get().getMetersCRS())).
						getArea();
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return ret;
	}
	
	/**
	 * Return a new ShapeValue transformed to the passed CRS. Must have a crs.
	 * 
	 * @param ocrs the CRS to transform to.
	 * @return a new shapevalue
	 * @throws ThinklabException if we have no CRS or a transformation cannot be found.
	 */
	public ShapeValue transform(CoordinateReferenceSystem ocrs) throws ThinklabException {
		
		if (crs == null)
			throw new ThinklabValidationException("shape: cannot compute area of shape without CRS");
		
		if (ocrs.equals(this.crs))
			return this;
		
		Geometry g = null;
		
		try {
			 g = JTS.transform(value, CRS.findMathTransform(crs, ocrs));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return new ShapeValue(g, ocrs);
	}

	public String getWKB() {
		return new String(WKBWriter.bytesToHex(new WKBWriter().write(value)));
	}

	public ShapeValue convertToMeters() throws ThinklabException {
		return transform(Geospace.get().getMetersCRS());
	}

	@Override
	public boolean contains(ShapeValue o) throws ThinklabException {
		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");
		
		return value.contains(((ShapeValue)o).transform(crs).value);
	}

	@Override
	public boolean overlaps(ShapeValue o) throws ThinklabException {
		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");

		return value.overlaps(((ShapeValue)o).transform(crs).value);
	}

	@Override
	public boolean intersects(ShapeValue o) throws ThinklabException {
		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");
		return value.intersects(((ShapeValue)o).transform(crs).value);
	}

	public void wrap(Object o) {
		value = (Geometry)o;
		setConceptWithoutValidation(null);
	}

	@Override
	public boolean is(Object object) {
		return 
			object instanceof ShapeValue && 
			((ShapeValue)object).value.equals(value);
	}

	@Override
	public IList conceptualize() throws ThinklabException {
		return PolyList.list(
				Thinklab.c("geospace:SpatialRecord"),
				PolyList.list(Thinklab.p("geospace:hasWKB"), getWKB()),
				PolyList.list(Thinklab.p("geospace:hasCRSCode"), Geospace.getCRSIdentifier(getCRS(), true)));
	}

	@Override
	public void define(IList l) throws ThinklabException {
		
		System.out.println(l.prettyPrint());
		
		/*
		 * create geometry and CRS, set into value
		 */
		try {
			String wkb = ((IList)(l.nth(1))).nth(1).toString();
			String crs = ((IList)(l.nth(2))).nth(1).toString();
			this.value = new WKBReader().read(WKBReader.hexToBytes(wkb));
			this.crs = Geospace.getCRSFromID(crs);
			
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
		
		/*
		 * establish proper concept
		 */
		setConceptWithoutValidation(null);
		
	}

}
