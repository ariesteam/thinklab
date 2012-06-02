package org.integratedmodelling.thinklab.geospace.literals;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Literal;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.lang.IList;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

@Literal(
		concept="geospace:PolygonValue", 
		datatype="http://www.integratedmodelling.org/ks/geospace/geospace.owl#polygon",
		javaClass=PolygonValue.class)
public class PolygonValue extends ShapeValue {

	public PolygonValue(IConcept c, Polygon value) {
		super(c, value);
	}

	public PolygonValue(IConcept c, MultiPolygon value) {
		super(c, value);
	}

	public PolygonValue() {
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(String s) throws ThinklabValidationException {
		super(s);
	}

	public PolygonValue(String s, IConcept c)
			throws ThinklabValidationException {
		super(s, c);
		// TODO Auto-generated constructor stub
	}

	public PolygonValue(Polygon geometry) {
		super(geometry);
	}

	public PolygonValue(Polygon geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public PolygonValue(MultiPolygon geometry) {
		super(geometry);
	}

	public PolygonValue(Geometry geometry, CoordinateReferenceSystem crs) {
		
		/*
		 * TODO check that it's a polygon or multipolygon 
		 */
		super(geometry, crs);
	}

	public PolygonValue(MultiPolygon geometry, CoordinateReferenceSystem crs) {
		super(geometry, crs);
	}

	public PolygonValue(double x1, double y1, double x2, double y2) {
		super(x1, y1, x2, y2);
	}

	public PolygonValue(String s, CoordinateReferenceSystem crs)
			throws ThinklabValidationException {
		super(s, crs);
	}

	public PolygonValue(ReferencedEnvelope e) {
		super(e);
	}
	
	@Override
	public IList conceptualize() throws ThinklabException {
		return PolyList.list(
				Thinklab.c("geospace:PolygonValue"),
				PolyList.list(Thinklab.p("geospace:hasWKB"), getWKB()),
				PolyList.list(Thinklab.p("geospace:hasCRSCode"), Geospace.getCRSIdentifier(getCRS(), true)));
	}

}
