package org.integratedmodelling.thinklab.geospace.literals;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

@Literal(
		concept="geospace:Polygon", 
		datatype="http://www.integratedmodelling.org/ks/geospace/geospace.owl#polygon",
		javaClass=Polygon.class)
public class PolygonValue extends ShapeValue {

	public PolygonValue(IConcept c, Polygon value) {
		super(c, value);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue() {
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(String s) throws ThinklabValidationException {
		super(s);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(String s, IConcept c)
			throws ThinklabValidationException {
		super(s, c);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(Polygon geometry) {
		super(geometry);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(Polygon geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(String s, CoordinateReferenceSystem crs)
			throws ThinklabValidationException {
		super(s, crs);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(ReferencedEnvelope e) {
		super(e);
		// TODO Auto-generated constructor stub
	}

}
