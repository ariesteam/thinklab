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
package org.integratedmodelling.geospace.literals;

import org.geotools.geometry.jts.JTS;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.integratedmodelling.corescience.interfaces.ITopologicallyComparable;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.annotations.LiteralImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;
import org.integratedmodelling.thinklab.literals.TextValue;
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
@LiteralImplementation(concept="geospace:SpatialRecord")
public class ShapeValue extends ParsedLiteralValue implements ITopologicallyComparable {

	Geometry shape = null;
	PrecisionModel precisionModel = null;
	CoordinateReferenceSystem crs = null;
	
	public ShapeValue() {
    	setConceptWithoutValidation(Geospace.get().Shape());
	}
	
    public ShapeValue(String s) throws ThinklabValidationException {
    	parseLiteral(s);
    	setConceptWithoutValidation(Geospace.get().Shape());
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
    
    public ShapeValue(Geometry geometry, CoordinateReferenceSystem crs) {
    	this.shape = geometry;
    	this.crs = crs;
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
    
    public ShapeValue(String s, CoordinateReferenceSystem crs) throws ThinklabValidationException {
    	parseLiteral(s);
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
		
		shape = gFactory.createPolygon(gFactory.createLinearRing(pts), null);
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
    public void parseLiteral(String s) throws ThinklabValidationException {
		
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
    			shape = new WKTReader().read(s);
    		} else {
    			shape = new WKBReader().read(WKBReader.hexToBytes(s));
    		}
		} catch (ParseException e) {
			throw new ThinklabValidationException(e);
		}
    }

	@Override
	public TextValue asText() throws ThinklabValueConversionException {
		return new TextValue(new WKTWriter().write(shape));
	}

	@Override
	public String toString() {
		return new WKTWriter().write(shape);
	}

	@Override
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

	@Override
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

	@Override
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
		
		/*
		 * happens with empty collection shapes
		 */
		if (c == null)
			c = Geospace.get().Shape();
			
		super.setConceptWithoutValidation(c);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.value.Value#clone()
	 */
	@Override
	public IValue clone() {
		return new ShapeValue((Geometry)(shape.clone()), concept);
	}
	
	public ShapeValue getBoundingBox() {
		return new ShapeValue(shape.getEnvelope(), crs);
	}

	public ShapeValue getCentroid() {
		return new ShapeValue(shape.getCentroid(), crs);
	}

	public Geometry getGeometry() {
		return shape;
	}
	
	public void simplify(double tolerance) {
		shape = TopologyPreservingSimplifier.simplify(shape, tolerance);
	}
	
	public int getSRID(int def) {
		int ret = shape.getSRID();
		if (ret <= 0)
			ret = def;
		return ret;
	}


	/**
	 * Get the referenced bounding box of the shape using the normalized axis order.
	 * @return
	 */
	public ReferencedEnvelope getEnvelope() {
		return new ReferencedEnvelope(shape.getEnvelopeInternal(), crs);
	}

	
	/**
	 * Get the referenced bounding box of the shape using the axis order requested by the crs.
	 * @return
	 */
	public ReferencedEnvelope getDefaultEnvelope() {
		ReferencedEnvelope ret = new ReferencedEnvelope(shape.getEnvelopeInternal(), crs);
		if (crs.getCoordinateSystem().getAxis(0).getDirection().equals(AxisDirection.NORTH)) {
			ret = new ReferencedEnvelope(ret.getMinY(),ret.getMaxY(), ret.getMinX(), ret.getMaxX(), getCRS());
		}
		return ret;
	}

	public CoordinateReferenceSystem getCRS() {
		return crs;
	}
	
	@Override
	public Object demote() {
		return shape;
	}

	public ShapeValue union(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(shape.union(region.shape));
		ret.crs = crs;
		return ret;
	}
	
	public ShapeValue intersection(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(shape.intersection(region.shape));
		ret.crs = crs;
		return ret;
	}
	
	public ShapeValue difference(ShapeValue region) throws ThinklabException {
		
		if ((crs != null || region.crs != null) && !crs.equals(region.crs))
			region = region.transform(crs);

		ShapeValue ret = new ShapeValue(shape.difference(region.shape));
		ret.crs = crs;
		return ret;
	}

	public String getWKT() {
		return new WKTWriter().write(shape);
	}

	public boolean isValid() {
		return shape == null ? true : shape.isValid();
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
						shape, 
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
			 g = JTS.transform(shape, CRS.findMathTransform(crs, ocrs));
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		return new ShapeValue(g, ocrs);
	}

	public String getWKB() {
		return new String(new WKBWriter().write(shape));
	}

	public ShapeValue convertToMeters() throws ThinklabException {
		return transform(Geospace.get().getMetersCRS());
	}

	@Override
	public boolean contains(ITopologicallyComparable o)
			throws ThinklabException {

		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");
		
		return shape.contains(((ShapeValue)o).transform(crs).shape);
	}

	@Override
	public boolean intersects(ITopologicallyComparable o)
			throws ThinklabException {

		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");
		return shape.intersects(((ShapeValue)o).transform(crs).shape);
	}

	@Override
	public boolean overlaps(ITopologicallyComparable o)
			throws ThinklabException {

		if (! (o instanceof ShapeValue))
			throw new ThinklabValidationException(
					"shapes can only be topologically compared with other shapes");

		return shape.overlaps(((ShapeValue)o).transform(crs).shape);
	}
}
